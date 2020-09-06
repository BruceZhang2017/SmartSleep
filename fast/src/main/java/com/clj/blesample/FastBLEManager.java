package com.clj.blesample;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.clj.blesample.comm.BLEDataObserver;
import com.clj.blesample.comm.ObserverManager;
import com.clj.blesample.operation.OperationManager;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleMtuChangedCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.data.BleScanState;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.util.ArrayList;
import java.util.List;


public class FastBLEManager implements BLEDataObserver{

    public Activity context;
    private static final int REQUEST_CODE_OPEN_GPS = 3;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
    public String macAddress = "";
    private final static String TAG = "FastBLEManager";
    public OperationManager operationManager;
    public BLEDataObserver bleDataObserver;
    public BleDevice currentBleDevice;
    public boolean bSearching = false;

    public void onCreate() {
        BleManager.getInstance().init(context.getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }

    public void onDestroy() {
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
        if (operationManager != null) {
            operationManager.onDestroy();
        }
    }

    public void startBLEScan() {
        checkPermissions();
    }

    public void stopBLEScan() {
        if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
            BleManager.getInstance().cancelScan();
        }
    }

    public void onDisConnect() {
        if (currentBleDevice == null) {
            return;
        }
        if (BleManager.getInstance().isConnected(currentBleDevice)) {
            BleManager.getInstance().disconnect(currentBleDevice);
        }
    }

    public void setScanRule() {
        String[] names = new String[]{"Sleep_Baby", "Sleep_Button"};
        BleScanRuleConfig.Builder builder = new BleScanRuleConfig.Builder()
                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
                .setScanTimeOut(5000);             // 扫描超时时间，可选，默认5秒
        if (macAddress != null && macAddress.length() > 0) {
           builder.setDeviceMac(macAddress);
        }
        BleScanRuleConfig scanRuleConfig = builder.build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    public void startScanAndConnect() {

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                Log.i(TAG, "[BLE] onScanFinished");
                if (bSearching) {
                    return;
                }
                if (scanResultList != null && scanResultList.size() > 0) {
                    connect(scanResultList.get(0));
                } else {
                    //startScanAndConnect(); // 重新开始扫描
                    ObserverManager.getInstance().notifyObserver(4, macAddress);
                }
            }

            @Override
            public void onScanStarted(boolean success) {
                Log.i(TAG, "[BLE] onScanStarted");
                ObserverManager.getInstance().notifyObserver(3, macAddress);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Log.i(TAG, "[BLE] onScanning");
            }
        });
    }

    private void connect(BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Log.i(TAG, "[BLE] onStartConnect");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Log.i(TAG, "[BLE] onConnectFail");
                currentBleDevice = null;
                startScanAndConnect(); // 重新开始扫描
                ObserverManager.getInstance().notifyObserver(2, bleDevice.getMac());
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Log.i(TAG, "[BLE] onConnectSuccess");
                if (operationManager == null) {
                    operationManager = new OperationManager();
                    operationManager.bleDevice = bleDevice;
                    operationManager.onCreate();
                    operationManager.bleDataObserver = FastBLEManager.this;
                }
                currentBleDevice = bleDevice;
                ObserverManager.getInstance().notifyObserver(1, bleDevice.getMac());
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                operationManager = null;
                currentBleDevice = null;
                ObserverManager.getInstance().notifyObserver(0, device.getMac());
                if (device.getMac().equals(macAddress)) {
                    connect(device);
                }
            }
        });
    }

    private void readRssi(BleDevice bleDevice) {
        BleManager.getInstance().readRssi(bleDevice, new BleRssiCallback() {
            @Override
            public void onRssiFailure(BleException exception) {
                //Log.i(TAG, "onRssiFailure" + exception.toString());
            }

            @Override
            public void onRssiSuccess(int rssi) {
                //Log.i(TAG, "onRssiSuccess: " + rssi);
            }
        });
    }

    private void setMtu(BleDevice bleDevice, int mtu) {
        BleManager.getInstance().setMtu(bleDevice, mtu, new BleMtuChangedCallback() {
            @Override
            public void onSetMTUFailure(BleException exception) {
                //Log.i(TAG, "onsetMTUFailure" + exception.toString());
            }

            @Override
            public void onMtuChanged(int mtu) {
                //Log.i(TAG, "onMtuChanged: " + mtu);
            }
        });
    }

    void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(context, context.getResources().getString(R.string.please_open_blue), Toast.LENGTH_LONG).show();
            return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(context, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    public void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.notifyTitle)
                            .setMessage(R.string.gpsNotifyMsg)
                            .setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                            .setPositiveButton(R.string.setting,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            context.startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                        }
                                    })

                            .setCancelable(false)
                            .show();
                } else {
                    setScanRule();
                    startScanAndConnect();
                }
                break;
        }
    }

    public boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    // Obeserver 实现

    @Override
    public void handleBLEData(int battery, int flash, String mac, String version, String sn) {
        if (bleDataObserver != null) {
            bleDataObserver.handleBLEData(battery, flash, mac, version, sn);
        }
    }

    @Override
    public void handleBLEData(int state, int heart, int breath) {
        if (bleDataObserver != null) {
            bleDataObserver.handleBLEData(state, heart, breath);
        }
    }

    @Override
    public void handleBLEData(String mac, float temperature, float humdity) {
        if (bleDataObserver != null) {
            bleDataObserver.handleBLEData(macAddress, temperature, humdity);
        }
    }

    @Override
    public void handleBLEData(String mac, int time, int[] array) {
        if (bleDataObserver != null) {
            bleDataObserver.handleBLEData(mac, time, array);
        }
    }

    @Override
    public void handleBLEWrite(int flag) {
        if (bleDataObserver != null) {
            bleDataObserver.handleBLEWrite(flag);
        }
    }

    @Override
    public void calculateReport(int value) {
        if (bleDataObserver != null) {
            bleDataObserver.calculateReport(value);
        }
    }
}
