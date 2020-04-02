package com.zhang.xiaofei.smartsleep.Model.Record;

import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;

import java.io.Serializable;

public class HistoryBreathList extends BaseProtocol implements Serializable {
    HistoryBreathData data;

    public HistoryBreathData getData() {
        return data;
    }

    public void setData(HistoryBreathData data) {
        this.data = data;
    }
}


