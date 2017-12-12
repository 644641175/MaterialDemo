package com.rh.materialdemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rh.materialdemo.MainActivity;
import com.rh.materialdemo.R;
import com.rh.materialdemo.service.AutoUpdateService;

public class WeatherLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_location);

        /*开启后台自动更新天气服务*/
        if (true){
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherLocationActivity.this);
        if (preferences.getString("weather_id", null) != null) {
            Intent intent_weather = new Intent(WeatherLocationActivity.this, WeatherActivity.class);
            startActivity(intent_weather);
            finish();
        }
    }
}
