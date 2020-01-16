package com.clj.blesample.operation;

import java.util.Calendar;

public class BLEOperation {

    public byte[] syncTime(int deviceId) {
        byte[] value = new byte[20];
        value[0] = (byte)0xeb;
        value[1] = 0x60;
        value[2] = (byte)deviceId;
        value[3] = 0x01;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = (cal.get(Calendar.MONTH))+1;
        int day_of_month = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        value[4] = (byte)(year - 2000);
        value[5] = (byte)month;
        value[6] = (byte)day_of_month;
        value[7] = (byte)hour;
        value[8] = (byte)minute;
        value[9] = (byte)second;
        for (int i = 10; i < value.length; i++) {
            value[i] = 0x00;
        }
        byte[] checkSum = calculateCheckSum(value);
        value[18] = checkSum[1];
        value[19] = 0x0a;
        return value;
    }

    public byte[] autoDetection(int deviceId, boolean open, String startTime, String endTime) {
        byte[] value = new byte[20];
        value[0] = (byte)0xeb;
        value[1] = 0x60;
        value[2] = (byte)deviceId;
        value[3] = 0x02;
        value[4] = (byte)(open == true ? 0x01 : 0x02);
        String[] start = startTime.split(":");
        value[5] = (byte)(Integer.parseInt(start[0]));
        value[6] = (byte)(Integer.parseInt(start[1]));
        value[7] = (byte)(Integer.parseInt(start[2]));
        value[8] = 0x00;
        String[] end = endTime.split(":");
        value[9] = (byte)(Integer.parseInt(end[0]));
        value[10] = (byte)(Integer.parseInt(end[1]));
        value[11] = (byte)(Integer.parseInt(end[2]));
        for (int i = 12; i < 20; i++) {
            value[i] = 0x00;
        }
        byte[] checkSum = calculateCheckSum(value);
        value[18] = checkSum[1];
        value[19] = 0x0a;
        return value;
    }

    public byte[] getTemplateAndHumidity(int deviceId) {
        byte[] value = new byte[20];
        value[0] = (byte)0xeb;
        value[1] = 0x60;
        value[2] = (byte)deviceId;
        value[3] = 0x03;
        for (int i = 4; i < 20; i++) {
            value[i] = 0x00;
        }
        byte[] checkSum = calculateCheckSum(value);
        value[18] = checkSum[1];
        value[19] = 0x0a;
        return value;
    }

    public byte[] getDeviceIDAndBatteryAndError(int deviceId, boolean bID, boolean bBattery, boolean bError) {
        byte[] value = new byte[20];
        value[0] = (byte)0xeb;
        value[1] = 0x60;
        value[2] = (byte)deviceId;
        value[3] = 0x04;
        value[4] = (byte)(bID == true ? 0x01 : 0x00);
        value[5] = (byte)(bBattery == true ? 0x01 : 0x00);
        value[6] = (byte)(bError == true ? 0x01 : 0x00);
        for (int i = 7; i < 20; i++) {
            value[i] = 0x00;
        }
        byte[] checkSum = calculateCheckSum(value);
        value[18] = checkSum[1];
        value[19] = 0x0a;
        return value;
    }

    public byte[] setSleepState(int deviceId, boolean bSleep) {
        byte[] value = new byte[20];
        value[0] = (byte)0xeb;
        value[1] = 0x60;
        value[2] = (byte)deviceId;
        value[3] = 0x05;
        value[4] = (byte)(bSleep == true ? 0x01 : 0x00);
        for (int i = 5; i < 20; i++) {
            value[i] = 0x00;
        }
        byte[] checkSum = calculateCheckSum(value);
        value[18] = checkSum[1];
        value[19] = 0x0a;
        return value;
    }

    public byte[] setGetUpState(int deviceId, boolean bGetUp) {
        byte[] value = new byte[20];
        value[0] = (byte)0xeb;
        value[1] = 0x60;
        value[2] = (byte)deviceId;
        value[3] = 0x06;
        value[4] = (byte)(bGetUp == true ? 0x01 : 0x00);
        for (int i = 5; i < 20; i++) {
            value[i] = 0x00;
        }
        byte[] checkSum = calculateCheckSum(value);
        value[18] = checkSum[1];
        value[19] = 0x0a;
        return value;
    }

