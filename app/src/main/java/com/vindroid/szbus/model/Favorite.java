package com.vindroid.szbus.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

public class Favorite implements Cloneable, Parcelable {
    private Station mStation;
    private List<InComingBusLine> mBusLineList = new ArrayList<>();

    public Favorite() {
    }

    public Station getStation() {
        return mStation;
    }

    public void setStation(Station station) {
        mStation = station;
    }

    public List<InComingBusLine> getBusLines() {
        return mBusLineList;
    }

    public InComingBusLine getBusLine(String busLineId) {
        for (InComingBusLine busLine : mBusLineList) {
            if (busLine.getId().equals(busLineId)) {
                return busLine;
            }
        }
        return new InComingBusLine();
    }

    public InComingBusLine getBusLine(int index) {
        return mBusLineList.get(index);
    }

    public void addBusLine(InComingBusLine busLine) {
        mBusLineList.add(busLine);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[Favorite]");
        if (mStation != null) {
            builder.append(" ").append(mStation.toString());
        }
        if (mBusLineList != null && mBusLineList.size() > 0) {
            builder.append(" bus lines: ").append(Arrays.toString(mBusLineList.toArray()));
        }
        return builder.toString();
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        Favorite clone = (Favorite) super.clone();
        clone.mBusLineList = new ArrayList<>();
        for (InComingBusLine busLine : mBusLineList) {
            clone.mBusLineList.add((InComingBusLine) busLine.clone());
        }
        return clone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mStation, flags);
        dest.writeTypedList(this.mBusLineList);
    }

    public void readFromParcel(Parcel source) {
        this.mStation = source.readParcelable(Station.class.getClassLoader());
        this.mBusLineList = source.createTypedArrayList(InComingBusLine.CREATOR);
    }

    protected Favorite(Parcel in) {
        this.mStation = in.readParcelable(Station.class.getClassLoader());
        this.mBusLineList = in.createTypedArrayList(InComingBusLine.CREATOR);
    }

    public static final Creator<Favorite> CREATOR = new Creator<Favorite>() {
        @Override
        public Favorite createFromParcel(Parcel source) {
            return new Favorite(source);
        }

        @Override
        public Favorite[] newArray(int size) {
            return new Favorite[size];
        }
    };
}
