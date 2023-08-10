package com.vindroid.szbus.helper;

import android.content.SharedPreferences;

import com.vindroid.szbus.App;
import com.vindroid.szbus.model.BusLine;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.Station;
import com.vindroid.szbus.model.Subscribe;
import com.vindroid.szbus.model.SubscribeBusLine;
import com.vindroid.szbus.utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubscribeHelper {
    // TODO add error notify time
    private static final String SP_KEY_STATIONS = "stations";
    private static final String SP_KEY_BUS_LINES = "bus_line_%s";
    private static final String SP_KEY_START_TIME = "start_time_%s";
    private static final String SP_KEY_END_TIME = "end_time_%s";
    private static final String SP_KEY_AHEAD = "ahead_%s_%s";

    public static void add(Station station, List<InComingBusLine> busLineList,
                           Map<String, Integer> extraMap, String startTime, String endTime, String dateType) {
        SharedPreferences sp = App.getSubscribeSp();
        SharedPreferences.Editor editor = sp.edit();

        Set<String> stationSet = new HashSet<>(sp.getStringSet(SP_KEY_STATIONS, new HashSet<>()));
        Iterator<String> iterator = stationSet.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            try {
                if (item.split(Constants.SPLIT_TEXT)[0].equals(station.getId())) {
                    iterator.remove();
                    break;
                }
            } catch (IndexOutOfBoundsException ignore) {
                iterator.remove();
            }
        }
        stationSet.add(station.getId() + Constants.SPLIT_TEXT + station.getName());
        editor.putStringSet(SP_KEY_STATIONS, stationSet);

        Set<String> busLineSet = new HashSet<>();
        for (BusLine busLine : busLineList) {
            busLineSet.add(busLine.getId() + Constants.SPLIT_TEXT + busLine.getName());
            int ahead = extraMap.get(busLine.getId());
            editor.putInt(String.format(SP_KEY_AHEAD, station.getId(), busLine.getId()), ahead);
        }
        editor.putStringSet(String.format(SP_KEY_BUS_LINES, station.getId()), busLineSet);

        editor.putString(String.format(SP_KEY_START_TIME, station.getId()), startTime);
        editor.putString(String.format(SP_KEY_END_TIME, station.getId()), endTime);
        editor.apply();
    }

    public static List<Subscribe> getAll() {
        List<Subscribe> subscribeList = new ArrayList<>();
        Subscribe subscribe;
        Station station;
        SubscribeBusLine busLine;
        String[] args;
        String startTime, endTime;
        SharedPreferences sp = App.getSubscribeSp();
        Set<String> stations = sp.getStringSet(SP_KEY_STATIONS, new HashSet<>());
        for (String item : stations) {
            args = item.split(Constants.SPLIT_TEXT);
            station = new Station(args[0], args[1]);
            startTime = sp.getString(String.format(SP_KEY_START_TIME, station.getId()), "");
            endTime = sp.getString(String.format(SP_KEY_END_TIME, station.getId()), "");

            subscribe = new Subscribe();
            subscribe.setStation(station);
            subscribe.setStartTime(startTime);
            subscribe.setEndTime(endTime);

            String key = String.format(SP_KEY_BUS_LINES, station.getId());
            Set<String> busLines = sp.getStringSet(key, new HashSet<>());
            for (String item2 : busLines) {
                args = item2.split(Constants.SPLIT_TEXT);
                busLine = new SubscribeBusLine(args[0], args[1]);
                busLine.setAheadOfStation(sp.getInt(
                        String.format(SP_KEY_AHEAD, station.getId(), busLine.getId()),
                        Constants.DEFAULT_AHEAD));
                subscribe.addBusLine(busLine);
            }
            subscribeList.add(subscribe);
        }
        return subscribeList;
    }

    public static void delete(String stationId) {
        SharedPreferences sp = App.getSubscribeSp();
        SharedPreferences.Editor editor = sp.edit();
        Set<String> stationIds = new HashSet<>(sp.getStringSet(SP_KEY_STATIONS, new HashSet<>()));
        Set<String> busLineIds = new HashSet<>(
                sp.getStringSet(String.format(SP_KEY_BUS_LINES, stationId), new HashSet<>()));
        stationIds.remove(stationId);
        editor.putStringSet(SP_KEY_STATIONS, stationIds);
        editor.remove(String.format(SP_KEY_BUS_LINES, stationId));
        editor.remove(String.format(SP_KEY_START_TIME, stationId));
        editor.remove(String.format(SP_KEY_END_TIME, stationId));
        for (String busLineId : busLineIds) {
            editor.remove(String.format(SP_KEY_AHEAD, stationId, busLineId));
        }
        editor.apply();
    }
}
