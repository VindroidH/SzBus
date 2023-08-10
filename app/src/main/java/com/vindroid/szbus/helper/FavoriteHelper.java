package com.vindroid.szbus.helper;

import android.content.SharedPreferences;
import android.util.Log;

import com.vindroid.szbus.App;
import com.vindroid.szbus.model.BusLine;
import com.vindroid.szbus.model.Favorite;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.Station;
import com.vindroid.szbus.utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FavoriteHelper {
    private static final String TAG;
    private static final String SP_KEY_INDEX = "index";
    private static final String SP_KEY_STATIONS = "stations";
    private static final String SP_KEY_BUS_LINE_HEADER = "bus_line_at_%s";

    static {
        TAG = App.getTag(FavoriteHelper.class.getSimpleName());
    }

    public static LinkedList<Favorite> getAll() {
        LinkedList<Favorite> favorites = new LinkedList<>();
        List<Favorite> caches = new ArrayList<>();

        SharedPreferences sp = App.getFavoriteSp();
        Set<String> stationSet = sp.getStringSet(SP_KEY_STATIONS, new HashSet<>());
        String[] args;
        Favorite favorite;
        Set<String> busLineSet;
        for (String value : stationSet) {
            args = value.split(Constants.SPLIT_TEXT);
            favorite = new Favorite();
            favorite.setStation(new Station(args[0], args[1]));

            String key = String.format(SP_KEY_BUS_LINE_HEADER, args[0]);
            busLineSet = sp.getStringSet(key, new HashSet<>());
            for (String value2 : busLineSet) {
                args = value2.split(Constants.SPLIT_TEXT);
                favorite.addBusLine(new InComingBusLine(args[0], args[1]));
            }
            caches.add(favorite);
        }

        // get sort index
        Set<String> indexSet = sp.getStringSet(SP_KEY_INDEX, new HashSet<>());
        String[] indexArgs = new String[indexSet.size()];
        for (String value : indexSet) {
            args = value.split(Constants.SPLIT_TEXT);
            String stationId = args[0];
            int index = Integer.parseInt(args[1]);
            indexArgs[index] = stationId;
        }

        // sort by index
        for (String id : indexArgs) {
            for (int i = 0; i < caches.size(); i++) {
                Favorite value = caches.get(i);
                if (value.getStation().getId().equals(id)) {
                    favorites.add(value);
                }
            }
        }

        return favorites;
    }

    public static void add(Station station, List<InComingBusLine> busLineList) {
        Log.d(TAG, "[add] station: " + station.getId() + ", " + station.getName() + ", bus line count: " + busLineList.size());
        SharedPreferences sp = App.getFavoriteSp();
        SharedPreferences.Editor editor = sp.edit();
        Set<String> array, newArray;

        // update station
        array = sp.getStringSet(SP_KEY_STATIONS, new HashSet<>());
        newArray = new HashSet<>();
        for (String value : array) {
            if (!value.startsWith(station.getId())) {
                newArray.add(value);
            }
        }
        newArray.add(station.getId() + Constants.SPLIT_TEXT + station.getName());
        editor.putStringSet(SP_KEY_STATIONS, newArray);

        // update bus line, replace all data without retaining existing data
        newArray = new HashSet<>();
        for (InComingBusLine value : busLineList) {
            newArray.add(value.getId() + Constants.SPLIT_TEXT + value.getName());
        }
        editor.putStringSet(String.format(SP_KEY_BUS_LINE_HEADER, station.getId()), newArray);

        // update index
        array = sp.getStringSet(SP_KEY_INDEX, new HashSet<>());
        newArray = new HashSet<>();
        int index = array.size();
        for (String value : array) {
            if (value.startsWith(station.getId())) {
                index = Integer.parseInt(value.split(Constants.SPLIT_TEXT)[1]);
            } else {
                newArray.add(value);
            }
        }
        newArray.add(station.getId() + Constants.SPLIT_TEXT + index);
        editor.putStringSet(SP_KEY_INDEX, newArray);

        editor.apply();
    }

    public static void delete(Station station, BusLine busLine) {

    }

    // type: UP, DOWN
    public static void setIndex(Station station, String type) {

    }
}
