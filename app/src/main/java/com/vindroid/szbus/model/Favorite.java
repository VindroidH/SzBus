package com.vindroid.szbus.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

public class Favorite implements Cloneable, Parcelable {
    private Station mStation;
    private List<InComingBusLine> mBusLines = new ArrayList<>();
    private int mIndex;

    public Favorite() {
        mIndex = 0;
    }

    public Station getStation() {
        return mStation;
    }

    public void setStation(Station station) {
        mStation = station;
    }

    public List<InComingBusLine> getBusLines() {
        return mBusLines;
    }

    public InComingBusLine getBusLine(String busLineId) {
        for (InComingBusLine busLine : mBusLines) {
            if (busLine.getId().equals(busLineId)) {
                return busLine;
            }
        }
        return new InComingBusLine();
    }

    public InComingBusLine getBusLine(int index) {
        return mBusLines.get(index);
    }

    public void addBusLine(InComingBusLine busLine) {
        mBusLines.add(busLine);
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public void sortBusLines() {
        mBusLines.sort((o1, o2) -> {
            if (o1.getName().length() != o2.getName().length()) {
                return o1.getName().length() - o2.getName().length();
            }
            return o1.getName().compareTo(o2.getName());
        });
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[Favorite]");
        if (mStation != null) {
            builder.append(" ").append(mStation.toString());
        }
        if (mBusLines != null && mBusLines.size() > 0) {
            builder.append(" bus lines: ").append(Arrays.toString(mBusLines.toArray()));
        }
        return builder.toString();
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        Favorite clone = (Favorite) super.clone();
        clone.mBusLines = new ArrayList<>();
        for (InComingBusLine busLine : mBusLines) {
            clone.mBusLines.add((InComingBusLine) busLine.clone());
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
        dest.writeTypedList(this.mBusLines);
        dest.writeInt(this.mIndex);
    }

    public void readFromParcel(Parcel source) {
        this.mStation = source.readParcelable(Station.class.getClassLoader());
        this.mBusLines = source.createTypedArrayList(InComingBusLine.CREATOR);
        this.mIndex = source.readInt();
    }

    protected Favorite(Parcel in) {
        this.mStation = in.readParcelable(Station.class.getClassLoader());
        this.mBusLines = in.createTypedArrayList(InComingBusLine.CREATOR);
        this.mIndex = in.readInt();
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
