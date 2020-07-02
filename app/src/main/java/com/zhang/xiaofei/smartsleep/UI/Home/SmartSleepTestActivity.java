package com.zhang.xiaofei.smartsleep.UI.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.clj.blesample.comm.DataOberverManager;
import com.clj.blesample.comm.DataObservable;
import com.clj.blesample.comm.DataObserver;
import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SmartSleepTestActivity extends BaseAppActivity implements DataObserver {

    //TextView tvContent;
    Button btnFlash;
    private ImageButton ibLeft;
    private TextView tvTitle;
    private DynamicView dynamicViewHeart;
    private DynamicView dynamicViewBreath;
    private int count = 0;
    private boolean bStart = false;
    private TextView tvHeart;
    private TextView tvBreath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_sleep_test);
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText(R.string.dynamic_curve);
        tvHeart = (TextView)findViewById(R.id.tv_sleep_time_3);
        tvBreath = (TextView)findViewById(R.id.tv_sleep_time_4);
        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //tvContent = (TextView)findViewById(R.id.tv_content);
        //tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        DataOberverManager.getInstance().addObserver(this);
        btnFlash = (Button)findViewById(R.id.btn_flash);
        btnFlash.setVisibility(View.INVISIBLE);
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        count = DisplayUtil.screenWidth(this);
        dynamicViewHeart = (DynamicView)findViewById(R.id.dv_heart);
        dynamicViewHeart.screenWidth = count;
        dynamicViewHeart.initArray(count);
        dynamicViewHeart.rate = 4096.0f / DisplayUtil.dip2px(200, this);
        dynamicViewHeart.height = DisplayUtil.dip2px(200, this);
        dynamicViewHeart.invalidate();
        dynamicViewBreath = (DynamicView)findViewById(R.id.dv_breath);
        dynamicViewBreath.screenWidth = count;
        dynamicViewBreath.initArray(count);
        dynamicViewBreath.rate = 4096.0f / DisplayUtil.dip2px(200, this);
        dynamicViewBreath.height = DisplayUtil.dip2px(200, this);
        dynamicViewBreath.invalidate();

        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction("Filter");
        intentBroadcast.putExtra("arg0", 11);
        intentBroadcast.putExtra("value", true);
        sendBroadcast(intentBroadcast);
    }

    public void addText(TextView textView, String content) {
        textView.append(content);
        textView.append("\n");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("SmartSleepTestAcitivty onStop");
        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction("Filter");
        intentBroadcast.putExtra("arg0", 12);
        intentBroadcast.putExtra("value", true);
        sendBroadcast(intentBroadcast);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dynamicViewHeart.clearAnimator();
        dynamicViewBreath.clearAnimator();
        DataOberverManager.getInstance().deleteObserver(this);
    }

    @Override
    public void notifyData(int[] heart, int[] breath) { // 每1秒钟回调一组数据
        System.out.println("有心跳和呼吸数据发送过来：" + heart.length + "-----" + breath.length);
        if (heart.length != 25) {
            return;
        }
        if (breath.length != 5) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dynamicViewHeart.current == 0) {
                    dynamicViewHeart.values = new int[250];
                    dynamicViewBreath.values = new int[50];
                }

                if (dynamicViewHeart.current >= 10) {
                    return;
                }

                for (int i = 0; i < heart.length; i++) {
                    dynamicViewHeart.values[i + dynamicViewHeart.current * 25] = heart[i];
                }
                for (int i = 0; i < breath.length; i++) {
                    dynamicViewBreath.values[i + dynamicViewBreath.current * 5] = breath[i];
                }
                dynamicViewHeart.current += 1;
                dynamicViewBreath.current += 1;
                dynamicViewHeart.startAnimator();
                dynamicViewBreath.startAnimator();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {

            }
        }
    };

}
