package com.zhang.xiaofei.smartsleep.Model.Login;

import java.io.Serializable;

public class BaseProtocol implements Serializable {
    int code;
    String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
