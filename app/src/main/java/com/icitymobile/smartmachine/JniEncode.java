package com.icitymobile.smartmachine;

import android.util.Log;

import com.vindroid.szbus.App;

public class JniEncode {
    private static final String TAG;

    static {
        TAG = App.getTag("iCityMobile." + JniEncode.class.getSimpleName());
    }

    public static synchronized String getEncodeUrl(String uri, String deviceId, String deviceInfo) {
        String url;
        synchronized (JniEncode.class) {
            long timeInSeconds = System.currentTimeMillis() / 1000;
            url = YLEncode.composeUri(uri, deviceId, deviceInfo, String.valueOf(timeInSeconds));
            Log.i(TAG, "URI: " + uri);
            Log.i(TAG, "URL: " + url);
            Log.i(TAG, "DEVICE_INFO: " + deviceInfo);
            Log.i(TAG, "DEVICE_ID: " + deviceId);
        }
        return url;
    }
}
