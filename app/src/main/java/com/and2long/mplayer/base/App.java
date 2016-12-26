package com.and2long.mplayer.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by L on 2016/12/3.
 */

public class App extends Application {

    public static Context appContext;
    private static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();
        mHandler = new Handler();

    }

    /**
     * 获取主线程handler
     *
     * @return
     */
    public static Handler getMainHandler() {
        return mHandler;
    }
}
