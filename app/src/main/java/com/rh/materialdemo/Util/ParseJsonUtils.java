package com.rh.materialdemo.Util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rh.materialdemo.bean.Diary;

import java.util.List;

/**
 * Created by RH on 2017/11/9.
 */

public class ParseJsonUtils {

    private static final String TAG = "ParseJsonUtils";

//使用GSON开源库来解析json
    public static void parseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Diary> diaryList = gson.fromJson(jsonData, new TypeToken<List<Diary>>(){}.getType());//需要解析的是一段JSON数组
        for (Diary diary: diaryList){
            Log.e(TAG, "parseJSONWithGSON: "+diary.getDate());
            Log.e(TAG, "parseJSONWithGSON: "+diary.getWeather());
            Log.e(TAG, "parseJSONWithGSON: "+diary.getLocation());
            Log.e(TAG, "parseJSONWithGSON: "+diary.getMood());
        }
       // Diary diary = gson.fromJson(jsonData,Diary.class);//需要解析的是一个对象
       // Log.e(TAG, "parseJSONWithGSON: "+diary.getDate()+"\n"+diary.getWeather()+"\n"+diary.getLocation()+"\n"+diary.getMood());

    }

}
