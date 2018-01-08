package com.rh.materialdemo.gson;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author RH
 * @date 2017/11/16
 *
 * 需要解析一个"daily_forecast"数组，Suggestion为数组中的一项
 */
public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;
    public Sport sport;
    public class Comfort{
        @SerializedName("txt")
        public String info;
    }
    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
