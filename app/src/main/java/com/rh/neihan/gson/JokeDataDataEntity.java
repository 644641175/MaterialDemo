package com.rh.neihan.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author RH
 * @date 2018/1/20
 */
public class JokeDataDataEntity {
    @SerializedName("group")
    private JokeDataDataGroupEntity group;

    public JokeDataDataGroupEntity getGroup() {
        return group;
    }
}
