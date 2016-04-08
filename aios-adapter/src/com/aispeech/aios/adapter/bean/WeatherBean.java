package com.aispeech.aios.adapter.bean;

import java.util.List;

/**
 * Created by Spring on 2016/2/16.
 * to do:
 */
public class WeatherBean extends BaseBean{

    private int searchDay;
    private List<WeatherData> mWeatherDatas;

    public int getSearchDay() {
        return searchDay;
    }

    public void setSearchDay(int searchDay) {
        this.searchDay = searchDay;
    }

    public List<WeatherData> getWeatherDatas() {
        return mWeatherDatas;
    }

    public void setWeatherDatas(List<WeatherData> mWeatherDatas) {
        this.mWeatherDatas = mWeatherDatas;
    }

    public static class WeatherData {

        private String date;
        private String weather;
        private String temperature;
        private String wind;

        public void setDate(String date) {
            this.date = date;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }

        public void setWind(String wind) {
            this.wind = wind;
        }

        public String getDate() {
            return date;
        }

        public String getWeather() {
            return weather;
        }

        public String getTemperature() {
            return temperature;
        }

        public String getWind() {
            return wind;
        }
    }
}
