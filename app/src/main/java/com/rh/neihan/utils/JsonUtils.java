package com.rh.neihan.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.rh.neihan.gson.Joke;
import com.rh.neihan.gson.JokeDataDataEntity;
import com.rh.neihan.gson.JokeDataDataGroupEntity;

/**
 * @author RH
 * @date 2018/1/18
 */
public class JsonUtils {

    public static Joke parseJokeData(String string){
        Gson gson = new Gson();
        Joke joke = gson.fromJson(string,Joke.class);

      /*  for (JokeDataDataEntity jokeDataDataEntity : joke.getData().getData()){
            Log.i("JsonUtils", "parseJokeData: "+jokeDataDataEntity.getGroup().toString() );
        }*/
        return joke;
    }
}
