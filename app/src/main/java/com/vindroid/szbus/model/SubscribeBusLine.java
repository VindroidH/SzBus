package com.vindroid.szbus.model;

import android.os.Parcel;

import androidx.annotation.NonNull;

public class SubscribeBusLine extends BusLine {
    private int mAhead;

    public SubscribeBusLine() {
        super();
    }

    public SubscribeBusLine(String id, String name) {
        super(id, name);
    }

    public int getAhead() {
        return mAhead;
    }

    public void setAhead(int ahead) {
        mAhead = ahead;
    }

    @NonNull
    @Override
    public String toString() {
        return "[SubscribeBusLine] " + super.toString() + " ahead: " + mAhead;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mAhead);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.mAhead = source.readInt();
    }

    protected SubscribeBusLine(Parcel in) {
        super(in);
        this.mAhead = in.readInt();
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
