package com.loonggg.lib.alarmmanager.clock;

import android.app.Activity;
import android.app.Service;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;


public class ClockAlarmActivity extends Activity {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_alarm);
        String message = getResources().getString(R.string.alarm_dialog_close);
        int arg = this.getIntent().getIntExtra("arg0", 0);
        if (arg > 0) {
            message = getResources().getString(R.string.reminder);
        }
        int flag = this.getIntent().getIntExtra("flag", 0);
        int index = this.getIntent().getIntExtra("index", 0);
        showDialogInBroadcastReceiver(message, flag, index);
    }

    private void showDialogInBroadcastReceiver(String message, final int flag, int index) {
        if (flag == 1 || flag == 2) {
            mediaPlayer = MediaPlayer.create(this, getAlarmSound(index));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        //数组参数意义：第一个参数为等待指定时间后开始震动，震动时间为第二个参数。后边的参数依次为等待震动和震动的时间
        //第二个参数为重复次数，-1为不重复，0为一直震动
        if (flag == 0 || flag == 2) {
            vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{100, 10, 100, 600}, 0);
        }
        String title = getResources().getString(R.string.alarm_dialog_title);
        if (message.equals(getResources().getString(R.string.reminder))) {
            title = getResources().getString(R.string.warning);
        }
        final SimpleDialog dialog = new SimpleDialog(this, R.style.Theme_dialog);
        dialog.show();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.bt_confirm == v || dialog.bt_cancel == v) {
                    if (flag == 1 || flag == 2) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    if (flag == 0 || flag == 2) {
                        vibrator.cancel();
                    }
                    dialog.dismiss();
                    finish();
                }
            }
        });


    }

    private int getAlarmSound(int index) {
        if (index == 0) {
            return R.raw.sound1;
        } else if (index == 1) {
            return R.raw.sound2;
        } else if (index == 2) {
            return R.raw.sound3;
        } else {
            return R.raw.sound4;
        }
    }

}
