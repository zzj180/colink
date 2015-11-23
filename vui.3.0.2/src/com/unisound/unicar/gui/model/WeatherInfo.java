/**
 * Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : WeatherInfo.java
 * @ProjectName : V Plus 1.0
 * @PakageName : cn.yunzhisheng.vui.assistant.weather
 * @Author : Dancindream
 * @CreateDate : 2012-5-25
 */
package com.unisound.unicar.gui.model;

public class WeatherInfo {
    public static final String TAG = "WeatherInfo";

    public static final int DAY_COUNT = 4;
    private static final int DAY_SIZE = 5;

    private WeatherDay[] mWeatherDays = null;
    private String mCityName = "";
    private String mCityId = "";
    private String mUpdateTime = "";

    public WeatherInfo(String cityName, String cityId) {
        mCityName = cityName;
        mCityId = cityId;

        mWeatherDays = new WeatherDay[DAY_SIZE];
    }

    public String getCityName() {
        return mCityName;
    }

    public String getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        mUpdateTime = updateTime;
    }

    public String getCityId() {
        return mCityId;
    }

    public void setWeatherDay(WeatherDay weatherDay, int whichDay) {
        whichDay = Math.abs(whichDay) % DAY_SIZE;
        mWeatherDays[whichDay] = weatherDay;
    }

    public WeatherDay getWeatherFocusDay() {
        for (WeatherDay day : mWeatherDays) {
            if (day.isFocusDay()) {
                return day;
            }
        }
        return null;
    }

    public WeatherDay getWeatherDay(int whichDay) {
        whichDay = Math.abs(whichDay) % DAY_SIZE;
        return mWeatherDays[whichDay];
    }
}
