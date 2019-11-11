package com.clj.blesample.comm;

public interface BLEDataObserver {
    void handleBLEData(int battery, int flash, String mac, int version);
    void handleBLEData(String mac, float temperature, float humdity);
    void handleBLEData(String mac, int time, int temperature, int humdity, int heartRate, Boolean heartStop, Boolean breatheStop, Boolean outBedAlarm);
}
