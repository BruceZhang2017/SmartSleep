package com.clj.blesample.comm;

public interface BLEDataObserver {
    void handleBLEData(int battery, int flash, String mac, int version);
    void handleBLEData(String mac, float temperature, float humdity);
    void handleBLEData(int state, int heart, int breath);
    void handleBLEData(String mac, int time, int[] array);
    void handleBLEWrite(int flag);
    void calculateReport(int value);
}
