package com.zhang.xiaofei.smartsleep.UI.Me;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhang.xiaofei.smartsleep.Kit.Language.LanguageType;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.Vendor.EsptouchDemoActivity;

import java.util.ArrayList;
import java.util.List;

public class WifiDeviceTypeActivity extends BaseAppActivity {

    List<String> appNames;
    private AppListAdapter adapter;
    private TextView tvTitle;
    private ImageButton ibLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_device_type);
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText(R.string.wifi_device_type);

        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ListView listView = findViewById(R.id.lv_settings);
        appNames = new ArrayList<>();
        appNames.add(getResources().getString(R.string.wifi_device_type_1));
        appNames.add(getResources().getString(R.string.wifi_device_type_2));
        //适配adapter
        adapter = new AppListAdapter(appNames);
        listView.setAdapter(adapter);
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
            ivSelected.setVisibility(View.INVISIBLE);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WifiDeviceTypeActivity.this, EsptouchDemoActivity.class);
                    intent.putExtra("type", position);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
}