package com.zhang.xiaofei.smartsleep.Model.Packages;

import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;

public class Goods extends BaseProtocol {
    GoodsItem[] data;

    public GoodsItem[] getData() {
        return data;
    }

    public void setData(GoodsItem[] data) {
        this.data = data;
    }
}
