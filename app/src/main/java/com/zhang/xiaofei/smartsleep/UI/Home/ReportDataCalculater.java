package com.zhang.xiaofei.smartsleep.UI.Home;

import com.github.mikephil.charting.data.Entry;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ReportDataCalculater {

    Realm mRealm;
    List<RecordModel> mlist = new  ArrayList<RecordModel>();

    public void readDataFromDB(String sleepTime, String getupTime) {
        mlist.clear();
        mRealm = Realm.getDefaultInstance();
        RealmResults<RecordModel> list = mRealm.where(RecordModel.class)
                .greaterThan("time", timeToLong(sleepTime))
                .lessThan("time", timeToLong(getupTime))
                .findAll().sort("time", Sort.ASCENDING);
        for (RecordModel model: list) {
            mlist.add(model);
        }
        System.out.println( " 开始时间：" + sleepTime + "结束时间：" + getupTime + "获取到的数据量：" + list.size());
    }

    // 将时间转换为秒制
    private long timeToLong(String value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = simpleDateFormat.parse(value);
            return date.getTime() / 1000;
        } catch (ParseException exception) {

        }
        return 0;
    }

    // 计算熟睡、中睡，浅睡、清醒
    public int[] calculateSleepValue(String sleepTime, String getupTime) {
        int heartTotal = 0; // 心跳总数
        int breathTotal = 0; // 呼吸总数
        if (mlist.size() > 0) {
            int deepSleep = 0;
            int middleSleep = 0;
            int cheapSleep = 0;
            int getup = 0;
            int getupCount = 0;
            int bodyMotionCount = 0;
            int heartAvarage = 0; // 心跳平均值
            int breathAvarage = 0; // 呼吸平均值
            int startTime = 0; // 开始计算的时间
            int i = 0;
            int apneaTotal = 0;
            int bodyMotionTotal = 0;
            int apneaCount = 0;
            for (RecordModel model : mlist) { // 计算清醒和体动
                i++;
                bodyMotionTotal += model.getBodyMotion();
                apneaTotal += model.getBreatheStop();
                heartTotal += model.getHeartRate(); // 统计心跳数据
                breathTotal += model.getBreathRate(); // 统计呼吸率数据
                if (model.getBreathRate() >  0) {
                    apneaCount += 1;
                }
                if (startTime == 0) {
                    startTime = model.getTime();
                }

                int a = model.getGetupFlag(); // 离床 在床
                int b = model.getBodyMotion(); // 体动
                if (a == 0) { // 离床，清醒
                    if (startTime > 0) {
                        getup += model.getTime() - startTime;
                        startTime = model.getTime();
                    }
                    getupCount += 1;
                    continue;
                }
                if (model.getTime() - startTime >= 60 && bodyMotionCount >= 5) {
                    cheapSleep += model.getTime() - startTime;
                    bodyMotionCount = 0;
                    startTime = model.getTime();
                    continue;
                }
                if (model.getTime() - startTime >= 3 * 60 && bodyMotionCount > 0) {
                    middleSleep += model.getTime() - startTime;
                    bodyMotionCount = 0;
                    startTime = model.getTime();
                    continue;
                }
                if (model.getTime() - startTime >= 5 * 60) {
                    if (bodyMotionCount > 2) {
                        middleSleep += model.getTime() - startTime;
                    } else {
                        deepSleep += model.getTime() - startTime;
                    }
                    bodyMotionCount = 0;
                    startTime = model.getTime();
                    continue;
                }
                if (i == mlist.size()) {
                    if (model.getTime() - startTime <= 3 * 60) {
                        if (bodyMotionCount > 5) {
                            cheapSleep += model.getTime() - startTime;
                        }else if (bodyMotionCount > 0) {
                            middleSleep += model.getTime() - startTime;
                        } else {
                            deepSleep += model.getTime() - startTime;
                        }
                    }
                }
                bodyMotionCount += b;
                continue;
            }
            heartAvarage = heartTotal / mlist.size();
            breathAvarage = breathTotal / mlist.size();
            return new int[]{deepSleep, middleSleep, cheapSleep, getup, 0, getupCount, heartAvarage, breathAvarage,bodyMotionTotal,apneaTotal,apneaCount};
        }
        return new int[]{0,0,0,0,0,0,0,0,0,0,0};
    }

    private String timeToHourMinute(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String str = simpleDateFormat.format(new Date(time + 0));
        return str;
    }

    // 将时间字符串转换为小时和分钟
    private int hourMinuteToInt(String value) {
        if (value.length() == 0) {
            return 0;
        }
        String hourMinute = value.substring(11);
        String[] array = hourMinute.split(":");
        return Integer.parseInt(array[0]) * 60 + Integer.parseInt(array[1]);
    }

    private String timeToDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String str = simpleDateFormat.format(new Date(time * 1000));
        return str;
    }
}
