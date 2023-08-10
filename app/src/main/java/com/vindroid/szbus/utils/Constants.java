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
    public static final String KEY_FROM = "from";
    public static final String TYPE_FAVORITE = "favorite";
    public static final String TYPE_SUBSCRIBE = "subscribe";

    public static final String UPDATE_TIME_FORMAT = "HH:mm:ss";
    public static final String SUBSCRIBE_TIME_FORMAT = "HH:mm";

    public static final int DEFAULT_AHEAD = 3;
    public static final int MIN_REFRESH_INTERVAL = 60000;
}
