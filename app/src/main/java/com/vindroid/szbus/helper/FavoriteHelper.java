package com.vindroid.szbus.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.vindroid.szbus.App;
import com.vindroid.szbus.model.Favorite;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.Station;
import com.vindroid.szbus.source.DataSource;
import com.vindroid.szbus.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoriteHelper {
    private static final String TAG;

    private static final String SP_NAME = "favorite_sp";
    private static final String SP_KEY_DATA_SZGJ = "data";
    private static final String SP_KEY_DATA_SZXING = "data_szxing";

    private static SharedPreferences mFavoriteSp = null;

    static {
        TAG = App.getTag(FavoriteHelper.class.getSimpleName());
    }

    private static SharedPreferences getFavoriteSp() {
        if (mFavoriteSp == null) {
            mFavoriteSp = App.getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return mFavoriteSp;
    }

    public static void add(Station station, List<InComingBusLine> busLines) {
        Log.d(TAG, "[add] station name: " + station.getName() +
                ", bus line count: " + busLines.size());
        SharedPreferences sp = getFavoriteSp();
        String key = SP_KEY_DATA_SZGJ;
        if (DataSource.SOURCE_SZXING.equals(DataSource.getDataSource())) {
            key = SP_KEY_DATA_SZXING;
        }
        String str = sp.getString(key, "[]");
        try {
            JSONArray array = new JSONArray(str);
            int index = -1;
            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i).getJSONObject(Constants.KEY_STATION)
                        .getString(Constants.KEY_ID).equals(station.getId())) {
                    index = array.getJSONObject(i).getInt(Constants.KEY_INDEX);
                    array.remove(i);
                    break;
                }
            }

            JSONObject data = new JSONObject();
            data.put(Constants.KEY_INDEX, index == -1 ? array.length() : index);

            JSONObject item = new JSONObject();
            item.put(Constants.KEY_ID, station.getId());
            item.put(Constants.KEY_NAME, station.getName());
            data.put(Constants.KEY_STATION, item);

            JSONArray items = new JSONArray();
            for (int i = 0; i < busLines.size(); i++) {
                item = new JSONObject();
                item.put(Constants.KEY_ID, busLines.get(i).getId());
                item.put(Constants.KEY_NAME, busLines.get(i).getName());
                items.put(item);
            }
            data.put(Constants.KEY_BUS_LINES, items);

            array.put(data);

            sp.edit().putString(key, array.toString()).apply();
        } catch (JSONException e) {
            Log.e(TAG, "[add] has exception", e);
        }
    }

    public static List<Favorite> getAll() {
        List<Favorite> favoriteList = new ArrayList<>();
        SharedPreferences sp = getFavoriteSp();
        String key = SP_KEY_DATA_SZGJ;
        if (DataSource.SOURCE_SZXING.equals(DataSource.getDataSource())) {
            key = SP_KEY_DATA_SZXING;
        }
        String str = sp.getString(key, "[]");
        try {
            JSONArray array = new JSONArray(str);
            Favorite[] favorites = new Favorite[array.length()];
            Favorite favorite;
            InComingBusLine busLine;
            JSONObject data, item;
            JSONArray items;
            for (int i = 0; i < array.length(); i++) {
                favorite = new Favorite();
                data = array.getJSONObject(i);

                favorite.setIndex(data.getInt(Constants.KEY_INDEX));

                item = data.getJSONObject(Constants.KEY_STATION);
                favorite.setStation(new Station(
                        item.getString(Constants.KEY_ID), item.getString(Constants.KEY_NAME)));

                items = data.getJSONArray(Constants.KEY_BUS_LINES);
                for (int j = 0; j < items.length(); j++) {
                    item = items.getJSONObject(j);
                    busLine = new InComingBusLine(
                            item.getString(Constants.KEY_ID), item.getString(Constants.KEY_NAME));
                    favorite.addBusLine(busLine);
                }
                favorite.sortBusLines();
                favorites[data.getInt(Constants.KEY_INDEX)] = favorite;
            }
            favoriteList = Arrays.asList(favorites);
        } catch (JSONException e) {
            Log.e(TAG, "[getAll] has exception", e);
        }
        return favoriteList;
    }

    public static void delete(String stationId) {
        Log.d(TAG, "[delete] station id: " + stationId);
        SharedPreferences sp = getFavoriteSp();
        String key = SP_KEY_DATA_SZGJ;
        if (DataSource.SOURCE_SZXING.equals(DataSource.getDataSource())) {
            key = SP_KEY_DATA_SZXING;
        }
        String str = sp.getString(key, "[]");
        try {
            JSONArray array = new JSONArray(str);
            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i).getJSONObject(Constants.KEY_STATION)
                        .getString(Constants.KEY_ID).equals(stationId)) {
                    array.remove(i);
                    break;
                }
            }

            sp.edit().putString(key, array.toString()).apply();
        } catch (JSONException e) {
            Log.e(TAG, "[delete] has exception", e);
        }
    }

    public static void moveUp(String stationId) {
        Log.d(TAG, "[moveUp] station id: " + stationId);
        move(stationId, -1);
    }

    public static void moveDown(String stationId) {
        Log.d(TAG, "[moveDown] station id: " + stationId);
        move(stationId, 1);
    }

    private static void move(String stationId, int moving) {
        SharedPreferences sp = getFavoriteSp();
        String key = SP_KEY_DATA_SZGJ;
        if (DataSource.SOURCE_SZXING.equals(DataSource.getDataSource())) {
            key = SP_KEY_DATA_SZXING;
        }
        String str = sp.getString(key, "[]");
        try {
            int movedIndex = -1;
            JSONArray array = new JSONArray(str);
            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i).getJSONObject(Constants.KEY_STATION)
                        .getString(Constants.KEY_ID).equals(stationId)) {
                    movedIndex = array.getJSONObject(i).getInt(Constants.KEY_INDEX) + moving;
                    Log.d(TAG, "test2 i " + movedIndex);
                    array.getJSONObject(i).put(Constants.KEY_INDEX, movedIndex);
                    break;
                }
            }

            if (movedIndex == -1) {
                Log.e(TAG, "[move] not found item: " + stationId);
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                String id = array.getJSONObject(i).getJSONObject(Constants.KEY_STATION)
                        .getString(Constants.KEY_ID);
                int index = array.getJSONObject(i).getInt(Constants.KEY_INDEX);
                if (index == movedIndex && !id.equals(stationId)) {
                    array.getJSONObject(i).put(Constants.KEY_INDEX, index - moving);
                    break;
                }
            }

            sp.edit().putString(key, array.toString()).apply();
        } catch (JSONException e) {
            Log.e(TAG, "[move] has exception", e);
        }
    }
}
