package com.zhang.xiaofei.smartsleep.UI.Home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sunofbeaches.himalaya.PlayHelper;
import com.sunofbeaches.himalaya.PlayHelperCallback;
import com.zhang.xiaofei.smartsleep.Kit.AlarmTimer;
import com.zhang.xiaofei.smartsleep.Kit.DB.CacheUtil;
import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceManager;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;

public class HelpSleepActivity extends BaseAppActivity implements View.OnClickListener, PlayHelperCallback, AlarmTimer.AlarmTimerInterface {

    ImageButton ibBack;
    TextView tvTime;
    TextView tvTimeRange;
    TextView tvTip;
    TextView tvRealTime;
    Handler handler;
    RoundedImageView roundedImageView;
    TextView tvCount;
    ImageButton ibPalyPause;
    ImageButton ibPlayPre;
    ImageButton ibPlayNext;
    TextView tvSoundTitle;
    TextView tvHeartAndBreath;
    Switch switch1;
    CountDownButton btnSleep;
    private static final String DYNAMICACTION = "Filter";
    private static final String HELPSLEEPDYNAMICACTION = "Filter2";
    Realm mRealm;
    int currentTime;
    int getupH = 0;
    int getupM = 0;
    int sleepH = 0;
    int sleepM = 0;
    String strH = "";
    String strM = "";
    private PlayHelper playHelper; // 播放帮助类
    private Timer buttonCountDownTimer; // 点击按钮，有3，2，1动画，使用定时器实现功能。
    private int buttonCountDownCount = 0; // 点击按钮有3秒过渡动画
    private boolean bSleep = false;
    private DynamicReceiver dynamicReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction("Filter");
        intentBroadcast.putExtra("arg0", 5);
        intentBroadcast.putExtra("value", true);
        sendBroadcast(intentBroadcast);

        bSleep = CacheUtil.getInstance(HelpSleepActivity.this).getBool("sleep");
        strH = getResources().getString(R.string.common_hour2);
        strM = getResources().getString(R.string.common_minute3);
        setContentView(R.layout.activity_help_sleep);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        ibBack.setOnClickListener(this);
        tvTime = (TextView)findViewById(R.id.tv_time);
        tvTime.setText(createTimeValue("00" + strH + "00" + strM));
        tvTimeRange = (TextView)findViewById(R.id.tv_time_range);
        tvTip = (TextView)findViewById(R.id.tv_tip);
        tvRealTime = (TextView)findViewById(R.id.tv_alarm_remain_time);
        tvHeartAndBreath = (TextView)findViewById(R.id.tv_heart_breath);
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
            tvTimeRange.setText((sleepH > 9 ? (sleepH + "") : ("0" + sleepH)) + ":" + (sleepM > 9 ? (sleepM + "") : ("0" + sleepM)) + "-" + (getupH > 9 ? (getupH + "") : ("0" + getupH)) + ":" + (getupM > 9 ? (getupM + "") : ("0" + getupM))  );
            Drawable drawable = getResources().getDrawable(R.mipmap.sleep_icon_clock);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvTimeRange.setCompoundDrawables(drawable,null,null,null);

