package com.vindroid.szbus.utils;

public class Config {
    public enum Source {
        SzBus,
        SzBusV2
    }

    private static final Source mSource = Source.SzBus;

    public static Source getSource() {
        return mSource;
    }
}
