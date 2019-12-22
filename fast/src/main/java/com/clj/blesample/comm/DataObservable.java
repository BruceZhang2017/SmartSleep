package com.clj.blesample.comm;

import com.clj.fastble.data.BleDevice;

public interface DataObservable {
    void addObserver(DataObserver obj);

    void deleteObserver(DataObserver obj);

    void notifyObserver(int[] heart, int[] breath);
}
