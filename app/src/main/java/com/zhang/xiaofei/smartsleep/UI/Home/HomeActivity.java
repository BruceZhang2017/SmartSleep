package com.zhang.xiaofei.smartsleep.UI.Home;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.NameValuePair;
import com.ansen.http.net.RequestDataCallback;
import com.clj.blesample.FastBLEManager;
import com.clj.blesample.comm.BLEDataObserver;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.jpeng.jptabbar.BadgeDismissListener;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;
import com.jpeng.jptabbar.anno.NorIcons;
import com.jpeng.jptabbar.anno.SeleIcons;
import com.jpeng.jptabbar.anno.Titles;
import com.king.zxing.Intents;
import com.zhang.xiaofei.smartsleep.Kit.Application.ScreenInfoUtils;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceInfoManager;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceModel;
import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.DeviceManageActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.EasyCaptureActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class HomeActivity extends BaseAppActivity implements BadgeDismissListener, OnTabSelectListener, EasyPermissions.PermissionCallbacks, BLEDataObserver {

    private static final String DYNAMICACTION = "com.example.petter.broadcast.MyDynamicFilter";
    @Titles
    private static final int[] mTitles = {R.string.tab_home_page,R.string.tab_report,R.string.tab_found,R.string.tab_mine};
    @SeleIcons
    private static final int[] mSeleIcons = {R.mipmap.nav_icon_home_select,R.mipmap.nav_icon_report_select,R.mipmap.nav_icon_discover_select,R.mipmap.nav_icon_me_select};
    @NorIcons
    private static final int[] mNormalIcons = {R.mipmap.nav_icon_home_default, R.mipmap.nav_icon_report_default, R.mipmap.nav_icon_discover_default, R.mipmap.nav_icon_me_default};
    private List<Fragment> list = new ArrayList<>();
    private NoScrollViewPager mPager;
    private JPTabBar mTabbar;
    private HomePageFragment mTab1;
    private ReportFragment mTab2;
    private FoundGoodsFragment mTab3;
    private MineFragment mTab4;
    private int currentIndex = 0;
    public static final int RC_CAMERA = 0X01;
    public static final int REQUEST_CODE_SCAN = 0X01;
    public FastBLEManager fastBLEManager;
    public Realm mRealm;
    DynamicReceiver dynamicReceiver;
    private int userId = 0; // 用户ID
    private Intent foregroundIntent;
    private static final String DEVICEACTION = "com.zhangxiaofei.broadcast.Filter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mTabbar = (JPTabBar) findViewById(R.id.tabbar);
        mPager = (NoScrollViewPager) findViewById(R.id.view_pager);
        mTab1 = new HomePageFragment();
        mTab2 = new ReportFragment();
        mTab3 = new FoundGoodsFragment();
        mTab4 = new MineFragment();
        mTabbar.setGradientEnable(true);
        mTabbar.setPageAnimateEnable(true);
        mTabbar.setTabListener(this);
        list.add(mTab1);
        list.add(mTab2);
        list.add(mTab3);
        list.add(mTab4);

        mPager.setAdapter(new Adapter(getSupportFragmentManager(),list));
        mTabbar.setContainer(mPager);
        //设置Badge消失的代理
        mTabbar.setDismissListener(this);
        mTabbar.setTabListener(this);
        if(mTabbar.getMiddleView()!=null)
        mTabbar.getMiddleView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentB = new Intent();
                intentB.setClassName(HomeActivity.this,"com.zhang.xiaofei.smartsleep.UI.Home.HelpSleepActivity");
                HomeActivity.this.startActivity(intentB);
                HomeActivity.this.overridePendingTransition(R.anim.activity_open,0);
            }
        });
        ScreenInfoUtils.printScreenInfo(this);
        initialBLE();
        registerBroadcast();

        YMUserInfoManager userManager = new YMUserInfoManager( HomeActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        if (userModel != null) {
            userId = userModel.getUserInfo().getUserId();
        }

        foregroundIntent = new Intent(); // 开启前端服务。
        foregroundIntent.setAction("com.Xiaofei.service.FOREGROUND_SERVICE");
        //Android 5.0之后，隐式调用是除了设置setAction()外，还需要设置setPackage();
        foregroundIntent.setPackage("com.zhang.xiaofei.smartsleep");
        startService(foregroundIntent);
    }

    @Override
    public void onDismiss(int position) {

    }


    @Override
    public void onTabSelect(int index) {
        currentIndex = index;
    }

    @Override
    public boolean onInterruptSelect(int index) {
//        if(index==2){
//            //如果这里有需要阻止Tab被选中的话,可以return true
//            return true;
//        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fastBLEManager.onDestroy();
        mRealm.close();
        unregisterBroadcast();
    }

    private void registerBroadcast() {
        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction(DYNAMICACTION);    //添加动态广播的Action
        dynamicReceiver = new DynamicReceiver();
        registerReceiver(dynamicReceiver, dynamic_filter);    //注册自定义动态广播消息
    }

    private void unregisterBroadcast() {
        unregisterReceiver(dynamicReceiver);
    }


    public JPTabBar getTabbar() {
        return mTabbar;
    }

    //退出时的时间
    private long mExitTime;
    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //退出方法
    private void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(HomeActivity.this, getResources().getString(R.string.common_exit_app), Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            //用户退出处理
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

        switch (requestCode) {
            case 2:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            fastBLEManager.onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
    }

    /**
     * 检测拍摄权限
     */
    @AfterPermissionGranted(RC_CAMERA)
    public void checkCameraPermissions(){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            startScan();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "将设备序列号放入框内，即可自动扫描。",
                    RC_CAMERA, perms);
        }
    }

    private void asyncThread(Runnable runnable){
        new Thread(runnable).start();
    }

    /**
     * 扫码
     */
    private void startScan(){
        Intent intent = new Intent(this, EasyCaptureActivity.class);
        startActivityForResult(intent,REQUEST_CODE_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case REQUEST_CODE_SCAN:
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
                    if (result.startsWith("SLEEPBABY_")) {
                        String res = result.replace("SLEEPBABY_", "");
                        String[] array = res.split(",");
                        if (array.length == 2) {
                            addDevice(array[0], array[1].replace("-", ":"));
                            refreshBLEAndDevice();
                        }
                    }
                    break;
            }

        }

        if (requestCode == 3) { // 开始扫描
            if (fastBLEManager.checkGPSIsOpen()) {
                //fastBLEManager.setScanRule("");
                //fastBLEManager.startScanAndConnect();
            }
        }
    }

    private void addDevice(String serial, String mac) {
        YMUserInfoManager userManager = new YMUserInfoManager( HomeActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        int userId = userModel.getUserInfo().getUserId();
        RealmResults<DeviceModel> userList = mRealm.where(DeviceModel.class).equalTo("userId", userId).equalTo("mac", mac).findAll();
        if (userList != null && userList.size() > 0) {
            System.out.println("数据库已经存在同样的设备");
            return;
        }

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceModel model = realm.createObject(DeviceModel.class);
                model.setUserId(userId);
                model.setMac(mac);
                model.setDeviceType(1);
                model.setBindTime("");
                model.setDeviceSerial("YMB" + serial);
                model.setVersion(1);
                model.setId(1);
                model.setUpToCloud(false);
            }
        });

        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction(DEVICEACTION);
        sendBroadcast(intentBroadcast);
    }

    // 初始化蓝牙
    public void initialBLE() {
        fastBLEManager = new FastBLEManager();
        fastBLEManager.context = this;
        fastBLEManager.onCreate();
        fastBLEManager.bleDataObserver = this;

        refreshBLEAndDevice();
    }

    public void refreshBLEAndDevice() {
        YMUserInfoManager userManager = new YMUserInfoManager( HomeActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        int userId = userModel.getUserInfo().getUserId();
        if (mTab1 != null) {
            mTab1.deviceList.clear();
        }
        mRealm = Realm.getDefaultInstance();
        RealmResults<DeviceModel> userList = mRealm.where(DeviceModel.class).equalTo("userId", userId).findAll();
        if (userList != null && userList.size() > 0) {
            for (DeviceModel model: userList) {
                mTab1.deviceList.add(model);
            }
            fastBLEManager.macAddress = userList.get(0).getMac();
            fastBLEManager.startBLEScan(); // 重新刷列表
            if (mTab1 != null) {
                mTab1.showDeviceList();
            }
        } else {
            if (mTab1 != null) {
                mTab1.showDeviceList();
            }
        }
    }

    @Override
    public void handleBLEData(int battery, int flash, String mac, int version) {
        System.out.println("处理BLE返回的数据 " + battery + " " + flash + " " + mac + " " + version);
        DeviceModel deviceModel = mRealm.where(DeviceModel.class).equalTo("mac", mac).findFirst();
        if (deviceModel != null && deviceModel.getUpToCloud() == false) {
            addDeviceToCloud(mac, deviceModel.getDeviceSerial(), version);
        }
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //先查找后得到DeviceModel对象
                DeviceModel deviceModel = mRealm.where(DeviceModel.class).equalTo("mac", mac).findFirst();
                if (deviceModel != null) {
                    deviceModel.setVersion(version);
                    deviceModel.setUpToCloud(true);
                }
            }
        });
    }

    @Override
    public void handleBLEData(String mac, float temperature, float humdity) {
        if (mTab1 != null) {
            mTab1.refreshTempratureAndHumdity(temperature, humdity);
        }
        DeviceInfoManager.getInstance().hashMap.put(mac, temperature + "-" + humdity);
    }

    @Override
    public void handleBLEData(String mac, int time, int temperature, int humdity, int heartRate, Boolean heartStop, Boolean breatheStop, Boolean outBedAlarm) {
        mRealm.executeTransactionAsync(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                RecordModel model = realm.createObject(RecordModel.class);
                model.setUserId(userId);
                model.setDeviceId(1);
                model.setTime(time);
                model.setTemperature(temperature);
                model.setHumidity(humdity);
                model.setHeartRate(heartRate);
                model.setHeartStop(heartStop);
                model.setBreatheStop(breatheStop);
                model.setOutBedAlarm(outBedAlarm);
            }
        });
    }

    // 将设备上报至服务器端
    private void addDeviceToCloud(String mac, String serial, int version) {
        YMUserInfoManager userManager = new YMUserInfoManager( HomeActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "multipart/form-data");
        com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
        System.out.println("token:" + userModel.getToken());
        List<NameValuePair> postParam = new ArrayList<>();
        postParam.add(new NameValuePair("userId",userModel.getUserInfo().getUserId() + ""));
        postParam.add(new NameValuePair("deviceSerial","YMB" + serial));
        postParam.add(new NameValuePair("mac",mac));
        postParam.add(new NameValuePair("version","" + version));

        HTTPCaller.getInstance().postFile(
                BaseProtocol.class,
                YMApplication.getInstance().domain() + "app/deviceManage/bindDevice",
                new com.ansen.http.net.Header[]{header, headerToken},
                postParam,
                requestDataCallbackB
        );
    }

    private RequestDataCallback requestDataCallbackB = new RequestDataCallback<BaseProtocol>() {
        @Override
        public void dataCallback(int status, BaseProtocol user) {
            System.out.println("网络请求返回的Status:" + status);
            if(user==null || user.getCode() != 200){
                if (user != null) {
                    Toast.makeText(HomeActivity.this, user.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }else{

            }

        }

        @Override
        public void dataCallback(BaseProtocol obj) {
            if (obj == null) {
                Toast.makeText(HomeActivity.this, getResources().getText(R.string.common_check_network), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public class DynamicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DYNAMICACTION)) {    //动作检测
                System.out.println("检测到用户到绑定设备");
                int arg0 = intent.getIntExtra("arg0", 0);
                if (arg0 == 0) {
                    String serial = intent.getStringExtra("serial");
                    String mac = intent.getStringExtra("mac");
                    addDevice(serial, mac);
                    refreshBLEAndDevice();
                } else if (arg0 == 1) {
                    String mac = intent.getStringExtra("mac").toUpperCase();
                    if (mac.equals(fastBLEManager.macAddress)) {
                        fastBLEManager.stopBLEScan();
                        fastBLEManager.onDisConnect();
                    }
                    refreshBLEAndDevice();
                } else if (arg0 == 2) { // 设置闹钟
                    mRealm = Realm.getDefaultInstance();
                    RealmResults<AlarmModel> userList = mRealm.where(AlarmModel.class).findAll();
                    if (userList != null && userList.size() > 0) {
                        boolean sleep = false;
                        int sleepHour = 0;
                        int sleepMinute = 0;
                        int getupHour = 0;
                        int getupMinute = 0;
                        for (AlarmModel model: userList) {
                            if (model.getType() == 0) {
                                getupHour = model.getHour();
                                getupMinute = model.getMinute();
                            } else {
                                sleep = model.isOpen();
                                sleepHour = model.getHour();
                                getupMinute = model.getMinute();
                            }
                        }
                        if (sleepHour != 12) {
                            sleepHour += 12;
                        }
                        if (fastBLEManager != null && fastBLEManager.operationManager != null) {
                            fastBLEManager.operationManager.write(
                                    fastBLEManager.operationManager.bleOperation.autoDetection(
                                            0x01,
                                            sleep,
                                            sleepHour + ":" + sleepMinute + ":" + "00",
                                            getupHour + ":" + getupMinute + ":" + "00"));
                        }
                    }
                }

            }
        }
    }
}




