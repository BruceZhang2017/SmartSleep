package com.zhang.xiaofei.smartsleep.Model.Record;

import android.content.Context;

import com.zhang.xiaofei.smartsleep.YMApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BreathRecordManger {

    private static BreathRecordManger instance;

    private BreathRecordManger() {

    }

    public static BreathRecordManger getInstance() {
        if (instance == null)
            instance = new BreathRecordManger();
        return instance;
    }

    public void addCache(BreathRecordModel cacheData) {
        if (cacheData == null) return;
        try {
            File file = new File(YMApplication.getContext().getFilesDir(), "breath");
            if (!file.exists()) {
                file.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(cacheData);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BreathRecordModel getCache() {
        try {
            File file = new File(YMApplication.getContext().getFilesDir(), "breath");
            if (file == null) return null;
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            BreathRecordModel cacheData = (BreathRecordModel) ois.readObject();
            ois.close();
            return cacheData;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteRecordModel() {
        File file = new File(YMApplication.getContext().getFilesDir(), "breath");
        if (file != null) {
            file.delete();
        }
    }
}
