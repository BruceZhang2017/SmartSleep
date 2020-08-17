package com.zhang.xiaofei.smartsleep.UI.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhang.xiaofei.smartsleep.Kit.Application.ScreenInfoUtils;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ApneaDialogActivity extends Activity {

    TextView tvTime;
    TextView tvHeartRate;
    TextView tvApnea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apnea_dialog);
        tvTime = (TextView)findViewById(R.id.tv_time);
        tvHeartRate = (TextView)findViewById(R.id.tv_value);
        tvApnea = (TextView)findViewById(R.id.tv_value_2);

        int time = getIntent().getIntExtra("time", 0);
        int stopTime = getIntent().getIntExtra("stopTime", 0);
        int heart = getIntent().getIntExtra("heart", 0);
        tvTime.setText(timeToDate(time));
        tvHeartRate.setText("" + heart);
        tvApnea.setText("" + stopTime);


        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) ScreenInfoUtils.getScreenWidth(YMApplication.getContext());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);

        setFinishOnTouchOutside(true);
    }

    private String timeToDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String str = simpleDateFormat.format(new Date(time * 1000));
        return str;
    }

}