package com.zhang.xiaofei.smartsleep.UI.Me;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhang.xiaofei.smartsleep.Kit.DB.CacheUtil;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceManager;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceModel;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Home.SleepAndGetupTimeManager;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.UI.Login.LoginActivity;
import com.zhang.xiaofei.smartsleep.Vendor.EsptouchDemoActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SettingsActivity extends BaseAppActivity {

    private TextView tvTitle;
    private ImageButton ibLeft;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); //
        ListView listView = findViewById(R.id.lv_settings);
        List<String> appNames = new ArrayList<>();
        appNames.add(getResources().getString(R.string.mine_wifi_configuration));
        appNames.add(getResources().getString(R.string.mine_clear_cache));
        appNames.add(getResources().getString(R.string.mine_language_choose));
        appNames.add(getResources().getString(R.string.mine_about_us));
        //适配adapter
        listView.setAdapter(new AppListAdapter(appNames));

        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText(R.string.mine_settings);
        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnLogout = (Button)findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "退出登录", Toast.LENGTH_SHORT).show();
                Intent intentBroadcast = new Intent();   //定义Intent
                intentBroadcast.setAction("Filter");
                intentBroadcast.putExtra("arg0", 16);
                intentBroadcast.putExtra("value", true);
                sendBroadcast(intentBroadcast);
                YMUserInfoManager userInfoManager = new YMUserInfoManager(SettingsActivity.this);
                userInfoManager.clearUserInfo();
                SleepAndGetupTimeManager.clearHashMapData(); // 清空时间记录
                DeviceManager.getInstance().scaningCurrentDevice = 0;
                DeviceManager.getInstance().connectedCurrentDevice = 0;
                YMApplication.getInstance().deviceBatteryMap.clear();
                RealmResults<DeviceModel> devices = Realm.getDefaultInstance().where(DeviceModel.class).findAll();
                if (devices != null && devices.size() > 0) {
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            devices.deleteAllFromRealm();
                        }
                    });
                }
                RealmResults<RecordModel> records = Realm.getDefaultInstance().where(RecordModel.class).findAll();
                if (records != null && records.size() > 0) {
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            records.deleteAllFromRealm();
                        }
                    });
                }
                CacheUtil.getInstance(SettingsActivity.this).putBool("SyncData", false);
                YMApplication.getInstance().setSleepbeltValue(new int[5]);
                SleepAndGetupTimeManager.times.clear();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
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
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        Intent intent = new Intent(SettingsActivity.this, WifiDeviceTypeActivity.class);
                        startActivity(intent);
                    } else if (position == 1) {
                        Intent intent = new Intent(SettingsActivity.this, ClearCacheActivity.class);
                        startActivity(intent);
                    } else if (position == 3) { //
                        Intent intent = new Intent(SettingsActivity.this, AboutUsActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SettingsActivity.this, LanguageSystemActivity.class);
                        startActivity(intent);
                    }
                }
            });
            return convertView;
        }
    }
}
