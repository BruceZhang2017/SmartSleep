package com.zhang.xiaofei.smartsleep.Kit.Application;

public interface SoundCoreObserver {


    int TYPE_BLUETOOTH_ON = 1;
    int TYPE_BLUETOOTH_OFF = 2;
    int TYPE_SPP_CONNECT_ERROR = 3;
    int TYPE_RENAME_SUCCESS = 4;
    int TYPE_OTA_SUCCESS = 5;
    int TYPE_SPP_CONNECTING = 6;
    int TYPE_BLUETOOTH_CONNECTED = 7;
    int TYPE_BLUETOOTH_DISCONNECTED = 8;
    int TYPE_TWS_SUCCESS = 9;
    int TYPE_CHANGE_LANGUAGE = 10;
    int TYPE_OTA_FAILED_FOR_SPP = 11;//use for ble device but ota spp
    int TYPE_OTA_DISCONNECT = 12;
    int TYPE_CURRENT_DB = 13;
    int TYPE_HEAR_ID_TEST_CANCEL = 14;
    int TYPE_HEAR_ID_TEST_FINISH = 15;
    int TYPE_SERIES_CONTENT_CLICK = 16;
    int TYPE_SERIES_FOOTER_CLICK = 17;
    int TYPE_DELETE_MY_DEVICE = 18;
    int TYPE_CONNECTED_DEVICE = 19;
    int TYPE_LEARN_MORE = 20;
    int TYPE_SERIES_TO_PRODUCT_VIEW = 21;
    int TYPE_HEAR_ID_SWITCH = 22;

    void notifyObserver(int type, Object data);
}