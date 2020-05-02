package com.zhang.xiaofei.smartsleep.Kit;

import java.util.regex.Pattern;

public class ValidateHelper {
    public static boolean isPhoneNumber(String input) {// 判断手机号码是否规则
        String regex = "[0-9]+";
        Pattern p = Pattern.compile(regex);
        return p.matches(regex, input);//如果不是号码，则返回false，是号码则返回true
    }

    public static boolean isCode(String input) {
        return  input.length() != 4 || input.length() != 6;
    }
}
