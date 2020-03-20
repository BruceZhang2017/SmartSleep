package com.zhang.xiaofei.smartsleep.UI.Home;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.NameValuePair;
import com.ansen.http.net.RequestDataCallback;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.clj.blesample.FastBLEManager;
import com.clj.blesample.comm.BLEDataObserver;
import com.clj.fastble.utils.HexUtil;
import com.jpeng.jptabbar.BadgeDismissListener;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;
import com.jpeng.jptabbar.anno.NorIcons;
import com.jpeng.jptabbar.anno.SeleIcons;
import com.jpeng.jptabbar.anno.Titles;
import com.king.zxing.Intents;
import com.zhang.xiaofei.smartsleep.Kit.AlarmTimer;
import com.zhang.xiaofei.smartsleep.Kit.Application.BluetoothMonitorReceiver;
import com.zhang.xiaofei.smartsleep.Kit.Application.ScreenInfoUtils;
import com.zhang.xiaofei.smartsleep.Kit.Application.TestDataNotification;
import com.zhang.xiaofei.smartsleep.Kit.DB.CacheUtil;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Kit.ReadTXT;
import com.zhang.xiaofei.smartsleep.Model.Alarm.AlarmModel;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceInfoManager;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceManager;
import com.zhang.xiaofei.smartsleep.Model.Device.DeviceModel;
import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.DeviceManageActivity;
import com.zhang.xiaofei.smartsleep.UI.Me.EasyCaptureActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.realm.Realm;
import io.realm.RealmResults;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class HomeActivity extends BaseAppActivity implements BadgeDismissListener, OnTabSelectListener, EasyPermissions.PermissionCallbacks, BLEDataObserver {

    private static final String DYNAMICACTION = "Filter";
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
    public static final int RC_CAMERA = 0X01;
    public static final int REQUEST_CODE_SCAN = 0X01;
    public FastBLEManager fastBLEManager;
    public Realm mRealm;
    DynamicReceiver dynamicReceiver;
    private int userId = 0; // 用户ID
    private Intent foregroundIntent;
    private static final String DEVICEACTION = "com.zhangxiaofei.broadcast.Filter";
    private Map<String, String> attachValue = new HashMap<String, String>();
    private BluetoothMonitorReceiver bleListenerReceiver;
    private List<RecordModel> recordModelList = new ArrayList<>(); // 记录将保存于数据库中

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DeviceManager.getInstance().context = this;
        DeviceManager.getInstance().readDB();
        mRealm = Realm.getDefaultInstance();
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
        mPager.setOffscreenPageLimit(3);
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

//        foregroundIntent = new Intent(); // 开启前端服务。会常驻在前台
//        foregroundIntent.setAction("com.Xiaofei.service.FOREGROUND_SERVICE");
//        //Android 5.0之后，隐式调用是除了设置setAction()外，还需要设置setPackage();
//        foregroundIntent.setPackage("com.zhang.xiaofei.smartsleep");
//        startService(foregroundIntent);

        DeviceManager.getInstance().downloadDeviceList();
        registerBLE(); // 注册BLE状态监听

        handleFixedTimeForTempuratureAndHumidity();

        if (CacheUtil.getInstance(HomeActivity.this).getBool("sleep")) {
            ImageView imageView = mTabbar.getMiddleView().findViewById(R.id.iv_middle);
            imageView.setImageResource(R.mipmap.nav_icon_sleep_select);
        } else {
            ImageView imageView = mTabbar.getMiddleView().findViewById(R.id.iv_middle);
            imageView.setImageResource(R.mipmap.nav_icon_sleep);
        }

        //readSimulateData();
    }

    @Override
    public void onDismiss(int position) {

    }


    @Override
    public void onTabSelect(int index) {
        // 底部Tab选中的index
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
        unregisterReceiver(this.bleListenerReceiver);
        readTempuratureTimer.cancel();
        readTempuratureTimer = null;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
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
            finishAffinity();
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
            EasyPermissions.requestPermissions(this, "请至设置打开相机权限",
                    RC_CAMERA, perms);
        }
    }

    private void asyncThread(Runnable runnable){
        new Thread(runnable).start();
    }

    /**
     * 扫码
     */
    public void startScan(){
        //Intent intent = new Intent(this, EasyCaptureActivity.class);
        //startActivityForResult(intent,REQUEST_CODE_SCAN);
        Intent intent = new Intent(this, BLESearchActivity.class);
        startActivity(intent);
    }

    private void handleQRCode(String result) {
        if (result.startsWith("SLEEPBABY_") || result.startsWith("SLEEPBUTTON_") || result.startsWith("YMB") ){
            int type = 0;
            String res = "";
            if (result.startsWith("SLEEPBABY_")) { // 睡眠带
                res = result.replace("SLEEPBABY_", "");
                type = 1;
            } else if (result.startsWith("SLEEPBUTTON_")) { // 睡眠纽扣
                res = result.replace("SLEEPBUTTON_", "");
                type = 2;
            } else { // 呼吸机
                res = result;
                type = 3;
            }
            String[] array = res.split(",");
            if (array.length == 2) {
                System.out.println("开始添加设备");
                attachValue.put("mac", array[1].replace("-", ":"));
                attachValue.put("serial", array[0]);
                attachValue.put("type", type + "");
                addDeviceToCloud();
            }
        }
    }

    private void addDevice() {
        String mac = attachValue.get("mac");
        if (mac == null || mac.length() <= 0) {
            return;
        }
        String serial = attachValue.get("serial");
        if (serial == null || mac.length() <= 0) {
            return;
        }
        int type = Integer.parseInt(attachValue.get("type"));
        YMUserInfoManager userManager = new YMUserInfoManager( HomeActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        int userId = userModel.getUserInfo().getUserId();
        RealmResults<DeviceModel> userList = mRealm.where(DeviceModel.class).equalTo("userId", userId).equalTo("mac", mac).findAll();
        if (userList != null && userList.size() > 0) {
            System.out.println("数据库已经存在同样的设备");
            return;
        }
        RealmResults<DeviceModel> userListB = mRealm.where(DeviceModel.class).findAll();
        int size = userListB.size();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceModel model = realm.createObject(DeviceModel.class);
                model.setUserId(userId);
                model.setMac(mac);
                model.setDeviceType(type);
                model.setBindTime("");
                model.setDeviceSerial(serial);
                model.setVersion(1 + "");
                model.setId(1 + size);
                model.setUpToCloud(true);
            }
        });

        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction(DEVICEACTION); // 传递绑定成功的Action
        sendBroadcast(intentBroadcast);
    }

    // 初始化蓝牙
    private void initialBLE() {
        fastBLEManager = new FastBLEManager();
        fastBLEManager.context = this;
        fastBLEManager.onCreate();
        fastBLEManager.bleDataObserver = this;

        refreshBLEAndDevice();
    }

    // 刷新设备列表并扫描第一个设备
    private void refreshBLEAndDevice() {
        DeviceManager.getInstance().readDB();
        String serialId = "";
        int type = 0;
        if (DeviceManager.getInstance().deviceList.size() > 0) {
            serialId = DeviceManager.getInstance().deviceList.get(0).getDeviceSerial();
            type = DeviceManager.getInstance().deviceList.get(0).getDeviceType();
            String mac = DeviceManager.getInstance().deviceList.get(0).getMac();
            if (mac.length() == 17) {
                fastBLEManager.macAddress = mac;
                fastBLEManager.startBLEScan(); // 重新刷列表
            }
        }
        if (mTab1 != null) {
            mTab1.showDeviceList();
        }
        if (mTab2 != null && type == 3) {
            mTab2.refreshData(serialId);
        }
    }

    @Override
    public void handleBLEData(int state, int heart, int breath) {
        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction("Filter2");
        intentBroadcast.putExtra("arg1", state);
        intentBroadcast.putExtra("arg2", heart);
        intentBroadcast.putExtra("arg3", breath);
        intentBroadcast.putExtra("arg0", 0);
        sendBroadcast(intentBroadcast);
    }

    @Override
    public void handleBLEData(int battery, int flash, String mac, int version) {
        System.out.println("处理BLE返回的数据 " + battery + " " + flash + " " + mac + " " + version);
        DeviceModel deviceModel = mRealm.where(DeviceModel.class).equalTo("mac", mac).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //先查找后得到DeviceModel对象
                DeviceModel deviceModel = mRealm.where(DeviceModel.class).equalTo("mac", mac).findFirst();
                if (deviceModel != null) {
                    deviceModel.setVersion(version + "");
                }
            }
        });
        DeviceManager.getInstance().readDB();
    }

    @Override
    public void handleBLEData(String mac, float temperature, float humdity) {
        if (mTab1 != null) {
            mTab1.refreshTempratureAndHumdity(temperature, humdity);
        }
        DeviceInfoManager.getInstance().hashMap.put(mac, temperature + "-" + humdity);
        TestDataNotification.getInstance().notifyObserver();
    }

    @Override // int temperature, int humdity, int heartRate, int breathRate, Boolean breatheStop, Boolean outBedAlarm
    public void handleBLEData(String mac, int time, int[] array) {
        if (array.length < 8) {
            return;
        }
        if (mTab1 == null && DeviceManager.getInstance().deviceList.size() > DeviceManager.getInstance().currentDevice) {
            return;
        }
        int deviceId = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getId();
        System.out.println("将固件端获取到的设备数据插入数据库");
        RecordModel model = new RecordModel();
        model.setUserId(userId);
        model.setDeviceId(deviceId);
        model.setTime(time);
        model.setTemperature(array[0]);
        model.setHumidity(array[1]);
        model.setHeartRate(array[2]);
        model.setBreathRate(array[3]);
        model.setBodyMotion(array[4]);
        model.setGetupFlag(array[5]);
        model.setSnore(array[6]);
        model.setBreatheStop(array[7]);
        recordModelList.add(model);
        if (recordModelList.size() < 256) {
            return;
        }
        List<RecordModel> tem = deepCopy(recordModelList);
        recordModelList.clear();

        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(tem);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                System.out.println("数据插入服务器成功");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                System.out.println("数据插入服务器失败");
            }
        });
    }

    /// 集合的深Copy，先放置在该处
    public <E> List<E> deepCopy(List<E> src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
            List<E> dest = (List<E>) in.readObject();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<E>();
        }
    }

    @Override
    public void handleBLEWrite(int flag) {
        if (flag == 1) { // 同步时间至设备端
            if (mTab1 == null) {
                return;
            }
            if (DeviceManager.getInstance().currentDevice >= DeviceManager.getInstance().deviceList.size()) {
                return;
            }
            int deviceId = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType();
            if (fastBLEManager != null && fastBLEManager.operationManager != null) {
                fastBLEManager.operationManager.write(
                        fastBLEManager.operationManager.bleOperation.syncTime(deviceId));
            }
        } else if (flag == 2) { // 读取温度和湿度
            if (mTab1 == null) {
                return;
            }
            if (DeviceManager.getInstance().currentDevice >= DeviceManager.getInstance().deviceList.size()) {
                return;
            }
            int deviceId = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType();
            if (fastBLEManager != null && fastBLEManager.operationManager != null) {
                fastBLEManager.operationManager.write(
                        fastBLEManager.operationManager.bleOperation.getTemplateAndHumidity(deviceId));
            }
        } else if (flag == 3) { // 读取flash内数据
            if (mTab1 == null) {
                return;
            }
            if (DeviceManager.getInstance().currentDevice >= DeviceManager.getInstance().deviceList.size()) {
                return;
            }
            int deviceId = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType();
            if (fastBLEManager != null && fastBLEManager.operationManager != null) {
                fastBLEManager.operationManager.write(
                        fastBLEManager.operationManager.bleOperation.syncDataToFlash(deviceId));
            }
        }
    }

    // 将设备上报至服务器端
    private void addDeviceToCloud() {
        String mac = attachValue.get("mac");
        if (mac == null || mac.length() <= 0) {
            return;
        }
        String serial = attachValue.get("serial");
        if (serial == null || mac.length() <= 0) {
            return;
        }
        String type = attachValue.get("type");
        if (checkDBHavenSameTypeDevice(Integer.parseInt(type))) {
            return;
        }
        YMUserInfoManager userManager = new YMUserInfoManager( HomeActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "multipart/form-data");
        com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
        System.out.println("token:" + userModel.getToken());
        List<NameValuePair> postParam = new ArrayList<>();
        postParam.add(new NameValuePair("userId",userModel.getUserInfo().getUserId() + ""));
        postParam.add(new NameValuePair("deviceSerial",serial));
        postParam.add(new NameValuePair("mac",mac));
        postParam.add(new NameValuePair("version","" + 1));
        postParam.add(new NameValuePair("deviceType",type));

        HTTPCaller.getInstance().postFile(
                BaseProtocol.class,
                YMApplication.getInstance().domain() + "app/deviceManage/bindDevice",
                new com.ansen.http.net.Header[]{header, headerToken},
                postParam,
                requestDataCallbackB
        );
    }

    /// 为了保证每个用户只能添加同一个类型，只有一台设备。
    private boolean checkDBHavenSameTypeDevice(int type) {
        YMUserInfoManager userManager = new YMUserInfoManager( HomeActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        int userId = userModel.getUserInfo().getUserId();
        RealmResults<DeviceModel> userList = mRealm.where(DeviceModel.class).equalTo("userId", userId).equalTo("deviceType", type).findAll();
        if (userList != null && userList.size() > 0) {
            System.out.println("数据库已经存在同类型的设备");
            deleteDevice(userList.get(0).getDeviceSerial(), userList.get(0).getMac());
            return true;
        }
        return false;
    }

    private RequestDataCallback requestDataCallbackB = new RequestDataCallback<BaseProtocol>() {
        @Override
        public void dataCallback(int status, BaseProtocol user) {
            System.out.println("网络请求返回的Status:" + status);
            if(user==null || user.getCode() != 200){
                if (user != null) {
                    Toast.makeText(HomeActivity.this, user.getMsg(), Toast.LENGTH_SHORT).show();
                }
            } else {
                addDevice();
                refreshBLEAndDevice();
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
                //System.out.println("检测到用户到绑定设备");
                int arg0 = intent.getIntExtra("arg0", 0);
                if (arg0 == 0) { // 新增设备
                    String result = intent.getStringExtra("result");
                    fastBLEManager.macAddress = "";
                    fastBLEManager.stopBLEScan();
                    fastBLEManager.onDisConnect();
                    handleQRCode(result);
                } else if (arg0 == 1) { // 删除设备
                    String mac = intent.getStringExtra("mac").toUpperCase();
                    if (mac.equals(fastBLEManager.macAddress)) {
                        fastBLEManager.macAddress = "";
                        fastBLEManager.stopBLEScan();
                        fastBLEManager.onDisConnect();
                    }
                    refreshBLEAndDevice();
                } else if (arg0 == 2) { // 设置闹钟
                    if (mTab1 == null) {
                        return;
                    }
                    if (DeviceManager.getInstance().deviceList.size() == 0 || DeviceManager.getInstance().deviceList.size() <= DeviceManager.getInstance().currentDevice ) {
                        return;
                    }
                    int deviceId = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType();
                    RealmResults<AlarmModel> alarmList = mRealm.where(AlarmModel.class).findAll();
                    if (alarmList != null && alarmList.size() > 0) {
                        boolean sleep = false;
                        int sleepHour = 0;
                        int sleepMinute = 0;
                        int getupHour = 0;
                        int getupMinute = 0;
                        for (AlarmModel model: alarmList) {
                            if (model.getType() == 0) {
                                sleep = model.isOpen();
                                getupHour = model.getHour();
                                getupMinute = model.getMinute();
                            } else {
                                sleepHour = model.getHour();
                                getupMinute = model.getMinute();
                            }
                        }
                        System.out.println("设置的闹钟：" + getupHour + " " + getupMinute + " " + sleepHour + " " + sleepMinute);
                        if (fastBLEManager != null && fastBLEManager.operationManager != null) {
                            fastBLEManager.operationManager.write(
                                    fastBLEManager.operationManager.bleOperation.autoDetection(
                                            deviceId,
                                            sleep,
                                            sleepHour + ":" + sleepMinute + ":" + "00",
                                            getupHour + ":" + getupMinute + ":" + "00"));
                        }
                    }
                } else if (arg0 == 3) { // 睡觉 operation
                    if (mTab1 == null) {
                        return;
                    }
                    if (DeviceManager.getInstance().currentDevice >= DeviceManager.getInstance().deviceList.size()) {
                        return;
                    }
                    int deviceId = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType();
                    if (fastBLEManager != null && fastBLEManager.operationManager != null) {
                        fastBLEManager.operationManager.write(
                                fastBLEManager.operationManager.bleOperation.setSleepState(deviceId, true));
                    }
                    ImageView imageView = mTabbar.getMiddleView().findViewById(R.id.iv_middle);
                    imageView.setImageResource(R.mipmap.nav_icon_sleep_select);
                    CacheUtil.getInstance(HomeActivity.this).putBool("sleep", true); // 保存睡眠状态
                } else if (arg0 == 4) { // 起床
                    if (mTab1 == null) {
                        return;
                    }
                    if (DeviceManager.getInstance().currentDevice >= DeviceManager.getInstance().deviceList.size()) {
                        return;
                    }
                    int deviceId = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType();
                    if (fastBLEManager != null && fastBLEManager.operationManager != null) {
                        fastBLEManager.operationManager.write(
                                fastBLEManager.operationManager.bleOperation.setGetUpState(deviceId, true));
                    }
                    ImageView imageView = mTabbar.getMiddleView().findViewById(R.id.iv_middle);
                    imageView.setImageResource(R.mipmap.nav_icon_sleep);
                    CacheUtil.getInstance(HomeActivity.this).putBool("sleep", false); // 保存起床状态
                } else if (arg0 == 5) { // 请求时时数据
                    if (mTab1 == null) {
                        return;
                    }
                    if (DeviceManager.getInstance().currentDevice >= DeviceManager.getInstance().deviceList.size()) {
                        return;
                    }
                    int deviceId = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType();
                    if (fastBLEManager != null && fastBLEManager.operationManager != null) {
                        boolean bDetection = intent.getBooleanExtra("value", false);
                        fastBLEManager.operationManager.write(
                                fastBLEManager.operationManager.bleOperation.setIntoDetectionState(deviceId, bDetection));
                    }
                } else if (arg0 == 6) { // 切换设备
                    exchangeDevice(DeviceManager.getInstance().currentDevice);
                } else if (arg0 == 7) { // 设置闹钟
                    int value = intent.getIntExtra("value", 0);
                    System.out.println("在" + value + "秒后，闹钟会自动关闭");
                    if (value > 0) {
                        AlarmTimer.getInstance().startTimer(value);
                    } else {
                        AlarmTimer.getInstance().stopTimer();
                    }
                } else if (arg0 == 8) { // 读取温度和湿度
                    readDeviceTempuratureAndHumidity();
                } else if (arg0 == 9) { // 退出读取时时数据
                    if (mTab1 == null) {
                        return;
                    }
                    if (DeviceManager.getInstance().currentDevice >= DeviceManager.getInstance().deviceList.size()) {
                        return;
                    }
                    int deviceId = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType();
                    if (fastBLEManager != null && fastBLEManager.operationManager != null) {
                        boolean bDetection = intent.getBooleanExtra("value", false);
                        fastBLEManager.operationManager.write(
                                fastBLEManager.operationManager.bleOperation.setOutOfDetectionState(deviceId, bDetection));
                    }
                } else if (arg0 == 10) { // 退出读取时时数据
                    if (mTab1 == null) {
                        return;
                    }
                    if (DeviceManager.getInstance().currentDevice >= DeviceManager.getInstance().deviceList.size()) {
                        return;
                    }
                    int deviceId = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType();
                    if (fastBLEManager != null && fastBLEManager.operationManager != null) {
                        fastBLEManager.operationManager.write(
                                fastBLEManager.operationManager.bleOperation.heartAndBreath(deviceId));
                    }
                } else if (arg0 == 11) { // 进入波形图
                    if (mTab1 == null) {
                        return;
                    }
                    if (DeviceManager.getInstance().currentDevice >= DeviceManager.getInstance().deviceList.size()) {
                        return;
                    }
                    int deviceId = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType();
                    if (fastBLEManager != null && fastBLEManager.operationManager != null) {
                        boolean bDetection = intent.getBooleanExtra("value", false);
                        fastBLEManager.operationManager.write(
                                fastBLEManager.operationManager.bleOperation.setIntoDynamicWave(deviceId, bDetection));
                    }
                } else if (arg0 == 12) { // 退出波形图
                    if (mTab1 == null) {
                        return;
                    }
                    if (DeviceManager.getInstance().currentDevice >= DeviceManager.getInstance().deviceList.size()) {
                        return;
                    }
                    int deviceId = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType();
                    if (fastBLEManager != null && fastBLEManager.operationManager != null) {
                        boolean bDetection = intent.getBooleanExtra("value", false);
                        fastBLEManager.operationManager.write(
                                fastBLEManager.operationManager.bleOperation.setOutOfDynamicWave(deviceId, bDetection));
                    }
                } else if (arg0 == 13) {
                    if (fastBLEManager != null) {
                        fastBLEManager.bSearching = true;
                    }
                } else if (arg0 == 14) {
                    if (fastBLEManager != null) {
                        fastBLEManager.bSearching = false;
                    }
                }
            }
        }
    }

    // 切换设备连接
    public void exchangeDevice(int current) {
        if (mTab1 != null) {
            fastBLEManager.macAddress = "";
            fastBLEManager.stopBLEScan();
            fastBLEManager.onDisConnect();
            String mac = DeviceManager.getInstance().deviceList.get(current).getMac();
            if (mac.length() == 17) {
                fastBLEManager.macAddress = mac;
                fastBLEManager.startBLEScan();
            }

        }
    }

    public void refreshTab1() {
        if (mTab1 != null) {
            mTab1.showDeviceList();
            if (DeviceManager.getInstance().deviceList.size() != 0 && fastBLEManager.macAddress.length() == 0) {
                String mac = DeviceManager.getInstance().deviceList.get(0).getMac();
                if (mac.length() == 17) {
                    fastBLEManager.macAddress = mac;
                    fastBLEManager.startBLEScan();
                }
            }
        }
    }

    Handler bluetoothHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: // 蓝牙关闭
                    YMApplication.getInstance().setBLEOpen(false);
                    mTab1.refreshDeviceWithBLEStateChanged();
                    break;
                case 2: // 蓝牙开启
                    YMApplication.getInstance().setBLEOpen(true);
                    mTab1.refreshDeviceWithBLEStateChanged();
                    if (fastBLEManager != null && fastBLEManager.macAddress.length() > 0) {
                        fastBLEManager.startBLEScan();
                    }
                    break;
            }

        }
    };

    /*
    * 注册蓝牙
    * */
    private void registerBLE() {
        System.out.println("注册监听蓝牙状态变化的广播");
        // 初始化广播
        this.bleListenerReceiver = new BluetoothMonitorReceiver();
        this.bleListenerReceiver.handler = bluetoothHandler;
        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            // 注册广播
        registerReceiver(this.bleListenerReceiver, intentFilter);

    }

    @Override
    public void calculateReport(int value) {
        System.out.println("获取到固件返回的数据的总量："  + value);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTab1.tvTitle.setText("" + value);
            }
        });
    }

    // 读取温度和湿度的定时器
    private Timer readTempuratureTimer = new Timer();
    // 读取温度和湿度的定时任务
    private TimerTask readTempuratureTask = new TimerTask() {
        @Override
        public void run() {
            // 要做的事情
            if (YMApplication.getInstance().getBLEOpen() == false) {
                return;
            }
            readDeviceTempuratureAndHumidity();
        }
    };

    /// 刷新定时获取温度和湿度的区域
    private void handleFixedTimeForTempuratureAndHumidity() {
        readTempuratureTimer.schedule(readTempuratureTask, 1000, 5000);
    }

    // 获取设备的文档和湿度
    private void readDeviceTempuratureAndHumidity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTab1 == null) {
                    return;
                }
                if (DeviceManager.getInstance().currentDevice >= DeviceManager.getInstance().deviceList.size()) {
                    return;
                }
                int deviceId = DeviceManager.getInstance().deviceList.get(DeviceManager.getInstance().currentDevice).getDeviceType();
                if (fastBLEManager != null && fastBLEManager.operationManager != null) {
                    fastBLEManager.operationManager.write(
                            fastBLEManager.operationManager.bleOperation.getTemplateAndHumidity(deviceId));
                }
            }
        });
    }

    // 如果有同系列的设备，则替换当前设备让用户选择
    // 显示删除按钮
    public void deleteDevice(String serial, String mac) {
        new AlertView(
                getResources().getString(R.string.index_unbinding),
                getResources().getString(R.string.add_device_same_type_fail_tip),
                getResources().getString(R.string.middle_quit),
                new String[]{getResources().getString(R.string.middle_confirm)},
                null,
                this, AlertView.Style.Alert, new OnItemClickListener(){
            public void onItemClick(Object o,int position){
                deleteDeviceCloud(serial, mac);
                deleteDeviceFromDB(mac);
            }
        }).show();
    }

    // 删除设备
    private void deleteDeviceCloud(String serial, String mac) {
        showHUD();
        YMUserInfoManager userManager = new YMUserInfoManager( HomeActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "multipart/form-data");
        com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
        System.out.println("token:" + userModel.getToken());
        List<NameValuePair> postParam = new ArrayList<>();
        postParam.add(new NameValuePair("userId",userModel.getUserInfo().getUserId() + ""));
        postParam.add(new NameValuePair("serial",serial));
        postParam.add(new NameValuePair("mac",mac));
        postParam.add(new NameValuePair("version",1 + ""));

        HTTPCaller.getInstance().postFile(
                BaseProtocol.class,
                YMApplication.getInstance().domain() + "app/deviceManage/delBindDevice",
                new com.ansen.http.net.Header[]{header, headerToken},
                postParam,
                requestDataCallbackC
        );
    }

    private RequestDataCallback requestDataCallbackC = new RequestDataCallback<BaseProtocol>() {
        @Override
        public void dataCallback(int status, BaseProtocol user) {
            hideHUD();
            System.out.println("网络请求返回的Status:" + status);
            if(user == null || user.getCode() != 200){
                if (user != null) {
                    Toast.makeText(HomeActivity.this, user.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }else{
                addDeviceToCloud(); // 将设备添加到Cloud
            }

        }

        @Override
        public void dataCallback(BaseProtocol obj) {
            hideHUD();
            if (obj == null) {
                Toast.makeText(HomeActivity.this, getResources().getText(R.string.common_check_network), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void deleteDeviceFromDB(String mac) {
        final RealmResults<DeviceModel> deviceList = mRealm.where(DeviceModel.class).equalTo("mac", mac).findAll();
        if (deviceList != null && deviceList.size() > 0) {
            String serial = deviceList.get(0).getDeviceSerial();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    deviceList.get(0).deleteFromRealm();
                }
            });
        }
    }


    public byte uniteBytes(byte src0, byte src1){
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte)(_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte)(_b0 ^ _b1);
        return ret;
    }

    /**
     * bytes字符串转换为Byte值
     * @param src Byte字符串，每个Byte之间没有分隔符
     * @return byte
     */
    public byte hexStr2Bytes(String src) {
        if (src.length() != 2) {
            System.out.print("长度不为2");
            return 0x00;
        }
        byte[] tmp = src.getBytes();
        return uniteBytes(tmp[0], tmp[1]);
    }

    private void readSimulateData() {
        InputStream inputStream = getResources().openRawResource(R.raw.b);
        String content = ReadTXT.getString(inputStream);
        String[] strByte = content.split(" "); // 每次截取
        int len = strByte.length;
        int c = len / 229;
        byte[] data = new byte[229];
        for (int i = 0; i < c; i++) {
            for (int j = 0; j < 229; j++) {
                data[j] = hexStr2Bytes(strByte[i * 229 + j]);
            }
            int count = (data.length - 5) / 14;
            for (int k = 0; k < count; k++) {
                int index = 4 + k * 14; // 游标位置
                byte[] subData = new byte[14];
                for (int n = 0; n < 14; n++) {
                    subData[n] = data[index + n];
                }
                parseFlashData(subData);
            }
        }
    }

    private void parseFlashData(byte[] data) {
        if (data.length != 14) {
            return;
        }
        System.out.println("解析的值: " + HexUtil.formatHexString(data, true));
        int year = 2000 + (data[0] & 0xff);
        int month = data[1];
        int day = data[2];
        int hour = data[3];
        int minute = data[4];
        int second = data[5];
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        int temTime = (int)(calendar.getTimeInMillis() / 1000);

        int temperature = data[6] & 0xff;
        int humdity = data[7] & 0xff; // 湿度
        int heartRate = data[8] & 0xff;
        int breathRate = data[9] & 0xff;
        int bodyMotion = data[10] & 0xff;
        int getupFlag = data[11] & 0xff;
        int snore = data[12] & 0xff;
        int breathStop = data[13] & 0xff;
        int[] array = new int[8];
        array[0] = temperature;
        array[1] = humdity;
        array[2] = heartRate;
        array[3] = breathRate;
        array[4] = bodyMotion;
        array[5] = getupFlag;
        array[6] = snore;
        array[7] = breathStop;
        handleBLEData("", temTime, array);
    }
}