            timer.schedule(task, 0, 10000);

        } else {
            tvTimeRange.setText(R.string.alarm_not_set);
            Drawable drawable = getResources().getDrawable(R.mipmap.sleep_icon_edit);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvTimeRange.setCompoundDrawables(null,null,drawable,null);
        }

        tvTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentB = new Intent(HelpSleepActivity.this, SmartSleepTestActivity.class);
                startActivity(intentB);
            }
        });

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
                                            int time = 0;
                                            if (position == 0) {
                                                time = 10 * 60;
                                            } else if (position == 1) {
                                                time = 20 * 60;
                                            } else if (position == 2) {
                                                time = 30 * 60;
                                            } else if (position == 3) {
                                                time = 60 * 60;
                                            } else {
                                                time = 90 * 60;
                                            }
                                            Intent intentBroadcast = new Intent();   //定义Intent
                                            intentBroadcast.setAction(DYNAMICACTION);
                                            intentBroadcast.putExtra("arg0", 7);
                                            intentBroadcast.putExtra("value", time);
                                            sendBroadcast(intentBroadcast);
                                        }
                                    })
                            .show();
                } else {
                    Intent intentBroadcast = new Intent();   //定义Intent
                    intentBroadcast.setAction(DYNAMICACTION);
                    intentBroadcast.putExtra("arg0", 7);
                    sendBroadcast(intentBroadcast);
                }
            }
        });
        switch1.setChecked(AlarmTimer.getInstance().bStart);
        AlarmTimer.getInstance().list.add(this);

        btnSleep = (CountDownButton) findViewById(R.id.btn_sleep);
        btnSleep.setHandler(countDownHandler);

        if (bSleep) {
            btnSleep.setText(R.string.common_get_up);
        } else {
            btnSleep.setText(R.string.alarm_sleep);
        }

        playHelper = new PlayHelper();
        playHelper.playHelperCallback = this;
        if (playHelper.initPresenter(this)) {
            showHUD();
        }
        if (playHelper.isPlaying()) {
            ibPalyPause.setImageResource(R.mipmap.sleep_icon_stop);
            tvSoundTitle.setText(playHelper.playTitle());
        }

        refreshMonitorValue();

        roundedImageView = (RoundedImageView)findViewById(R.id.iv_middle_circle);
        tvCount = (TextView)findViewById(R.id.tv_count);
        roundedImageView.setVisibility(View.INVISIBLE);
        tvCount.setVisibility(View.INVISIBLE);

        handleFixedTimeForHeartAndHealth(); // 读取心率和呼吸率
        registerBroadcast();
    }

    private void refreshMonitorValue() {
        if (DeviceManager.getInstance().deviceList.size() > 0 && bSleep == true) {
            if (DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType() == 1) {
                tvTip.setText(R.string.sleep_motion_tip3);
            } else {
                tvTip.setText(R.string.sleep_motion_tip2);
            }
        } else {
            tvTip.setText("");
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.activity_close);
        readHeartAndHealthTimer.cancel();
        readHeartAndHealthTimer = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.ib_play_pause:
                playHelper.play();
                break;
            case R.id.ib_pre:
                playHelper.playPre();
                break;
            case R.id.ib_next:
                playHelper.playNext();
                break;
            default:
                System.out.println("none");
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playHelper.deinitPresenter();
        playHelper.playHelperCallback = null;
        AlarmTimer.getInstance().list.remove(this);
        unregisterBroadcast();
        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction("Filter");
        intentBroadcast.putExtra("arg0", 9);
        intentBroadcast.putExtra("value", false);
        sendBroadcast(intentBroadcast);
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
        if (buttonCountDownTimer != null) {
            buttonCountDownTimer.cancel();
            buttonCountDownTimer = null;
        }

    }

    // 生成大小字体不一样的内容
    private SpannableString createTimeValue(String content) {
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new AbsoluteSizeSpan(DisplayUtil.sp2px(40, this))
                , content.indexOf(strH)
                , content.indexOf(strH) + 1
                , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        if (strM.trim().length() > 0) {
            spannableString.setSpan(new AbsoluteSizeSpan(DisplayUtil.sp2px(40, this))
                    , content.indexOf(strM)
                    , content.indexOf(strM) + 1
                    , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
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
        int sleep = currentTime + sleepH * 60 * 60 + sleepM * 60;
        int getup = currentTime + getupH * 60 * 60 + getupM * 60;
        if (getup < sleep) {
            getup += 24 * 60 * 60;
        }
        if (realTime > getup) {
            sleep += 24 * 60 * 60;
            getup += 24 * 60 * 60;
        }
        if (realTime <= sleep) {
            int h = (sleep - realTime) / (60 * 60);
            int m = ((sleep - realTime) % (60 * 60)) / 60;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTime.setText(createTimeValue( (h > 9 ? ("" + h) : ("0" + h)) + strH + (m > 9 ? ("" + m) : ("0" + m)) + strM));
                    tvRealTime.setText(R.string.alarm_sleep_to_time);
                }
            });
        } else if (realTime <= getup) {
            int h = (getup - realTime) / (60 * 60);
            int m = ((getup - realTime) % (60 * 60)) / 60;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTime.setText(createTimeValue( (h > 9 ? ("" + h) : ("0" + h)) + strH + (m > 9 ? ("" + m) : ("0" + m)) + strM));
                    tvRealTime.setText(R.string.alarm_get_up_to_time);
                }
            });
        }
    }


    @Override
    public void refreshPlayStatus(boolean isPlaying) {
        if (isPlaying) {
            ibPalyPause.setImageResource(R.mipmap.sleep_icon_stop);
        } else {
            ibPalyPause.setImageResource(R.mipmap.sleep_icon_play);
        }

    }

    @Override
    public void refreshPlayTitle(String title) {
        tvSoundTitle.setText(title);
    }

    @Override
    public void callbackHideHUD() {
        hideHUD();
    }

    @Override
    public void stopAlarm() {
        switch1.setChecked(AlarmTimer.getInstance().bStart);
    }

    Handler countDownHandler = new Handler(){
        public void handleMessage(Message msg) {
            int value = msg.what;
            switch (value) {
                case 0:
                    if (buttonCountDownTimer != null) {
                        return;
                    }
                    roundedImageView.setVisibility(View.VISIBLE);
                    tvCount.setVisibility(View.VISIBLE);
                    buttonCountDownTimer = new Timer();
                    buttonCountDownTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    buttonCountDownCount++;
                                    if (buttonCountDownCount >= 3) {
                                        buttonCountDownTimer.cancel();
                                        buttonCountDownTimer = null;
                                        buttonCountDownCount = 0;
                                        roundedImageView.setVisibility(View.INVISIBLE);
                                        tvCount.setVisibility(View.INVISIBLE);
                                        tvCount.setText("" + 3);
                                        if (btnSleep.getText().toString().equals(getResources().getString(R.string.alarm_sleep))) {
                                            bSleep = true;
                                            btnSleep.setText(R.string.common_get_up);
                                            Intent intentBroadcast = new Intent();   //定义Intent
                                            intentBroadcast.setAction(DYNAMICACTION);
                                            intentBroadcast.putExtra("arg0", 3);
                                            sendBroadcast(intentBroadcast);
                                        } else {
                                            bSleep = false;
                                            btnSleep.setText(R.string.alarm_sleep);
                                            Intent intentBroadcast = new Intent();   //定义Intent
                                            intentBroadcast.setAction(DYNAMICACTION);
                                            intentBroadcast.putExtra("arg0", 4);
                                            sendBroadcast(intentBroadcast);
                                        }

                                        refreshMonitorValue();

                                        return;
                                    }
                                    tvCount.setText("" + (3 - buttonCountDownCount));
                                }
                            });
                        }
                    }, 1000, 1000);
                    break;
                case 1:
                    if (buttonCountDownTimer != null) {
                        buttonCountDownTimer.cancel();
                        buttonCountDownTimer = null;
                        buttonCountDownCount = 0;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                roundedImageView.setVisibility(View.INVISIBLE);
                                tvCount.setVisibility(View.INVISIBLE);
                                tvCount.setText("" + 3);
                            }
                        });
                        return;
                    }
                    break;
                case 2:
                    break;
            }
        };
    };

    // 读取离床、心率和呼吸率
    private Timer readHeartAndHealthTimer = new Timer();
    // 读取温度和湿度的定时任务
    private TimerTask readHeartAndHealthTask = new TimerTask() {
        @Override
        public void run() {
            // 要做的事情
            Intent intentBroadcast = new Intent();   //定义Intent
            intentBroadcast.setAction("Filter");
            intentBroadcast.putExtra("arg0", 10);
            sendBroadcast(intentBroadcast);

        }
    };

    /// 刷新定时获取温度和湿度的区域
    private void handleFixedTimeForHeartAndHealth() {
        readHeartAndHealthTimer.schedule(readHeartAndHealthTask, 1000, 5000);
    }

    private void registerBroadcast() {
        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction(HELPSLEEPDYNAMICACTION);    //添加动态广播的Action
        dynamicReceiver = new DynamicReceiver();
        registerReceiver(dynamicReceiver, dynamic_filter);    //注册自定义动态广播消息
    }

    private void unregisterBroadcast() {
        unregisterReceiver(dynamicReceiver);
    }

    public class DynamicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(HELPSLEEPDYNAMICACTION)) {    //动作检测
                //System.out.println("检测到用户到绑定设备");
                int arg0 = intent.getIntExtra("arg0", 0);
                if (arg0 == 0) { // 心跳和呼吸率
                    int arg1 = intent.getIntExtra("arg1", 0);
                    int arg2 = intent.getIntExtra("arg2", 0);
                    int arg3 = intent.getIntExtra("arg3", 0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (arg1 == 0) {
                                tvHeartAndBreath.setText(getResources().getString(R.string.bed_away));
                            } else {
                                tvHeartAndBreath.setText(getResources().getString(R.string.report_heart) + "：" + arg2 + getResources().getString(R.string.common_times_minute) + " " + getResources().getString(R.string.report_respiratory_rate) + "：" + arg3 + getResources().getString(R.string.common_times_minute));
                            }
                        }
                    });
                } else if (arg0 == 1) { // 删除设备

                }
            }
        }
    }
}
