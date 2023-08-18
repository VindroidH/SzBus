package com.vindroid.szbus.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.vindroid.szbus.App;
import com.vindroid.szbus.model.Station;
import com.vindroid.szbus.model.Subscribe;
import com.vindroid.szbus.model.SubscribeBusLine;
import com.vindroid.szbus.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubscribeHelper {
    private static final String TAG;

    private static final String SP_NAME = "subscribe_sp";
    private static final String SP_KEY_DATA = "data";

    private static SharedPreferences mSubscribeSp = null;

    static {
        TAG = App.getTag(SubscribeHelper.class.getSimpleName());
    }

    public static SharedPreferences getSubscribeSp() {
        if (mSubscribeSp == null) {
            mSubscribeSp = App.getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return mSubscribeSp;
    }

    public static void add(Subscribe subscribe) {
        Log.d(TAG, "[add] station name: " + subscribe.getStation().getName()
                + ", date: " + subscribe.getWeekBit()
                + ", time: " + subscribe.getStartTime() + " ~ " + subscribe.getEndTime());
        SharedPreferences sp = getSubscribeSp();
        String str = sp.getString(SP_KEY_DATA, "[]");
        try {
            JSONArray array = new JSONArray(str);
            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i).getJSONObject(Constants.KEY_STATION)
                        .getString(Constants.KEY_ID).equals(subscribe.getStation().getId())) {
                    array.remove(i);
                    break;
                }
            }

            JSONObject data = new JSONObject();

            JSONObject item = new JSONObject();
            item.put(Constants.KEY_START_TIME, subscribe.getStartTime());
            item.put(Constants.KEY_END_TIME, subscribe.getEndTime());
            item.put(Constants.KEY_DATE, subscribe.getWeekBit());
            data.put(Constants.KEY_NOTIFICATION, item);

            item = new JSONObject();
            item.put(Constants.KEY_ID, subscribe.getStation().getId());
            item.put(Constants.KEY_NAME, subscribe.getStation().getName());
            data.put(Constants.KEY_STATION, item);

            JSONArray items = new JSONArray();
            for (int i = 0; i < subscribe.getBusLines().size(); i++) {
                item = new JSONObject();
                item.put(Constants.KEY_ID, subscribe.getBusLine(i).getId());
                item.put(Constants.KEY_NAME, subscribe.getBusLine(i).getName());
                item.put(Constants.KEY_AHEAD, subscribe.getBusLine(i).getAhead());
                items.put(item);
            }
            data.put(Constants.KEY_BUS_LINES, items);

            array.put(data);

            sp.edit().putString(SP_KEY_DATA, array.toString()).apply();
        } catch (JSONException e) {
            Log.e(TAG, "[add] has exception", e);
        }
    }

    public static List<Subscribe> getAll() {
        List<Subscribe> subscribes = new ArrayList<>();
        SharedPreferences sp = getSubscribeSp();
        String str = sp.getString(SP_KEY_DATA, "[]");
        try {
            JSONArray array = new JSONArray(str);
            Subscribe subscribe;
            SubscribeBusLine busLine;
            JSONObject data, item;
            JSONArray items;
            for (int i = 0; i < array.length(); i++) {
                subscribe = new Subscribe();
                data = array.getJSONObject(i);

                item = data.getJSONObject(Constants.KEY_NOTIFICATION);
                subscribe.setWeekBit(item.getInt(Constants.KEY_DATE));
                subscribe.setStartTime(item.getString(Constants.KEY_START_TIME));
                subscribe.setEndTime(item.getString(Constants.KEY_END_TIME));

                item = data.getJSONObject(Constants.KEY_STATION);
                subscribe.setStation(new Station(
                        item.getString(Constants.KEY_ID), item.getString(Constants.KEY_NAME)));

                items = data.getJSONArray(Constants.KEY_BUS_LINES);
                for (int j = 0; j < items.length(); j++) {
                    item = items.getJSONObject(j);
                    busLine = new SubscribeBusLine(
                            item.getString(Constants.KEY_ID), item.getString(Constants.KEY_NAME));
                    busLine.setAhead(item.getInt(Constants.KEY_AHEAD));
                    subscribe.addBusLine(busLine);
                }
                subscribe.sortBusLines();
                subscribes.add(subscribe);
            }
        } catch (JSONException e) {
            Log.e(TAG, "[getAll] has exception", e);
        }
        return subscribes;
    }

    public static void delete(String stationId) {
        SharedPreferences sp = getSubscribeSp();
        String str = sp.getString(SP_KEY_DATA, "[]");
        try {
            JSONArray array = new JSONArray(str);
            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i).getJSONObject(Constants.KEY_STATION)
                        .getString(Constants.KEY_ID).equals(stationId)) {
                    array.remove(i);
                    break;
                }
            }

            sp.edit().putString(SP_KEY_DATA, array.toString()).apply();
        } catch (JSONException e) {
            Log.e(TAG, "[delete] has exception", e);
        }
    }
}
