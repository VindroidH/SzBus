package com.vindroid.szbus.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Archive {
    public static final String SP_NAME = "suzhou_bus";
    public static final String KEY_CACHE_TIME = "cache_time";
    Context mContext;
    SharedPreferences mSp;
    public Archive(Context context) {
        mContext = context.getApplicationContext();
        mSp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }
}
