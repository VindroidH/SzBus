package com.vindroid.szbus.model;

import android.os.Parcel;

import androidx.annotation.NonNull;

public class InComingBusLine extends BusLine {
    public static final int COMING_NO = -1;
    public static final int COMING_NOW = 0;
    public static final int COMING_ERR = 999;

    private int mComing = COMING_NO;

    public InComingBusLine() {
        super();
    }

    public InComingBusLine(String id, String name) {
        super(id, name);
    }

    public int getComing() {
        return mComing;
    }

    public void setComing(int coming) {
        mComing = coming;
    }

    @NonNull
    @Override
    public String toString() {
        return "[InComingBusLine] " + super.toString() + " coming: " + mComing;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mComing);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.mComing = source.readInt();
    }

    protected InComingBusLine(Parcel in) {
        super(in);
        this.mComing = in.readInt();
    }

    public static final Creator<InComingBusLine> CREATOR = new Creator<InComingBusLine>() {
        @Override
        public InComingBusLine createFromParcel(Parcel source) {
            return new InComingBusLine(source);
        }

        @Override
        public InComingBusLine[] newArray(int size) {
            return new InComingBusLine[size];
        }
    };
}
