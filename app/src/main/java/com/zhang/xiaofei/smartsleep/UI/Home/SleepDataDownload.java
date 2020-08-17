package com.zhang.xiaofei.smartsleep.UI.Home;

import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;

import java.util.List;

public class SleepDataDownload {
    private int code;
    private List<SleepData> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<SleepData> getData() {
        return data;
    }

    public void setData(List<SleepData> data) {
        this.data = data;
    }

    public static class SleepData {
        private String date;
        private String sleepData;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getSleepData() {
            return sleepData;
        }

        public void setSleepData(String sleepData) {
            this.sleepData = sleepData;
        }
    }
}


