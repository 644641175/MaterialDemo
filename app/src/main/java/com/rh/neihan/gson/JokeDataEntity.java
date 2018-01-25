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
    @SerializedName("has_more")
    private boolean has_more;
    @SerializedName("data")
    private List<JokeDataDataEntity> data;

    public boolean isHas_more() {
        return has_more;
    }

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
