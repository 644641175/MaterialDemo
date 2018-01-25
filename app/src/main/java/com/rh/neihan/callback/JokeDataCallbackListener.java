package com.rh.neihan.callback;

import com.rh.neihan.gson.Joke;
import com.rh.neihan.gson.JokeDataDataEntity;

import java.util.List;

/**
 * @author RH
 * @date 2018/1/19
 */
public interface JokeDataCallbackListener {
    void onSuccess( List<JokeDataDataEntity> jokeDataDataEntityList);
    void onError(Exception e);
    void onFail(String string);
}
