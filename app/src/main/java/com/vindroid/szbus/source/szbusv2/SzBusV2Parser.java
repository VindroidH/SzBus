package com.vindroid.szbus.source.szbusv2;

import android.accounts.NetworkErrorException;
import android.text.TextUtils;
import android.util.Log;

import com.vindroid.szbus.App;
import com.vindroid.szbus.model.BusLine;
import com.vindroid.szbus.model.BusLineDetail;
import com.vindroid.szbus.model.BusLineRealTimeInfo;
import com.vindroid.szbus.model.SearchResult;
import com.vindroid.szbus.model.StationDetail;
import com.vindroid.szbus.source.BusParserInterface;
import com.vindroid.szbus.source.SzBus;
import com.vindroid.szbus.source.SzBusV2;
import com.vindroid.szbus.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SzBusV2Parser implements BusParserInterface {
    private static final String TAG;

    static {
        TAG = App.getTag(SzBusV2Parser.class.getSimpleName());
    }

    public SzBusV2Parser() {
        if (!SzBusV2.hasAllRoute()) {
            updateToken();
        }
    }

    @Override
    public SearchResult search(String keyword) throws Exception {
        if (!SzBusV2.hasAllRoute()) {
            pullAllRoute();
        }

        SearchResult search = new SearchResult();
        boolean hasRoute = false;
        for (int i = 0; i < SzBusV2.getAllRoute().length(); i++) {
            JSONObject v = SzBusV2.getAllRoute().getJSONObject(i);
            if (v.getString(SzBusV2.KEY_ROUTE_NAME).contains(keyword)) {
                hasRoute = true;
                BusLine busLine = new BusLine();
                busLine.setId(v.getString(SzBusV2.KEY_SEGMENT_ID));
                busLine.setName(v.getString(SzBusV2.KEY_ROUTE_NAME));
                busLine.setStartStationName(v.getString(SzBusV2.KEY_START_STATION));
                busLine.setEndStationName(v.getString(SzBusV2.KEY_END_STATION));
                search.addBusLine(busLine);
            }
        }

        boolean hasStation = false;
        String url = SzBusV2.URL_GET_SEARCH_STATION + keyword;
        Request request = getRequest(url);
        String data = getResponseBody(App.getHttpClient(), request);

        if (!checkResult(data)) {
            request = getRequest(url);
            data = getResponseBody(App.getHttpClient(), request);
        }

        JSONObject json = new JSONObject(data);
        JSONArray items = json.getJSONArray(SzBusV2.KEY_ITEMS);
        Log.d(TAG, "[search] test, station" + json);
        if (items.length() > 0) {
            hasStation = true;
            for (int i = 0; i < items.length(); i++) {
                JSONObject v = items.getJSONObject(i);
                StationDetail station = new StationDetail();
                station.setId(v.getString(SzBusV2.KEY_STATION_ID));
                station.setName(v.getString(SzBusV2.KEY_STATION_NAME));
                station.setAddress(v.getString(SzBusV2.KEY_STATION_ROAD) + "-" + v.getString(SzBusV2.KEY_STATION_DIRECT));
                search.addStation(station);
            }
        }

        if (!hasRoute && !hasStation) {
            search.setType(SearchResult.Type.None);
        } else if (hasRoute && hasStation) {
            search.setType(SearchResult.Type.Both);
        } else if (hasStation) {
            search.setType(SearchResult.Type.Station);
        } else {
            search.setType(SearchResult.Type.Bus);
        }
        return search;
    }

    @Override
    public BusLineDetail getBusLine(String busLineId) throws Exception {
        // TODO failed url? try use SzBusV2.URL_GET_ROUTE_STATION_LIST
        String url = SzBusV2.URL_GET_ROUTE_STAT + busLineId;
        Request request = getRequest(url);
        String data = getResponseBody(App.getHttpClient(), request);
        JSONObject json = new JSONObject(data);
        JSONArray items = json.getJSONArray(SzBusV2.KEY_ITEMS);
        for (int i = 0; i < items.length(); i++) {

        }
        return null;
    }

    @Override
    public BusLineRealTimeInfo getBusLineRealTimeInfo(String busLineId) throws Exception {
        return null;
    }

    @Override
    public StationDetail getStation(String stationId) throws Exception {
        return null;
    }

    private void updateToken() {
        String url = SzBusV2.URL_POST_TOKEN;
        Log.d(TAG, "[updateToken] url: " + url);
        try {
            JSONObject content = new JSONObject();
            content.put(SzBusV2.KEY_ACCESS_ID, SzBusV2.VALUE_ACCESS_ID);
            content.put(SzBusV2.KEY_ACCESS_SECRET, SzBusV2.VALUE_ACCESS_SECRET);
            Request request = postRequest(url, content.toString());
            String data = getResponseBody(App.getHttpClient(), request);
            JSONObject json = new JSONObject(data);
            if (SzBusV2.VALUE_RESULT_OK.equals(json.getString(SzBusV2.KEY_RESULT))) {
                String token = json.getJSONObject(SzBusV2.KEY_ITEMS).getString(SzBusV2.KEY_TOKEN);
                Log.d(TAG, "[updateToken] token: " + token);
                SzBusV2.setToken(token);
            } else {
                Log.w(TAG, "[updateToken] error msg: " + json.getString(SzBusV2.KEY_MESSAGE));
            }
        } catch (JSONException e) {
            Log.e(TAG, "[updateToken] json exception: " + e);
        } catch (NetworkErrorException e) {
            Log.e(TAG, "[updateToken] network error exception: " + e);
        }
    }

    private void pullAllRoute() throws NetworkErrorException {
        String url = SzBusV2.URL_GET_ALL_Route;
        Log.d(TAG, "[pullAllRoute] url: " + url);
        Request request = getRequest(url);
        String data = getResponseBody(App.getHttpClient(), request);

        if (!checkResult(data)) {
            request = getRequest(url);
            data = getResponseBody(App.getHttpClient(), request);
        }

        SzBusV2.setAllRoute(data);
    }

    private boolean checkResult(String data) {
        try {
            JSONObject json = new JSONObject(data);
            String result = json.getString(SzBusV2.KEY_RESULT);
            if (SzBusV2.VALUE_RESULT_OK.equals(result)) {
                return true;
            }
            if (SzBusV2.VALUE_RESULT_TOKEN_INVALID.equals(result)) {
                Log.d(TAG, "[checkResult] token invalid, update token");
                updateToken();
            } else {
                String msg = json.getString(SzBusV2.KEY_MESSAGE);
                Log.w(TAG, "[checkResult] result: " + result + ", message: " + msg);
            }
        } catch (JSONException e) {
            Log.e(TAG, "[checkResult] json exception: " + e);
        }
        return false;
    }

    private Request postRequest(String url, String data) {
        Headers headers = new Headers.Builder()
                .add(SzBusV2.KEY_TOKEN, SzBusV2.getToken())
                .build();
        RequestBody body = RequestBody.create(
                data, MediaType.parse("application/json; charset=utf-8"));
        return new Request.Builder().headers(headers).url(url).post(body).build();
    }

    private Request getRequest(String url) {
        Headers headers = new Headers.Builder()
                .add(SzBusV2.KEY_TOKEN, SzBusV2.getToken())
                .build();
        return new Request.Builder().headers(headers).url(url).get().build();
    }

    private String getResponseBody(OkHttpClient client, Request request) throws NetworkErrorException {
        return getResponseBody(client, request, true);
    }

    private String getResponseBody(OkHttpClient client, Request request, boolean retry) throws NetworkErrorException {
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body() == null ? "" : response.body().string();
            } else if (retry)
                return getResponseBody(client, request, false);
        } catch (Exception e) {
            e.printStackTrace();
            if (retry)
                return getResponseBody(client, request, false);
        }
        throw new NetworkErrorException();
    }
}
