package com.example.rytryde;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class App extends Application {
    private static App sApp;
    private OkHttpClient okclient;
    private App app;
    private StethoInterceptor stethoInterceptor;

    public static App getApp() {
        return sApp;
    }

    public StethoInterceptor getStethoInterceptor() {
        return stethoInterceptor;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        Stetho.initializeWithDefaults(this);
        stethoInterceptor = new StethoInterceptor();

        okclient = new OkHttpClient.Builder()
                .readTimeout(1000000, TimeUnit.MILLISECONDS)
                .connectTimeout(1000000, TimeUnit.MILLISECONDS)
                .writeTimeout(1000000, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(stethoInterceptor)
                //.addInterceptor(new AuthInterceptor())
                .build();


    }

    public OkHttpClient getOkHttpClient() {
        return okclient;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //  MultiDex.install(this);
    }

}
