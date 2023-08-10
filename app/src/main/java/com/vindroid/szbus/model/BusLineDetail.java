package com.vindroid.szbus.model;

import android.os.Parcel;
import android.text.TextUtils;

import com.vindroid.szbus.utils.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;

public class BusLineDetail extends BusLine {
    private LinkedList<Station> mStations = new LinkedList<>(); // 途径的站台
    private BusLineRealTimeInfo mInfo; // 车辆实时信息

    public BusLineDetail() {
    }

    public LinkedList<Station> getStations() {
        return mStations;
    }

    public void addStation(String id, String name) {
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(name)) {
            return;
        }
        String idHeader = "gotoStation"; // gotoStation('*')
        if (id.startsWith(idHeader)) {
            id = StringUtils.substring(id, idHeader.length() + 2, 2);
        }
        mStations.add(new Station(id, name));
    }

    public void addStationSubway(String stationId, List<String> subways) {
        for (Station station : mStations) {
            if (station.getId().equals(stationId)) {
                station.setSubways(subways);
                break;
            }
        }
    }

    public Station getStation(String stationId) {
        for (Station station : mStations) {
            if (station.getId().equals(stationId)) {
                return station;
            }
        }
        return null;
    }

    public BusLineRealTimeInfo getRealInfo() {
        return mInfo;
    }

    public void setRealTimeInfo(BusLineRealTimeInfo info) {
        mInfo = info;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[BusLineDetail] ");
        builder.append(super.toString());
        if (mStations != null && mStations.size() > 0) {
            builder.append(" stations: ").append(Arrays.toString(mStations.toArray()));
        }
        if (mInfo != null) {
            builder.append(" ").append(mInfo.toString());
        }
        return builder.toString();
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        BusLineDetail busLine = (BusLineDetail) super.clone();
        busLine.mStations = new LinkedList<>();
        for (Station station : mStations) {
            busLine.mStations.add((Station) station.clone());
        }
        return busLine;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.mStations);
        dest.writeParcelable(this.mInfo, flags);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.mStations = new LinkedList<>();
        source.readList(this.mStations, Station.class.getClassLoader());
        this.mInfo = source.readParcelable(BusLineRealTimeInfo.class.getClassLoader());
    }

    protected BusLineDetail(Parcel in) {
        super(in);
        this.mStations = new LinkedList<>();
        in.readList(this.mStations, Station.class.getClassLoader());
        this.mInfo = in.readParcelable(BusLineRealTimeInfo.class.getClassLoader());
    }

    public static final Creator<BusLineDetail> CREATOR = new Creator<BusLineDetail>() {
        @Override
        public BusLineDetail createFromParcel(Parcel source) {
            return new BusLineDetail(source);
        }

        @Override
        public BusLineDetail[] newArray(int size) {
            return new BusLineDetail[size];
        }
    };
}
