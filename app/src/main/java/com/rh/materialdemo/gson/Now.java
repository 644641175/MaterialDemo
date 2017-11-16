package com.rh.materialdemo.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by RH on 2017/11/16.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
    /*
    "now":{
        "tmp":"29",
        "cond":{
        "txt":"阵雨"
                }
       }
     */
