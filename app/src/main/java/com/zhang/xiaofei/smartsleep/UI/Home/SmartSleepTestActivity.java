package com.zhang.xiaofei.smartsleep.UI.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    int[] heart;
    int[] breath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_sleep_test);
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText(R.string.dynamic_curve);
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
        dynamicViewHeart.initArray(count);
        dynamicViewHeart.rate = 4096.0f / 100;
        dynamicViewHeart.invalidate();
        dynamicViewBreath = (DynamicView)findViewById(R.id.dv_breath);
        dynamicViewBreath.initArray(count);
        dynamicViewBreath.rate = 255.0f / 100;
        dynamicViewBreath.invalidate();

        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction("com.example.petter.broadcast.MyDynamicFilter");
        intentBroadcast.putExtra("arg0", 5);
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
        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction("com.example.petter.broadcast.MyDynamicFilter");
        intentBroadcast.putExtra("arg0", 5);
        intentBroadcast.putExtra("value", false);
        sendBroadcast(intentBroadcast);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataOberverManager.getInstance().deleteObserver(this);
    }

    @Override
    public void notifyData(int[] heart, int[] breath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //addText(tvContent, content);
                if (dynamicViewHeart.current + 50 >= count) {
                    dynamicViewHeart.values = new int[count];
                    dynamicViewHeart.current = 0;
                    return;
                }
                for (int i = 0; i < 50; i++) {
                    dynamicViewHeart.values[dynamicViewHeart.current + i] = heart[i];
                }

                if (dynamicViewBreath.current + 50 >= count) {
                    dynamicViewBreath.values = new int[count];
                    dynamicViewBreath.current = 0;
                    return;
                }
                for (int i = 0; i < 50; i++) {
                    dynamicViewBreath.values[dynamicViewBreath.current + i] = breath[i];
                }

                dynamicViewHeart.current += 50;
                dynamicViewHeart.invalidate();

                dynamicViewBreath.current += 50;
                dynamicViewBreath.invalidate();
            }
        });
    }

    // 定时器
    private Timer mTimer = new Timer();
    // 定时任务
    private TimerTask mTask = new TimerTask() {
        @Override
        public void run() {
            // 要做的事情

        }
    };

}
