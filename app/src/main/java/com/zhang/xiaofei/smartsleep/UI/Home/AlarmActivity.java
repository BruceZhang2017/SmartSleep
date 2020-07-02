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

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class AlarmActivity extends BaseAppActivity implements View.OnClickListener {

    @BindView(R.id.im_l) ImageButton ibLeft;
    @BindView(R.id.wv_minutes) WheelView wvMinutes1;
    @BindView(R.id.wv_minutes_2) WheelView wvMinutes2;
    @BindView(R.id.wv_hours) WheelView wvHours1;
    @BindView(R.id.wv_hours_2) WheelView wvHours2;
    @BindView(R.id.btn_sunday) StateButton btnSunday;
    @BindView(R.id.btn_monday) StateButton btnMonday;
    @BindView(R.id.btn_tuesday) StateButton btnTuesday;
    @BindView(R.id.btn_wednesday) StateButton btnWednesday;
    @BindView(R.id.btn_thursday) StateButton btnThursday;
    @BindView(R.id.btn_friday) StateButton btnFirday;
    @BindView(R.id.btn_saturday) StateButton btnSaturday;
    @BindView(R.id.btn_sunday2) StateButton btnSunday2;
    @BindView(R.id.btn_monday2) StateButton btnMonday2;
    @BindView(R.id.btn_tuesday2) StateButton btnTuesday2;
    @BindView(R.id.btn_wednesday2) StateButton btnWednesday2;
    @BindView(R.id.btn_thursday2) StateButton btnThursday2;
    @BindView(R.id.btn_friday2) StateButton btnFirday2;
    @BindView(R.id.btn_saturday2) StateButton btnSaturday2;
    @BindView(R.id.switch_alarm) Switch switchAlarm;
    @BindView(R.id.switch_sleep_alert) Switch switchSleepAlarm;
    @BindView(R.id.tv_value) TextView tvValue;
    @BindView(R.id.tv_value2) TextView tvValue2;

    private ArrayList<String> hours = new ArrayList<>();
    private ArrayList<String> minutes = new ArrayList<>();
    private Realm mRealm;
    private AlarmModel alramGetupModel;
    private AlarmModel alramSleepModel;
    private AlarmModel alarmGetupModelNew;
    private AlarmModel alarmSleepModelNew;
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
                } else {
                    alramSleepModel = model;
                    alarmSleepModelNew = new AlarmModel();
                    alarmSleepModelNew.setType(1);
                    alarmSleepModelNew.setHour(model.getHour());
                    alarmSleepModelNew.setMinute(model.getMinute());
                    alarmSleepModelNew.setMonday(model.isMonday());
                    alarmSleepModelNew.setThursday(model.isThursday());
                    alarmSleepModelNew.setWednesday(model.isWednesday());
                    alarmSleepModelNew.setThursday(model.isThursday());
                    alarmSleepModelNew.setFirday(model.isFirday());
                    alarmSleepModelNew.setSaturday(model.isSaturday());
                    alarmSleepModelNew.setSunday(model.isSunday());
                    alarmSleepModelNew.setOpen(model.isOpen());
                }
            }
        }
        if (alarmGetupModelNew != null) {
            switchAlarm.setChecked(alarmGetupModelNew.isOpen());
        }
        if (alarmSleepModelNew != null) {
            switchSleepAlarm.setChecked(alarmSleepModelNew.isOpen());
        }
        initialWheel();
        initialButtons();
        handleSwitchValueChanged();

        if (alarmGetupModelNew == null) {
            alarmGetupModelNew = new AlarmModel();
            alarmGetupModelNew.setType(0);
        }
        if (alarmSleepModelNew == null) {
            alarmSleepModelNew = new AlarmModel();
            alarmSleepModelNew.setType(1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("初始化闹钟功能");
        boolean bAlarmOpen = alarmSleepModelNew.isOpen() || alarmGetupModelNew.isOpen();
        if (bAlarmOpen) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (alramSleepModel == null) {
                        AlarmModel model = realm.createObject(AlarmModel.class);
                        model.setHour(alarmSleepModelNew.getHour());
                        model.setMinute(alarmSleepModelNew.getMinute());
                        model.setMonday(alarmSleepModelNew.isMonday());
                        model.setThursday(alarmSleepModelNew.isThursday());
                        model.setWednesday(alarmSleepModelNew.isWednesday());
                        model.setThursday(alarmSleepModelNew.isThursday());
                        model.setFirday(alarmSleepModelNew.isFirday());
                        model.setSaturday(alarmSleepModelNew.isSaturday());
                        model.setSunday(alarmSleepModelNew.isSunday());
                        model.setOpen(alarmSleepModelNew.isOpen());
                        model.setType(alarmSleepModelNew.getType());
                    } else {
                        alramSleepModel.setHour(alarmSleepModelNew.getHour());
                        alramSleepModel.setMinute(alarmSleepModelNew.getMinute());
                        alramSleepModel.setMonday(alarmSleepModelNew.isMonday());
                        alramSleepModel.setThursday(alarmSleepModelNew.isThursday());
                        alramSleepModel.setWednesday(alarmSleepModelNew.isWednesday());
                        alramSleepModel.setThursday(alarmSleepModelNew.isThursday());
                        alramSleepModel.setFirday(alarmSleepModelNew.isFirday());
                        alramSleepModel.setSaturday(alarmSleepModelNew.isSaturday());
                        alramSleepModel.setSunday(alarmSleepModelNew.isSunday());
                        alramSleepModel.setOpen(alarmSleepModelNew.isOpen());
                        alramSleepModel.setType(alarmSleepModelNew.getType());
                    }
                }
            });
        } else {
            RealmResults<AlarmModel> userList = mRealm.where(AlarmModel.class).equalTo("type", 1).findAll();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    userList.deleteAllFromRealm();
                }
            });
        }
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
            }
        });
        if (alarmGetupModelNew != null) {
            wvMinutes1.setCurrentItem(alarmGetupModelNew.getMinute());
        } else {
            wvMinutes1.setCurrentItem(0);
        }

        wvMinutes2.setTextSize(40);
        wvMinutes2.setLineSpacingMultiplier(2f);
        wvMinutes2.setDividerWidth(10);
        wvMinutes2.setItemsVisibleCount(3);
        wvMinutes2.setDividerColor(getResources().getColor(R.color.tranparencyColor));
        wvMinutes2.setTextColorCenter(getResources().getColor(R.color.colorWhite));
        wvMinutes2.setTextColorOut(getResources().getColor(R.color.color_555A63));
        wvMinutes2.setAdapter(new ArrayWheelAdapter(minutes));
        wvMinutes2.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                System.out.println("设置的分钟2：" + index);
                alarmSleepModelNew.setMinute(index);
            }
        });
        if (alarmSleepModelNew != null) {
            wvMinutes2.setCurrentItem(alarmSleepModelNew.getMinute());
        } else {
            wvMinutes2.setCurrentItem(0);
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
            }
        });
        if (alarmGetupModelNew != null) {
            wvHours1.setCurrentItem(alarmGetupModelNew.getHour());
        }

        wvHours2.setTextSize(40);
        wvHours2.setLineSpacingMultiplier(2f);
        wvHours2.setDividerWidth(10);
        wvHours2.setItemsVisibleCount(3);
        wvHours2.setDividerColor(getResources().getColor(R.color.tranparencyColor));
        wvHours2.setTextColorCenter(getResources().getColor(R.color.colorWhite));
        wvHours2.setTextColorOut(getResources().getColor(R.color.color_555A63));
        wvHours2.setAdapter(new ArrayWheelAdapter(hours));
        wvHours2.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                System.out.println("设置的小时2：" + index);
                alarmSleepModelNew.setHour(index);
            }
        });
        if (alarmSleepModelNew != null) {
            wvHours2.setCurrentItem(alarmSleepModelNew.getHour());
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
        btnSunday2.setOnClickListener(this);
        btnMonday2.setOnClickListener(this);
        btnTuesday2.setOnClickListener(this);
        btnWednesday2.setOnClickListener(this);
        btnThursday2.setOnClickListener(this);
        btnFirday2.setOnClickListener(this);
        btnSaturday2.setOnClickListener(this);
        tvValue.setOnClickListener(this);
        tvValue2.setOnClickListener(this);
        tvValue.setText(getResources().getString(R.string.sound) + Math.max(1, currentPostion / 10));
        tvValue2.setText(getResources().getString(R.string.sound) + Math.max(1, currentPostion % 10));
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
        if (alarmSleepModelNew != null) {
            btnMonday2.setSelected(alarmSleepModelNew.isMonday());
            btnTuesday2.setSelected(alarmSleepModelNew.isTuesday());
            btnWednesday2.setSelected(alarmSleepModelNew.isWednesday());
            btnThursday2.setSelected(alarmSleepModelNew.isThursday());
            btnFirday2.setSelected(alarmSleepModelNew.isFirday());
            btnSaturday2.setSelected(alarmSleepModelNew.isSaturday());
            btnSunday2.setSelected(alarmSleepModelNew.isSunday());
            changeColor(btnSunday2.isSelected(), btnSunday2);
            changeColor(btnMonday2.isSelected(), btnMonday2);
            changeColor(btnTuesday2.isSelected(), btnTuesday2);
            changeColor(btnWednesday2.isSelected(), btnWednesday2);
            changeColor(btnThursday2.isSelected(), btnThursday2);
            changeColor(btnFirday2.isSelected(), btnFirday2);
            changeColor(btnSaturday2.isSelected(), btnSaturday2);
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
            case R.id.btn_sunday2:
                btnSunday2.setSelected(!btnSunday2.isSelected());
                changeColor(btnSunday2.isSelected(), btnSunday2);
                boolean sunday2 = btnSunday2.isSelected();
                alarmSleepModelNew.setSunday(sunday2);
                break;
            case R.id.btn_monday2:
                btnMonday2.setSelected(!btnMonday2.isSelected());
                changeColor(btnMonday2.isSelected(), btnMonday2);
                boolean monday2 = btnMonday2.isSelected();
                alarmSleepModelNew.setMonday(monday2);
                break;
            case R.id.btn_tuesday2:
                btnTuesday2.setSelected(!btnTuesday2.isSelected());
                changeColor(btnTuesday2.isSelected(), btnTuesday2);
                boolean tuesday2 = btnTuesday2.isSelected();
                alarmSleepModelNew.setTuesday(tuesday2);
                break;
            case R.id.btn_wednesday2:
                btnWednesday2.setSelected(!btnWednesday2.isSelected());
                changeColor(btnWednesday2.isSelected(), btnWednesday2);
                boolean wednesday2 = btnWednesday2.isSelected();
                alarmSleepModelNew.setWednesday(wednesday2);
                break;
            case R.id.btn_thursday2:
                btnThursday2.setSelected(!btnThursday2.isSelected());
                changeColor(btnThursday2.isSelected(), btnThursday2);
                boolean thursday2 = btnThursday2.isSelected();
                alarmSleepModelNew.setThursday(thursday2);
                break;
            case R.id.btn_friday2:
                btnFirday2.setSelected(!btnFirday2.isSelected());
                changeColor(btnFirday2.isSelected(), btnFirday2);
                boolean firday2 = btnFirday2.isSelected();
                alarmSleepModelNew.setFirday(firday2);
                break;
            case R.id.btn_saturday2:
                btnSaturday2.setSelected(!btnSaturday2.isSelected());
                changeColor(btnSaturday2.isSelected(), btnSaturday2);
                boolean saturday2 = btnSaturday2.isSelected();
                alarmSleepModelNew.setSaturday(saturday2);
                break;
            case R.id.tv_value:
                Intent intent = new Intent(AlarmActivity.this, SoundChooseActivity.class);
                intent.putExtra("isSleep", false);
                startActivityForResult(intent, 1);
                break;
            case R.id.tv_value2:
                Intent intentB = new Intent(AlarmActivity.this, SoundChooseActivity.class);
                intentB.putExtra("isSleep", true);
                startActivityForResult(intentB, 2);
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
                    alarmGetupModelNew.setOpen(true);
                    startClockStartGetup();
                } else {
                    alarmGetupModelNew.setOpen(false);
                    clearAlarm(2);
                }
            }
        });
        switchSleepAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    alarmSleepModelNew.setOpen(true);
                    startClockStartSleep();
                } else {
                    alarmSleepModelNew.setOpen(false);
                    clearAlarm(1);
                }
            }
        });
    }

    private void startClockStartSleep() {
        if (alarmSleepModelNew.getHour() == 0 && alarmSleepModelNew.getMinute() == 0) {
            return;
        }
        if (btnSunday2.isSelected() == false &&
            btnMonday2.isSelected() == false &&
            btnTuesday2.isSelected() == false &&
            btnWednesday2.isSelected() == false &&
            btnThursday2.isSelected() == false &&
            btnFirday2.isSelected() == false &&
            btnSaturday2.isSelected() == false) {
            return;
        }
//        if (alramSleepModel != null && alramSleepModel.isEqual(alarmSleepModelNew) ) {
//            return;
//        }
        if (alarmSleepModelNew.isOpen() == false ) {
            return;
        }
        System.out.println("有闹钟被设置");
        int hour = alarmSleepModelNew.getHour();
        int minute = alarmSleepModelNew.getMinute();
        ArrayList<Integer> testDays = new ArrayList<>();
        if (btnSaturday2.isSelected()) {
            testDays.add(Calendar.SATURDAY);
        }
        if (btnMonday2.isSelected()) {
            testDays.add(Calendar.MONDAY);
        }
        if (btnTuesday2.isSelected()) {
            testDays.add(Calendar.TUESDAY);
        }
        if (btnWednesday2.isSelected()) {
            testDays.add(Calendar.WEDNESDAY);
        }
        if (btnThursday2.isSelected()) {
            testDays.add(Calendar.THURSDAY);
        }
        if (btnFirday2.isSelected()) {
            testDays.add(Calendar.FRIDAY);
        }
        if (btnSunday2.isSelected()) {
            testDays.add(Calendar.SUNDAY);
        }

        AlarmManagerUtil.setAlarm(this, 0, hour, minute, 1, 0, Math.max(0, currentPostion % 10 - 1));
    }

    private void startClockStartGetup() {
        if (alarmGetupModelNew.getHour() == 0 && alarmGetupModelNew.getMinute() == 0) {
            return;
        }
        if (btnSunday.isSelected() == false &&
                btnMonday.isSelected() == false &&
                btnTuesday.isSelected() == false &&
                btnWednesday.isSelected() == false &&
                btnThursday.isSelected() == false &&
                btnFirday.isSelected() == false &&
                btnSaturday.isSelected() == false) {
            return;
        }
//        if (alramGetupModel != null && alramGetupModel.isEqual(alarmGetupModelNew) ) {
//            return;
//        }
        if (alarmGetupModelNew.isOpen() == false ) {
            return;
        }
        if (alarmGetupModelNew.isEqual(alarmSleepModelNew)) {
            return;
        }
        System.out.println("有闹钟被设置");
        int hour1 = alarmGetupModelNew.getHour();
        int minute1 = alarmGetupModelNew.getMinute();
        ArrayList<Integer> testDays = new ArrayList<>();
        if (btnSaturday.isSelected()) {
            testDays.add(Calendar.SATURDAY);
        }
        if (btnMonday.isSelected()) {
            testDays.add(Calendar.MONDAY);
        }
        if (btnTuesday.isSelected()) {
            testDays.add(Calendar.TUESDAY);
        }
        if (btnWednesday.isSelected()) {
            testDays.add(Calendar.WEDNESDAY);
        }
        if (btnThursday.isSelected()) {
            testDays.add(Calendar.THURSDAY);
        }
        if (btnFirday.isSelected()) {
            testDays.add(Calendar.FRIDAY);
        }
        if (btnSunday.isSelected()) {
            testDays.add(Calendar.SUNDAY);
        }
        AlarmManagerUtil.setAlarm(this, 0, hour1, minute1, 2, 0, Math.max(0, currentPostion / 10 - 1));
    }

    private void clearAlarm(int id) {
        AlarmManagerUtil.cancelAlarm(this, id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            tvValue.setText(getResources().getString(R.string.sound) + resultCode);
        } else {
            tvValue2.setText(getResources().getString(R.string.sound) + resultCode);
        }
    }
}
