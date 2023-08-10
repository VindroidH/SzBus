package com.vindroid.szbus.model;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
    public enum Type {
        None,
        Bus,
        Station,
        Both
    }

    private Type mType;
    private final List<BusLine> mBusLines;
    private final List<StationDetail> mStations;

    public SearchResult() {
        mBusLines = new ArrayList<>();
        mStations = new ArrayList<>();
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public List<BusLine> getBusLines() {
        return mBusLines;
    }

    public List<StationDetail> getStations() {
        return mStations;
    }

    public void addBusLine(BusLine busLine) {
        mBusLines.add(busLine);
    }

    public void addStation(StationDetail station) {
        mStations.add(station);
    }
}
