package com.rh.materialdemo.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *
 * @author RH
 * @date 2017/12/13
 */

public class BingList {
    @SerializedName("images")
    public List<BingDaily> bingDailies;
}
