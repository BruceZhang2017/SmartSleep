package com.zhang.xiaofei.smartsleep.Kit.BigSmallFont;

import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import com.zhang.xiaofei.smartsleep.Kit.DisplayUtil;

import java.lang.reflect.Array;

import io.github.inflationx.calligraphy3.CalligraphyTypefaceSpan;
import io.github.inflationx.calligraphy3.TypefaceUtils;

public class BigSmallFontManager {
    // 生成大小字体不一样的内容 小时 分
    public static SpannableString createTimeValue(String content, Activity activity, float fontSize, String[] array) {
        SpannableString spannableString = new SpannableString(content);
        for (String item: array) {
            spannableString.setSpan(new AbsoluteSizeSpan(DisplayUtil.sp2px(fontSize, activity))
                    , content.indexOf(item)
                    , content.indexOf(item) + item.length()
                    , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(activity.getAssets(), "fonts/pingjian_normal.ttf"));
        spannableString.setSpan(typefaceSpan, 0, content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static SpannableString createTimeValue(String content, Activity activity, float fontSize, String unit) {
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new AbsoluteSizeSpan(DisplayUtil.sp2px(fontSize, activity))
                , content.indexOf(unit)
                , content.indexOf(unit) + unit.length()
                , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(0xFF5DF2FF), 0, content.indexOf(unit), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(activity.getAssets(), "fonts/pingjian_normal.ttf"));
        spannableString.setSpan(typefaceSpan, 0, content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
