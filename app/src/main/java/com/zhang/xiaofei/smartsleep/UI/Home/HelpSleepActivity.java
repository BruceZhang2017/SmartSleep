package com.zhang.xiaofei.smartsleep.UI.Home;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.UiThread;

import com.deadline.statebutton.StateButton;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.ximalaya.ting.android.opensdk.test.MainFragmentActivity;
import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;

public class HelpSleepActivity extends BaseAppActivity implements View.OnClickListener {

    ImageButton ibBack;
    TextView tvTime;
    TextView tvTimeRange;
    TextView tvTip;
    TextView tvRealTime;
    Handler handler;
    MainFragmentActivity xmPlayer;
    ImageButton ibPalyPause;
    ImageButton ibPlayPre;
    ImageButton ibPlayNext;
    TextView tvSoundTitle;
    Switch switch1;
    StateButton btnSleep;
    private static final String DYNAMICACTION = "com.example.petter.broadcast.MyDynamicFilter";
    Realm mRealm;
    int currentTime;
    int getupH = 0;
    int getupM = 0;
    int sleepH = 0;
    int sleepM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_sleep);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        ibBack.setOnClickListener(this);
        tvTime = (TextView)findViewById(R.id.tv_time);
        tvTime.setText(createTimeValue("00时00分"));
        tvTimeRange = (TextView)findViewById(R.id.tv_time_range);
        tvTip = (TextView)findViewById(R.id.tv_tip);
        tvRealTime = (TextView)findViewById(R.id.tv_alarm_remain_time);
        initialCurrentTime();
        mRealm = Realm.getDefaultInstance();
        RealmResults<AlarmModel> userList = mRealm.where(AlarmModel.class).findAll();
        if (userList != null && userList.size() > 0) {
            System.out.println("已经制定过闹钟信息");
            for (AlarmModel model: userList) {
                if (model.getType() == 0) {
                    getupH = model.getHour();
                    getupM = model.getMinute();
                } else {
                    sleepH = model.getHour();
                    sleepM = model.getMinute();
                }
            }
            tvTimeRange.setText((sleepH > 10 ? (sleepH + "") : ("0" + sleepH)) + ":" + (sleepM > 10 ? (sleepM + "") : ("0" + sleepM)) + "-" + (getupH > 10 ? (getupH + "") : ("0" + getupH)) + ":" + (getupM > 10 ? (getupM + "") : ("0" + getupM))  );
            Drawable drawable = getResources().getDrawable(R.mipmap.sleep_icon_clock);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvTimeRange.setCompoundDrawables(drawable,null,null,null);
            tvTip.setText(R.string.sleep_motion_tip2);
            timer.schedule(task, 0, 10000);
            tvTip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentB = new Intent(HelpSleepActivity.this, SmartSleepTestActivity.class);
                    startActivity(intentB);
                }
            });
        } else {
            tvTimeRange.setText(R.string.alarm_not_set);
            Drawable drawable = getResources().getDrawable(R.mipmap.sleep_icon_edit);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvTimeRange.setCompoundDrawables(null,null,drawable,null);
        }

        initializeHandler();

        ibPalyPause = (ImageButton)findViewById(R.id.ib_play_pause);
        ibPalyPause.setOnClickListener(this);
        ibPlayPre = (ImageButton)findViewById(R.id.ib_pre);
        ibPlayPre.setOnClickListener(this);
        ibPlayNext = (ImageButton)findViewById(R.id.ib_next);
        ibPlayNext.setOnClickListener(this);
        tvSoundTitle = (TextView)findViewById(R.id.textView3);

        switch1 = (Switch)findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new XPopup.Builder(HelpSleepActivity.this)
