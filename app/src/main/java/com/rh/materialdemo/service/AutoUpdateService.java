package com.rh.materialdemo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rh.materialdemo.Util.HttpUtils;
import com.rh.materialdemo.Util.ParseJsonUtils;
import com.rh.materialdemo.activity.WeatherActivity;
import com.rh.materialdemo.gson.Weather;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    private static final String TAG = "AutoUpdateService";

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "服务onStartCommand");
        updateWeather();
        updateBingPic();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour =  1 * 30 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, 0);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新必应每日一图
     */
    private void updateBingPic() {
        String requestBinPic = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";
        HttpUtils.sendOkHttpRequestWithGET(requestBinPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String bingPic = "https://www.bing.com" + ParseJsonUtils.handleBingPicResponse(response.body().string());
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("bing_pic", bingPic);
                    editor.apply();
                    Log.d(TAG, "后台服务请求图片完成");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherId = prefs.getString("weather_id", null);
        if (weatherId != null) {
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=45f7b6d8dc9c44f18e8d78c91339609b";
            Log.d(TAG, "后台服务请求天气中..... ");
            HttpUtils.sendOkHttpRequestWithGET(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = ParseJsonUtils.handleWeatherResponse(responseText);
                    if (weather !=null &&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                        Log.d(TAG, "后台服务请求天气完成");
                    }
                }
            });
        }
    }
}
