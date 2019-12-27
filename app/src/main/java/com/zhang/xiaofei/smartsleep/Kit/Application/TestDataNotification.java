package com.zhang.xiaofei.smartsleep.Kit.Application;

import com.clj.blesample.comm.DataOberverManager;
import com.clj.blesample.comm.DataObserver;

import java.util.ArrayList;
import java.util.List;

public class TestDataNotification {
    public static TestDataNotification getInstance() {
        return TestDataNotification.ObserverManagerHolder.sObserverManager;
    }

    private static class ObserverManagerHolder {
        private static final TestDataNotification sObserverManager = new TestDataNotification();
    }

    private List<ActivityObserver> observers = new ArrayList<>();

    public void addObserver(ActivityObserver obj) {
        observers.add(obj);
    }

    public void deleteObserver(ActivityObserver obj) {
        int i = observers.indexOf(obj);
        if (i >= 0) {
            observers.remove(obj);
        }
    }

    public void notifyObserver() {
        for (int i = 0; i < observers.size(); i++) {
            ActivityObserver o = observers.get(i);
            o.notifyData();
        }
    }
}


