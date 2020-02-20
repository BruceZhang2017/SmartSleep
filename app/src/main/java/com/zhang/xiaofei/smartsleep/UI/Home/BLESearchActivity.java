package com.zhang.xiaofei.smartsleep.UI.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.blesample.FastBLEManager;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.king.zxing.Intents;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;

import java.util.Date;
import java.util.List;
import java.util.UUID;


public class BLESearchActivity extends BaseAppActivity implements View.OnClickListener {

    private static final String TAG = BLESearchActivity.class.getSimpleName();
    private Button btn_scan;
    private DeviceAdapter mDeviceAdapter;
    private ImageButton ibLeft;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_search);
        initView();
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText(R.string.index_binding_device);
        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                if (btn_scan.getText().equals(getString(R.string.start_scan))) {
                    Intent intentBroadcast = new Intent();   //定义Intent
                    intentBroadcast.setAction("Filter");
                    intentBroadcast.putExtra("arg0", 13);
                    sendBroadcast(intentBroadcast);
                    setScanRule();
                    startScan();
                } else if (btn_scan.getText().equals(getString(R.string.stop_scan))) {
                    BleManager.getInstance().cancelScan();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction("Filter");
        intentBroadcast.putExtra("arg0", 14);
        sendBroadcast(intentBroadcast);
        super.onDestroy();
    }

    private void initView() {

        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setText(getString(R.string.start_scan));
        btn_scan.setOnClickListener(this);

        mDeviceAdapter = new DeviceAdapter(this);
        mDeviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice) {
                System.out.println("搜索到的设备名称：" + bleDevice.getName());
                String result = "";
                if (bleDevice.getName().toLowerCase().equals("sleep_baby")) {
                    result = "SLEEPBABY_" + System.currentTimeMillis() +","+bleDevice.getMac();
                } else if (bleDevice.getName().toLowerCase().equals("sleep_button")) {
                    result = "SLEEPBUTTON_" + System.currentTimeMillis() +","+bleDevice.getMac();
                } else {
                    return;
                }
                Intent intentBroadcast = new Intent();   //定义Intent
                intentBroadcast.setAction("Filter");
                intentBroadcast.putExtra("arg0", 0);
                intentBroadcast.putExtra("result", result);
                sendBroadcast(intentBroadcast);
                BLESearchActivity.this.finish();
            }
        });
        ListView listView_device = (ListView) findViewById(R.id.list_device);
        listView_device.setAdapter(mDeviceAdapter);
    }

    private void setScanRule() {
        String[] uuids = null;
        UUID[] serviceUuids = null;

        String[] names = null;

        boolean isAutoConnect = false;

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
                .setAutoConnect(isAutoConnect)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(3000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }


    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                mDeviceAdapter.clearScanDevice();
                mDeviceAdapter.notifyDataSetChanged();
                btn_scan.setText(getString(R.string.stop_scan));
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                btn_scan.setText(getString(R.string.start_scan));
            }
        });
    }
}
