package com.rh.materialdemo.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.rh.materialdemo.R;
import com.rh.materialdemo.Util.ActivityCollector;
import com.rh.materialdemo.Util.HttpUtils;
import com.rh.materialdemo.Util.MyToast;
import com.rh.materialdemo.Util.ParseJsonUtils;
import com.rh.materialdemo.gson.Forecast;
import com.rh.materialdemo.gson.Weather;


import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author RH
 * @date 2017/11/15
 */

public class WeatherActivity extends BaseActivity {
    private static final String TAG = "WeatherActivity";
    private ImageView bingPicImg;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    public SwipeRefreshLayout swipeRefreshLayout;
    public DrawerLayout drawerLayout;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*系统版本为Android5.0以上时 状态栏与布局融合*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);

        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.weather_title_city);
        titleUpdateTime = (TextView) findViewById(R.id.weather_title_update_time);
        degreeText = (TextView) findViewById(R.id.weather_now_degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_now_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.weather_forecast_layout);
        aqiText = (TextView) findViewById(R.id.weather_api_text);
        pm25Text = (TextView) findViewById(R.id.weather_api_pm25_text);
        comfortText = (TextView) findViewById(R.id.weather_suggestion_comfort_text);
        carWashText = (TextView) findViewById(R.id.weather_suggestion_car_wash_text);
        sportText = (TextView) findViewById(R.id.weather_suggestion_sport_text);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.weather_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        drawerLayout = (DrawerLayout) findViewById(R.id.weather_drawer_layout);
        Button titleButton = (Button) findViewById(R.id.weather_title_button);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        swipeRefreshLayout.setOnRefreshListener(() -> {
            String weatherId = prefs.getString("weather_id", null);
            requestWeather(weatherId);
            Log.e(TAG, ": weather_id为：" + weatherId);
        });

        titleButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        Log.d(TAG, "onCreate: ");
    }

    /**
     * 每次重新进入天气页面的时候加载最新的天气
     */
    @Override
    protected void onResume() {
        super.onResume();
           /*加载bing壁纸*/
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Log.d(TAG, "有缓存的壁纸 ");
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }

        String weatherId;
        /*加载天气*/
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = ParseJsonUtils.handleWeatherResponse(weatherString);
            //从解析的json数据中获取weatherId
            // weatherId = weather.basic.weatherId;
            Log.d(TAG, "有缓存的weather: ");
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询天气
            // weatherId = getIntent().getStringExtra("weather_id");//获取Fragment传入的weatherId
            weatherId = prefs.getString("weather_id", null);
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }


    /**
     * j加载必应每日一图
     */
    private void loadBingPic() {
        String requestBinPic = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
        HttpUtils.sendOkHttpRequestWithGET(requestBinPic, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> MyToast.show("获取必应图片失败"));
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    final String bingPic = "https://www.bing.com" + ParseJsonUtils.handleBingPicResponse(response.body().string());
                    Log.d(TAG, "onResponse: " + bingPic);
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("bing_pic", bingPic);
                    editor.apply();
                    if (ActivityCollector.isActivityExist(WeatherActivity.class)) {
                        runOnUiThread(() -> Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg));
                    } else {
                        Log.e(TAG, "WeatherActivity已被注销，此处不应加载图片");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=45f7b6d8dc9c44f18e8d78c91339609b";
        Log.d(TAG, "服务器请求天气中..... ");
        HttpUtils.sendOkHttpRequestWithGET(weatherUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    MyToast.show("获取天气信息失败");
                    swipeRefreshLayout.setRefreshing(false);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = ParseJsonUtils.handleWeatherResponse(responseText);
                runOnUiThread(() -> {
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        showWeatherInfo(weather);
                    } else {
                        MyToast.show("获取天气信息失败");
                    }
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
        });
        loadBingPic();
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {
        if (weather != null && "ok".equals(weather.status)) {
            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.more.info;
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            forecastLayout.removeAllViews();
            for (Forecast forecast : weather.forecastList) {
                View view = LayoutInflater.from(this).inflate(R.layout.weather_forecast_item, forecastLayout, false);
                TextView dateText = view.findViewById(R.id.weather_forecast_data_text);
                TextView infoText = view.findViewById(R.id.weather_forecast_info_text);
                TextView maxText = view.findViewById(R.id.weather_forecast_max_text);
                TextView minText = view.findViewById(R.id.weather_forecast_min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max);
                minText.setText(forecast.temperature.min);
                forecastLayout.addView(view);
            }
            if (weather.aqi != null) {
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }
            String comfort = "舒适度：" + weather.suggestion.comfort.info;
            String carWash = "洗车指数：" + weather.suggestion.carWash.info;
            String sport = "运动建议：" + weather.suggestion.sport.info;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);
           /* Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);*/
        } else {
            MyToast.show("获取天气信息失败");
        }

    }
}
