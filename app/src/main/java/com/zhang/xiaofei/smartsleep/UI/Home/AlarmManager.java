package com.zhang.xiaofei.smartsleep.UI.Home;

import android.content.Context;
import android.content.Intent;

import com.loonggg.lib.alarmmanager.clock.ClockAlarmActivity;

public class AlarmManager {
    public static void alarmGetup(Context context) {
        System.out.println("当前离床统计的数据为：触发通知");
        Intent clockIntent = new Intent(context, ClockAlarmActivity.class);
        clockIntent.putExtra("arg0",1);
        clockIntent.putExtra("flag", 2);
        clockIntent.putExtra("index", 0);
        clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(clockIntent);
    }
}