    public byte[] setIntoDetectionState(int deviceId, boolean bDetection) {
        byte[] value = new byte[20];
        value[0] = (byte)0xeb;
        value[1] = 0x60;
        value[2] = (byte)deviceId;
        value[3] = 0x07;
        value[4] = (byte)(bDetection == true ? 0x01 : 0x00);
        for (int i = 5; i < 20; i++) {
            value[i] = 0x00;
        }
        byte[] checkSum = calculateCheckSum(value);
        value[18] = checkSum[1];
        value[19] = 0x0a;
        return value;
    }

    public byte[] setOutOfDetectionState(int deviceId, boolean bDetection) {
        byte[] value = new byte[20];
        value[0] = (byte)0xeb;
        value[1] = 0x60;
        value[2] = (byte)deviceId;
        value[3] = 0x08;
        value[4] = (byte)(bDetection == true ? 0x01 : 0x00);
        for (int i = 5; i < 20; i++) {
            value[i] = 0x00;
        }
        byte[] checkSum = calculateCheckSum(value);
        value[18] = checkSum[1];
        value[19] = 0x0a;
        return value;
    }

    public byte[] setIntoDynamicWave(int deviceId, boolean bDetection) {
        byte[] value = new byte[20];
        value[0] = (byte)0xeb;
        value[1] = 0x60;
        value[2] = (byte)deviceId;
        value[3] = 0x09;
        value[4] = (byte)(bDetection == true ? 0x01 : 0x00);
        for (int i = 5; i < 20; i++) {
            value[i] = 0x00;
        }
        byte[] checkSum = calculateCheckSum(value);
        value[18] = checkSum[1];
        value[19] = 0x0a;
        return value;
    }

    public byte[] setOutOfDynamicWave(int deviceId, boolean bDetection) {
        byte[] value = new byte[20];
        value[0] = (byte)0xeb;
        value[1] = 0x60;
        value[2] = (byte)deviceId;
        value[3] = 0x0a;
        value[4] = (byte)(bDetection == true ? 0x01 : 0x00);
        for (int i = 5; i < 20; i++) {
            value[i] = 0x00;
        }
        byte[] checkSum = calculateCheckSum(value);
        value[18] = checkSum[1];
        value[19] = 0x0a;
        return value;
    }

    public byte[] syncDataToFlash(int deviceId) {
        byte[] value = new byte[20];
        value[0] = (byte)0xeb;
        value[1] = 0x60;
        value[2] = (byte)deviceId;
        value[3] = 0x0b;
        for (int i = 4; i < 20; i++) {
            value[i] = 0x00;
        }
        byte[] checkSum = calculateCheckSum(value);
        value[18] = checkSum[1];
        value[19] = 0x0a;
        return value;
    }

    public byte[] heartAndBreath(int deviceId) {
        byte[] value = new byte[20];
        value[0] = (byte)0xeb;
        value[1] = 0x60;
        value[2] = (byte)deviceId;
        value[3] = 0x0c;
        for (int i = 4; i < 20; i++) {
            value[i] = 0x00;
        }
        byte[] checkSum = calculateCheckSum(value);
        value[18] = checkSum[1];
        value[19] = 0x0a;
        return value;
    }

    public byte[] calculateCheckSum(byte[] value) {
        if (value.length != 20 && value.length != 21) {
            return new byte[]{0x00, 0x00};
        }
        byte[] cs = new byte[2];
        short s = 0;
        for (int i = 2; i < 18; i++) {
            s += value[i] & 0xff;
        }
        cs[0] = (byte) ((s >> 8) & 0xff);
        cs[1] = (byte) (s & 0xff);
        return cs;
    }

    public Boolean invalidateCheckSum(byte[] value) {
        if (value.length != 20 && value.length != 21) {
            return false;
        }
        short s = 0;
        for (int i = 2; i < 18; i++) {
            s += value[i] & 0xff;
        }
        byte[] cs = new byte[2];
        cs[0] = (byte) ((s >> 8) & 0xff);
        cs[1] = (byte) (s & 0xff);
        return cs[0] == value[18] && cs[1] == value[19];
    }
}
