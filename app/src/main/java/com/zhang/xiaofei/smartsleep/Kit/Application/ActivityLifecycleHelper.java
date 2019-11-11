//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.zhang.xiaofei.smartsleep.Kit.Application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;


import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/7/18.
 */

public class ActivityLifecycleHelper implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = ActivityLifecycleHelper.class.getSimpleName();
    private static ActivityLifecycleHelper singleton;
    private final List<WeakReference<Activity>> activities;

    private ActivityLifecycleHelper() {
        activities = new LinkedList<>();
    }

    public static synchronized ActivityLifecycleHelper build() {

        if (singleton == null) {
            singleton = new ActivityLifecycleHelper();
        }
        return singleton;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

        for (WeakReference<Activity> activityWeakReference : activities) {

            if (activityWeakReference.get() != null && activityWeakReference.get() == activity) {
                activities.remove(activityWeakReference);
                break;
            }
        }
    }

    /**
     * add to activity stack
     * here except WelcomeActivity because of；
     * 1.WelcomeActivity 作为启动页面会被android系统bug多次启动的情况，再次启动的时候会触发finishAllActivity的动作
     * 2.下文中notify对activity 向上转性，但WelcomeActivity 没有继承BaseActivity
     */
    public void addActivity(Activity activity) {
        activities.add(new WeakReference<>(activity));
    }

    /**
     * get top activity
     *
     * @return
     */
    public static Activity getLatestActivity() {
        ActivityLifecycleHelper adapter = build();
        int count = adapter.activities.size();
        if (count == 0) {
            return null;
        }
        return adapter.activities.get(count - 1).get();
    }

    /**
     * get bottom activity
     *
     * @return
     */
    public static Activity getBottomActivity() {
        ActivityLifecycleHelper adapter = build();
        int count = adapter.activities.size();
        if (count == 0) {
            return null;
        }
        return adapter.activities.get(0).get();
    }

    /**
     * get previous activity
     *
     * @return
     */
    public static Activity getPreviousActivity() {
        ActivityLifecycleHelper adapter = build();
        int count = adapter.activities.size();
        if (count < 2) {
            return null;
        }
        return adapter.activities.get(count - 2).get();
    }


    public static void notifyObservers(int type, Object data) {

        ActivityLifecycleHelper adapter = build();
        int count = adapter.activities.size();
        if (count > 0) {
            for (WeakReference<Activity> activity : adapter.activities) {

                if (activity.get() instanceof SoundCoreObserver) {
                    SoundCoreObserver observer = (SoundCoreObserver) activity.get();
                    if (observer != null) {
                        observer.notifyObserver(type, data);
                    }
                }
            }
        }
    }

    public static void removeActivities(String[] activites) {
        ActivityLifecycleHelper adapter = build();
        for (String activite : activites) {
            for (WeakReference<Activity> activity : adapter.activities) {
                if (!activity.get().isFinishing() && activity.get().getClass().getName().contains
                        (activite)) {
                    activity.get().finish();
                    adapter.activities.remove(activity);
                    break;
                }
            }
        }

    }

    public static void finishAll() {
        ActivityLifecycleHelper adapter = build();

        List<WeakReference<Activity>> activities = adapter.activities;
        try {
            for(int i=0;i<activities.size();i++){
                    WeakReference<Activity> activityWeakReference = activities.get(i);
                    Activity activity = activityWeakReference.get();
                    if (activity != null) {
                        activity.finish();
                    }
            }
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG,"finishAll error " +e.getMessage());

        }
    }

    public static void finishElseActivity(){
        ActivityLifecycleHelper adapter = build();

        List<WeakReference<Activity>> activities = adapter.activities;
        try {
            for(int i=0;i<activities.size();i++){
                WeakReference<Activity> activityWeakReference = activities.get(i);
                Activity activity = activityWeakReference.get();
                if (activity != null ) {
                    Log.d(TAG,"getBottomActivity() : "+getBottomActivity());
                    if(activity!= getBottomActivity()) {
                        activity.finish();
                    }
                }
            }
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG,"finishAll error " +e.getMessage());

        }
    }
}


