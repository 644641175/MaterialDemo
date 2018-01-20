package com.rh.neihan.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author RH
 * @date 2018/1/18
 */
public class JokeDataEntity {

    @SerializedName("tip")
    private String tip;

    @SerializedName("data")
    private List<JokeDataDataEntity> data;

    public List<JokeDataDataEntity> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "JokeDataEntity{" +
                "data=" + data +
                '}';
    }
}
