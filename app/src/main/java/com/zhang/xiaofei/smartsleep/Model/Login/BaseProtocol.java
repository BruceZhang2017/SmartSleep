package com.zhang.xiaofei.smartsleep.Model.Login;

import java.io.Serializable;

public class BaseProtocol implements Serializable {
    int code;
    String msg;
    String enmsg;

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

    public String getEnmsg() {
        return enmsg;
    }

    public void setEnmsg(String enmsg) {
        this.enmsg = enmsg;
    }
}
