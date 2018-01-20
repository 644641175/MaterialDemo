package com.rh.neihan.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author RH
 * @date 2018/1/18
 */

public class Joke {
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private JokeDataEntity data;

    public JokeDataEntity getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
