package com.zhang.xiaofei.smartsleep.Kit;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.sunofbeaches.himalaya.PlayHelper;

import java.util.ArrayList;
import java.util.List;

public class AlarmTimer {

    public boolean bStart = false;
    private static AlarmTimer singleton;
    public List<AlarmTimerInterface> list = new ArrayList<AlarmTimerInterface>();

    // 定义一个Handler类
    private Handler mHandler = new Handler();
    //
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // 要做的事情
            PlayHelper.stop();
            System.out.println("关闭音乐");
            bStart = false;
            if (list.size() > 0) {
                for (AlarmTimerInterface item : list){
                    item.stopAlarm();
                }
            }
        }
    };

    public static synchronized AlarmTimer getInstance() {//同步控制,避免多线程的状况多创建了实例对象
        if (singleton == null) {
            singleton = new AlarmTimer();//在需要的时候在创建
        }
        return singleton;
    }


    public void startTimer(int time) {
        // 启动计时器
        stopTimer();
        mHandler.postDelayed(mRunnable, 1000 * time);
        bStart = true;
    }

    public void stopTimer() {
        // 停止计时器
        mHandler.removeCallbacks(mRunnable);
        bStart = false;
    }

    public interface AlarmTimerInterface {
        public void stopAlarm();
    }
}
