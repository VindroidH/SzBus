package com.vindroid.szbus.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.vindroid.szbus.App;
import com.vindroid.szbus.BusCenter;
import com.vindroid.szbus.R;
import com.vindroid.szbus.helper.SubscribeHelper;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.StationDetail;
import com.vindroid.szbus.model.Subscribe;
import com.vindroid.szbus.model.SubscribeBusLine;
import com.vindroid.szbus.utils.Constants;
import com.vindroid.szbus.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class SubscribeService extends Service implements BusCenter.GetStationListener {

    private final static String TAG;
    private List<Subscribe> mSubscribes;
    private final List<String> mStopUpdateStationIds = new ArrayList<>();
    private final Map<String, Integer> mNotificationIds = new HashMap<>();
    private int mRefreshCount = 0;

    static {
        TAG = App.getTag(SubscribeService.class.getSimpleName());
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        runningAsForegroundService();
        mSubscribes = SubscribeHelper.getAll();
        int id = 100;
        for (Subscribe subscribe : mSubscribes) {
            mNotificationIds.put(subscribe.getStation().getId(), id++);
        }
        refreshData(0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "[onStartCommand] start");
        if (intent.hasExtra(Constants.KEY_TYPE)
                && Constants.TYPE_STOP_UPDATE.equals(intent.getStringExtra(Constants.KEY_TYPE))) {
            mStopUpdateStationIds.add(intent.getStringExtra(Constants.KEY_STATION_ID));
            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(intent.getIntExtra(Constants.KEY_NOTIFICATION_ID, 100));
            if (mStopUpdateStationIds.size() == mSubscribes.size()) {
                Log.d(TAG, "ignore all, start next alarm & stop self");
                startAlarm(SubscribeService.this);
                stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void runningAsForegroundService() {
        NotificationChannel channel = new NotificationChannel(
                Constants.NOTIFICATION_DEFAULT_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_NONE);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(
                this, Constants.NOTIFICATION_DEFAULT_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.notification_daily))
                .build();

        startForeground(1000, notification);
    }

    public void getInfoByStation(String stationId) {
        AsyncTask<String, Integer, Boolean> task = new BusCenter.GetStation(this);
        task.execute(stationId);
    }

    private void refreshData(long delayMillis) {
        new Handler().postDelayed(() -> {
            Log.d(TAG, "[refreshData]");
            mRefreshCount = 0;
            int findCount = 0;
            int todayWeekBit = Utils.getDayOfWeek(new Date());
            String[] args = Utils.getTime(Constants.SUBSCRIBE_TIME_FORMAT).split(":");
            int todayTime = Integer.parseInt(args[0]) * 60 + Integer.parseInt(args[1]);
            for (Subscribe subscribe : mSubscribes) {
                if (mStopUpdateStationIds.contains(subscribe.getStation().getId())) {
                    continue;
                }

                int weekBit = Integer.parseInt(String.valueOf(subscribe.getWeekBit()), 2);
                args = subscribe.getStartTime().split(":");
                int startTime = Integer.parseInt(args[0]) * 60 + Integer.parseInt(args[1]);
                args = subscribe.getEndTime().split(":");
                int endTime = Integer.parseInt(args[0]) * 60 + Integer.parseInt(args[1]);

                if ((weekBit & todayWeekBit) == todayWeekBit
                        && startTime <= todayTime && todayTime <= endTime) {
                    mRefreshCount++;
                    findCount++;
                    getInfoByStation(subscribe.getStation().getId());
                }
            }
            if (findCount == 0) {
                Log.d(TAG, "there are no eligible subscriptions, start next alarm & stop self");
                startAlarm(SubscribeService.this);
                SubscribeService.this.stopSelf();
            }
        }, delayMillis);
    }

    @Override
    public void onGetStationCompleted(boolean result, StationDetail station, String msg) {
        mRefreshCount--;

        Subscribe subscribe = null;
        for (Subscribe item : mSubscribes) {
            if (item.getStation().getId().equals(station.getId())) {
                subscribe = item;
                break;
            }
        }
        if (subscribe != null) {
            List<String> contents = new ArrayList<>();
            for (SubscribeBusLine busLine : subscribe.getBusLines()) {
                for (InComingBusLine info : station.getBusLines()) {
                    if (busLine.getId().equals(info.getId())
                            && 0 <= info.getComing() && info.getComing() <= busLine.getAhead()) {
                        if (info.getComing() == InComingBusLine.COMING_NOW) {
                            contents.add(info.getName() + "：" + getString(R.string.coming_now));
                        } else {
                            contents.add(info.getName() + "："
                                    + getString(R.string.coming_still, String.valueOf(info.getComing())));
                        }
                    }
                }
            }
            if (contents.size() > 0) {
                showNotification(mNotificationIds.get(subscribe.getStation().getId()),
                        subscribe.getStation().getName(), contents, subscribe.getStation().getId());
            }
        }

        if (mRefreshCount == 0) {
            Log.d(TAG, "all refreshed, refresh again in " + Constants.REFRESH_MIN_INTERVAL + "ms");
            refreshData(Constants.REFRESH_MIN_INTERVAL);
        }
    }

    private void showNotification(int id, String title, List<String> contents, String stationId) {
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        style.setBigContentTitle(title);
        for (String content : contents) {
            Log.d(TAG, "test, add content " + content);
            style.addLine(content);
        }

        Intent intent = new Intent(SubscribeService.this, SubscribeService.class);
        intent.putExtra(Constants.KEY_TYPE, Constants.TYPE_STOP_UPDATE);
        intent.putExtra(Constants.KEY_STATION_ID, stationId);
        intent.putExtra(Constants.KEY_NOTIFICATION_ID, id);
        int pendingFlags = PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent actionIntent = PendingIntent.getForegroundService(
                SubscribeService.this, 0, intent, pendingFlags);

        NotificationCompat.Action action = new NotificationCompat.Action(
                R.drawable.ic_cancel, getString(R.string.stop_update), actionIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, Constants.NOTIFICATION_SUBSCRIBE_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(style)
                .addAction(action);

        NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_SUBSCRIBE_ID,
                getString(R.string.notification_title), NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(getString(R.string.notification_description));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(id, builder.build());
    }

    public static void startAlarm(Context context) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            Log.w(TAG, "[startAlarm] no notification permission, stop process");
            return;
        }

        List<Subscribe> subscribes = SubscribeHelper.getAll();
        if (subscribes.size() == 0) {
            return;
        }

        long triggerTime = -1;
        Calendar calendar;
        String[] args;
        for (Subscribe subscribe : subscribes) {
            args = subscribe.getStartTime().split(":");
            calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    Integer.parseInt(args[0]), Integer.parseInt(args[1]), 0);
            long millis = calendar.getTimeInMillis();
            if (millis <= System.currentTimeMillis()) millis += 24 * 3600 * 1000;
            if (triggerTime == -1 || millis < triggerTime) triggerTime = millis;
        }

        Intent intent = new Intent(context, SubscribeService.class);
        int pendingFlags = PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent sender = PendingIntent.getForegroundService(context, 0, intent, pendingFlags);

        Log.d(TAG, "[startAlarm] trigger time: " + triggerTime);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, sender);
    }
}