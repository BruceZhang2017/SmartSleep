package com.clj.blesample.operation;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.widget.Toast;

import com.clj.blesample.comm.BLEDataObserver;
import com.clj.blesample.comm.DataOberverManager;
import com.clj.blesample.comm.Observer;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

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
                int flash = (data[6] << 24) + (data[7] << 16) + (data[8] << 8) + data[9];
                if (bleDataObserver != null) {
                    System.out.println("解析数据中:" + battery + " " + flash + " " + strMac + " " + version);
                    bleDataObserver.handleBLEData(battery, flash, strMac, version);
                }
                bleDataObserver.handleBLEWrite(2); // 读取温度和湿度
                if (flash >= 0) { // 读取设备里数据
                    bleDataObserver.handleBLEWrite(3); // 读取flash内数据
                }

            } else if (data[3] == 0x03) {
                float temprature = data[4] + data[5] / 100;
                float humdity = data[6] + data[7] / 100;
                if (bleDataObserver != null) {
                    bleDataObserver.handleBLEData("",temprature, humdity);
                }
            } else if (data[3] == 0x0b) { // Flash数据解析
                if ((data[4] & 0xff) == 0) {
                    return;
                }
                if (data.length < 20) {
                    return;
                }
                int count = (data.length - 5) / 14;
                for (int i = 0; i < count; i++) {
                    int index = 4 + i * 14; // 游标位置
                    byte[] subData = new byte[14];
                    for (int j = 0; j < 14; j++) {
                        subData[j] = data[index + j];
                    }
                    parseFlashData(subData);
                }
            } else if (data[3] == 0x09) { // 时时数据解析
                System.out.println("数据长度：" + data.length);
                if (data.length != 65) {
                    //timer.schedule(task, 0, 1000);
                    return;
                }
                int[] heartRates = new int[25];
                int[] breathRates = new int[5];
                for (int i = 0; i < 5; i++) {
                    breathRates[i] = ((data[4 + i * 2] & 0xff) << 8) + (data[4 + i * 2 + 1] & 0xff);
                }
                for (int i = 0; i < 25; i++) {
                    heartRates[i] = ((data[4 + 10 + i * 2] & 0xff) << 8) + (data[4 + 10 + i * 2 + 1] & 0xff);
                }
                DataOberverManager.getInstance().notifyObserver(heartRates, breathRates);
            } else if (data[3] == 0x0c) {
                int state = data[4];
                int heart = data[5];
                int breath = data[6];
                if (bleDataObserver != null) {
                    bleDataObserver.handleBLEData(state, heart, breath);
                }
            }
        }
    }

    private void parseFlashData(byte[] data) {
        if (data.length != 14) {
            return;
        }
        System.out.println("解析的值: " + HexUtil.formatHexString(data, true));
        int year = 2000 + (data[0] & 0xff);
        int month = data[1];
        int day = data[2];
        int hour = data[3];
        int minute = data[4];
        int second = data[5];
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        int temTime = (int)(calendar.getTimeInMillis() / 1000);

        int temperature = data[6] & 0xff;
        int humdity = data[7] & 0xff; // 湿度
        int heartRate = data[8] & 0xff;
        int breathRate = data[9] & 0xff;
        int bodyMotion = data[10] & 0xff;
        int getupFlag = data[11] & 0xff;
        int snore = data[12] & 0xff;
        int breathStop = data[13] & 0xff;
        int[] array = new int[8];
        array[0] = temperature;
        array[1] = humdity;
        array[2] = heartRate;
        array[3] = breathRate;
        array[4] = bodyMotion;
        array[5] = getupFlag;
        array[6] = snore;
        array[7] = breathStop;
        if (bleDataObserver != null) {
            bleDataObserver.handleBLEData("", temTime, array);
        }
    }
}
