package com.rh.materialdemo.Util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rh.materialdemo.bean.Diary;
import com.rh.materialdemo.db.City;
import com.rh.materialdemo.db.County;
import com.rh.materialdemo.db.Province;
import com.rh.materialdemo.gson.BingDaily;
import com.rh.materialdemo.gson.BingList;
import com.rh.materialdemo.gson.DailyArticle;
import com.rh.materialdemo.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author RH
 * @date 2017/11/9
 */

public class ParseJsonUtils {

    private static final String TAG = "ParseJsonUtils";

    /**
     * 使用GSON开源库来解析json
     */
    public static void parseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Diary> diaryList = gson.fromJson(jsonData, new TypeToken<List<Diary>>() {
        }.getType());//需要解析的是一段JSON数组
        for (Diary diary : diaryList) {
            Log.d(TAG, "parseJSONWithGSON: " + diary.getDate());
            Log.d(TAG, "parseJSONWithGSON: " + diary.getWeather());
            Log.d(TAG, "parseJSONWithGSON: " + diary.getLocation());
            Log.d(TAG, "parseJSONWithGSON: " + diary.getMood());
        }
        // Diary diary = gson.fromJson(jsonData,Diary.class);//需要解析的是一个对象
        // Log.e(TAG, "parseJSONWithGSON: "+diary.getDate()+"\n"+diary.getWeather()+"\n"+diary.getLocation()+"\n"+diary.getMood());

    }

    /**
     * 解析和处理服务器返回的省级数据
     * 因服务器返回的参数字段与java bean中的参数不一致，此处采用JSONObject来解析json数据
     * 若采用gson来解析需要使用@SerializedName注释，让JSON字段和Java字段之间建立映射关系
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty((response))) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.setProvinceName(provinceObject.getString("name"));
                    province.save();//将省份数据存入数据库
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty((response))) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                    Log.d(TAG, "保存市信息到数据库: " + city.toString());
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty((response))) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCountyName(countyObject.getString("name"));
                    county.setCityId(cityId);
                    county.save();
                    Log.d(TAG, "保存县信息到数据库: " + county.toString());
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将返回的JSON数据解析成Weather实体类
     */

    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析必应返回的图片消息
     */
    public static String handleBingPicResponse(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("images");
        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
        return jsonObject1.getString("url");
    }

    /**
     * 解析必应图片数组
     */
    public static List<BingDaily> handleBingPicResponseWithGson(String string) {
        Gson gson = new Gson();
        //需要解析的是一个数组对象
        BingList bingList = gson.fromJson(string, BingList.class);
        /*for (BingDaily bingDaily : bingList.bingDailies){
            Log.e(TAG, "解析必应图片数组: " + bingDaily.getDate());
            Log.e(TAG, "解析必应图片数组: " + bingDaily.getUrl());
        }*/
        return bingList.bingDailies;
    }

    public static DailyArticle handleDailyArticle(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        String data = jsonObject.getString("data");
        Gson gson = new Gson();
        DailyArticle dailyArticle = gson.fromJson(data, DailyArticle.class);
        return dailyArticle;
    }

}