package com.vindroid.szbus;

public class Note {
/*
public class ServiceCenter {
    public static final String TAG = ServiceCenter.class.getSimpleName();

    public static List<Line> getFavoriteLines(String lessThan, int number) {
        List<Line> lineList;
        String xml = getFavorites(Kind.LINE_BUS, lessThan, number);
        Map<String, List<SubLine>> data = new LinkedHashMap<>();
        List<Line> lineList2 = null;
        try {
            List<SubLine> infos = PullXmlCenter.parseFavoriteLines(StringKit.string2Stream(xml));
            for (SubLine subline : infos) {
                String name = subline.getName();
                if (data.containsKey(name)) {
                    data.get(name).add(subline);
                } else {
                    List<SubLine> sublines = new ArrayList<>();
                    sublines.add(subline);
                    data.put(name, sublines);
                }
            }
            lineList = new ArrayList<>();
        } catch (Exception e) {
            e = e;
        }
        try {
            for (String name2 : data.keySet()) {
                Line line = new Line();
                line.setName(name2);
                line.setSubLines(data.get(name2));
                lineList.add(line);
            }
            return lineList;
        } catch (Exception e2) {
            e = e2;
            lineList2 = lineList;
            Logger.e(TAG, e.getMessage(), e);
            return lineList2;
        }
    }

    public static List<Favorite> getFavoriteLines2(String lessThan, int number) {
        String xml = getFavorites(Kind.LINE_BUS, lessThan, number);
        List<Favorite> favList = null;
        try {
            List<SubLine> infos = PullXmlCenter.parseFavoriteLines(StringKit.string2Stream(xml));
            if (infos == null) {
                return null;
            }
            List<Favorite> favList2 = new ArrayList<>();
            try {
                for (SubLine subLine : infos) {
                    Favorite fav = new Favorite();
                    fav.setId(subLine.getId());
                    fav.setName(subLine.getName());
                    fav.setDesc(subLine.getDirection());
                    fav.setCategory(Kind.LINE_BUS);
                    favList2.add(fav);
                }
                return favList2;
            } catch (Exception e) {
                e = e;
                favList = favList2;
                Logger.e(TAG, e.getMessage(), e);
                return favList;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public static List<Station> getFavoriteStations(String lessThan, int number) {
        List<Station> stations;
        String xml = getFavorites(Kind.STATION_BUS, lessThan, number);
        Map<String, List<SubStation>> data = new LinkedHashMap<>();
        List<Station> stations2 = null;
        try {
            List<SubStation> infos = PullXmlCenter.parseFavoriteStations(StringKit.string2Stream(xml));
            for (SubStation station : infos) {
                String name = station.getName();
                if (data.containsKey(name)) {
                    data.get(name).add(station);
                } else {
                    List<SubStation> subs = new ArrayList<>();
                    subs.add(station);
                    data.put(name, subs);
                }
            }
            stations = new ArrayList<>();
        } catch (Exception e) {
            e = e;
        }
        try {
            for (String name2 : data.keySet()) {
                Station sta = new Station();
                sta.setName(name2);
                sta.setSubStationList(data.get(name2));
                stations.add(sta);
            }
            return stations;
        } catch (Exception e2) {
            e = e2;
            stations2 = stations;
            Logger.e(TAG, e.getMessage(), e);
            return stations2;
        }
    }

    public static List<Favorite> getFavoriteStations2(String lessThan, int number) {
        String xml = getFavorites(Kind.STATION_BUS, lessThan, number);
        List<Favorite> favList = null;
        try {
            List<SubStation> infos = PullXmlCenter.parseFavoriteStations(StringKit.string2Stream(xml));
            if (infos == null) {
                return null;
            }
            List<Favorite> favList2 = new ArrayList<>();
            try {
                for (SubStation subLine : infos) {
                    Favorite fav = new Favorite();
                    fav.setId(subLine.getId());
                    fav.setName(subLine.getName());
                    fav.setDesc(subLine.getPosition());
                    fav.setCategory(Kind.STATION_BUS);
                    favList2.add(fav);
                }
                return favList2;
            } catch (Exception e) {
                e = e;
                favList = favList2;
                Logger.e(TAG, e.getMessage(), e);
                return favList;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    private static String getFavorites(Kind kind, String lessThan, int number) {
        String url = iCityMachine.encode(String.format("sjGetMyFavorites?number=%d&lessThan=%s&kind=%d", Integer.valueOf(number), lessThan, Integer.valueOf(kind.id)));
        return HttpKit.getStringResponse(url);
    }

    public static List<LineInfo> getStationInfo(String stationID) {
        String url = iCityMachine.encode(String.format("sjGetStationInfo?stationID=%s", stationID));
        String xml = HttpKit.getStringResponse(url);
        try {
            List<LineInfo> list = PullXmlCenter.parseLineInfos(StringKit.string2Stream(xml));
            if (list != null) {
                Collections.sort(list);
                return list;
            }
            return list;
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static List<MenuItem> getMenuItemList(Context context) {
        String response = HttpKit.getStringResponse("http://szxing.icitymobile.com/Menu/home_menu.json");
        if (StringKit.isNotEmpty(response)) {
            try {
                Gson gson = new Gson();
                List<MenuItem> temp = (List) gson.fromJson(response, new TypeToken<List<MenuItem>>() { // from class: com.icitysuzhou.szjt.data.ServiceCenter.1
                }.getType());
                if (temp != null && temp.size() > 0) {
                    FileKit.save(context, response, "home_menu.json");
                    return temp;
                }
                return temp;
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage(), e);
            }
        }
        return null;
    }

    public static void delFavorite(int id, Kind kind, AsyncHttpResponseHandler handler) {
        String url = iCityMachine.encode(String.format("sjDelMyFavoriteStationOrLine?ID=%d&kind=%d", Integer.valueOf(id), Integer.valueOf(kind.id)));
        AsyncHttpKit.get(url, handler);
    }

    public static GeoPoint convertToBaiduCoord(double lat, double lon) {
        if (((int) lat) == 0 || ((int) lon) == 0) {
            return null;
        }
        String url = String.format("http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=%f&y=%f", Double.valueOf(lon), Double.valueOf(lat));
        Logger.i(TAG, url);
        String response = HttpKit.getStringResponse(url);
        return parseGeoPointConversionResult(response);
    }

    public static GeoPoint convertToGpsCoord(double lat, double lon) {
        GeoPoint converted = convertToBaiduCoord(lat, lon);
        if (converted == null) {
            return null;
        }
        int gpsLat = (int) (((lat * 1000000.0d) * 2.0d) - converted.getLatitudeE6());
        int gpsLon = (int) (((lon * 1000000.0d) * 2.0d) - converted.getLongitudeE6());
        GeoPoint gps = new GeoPoint(gpsLat, gpsLon);
        return gps;
    }

    private static GeoPoint parseGeoPointConversionResult(String response) {
        try {
            Logger.i(TAG, response);
            JSONObject json = new JSONObject(response);
            int error = json.optInt("error");
            if (error != 0) {
                return null;
            }
            String x = json.optString("x");
            String y = json.optString("y");
            String lonStr = new String(Base64.decode(x, 2), "UTF-8");
            String latStr = new String(Base64.decode(y, 2), "UTF-8");
            int longitude = (int) (Double.parseDouble(lonStr) * 1000000.0d);
            int latitude = (int) (Double.parseDouble(latStr) * 1000000.0d);
            GeoPoint gp = new GeoPoint(latitude, longitude);
            return gp;
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static void postReport(String content, int contact_type, String contact, AsyncHttpResponseHandler handler) throws Exception {
        String url = iCityMachine.encode("sjput");
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "text/plain");
        client.addHeader("Content-Length", String.valueOf(content.getBytes().length));
        client.addHeader("X-FB-Mode", "UploadText");
        client.addHeader("X-FB-Title", new String(Base64.encode(contact.getBytes(), 2)));
        client.addHeader("X-FB-Category", String.valueOf(contact_type));
        client.put(null, url, new StringEntity(content, "utf8"), null, handler);
    }

    public static List<RoutePlan> getTransferPlans(int lonFrom, int latFrom, int lonTo, int latTo) {
        String url = String.format("sjGetLinePlanByLonLat?lonFrom=%d&latFrom=%d&lonTo=%d&latTo=%d", Integer.valueOf(lonFrom), Integer.valueOf(latFrom), Integer.valueOf(lonTo), Integer.valueOf(latTo));
        String xml = HttpKit.getStringResponse(iCityMachine.encode(url));
        try {
            List<RoutePlan> plans = PullXmlCenter.parseTransferPlans(StringKit.string2Stream(xml));
            filterRoute(plans);
            List<RoutePlan> plans2 = filterDuplicateRoutePlan(plans);
            filterStartEnd(plans2);
            return filterRoutePlan(plans2);
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    private static List<RoutePlan> filterRoutePlan(List<RoutePlan> plans) {
        if (plans == null) {
            return null;
        }
        List<RoutePlan> plans2 = filterDuplicateRoutePlan(plans);
        for (RoutePlan plan : plans2) {
            filter(plan.getRouteList(), 0);
            filterStep(plan.getRouteList());
        }
        return filterDuplicateRoutePlan(plans2);
    }

    private static void filterRoute(List<RoutePlan> plans) {
        if (plans != null) {
            for (RoutePlan plan : plans) {
                List<Route> routeList = plan.getRouteList();
                if (routeList != null) {
                    for (Route route : routeList) {
                        if (route.getIds().equals("-1")) {
                            routeList.removeAll(routeList);
                            Route route2 = new Route();
                            SubStation sub = new SubStation();
                            sub.setName(MyApplication.startStation.getName());
                            route2.setCategory(2);
                            route2.setStartStation(sub);
                            SubStation sub2 = new SubStation();
                            sub2.setName(MyApplication.endStation.getName());
                            route2.setEndStation(sub2);
                            routeList.add(route2);
                        }
                    }
                }
            }
        }
    }

    private static List<RoutePlan> filterStartEnd(List<RoutePlan> plans) {
        if (plans == null) {
            return null;
        }
        Station start = MyApplication.startStation;
        Station end = MyApplication.endStation;
        for (RoutePlan plan : plans) {
            List<Route> routeList = plan.getRouteList();
            if (routeList != null) {
                try {
                    if (!routeList.get(0).getStartStation().getName().equals(start.getName())) {
                        Route route = new Route();
                        SubStation sub = new SubStation();
                        sub.setName(start.getName());
                        sub.setEname(start.getEname());
                        route.setStartStation(sub);
                        route.setEndStation(routeList.get(0).getStartStation());
                        route.setCategory(2);
                        routeList.add(0, route);
                    }
                    int count = routeList.size();
                    if (!routeList.get(count - 1).getEndStation().getName().equals(end.getName())) {
                        Route route2 = new Route();
                        SubStation sub2 = new SubStation();
                        sub2.setName(end.getName());
                        sub2.setEname(end.getEname());
                        route2.setStartStation(routeList.get(count - 1).getEndStation());
                        route2.setEndStation(sub2);
                        route2.setCategory(2);
                        routeList.add(route2);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        return plans;
    }

    private static void filterStep(List<Route> routeList) {
        if (routeList != null) {
            for (int i = routeList.size() - 1; i >= 0 && i - 1 >= 0; i--) {
                Route a = routeList.get(i);
                Route b = routeList.get(i - 1);
                if (a.getCategory() == 2 && b.getCategory() == 2) {
                    b.setEndStation(a.getEndStation());
                    routeList.remove(i);
                }
            }
        }
    }

    private static List<Route> filter(List<Route> routeList, int filterIndex) {
        if (routeList == null) {
            return null;
        }
        if (filterIndex < routeList.size()) {
            int endIndex1 = 0;
            int endIndex2 = 0;
            Route a = routeList.get(filterIndex);
            int j = routeList.size() - 1;
            while (true) {
                if (j <= filterIndex) {
                    break;
                }
                Route b = routeList.get(j);
                if (a.getStartStation().getName().equals(b.getStartStation().getName())) {
                    endIndex1 = j;
                    break;
                }
                j--;
            }
            for (int i = endIndex1 - 1; i >= filterIndex; i--) {
                routeList.remove(i);
            }
            int j2 = routeList.size() - 1;
            while (true) {
                if (j2 <= filterIndex) {
                    break;
                }
                Route b2 = routeList.get(j2);
                if (a.getEndStation().getName().equals(b2.getEndStation().getName())) {
                    endIndex2 = j2;
                    break;
                }
                j2--;
            }
            for (int i2 = endIndex2; i2 > filterIndex; i2--) {
                routeList.remove(i2);
            }
            return filterIndex < routeList.size() + (-1) ? filter(routeList, filterIndex + 1) : routeList;
        }
        return routeList;
    }

    private static List<RoutePlan> filterDuplicateRoutePlan(List<RoutePlan> plans) {
        if (plans == null) {
            return null;
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (RoutePlan plan : plans) {
            if (!linkedHashSet.contains(plan)) {
                Logger.d(TAG, "!!!!{{{{" + plan.getRoutePlanName() + " SIZE: " + plan.getRouteList().size() + " }}}}");
                linkedHashSet.add(plan);
            }
        }
        Logger.d(TAG, "!!!!-------------------------" + linkedHashSet.size());
        List<RoutePlan> result = new ArrayList<>();
        result.addAll(linkedHashSet);
        return result;
    }

    public static List<LineInfo> getStationRTInfo(String id) {
        String url = iCityMachine.encode_for_rt("sjGetStationRTInfo?stationID=" + id);
        String xml = HttpKit.getStringResponse(url);
        try {
            List<LineInfo> list = PullXmlCenter.parseLineInfos(StringKit.string2Stream(xml));
            if (list != null) {
                Collections.sort(list);
                return list;
            }
            return list;
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static List<StationInfo> getLineRTInfo(String lineId) {
        String url = iCityMachine.encode_for_rt(String.format("sjGetLineRTInfo?lineID=%s", lineId));
        String xml = HttpKit.getStringResponse(url);
        try {
            return PullXmlCenter.parseStationInfos(StringKit.string2Stream(xml));
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static List<StationInfo> getLineInfo(String lineId) {
        String url = iCityMachine.encode(String.format("sjGetLineInfo?lineID=%s", lineId));
        String xml = HttpKit.getStringResponse(url);
        try {
            return PullXmlCenter.parseStationInfos(StringKit.string2Stream(xml));
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static LineTime getLineTimeInfo(String lineId) throws XmlParseException {
        String url = iCityMachine.encode(String.format("sjGetLineDetailInfo?lineID=%s", lineId));
        return XmlParse.parseLineTimeByUrl(url);
    }

    public static List<PoiBean> getKeyWords(String keyword, int limit) throws XmlParseException {
        int lat = (int) MyApplication.getMyLocation().latitudeE6;
        int lon = (int) MyApplication.getMyLocation().longitudeE6;
        String url = iCityMachine.encode(String.format("sjGetPOIListByKeyword?keyword=%s&category=all&limit=%d&longitude=%d&latitude=%d", YLBase64.encode(keyword), Integer.valueOf(limit), Integer.valueOf(lon), Integer.valueOf(lat)));
        return XmlParse.parsePoiByUrl(url);
    }

    public enum Kind {
        STATION_BUS(0),
        STATION_SUBWAY(3),
        LINE_BUS(1),
        LINE_SUBWAY(4),
        RoutePlan(2);

        int id;

        Kind(int id) {
            this.id = id;
        }
    }

    public static List<LineLatAndLon> getLineLatAndLonList(String id) throws JSONException {
        if (id == null) {
            id = "";
        }
        String url = iCityMachine.encode(String.format("maplines/line_%s.json", id));
        String response = HttpKit.getStringResponse(url);
        if (StringKit.isEmpty(response)) {
            return null;
        }
        return LineLatAndLon.fromJsonArray(new JSONArray(response));
    }
}
 */

    /*
    {
"bike":{"time":"20140507112615","checksum":"18040148"},
"station":{"time":"20230908020001","checksum":"150807784"},
"line":{"time":"20230908020001","checksum":"14095511"}
}



     */
}
