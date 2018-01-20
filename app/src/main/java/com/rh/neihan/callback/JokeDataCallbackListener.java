package com.rh.neihan.callback;

import com.rh.neihan.gson.Joke;

/**
 * @author RH
 * @date 2018/1/19
 */
public interface JokeDataCallbackListener {
    void onSuccess(Joke joke);
    void onError(Exception e);
}
