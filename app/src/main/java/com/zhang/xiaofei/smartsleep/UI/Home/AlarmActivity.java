package com.zhang.xiaofei.smartsleep.UI.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.deadline.statebutton.StateButton;
import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.UI.Report.SoundChooseActivity;

import java.util.ArrayList;

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
    @BindView(R.id.tv_value) TextView tvValue;
    @BindView(R.id.tv_value2) TextView tvValue2;

    private ArrayList<String> hours = new ArrayList<>();
    private ArrayList<String> minutes = new ArrayList<>();
    private Realm mRealm;
    private AlarmModel alramGetupModel;
    private AlarmModel alramSleepModel;
    private int userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);

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
                } else {
                    alramSleepModel = model;
                }
            }
        }
        if (alramGetupModel != null) {
            switchAlarm.setChecked(alramGetupModel.isOpen());
        }
        initialWheel();
        initialButtons();
        handleSwitchValueChanged();

        if (alramGetupModel == null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    //先查找后得到User对象
                    alramGetupModel = realm.createObject(AlarmModel.class);
                    alramGetupModel.setUserId(userId);
                    alramGetupModel.setType(0);
                }
            });
        }
        if (alramSleepModel == null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    //先查找后得到User对象
                    alramSleepModel = realm.createObject(AlarmModel.class);
                    alramSleepModel.setUserId(userId);
                    alramSleepModel.setType(1);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("初始化闹钟功能");
        clearAlarm();
        startClockStartGetup();
        startClockStartSleep();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();

        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction("com.example.petter.broadcast.MyDynamicFilter");
        intentBroadcast.putExtra("arg0", 2);
        sendBroadcast(intentBroadcast);

    }

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
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramGetupModel.setMinute(index);
                    }
                });

            }
        });
        if (alramGetupModel != null) {
            wvMinutes1.setCurrentItem(alramGetupModel.getMinute());
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
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramSleepModel.setMinute(index);
                    }
                });
            }
        });
        if (alramSleepModel != null) {
            wvMinutes2.setCurrentItem(alramSleepModel.getMinute());
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
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramGetupModel.setHour(index);
                    }
                });
            }
        });
        if (alramGetupModel != null) {
            wvHours1.setCurrentItem(alramGetupModel.getHour());
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
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramSleepModel.setHour(index);
                    }
                });
            }
        });
        if (alramSleepModel != null) {
            wvHours2.setCurrentItem(alramSleepModel.getHour());
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
        if (alramGetupModel != null) {
            btnMonday.setSelected(alramGetupModel.isMonday());
            btnTuesday.setSelected(alramGetupModel.isTuesday());
            btnWednesday.setSelected(alramGetupModel.isWednesday());
            btnThursday.setSelected(alramGetupModel.isThursday());
            btnFirday.setSelected(alramGetupModel.isFirday());
            btnSaturday.setSelected(alramGetupModel.isSaturday());
            btnSunday.setSelected(alramGetupModel.isSunday());
            changeColor(btnSunday.isSelected(), btnSunday);
            changeColor(btnMonday.isSelected(), btnMonday);
            changeColor(btnTuesday.isSelected(), btnTuesday);
            changeColor(btnWednesday.isSelected(), btnWednesday);
            changeColor(btnThursday.isSelected(), btnThursday);
            changeColor(btnFirday.isSelected(), btnFirday);
            changeColor(btnSaturday.isSelected(), btnSaturday);
        }
        if (alramSleepModel != null) {
            btnMonday2.setSelected(alramSleepModel.isMonday());
            btnTuesday2.setSelected(alramSleepModel.isTuesday());
            btnWednesday2.setSelected(alramSleepModel.isWednesday());
            btnThursday2.setSelected(alramSleepModel.isThursday());
            btnFirday2.setSelected(alramSleepModel.isFirday());
            btnSaturday2.setSelected(alramSleepModel.isSaturday());
            btnSunday2.setSelected(alramSleepModel.isSunday());
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
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramGetupModel.setSunday(sunday);
                    }
                });

                break;
            case R.id.btn_monday:
                btnMonday.setSelected(!btnMonday.isSelected());
                changeColor(btnMonday.isSelected(), btnMonday);
                boolean monday = btnMonday.isSelected();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramGetupModel.setMonday(monday);
                    }
                });

                break;
            case R.id.btn_tuesday:
                btnTuesday.setSelected(!btnTuesday.isSelected());
                changeColor(btnTuesday.isSelected(), btnTuesday);
                boolean tuesday = btnTuesday.isSelected();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramGetupModel.setTuesday(tuesday);
                    }
                });

                break;
            case R.id.btn_wednesday:
                btnWednesday.setSelected(!btnWednesday.isSelected());
                changeColor(btnWednesday.isSelected(), btnWednesday);
                boolean wednesday = btnWednesday.isSelected();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramGetupModel.setWednesday(wednesday);
                    }
                });

                break;
            case R.id.btn_thursday:
                btnThursday.setSelected(!btnThursday.isSelected());
                changeColor(btnThursday.isSelected(), btnThursday);
                boolean thursday = btnThursday.isSelected();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramGetupModel.setThursday(thursday);
                    }
                });

                break;
            case R.id.btn_friday:
                btnFirday.setSelected(!btnFirday.isSelected());
                changeColor(btnFirday.isSelected(), btnFirday);
                boolean firday = btnFirday.isSelected();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramGetupModel.setFirday(firday);
                    }
                });

                break;
            case R.id.btn_saturday:
                btnSaturday.setSelected(!btnSaturday.isSelected());
                changeColor(btnSaturday.isSelected(), btnSaturday);
                boolean saturday = btnSaturday.isSelected();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramGetupModel.setSaturday(saturday);
                    }
                });

                break;
            case R.id.btn_sunday2:
                btnSunday2.setSelected(!btnSunday2.isSelected());
                changeColor(btnSunday2.isSelected(), btnSunday2);
                boolean sunday2 = btnSunday2.isSelected();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramSleepModel.setSunday(sunday2);
                    }
                });

                break;
            case R.id.btn_monday2:
                btnMonday2.setSelected(!btnMonday2.isSelected());
                changeColor(btnMonday2.isSelected(), btnMonday2);
                boolean monday2 = btnMonday2.isSelected();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramSleepModel.setMonday(monday2);
                    }
                });

                break;
            case R.id.btn_tuesday2:
                btnTuesday2.setSelected(!btnTuesday2.isSelected());
                changeColor(btnTuesday2.isSelected(), btnTuesday2);
                boolean tuesday2 = btnTuesday2.isSelected();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramSleepModel.setTuesday(tuesday2);
                    }
                });

                break;
            case R.id.btn_wednesday2:
                btnWednesday2.setSelected(!btnWednesday2.isSelected());
                changeColor(btnWednesday2.isSelected(), btnWednesday2);
                boolean wednesday2 = btnWednesday2.isSelected();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramSleepModel.setWednesday(wednesday2);
                    }
                });

                break;
            case R.id.btn_thursday2:
                btnThursday2.setSelected(!btnThursday2.isSelected());
                changeColor(btnThursday2.isSelected(), btnThursday2);
                boolean thursday2 = btnThursday2.isSelected();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramSleepModel.setThursday(thursday2);
                    }
                });

                break;
            case R.id.btn_friday2:
                btnFirday2.setSelected(!btnFirday2.isSelected());
                changeColor(btnFirday2.isSelected(), btnFirday2);
                boolean firday2 = btnFirday2.isSelected();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramSleepModel.setFirday(firday2);
                    }
                });

                break;
            case R.id.btn_saturday2:
                btnSaturday2.setSelected(!btnSaturday2.isSelected());
                changeColor(btnSaturday2.isSelected(), btnSaturday2);
                boolean saturday2 = btnSaturday2.isSelected();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alramSleepModel.setSaturday(saturday2);
                    }
                });

                break;
            case R.id.tv_value:
                Intent intent = new Intent(AlarmActivity.this, SoundChooseActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_value2:
                Intent intentB = new Intent(AlarmActivity.this, SoundChooseActivity.class);
                startActivity(intentB);
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
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            alramGetupModel.setOpen(true);
                        }
                    });
                } else {
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            alramGetupModel.setOpen(false);
                        }
                    });
                }
            }
        });
    }

    private void startClockStartSleep() {
        if (alramSleepModel.getHour() == 0 && alramSleepModel.getMinute() == 0) {
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
        int hour = alramSleepModel.getHour();
        int munitue = alramSleepModel.getMinute();
        if (btnSaturday2.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour, munitue, 16, 6, "睡觉闹钟响了", 1);
        }
        if (btnMonday2.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour, munitue, 11, 1, "睡觉闹钟响了", 1);
        }
        if (btnTuesday2.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour, munitue, 12, 2, "睡觉闹钟响了", 1);
        }
        if (btnWednesday2.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour, munitue, 13, 3, "睡觉闹钟响了", 1);
        }
        if (btnThursday2.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour, munitue, 14, 4, "睡觉闹钟响了", 1);
        }
        if (btnFirday2.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour, munitue, 15, 5, "睡觉闹钟响了", 1);
        }
        if (btnSunday2.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour, munitue, 17, 7, "睡觉闹钟响了", 1);
        }
    }

    private void startClockStartGetup() {
        if (alramGetupModel.getHour() == 0 && alramGetupModel.getMinute() == 0) {
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
        int hour1 = alramGetupModel.getHour();
        int minute1 = alramGetupModel.getMinute();
        if (btnSaturday.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour1, minute1, 6, 6, "起床闹钟响了", 1);
        }
        if (btnMonday.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour1, minute1, 1, 1, "起床闹钟响了", 1);
        }
        if (btnTuesday.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour1, minute1, 2, 2, "起床闹钟响了", 1);
        }
        if (btnWednesday.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour1, minute1, 3, 3, "起床闹钟响了", 1);
        }
        if (btnThursday.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour1, minute1, 4, 4, "起床闹钟响了", 1);
        }
        if (btnFirday.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour1, minute1, 5, 5, "起床闹钟响了", 1);
        }
        if (btnSunday.isSelected()) {
            AlarmManagerUtil.setAlarm(this, 2, hour1, minute1, 7, 7, "起床闹钟响了", 1);
        }
    }

    private void clearAlarm() {
        for (int i = 1; i < 8; i++) {
            AlarmManagerUtil.cancelAlarm(this, i);
            AlarmManagerUtil.cancelAlarm(this, i + 10);
        }
    }
}
