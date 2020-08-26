package com.zhang.xiaofei.smartsleep.UI.Home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SleepAndGetupTimeManager {
    private static String TAG = "SleepAndGetupTimeManager";
    public static Map<String, List<String>> times = new HashMap<>();
    /**
     * 用于保存集合
     *
     * @return 保存结果
     */
    public static boolean putHashMapData() {
        boolean result;
        SharedPreferences sp = YMApplication.getContext().getSharedPreferences("SmartSleep", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        try {
            Gson gson = new Gson();
            String json = gson.toJson(times);
            editor.putString("times", json);
            result = true;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        editor.apply();
        return result;
    }

    /**
     * 用于取出集合
     *
     * @return HashMap
     */
    public static void getHashMapData() {
        SharedPreferences sp = YMApplication.getContext().getSharedPreferences("SmartSleep", Context.MODE_PRIVATE);
        String json = sp.getString("times", "");
        if (json.length() == 0) {
            return;
        }
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonObject obj= jsonParser.parse(json).getAsJsonObject();
        times = gson.fromJson(obj, times.getClass());
        remove7daysDateTime();
    }

    public static void clearHashMapData() {
        SharedPreferences sp = YMApplication.getContext().getSharedPreferences("SmartSleep", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
    
    public static void remove7daysDateTime() {
        if (times.size() == 0) {
            return;
        }
        Map<String, List<String>> timeBs = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : times.entrySet()) {
            String first = entry.getValue().get(0);
            String[] array = first.split("&");
            if (array.length > 1) {
                System.out.println("计算的参数为：" + array[1]);
                if (checkTime(array[1].substring(0, 10)) || checkTime(array[0].substring(0, 10)) || checkTime(entry.getKey())) {

                } else {
                    timeBs.put(entry.getKey(), entry.getValue());
                }
            }
        }
        times = timeBs;
    }
    
    private static boolean checkTime(String time) {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(time);
            if (now.getTime() - date.getTime() >= 7 * 24 * 60 * 60 * 1000) {
                return true;
            }
        } catch (ParseException exception) {

        }
        return false;
    }
}
