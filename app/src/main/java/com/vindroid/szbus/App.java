package com.vindroid.szbus;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

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

    public static final String TAG = "SzBus";
    private static OkHttpClient mHttpClient;

    private static Context sContext;
    private static SharedPreferences mFavoriteSp;
    private static SharedPreferences mSubscribeSp;

    public static String getTag(String tag) {
        return TAG + "." + tag;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this.getApplicationContext();
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

    public static SharedPreferences getFavoriteSp() {
        if (mFavoriteSp == null) {
            mFavoriteSp = sContext.getSharedPreferences("favorite_sp", Context.MODE_PRIVATE);
        }
        return mFavoriteSp;
    }

    public static SharedPreferences getSubscribeSp() {
        if (mSubscribeSp == null) {
            mSubscribeSp = sContext.getSharedPreferences("subscribe_sp", Context.MODE_PRIVATE);
        }
        return mSubscribeSp;
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
