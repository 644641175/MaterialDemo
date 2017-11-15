package com.rh.materialdemo;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * Created by RH on 2017/11/4.
 */

public class MyApplication extends Application {
    private static Context context;//全局context
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        /**LitePalApplication.initialize(context)的调用原则是尽可能早，
        *比如合适的调用位置是在Application的onCreate()里调用。
        *调用时传递的参数是Application的context，
        *不要使用任何activity或service的实例作为参数，否则可能发生内存泄漏。
         *
         * 也可以在AndroidManifest.xml，配置application的参数android:name="org.litepal.LitePalApplication"
         * 然后MyApplication继承LitePalApplication
         */
        LitePalApplication.initialize(context);
    }

    public static Context getContext(){
        return context;
    }
}
