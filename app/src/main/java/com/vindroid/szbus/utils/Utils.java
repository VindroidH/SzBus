package com.vindroid.szbus.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static String getTime(String format) {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return currentTime.format(formatter);
    }
}
