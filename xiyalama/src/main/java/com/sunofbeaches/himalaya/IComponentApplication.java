package com.sunofbeaches.himalaya;

import android.app.Application;



/**
 * 组件 APplication 接口
 *
 * @author DR
 * @date 2018/8/21
 */
public interface IComponentApplication {

    void onCreate(Application application);

    Application getAppliaction();

}
