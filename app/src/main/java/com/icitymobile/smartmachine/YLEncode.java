package com.icitymobile.smartmachine;

public class YLEncode {
    public static native String composeUri(String str, String str2, String str3, String str4);

    static {
        System.loadLibrary("YLEncode");
    }
}
