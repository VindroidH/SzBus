package com.vindroid.szbus;

import android.app.Application;
import android.content.Context;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class App extends Application {
    private static final String TAG = "SzBus";

    private static Context sContext;
    private static OkHttpClient mHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this.getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

    public static String getTag(String tag) {
        return TAG + "." + tag;
    }

    public static String getStringById(int id) {
        if (sContext == null) {
            return NullPointerException.class.getSimpleName();
        }
        return sContext.getString(id);
    }

    public static String getStringById(int id, Object... args) {
        if (sContext == null) {
            return NullPointerException.class.getSimpleName();
        }
        return sContext.getString(id, args);
    }

    public static int getColorById(int id) {
        if (sContext == null) {
            return 0;
        }
        return sContext.getColor(id);
    }

    public static OkHttpClient getHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new OkHttpClient().newBuilder()
                    .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .retryOnConnectionFailure(true)
                    .build();
        }
        return mHttpClient;
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
