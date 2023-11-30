package com.vindroid.szbus.source.szxing;

import android.accounts.NetworkErrorException;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.icitymobile.smartmachine.JniEncode;
import com.vindroid.szbus.App;
import com.vindroid.szbus.model.BusLineDetail;
import com.vindroid.szbus.model.BusLineRealTimeInfo;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.RunningBus;
import com.vindroid.szbus.model.SearchResult;
import com.vindroid.szbus.model.Station;
import com.vindroid.szbus.model.StationDetail;
import com.vindroid.szbus.source.ICommonApi;
import com.vindroid.szbus.utils.StringKit;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
lists/LAST_UPDATE_TIME.JSON
lists/line.json
lists/station.json
sjGetStationRTInfo?stationID=
sjGetLineRTInfo?lineID=%s
sjGetLineInfo?lineID=%s
sjGetLineDetailInfo?lineID=%s
sjGetPOIListByKeyword?keyword=%s&category=all&limit=%d&longitude=%d&latitude=%d


406 袁家浜 10000110
"406路":[{"Category":50,"Direction":"袁家浜","ID":"10000110"},{"Category":50,"Direction":"苏州站南广场公交枢纽","ID":"10000165"}]

公交寒山寺南站 10000279
"公交寒山寺南站":{"CATEGORY":50,"EName":"5BD25C715BFA5357","INFO":[{"Code":"DPP","lat":31.31327,"lon":120.58051,"Position":"姑苏区金门路南","ID":"10000088"},{"Code":"CZX","lat":31.31347,"lon":120.5804,"Position":"姑苏区金门路北","ID":"10000279"}]}
 */
public class BusApi implements ICommonApi {
    private static final String TAG;

    // data from SZXing_v2.8.5
    public final static String URL = "http://szxing-tel.icitymobile.mobi:18008/";
    public final static String URL_RT = "http://szxing-tel.icitymobile.mobi:18508/";

    private final String SZXING = "Model: Android; Software Version: %s; Client Version: %s-Android-%s-%s; Lat: %s; Lon: %s ";
    private final String SZXING_SYS_VERSION = Build.VERSION.RELEASE;
    private final String SZXING_CLIENT_VERSION_HEADER = "iCitySZXing";
    private final String SZXING_CLIENT_VERSION_FOOTER = "Yulong";
    private final String SZXING_UDID = "IMEINNNNNNNNNNNNNNN-IMSI460NNNNNNNNNNNN";
    private final String SZXING_CLIENT_VERSION_NAME = "3.2.0";
    private final String SZXING_CLIENT_VERSION_CODE = "45";
    // 寒山寺 120.574947,31.316435 http://api.map.baidu.com/lbsapi/getpoint/index.html
    private final int SZXING_LAT = 120;
    private final int SZXING_LON = 31;

    private final String PATH_LAST_UPDATE_TIME = "lists/LAST_UPDATE_TIME.JSON";

    private final String PATH_LINE_FILE = "lists/line.json";
    private final String PATH_STATION_FILE = "lists/station.json";

    private final String PATH_LINE_INFO = "sjGetLineInfo?lineID=";
    private final String PATH_LINE_DETAIL_INFO = "sjGetLineDetailInfo?lineID=";
    private final String PATH_LINE_RT_INFO = "sjGetLineRTInfo?lineID=";

    private final String PATH_STATION_RT_INFO = "sjGetStationRTInfo?stationID=";

    private final String SZXING_FOLDER = "SZXing";
    private final String STATION_FILE = "station.json";
    private final String LINE_FILE = "line.json";

    private final String NS = null;

    static {
        TAG = App.getTag(BusApi.class.getSimpleName());
    }

    @Override
    public SearchResult search(String keyword) throws Exception {
        // search from station.json and line.json
        return null;
    }

