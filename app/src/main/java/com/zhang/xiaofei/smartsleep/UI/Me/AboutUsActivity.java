package com.zhang.xiaofei.smartsleep.UI.Me;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhang.xiaofei.smartsleep.Kit.Webview.WebActivity;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.Vendor.EsptouchDemoActivity;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AboutUsActivity extends BaseAppActivity {

    private ImageButton ibLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us); //
        ListView listView = findViewById(R.id.lv_settings);
        List<String> appNames = new ArrayList<>();
        appNames.add(getResources().getString(R.string.mine_checking_update));
        appNames.add(getResources().getString(R.string.login_term_for_usage));
        //适配adapter
        listView.setAdapter(new AboutUsActivity.AppListAdapter(appNames));

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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public class AppListAdapter extends BaseAdapter {
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
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_settings_item, parent,false);
            TextView textView = convertView.findViewById(R.id.tv_title);
            textView.setText(mAppNames.get(position));
            TextView tvValue = convertView.findViewById(R.id.tv_value);
            if (position == 0) {
                tvValue.setText("V1.0.19");
            } else {
                tvValue.setText("");
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        Toast.makeText(AboutUsActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
                    } else if (position == 1) {
                        Intent intent = new Intent(AboutUsActivity.this, WebActivity.class);
                        intent.putExtra("url", "https://www.baidu.com");
                        AboutUsActivity.this.startActivity(intent);
                    }
                }
            });
            return convertView;
        }
    }
}