//                        .enableDrag(false)
                            .asBottomList(getResources().getString(R.string.report_smart_timing),
                                    new String[]{"10" + getResources().getString(R.string.common_minute),
                                            "20" + getResources().getString(R.string.common_minute),
                                            "30" + getResources().getString(R.string.common_minute),
                                            "60" + getResources().getString(R.string.common_minute),
                                            "90" + getResources().getString(R.string.common_minute)},
                                    new OnSelectListener() {
                                        @Override
                                        public void onSelect(int position, String text) {

                                        }
                                    })
                            .show();
                }
            }
        });

        btnSleep = (StateButton) findViewById(R.id.btn_sleep);
        btnSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSleep.getText().toString().equals(getResources().getString(R.string.alarm_sleep))) {
                    btnSleep.setText(R.string.common_get_up);
                    Intent intentBroadcast = new Intent();   //定义Intent
                    intentBroadcast.setAction(DYNAMICACTION);
                    intentBroadcast.putExtra("arg0", 3);
                    sendBroadcast(intentBroadcast);
                } else {
                    btnSleep.setText(R.string.alarm_sleep);
                    Intent intentBroadcast = new Intent();   //定义Intent
                    intentBroadcast.setAction(DYNAMICACTION);
                    intentBroadcast.putExtra("arg0", 4);
                    sendBroadcast(intentBroadcast);
                }
            }
        });

        boolean isSleep = getIntent().getBooleanExtra("value", false);
        if (isSleep) {
            btnSleep.setText(R.string.common_get_up);
        } else {
            btnSleep.setText(R.string.alarm_sleep);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.activity_close);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.ib_play_pause:
                if (xmPlayer.isPlaying()) {
                    xmPlayer.pause();
                } else {
                    xmPlayer.play();
                }
                break;
            case R.id.ib_pre:
                xmPlayer.playPre();
                break;
            case R.id.ib_next:
                xmPlayer.playNext();
                break;
            default:
                System.out.println("none");
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        xmPlayer.onDestroy();
    }

    // 生成大小字体不一样的内容
    private SpannableString createTimeValue(String content) {
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new AbsoluteSizeSpan(DisplayUtil.sp2px(40, this))
                , content.indexOf("时")
                , content.indexOf("时") + 1
                , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(DisplayUtil.sp2px(40, this))
                , content.indexOf("分")
                , content.indexOf("分") + 1
                , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void initializeHandler() {
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 4:
                        int arg1 = msg.arg1;
                        if (arg1 == 0) {
                            ibPalyPause.setEnabled(false);
                            ibPalyPause.setImageResource(R.mipmap.sleep_icon_play);
                        } else if (arg1 == 2) {
                            ibPalyPause.setEnabled(true);
                            ibPalyPause.setImageResource(R.mipmap.sleep_icon_stop);
                        } else if (arg1 == 3 || arg1 == 1) {
                            ibPalyPause.setEnabled(true);
                            ibPalyPause.setImageResource(R.mipmap.sleep_icon_play);
                        }
                        break;
                    case 1: // 歌曲标题
                        String title = msg.obj.toString();
                        tvSoundTitle.setText(title);
                        break;
                    case 2: // 上一首
                        int arg2 = msg.arg1;
                        ibPlayPre.setEnabled(arg2 > 0);
                        break;
                    case 3:
                        int arg3 = msg.arg1;
                        ibPlayNext.setEnabled(arg3 > 0);
                        default:
                            System.out.println("none");
                            break;
                }
            }
        };

        xmPlayer = new MainFragmentActivity();
        xmPlayer.activity = this;
        xmPlayer.handler = handler;
        xmPlayer.onCreate();
    }

    private void initialCurrentTime() {
        String str = currentDate(System.currentTimeMillis());
        String[] array = str.split("-");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(array[0]), Integer.parseInt(array[1]) - 1, Integer.parseInt(array[2]),0,0,0);
        currentTime = (int)(calendar.getTimeInMillis() / 1000);
        System.out.println("当前时间：" + currentTime);
    }

    private String currentDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(time);
        String str=simpleDateFormat.format(date);
        return str;
    }

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            try {
                refreshTime();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void refreshTime() {
        int realTime = (int)(System.currentTimeMillis() / 1000);
        System.out.println("刷新时间: " + realTime + "当前为主线程：" + (Looper.getMainLooper() == Looper.myLooper()));
        if (sleepH > 12) {
            int sleep = currentTime + sleepH * 60 * 60 + sleepM * 60  - 24 * 60 * 60;
            int getup = currentTime + getupH * 60 * 60 + getupM * 60;
            if (realTime < getup) {
                int h = (getup - realTime) / (60 * 60);
                int m = ((getup - realTime) % (60 * 60)) / 60;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTime.setText(createTimeValue( h + "时" + m + "分"));
                        tvRealTime.setText(R.string.alarm_get_up_to_time);
                    }
                });
            } else {
                int h = (sleep + 24 * 60 * 60 - realTime) / (60 * 60);
                int m = ((sleep + 24 * 60 * 60 - realTime) % (60 * 60)) / 60;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTime.setText(createTimeValue( h + "时" + m + "分"));
                        tvRealTime.setText(R.string.alarm_sleep_to_time);
                    }
                });

            }
        } else {
            int sleep = currentTime + sleepH * 60 * 60 + sleepM * 60;
            int getup = currentTime + getupH * 60 * 60 + getupM * 60;
            if (realTime < getup) {
                int h = (getup - realTime) / (60 * 60);
                int m = ((getup - realTime) % (60 * 60)) / 60;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTime.setText(createTimeValue( h + "时" + m + "分"));
                        tvRealTime.setText(R.string.alarm_get_up_to_time);
                    }
                });
            } else {
                int h = (sleep + 24 * 60 * 60 - realTime) / (60 * 60);
                int m = ((sleep + 24 * 60 * 60 - realTime) % (60 * 60)) / 60;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTime.setText(createTimeValue( h + "时" + m + "分"));
                        tvRealTime.setText(R.string.alarm_sleep_to_time);
                    }
                });
            }
        }
    }
}
