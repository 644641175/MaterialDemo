package com.rh.materialdemo.bean;

import java.io.Serializable;

/**
 * @author RH
 * @date 2017/11/7
 * <p>
 * <p>
 * Serializable序列化，将对象转换成可存储或可传输的状态
 */
public class Diary implements Serializable {
    private String date;
    private String weather;
    private String location;
    private String mood;

    public Diary() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }
}
