package com.rh.materialdemo.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by RH on 2017/12/13.
 */

public class BingList {
    @SerializedName("images")
    public List<BingDaily> bingDailies;
}
