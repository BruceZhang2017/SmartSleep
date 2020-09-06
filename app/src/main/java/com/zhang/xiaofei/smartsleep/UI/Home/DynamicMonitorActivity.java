package com.zhang.xiaofei.smartsleep.UI.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.zhang.xiaofei.smartsleep.Kit.DB.CacheUtil;
import com.zhang.xiaofei.smartsleep.R;

import java.util.ArrayList;

import butterknife.BindView;

public class DynamicMonitorActivity extends AppCompatActivity {

    private ImageButton ibLeft;
    WheelView wvMinutes1;
    WheelView wvHours1;
    TextView tvSleep;
    TextView tvGetup;
    TextView tvMaohao;
    Button btnSave;
    Switch sSwitch;
    private ArrayList<String> hours = new ArrayList<>();
    private ArrayList<String> minutes = new ArrayList<>();
    int flag = 0;
    int hour1 = 0;
    int minute1 = 0;
    int hour2 = 0;
    int minute2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_monitor);
        wvMinutes1 = (WheelView)findViewById(R.id.wv_minutes);
        wvHours1 = (WheelView)findViewById(R.id.wv_hours);
        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        sSwitch = (Switch)findViewById(R.id.switch_alarm);
        tvMaohao = (TextView)findViewById(R.id.tv_maohao);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initialWheel();
        wvMinutes1.setVisibility(View.INVISIBLE);
        wvHours1.setVisibility(View.INVISIBLE);
        tvMaohao.setVisibility(View.INVISIBLE);
        tvSleep = (TextView)findViewById(R.id.tv_sleep_value);
        tvSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                wvMinutes1.setVisibility(View.VISIBLE);
                wvHours1.setVisibility(View.VISIBLE);
                tvMaohao.setVisibility(View.VISIBLE);
                wvHours1.setCurrentItem(hour1);
                wvMinutes1.setCurrentItem(minute1);
            }
        });
        tvGetup = (TextView)findViewById(R.id.tv_getup_value);
        tvGetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                wvMinutes1.setVisibility(View.VISIBLE);
                wvHours1.setVisibility(View.VISIBLE);
                tvMaohao.setVisibility(View.VISIBLE);
                wvHours1.setCurrentItem(hour2);
                wvMinutes1.setCurrentItem(minute2);
            }
        });
        btnSave = (Button)findViewById(R.id.btn_login);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheUtil.getInstance(DynamicMonitorActivity.this).putBool("DynamicMonitorSwitch", sSwitch.isChecked());
                CacheUtil.getInstance(DynamicMonitorActivity.this).putString("DynamicMonitor", hour1 + ":" + minute1 + " " + hour2 + ":" + minute2);
                Intent intentBroadcast = new Intent();   //定义Intent
                intentBroadcast.setAction("Filter");
                intentBroadcast.putExtra("arg0", 20);
                intentBroadcast.putExtra("arg1", sSwitch.isChecked());
                intentBroadcast.putExtra("arg2", hour1 + ":" + minute1 + " " + hour2 + ":" + minute2);
                sendBroadcast(intentBroadcast);

                finish();
            }
        });

        Boolean b = CacheUtil.getInstance(DynamicMonitorActivity.this).getBool("DynamicMonitorSwitch");
        String s = CacheUtil.getInstance(DynamicMonitorActivity.this).getString("DynamicMonitor");
        sSwitch.setChecked(b);
        if (s != null && s.length() > 0) {
            String[] array = s.split(" ");
            String[] array1 = array[0].split(":");
            String[] array2 = array[1].split(":");
            hour1 = Integer.parseInt(array1[0]);
            minute1 = Integer.parseInt(array1[1]);
            hour2 = Integer.parseInt(array2[0]);
            minute2 = Integer.parseInt(array2[1]);
            tvSleep.setText(hour1 + ":" + minute1);
            tvGetup.setText(hour2 + ":" + minute2);
        }
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
                if (flag == 0) {
                    minute1 = index;
                    tvSleep.setText(hour1 + ":" + minute1);
                } else {
                    minute2 = index;
                    tvGetup.setText(hour2 + ":" + minute2);
                }
            }
        });

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
                if (flag == 0) {
                    hour1 = index;
                    tvSleep.setText(hour1 + ":" + minute1);
                } else {
                    hour2 = index;
                    tvGetup.setText(hour2 + ":" + minute2);
                }
            }
        });
    }
}