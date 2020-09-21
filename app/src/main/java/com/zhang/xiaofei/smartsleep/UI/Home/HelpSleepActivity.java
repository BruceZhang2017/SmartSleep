package com.zhang.xiaofei.smartsleep.UI.Home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.deadline.statebutton.StateButton;
import com.loonggg.lib.alarmmanager.clock.ClockAlarmBActivity;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zhang.xiaofei.smartsleep.Kit.AlarmTimer;
import com.zhang.xiaofei.smartsleep.Kit.Application.LogInterceptor;
import com.zhang.xiaofei.smartsleep.Kit.DB.CacheUtil;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceManager;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.Tools.SendCMDToHomeActivity;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.UI.music.Audio;
import com.zhang.xiaofei.smartsleep.UI.music.PlayMusicChange;
import com.zhang.xiaofei.smartsleep.UI.music.StorageUtil;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HelpSleepActivity extends BaseAppActivity implements View.OnClickListener, AlarmTimer.AlarmTimerInterface, PlayMusicChange {
    ImageButton ibBack;
    TextView tvTime;
    TextView tvTimeRange;
    TextView tvTip;
    TextView tvRealTime;
    RoundedImageView roundedImageView;
    TextView tvCount;
    TextView tvMonitor;
    ImageButton ibPalyPause;
    ImageButton ibPlayPre;
    ImageButton ibPlayNext;
    TextView tvSoundTitle;
    TextView tvHeartAndBreath;
    Switch switch1;
    StateButton btnSleep;
    Button btnSleepTime;
    private static final String HELPSLEEPDYNAMICACTION = "Filter2";
    Realm mRealm;
    int currentTime;
    int getupH = 0;
    int getupM = 0;
    int sleepH = 0;
    int sleepM = 0;
    String strH = "";
    String strM = "";
    private boolean bSleep = false;
    private DynamicReceiver dynamicReceiver;
    private String sleepStartTime = ""; // 睡觉开始时间
    private String sleepEndTime = ""; // 睡觉结束时间
    private boolean bleConnected = false;
    private int getAwayDuration = 0; // 离床时间统计
    private int heartValue = 0;
    private int breathValue = 0;
    private Boolean timeRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bleConnected = getIntent().getBooleanExtra("bleConnected", false);
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

        tvTimeRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentB = new Intent(HelpSleepActivity.this, AlarmActivity.class);
                startActivity(intentB);
            }
        });

        tvTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentB = new Intent(HelpSleepActivity.this, SmartSleepTestActivity.class);
                intentB.putExtra("arg2", heartValue);
                intentB.putExtra("arg3", breathValue);
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

        if (YMApplication.getInstance().player != null && YMApplication.getInstance().player.playing) {
            ibPalyPause.setImageResource(R.mipmap.sleep_icon_stop);
            refreshMusicName();
        }

        switch1 = (Switch)findViewById(R.id.switch1);
        switch1.setChecked(AlarmTimer.getInstance().bStart);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showTimeChoose();
                } else {
                    SendCMDToHomeActivity.send(7, 0, HelpSleepActivity.this);
                }
            }
        });
        AlarmTimer.getInstance().list.add(this);

        btnSleep = (StateButton) findViewById(R.id.btn_sleep);
        btnSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSleep.getText().toString().equals(getResources().getString(R.string.alarm_sleep))) {
                    bSleep = true;
                    btnSleep.setText(R.string.common_get_up);
                    SendCMDToHomeActivity.send(3, 0, HelpSleepActivity.this); // 发送睡觉通知
                    saveSleepStartTime();
                } else {
                    bSleep = false;
                    SendCMDToHomeActivity.send(4,0, HelpSleepActivity.this); // 发送起床通知
                    saveSleepEndTime();
                    Intent intentBroadcast = new Intent();   //定义Intent
                    intentBroadcast.setAction("Filter");
                    intentBroadcast.putExtra("arg0", 17);
                    YMApplication.getContext().sendBroadcast(intentBroadcast);
                    btnSleep.setText(R.string.alarm_sleep);
                }
                refreshMonitorValue();
            }
        });

        if (bSleep) {
            btnSleep.setText(R.string.common_get_up);
        } else {
            btnSleep.setText(R.string.alarm_sleep);
        }

        refreshMonitorValue();

        roundedImageView = (RoundedImageView)findViewById(R.id.iv_middle_circle);
        tvCount = (TextView)findViewById(R.id.tv_count);
        roundedImageView.setVisibility(View.INVISIBLE);
        tvCount.setVisibility(View.INVISIBLE);

        handleFixedTimeForHeartAndHealth(); // 读取心率和呼吸率
        registerBroadcast();

        btnSleepTime = (Button)findViewById(R.id.btn_sleep_time);
        btnSleepTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AlarmTimer.getInstance().bStart) {
                    showTimeChoose();
                }
            }
        });

        if (YMApplication.getInstance().player != null) {
            YMApplication.getInstance().player.regMusicChange(this);
        }

        tvMonitor = (TextView)findViewById(R.id.tv_monitor);
        tvMonitor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpSleepActivity.this, DynamicMonitorActivity.class);
                startActivity(intent);
            }
        });
    }

    private void refreshMusicName() {
        StorageUtil storage = new StorageUtil(getApplicationContext());
        List<Audio> audioList = storage.loadAudio();
        int audioIndex = storage.loadAudioIndex();
        tvSoundTitle.setText(audioList.get(audioIndex).getTitle());
    }

    private void showTimeChoose() {
        int position = CacheUtil.getInstance(this).getInt("StopMusic");
        new XPopup.Builder(HelpSleepActivity.this)
//                        .enableDrag(false)
                .asBottomList(getResources().getString(R.string.report_smart_timing),
                        new String[]{"10" + getResources().getString(R.string.common_minute),
                                "20" + getResources().getString(R.string.common_minute),
                                "30" + getResources().getString(R.string.common_minute),
                                "60" + getResources().getString(R.string.common_minute),
                                "90" + getResources().getString(R.string.common_minute)},
                        null,
                        position - 1,
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
                                SendCMDToHomeActivity.send(7, time, HelpSleepActivity.this);
                                CacheUtil.getInstance(HelpSleepActivity.this).setInt("StopMusic", position + 1);
                            }
                        })
                .show();
    }

    private void refreshMonitorValue() {
        if (DeviceManager.getInstance().deviceList.size() > 0 && bSleep == true && bleConnected) {
            if (DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType() == 1) {
                tvTip.setText(getResources().getString(R.string.sleep_motion_tip3) + " >");
            } else {
                tvTip.setText(getResources().getString(R.string.sleep_motion_tip2) + " >");
            }
            tvTip.setVisibility(View.VISIBLE);
        } else {
            tvTip.setVisibility(View.INVISIBLE);
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
                if (YMApplication.getInstance().player == null) {
                    return;
                }
                if (YMApplication.getInstance().player.playing) {
                    YMApplication.getInstance().player.pauseMedia();
                    ibPalyPause.setImageResource(R.mipmap.sleep_icon_play);
                    tvSoundTitle.setText("");
                } else {
                    YMApplication.getInstance().player.resumeMedia();
                    ibPalyPause.setImageResource(R.mipmap.sleep_icon_stop);
                    refreshMusicName();
                }
                break;
            case R.id.ib_pre:
                if (YMApplication.getInstance().player == null) {
                    return;
                }
                YMApplication.getInstance().player.skipToPrevious();
                refreshMusicName();
                break;
            case R.id.ib_next:
                if (YMApplication.getInstance().player == null) {
                    return;
                }
                YMApplication.getInstance().player.skipToNext();
                refreshMusicName();
                break;
            default:
                System.out.println("none");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean bSetAlarm = false;
        RealmResults<AlarmModel> userList = mRealm.where(AlarmModel.class).findAll();
        if (userList != null && userList.size() > 0) {
            System.out.println("已经制定过闹钟信息");
            for (AlarmModel model : userList) {
                if (model.getType() == 0) {
                    getupH = model.getHour();
                    getupM = model.getMinute();
                }
                if (model.isOpen()) {
                    bSetAlarm = true;
                }
            }
            sleepH = 22;
            sleepM = 30;
        }
        if (bSetAlarm) {
            tvTimeRange.setText((sleepH > 9 ? (sleepH + "") : ("0" + sleepH)) + ":" + (sleepM > 9 ? (sleepM + "") : ("0" + sleepM)) + "-" + (getupH > 9 ? (getupH + "") : ("0" + getupH)) + ":" + (getupM > 9 ? (getupM + "") : ("0" + getupM))  );
            Drawable drawable = getResources().getDrawable(R.mipmap.sleep_icon_clock);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvTimeRange.setCompoundDrawables(drawable,null,null,null);
            if (timeRunning) {
                return;
            }
            timeRunning = true;
            timer.schedule(task, 0, 10000);

        } else {
            tvTimeRange.setText(R.string.alarm_not_set);
            Drawable drawable = getResources().getDrawable(R.mipmap.sleep_icon_edit);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvTimeRange.setCompoundDrawables(null,null,drawable,null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

        if (YMApplication.getInstance().player != null) {
            YMApplication.getInstance().player.unregMusicChange();
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
    public void stopAlarm() {
        switch1.setChecked(AlarmTimer.getInstance().bStart);
    }

    private void saveSleepStartTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        String str = simpleDateFormat.format(date);
        sleepStartTime = str;
        CacheUtil.getInstance(HelpSleepActivity.this).putString("SleepStart", sleepStartTime);
    }

    private void saveSleepEndTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        String str = simpleDateFormat.format(date);
        sleepEndTime = str;
        if (sleepStartTime.length() == 0) {
            sleepStartTime = CacheUtil.getInstance(HelpSleepActivity.this).getString("SleepStart");
        }
        if (sleepStartTime.length() == 0) {
            return;
        }
        try {
            Date dateB = simpleDateFormat.parse(sleepStartTime);
            if (date.getTime() - dateB.getTime() < 10 * 60 * 1000) {
                Toast.makeText(this, R.string.sleep_report_fail, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception exc) {

        }

        List<String> arrayList = SleepAndGetupTimeManager.times.get(sleepEndTime.substring(0, 10));
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        String time = sleepStartTime + "&" + sleepEndTime;
        if (arrayList.contains(time)) {
            return;
        }
        arrayList.add(sleepStartTime + "&" + sleepEndTime);
        SleepAndGetupTimeManager.times.put(sleepEndTime.substring(0, 10), arrayList);
        SleepAndGetupTimeManager.putHashMapData();
        upSleepAndGetupTime(sleepStartTime + ":00", sleepEndTime + ":00");
        YMApplication.getInstance().uploadDataToCloud();
    }

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
            if (intent.getAction().equals(HELPSLEEPDYNAMICACTION)) {    //动作检测 5秒一次
                //System.out.println("检测到用户到绑定设备");
                int arg0 = intent.getIntExtra("arg0", 0);
                if (arg0 == 0) { // 心跳和呼吸率
                    int arg1 = intent.getIntExtra("arg1", 0);
                    int arg2 = intent.getIntExtra("arg2", 0);
                    int arg3 = intent.getIntExtra("arg3", 0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("当前离床统计的数据为：" + getAwayDuration);
                            if (arg1 == 0) {
                                tvHeartAndBreath.setText(getResources().getString(R.string.bed_away)); // 离床
                                getAwayDuration++;
                                if (getAwayDuration >= 180) {
                                    if (CacheUtil.getInstance(HelpSleepActivity.this).getBool("GetAway")) {
                                        //AManager.alarmGetup(HelpSleepActivity.this.getApplicationContext(), 0);
                                        showAlarmDialog(context.getResources().getString(R.string.warning), context.getResources().getString(R.string.reminder));
                                    }
                                    getAwayDuration = 0;
                                }
                                Intent intentBroadcast = new Intent();   //定义Intent
                                intentBroadcast.setAction("filter3");
                                intentBroadcast.putExtra("arg2", 0);
                                intentBroadcast.putExtra("arg3", 0);
                                sendBroadcast(intentBroadcast);
                            } else {
                                getAwayDuration = 0;
                                if (bSleep){
                                    tvHeartAndBreath.setText(getResources().getString(R.string.report_heart) + "：" + arg2 + " " + getResources().getString(R.string.common_times_minute) + "\n" + getResources().getString(R.string.report_respiratory_rate) + "：" + arg3 + " " + getResources().getString(R.string.common_times_minute));
                                    if (CacheUtil.getInstance(HelpSleepActivity.this).getBool("AbnormalHeartRate")) {
                                        if (arg2 > 100) {
                                            //AManager.alarmGetup(HelpSleepActivity.this.getApplicationContext(), 1);
                                            showAlarmDialog(context.getResources().getString(R.string.warning), context.getResources().getString(R.string.heart_rate_100));
                                        }
                                    }
                                    heartValue = arg2;
                                    breathValue = arg3;
                                    Intent intentBroadcast = new Intent();   //定义Intent
                                    intentBroadcast.setAction("filter3");
                                    intentBroadcast.putExtra("arg2", arg2);
                                    intentBroadcast.putExtra("arg3", arg3);
                                    sendBroadcast(intentBroadcast);
                                } else {
                                    tvHeartAndBreath.setText(getResources().getString(R.string.report_heart) + "：" + "-- " + getResources().getString(R.string.common_times_minute) + "\n" + getResources().getString(R.string.report_respiratory_rate) + "：" + "-- " + getResources().getString(R.string.common_times_minute));
                                }
                            }
                        }
                    });
                } else if (arg0 == 1) { // 删除设备

                }
            }
        }
    }

    private void upSleepAndGetupTime(String upTime, String downTime) {
        YMUserInfoManager userInfoManager = new YMUserInfoManager(YMApplication.getContext());
        UserModel model = userInfoManager.loadUserInfo();
        if (model == null) {
            return;
        }
        OkHttpClient okHttpClient  = new OkHttpClient.Builder().addInterceptor(new LogInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = new FormBody.Builder().add("appUserId", model.getUserInfo().getUserId() + "").add("upTime", upTime).add("downTime", downTime).build();
        Request request = new Request.Builder()
                .url(YMApplication.getInstance().domain() + "app/userPara/saveUserTime")//请求的url
                .addHeader("token", model.getToken())
                .post(requestBody)
                .build();

        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("网络请求失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {

                } else {
                    System.out.println("从服务器下载数据失败");
                }
            }
        });
    }

    @Override
    public void playMusicChange() {
        refreshMusicName();
    }

    public void showAlarmDialog(String title, String message) {
        Intent clockIntent = new Intent(this, ClockAlarmBActivity.class);
        clockIntent.putExtra("flag", 2);
        clockIntent.putExtra("index", 0);
        clockIntent.putExtra("title", title);
        clockIntent.putExtra("message", message);
        clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(clockIntent);
    }
}
