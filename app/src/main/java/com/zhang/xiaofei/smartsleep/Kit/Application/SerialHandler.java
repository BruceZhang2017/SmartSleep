package com.zhang.xiaofei.smartsleep.Kit.Application;

public class SerialHandler {
    public static String handleSerial(String serial) {
        if (serial.contains("-")) {
            String[] array = serial.split("-");
            return array[0];
        }
        return serial;
    }
}
