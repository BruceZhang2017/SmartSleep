package com.zhang.xiaofei.smartsleep.Model;

import java.util.List;

public class SleepTimeResponse {
    private int code;
    private String msg;
    private List<SleepTimeBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<SleepTimeBean> getData() {
        return data;
    }

    public void setData(List<SleepTimeBean> data) {
        this.data = data;
    }
}
