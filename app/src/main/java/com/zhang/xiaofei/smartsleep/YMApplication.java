package com.zhang.xiaofei.smartsleep;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;

import com.ansen.http.entity.HttpConfig;
import com.ansen.http.net.HTTPCaller;
import com.facebook.stetho.Stetho;
import com.sunofbeaches.himalaya.IComponentApplication;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
import com.zhang.xiaofei.smartsleep.Kit.Application.CustomMigration;
import com.zhang.xiaofei.smartsleep.Kit.Application.LogcatHelper;
import com.zhang.xiaofei.smartsleep.Kit.Application.ScreenInfoUtils;
import com.zhang.xiaofei.smartsleep.Kit.Language.LanguageUtil;
import com.zhang.xiaofei.smartsleep.Kit.Language.SpUtil;
import com.zhang.xiaofei.smartsleep.UI.Home.SleepDataUploadManager;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class YMApplication extends Application {

    public synchronized static YMApplication getInstance() {
        return instante;
    }
    private static YMApplication instante;
    public static final String[] MODULESLIST =
            {"com.sunofbeaches.himalaya.base.TingApplication"};
    private boolean bleOpen = false; // 保存蓝牙是否开启

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
        PlatformConfig.setWeixin("wxa4dbc47c00500034", "6a223a6a98d76525f73fadb374d14e5f");//微信APPID和AppSecret
//        PlatformConfig.setQQZone("你的QQAPPID", "你的QQAppSecret");//QQAPPID和AppSecret
        PlatformConfig.setSinaWeibo("30910805", "375e1761984e8ff4c55074d1f8daa312","http://www.yamind.cn");//微博
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this,"5db6902d4ca357d29c00074e","umeng",UMConfigure.DEVICE_TYPE_PHONE,"");

        //Module类的APP初始化
        modulesApplicationInit();

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

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bleOpen = bluetoothAdapter.isEnabled();

        LogcatHelper.getInstance(this).start();
        SleepDataUploadManager uploadManager = new SleepDataUploadManager();
        uploadManager.uploadSleepData();
    }

    private void modulesApplicationInit(){
        for (String moduleImpl : MODULESLIST){
            try {
                Class<?> clazz = Class.forName(moduleImpl);
                Object obj = clazz.newInstance();
                if (obj instanceof IComponentApplication){
                    ((IComponentApplication) obj).onCreate(YMApplication.getInstance());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
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
        return "http://cloud.yamind.cn:9999/"; //test/zips/20191022/dfufile.zip
    }

    public boolean getBLEOpen() {
        return bleOpen;
    }

    public void setBLEOpen(boolean value) {
        bleOpen = value;
    }

}




