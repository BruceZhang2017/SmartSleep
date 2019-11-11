package com.zhang.xiaofei.smartsleep.Model.Device;

import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;

public class DeviceListModel extends BaseProtocol {
    DeviceModel[] data;

    public DeviceModel[] getData() {
        return data;
    }

    public void setData(DeviceModel[] data) {
        this.data = data;
    }
}
