package com.vindroid.szbus.source;

import com.vindroid.szbus.model.BusLineDetail;
import com.vindroid.szbus.model.BusLineRealTimeInfo;
import com.vindroid.szbus.model.SearchResult;
import com.vindroid.szbus.model.StationDetail;

public interface BusParserInterface {
    SearchResult search(String keyword) throws Exception;

    BusLineDetail getBusLine(String busLineId) throws Exception;

    BusLineRealTimeInfo getBusLineRealTimeInfo(String busLineId) throws Exception;

    StationDetail getStation(String stationId) throws Exception;
}
