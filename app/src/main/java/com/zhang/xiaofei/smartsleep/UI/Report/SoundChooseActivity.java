package com.zhang.xiaofei.smartsleep.UI.Report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loonggg.lib.alarmmanager.clock.MediaPlayerUtil;
import com.zhang.xiaofei.smartsleep.Kit.DB.CacheUtil;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;

import java.util.ArrayList;
import java.util.List;

public class SoundChooseActivity extends BaseAppActivity {

    private TextView tvTitle;
    private ImageButton ibLeft;
    private int currentPostion = 0;
    private boolean isSleep = false;
    private AppListAdapter adapter;
    private TextView tvCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_choose);
        isSleep = getIntent().getBooleanExtra("isSleep", false);
        currentPostion = CacheUtil.getInstance(this).getInt("alarmSound");
        ListView listView = findViewById(R.id.lv_settings);
        List<String> appNames = new ArrayList<>();
        appNames.add(getResources().getString(R.string.sound1));
        appNames.add(getResources().getString(R.string.sound2));
        appNames.add(getResources().getString(R.string.sound3));
        appNames.add(getResources().getString(R.string.sound4));
        //适配adapter
        adapter = new AppListAdapter(appNames);
        listView.setAdapter(adapter);

        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText("选择提示音");
        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvCurrent = (TextView)findViewById(R.id.tv_currnet_language_value);
        int position = Math.max(0,currentPostion / 10 - 1);
        tvCurrent.setText(appNames.get(position));
    }

    @Override
    protected void onStop() {
        super.onStop();
        MediaPlayerUtil.getInstance().stopRing();
    }

    private class AppListAdapter extends BaseAdapter {
        //要填充的数据列表
        List<String> mAppNames;
        public AppListAdapter(List<String> appNames){
            this.mAppNames = appNames;
        }
        @Override
        public int getCount() {
            //返回数据总数
            return mAppNames.size();
        }
        @Override
        public Object getItem(int position) {
            //返回当前position位置的item
            return mAppNames.get(position);
        }
        @Override
        public long getItemId(int position) {
            //返回当前position位置的item的id
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //处理view与data，进行数据填充
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_language_item, parent,false);
            TextView textView = (TextView)convertView.findViewById(R.id.tv_title);
            textView.setText(mAppNames.get(position));
            ImageView ivSelected = (ImageView)convertView.findViewById(R.id.iv_select);
            ImageView ivLine = (ImageView)convertView.findViewById(R.id.iv_line);
            if (position == (isSleep ? Math.max(0,currentPostion % 10 - 1) : Math.max(0,currentPostion / 10 - 1))) {
                ivSelected.setVisibility(View.VISIBLE);
            } else {
                ivSelected.setVisibility(View.INVISIBLE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaPlayerUtil.getInstance().startRing(getApplicationContext(), position);
                    if (isSleep) {
                        currentPostion = (currentPostion / 10) * 10 + (position + 1);
                    } else {
                        currentPostion = (currentPostion % 10) + (position + 1) * 10;
                    }
                    CacheUtil.getInstance(SoundChooseActivity.this).setInt("alarmSound", currentPostion);
                    adapter.notifyDataSetChanged();
                    tvCurrent.setText(mAppNames.get(position));
                }
            });
            return convertView;
        }
    }
}
