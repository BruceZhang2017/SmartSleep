package com.zhang.xiaofei.smartsleep.UI.Report;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.LanguageSystemActivity;

import java.util.ArrayList;
import java.util.List;

public class SoundChooseActivity extends BaseAppActivity {

    private TextView tvTitle;
    private ImageButton ibLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_choose);
        ListView listView = findViewById(R.id.lv_settings);
        List<String> appNames = new ArrayList<>();
        appNames.add("铃声1");
        appNames.add("铃声2");
        appNames.add("铃声3");
        //适配adapter
        listView.setAdapter(new AppListAdapter(appNames));

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
            if (position == 0) {
                ivSelected.setVisibility(View.VISIBLE);
            } else {
                ivSelected.setVisibility(View.INVISIBLE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {

                    } else if (position == 1) {

                    }
                }
            });
            return convertView;
        }
    }
}
