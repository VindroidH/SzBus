package com.vindroid.szbus.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static String getTime(String format) {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return currentTime.format(formatter);
    }

    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return Constants.MONDAY_BIT;
            case Calendar.TUESDAY:
                return Constants.TUESDAY_BIT;
            case Calendar.WEDNESDAY:
                return Constants.WEDNESDAY_BIT;
            case Calendar.THURSDAY:
                return Constants.THURSDAY_BIT;
            case Calendar.FRIDAY:
                return Constants.FRIDAY_BIT;
            case Calendar.SATURDAY:
                return Constants.SATURDAY_BIT;
            case Calendar.SUNDAY:
                return Constants.SUNDAY_BIT;
            default:
                return Constants.UNKNOWN_BIT;
        }
    }
}
