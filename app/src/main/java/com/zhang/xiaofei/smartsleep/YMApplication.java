package com.zhang.xiaofei.smartsleep;

import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.ansen.http.entity.HttpConfig;
import com.ansen.http.net.HTTPCaller;
import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.bugly.crashreport.CrashReport;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
import com.zhang.xiaofei.smartsleep.Kit.Application.CustomMigration;
import com.zhang.xiaofei.smartsleep.Kit.Application.LogInterceptor;
import com.zhang.xiaofei.smartsleep.Kit.Application.LogcatHelper;
import com.zhang.xiaofei.smartsleep.Kit.Application.ScreenInfoUtils;
import com.zhang.xiaofei.smartsleep.Kit.DB.CacheUtil;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Kit.Language.LanguageUtil;
import com.zhang.xiaofei.smartsleep.Kit.Language.SpUtil;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.Model.Record.RecordModel;
import com.zhang.xiaofei.smartsleep.Model.SleepTimeBean;
import com.zhang.xiaofei.smartsleep.Model.SleepTimeResponse;
import com.zhang.xiaofei.smartsleep.Tools.SendCMDToHomeActivity;
import com.zhang.xiaofei.smartsleep.UI.Home.HelpSleepActivity;
import com.zhang.xiaofei.smartsleep.UI.Home.SleepAndGetupTimeManager;
import com.zhang.xiaofei.smartsleep.UI.Home.SleepDataDownload;
import com.zhang.xiaofei.smartsleep.UI.Home.SleepDataUploadManager;
import com.zhang.xiaofei.smartsleep.UI.music.MediaPlayerService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import com.umeng.commonsdk.UMConfigure;
//import com.umeng.socialize.PlatformConfig;

public class YMApplication extends Application {

    public synchronized static YMApplication getInstance() {
        return instante;
    }
    private static YMApplication instante;
    private boolean bleOpen = false; // 保存蓝牙是否开启
    private int[] sleepbeltValue = new int[5]; // 得分， 入睡，睡眠时长，心率，呼吸率
    public Map<String, Integer> deviceBatteryMap = new HashMap<>();
    public static final String CHANNEL_ID_1 = "channel1";

    public static final long[] VIBRATION_PATTERN = {100, 400, 250, 350, 1000};
    public MediaPlayerService player;

    @Override
    public void onCreate() {
        super.onCreate();
        instante = this;
        ScreenInfoUtils.printScreenInfo(this);
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/pingjian_normal.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
//        PlatformConfig.setWeixin("wxa4dbc47c00500034", "6a223a6a98d76525f73fadb374d14e5f");//微信APPID和AppSecret
////        PlatformConfig.setQQZone("你的QQAPPID", "你的QQAppSecret");//QQAPPID和AppSecret
//        PlatformConfig.setSinaWeibo("30910805", "375e1761984e8ff4c55074d1f8daa312","http://www.yamind.cn");//微博
//        UMConfigure.setLogEnabled(true);
//        UMConfigure.init(this,"5db6902d4ca357d29c00074e","umeng",UMConfigure.DEVICE_TYPE_PHONE,"");

        CrashReport.initCrashReport(getApplicationContext(), "c0232e0b86", false); // bugly初始化

        /**
         * 对于7.0以下，需要在Application创建的时候进行语言切换
         */
        String language = SpUtil.getInstance(this).getString(SpUtil.LANGUAGE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            LanguageUtil.changeAppLanguage(YMApplication.getContext(), language);
        }

        initialOKHttp3();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myrealm.realm") //文件名
                .schemaVersion(1)
                .migration(new CustomMigration())//升级数据库
                .build();
        Realm.setDefaultConfiguration(config);
        System.out.println("数据库位置：" + Realm.getDefaultInstance().getPath());

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bleOpen = bluetoothAdapter.isEnabled();
        downloadSleepAndGetupTime();
        LogcatHelper.getInstance(this).start();
        SleepDataUploadManager uploadManager = new SleepDataUploadManager();
        uploadManager.uploadSleepData();

        SleepAndGetupTimeManager.getHashMapData();
        initNotificationChannels();
    }

    public static Context getContext() {
        return instante;
    }

    private void initialOKHttp3() {
        HttpConfig httpConfig=new HttpConfig();
        httpConfig.setAgent(true);//有代理的情况能不能访问
        httpConfig.setDebug(true);//是否debug模式 如果是debug模式打印log
        httpConfig.setTagName("ansen");//打印log的tagname

        //可以添加一些公共字段 每个接口都会带上
        //httpConfig.addCommonField("pf","android");
        //httpConfig.addCommonField("version_code","1");

        //初始化HTTPCaller类
        HTTPCaller.getInstance().setHttpConfig(httpConfig);
    }

    public String domain() {
        return "https://cloud.yamind.cn/"; //test/zips/20191022/dfufile.zip
    }

    public boolean getBLEOpen() {
        return bleOpen;
    }

    public void setBLEOpen(boolean value) {
        bleOpen = value;
    }

    public int[] getSleepbeltValue() {
        return sleepbeltValue;
    }

    public void setSleepbeltValue(int[] sleepbeltValue) {
        this.sleepbeltValue = sleepbeltValue;
    }

    void initNotificationChannels() {
        // make sure the least version is android oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            System.out.println("AAAAAAAA");
            // initialise the channels
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_ID_1,
                    "Channel1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setVibrationPattern(VIBRATION_PATTERN);
            channel1.setDescription("This is channel 1");

            // create the notification manager
            NotificationManager manager = getSystemService(NotificationManager.class);

            // create the channels
            try {
                manager.createNotificationChannel(channel1);
            } catch(NullPointerException exception) {
                System.err.println("Error: notification manager is NULL");
            }
        }
    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public void downloadSleepAndGetupTime() {
        boolean value = CacheUtil.getInstance(YMApplication.getContext()).getBool("SyncData");
        if (!value) {
            return;
        }
        YMUserInfoManager userInfoManager = new YMUserInfoManager(this);
        UserModel model = userInfoManager.loadUserInfo();
        if (model == null) {
            return;
        }
        OkHttpClient okHttpClient  = new OkHttpClient.Builder().addInterceptor(new LogInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = new FormBody.Builder().add("appUserId", model.getUserInfo().getUserId() + "").build();
        Request request = new Request.Builder()
                .url(YMApplication.getInstance().domain() + "app/userPara/listForUserSleepTime")//请求的url
                .addHeader("token", model.getToken())
                .post(requestBody)
                .build();

        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("网络请求失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    SleepTimeResponse res = gson.fromJson(response.body().string(), SleepTimeResponse.class);
                    if (res.getData() != null && res.getData().size() > 0) {
                        saveSleepTime(res.getData());
                    }
                } else {
                    System.out.println("从服务器下载数据失败");
                }
            }
        });
    }

    private void saveSleepTime(List<SleepTimeBean> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (SleepTimeBean bean: list) {
            String key = bean.getDownTime().substring(0, 10);
            List<String> arrayList = SleepAndGetupTimeManager.times.get(key);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
            }
            String time = bean.getUpTime().substring(0, 16) + "&" + bean.getDownTime().substring(0, 16);
            arrayList.add(time);
            SleepAndGetupTimeManager.times.put(key, arrayList);
        }
        SleepAndGetupTimeManager.putHashMapData();
        Intent intentBroadcast = new Intent();   //定义Intent
        intentBroadcast.setAction("Filter");
        intentBroadcast.putExtra("arg0", 17);
        sendBroadcast(intentBroadcast);
    }
}




