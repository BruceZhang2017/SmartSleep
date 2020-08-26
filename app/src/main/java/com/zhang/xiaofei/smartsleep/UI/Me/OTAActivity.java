package com.zhang.xiaofei.smartsleep.UI.Me;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.NameValuePair;
import com.ansen.http.net.RequestDataCallback;
import com.zhang.xiaofei.smartsleep.Kit.AlarmTimer;
import com.zhang.xiaofei.smartsleep.Kit.DB.CacheUtil;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Kit.Language.SpUtil;
import com.zhang.xiaofei.smartsleep.Kit.dfutest.DfuUpdateActivity;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceManager;
import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Home.HomeActivity;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.UI.Login.LoginActivity;
import com.zhang.xiaofei.smartsleep.Vendor.EsptouchDemoActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class OTAActivity extends BaseAppActivity {

    private TextView tvTitle;
    private ImageButton ibLeft;
    private TextView tvTip;
    String deviceSerial;
    String deviceVersion;
    String mac;
    String url;
    DynamicReceiver dynamicReceiver;
    private String newVersion;
    AppListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota);
        Intent intent = getIntent();
        String deviceName = intent.getStringExtra("name");
        deviceSerial = intent.getStringExtra("serial");
        deviceVersion = intent.getStringExtra("version");
        mac = intent.getStringExtra("mac");
        ListView listView = findViewById(R.id.lv_settings);
        List<OTAInfo> appNames = new ArrayList<>();
        appNames.add(new OTAInfo(getResources().getString(R.string.device_name), deviceName));
        appNames.add(new OTAInfo(getResources().getString(R.string.index_serial_no), deviceSerial));
        Integer battery = YMApplication.getInstance().deviceBatteryMap.get(mac);
        if (battery == null){
            battery = 0;
        }
        appNames.add(new OTAInfo(getResources().getString(R.string.device_info_battery), battery + "%"));
        if (mac == null || mac.equals("00:00:00:00:00:00")) {
            appNames.add(new OTAInfo(getResources().getString(R.string.index_version), "V0.1"));
        } else {
            appNames.add(new OTAInfo(getResources().getString(R.string.index_version), "V" + deviceVersion));
        }

        //适配adapter
        adapter = new AppListAdapter(appNames);
        listView.setAdapter(adapter);
        tvTip = (TextView)findViewById(R.id.tv_tip);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(R.string.device_information);
        ibLeft = (ImageButton) findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (!mac.equals("00:00:00:00:00:00")) {
            downloadOTA();
        }
        registerBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    public class AppListAdapter extends BaseAdapter {
        //要填充的数据列表
        List<OTAInfo> mAppNames;

        public AppListAdapter(List<OTAInfo> appNames) {
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
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_settings_item, parent, false);
            TextView textView = convertView.findViewById(R.id.tv_title);
            textView.setText(mAppNames.get(position).key);
            TextView tvValue = convertView.findViewById(R.id.tv_value);
            tvValue.setText(mAppNames.get(position).value);
            if (position == 3 && newVersion != null && newVersion.length() > 0) {
                tvValue.setText(newVersion);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 3) { // 调整至OTA
                        if (!DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getMac().equals(mac)) {
                            Toast.makeText(OTAActivity.this, R.string.please_connect_first, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (url == null || url.length() == 0) {
                            return;
                        }
                        Intent intent = new Intent(OTAActivity.this, DfuUpdateActivity.class);
                        intent.putExtra("mac", mac);
                        intent.putExtra("name", "Sleep_Baby");
                        if (url != null && url.length() > 0) {
                            intent.putExtra("url", url);
                        }
                        startActivity(intent);
                    }

                }
            });
            return convertView;
        }
    }

    public class OTAInfo {
        public String key;
        public String value;

        public OTAInfo(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private void downloadOTA() {
        YMUserInfoManager userManager = new YMUserInfoManager( OTAActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
        System.out.println("token:" + userModel.getToken());
        List<NameValuePair> postParam = new ArrayList<>();
        postParam.add(new NameValuePair("type",1 + ""));
        postParam.add(new NameValuePair("version",deviceVersion));

        HTTPCaller.getInstance().postFile(
                DeviceVersion.class,
                YMApplication.getInstance().domain() + "app/deviceManage/compareVersion",
                new com.ansen.http.net.Header[]{headerToken},
                postParam,
                requestDataCallback
        );
    }

    private RequestDataCallback requestDataCallback = new RequestDataCallback<DeviceVersion>() {
        @Override
        public void dataCallback(int status, DeviceVersion user) {
            System.out.println("网络请求返回的Status:" + status + " " + (Looper.myLooper() == Looper.getMainLooper()));
            if(user != null && (user.getCode() == 200 || user.getCode() == 201)){
                if (user.getCode() == 200) {
                    tvTip.setText(user.getData());
                } else {
                    String language = SpUtil.getInstance(YMApplication.getContext()).getString(SpUtil.LANGUAGE);
                    if (language.equals("en")) {
                        tvTip.setText(user.getEnmsg());
                    } else {
                        tvTip.setText(user.getMsg());
                    }
                }
                newVersion = user.getVersion();
                url = user.getUrl();
            }

        }

        @Override
        public void dataCallback(DeviceVersion obj) {

            if (obj == null) {
                Toast.makeText(OTAActivity.this, getResources().getText(R.string.common_check_network), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private class DeviceVersion extends BaseProtocol {
        String url;
        String version;
        String data;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }


    private void registerBroadcast() {
        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction("Filter100");    //添加动态广播的Action
        dynamicReceiver = new DynamicReceiver();
        registerReceiver(dynamicReceiver, dynamic_filter);    //注册自定义动态广播消息
    }

    private void unregisterBroadcast() {
        unregisterReceiver(dynamicReceiver);
    }

    public class DynamicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Filter100")) {    //动作检测
                //System.out.println("检测到用户到绑定设备");
                if (newVersion == null && newVersion.length() > 0) {
                    Intent intentBroadcast = new Intent();   //定义Intent
                    intentBroadcast.setAction("Filter");
                    intentBroadcast.putExtra("arg0", 19);
                    intentBroadcast.putExtra("mac",mac);
                    intentBroadcast.putExtra("version", newVersion);
                    sendBroadcast(intentBroadcast);
                }
                adapter.notifyDataSetChanged();
                tvTip.setText("");
            }
        }
    }

}