package com.zhang.xiaofei.smartsleep.UI.Home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.ximalaya.ting.android.opensdk.test.MainFragmentActivity;
import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpSleepActivity extends BaseAppActivity implements View.OnClickListener {

    ImageButton ibBack;
    TextView tvTime;
    TextView tvTimeRange;
    Handler handler;
    MainFragmentActivity xmPlayer;
    ImageButton ibPalyPause;
    ImageButton ibPlayPre;
    ImageButton ibPlayNext;
    TextView tvSoundTitle;
    Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_sleep);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        ibBack.setOnClickListener(this);
        tvTime = (TextView)findViewById(R.id.tv_time);
        tvTime.setText(createTimeValue("13时20分"));
        tvTimeRange = (TextView)findViewById(R.id.tv_time_range);
        tvTimeRange.setText("07:40-08:00");

        initializeHandler();

        ibPalyPause = (ImageButton)findViewById(R.id.ib_play_pause);
        ibPalyPause.setOnClickListener(this);
        ibPlayPre = (ImageButton)findViewById(R.id.ib_pre);
        ibPlayPre.setOnClickListener(this);
        ibPlayNext = (ImageButton)findViewById(R.id.ib_next);
        ibPlayNext.setOnClickListener(this);
        tvSoundTitle = (TextView)findViewById(R.id.textView3);

        switch1 = (Switch)findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new XPopup.Builder(HelpSleepActivity.this)
//                        .enableDrag(false)
                            .asBottomList(getResources().getString(R.string.report_smart_timing),
                                    new String[]{"10" + getResources().getString(R.string.common_minute),
                                            "20" + getResources().getString(R.string.common_minute),
                                            "30" + getResources().getString(R.string.common_minute),
                                            "60" + getResources().getString(R.string.common_minute),
                                            "90" + getResources().getString(R.string.common_minute)},
                                    new OnSelectListener() {
                                        @Override
                                        public void onSelect(int position, String text) {

                                        }
                                    })
                            .show();
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.activity_close);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.ib_play_pause:
                if (xmPlayer.isPlaying()) {
                    xmPlayer.pause();
                } else {
                    xmPlayer.play();
                }
                break;
            case R.id.ib_pre:
                xmPlayer.playPre();
                break;
            case R.id.ib_next:
                xmPlayer.playNext();
                break;
            default:
                System.out.println("none");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        xmPlayer.onDestroy();
    }

    // 生成大小字体不一样的内容
    private SpannableString createTimeValue(String content) {
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new AbsoluteSizeSpan(DisplayUtil.sp2px(40, this))
                , content.indexOf("时")
                , content.indexOf("时") + 1
                , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(DisplayUtil.sp2px(40, this))
                , content.indexOf("分")
                , content.indexOf("分") + 1
                , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void initializeHandler() {
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 4:
                        int arg1 = msg.arg1;
                        if (arg1 == 0) {
                            ibPalyPause.setEnabled(false);
                            ibPalyPause.setImageResource(R.mipmap.sleep_icon_play);
                        } else if (arg1 == 2) {
                            ibPalyPause.setEnabled(true);
                            ibPalyPause.setImageResource(R.mipmap.sleep_icon_stop);
                        } else if (arg1 == 3 || arg1 == 1) {
                            ibPalyPause.setEnabled(true);
                            ibPalyPause.setImageResource(R.mipmap.sleep_icon_play);
                        }
                        break;
                    case 1: // 歌曲标题
                        String title = msg.obj.toString();
                        tvSoundTitle.setText(title);
                        break;
                    case 2: // 上一首
                        int arg2 = msg.arg1;
                        ibPlayPre.setEnabled(arg2 > 0);
                        break;
                    case 3:
                        int arg3 = msg.arg1;
                        ibPlayNext.setEnabled(arg3 > 0);
                        default:
                            System.out.println("none");
                            break;
                }
            }
        };

        xmPlayer = new MainFragmentActivity();
        xmPlayer.activity = this;
        xmPlayer.handler = handler;
        xmPlayer.onCreate();
    }


}
