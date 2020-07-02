package com.zhang.xiaofei.smartsleep.Kit.DB;

import android.content.Context;
import android.content.SharedPreferences;

import com.zhang.xiaofei.smartsleep.Kit.Language.SpUtil;

public class CacheUtil {
    private static final String SP_NAME = "smartsleep";
    private static CacheUtil cUtil;
    private static SharedPreferences hmSpref;
    private static SharedPreferences.Editor editor;

    private CacheUtil(Context context) {
        hmSpref = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = hmSpref.edit();
    }

    public static CacheUtil getInstance(Context context) {
        if (cUtil == null) {
            synchronized (SpUtil.class) {
                if (cUtil == null) {
                    cUtil = new CacheUtil(context);
                }
            }
        }
        return cUtil;
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putBool(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return hmSpref.getString(key,"");
    }

    public Boolean getBool(String key) {
        return hmSpref.getBoolean(key,false);
    }

    public int getInt(String key) {
        return hmSpref.getInt(key, 0);
    }

    public void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }
}
