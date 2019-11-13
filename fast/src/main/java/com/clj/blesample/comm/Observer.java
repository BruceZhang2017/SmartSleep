package com.clj.blesample.comm;


import com.clj.fastble.data.BleDevice;

public interface Observer {

    void connectedState(boolean connected, String mac);
}
