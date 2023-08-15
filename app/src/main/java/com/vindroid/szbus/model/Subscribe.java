package com.vindroid.szbus.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;

public class Subscribe implements Cloneable, Parcelable {
    private Station mStation;
    private String mStartTime;
    private String mEndTime;
    private int mWeekBit; // 0000000: sun mon tus wed thu fri sat
    private List<SubscribeBusLine> mBusLines = new ArrayList<>();

    public Subscribe() {
    }

    public Station getStation() {
        return mStation;
    }

    public void setStation(Station station) {
        mStation = station;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String endTime) {
        this.mEndTime = endTime;
    }

    public List<SubscribeBusLine> getBusLines() {
        return mBusLines;
    }

    public SubscribeBusLine getBusLine(String id) {
        for (SubscribeBusLine busLine : mBusLines) {
            if (busLine.getId().equals(id)) {
                return busLine;
            }
        }
        return new SubscribeBusLine();
    }

    public SubscribeBusLine getBusLine(int index) {
        return mBusLines.get(index);
    }

    public void addBusLine(SubscribeBusLine busLine) {
        mBusLines.add(busLine);
    }

    public int getWeekBit() {
        return mWeekBit;
    }

    public void setWeekBit(int bit) {
        mWeekBit = bit;
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
        StringBuilder builder = new StringBuilder("[Subscribe]");
        if (mStation != null) {
            builder.append(" ").append(mStation.toString());
        }
        if (mBusLines != null && mBusLines.size() > 0) {
            builder.append(" bus lines: ").append(Arrays.toString(mBusLines.toArray()));
        }
        if (!TextUtils.isEmpty(mStartTime)) {
            builder.append(" start time: ").append(mStartTime);
        }
        if (!TextUtils.isEmpty(mEndTime)) {
            builder.append(" end time: ").append(mEndTime);
        }
        builder.append(" date: ").append(mWeekBit);
        return builder.toString();
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        Subscribe clone = (Subscribe) super.clone();
        clone.mBusLines = new LinkedList<>();
        for (SubscribeBusLine busLine : mBusLines) {
            clone.mBusLines.add((SubscribeBusLine) busLine.clone());
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
        dest.writeString(this.mStartTime);
        dest.writeString(this.mEndTime);
        dest.writeInt(this.mWeekBit);
        dest.writeTypedList(this.mBusLines);
    }

    public void readFromParcel(Parcel source) {
        this.mStation = source.readParcelable(Station.class.getClassLoader());
        this.mStartTime = source.readString();
        this.mEndTime = source.readString();
        this.mWeekBit = source.readInt();
        this.mBusLines = source.createTypedArrayList(SubscribeBusLine.CREATOR);
    }

    protected Subscribe(Parcel in) {
        this.mStation = in.readParcelable(Station.class.getClassLoader());
        this.mStartTime = in.readString();
        this.mEndTime = in.readString();
        this.mWeekBit = in.readInt();
        this.mBusLines = in.createTypedArrayList(SubscribeBusLine.CREATOR);
    }

    public static final Creator<Subscribe> CREATOR = new Creator<Subscribe>() {
        @Override
        public Subscribe createFromParcel(Parcel source) {
            return new Subscribe(source);
        }

        @Override
        public Subscribe[] newArray(int size) {
            return new Subscribe[size];
        }
    };
}
