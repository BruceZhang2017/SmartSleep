package com.zhang.xiaofei.smartsleep.Tools;

import android.app.Activity;
import android.content.Intent;

public class SendCMDToHomeActivity {
    private static final String DYNAMICACTION = "Filter";

    public static void send(int arg0, int value, Activity activity) {
        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction(DYNAMICACTION);
        intentBroadcast.putExtra("arg0", arg0);
        intentBroadcast.putExtra("value", value);
        activity.sendBroadcast(intentBroadcast);
    }
}
