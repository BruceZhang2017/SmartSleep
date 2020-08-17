package com.zhang.xiaofei.smartsleep.UI.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.deadline.statebutton.StateButton;
import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;
import com.zhang.xiaofei.smartsleep.Kit.DB.CacheUtil;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.UI.Report.SoundChooseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class AlarmActivity extends BaseAppActivity implements View.OnClickListener {

    @BindView(R.id.im_l) ImageButton ibLeft;
    @BindView(R.id.wv_minutes) WheelView wvMinutes1;
    @BindView(R.id.wv_hours) WheelView wvHours1;
    @BindView(R.id.btn_sunday) StateButton btnSunday;
    @BindView(R.id.btn_monday) StateButton btnMonday;
    @BindView(R.id.btn_tuesday) StateButton btnTuesday;
    @BindView(R.id.btn_wednesday) StateButton btnWednesday;
    @BindView(R.id.btn_thursday) StateButton btnThursday;
    @BindView(R.id.btn_friday) StateButton btnFirday;
    @BindView(R.id.btn_saturday) StateButton btnSaturday;
    @BindView(R.id.switch_alarm) Switch switchAlarm;
    @BindView(R.id.switch_sleep_alert) Switch switchSleepAlarm;
    @BindView(R.id.tv_value) TextView tvValue;

    private ArrayList<String> hours = new ArrayList<>();
    private ArrayList<String> minutes = new ArrayList<>();
    private Realm mRealm;
    private AlarmModel alramGetupModel;
    private AlarmModel alarmGetupModelNew;
    private int currentPostion = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);
        currentPostion = CacheUtil.getInstance(this).getInt("alarmSound");
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRealm = Realm.getDefaultInstance();
        RealmResults<AlarmModel> userList = mRealm.where(AlarmModel.class).findAll();
        if (userList != null && userList.size() > 0) {
            System.out.println("已经制定过闹钟信息");
            for (AlarmModel model: userList) {
                if (model.getType() == 0) {
                    alramGetupModel = model;
                    alarmGetupModelNew = new AlarmModel();
                    alarmGetupModelNew.setType(0);
                    alarmGetupModelNew.setHour(model.getHour());
                    alarmGetupModelNew.setMinute(model.getMinute());
                    alarmGetupModelNew.setMonday(model.isMonday());
                    alarmGetupModelNew.setThursday(model.isThursday());
                    alarmGetupModelNew.setWednesday(model.isWednesday());
                    alarmGetupModelNew.setThursday(model.isThursday());
                    alarmGetupModelNew.setFirday(model.isFirday());
                    alarmGetupModelNew.setSaturday(model.isSaturday());
                    alarmGetupModelNew.setSunday(model.isSunday());
                    alarmGetupModelNew.setOpen(model.isOpen());
                }
            }
        }
        if (alarmGetupModelNew != null) {
            switchAlarm.setChecked(alarmGetupModelNew.isOpen());
        }
        initialWheel();
        initialButtons();
        handleSwitchValueChanged();

        if (alarmGetupModelNew == null) {
            alarmGetupModelNew = new AlarmModel();
            alarmGetupModelNew.setType(0);
            alarmGetupModelNew.setHour(12);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int postion = CacheUtil.getInstance(this).getInt("alarmSound");
        if (currentPostion == postion) {
            return;
        }
        currentPostion = postion;
        tvValue.setText(getResources().getString(R.string.sound) + Math.max(1, currentPostion / 10));
        startClockStartGetup();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("初始化闹钟功能");
        boolean bAlarmOpen = alarmGetupModelNew.isOpen();
        if (bAlarmOpen) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (alramGetupModel == null) {
                        AlarmModel model = realm.createObject(AlarmModel.class);
                        model.setHour(alarmGetupModelNew.getHour());
                        model.setMinute(alarmGetupModelNew.getMinute());
                        model.setMonday(alarmGetupModelNew.isMonday());
                        model.setThursday(alarmGetupModelNew.isThursday());
                        model.setWednesday(alarmGetupModelNew.isWednesday());
                        model.setThursday(alarmGetupModelNew.isThursday());
                        model.setFirday(alarmGetupModelNew.isFirday());
                        model.setSaturday(alarmGetupModelNew.isSaturday());
                        model.setSunday(alarmGetupModelNew.isSunday());
                        model.setOpen(alarmGetupModelNew.isOpen());
                        model.setType(alarmGetupModelNew.getType());
                    } else {
                        alramGetupModel.setHour(alarmGetupModelNew.getHour());
                        alramGetupModel.setMinute(alarmGetupModelNew.getMinute());
                        alramGetupModel.setMonday(alarmGetupModelNew.isMonday());
                        alramGetupModel.setThursday(alarmGetupModelNew.isThursday());
                        alramGetupModel.setWednesday(alarmGetupModelNew.isWednesday());
                        alramGetupModel.setThursday(alarmGetupModelNew.isThursday());
                        alramGetupModel.setFirday(alarmGetupModelNew.isFirday());
                        alramGetupModel.setSaturday(alarmGetupModelNew.isSaturday());
                        alramGetupModel.setSunday(alarmGetupModelNew.isSunday());
                        alramGetupModel.setOpen(alarmGetupModelNew.isOpen());
                        alramGetupModel.setType(alarmGetupModelNew.getType());
                    }
                }
            });
        } else {
            RealmResults<AlarmModel> userList = mRealm.where(AlarmModel.class).equalTo("type", 0).findAll();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    userList.deleteAllFromRealm();
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    // 设置时间选择器上数据
    private void getNoLinkData() {
        for (int i = 0; i < 24; i++) {
            hours.add(i < 10 ? ("0" + i) : (i + ""));
        }
        for (int i = 0; i < 59; i++) {
            minutes.add(i < 10 ? ("0" + i) : ("" + i));
        }
    }

    private void initialWheel() {
        getNoLinkData();

        wvMinutes1.setTextSize(40);
        wvMinutes1.setLineSpacingMultiplier(2f);
        wvMinutes1.setDividerWidth(10);
        wvMinutes1.setItemsVisibleCount(3);
        wvMinutes1.setDividerColor(getResources().getColor(R.color.tranparencyColor));
        wvMinutes1.setTextColorCenter(getResources().getColor(R.color.colorWhite));
        wvMinutes1.setTextColorOut(getResources().getColor(R.color.color_555A63));

        wvMinutes1.setAdapter(new ArrayWheelAdapter(minutes));
        wvMinutes1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                System.out.println("设置的分钟：" + index);
                alarmGetupModelNew.setMinute(index);
                startClockStartGetup();
            }
        });
        if (alarmGetupModelNew != null) {
            wvMinutes1.setCurrentItem(alarmGetupModelNew.getMinute());
        } else {
            wvMinutes1.setCurrentItem(0);
        }

        wvHours1.setTextSize(40);
        wvHours1.setLineSpacingMultiplier(2f);
        wvHours1.setDividerWidth(10);
        wvHours1.setItemsVisibleCount(3);
        wvHours1.setDividerColor(getResources().getColor(R.color.tranparencyColor));
        wvHours1.setTextColorCenter(getResources().getColor(R.color.colorWhite));
        wvHours1.setTextColorOut(getResources().getColor(R.color.color_555A63));
        wvHours1.setAdapter(new ArrayWheelAdapter(hours));
        wvHours1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                System.out.println("设置的小时：" + index);
                alarmGetupModelNew.setHour(index);
                startClockStartGetup();
            }
        });
        if (alarmGetupModelNew != null) {
            wvHours1.setCurrentItem(alarmGetupModelNew.getHour());
        }
    }

    private void initialButtons() {
        btnSunday.setOnClickListener(this);
        btnMonday.setOnClickListener(this);
        btnTuesday.setOnClickListener(this);
        btnWednesday.setOnClickListener(this);
        btnThursday.setOnClickListener(this);
        btnFirday.setOnClickListener(this);
        btnSaturday.setOnClickListener(this);

        tvValue.setOnClickListener(this);
        tvValue.setText(getResources().getString(R.string.sound) + Math.max(1, currentPostion / 10));
        if (alarmGetupModelNew != null) {
            btnMonday.setSelected(alarmGetupModelNew.isMonday());
            btnTuesday.setSelected(alarmGetupModelNew.isTuesday());
            btnWednesday.setSelected(alarmGetupModelNew.isWednesday());
            btnThursday.setSelected(alarmGetupModelNew.isThursday());
            btnFirday.setSelected(alarmGetupModelNew.isFirday());
            btnSaturday.setSelected(alarmGetupModelNew.isSaturday());
            btnSunday.setSelected(alarmGetupModelNew.isSunday());
            changeColor(btnSunday.isSelected(), btnSunday);
            changeColor(btnMonday.isSelected(), btnMonday);
            changeColor(btnTuesday.isSelected(), btnTuesday);
            changeColor(btnWednesday.isSelected(), btnWednesday);
            changeColor(btnThursday.isSelected(), btnThursday);
            changeColor(btnFirday.isSelected(), btnFirday);
            changeColor(btnSaturday.isSelected(), btnSaturday);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sunday:
                btnSunday.setSelected(!btnSunday.isSelected());
                changeColor(btnSunday.isSelected(), btnSunday);
                boolean sunday = btnSunday.isSelected();
                alarmGetupModelNew.setSunday(sunday);
                break;
            case R.id.btn_monday:
                btnMonday.setSelected(!btnMonday.isSelected());
                changeColor(btnMonday.isSelected(), btnMonday);
                boolean monday = btnMonday.isSelected();
                alarmGetupModelNew.setMonday(monday);
                break;
            case R.id.btn_tuesday:
                btnTuesday.setSelected(!btnTuesday.isSelected());
                changeColor(btnTuesday.isSelected(), btnTuesday);
                boolean tuesday = btnTuesday.isSelected();
                alarmGetupModelNew.setTuesday(tuesday);
                break;
            case R.id.btn_wednesday:
                btnWednesday.setSelected(!btnWednesday.isSelected());
                changeColor(btnWednesday.isSelected(), btnWednesday);
                boolean wednesday = btnWednesday.isSelected();
                alarmGetupModelNew.setWednesday(wednesday);
                break;
            case R.id.btn_thursday:
                btnThursday.setSelected(!btnThursday.isSelected());
                changeColor(btnThursday.isSelected(), btnThursday);
                boolean thursday = btnThursday.isSelected();
                alarmGetupModelNew.setThursday(thursday);
                break;
            case R.id.btn_friday:
                btnFirday.setSelected(!btnFirday.isSelected());
                changeColor(btnFirday.isSelected(), btnFirday);
                boolean firday = btnFirday.isSelected();
                alarmGetupModelNew.setFirday(firday);
                break;
            case R.id.btn_saturday:
                btnSaturday.setSelected(!btnSaturday.isSelected());
                changeColor(btnSaturday.isSelected(), btnSaturday);
                boolean saturday = btnSaturday.isSelected();
                alarmGetupModelNew.setSaturday(saturday);
                break;
            case R.id.tv_value:
                Intent intent = new Intent(AlarmActivity.this, SoundChooseActivity.class);
                intent.putExtra("isSleep", false);
                startActivity(intent);
                break;

        }
    }

    private void changeColor(boolean value, StateButton btn) {
        if (value) {
            btn.setNormalBackgroundColor(getResources().getColor(R.color.color_4AC4C4));
            btn.setNormalStrokeColor(getResources().getColor(R.color.color_4AC4C4));
            btn.setNormalTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            btn.setNormalBackgroundColor(getResources().getColor(R.color.color_090E17));
            btn.setNormalStrokeColor(getResources().getColor(R.color.color_555A63));
            btn.setNormalTextColor(getResources().getColor(R.color.color_555A63));
        }
    }

    private void handleSwitchValueChanged() {
        switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CacheUtil.getInstance(AlarmActivity.this).putBool("GetupAlarm", true);
                    alarmGetupModelNew.setOpen(true);
                    startClockStartGetup();
                } else {
                    CacheUtil.getInstance(AlarmActivity.this).putBool("GetupAlarm", false);
                    alarmGetupModelNew.setOpen(false);
                    stopClockGetup();
                }
            }
        });
        switchSleepAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CacheUtil.getInstance(AlarmActivity.this).putBool("SleepAlarm", true);
                    startClockSleep();
                } else {
                    CacheUtil.getInstance(AlarmActivity.this).putBool("SleepAlarm", false);
                    stopClockSleep();
                }
            }
        });
        boolean bSleep = CacheUtil.getInstance(AlarmActivity.this).getBool("SleepAlarm");
        switchSleepAlarm.setChecked(bSleep);
    }

    private void startClockStartGetup() {
        if (alarmGetupModelNew.getHour() == 0 && alarmGetupModelNew.getMinute() == 0) {
            return;
        }
        if (alarmGetupModelNew.isOpen() == false ) {
            return;
        }
        int hour1 = alarmGetupModelNew.getHour();
        int minute1 = alarmGetupModelNew.getMinute();
        boolean sleep = CacheUtil.getInstance(AlarmActivity.this).getBool("SleepAlarm");
        if (sleep) {
            int value = startClock();
            if (value == hour1 * 60 + minute1) {
                AlarmManagerUtil.setAlarm(this, 0, hour1, minute1, 2, 0, Math.max(0, currentPostion / 10 - 1));
            }
        } else {
            AlarmManagerUtil.setAlarm(this, 0, hour1, minute1, 2, 0, Math.max(0, currentPostion / 10 - 1));
        }
    }

    private void stopClockGetup() {
        boolean sleep = CacheUtil.getInstance(AlarmActivity.this).getBool("SleepAlarm");
        if (sleep) {
            AlarmManagerUtil.setAlarm(this, 0, 22, 30, 2, 0, 0);
        } else {
            clearAlarm();
        }
    }

    private void startClockSleep() {
        int sleep = 22 * 60 + 30;
        boolean getup = CacheUtil.getInstance(AlarmActivity.this).getBool("GetupAlarm");
        if (getup) {
            int value = startClock();
            if (value == sleep) {
                AlarmManagerUtil.setAlarm(this, 0, 22, 30, 2, 0, 0);
            }
        } else {
            AlarmManagerUtil.setAlarm(this, 0, 22, 30, 2, 0, 0);
        }
    }

    private void stopClockSleep() {
        boolean getup = CacheUtil.getInstance(AlarmActivity.this).getBool("GetupAlarm");
        if (getup) {
            int hour1 = alarmGetupModelNew.getHour();
            int minute1 = alarmGetupModelNew.getMinute();
            AlarmManagerUtil.setAlarm(this, 0, hour1, minute1, 2, 0, Math.max(0, currentPostion / 10 - 1));
        } else {
            clearAlarm();
        }
    }

    private int startClock() {
        int now = timeToHourMinute();
        int hour1 = alarmGetupModelNew.getHour();
        int minute1 = alarmGetupModelNew.getMinute();
        int getup = hour1 * 60 + minute1;
        int sleep = 22 * 60 + 30;
        if (getup >= now && sleep >= now) {
            return getup > sleep ? sleep : getup;
        } else if (getup < now && sleep >= now) {
            return sleep;
        } else if (getup < now && sleep < now) {
            return getup > sleep ? sleep : getup;
        } else if (getup >= now && sleep < now) {
            return getup;
        }
        return 0;
    }

    private int timeToHourMinute() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String str = simpleDateFormat.format(new Date());
        String[] array = str.split(":");
        return Integer.parseInt(array[0]) * 60 + Integer.parseInt(array[1]);
    }

    private void clearAlarm() {
        AlarmManagerUtil.cancelAlarm(this, 2);
    }
}