    @Override
    public BusLineDetail getBusLine(String busLineId) throws Exception {
        BusLineDetail busLine = new BusLineDetail();
        busLine.setId(busLineId);

        String xml = getLineDetailInfo(busLineId);
        InputStream is = StringKit.string2Stream(xml);
        XmlPullParser parser = getParser(is);
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "LineInfo");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            if ("FirstTime".equals(parser.getName())) {
                busLine.setFirstTime(parser.getText());
                parser.nextTag();
            } else if ("LastTime".equals(parser.getName())) {
                busLine.setLastTime(parser.getText());
                parser.nextTag();
            } else if ("FirstStationName".equals(parser.getName())) {
                busLine.setStartStationName(parser.getText());
                parser.nextTag();
            } else if ("LastStationName".equals(parser.getName())) {
                busLine.setEndStationName(parser.getText());
                parser.nextTag();
            } else if ("LineName".equals(parser.getName())) {
                busLine.setName(parser.getText());
                parser.nextTag();
            } else {
                skip(parser);
            }
        }
        if (is != null) is.close();

        xml = getLineInfo(busLineId);
        is = StringKit.string2Stream(xml);
        parser = getParser(is);
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "StationList");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            parser.require(XmlPullParser.START_TAG, null, "Station");
            String stationId = "";
            String stationName = "";
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.next() != XmlPullParser.START_TAG) {
                    continue;
                }
                if ("StationCName".equals(parser.getName())) {
                    stationName = parser.getText();
                    parser.nextTag();
                } else if ("ID".equals(parser.getName())) {
                    stationId = parser.getText();
                    parser.nextTag();
                } else {
                    skip(parser);
                }
            }
            busLine.addStation(stationId, stationName);
        }
        if (is != null) is.close();
        return busLine;
    }

    @Override
    public BusLineRealTimeInfo getBusLineRealTimeInfo(String busLineId) throws Exception {
        BusLineRealTimeInfo runningInfo = new BusLineRealTimeInfo();
        String xml = getLineRT(busLineId);
        InputStream is = StringKit.string2Stream(xml);
        XmlPullParser parser = getParser(is);
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "StationList");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            RunningBus bus = new RunningBus();
            parser.require(XmlPullParser.START_TAG, null, "Station");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                if ("ID".equals(parser.getName())) {
                    bus.setStationId(parser.getText());
                    parser.nextTag();
                } else if ("InTime".equals(parser.getName())) {
                    bus.setInTime(parser.getText());
                    parser.nextTag();
                } else if ("BusInfo".equals(parser.getName())) {
                    bus.setInfo(parser.getText());
                    parser.nextTag();
                } else {
                    skip(parser);
                }
            }
            if (!TextUtils.isEmpty(bus.getInTime()) && !TextUtils.isEmpty(bus.getInfo())) {
                runningInfo.addRunningBus(bus);
            }
        }
        return runningInfo;
    }

    @Override
    public StationDetail getStation(String stationId) throws Exception {
        StationDetail station = new StationDetail();
        station.setId(stationId);

        // TODO set name & address from station.json

        String xml = getStationRT(stationId);
        InputStream is = StringKit.string2Stream(xml);
        XmlPullParser parser = getParser(is);
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "LineList");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            InComingBusLine busLine = new InComingBusLine();
            parser.require(XmlPullParser.START_TAG, null, "Line");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                if ("Name".equals(parser.getName())) {
                    busLine.setName(parser.getText());
                    parser.nextTag();
                } else if ("Direction".equals(parser.getName())) {
                    busLine.setEndStationName(parser.getText());
                    parser.nextTag();
                } else if ("ID".equals(parser.getName())) {
                    busLine.setId(parser.getText());
                    parser.nextTag();
                } else if ("Distince".equals(parser.getName())) {
                    try {
                        busLine.setComing(Integer.parseInt(parser.getText()));
                    } catch (Exception e) {
                        Log.e(TAG, "Distince error value: " + parser.getText());
                        busLine.setComing(InComingBusLine.COMING_ERR);
                    }
                    parser.nextTag();
                } else {
                    skip(parser);
                }
            }
            station.addBusLine(busLine);
        }
        return station;
    }

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>> SZXing functions >>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private String encode(String url, String path) {
        String deviceInfo = String.format(SZXING,
                SZXING_SYS_VERSION,
                SZXING_CLIENT_VERSION_HEADER, SZXING_CLIENT_VERSION_NAME, SZXING_CLIENT_VERSION_FOOTER,
                SZXING_LAT, SZXING_LON);
        return url + JniEncode.getEncodeUrl(path, SZXING_UDID, deviceInfo);
    }

    public String getLastUpdateTime() throws NetworkErrorException {
        String url = encode(URL, PATH_LAST_UPDATE_TIME);
        Log.d(TAG, "getLastUpdateTime " + url);
        Request request = new Request.Builder().url(url).build();
        return getResponseBody(App.getHttpClient(), request);
    }

    public String getStationFile() throws NetworkErrorException {
        String url = encode(URL, PATH_STATION_FILE);
        Log.d(TAG, "getStationFile " + url);
        Request request = new Request.Builder().url(url).build();
        return getResponseBody(App.getHttpClient(), request);
    }

    public String getBusLineFile() throws NetworkErrorException {
        String url = encode(URL, PATH_LINE_FILE);
        Log.d(TAG, "getBusLineFile " + url);
        Request request = new Request.Builder().url(url).build();
        return getResponseBody(App.getHttpClient(), request);
    }

    /**
     * get station's bus line list
     */
    public String getLineInfo(String id) throws NetworkErrorException {
        String url = encode(URL, PATH_LINE_INFO + id);
        Log.d(TAG, "getLineInfo " + url);
        Request request = new Request.Builder().url(url).build();
        return getResponseBody(App.getHttpClient(), request);
    }

    /**
     * get station info, such as name, first/end station, first/end time...
     */
    public String getLineDetailInfo(String id) throws NetworkErrorException {
        String url = encode(URL, PATH_LINE_DETAIL_INFO + id);
        Log.d(TAG, "getLineDetailInfo " + url);
        Request request = new Request.Builder().url(url).build();
        return getResponseBody(App.getHttpClient(), request);
    }

    public String getLineRT(String id) throws NetworkErrorException {
        String url = encode(URL_RT, PATH_LINE_RT_INFO + id);
        Log.d(TAG, "getLineRT " + url);
        Request request = new Request.Builder().url(url).build();
        return getResponseBody(App.getHttpClient(), request);
    }

    public String getStationRT(String id) throws NetworkErrorException {
        String url = encode(URL_RT, PATH_STATION_RT_INFO + id);
        Log.d(TAG, "getStationRT " + url);
        Request request = new Request.Builder().url(url).build();
        return getResponseBody(App.getHttpClient(), request);
    }
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<< SZXing functions <<<<<<<<<<<<<<<<<<<<<<<<<<<<<

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

    private XmlPullParser getParser(InputStream ins) throws XmlPullParserException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(ins, null);
        return parser;
    }

    public static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Station readStationInfo(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NS, "Station");
        Station info = new Station();
        while (true) {
            if (parser.next() != XmlPullParser.END_TAG || !"Station".equals(parser.getName())) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if ("StationCName".equals(name)) {
                        info.setName(parser.nextText());
                    } else if ("ID".equals(name)) {
                        info.setId(parser.nextText());
                    }
                    /*
                    else if ("Code".equals(name)) {
                        info.setCode(parser.nextText());
                    } else if ("InTime".equals(name)) {
                        info.setInTime(parser.nextText());
                    } else if ("OutTime".equals(name)) {
                        info.setOutTime(parser.nextText());
                    } else if ("BusInfo".equals(name)) {
                        info.setInCard(parser.nextText());
                    }
                     */
                }
            } else {
                return info;
            }
        }
    }
}
