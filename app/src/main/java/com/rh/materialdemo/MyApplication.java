package com.rh.materialdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by RH on 2017/11/4.
 */

public class MyApplication extends Application {
    private static Context context;//全局context
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
