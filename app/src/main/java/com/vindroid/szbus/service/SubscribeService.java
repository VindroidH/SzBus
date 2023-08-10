package com.vindroid.szbus.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.vindroid.szbus.App;
import com.vindroid.szbus.BusCenter;
import com.vindroid.szbus.SubscribeWaiter;
import com.vindroid.szbus.model.NotificationInfo;
import com.vindroid.szbus.model.StationDetail;
import com.vindroid.szbus.model.Subscribe;
import com.vindroid.szbus.utils.Constants;
import com.vindroid.szbus.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SubscribeService extends Service implements BusCenter.GetStationListener {

    private final static String TAG;
    private List<Subscribe> mList;
    private int mCount = 0;
    List<NotificationInfo> notifList;

    static {
        TAG = App.getTag(SubscribeService.class.getSimpleName());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notifList = new ArrayList<>();
        mList = SubscribeWaiter.getAll();
        filterByTime();
        mCount = mList.size();
        for (Subscribe subscribe : mList) {
            getInfoByStation(subscribe.getStation().getId());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void filterByTime() {
        String[] args;
        args = Utils.getTime(Constants.SUBSCRIBE_TIME_FORMAT).split(":");
        int currentTime = Integer.parseInt(args[0]) * 60 + Integer.parseInt(args[1]);

        Iterator<Subscribe> iterator = mList.iterator();
        while (iterator.hasNext()) {
            Subscribe subscribe = iterator.next();
            args = subscribe.getStartTime().split(":");
            int startTime = Integer.parseInt(args[0]) * 60 + Integer.parseInt(args[1]);
            args = subscribe.getEndTime().split(":");
            int endTime = Integer.parseInt(args[0]) * 60 + Integer.parseInt(args[1]);
            if (currentTime < startTime || currentTime > endTime) {
                iterator.remove();
            }
        }
    }

    public void getInfoByStation(String stationId) {
        AsyncTask<String, Integer, Boolean> task = new BusCenter.GetStation(this);
        task.execute(stationId);
    }

    @Override
    public void onGetStationCompleted(boolean result, StationDetail station, String msg) {
        mCount--;

        Subscribe subscribe = null;
        for (Subscribe item : mList) {
            if (item.getStation().getId().equals(station.getId())) {
                subscribe = item;
                break;
            }
        }

        /*
        if (subscribe != null) {
            for (BusLine busLine : subscribe.getBusLineList()) {
                for (StationOperationalInfo info : station.getViaBusLineList()) {
                    if (busLine.getId().equals(info.getId())
                            && info.getComing() >= 0
                            && info.getComing() <= subscribe.getAhead(busLine.getId())) {
                        // TODO add
                        NotificationInfo i = new NotificationInfo();
                        notifList.add(i);
                    }
                }
            }
        }
         */

        if (mCount == 0) {
            Log.d(TAG, "All done");
            // TODO notify
        }
    }
}