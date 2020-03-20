package com.zhang.xiaofei.smartsleep.Kit;

import android.app.Activity;
import android.util.DisplayMetrics;

import java.util.HashMap;
import java.util.Map;

/**
 * 屏幕显示相关工具类
 * （px ，sp， dp 等转换）
 */
public class DisplayUtil {
    private static Map<ScreenEnum,Integer> screenMap = new HashMap<>();
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue, Activity activity) {
        return (int) (pxValue / (getScreenMsg(activity).get(ScreenEnum.Density) / 10 )+ 0.5f);
    }
    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * @return
     */
    public static int dip2px(float dipValue, Activity activity) {
        return (int) (dipValue * getScreenMsg(activity).get(ScreenEnum.Density) / 10 + 0.5f);
    }
    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @return
     */
    public static int px2sp(float pxValue, Activity activity) {
        return (int) (pxValue / getScreenMsg(activity).get(ScreenEnum.ScaledDensity) / 10 + 0.5f);
    }
    /**
     * 将sp值转换为px值，保证文字大小不变
     * @return
     */
    public static int sp2px(float spValue, Activity activity) {
        return (int) (spValue * getScreenMsg(activity).get(ScreenEnum.ScaledDensity) / 10 + 0.5f);
    }
    /**
     * 获取屏幕尺寸等信息
     */
    public static Map<ScreenEnum,Integer> getScreenMsg(Activity activity){
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        float density = metric.density;///屏幕密度（0.75, 1.0 . 1.5）
        int densityDpi = metric.densityDpi;///屏幕密度DPI（120/160/240/320/480）
        float scaledDensity = metric.scaledDensity;
        if (screenMap==null) screenMap = new HashMap<>();

        screenMap.clear();
        screenMap.put(ScreenEnum.Width,width);
        screenMap.put(ScreenEnum.Height,height);
        screenMap.put(ScreenEnum.Density,(int)(density * 10));
        screenMap.put(ScreenEnum.DendityDpi,densityDpi);
        screenMap.put(ScreenEnum.ScaledDensity, (int)(scaledDensity * 10));
        return screenMap;
    }

    public enum ScreenEnum{
        Width,Height,Density,DendityDpi,ScaledDensity
    }

    public static int screenWidth(Activity activity) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;
        System.out.println("屏幕的宽度: " + widthPixels + "屏幕的高度：" + heightPixels);
        return widthPixels;
    }

    public static float density(Activity activity) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.density;
    }
}
