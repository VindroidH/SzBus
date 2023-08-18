package com.vindroid.szbus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vindroid.szbus.App;
import com.vindroid.szbus.service.SubscribeService;

public class BootReceiver extends BroadcastReceiver {
    private final static String TAG;

    static {
        TAG = App.getTag(BootReceiver.class.getSimpleName());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "[onReceive] ACTION_BOOT_COMPLETED, start alarm");
            SubscribeService.startAlarm(context);
        }
    }
}