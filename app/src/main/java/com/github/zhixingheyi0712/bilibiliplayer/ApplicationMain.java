package com.github.zhixingheyi0712.bilibiliplayer;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class ApplicationMain extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
