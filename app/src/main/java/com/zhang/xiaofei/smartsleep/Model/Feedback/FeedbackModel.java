package com.zhang.xiaofei.smartsleep.Model.Feedback;

import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;

public class FeedbackModel extends BaseProtocol {
    FeedbackItemModel[] data;

    public FeedbackItemModel[] getData() {
        return data;
    }

    public void setData(FeedbackItemModel[] data) {
        this.data = data;
    }
}
