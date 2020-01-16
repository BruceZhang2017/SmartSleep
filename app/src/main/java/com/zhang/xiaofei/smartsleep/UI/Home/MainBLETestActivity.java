package com.zhang.xiaofei.smartsleep.UI.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zhang.xiaofei.smartsleep.Kit.Application.ActivityObserver;
import com.zhang.xiaofei.smartsleep.Kit.Application.TestDataNotification;
import com.zhang.xiaofei.smartsleep.R;

import java.util.Date;

public class MainBLETestActivity extends AppCompatActivity implements ActivityObserver {

    private ImageButton ibLeft;
    TextView tvValue;
    Button btnRead;
    long currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bletest);
        tvValue = (TextView)findViewById(R.id.tv_temp_value);
        btnRead = (Button)findViewById(R.id.btn_temp_value);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTime = System.currentTimeMillis();
                Intent intentBroadcast = new Intent();   //定义Intent
                intentBroadcast.setAction("Filter");
                intentBroadcast.putExtra("arg0", 8);
                sendBroadcast(intentBroadcast);
            }
        });

        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        TestDataNotification.getInstance().addObserver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TestDataNotification.getInstance().deleteObserver(this);
    }

    @Override
    public void notifyData() {
        long time = System.currentTimeMillis();
        tvValue.setText("耗时：" + (time - currentTime) + "毫秒");
    }
}

