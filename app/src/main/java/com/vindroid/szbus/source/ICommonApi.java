package com.vindroid.szbus.source;

import com.vindroid.szbus.model.BusLineDetail;
import com.vindroid.szbus.model.BusLineRealTimeInfo;
import com.vindroid.szbus.model.SearchResult;
import com.vindroid.szbus.model.StationDetail;

public interface ICommonApi {
    SearchResult search(String keyword) throws Exception;

    BusLineDetail getBusLine(String id) throws Exception;

    BusLineRealTimeInfo getBusLineRealTimeInfo(String id) throws Exception;

    StationDetail getStation(String id) throws Exception;
}
