package com.zhang.xiaofei.smartsleep.Model.Record;

import io.realm.RealmObject;

public class RecordModel extends RealmObject {
    int userId;
    int deviceId;
    int time;
    int temperature;
    int humidity;
    int heartRate;
    Boolean heartStop;
    Boolean breatheStop;
    Boolean outBedAlarm;

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

    public Boolean getHeartStop() {
        return heartStop;
    }

    public void setHeartStop(Boolean heartStop) {
        this.heartStop = heartStop;
    }

    public Boolean getBreatheStop() {
        return breatheStop;
    }

    public void setBreatheStop(Boolean breatheStop) {
        this.breatheStop = breatheStop;
    }

    public Boolean getOutBedAlarm() {
        return outBedAlarm;
    }

    public void setOutBedAlarm(Boolean outBedAlarm) {
        this.outBedAlarm = outBedAlarm;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }
}
