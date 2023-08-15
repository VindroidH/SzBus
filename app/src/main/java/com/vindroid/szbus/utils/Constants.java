package com.vindroid.szbus.utils;

public class Constants {
    public static final String BUS_LINE_SPLIT_REGEX = " - ";
    public static final String BUS_LINE_TO_HEADER = "开往：";
    public static final String BUS_COMING = "即将进站";
    public static final String BUS_NO_COMING = "尚未发车";
    public static final String BUS_NO_COMING2 = "待发车";
    public static final String ADDRESS_HEADER = "位于：";
    public static final String COMING_HEADER = "还有";
    public static final String COMING_FOOTER = "站";
    public static final String SPLIT_TEXT = "@SzBus@";

    public static final String KEY_DATA = "data";
    public static final String KEY_TYPE = "type";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_INDEX = "index";
    public static final String KEY_STATION = "station";
    public static final String KEY_BUS_LINES = "bus_lines";
    public static final String KEY_NOTIFICATION = "notification";
    public static final String KEY_START_TIME = "start_time";
    public static final String KEY_END_TIME = "end_time";
    public static final String KEY_DATE = "date";
    public static final String KEY_AHEAD = "ahead";

    public static final String TYPE_FAVORITE = "favorite";
    public static final String TYPE_SUBSCRIBE = "subscribe";

    public static final String UPDATE_TIME_FORMAT = "HH:mm:ss";
    public static final String SUBSCRIBE_TIME_FORMAT = "HH:mm";

    public static final int DEFAULT_AHEAD = 3;
    public static final int REFRESH_MIN_INTERVAL = 60000; // 60s
    public static final int DEFAULT_MAX_INDEX = 999;

    public static final int SUNDAY_BIT = 0b1000000;
    public static final int MONDAY_BIT = 0b100000;
    public static final int TUESDAY_BIT = 0b10000;
    public static final int WEDNESDAY_BIT = 0b1000;
    public static final int THURSDAY_BIT = 0b100;
    public static final int FRIDAY_BIT = 0b10;
    public static final int SATURDAY_BIT = 0b1;
}
