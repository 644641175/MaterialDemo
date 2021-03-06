package com.rh.materialdemo.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *
 * @author RH
 * @date 2017/11/16
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}

/*
返回的json数据的大致格式
{"HeWeather":[
        {
        "status":"ok"
        "basic":{},
        "aqi":{},
        "now":{},
        "suggestion":{},
        "daily_forecast":[]
        }
        ]
  }      */
