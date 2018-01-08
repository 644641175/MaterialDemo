package com.rh.materialdemo.db;

import org.litepal.crud.DataSupport;

/**
 * @author RH
 * @date 2017/11/15
 */

public class County extends DataSupport {
    private int id;
    /**
     * 记录县的名字
     */
    private String countyName;
    /**
     * 记录县对应的天气id
     */
    private String weatherId;
    /**
     * 记录当前县所属市的id值
     */
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        return "County{" +
                "id=" + id +
                ", countyName='" + countyName + '\'' +
                ", weatherId='" + weatherId + '\'' +
                ", cityId=" + cityId +
                '}';
    }
}
