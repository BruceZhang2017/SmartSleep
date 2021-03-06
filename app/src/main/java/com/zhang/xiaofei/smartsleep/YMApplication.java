package com.zhang.xiaofei.smartsleep;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
import com.ximalaya.ting.android.opensdk.test.IComponentApplication;
import com.zhang.xiaofei.smartsleep.Kit.Language.LanguageUtil;
import com.zhang.xiaofei.smartsleep.Kit.Language.SpUtil;

import com.ansen.http.entity.HttpConfig;
import com.ansen.http.net.HTTPCaller;

import io.realm.Realm;

public class YMApplication extends Application {

    public synchronized static YMApplication getInstance() {
        return instante;
    }
    private static YMApplication instante;
    public static final String[] MODULESLIST =
            {"com.ximalaya.ting.android.opensdk.test.TingApplication"};


    @Override
    public void onCreate() {
        super.onCreate();
        instante = this;
        Log.i("Application", "YMApplication 初始化");
//        PlatformConfig.setWeixin("你的微信APPID", "你的微信AppSecret");//微信APPID和AppSecret
//        PlatformConfig.setQQZone("你的QQAPPID", "你的QQAppSecret");//QQAPPID和AppSecret
//        PlatformConfig.setSinaWeibo("你的微博APPID", "你的微博APPSecret","微博的后台配置回调地址");//微博
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this,"5db6902d4ca357d29c00074e","umeng",UMConfigure.DEVICE_TYPE_PHONE,"");

        //Module类的APP初始化
        modulesApplicationInit();

        CrashReport.initCrashReport(getApplicationContext(), "c0232e0b86", false);

        /**
         * 对于7.0以下，需要在Application创建的时候进行语言切换
         */
        String language = SpUtil.getInstance(this).getString(SpUtil.LANGUAGE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            LanguageUtil.changeAppLanguage(YMApplication.getContext(), language);
        }

        initialOKHttp3();

        Realm.init(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
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

}




