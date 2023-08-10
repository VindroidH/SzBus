package com.vindroid.szbus.model;

import android.os.Parcel;

import androidx.annotation.NonNull;

public class SubscribeBusLine extends BusLine {
    private int mAheadOfStation;

    public SubscribeBusLine() {
        super();
    }

    public SubscribeBusLine(String id, String name) {
        super(id, name);
    }

    public int getAheadOfStation() {
        return mAheadOfStation;
    }

    public void setAheadOfStation(int ahead) {
        mAheadOfStation = ahead;
    }

    @NonNull
    @Override
    public String toString() {
        return "[SubscribeBusLine] " + super.toString() + " ahead: " + mAheadOfStation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mAheadOfStation);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.mAheadOfStation = source.readInt();
    }

    protected SubscribeBusLine(Parcel in) {
        super(in);
        this.mAheadOfStation = in.readInt();
    }

    public static final Creator<SubscribeBusLine> CREATOR = new Creator<SubscribeBusLine>() {
        @Override
        public SubscribeBusLine createFromParcel(Parcel source) {
            return new SubscribeBusLine(source);
        }

        @Override
        public SubscribeBusLine[] newArray(int size) {
            return new SubscribeBusLine[size];
        }
    };
}
