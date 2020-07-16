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

    /**
     * 全局可用的获取Context
     * @return 全局Context
     */
    public static Context getContext() {
        return context;
    }
}
