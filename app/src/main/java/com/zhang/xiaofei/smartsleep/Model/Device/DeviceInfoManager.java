package com.zhang.xiaofei.smartsleep.Model.Device;

import java.util.HashMap;

public class DeviceInfoManager {
    public static DeviceInfoManager getInstance() {
        return ObserverManagerHolder.sObserverManager;
    }

    private static class ObserverManagerHolder {
        private static final DeviceInfoManager sObserverManager = new DeviceInfoManager();
    }

    public HashMap<String, String> hashMap = new HashMap<String, String>();
}
