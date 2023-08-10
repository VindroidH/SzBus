package com.vindroid.szbus.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

public class StationDetail extends Station {
    private List<InComingBusLine> mBusLines = new ArrayList<>(); // 途径此站台的车辆

    public StationDetail() {
    }

    public void addBusLine(InComingBusLine busLine) {
        mBusLines.add(busLine);
    }

    public List<InComingBusLine> getBusLines() {
        return mBusLines;
    }

    public String getBusLinesText() {
        String[] busLines = new String[mBusLines.size()];
        for (int i = 0; i < mBusLines.size(); i++) {
            busLines[i] = mBusLines.get(i).getName();
        }
        return Arrays.toString(busLines);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[StationDetail] ");
        builder.append(super.toString());
        if (mBusLines != null && mBusLines.size() > 0) {
            builder.append(" bus lines: ").append(Arrays.toString(mBusLines.toArray()));
        }
        return builder.toString();
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        StationDetail clone = (StationDetail) super.clone();
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
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.mBusLines);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.mBusLines = new ArrayList<>();
        source.readList(this.mBusLines, InComingBusLine.class.getClassLoader());
    }

    protected StationDetail(Parcel in) {
        super(in);
        this.mBusLines = new ArrayList<>();
        in.readList(this.mBusLines, InComingBusLine.class.getClassLoader());
    }

    public static final Creator<StationDetail> CREATOR = new Creator<StationDetail>() {
        @Override
        public StationDetail createFromParcel(Parcel source) {
            return new StationDetail(source);
        }

        @Override
        public StationDetail[] newArray(int size) {
            return new StationDetail[size];
        }
    };
}
