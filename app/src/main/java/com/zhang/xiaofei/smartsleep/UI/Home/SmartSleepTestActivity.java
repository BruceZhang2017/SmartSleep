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
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;

public class SmartSleepTestActivity extends BaseAppActivity implements DataObserver {

    //TextView tvContent;
    Button btnFlash;
    private ImageButton ibLeft;
    private TextView tvTitle;
    private DynamicView dynamicViewHeart;
    private DynamicView dynamicViewBreath;

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
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBroadcast = new Intent();   //定义Intent
                intentBroadcast.setAction("com.example.petter.broadcast.MyDynamicFilter");
                intentBroadcast.putExtra("arg0", 5);
                sendBroadcast(intentBroadcast);
            }
        });

        dynamicViewHeart = (DynamicView)findViewById(R.id.dv_heart);
        dynamicViewHeart.rate = 1.5;
        dynamicViewHeart.invalidate();
        dynamicViewBreath = (DynamicView)findViewById(R.id.dv_breath);
        dynamicViewBreath.rate = 0.4;
        dynamicViewBreath.invalidate();
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
    protected void onDestroy() {
        super.onDestroy();
        DataOberverManager.getInstance().deleteObserver(this);
    }

    @Override
    public void notifyData(int heart, int breath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //addText(tvContent, content);
                if (dynamicViewHeart.current >= 1024) {
                    dynamicViewHeart.values = new int[1024];
                    dynamicViewHeart.current = 0;
                }
                dynamicViewHeart.values[dynamicViewHeart.current] = heart;
                dynamicViewHeart.current += 1;
                dynamicViewHeart.invalidate();

                if (dynamicViewBreath.current >= 1024) {
                    dynamicViewBreath.values = new int[1024];
                    dynamicViewBreath.current = 0;
                }
                dynamicViewBreath.values[dynamicViewHeart.current] = breath;
                dynamicViewBreath.current += 1;
                dynamicViewBreath.invalidate();
            }
        });
    }
}
