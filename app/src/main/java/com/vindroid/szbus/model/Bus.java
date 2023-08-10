package com.vindroid.szbus.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Bus implements Cloneable, Parcelable {
    protected String mInfo; // 车牌号

    public Bus() {
    }

    protected Bus(Parcel in) {
        this.mInfo = in.readString();
    }

    public String getInfo() {
        if (mInfo == null) return "";
        return mInfo;
    }

    public void setInfo(String info) {
        mInfo = info;
    }

    @NonNull
    @Override
    public String toString() {
        return "[Bus] info: " + mInfo;
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
        dest.writeString(this.mInfo);
    }

    public void readFromParcel(Parcel source) {
        this.mInfo = source.readString();
    }

    public static final Creator<Bus> CREATOR = new Creator<Bus>() {
        @Override
        public Bus createFromParcel(Parcel in) {
            return new Bus(in);
        }

        @Override
        public Bus[] newArray(int size) {
            return new Bus[size];
        }
    };
}
