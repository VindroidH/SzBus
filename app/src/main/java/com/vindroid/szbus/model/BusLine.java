package com.vindroid.szbus.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.vindroid.szbus.utils.Constants;
import com.vindroid.szbus.utils.StringUtils;

import androidx.annotation.NonNull;

public class BusLine implements Cloneable, Parcelable {
    private String mId; // 路线ID
    private String mName; // 路线名称
    private String mStartStationName; // 起始站台
    private String mEndStationName; // 终点站台
    private String mFirstTime; // 首班车时间
    private String mLastTime; // 末班车时间
    private String mReverseId; // 反向车辆ID

    public BusLine() {
    }

    public BusLine(String id, String name) {
        setId(id);
        setName(name);
    }

    public String getId() {
        if (mId == null) return "";
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        if (mName == null) return "";
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getStartStationName() {
        return mStartStationName;
    }

    public void setStartStationName(String startSite) {
        mStartStationName = startSite;
    }

    public String getEndStationName() {
        return mEndStationName;
    }

    public void setEndStationName(String endSite) {
        if (TextUtils.isEmpty(endSite)) {
            return;
        }
        if (endSite.startsWith(Constants.BUS_LINE_TO_HEADER)) {
            mEndStationName = StringUtils.substring(endSite, Constants.BUS_LINE_TO_HEADER.length());
        } else {
            mEndStationName = endSite;
        }
    }

    public String getFirstTime() {
        return mFirstTime;
    }

    public void setFirstTime(String firstTime) {
        mFirstTime = firstTime;
    }

    public String getLastTime() {
        return mLastTime;
    }

    public void setLastTime(String lastTime) {
        mLastTime = lastTime;
    }

    public String getReverseId() {
        return mReverseId;
    }

    public void setReverseId(String id) {
        mReverseId = id;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[BusLine]");
        if (!TextUtils.isEmpty(mId)) {
            builder.append(" id: ").append(mId);
        }
        if (!TextUtils.isEmpty(mName)) {
            builder.append(" name: ").append(mName);
        }
        if (!TextUtils.isEmpty(mStartStationName)) {
            builder.append(" start station: ").append(mStartStationName);
        }
        if (!TextUtils.isEmpty(mEndStationName)) {
            builder.append(" end station: ").append(mEndStationName);
        }
        if (!TextUtils.isEmpty(mFirstTime)) {
            builder.append(" first time: ").append(mFirstTime);
        }
        if (!TextUtils.isEmpty(mLastTime)) {
            builder.append(" last time: ").append(mLastTime);
        }
        if (!TextUtils.isEmpty(mReverseId)) {
            builder.append(" reverse id: ").append(mReverseId);
        }
        return builder.toString();
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mName);
        dest.writeString(this.mStartStationName);
        dest.writeString(this.mEndStationName);
        dest.writeString(this.mFirstTime);
        dest.writeString(this.mLastTime);
        dest.writeString(this.mReverseId);
    }

    public void readFromParcel(Parcel source) {
        this.mId = source.readString();
        this.mName = source.readString();
        this.mStartStationName = source.readString();
        this.mEndStationName = source.readString();
        this.mFirstTime = source.readString();
        this.mLastTime = source.readString();
        this.mReverseId = source.readString();
    }

    protected BusLine(Parcel in) {
        this.mId = in.readString();
        this.mName = in.readString();
        this.mStartStationName = in.readString();
        this.mEndStationName = in.readString();
        this.mFirstTime = in.readString();
        this.mLastTime = in.readString();
        this.mReverseId = in.readString();
    }

    public static final Creator<BusLine> CREATOR = new Creator<BusLine>() {
        @Override
        public BusLine createFromParcel(Parcel in) {
            return new BusLine(in);
        }

        @Override
        public BusLine[] newArray(int size) {
            return new BusLine[size];
        }
    };
}
