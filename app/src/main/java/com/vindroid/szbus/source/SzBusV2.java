package com.vindroid.szbus.source;

import android.util.Log;

import com.vindroid.szbus.App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SzBusV2 {
    private static final String TAG;

    static {
        TAG = App.getTag(SzBusV2.class.getSimpleName());
    }

    public static final String URL_SITE = "https://app.szgjgs.com:58050";
    /**
     * header: token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxMTk0MDEiLCJleHAiOjE3MDk3MjEyNzYsImlhdCI6MTcwOTcxMzc3Nn0.qCHLJXIcW2iG5X4fsNYbOSbkzCbhMBzFRvv6vTdc8mY
     * body: {"accessId":"119401","accessSecret":"48ce9696a22611e98e7e00163e087c"}
     */
    public static final String URL_POST_TOKEN = "/BusService/MiniApps/QueryToken";
    public static final String URL_GET_ALL_Route = URL_SITE + "/BusService/MiniApps/Require_AllRouteData?";
    public static final String URL_GET_RUNNING_BUS = URL_SITE + "/BusService/MiniApps/Query_BusBySegmentID?segmentId=";
    public static final String URL_GET_TIMETABLE = URL_SITE + "/BusService/MiniApps/Query_TimetableBySegmentID?segmentId=";
    public static final String URL_GET_ROUTE_STAT = "/BusService/MiniApps/Require_RouteStatData?routeId=";
    public static final String URL_GET_SEARCH_STATION = URL_SITE + "/BusService/MiniApps/Query_ByStationName?stationName=";
    public static final String URL_GET_STATION_INFO = URL_SITE + "/BusService/MiniApps/Query_ByStationID?stationId=";
    public static final String URL_GET_ROUTE_STATION_LIST = URL_SITE + "/BusService/MiniApps/Query_CrowdBySegmentID?segmentId=";

    public static final String KEY_TOKEN = "token";
    public static final String KEY_ACCESS_ID = "accessId";
    public static final String KEY_ACCESS_SECRET = "accessSecret";
    public static final String KEY_RESULT = "result";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_ITEMS = "items";
    public static final String KEY_SEGMENT_ID = "segmentId";
    public static final String KEY_ROUTE_ID = "routeId";
    public static final String KEY_ROUTE_NAME = "routeName";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_END_TIME = "endTime";
    public static final String KEY_START_STATION = "startStation";
    public static final String KEY_END_STATION = "endStation";
    public static final String KEY_STATION_ID = "stationId";
    public static final String KEY_STATION_NAME = "stationName";
    public static final String KEY_STATION_ROAD = "stationRoad";
    public static final String KEY_STATION_DIRECT = "stationDirect";

    public static final String VALUE_ACCESS_ID = "119401"; // for query token
    public static final String VALUE_ACCESS_SECRET = "48ce9696a22611e98e7e00163e087c"; // for query token
    public static final String VALUE_RESULT_OK = "0";
    public static final String VALUE_RESULT_TOKEN_INVALID = "2";

    private static String sToken;
    private static JSONArray sAllRoute;

    public static void setAllRoute(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            if ("0".equals(obj.getString("result"))) {
                sAllRoute = obj.getJSONArray("items");
            }
        } catch (JSONException e) {
            Log.e(TAG, "[setAllRoute] json exception, json: " + json, e);
        }
    }

    public static JSONArray getAllRoute() {
        return sAllRoute;
    }

    public static boolean hasAllRoute() {
        return sAllRoute != null && sAllRoute.length() > 0;
    }

    public static void setToken(String token) {
        sToken = token;
    }

    public static String getToken() {
        return sToken;
    }

}
