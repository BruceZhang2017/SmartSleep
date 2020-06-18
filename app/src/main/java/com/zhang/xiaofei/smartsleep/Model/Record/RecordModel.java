package com.zhang.xiaofei.smartsleep.Model.Record;

import io.realm.RealmObject;

public class RecordModel extends RealmObject {
    int userId;
    int deviceId;
    int time;
    int temperature;
    int humidity;
    int heartRate;
    int breathRate;
    int bodyMotion; // 体动
    int getupFlag; // 离床和在床
    int snore; // 打鼾
    int breatheStop; // 呼吸停止
    boolean isSyncCloud;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getBreathRate() {
        return breathRate;
    }

    public void setBreathRate(int breathRate) {
        this.breathRate = breathRate;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getBodyMotion() {
        return bodyMotion;
    }

    public void setBodyMotion(int bodyMotion) {
        this.bodyMotion = bodyMotion;
    }

    public int getGetupFlag() {
        return getupFlag;
    }

    public void setGetupFlag(int getupFlag) {
        this.getupFlag = getupFlag;
    }

    public int getSnore() {
        return snore;
    }

    public void setSnore(int snore) {
        this.snore = snore;
    }

    public int getBreatheStop() {
        return breatheStop;
    }

    public void setBreatheStop(int breatheStop) {
        this.breatheStop = breatheStop;
    }

    public boolean isSyncCloud() {
        return isSyncCloud;
    }

    public void setSyncCloud(boolean syncCloud) {
        isSyncCloud = syncCloud;
    }
}
