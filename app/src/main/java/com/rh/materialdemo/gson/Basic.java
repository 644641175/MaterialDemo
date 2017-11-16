package com.rh.materialdemo.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by RH on 2017/11/16.
 */
/*
解析以下json数据，创建gson实体类并建立如下映射关系
"basic":{
    "city":"苏州",
     "id":"CN101190401",
      "update":{
        "loc":"2017-11-16 09:33"
        }
        }
*/
public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }

}
