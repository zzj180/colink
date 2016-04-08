package com.aispeech.aios.adapter.model;

import com.aispeech.aios.adapter.R;

import java.util.HashMap;
import java.util.Map;

/**
 * @desc 天气资源类
 * @auth AISPEECH
 * @date 2016-01-14
 * @copyright aispeech.com
 */
public class WeatherResource {

    /**
     * 天气-图片 的键值对
     */
    private static final Map<String, Integer> weatherImgMap = new HashMap<String, Integer>() {
        {
            put("晴", R.drawable.weather_sunny);
            put("多云", R.drawable.weather_cloudy);
            put("晴间多云", R.drawable.weather_cloudy);
            put("阴", R.drawable.weather_overcast);

            put("雨夹雪", R.drawable.weather_rain_snow);

            put("小雨", R.drawable.weather_rain_small);
            put("中雨", R.drawable.weather_rain_midle);
            put("大雨", R.drawable.weather_rain_heavy);
            put("暴雨", R.drawable.weather_rain_storm);
            put("大暴雨", R.drawable.weather_rain_storm);
            put("特大暴雨", R.drawable.weather_rain_storm);

            put("小到中雨", R.drawable.weather_rain_midle);
            put("中到大雨", R.drawable.weather_rain_heavy);
            put("大到暴雨", R.drawable.weather_rain_storm);
            put("暴雨到大暴雨", R.drawable.weather_rain_storm);
            put("大暴雨到特大暴雨", R.drawable.weather_rain_storm);

            put("阵雨", R.drawable.weather_rain_shower);
            put("雷阵雨", R.drawable.weather_rain_thunder);
            put("雷阵雨伴有冰雹", R.drawable.weather_rain_thunder);

            put("小雪", R.drawable.weather_snow_small);
            put("中雪", R.drawable.weather_snow_midle);
            put("大雪", R.drawable.weather_snow_heavy);
            put("暴雪", R.drawable.weather_snow_heavy);
            put("阵雪", R.drawable.weather_snow_small);

            put("小到中雪", R.drawable.weather_snow_midle);
            put("中到大雪", R.drawable.weather_snow_heavy);
            put("大到暴雪", R.drawable.weather_snow_heavy);

            put("雾", R.drawable.weather_fog);
            put("冻雨", R.drawable.weather_rain_snow);

            put("浮尘", R.drawable.weather_sand_storm);
            put("扬沙", R.drawable.weather_sand_storm);
            put("沙尘暴", R.drawable.weather_sand_storm);
            put("强沙尘暴", R.drawable.weather_sand_storm);

            put("霾", R.drawable.weather_fog_haze);
        }

    };

    private static final Map<String, Integer> weatherImgSmallMap = new HashMap<String, Integer>() {
        {
            put("晴", R.drawable.weather_sunny_small);
            put("多云", R.drawable.weather_cloudy_small);
            put("晴间多云", R.drawable.weather_cloudy_small);
            put("阴", R.drawable.weather_overcast_small);

            put("阵雨", R.drawable.weather_rain_shower_small);
            put("雷阵雨", R.drawable.weather_thundershower_small);
            put("雷阵雨伴有冰雹", R.drawable.weather_thundershower_hail_small);

            put("雨夹雪", R.drawable.weather_rain_snow_small);

            put("小雨", R.drawable.weather_rain_light_small);
            put("中雨", R.drawable.weather_rain_midle_small);
            put("大雨", R.drawable.weather_rain_heavy_small);
            put("暴雨", R.drawable.weather_rain_storm_small);
            put("大暴雨", R.drawable.weather_rain_storm_small);
            put("特大暴雨", R.drawable.weather_rain_storm_small);

            put("小到中雨", R.drawable.weather_rain_midle_small);
            put("中到大雨", R.drawable.weather_rain_heavy_small);
            put("大到暴雨", R.drawable.weather_rain_storm_small);
            put("暴雨到大暴雨", R.drawable.weather_rain_storm_small);
            put("大暴雨到特大暴雨", R.drawable.weather_rain_storm_small);

            put("阵雪", R.drawable.weather_snow_light_small);
            put("小雪", R.drawable.weather_snow_light_small);
            put("中雪", R.drawable.weather_snow_midle_small);
            put("大雪", R.drawable.weather_snow_heavy_small);
            put("暴雪", R.drawable.weather_snow_storm_small);

            put("小到中雪", R.drawable.weather_snow_midle_small);
            put("中到大雪", R.drawable.weather_snow_heavy_small);
            put("大到暴雪", R.drawable.weather_snow_heavy_small);

            put("雾", R.drawable.weather_fog_small);
            put("冻雨", R.drawable.weather_rain_snow_small);

            put("浮尘", R.drawable.weather_sand_storm_small);
            put("扬沙", R.drawable.weather_sand_storm_small);
            put("沙尘暴", R.drawable.weather_sand_storm_small);
            put("强沙尘暴", R.drawable.weather_sand_storm_small);

            put("霾", R.drawable.weather_fog_haze_small);
        }

    };

    /**
     * 根据天气获取天气对应图片
     *
     * @param weather 如果包含暴雨，就直接当暴雨处理，其它的一样
     * @return 天气对应图片
     */
    public static int getImgByWeather(String weather) {
        int id = weatherImgMap.get(weather) == null ? R.drawable.weather_no_result : weatherImgMap.get(weather);
        return id;
    }

    public static int getImgSmallByWeather(String weather) {
        if (weatherImgSmallMap.get(weather) == null) {
            return R.drawable.weather_no_result_small;
        }
        return weatherImgSmallMap.get(weather);
    }
}
