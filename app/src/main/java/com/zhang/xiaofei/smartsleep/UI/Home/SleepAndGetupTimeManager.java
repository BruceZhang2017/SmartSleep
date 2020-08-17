package com.zhang.xiaofei.smartsleep.UI.Home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhang.xiaofei.smartsleep.YMApplication;

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
    }


}
