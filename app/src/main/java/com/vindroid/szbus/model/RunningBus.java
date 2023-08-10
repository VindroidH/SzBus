package com.vindroid.szbus.model;

import android.os.Parcel;
import android.text.TextUtils;

import androidx.annotation.NonNull;

public class RunningBus extends Bus {
    private String mInStationId; // 入站的站台ID
    private String mInTime; // 入站时间

    public RunningBus() {
    }

    protected RunningBus(Parcel in) {
        super(in);
        this.mInStationId = in.readString();
        this.mInTime = in.readString();
    }

    public String getStationId() {
        if (mInStationId == null) return "";
        return mInStationId;
    }

    public void setStationId(String stationId) {
        mInStationId = stationId;
    }

    public String getInTime() {
        if (mInTime == null) return "";
        return mInTime;
    }

    public void setInTime(String inTime) {
        mInTime = inTime;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[RunningBus] ");
        builder.append(super.toString());
        if (!TextUtils.isEmpty(mInStationId)) {
            builder.append(" inbound station id: ").append(mInStationId);
        }
        if (!TextUtils.isEmpty(mInTime)) {
            builder.append(" inbound time: ").append(mInTime);
        }
        return builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mInStationId);
        dest.writeString(this.mInTime);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.mInStationId = source.readString();
        this.mInTime = source.readString();
    }

    public static final Creator<RunningBus> CREATOR = new Creator<RunningBus>() {
        @Override
        public RunningBus createFromParcel(Parcel source) {
            return new RunningBus(source);
        }

        @Override
        public RunningBus[] newArray(int size) {
            return new RunningBus[size];
        }
    };
}
