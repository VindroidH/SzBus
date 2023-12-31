package com.vindroid.szbus.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.vindroid.szbus.App;
import com.vindroid.szbus.BusCenter;
import com.vindroid.szbus.R;
import com.vindroid.szbus.helper.SubscribeHelper;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.Station;
import com.vindroid.szbus.model.StationDetail;
import com.vindroid.szbus.model.Subscribe;
import com.vindroid.szbus.model.SubscribeBusLine;
import com.vindroid.szbus.ui.station.StationActivity;
import com.vindroid.szbus.utils.Constants;
import com.vindroid.szbus.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class SubscribeService extends Service implements BusCenter.GetStationListener {
    private final static String TAG;

    private NotificationManager mManager;

    private List<Subscribe> mSubscribes;
    private final List<String> mStopUpdateStationIds = new ArrayList<>();
    private final Map<String, Integer> mNotificationIds = new HashMap<>();
    private final Map<String, String> mStationInfoMap = new HashMap<>();
    private int mRefreshCount = 0;

    private final int WHAT_REFRESH = 0;
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_REFRESH) {
                refreshData();
            }
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SubscribeHelper.ACTION_SUBSCRIBE_CHANGED.equals(intent.getAction())) {
                Log.d(TAG, "[onReceive] subscribe data changed");
                mHandler.removeMessages(WHAT_REFRESH);
                mSubscribes.clear();
                mStationInfoMap.clear();
                mNotificationIds.clear();
                mStopUpdateStationIds.clear();
                readyGo();
            }
        }
    };

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
        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        runningAsForegroundService();

        IntentFilter filter = new IntentFilter();
        filter.addAction(SubscribeHelper.ACTION_SUBSCRIBE_CHANGED);
        registerReceiver(mReceiver, filter);

        readyGo();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "[onStartCommand] type: " + intent.getStringExtra(Constants.KEY_TYPE));
        if (Constants.TYPE_STOP_UPDATE.equals(intent.getStringExtra(Constants.KEY_TYPE))) {
            stopUpdate(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void readyGo() {
        mSubscribes = SubscribeHelper.getAll();
        int id = 100;
        for (Subscribe subscribe : mSubscribes) {
            mNotificationIds.put(subscribe.getStation().getId(), id++);
        }
        mHandler.sendEmptyMessage(WHAT_REFRESH);
    }

    private void stopUpdate(Intent intent) {
        int notificationId = intent.getIntExtra(Constants.KEY_NOTIFICATION_ID, 100);
        String stationId = intent.getStringExtra(Constants.KEY_STATION_ID);
        if (!mStopUpdateStationIds.contains(stationId)) {
            mStopUpdateStationIds.add(stationId);
        }
        mManager.cancel(notificationId);

        mStationInfoMap.remove(stationId);

        int count = 0;
        int todayWeekBit = Utils.getDayOfWeek(new Date());
        String[] args = Utils.getTime(Constants.SUBSCRIBE_TIME_FORMAT).split(":");
        int todayTime = Integer.parseInt(args[0]) * 60 + Integer.parseInt(args[1]);
        for (Subscribe subscribe : mSubscribes) {
            if (mStopUpdateStationIds.contains(subscribe.getStation().getId())) continue;
            int weekBit = Integer.parseInt(String.valueOf(subscribe.getWeekBit()), 2);
            args = subscribe.getStartTime().split(":");
            int startTime = Integer.parseInt(args[0]) * 60 + Integer.parseInt(args[1]);
            args = subscribe.getEndTime().split(":");
            int endTime = Integer.parseInt(args[0]) * 60 + Integer.parseInt(args[1]);

            if ((weekBit & todayWeekBit) == todayWeekBit
                    && startTime <= todayTime && todayTime <= endTime) {
                count++;
            }
        }
        if (count == 0) {
            Log.d(TAG, "[stopUpdate] nothing to update, turn on the next alarm");
            mHandler.removeMessages(WHAT_REFRESH);
            startAlarm(SubscribeService.this);
            stopSelf();
        }
    }

    private void runningAsForegroundService() {
        NotificationChannel channel = new NotificationChannel(
                Constants.NOTIFICATION_DEFAULT_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        mManager.createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(
                this, Constants.NOTIFICATION_DEFAULT_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.notification_daily))
                .setGroup(Constants.NOTIFICATION_DEFAULT_ID)
                .build();

        startForeground(1000, notification);
    }

    public void getInfoByStation(String stationId) {
        AsyncTask<String, Integer, Boolean> task = new BusCenter.GetStation(this);
        task.execute(stationId);
    }

    private void refreshData() {
        Log.d(TAG, "[refreshData]");
        mRefreshCount = 0;
        int count = 0;
        int todayWeekBit = Utils.getDayOfWeek(new Date());
        String[] args = Utils.getTime(Constants.SUBSCRIBE_TIME_FORMAT).split(":");
        int todayTime = Integer.parseInt(args[0]) * 60 + Integer.parseInt(args[1]);
        for (Subscribe subscribe : mSubscribes) {
            if (mStopUpdateStationIds.contains(subscribe.getStation().getId())) continue;

            int weekBit = Integer.parseInt(String.valueOf(subscribe.getWeekBit()), 2);
            args = subscribe.getStartTime().split(":");
            int startTime = Integer.parseInt(args[0]) * 60 + Integer.parseInt(args[1]);
            args = subscribe.getEndTime().split(":");
            int endTime = Integer.parseInt(args[0]) * 60 + Integer.parseInt(args[1]);

            if ((weekBit & todayWeekBit) == todayWeekBit
                    && startTime <= todayTime && todayTime <= endTime) {
                mRefreshCount++;
                count++;
                getInfoByStation(subscribe.getStation().getId());
            }
        }
        if (count == 0) {
            Log.d(TAG, "[refreshData] nothing to update, turn on the next alarm");
            startAlarm(SubscribeService.this);
            SubscribeService.this.stopSelf();
        }
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
            int nearest = InComingBusLine.COMING_NO;
            String summary = "";
            List<String> contents = new ArrayList<>();
            for (SubscribeBusLine busLine : subscribe.getBusLines()) {
                if (result) {
                    for (InComingBusLine info : station.getBusLines()) {
                        if (busLine.getId().equals(info.getId())
                                && info.getComing() >= InComingBusLine.COMING_NOW
                                && info.getComing() <= busLine.getAhead()) {
                            Log.d(TAG, "[onGetStationCompleted] " + busLine.getName() + ", " + info.getComing());
                            String str = info.getName() + getString(R.string.colon);
                            if (info.getComing() == InComingBusLine.COMING_NOW) {
                                str += getString(R.string.coming_now);
                            } else {
                                str += getString(R.string.coming_still, String.valueOf(info.getComing()));
                            }
                            contents.add(str);
                            if (info.getComing() < nearest) {
                                nearest = info.getComing();
                                summary = str;
                            }
                        }
                    }
                } else {
                    contents.add(busLine.getName()
                            + getString(R.string.colon)
                            + getString(R.string.coming_err));
                }
            }
            if (contents.size() > 0) {
                Integer id = mNotificationIds.get(subscribe.getStation().getId());
                showNotification(id == null ? 100 : id, subscribe.getStation(), contents, summary);
            }
        }

        if (mRefreshCount == 0) {
            Log.d(TAG, "[onGetStationCompleted] all refreshed");
            mHandler.sendEmptyMessageDelayed(WHAT_REFRESH, Constants.REFRESH_MIN_INTERVAL);
        }
    }

    private void showNotification(int id, Station station, List<String> contents, String summary) {
        StringBuilder str = new StringBuilder();
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        style.setBigContentTitle(station.getName() + " - " + Utils.getTime(Constants.UPDATE_TIME_FORMAT));
        if (!TextUtils.isEmpty(summary)) {
            style.setSummaryText(summary);
        }
        for (String content : contents) {
            str.append(content);
            style.addLine(content);
        }

        if (Objects.equals(str.toString(), mStationInfoMap.get(station.getId()))) {
            Log.d(TAG, "[showNotification] station info has not changed");
            return;
        }
        mStationInfoMap.put(station.getId(), str.toString());

        int pendingFlags = PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE;

        Intent intent = new Intent(SubscribeService.this, SubscribeService.class);
        intent.putExtra(Constants.KEY_TYPE, Constants.TYPE_STOP_UPDATE);
        intent.putExtra(Constants.KEY_STATION_ID, station.getId());
        intent.putExtra(Constants.KEY_NOTIFICATION_ID, id);
        PendingIntent actionIntent = PendingIntent.getForegroundService(
                SubscribeService.this, 0, intent, pendingFlags);

        intent = new Intent(SubscribeService.this, StationActivity.class);
        intent.putExtra(Constants.KEY_ID, station.getId());
        intent.putExtra(Constants.KEY_NAME, station.getName());
        PendingIntent contentIntent = PendingIntent.getActivity(
                SubscribeService.this, 0, intent, pendingFlags);

        NotificationCompat.Action action = new NotificationCompat.Action(
                R.drawable.ic_cancel, getString(R.string.stop_update), actionIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, Constants.NOTIFICATION_SUBSCRIBE_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setGroup(Constants.NOTIFICATION_SUBSCRIBE_ID)
                .setGroupSummary(false)
                .setAutoCancel(true)
                .setStyle(style)
                .addAction(action)
                .setContentIntent(contentIntent)
                .setFullScreenIntent(contentIntent, true);

        NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_SUBSCRIBE_ID,
                getString(R.string.notification_title), NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(getString(R.string.notification_description));

        mManager.createNotificationChannel(channel);
        mManager.cancel(id);
        mManager.notify(id, builder.build());
    }

    public static void startAlarm(Context context) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            Log.w(TAG, "[startAlarm] no notification permission");
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
        triggerTime = triggerTime / 1000 * 1000;

        Intent intent = new Intent(context, SubscribeService.class);
        intent.putExtra(Constants.KEY_TYPE, Constants.TYPE_START_UPDATE);
        int pendingFlags = PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent sender = PendingIntent.getForegroundService(context, 0, intent, pendingFlags);

        Log.d(TAG, "[startAlarm] trigger time: " + triggerTime);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, sender);
    }
}