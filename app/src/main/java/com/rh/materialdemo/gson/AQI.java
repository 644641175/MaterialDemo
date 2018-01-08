package com.rh.materialdemo.gson;

/**
 * @author RH
 * @date 2017/11/16
 * <p>
 * <p>
 * <p>
 * 使用gson解析以下json数据，因为json中的字段与java中的字段一致，因此无需使用@SerializedName注解
 * "api":{
 * "city":{
 * "aqi":"44",
 * "pm25":"13"
 * }
 * }
 */

public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
