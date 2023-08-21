package com.vindroid.szbus.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

public class BusLineRealTimeInfo implements Cloneable, Parcelable {
    private String mNextDepartTime;
    private String mNextDepartBus;
    private List<RunningBus> mRunningBuses = new ArrayList<>();

    public BusLineRealTimeInfo() {
    }

    public void setNextDepartTime(String time) {
        mNextDepartTime = time;
    }

    public String getNextDepartTime() {
        return mNextDepartTime;
    }

    public void addRunningBus(RunningBus bus) {
        mRunningBuses.add(bus);
    }

    public List<RunningBus> getRunningBuses() {
        return mRunningBuses;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[BusLineRealTimeInfo]");
        if (!TextUtils.isEmpty(mNextDepartTime)) {
            builder.append(" next depart: ").append(mNextDepartTime);
        }
        if (!TextUtils.isEmpty(mNextDepartBus)) {
            builder.append(" next depart bus: ").append(mNextDepartBus);
        }
        if (mRunningBuses != null && mRunningBuses.size() > 0) {
            builder.append(" running buses: ").append(Arrays.toString(mRunningBuses.toArray()));
        }
        return builder.toString();
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        BusLineRealTimeInfo clone = (BusLineRealTimeInfo) super.clone();
        clone.mRunningBuses = new ArrayList<>();
        for (RunningBus bus : mRunningBuses) {
            clone.mRunningBuses.add((RunningBus) bus.clone());
        }
        return clone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mNextDepartTime);
        dest.writeString(this.mNextDepartBus);
        dest.writeTypedList(this.mRunningBuses);
    }

    public void readFromParcel(Parcel source) {
        this.mNextDepartTime = source.readString();
        this.mNextDepartBus = source.readString();
        this.mRunningBuses = source.createTypedArrayList(RunningBus.CREATOR);
    }

    protected BusLineRealTimeInfo(Parcel in) {
        this.mNextDepartTime = in.readString();
        this.mNextDepartBus = in.readString();
        this.mRunningBuses = in.createTypedArrayList(RunningBus.CREATOR);
    }

    public static final Creator<BusLineRealTimeInfo> CREATOR = new Creator<BusLineRealTimeInfo>() {
        @Override
        public BusLineRealTimeInfo createFromParcel(Parcel source) {
            return new BusLineRealTimeInfo(source);
        }

        @Override
        public BusLineRealTimeInfo[] newArray(int size) {
            return new BusLineRealTimeInfo[size];
        }
    };
}
