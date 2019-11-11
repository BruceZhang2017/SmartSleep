package com.zhang.xiaofei.smartsleep.Kit;

import androidx.constraintlayout.solver.Cache;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class NetworkManager {
    private OkHttpClient okHttpClient;
    private static NetworkManager singletonUpdate = null;

    public static NetworkManager getInstance() {
        if (null == singletonUpdate) {
            synchronized (NetworkManager.class) {
                if (null == singletonUpdate) {
                    singletonUpdate = new NetworkManager();
                }
            }
        }
        return singletonUpdate;
    }

    private NetworkManager() {
        //进行OkHttpClient的一些设置
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .build();
    }
}
