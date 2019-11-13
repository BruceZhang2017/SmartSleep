package com.clj.blesample.operation;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.clj.blesample.comm.BLEDataObserver;
import com.clj.blesample.comm.DataOberverManager;
import com.clj.blesample.comm.Observer;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import java.util.Calendar;

public class OperationManager {

    public BleDevice bleDevice;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic notifiyCharacteristic;
    public BLEOperation bleOperation;
    public BLEDataObserver bleDataObserver;
    private int currentTime;

    public void onCreate() {
        bleOperation = new BLEOperation();
        readService();
    }

    public void onDestroy() {
        BleManager.getInstance().clearCharacterCallback(bleDevice);
    }


    private void readService() {
        String name = bleDevice.getName();
        String mac = bleDevice.getMac();
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        for (BluetoothGattService service : gatt.getServices()) {
            if (service.getUuid().toString().toLowerCase().equals("6e400001-b5a3-f393-e0a9-e50e24dcca9e")) {
                bluetoothGattService = service;
                readCharacteristic();
                break;
            }
        }
    }

    private void readCharacteristic() {
        for (BluetoothGattCharacteristic character : bluetoothGattService.getCharacteristics()) {
            if (character.getUuid().toString().toLowerCase().equals("6e400002-b5a3-f393-e0a9-e50e24dcca9e")) {
                writeCharacteristic = character;
                continue;
            }
            if (character.getUuid().toString().toLowerCase().equals("6e400003-b5a3-f393-e0a9-e50e24dcca9e")) {
                notifiyCharacteristic = character;
                openNotify();
                continue;
            }
        }
    }

    private void openNotify() {
        BleManager.getInstance().notify(
                bleDevice,
                bluetoothGattService.getUuid().toString(),
                notifiyCharacteristic.getUuid().toString(),
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        System.out.println("[OperationManager] onNotifySuccess");
                        bleDataObserver.handleBLEWrite(1); // 同步时间
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {
                        System.out.println("[OperationManager] onNotifyFailure");
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        System.out.println("[OperationManager] notify:" + HexUtil.formatHexString(notifiyCharacteristic.getValue(), true));
                        parseBLEData(notifiyCharacteristic.getValue()); // 解析数据
                    }
                });
    }

    private void closeNotify() {
        BleManager.getInstance().stopNotify(
                bleDevice,
                bluetoothGattService.getUuid().toString(),
                notifiyCharacteristic.getUuid().toString());
    }

    public void write(byte[] hex) {
        System.out.println("发送的数据" + HexUtil.formatHexString(hex, true));
        BleManager.getInstance().write(
                bleDevice,
                bluetoothGattService.getUuid().toString(),
                writeCharacteristic.getUuid().toString(),
                hex,
                new BleWriteCallback() {

                    @Override
                    public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                        System.out.println("[OperationManager] write: " + HexUtil.formatHexString(justWrite, true));
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                        System.out.println("[OperationManager] onWriteFailure " + exception.getDescription());
                    }
                });
    }

    // byte[] hex = operation.syncTime(0x01);
    // byte[] hex = operation.autoDetection(0x01, true, "08:00:00", "16:00:00");
    // byte[] hex = operation.getTemplateAndHumidity(0x01);
    // byte[] hex = operation.getDeviceIDAndBatteryAndError(0x01, true, true, true);
    // byte[] hex = operation.setSleepState(0x01, true);
    // byte[] hex = operation.setGetUpState(0x01, true);
    // byte[] hex = operation.setIntoDetectionState(0x01, true);
    // byte[] hex = operation.setOutOfDetectionState(0x01, true);
    // byte[] hex = operation.setIntoDynamicWave(0x01, true);
    // byte[] hex = operation.setOutOfDynamicWave(0x01, true);
    // byte[] hex = operation.syncDataToFlash(0x01);

    private void parseBLEData(byte[] data) {
        if (data == null && data.length < 20) {
            return;
        }
        if (data[0] == (byte)0xeb && data[1] == (byte)0x60) {
            if (data[3] == 0x01) {
                int battery = (int)(data[4] & 0xff);
                int version = (int)(data[17] & 0xff);
                byte[] mac = new byte[6];
                for (int i = 11; i < 17; i++) {
                    mac[i - 11] = data[i];
                }
                String strMac = HexUtil.formatHexString(mac, true);
                strMac = strMac.replaceAll(" ", ":").toUpperCase();
                int flash = (data[6] << 24) + (data[5] << 16) + (data[6] << 8) + data[7];
                if (bleDataObserver != null) {
                    System.out.println("解析数据中:" + battery + " " + flash + " " + strMac + " " + version);
                    bleDataObserver.handleBLEData(battery, flash, strMac, version);
                }
                bleDataObserver.handleBLEWrite(2); // 读取温度和湿度
                if (flash > 0) { // 读取设备里数据
                    bleDataObserver.handleBLEWrite(3); // 读取flash内数据
                }

            } else if (data[3] == 0x03) {
                float temprature = data[4] + data[5] / 100;
                float humdity = data[6] + data[7] / 100;
                if (bleDataObserver != null) {
                    bleDataObserver.handleBLEData("",temprature, humdity);
                }
            } else if (data[3] == 0x0b) {
                if ((data[4] & 0xff) == 0) {
                    return;
                }
                int year = 2000 + (data[4] & 0xff);
                int month = data[5];
                int day = data[6];
                int hour = data[7];
                int minute = data[8];
                int second = data[9];
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, day, hour, minute, second);
                int temTime = (int)(calendar.getTimeInMillis() / 1000);

                int temperature = data[10] & 0xff;
                int humdity = data[11] & 0xff;
                int heartRate = data[12] & 0xff;
                int breathRate = data[13] & 0xff;
                boolean breatheStop = (data[14] & 0x01) > 0;
                boolean outBedAlarm = (data[15] & 0x01) > 0;
                if (bleDataObserver != null) {
                    bleDataObserver.handleBLEData("", temTime, temperature, humdity, heartRate, breathRate, breatheStop, outBedAlarm);
                }
                if (currentTime != 0 && temTime == currentTime) {
                    return;
                }
                currentTime = temTime;
                DataOberverManager.getInstance().notifyObserver(heartRate, breathRate);
            }
        }
    }

//    private String intToTime(int value) {
//        return value > 9 ? ("" + value) : ("0" + value);
//    }
//
//    public int parseServerTime(String serverTime) {
//        String format = "yyyy-MM-dd HH:mm:ss";
//        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINESE);
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
//        Date date = new Date();
//        int value = 0;
//        try {
//            date = sdf.parse(serverTime);
//            value = date.
//        } catch (Exception e) {
//
//        }
//        return value;
//    }

}
