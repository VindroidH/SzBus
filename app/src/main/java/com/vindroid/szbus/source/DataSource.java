package com.vindroid.szbus.source;

import android.content.Context;
import android.content.SharedPreferences;

import com.vindroid.szbus.App;

public class DataSource {
    private static final String TAG;

    private static final String SP_NAME = "data_source_sp";
    private static final String SP_KEY_SOURCE = "data";
    private static final String SP_KEY_SZXING_VERSION = "szxing_version";

    public static final String SOURCE_SZGJ = "szgj";
    public static final String SOURCE_SZXING = "szxing";

    private static SharedPreferences mDataSourceSp = null;

    static {
        TAG = App.getTag(DataSource.class.getSimpleName());
    }

    private static SharedPreferences getDataSourceSp() {
        if (mDataSourceSp == null) {
            mDataSourceSp = App.getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return mDataSourceSp;
    }

    public static String getDataSource() {
        return getDataSourceSp().getString(SP_KEY_SOURCE, SOURCE_SZGJ);
    }

    public static String getSZXingVersion() {
        return getDataSourceSp().getString(SP_KEY_SZXING_VERSION, "");
    }

    public static void saveSZXingVersion(String json) {
        getDataSourceSp().edit().putString(SP_KEY_SZXING_VERSION, json).apply();
    }
}
