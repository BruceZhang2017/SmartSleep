package com.loonggg.lib.alarmmanager.clock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.WindowManager;

/**
 * Created by loongggdroid on 2016/3/21.
 */
public class LoongggAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        int index = intent.getIntExtra("sound", 0);
        Intent clockIntent = new Intent(context, ClockAlarmActivity.class);
        clockIntent.putExtra("flag", 2);
        clockIntent.putExtra("index", index);
        clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(clockIntent);
    }


}
