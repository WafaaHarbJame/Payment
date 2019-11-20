package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyApplication extends Application {

    private RequestQueue requestQueue;
    @SuppressLint("StaticFieldLeak")
    private static MyApplication anInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        anInstance = this;
        //  ConnectivityReceiver.init(this);

        //EmojiManager.install(new GoogleEmojiProvider());


    }

    public void addToRequestQueue(Request request) {
        if (getRequestQueue() != null) {
            getRequestQueue().add(request);
        } else {
            requestQueue = Volley.newRequestQueue(this);
            getRequestQueue().add(request);
        }
    }

    private RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void cancelRequest(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    public static synchronized MyApplication getInstance() {
        return anInstance;
    }

    @SuppressLint("StaticFieldLeak")
    private static Activity mCurrentActivity = null;

    public static Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        MyApplication.mCurrentActivity = mCurrentActivity;
    }
    //EmojiManager.install(new GoogleEmojiProvider());

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}