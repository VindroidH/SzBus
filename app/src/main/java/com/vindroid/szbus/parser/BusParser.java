package com.vindroid.szbus.parser;

import android.accounts.NetworkErrorException;
import android.text.TextUtils;
import android.util.Log;

import com.vindroid.szbus.App;
import com.vindroid.szbus.R;
import com.vindroid.szbus.model.BusLine;
import com.vindroid.szbus.model.BusLineDetail;
import com.vindroid.szbus.model.BusLineRealTimeInfo;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.RunningBus;
import com.vindroid.szbus.model.SearchResult;
import com.vindroid.szbus.model.StationDetail;
import com.vindroid.szbus.utils.Constants;
import com.vindroid.szbus.utils.StringUtils;
import com.vindroid.szbus.utils.SzBus;
import com.vindroid.szbus.utils.SzSubway;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BusParser {
    private static final String TAG;

    private final String KEY_USER_AGENT = "User-Agent";
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.82 Safari/537.36";

    static {
        TAG = App.getTag(BusParser.class.getSimpleName());
    }

    public SearchResult search(String keyword) throws NetworkErrorException {
        SearchResult search = new SearchResult();
        if (TextUtils.isEmpty(keyword)) return search;
        String url = SzBus.URL_SEARCH + keyword;
        Log.d(TAG, "[search] url: " + url);
        Request request = new Request.Builder().url(url).build();
        String html = getResponseBody(App.getHttpClient(), request);
        List<Node> stationNodes = new Node(html).list("div.busstation > div > a");
        List<Node> busNodes = new Node(html).list("div.buslinediv > div > a");
        Log.d(TAG, "[search] station count: " + stationNodes.size() + ", bus count: " + busNodes.size());

        if (stationNodes.size() > 0 && busNodes.size() == 0) {
            search.setType(SearchResult.Type.Station);
        } else if (stationNodes.size() == 0 && busNodes.size() > 0) {
            search.setType(SearchResult.Type.Bus);
        } else if (stationNodes.size() > 0 && busNodes.size() > 0) {
            search.setType(SearchResult.Type.Both);
        } else {
            search.setType(SearchResult.Type.None);
        }

        StationDetail station;
        for (Node node : stationNodes) {
            station = new StationDetail();
            station.setId(node.hrefWithSubString(SzBus.URL_STATION.length()));
            station.setName(node.getChild("div.stationline > div.stationname").text());
            station.setAddress(node.getChild("div.stationline > div.stationposition > span.locposition").text());
            List<Node> lineNodes = node.list("div.stationline > div.stationinfo > div");
            InComingBusLine busLine;
            for (Node lineNode : lineNodes) {
                if (!TextUtils.isEmpty(lineNode.text())) {
                    busLine = new InComingBusLine();
                    busLine.setName(lineNode.text());
                    station.addBusLine(busLine);
                }
            }
            if (station.getBusLines().size() > 0) {
                search.addStation(station);
            } else {
                Log.d(TAG, "[search] " + station.getName() + " this station without bus line, ignore it");
            }
        }

        BusLine busLine;
        for (Node node : busNodes) {
            busLine = new BusLine();
            busLine.setId(node.hrefWithSubString(SzBus.URL_BUS.length()));
            busLine.setName(node.getChild("div.buslineinfo > div.buslinename").text());
            String text = node.getChild("div.buslineinfo > div.buslineto").text();
            String[] stations = text.split(Constants.BUS_LINE_SPLIT_REGEX);
            if (stations.length == 2) {
                busLine.setStartStationName(stations[0]);
                busLine.setEndStationName(stations[1]);
            } else {
                Log.w(TAG, "[search] bus line, the data structure changed");
            }
            search.addBusLine(busLine);
        }
        return search;
    }

    public BusLineDetail getBusLine(String busLineId) throws NetworkErrorException {
        BusLineDetail busLine = new BusLineDetail();
        busLine.setId(busLineId);
        String url = SzBus.URL_BUS + busLineId;
        Log.d(TAG, "[getBusLine] url: " + url);
        Request request = new Request.Builder().url(url).build();
        String html = getResponseBody(App.getHttpClient(), request);
        Node node = new Node(html);
        busLine.setName(node.text("div.topinfodiv > div.line1 > div.towhere > span"));
        List<Node> info = node.list("div.topinfodiv > div");
        if (info.size() >= 2) {
            busLine.setEndStationName(info.get(1).text());
        } else {
            busLine.setEndStationName(App.getStringById(R.string.unavailable));
            Log.w(TAG, "[getBusLine] the end station data structure changed");
        }
        List<Node> startEndTimes = node.list(
                "div.topinfodiv > div.line2 > div.line2left > div.startend > span");
        if (startEndTimes.size() >= 4) {
            busLine.setFirstTime(startEndTimes.get(1).text());
            busLine.setLastTime(startEndTimes.get(3).text());
        } else {
            busLine.setFirstTime(App.getStringById(R.string.unavailable));
            busLine.setLastTime(App.getStringById(R.string.unavailable));
            Log.w(TAG, "[getBusLine] the first & last time data structure changed");
        }
        busLine.setReverseId(node.hrefWithSubString("div.topinfodiv > div.line3 > div.switchdirection > a",
                SzBus.URL_BUS.length()));

        List<Node> busLines = node.list("div.busline > div.stationinfo");
        for (Node line : busLines) {
            String stationId = line.attr("div.stationdetail", "data-sid");
            String stationName = line.text("div.stationname");
            busLine.addStation(stationId, stationName);

            List<String> subways = new ArrayList<>();
            for (String id : SzSubway.SUBWAYS) {
                String text = line.text("a.subway" + id);
                if (!TextUtils.isEmpty(text)) {
                    subways.add(text);
                }
            }
            busLine.addStationSubway(stationId, subways);
        }
        return busLine;
    }

    public BusLineRealTimeInfo getBusLineRealTimeInfo(String busLineId) throws NetworkErrorException, JSONException {
        BusLineRealTimeInfo runningInfo = new BusLineRealTimeInfo();
        String api = SzBus.API_BUS_LINE + busLineId;
        Log.d(TAG, "[getBusLineRealTimeInfo] url: " + api);
        Request request = new Request.Builder().url(api).build();
        String html = getResponseBody(App.getHttpClient(), request);
        JSONObject object = new JSONObject(html);
        JSONObject data = object.getJSONObject("data");
        runningInfo.setNextDepartTime(data.getString("nextShift"));

        JSONObject standInfo = data.getJSONObject("standInfo");
        RunningBus bus;
        for (Iterator<String> it = standInfo.keys(); it.hasNext(); ) {
            String key = it.next();
            JSONArray values = standInfo.getJSONArray(key); // 只取一辆车，多辆同时进站无意义
            if (values.length() == 0) continue;
            JSONObject value = values.getJSONObject(0);
            bus = new RunningBus();
            bus.setInfo(value.getString("busInfo"));
            bus.setInTime(value.getString("inTime"));
            bus.setStationId(key);
            runningInfo.addRunningBus(bus);
        }
        return runningInfo;
    }

    public StationDetail getStation(String stationId) throws NetworkErrorException {
        StationDetail station = new StationDetail();
        station.setId(stationId);
        String url = SzBus.URL_STATION + stationId;
        Log.d(TAG, "[getStation] url: " + url);
        Request request = new Request.Builder().url(url).build();
        String html = getResponseBody(App.getHttpClient(), request);
        Node node = new Node(html);
        station.setName(node.text("div.busnamediv > div.stationname"));
        station.setAddress(node.text("div.busnamediv > div.addr"));

        List<Node> busNodes = node.list("div.content > a");
        InComingBusLine busLine;
        for (Node item : busNodes) {
            busLine = new InComingBusLine();
            busLine.setId(item.hrefWithSubString(SzBus.URL_BUS.length()));
            busLine.setName(item.getChild("div.busdiv > div.left > div.line1 > div.busname").text());
            String text = item.getChild("div.busdiv > div.left > div.todestination").text();
            busLine.setEndStationName(StringUtils.substring(text, Constants.BUS_LINE_TO_HEADER.length()));

            String status = item.getChild("div.busdiv > div.right > div.status").text();
            if (Constants.BUS_NO_COMING.equals(status) || Constants.BUS_NO_COMING2.equals(status)) {
                busLine.setComing(InComingBusLine.COMING_NO);
            } else if (Constants.BUS_COMING.equals(status)) {
                busLine.setComing(InComingBusLine.COMING_NOW);
            } else {
                if (status.startsWith(Constants.COMING_HEADER)) {
                    status = StringUtils.substring(status,
                            Constants.COMING_HEADER.length(),
                            status.length() - Constants.COMING_FOOTER.length());
                    try {
                        busLine.setComing(Integer.parseInt(status));
                    } catch (NumberFormatException e) {
                        busLine.setComing(InComingBusLine.COMING_ERR);
                    }
                } else {
                    busLine.setComing(InComingBusLine.COMING_ERR);
                }
            }
            station.addBusLine(busLine);
        }
        return station;
    }

    private String getResponseBody(OkHttpClient client, Request request) throws NetworkErrorException {
        return getResponseBody(client, request, true);
    }

    private String getResponseBody(OkHttpClient client, Request request, boolean retry) throws NetworkErrorException {
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                byte[] bodyBytes = response.body().bytes();
                String body = new String(bodyBytes);
                Matcher m = Pattern.compile("charset=([\\w\\-]+)").matcher(body);
                if (m.find()) {
                    body = new String(bodyBytes, m.group(1));
                }
                return body;
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
