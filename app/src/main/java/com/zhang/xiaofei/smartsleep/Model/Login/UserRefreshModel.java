package com.zhang.xiaofei.smartsleep.Model.Login;

public class UserRefreshModel extends BaseProtocol {
    UserInfoModel data;

    public UserInfoModel getData() {
        return data;
    }

    public void setData(UserInfoModel data) {
        this.data = data;
    }
}
