package com.rh.materialdemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rh.materialdemo.MainActivity;
import com.rh.materialdemo.R;

public class WeatherLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_location);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherLocationActivity.this);
        if (preferences.getString("weather", null) != null) {
            Intent intent_weather = new Intent(WeatherLocationActivity.this, WeatherActivity.class);
            startActivity(intent_weather);
            finish();
        }
    }
}
