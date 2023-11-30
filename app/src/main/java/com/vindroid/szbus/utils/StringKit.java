package com.vindroid.szbus.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

public class StringKit {
    private static final Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private static final Pattern numericPattern = Pattern.compile("^[0-9]+$");
    private static final Pattern blankPattern = Pattern.compile("\\s*|\t|\r|\n");
    private static final Pattern blankPatternLR = Pattern.compile("^\\s*|\\s*$");
    private static final Pattern htmlPattern = Pattern.compile("<[^>]+>", 2);
    private static Pattern abstractPattern = Pattern.compile("^.{1,10}[报网]讯\\s?([（(][^）)]*[）)])?");

    public static boolean isEmpty(String src) {
        return src == null || src.trim().length() == 0;
    }

    public static boolean isNotEmpty(String src) {
        return !isEmpty(src);
    }

    public static InputStream string2Stream(String str) {
        if (isEmpty(str)) {
            return null;
        }
        return new ByteArrayInputStream(str.getBytes());
    }

    public static String stream2String(InputStream inStream) throws IOException {
        return new String(stream2Bytes(inStream));
    }

    public static byte[] stream2Bytes(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream;
        ByteArrayOutputStream outStream2;
        outStream = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            while (true) {
                int len = inStream.read(buffer);
                if (len == -1) {
                    break;
                }
                outStream.write(buffer, 0, len);
            }
            byte[] byteArray = outStream.toByteArray();
            try {
                outStream.close();
            } catch (Exception ignored) {
            }
            return byteArray;
        } catch (Throwable th2) {
            outStream2 = outStream;
            try {
                outStream2.close();
            } catch (Exception ignored) {
            }
            throw th2;
        }
    }
}
