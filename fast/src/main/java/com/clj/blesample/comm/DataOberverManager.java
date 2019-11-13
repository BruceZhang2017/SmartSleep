package com.clj.blesample.comm;

import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

public class DataOberverManager implements DataObservable {

    public static DataOberverManager getInstance() {
        return ObserverManagerHolder.sObserverManager;
    }

    private static class ObserverManagerHolder {
        private static final DataOberverManager sObserverManager = new DataOberverManager();
    }

    private List<DataObserver> observers = new ArrayList<>();

    @Override
    public void addObserver(DataObserver obj) {
        observers.add(obj);
    }

    @Override
    public void deleteObserver(DataObserver obj) {
        int i = observers.indexOf(obj);
        if (i >= 0) {
            observers.remove(obj);
        }
    }

    @Override
    public void notifyObserver(int heart, int breath) {
        for (int i = 0; i < observers.size(); i++) {
            DataObserver o = observers.get(i);
            o.notifyData(heart, breath);
        }
    }
}
