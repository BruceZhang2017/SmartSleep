//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.zhang.xiaofei.smartsleep.Kit.Application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = CrashHandler.class.getSimpleName();

    // CrashHandler instance
    private static CrashHandler INSTANCE = new CrashHandler();


    private Context mContext;

    private Application app;

    private Thread.UncaughtExceptionHandler mDefaultHandler;


    /**
     * Constructor
     */
    private CrashHandler() {
    }

    private static class SingletonHolder {
        public static CrashHandler instance = new CrashHandler();
    }

    public static CrashHandler getInstance() {
        return CrashHandler.SingletonHolder.instance;
    }

    /**
     * @param context
     * @param app
     */
    public void init(Context context, Application app) {
        this.app = app;
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, "uncaughtException: " + ex != null ? ex.getMessage() : "");
        ex.printStackTrace();
        app.onTerminate();
    }
}