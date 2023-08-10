package com.vindroid.szbus.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.vindroid.szbus.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

public class Station implements Cloneable, Parcelable {
    private String mId; // 站台ID
    private String mName; // 站台名称
    private String mAddress; // 站台地址
    private List<String> mSubways = new ArrayList<>(); // 可换乘的地铁线路

    public Station() {
    }

    public Station(String id, String name) {
        setId(id);
        setName(name);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return;
        }
        if (address.startsWith(Constants.ADDRESS_HEADER)) {
            mAddress = address.substring(Constants.ADDRESS_HEADER.length());
        } else {
            mAddress = address;
        }
    }

    public void setSubways(List<String> subways) {
        mSubways = subways;
    }

    public List<String> getSubways() {
        return mSubways;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[Station]");
        if (!TextUtils.isEmpty(mId)) {
            builder.append(" id: ").append(mId);
        }
        if (!TextUtils.isEmpty(mName)) {
            builder.append(" name: ").append(mName);
        }
        if (!TextUtils.isEmpty(mAddress)) {
            builder.append(" address: ").append(mAddress);
        }
        if (mSubways != null && mSubways.size() > 0) {
            builder.append(" subways: ").append(Arrays.toString(mSubways.toArray()));
        }
        return builder.toString();
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
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
        dest.writeString(this.mAddress);
        dest.writeStringList(this.mSubways);
    }

    public void readFromParcel(Parcel source) {
        this.mId = source.readString();
        this.mName = source.readString();
        this.mAddress = source.readString();
        this.mSubways = source.createStringArrayList();
    }

    protected Station(Parcel in) {
        this.mId = in.readString();
        this.mName = in.readString();
        this.mAddress = in.readString();
        this.mSubways = in.createStringArrayList();
    }

    public static final Creator<Station> CREATOR = new Creator<Station>() {
        @Override
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };
}
