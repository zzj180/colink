/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : WeatherContentView.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-11-15
 */
package com.unisound.unicar.gui.view;

import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.WeatherDay;
import com.unisound.unicar.gui.model.WeatherInfo;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.Util;

/**
 * @Module : 天气
 * @Comments : 描述 天气模块view显示
 * @Author : Zhuoran
 * @CreateDate : 2015-07-01 2015-07-01: 实现基本功能
 */

public class WeatherContentView extends FrameLayout implements ISessionView {
    public static final String TAG = "WeatherContentView";

    private HashMap<String, Integer> mBigImageNameIdMap = null;

    private String TODAY_STRING = getResources().getString(R.string.today);
    private String TOMMOROW_STRING = getResources().getString(R.string.tomorrow);
    private String DAY_AFTER_TOMMOROW_STRING = getResources()
            .getString(R.string.day_after_tomorrow);

    private String sunny = getResources().getString(R.string.weather_sunny);
    private String cloudy = getResources().getString(R.string.weather_cloudy);
    private String overcast = getResources().getString(R.string.weather_overcast);
    private String foggy = getResources().getString(R.string.weather_foggy);
    private String dustblow = getResources().getString(R.string.weather_dustblow);
    private String dust = getResources().getString(R.string.weather_dust);
    private String sandstorm = getResources().getString(R.string.weather_sandstorm);
    private String strong_sandstorm = getResources().getString(R.string.weather_sandstorm_strong);
    private String icerain = getResources().getString(R.string.weather_icerain);
    private String shower = getResources().getString(R.string.weather_shower);
    private String thunder_rain = getResources().getString(R.string.weather_thunder_rain);
    private String hail = getResources().getString(R.string.weather_hail);
    private String sleety = getResources().getString(R.string.weather_sleety);
    private String light_rain = getResources().getString(R.string.weather_rain_light);
    private String moderate_rain = getResources().getString(R.string.weather_rain_moderate);
    private String heavy_rain = getResources().getString(R.string.weather_rain_heavy);
    private String rainstorm = getResources().getString(R.string.weather_rainstorm);
    private String big_rainstorm = getResources().getString(R.string.weather_rainstorm_big);
    private String super_rainstorm = getResources().getString(R.string.weather_rainstorm_super);
    private String snow_shower = getResources().getString(R.string.weather_snow_shower);
    private String light_snow = getResources().getString(R.string.weather_snow_light);
    private String moderate_snow = getResources().getString(R.string.weather_snow_moderate);
    private String heavy_snow_big = getResources().getString(R.string.weather_snow_heavy);
    private String blizzard = getResources().getString(R.string.weather_blizzard);
    private String haze = getResources().getString(R.string.weather_haze);

    private void initBigImageNameIdMap() {
        if (mBigImageNameIdMap == null) {
            mBigImageNameIdMap = new HashMap<String, Integer>();
        }
        mBigImageNameIdMap.clear();
        mBigImageNameIdMap.put(sunny, R.drawable.ic_weather_sunny);
        mBigImageNameIdMap.put(cloudy, R.drawable.ic_weather_cloudy);
        mBigImageNameIdMap.put(overcast, R.drawable.ic_weather_overcast);
        mBigImageNameIdMap.put(foggy, R.drawable.ic_weather_foggy);
        mBigImageNameIdMap.put(dustblow, R.drawable.ic_weather_dustblow);
        mBigImageNameIdMap.put(dust, R.drawable.ic_weather_dust);
        mBigImageNameIdMap.put(sandstorm, R.drawable.ic_weather_sandstorm);
        mBigImageNameIdMap.put(strong_sandstorm, R.drawable.ic_weather_sandstorm_strong);
        mBigImageNameIdMap.put(icerain, R.drawable.ic_weather_icerain);
        mBigImageNameIdMap.put(shower, R.drawable.ic_weather_shower);
        mBigImageNameIdMap.put(thunder_rain, R.drawable.ic_weather_thunder_rain);
        mBigImageNameIdMap.put(hail, R.drawable.ic_weather_hail);
        mBigImageNameIdMap.put(sleety, R.drawable.ic_weather_sleety);
        mBigImageNameIdMap.put(light_rain, R.drawable.ic_weather_rain_light);
        mBigImageNameIdMap.put(moderate_rain, R.drawable.ic_weather_rain_moderate);
        mBigImageNameIdMap.put(heavy_rain, R.drawable.ic_weather_rain_heavy);
        mBigImageNameIdMap.put(rainstorm, R.drawable.ic_weather_rainstorm);
        mBigImageNameIdMap.put(big_rainstorm, R.drawable.ic_weather_rainstorm_big);
        mBigImageNameIdMap.put(super_rainstorm, R.drawable.ic_weather_rainstorm_super);
        mBigImageNameIdMap.put(snow_shower, R.drawable.ic_weather_snow_shower);
        mBigImageNameIdMap.put(light_snow, R.drawable.ic_weather_snow_light);
        mBigImageNameIdMap.put(moderate_snow, R.drawable.ic_weather_snow_moderate);
        mBigImageNameIdMap.put(heavy_snow_big, R.drawable.ic_weather_snow_heavy);
        mBigImageNameIdMap.put(blizzard, R.drawable.ic_weather_blizzard);
        mBigImageNameIdMap.put(haze, R.drawable.ic_weather_haze);
    }

    private String[] sDayOfWeekNames;

    private final int TOTAL_NUMBER = 4;
    private final int FIRST_DAY = 0;
    private final int WEEKDAY_NUM = 7;
    private final int WEEK_TEMP_NUM = 6;

    private WeatherInfo mWeatherInfo;
    private int currentTemperature = -1;
    private String weather = "";
    private int lowestTemperature = -1;
    private int highestTemperature = -1;
    private String wind = "";
    private int pm25 = -1;
    private String carWashIndex = "";

    private TextView tv_weather_today_city;
    private TextView tv_weather_today_currentTem;
    private TextView tv_weather_today_weatherDesc;
    private TextView tv_weather_today_windDesc;
    private ImageView iv_weather_today_weather;
    private TextView tv_weather_today_pm25;
    private TextView tv_weather_today_airquality;
    private String airQuality;
    private TextView tv_weather_today_carWashIndex;
    private TextView tv_weather_today_updateTime;

    private TextView tv_weather_otherday_city;
    private RelativeLayout rl_weather_other_first;
    private TextView tv_otherday_title_first;
    private ImageView iv_otherday_weather_first;
    private TextView tv_otherday_weather_first;
    private TextView tv_otherday_temperature_first;

    private RelativeLayout rl_weather_other_second;
    private TextView tv_otherday_title_second;
    private ImageView iv_otherday_weather_second;
    private TextView tv_otherday_weather_second;
    private TextView tv_otherday_temperature_second;

    private RelativeLayout rl_weather_other_third;
    private TextView tv_otherday_title_third;
    private ImageView iv_otherday_weather_third;
    private TextView tv_otherday_weather_third;
    private TextView tv_otherday_temperature_third;

    private RelativeLayout rl_weather_other_fourth;
    private TextView tv_otherday_title_fourth;
    private ImageView iv_otherday_weather_fourth;
    private TextView tv_otherday_weather_fourth;
    private TextView tv_otherday_temperature_fourth;

    private RelativeLayout[] rl_weather_others;
    private TextView[] tv_otherday_titles;
    private ImageView[] iv_otherday_weathers;
    private TextView[] tv_otherday_weathers;
    private TextView[] tv_otherday_temperatures;


    public WeatherContentView(Context context, AttributeSet attrs, int defStyle, int focusIndex) {
        super(context, attrs, defStyle);
        Logger.d(TAG, "!--->WeatherContentView----focusIndex = " + focusIndex);
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (focusIndex == FIRST_DAY) {
            inflater.inflate(R.layout.weather_view_v3_today, this, true);
        } else {
            inflater.inflate(R.layout.weather_view_v3_otherday, this, true);
        }
        findViews();
        sDayOfWeekNames = context.getResources().getStringArray(R.array.days_of_week);
        initBigImageNameIdMap();
    }

    public WeatherContentView(Context context, AttributeSet attrs, int focusIndex) {
        this(context, attrs, 0, focusIndex);
    }

    public WeatherContentView(Context context, int focusIndex) {
        this(context, null, focusIndex);
    }

    private void findViews() {
        // -------------------------------------------------------------------------------------
        // find today real view

        tv_weather_today_city = (TextView) findViewById(R.id.tv_weather_today_city);
        tv_weather_today_currentTem = (TextView) findViewById(R.id.tv_weather_today_currentTem);
        tv_weather_today_weatherDesc = (TextView) findViewById(R.id.tv_weather_today_weatherDesc);
        tv_weather_today_windDesc = (TextView) findViewById(R.id.tv_weather_today_windDesc);
        iv_weather_today_weather = (ImageView) findViewById(R.id.iv_weather_today_weather);
        tv_weather_today_pm25 = (TextView) findViewById(R.id.tv_weather_today_pm25);
        tv_weather_today_airquality = (TextView) findViewById(R.id.tv_weather_today_airquality);
        tv_weather_today_carWashIndex = (TextView) findViewById(R.id.tv_weather_today_carWashIndex);
        tv_weather_today_updateTime = (TextView) findViewById(R.id.tv_weather_today_updateTime);

        // ----------------------------------------------------------------------------------------
        // find otherday real view

        tv_weather_otherday_city = (TextView) findViewById(R.id.tv_weather_otherday_city);

        rl_weather_other_first = (RelativeLayout) findViewById(R.id.rl_weather_other_first);
        tv_otherday_title_first = (TextView) findViewById(R.id.tv_otherday_title_first);
        iv_otherday_weather_first = (ImageView) findViewById(R.id.iv_otherday_weather_first);
        tv_otherday_weather_first = (TextView) findViewById(R.id.tv_otherday_weather_first);
        tv_otherday_temperature_first = (TextView) findViewById(R.id.tv_otherday_temperature_first);

        rl_weather_other_second = (RelativeLayout) findViewById(R.id.rl_weather_other_second);
        tv_otherday_title_second = (TextView) findViewById(R.id.tv_otherday_title_second);
        iv_otherday_weather_second = (ImageView) findViewById(R.id.iv_otherday_weather_second);
        tv_otherday_weather_second = (TextView) findViewById(R.id.tv_otherday_weather_second);
        tv_otherday_temperature_second =
                (TextView) findViewById(R.id.tv_otherday_temperature_second);

        rl_weather_other_third = (RelativeLayout) findViewById(R.id.rl_weather_other_third);
        tv_otherday_title_third = (TextView) findViewById(R.id.tv_otherday_title_third);
        iv_otherday_weather_third = (ImageView) findViewById(R.id.iv_otherday_weather_third);
        tv_otherday_weather_third = (TextView) findViewById(R.id.tv_otherday_weather_third);
        tv_otherday_temperature_third = (TextView) findViewById(R.id.tv_otherday_temperature_third);

        rl_weather_other_fourth = (RelativeLayout) findViewById(R.id.rl_weather_other_fourth);
        tv_otherday_title_fourth = (TextView) findViewById(R.id.tv_otherday_title_fourth);
        iv_otherday_weather_fourth = (ImageView) findViewById(R.id.iv_otherday_weather_fourth);
        tv_otherday_weather_fourth = (TextView) findViewById(R.id.tv_otherday_weather_fourth);
        tv_otherday_temperature_fourth =
                (TextView) findViewById(R.id.tv_otherday_temperature_fourth);


        rl_weather_others =
                new RelativeLayout[] {rl_weather_other_first, rl_weather_other_second,
                        rl_weather_other_third, rl_weather_other_fourth};
        tv_otherday_titles =
                new TextView[] {tv_otherday_title_first, tv_otherday_title_second,
                        tv_otherday_title_third, tv_otherday_title_fourth};
        iv_otherday_weathers =
                new ImageView[] {iv_otherday_weather_first, iv_otherday_weather_second,
                        iv_otherday_weather_third, iv_otherday_weather_fourth};
        tv_otherday_weathers =
                new TextView[] {tv_otherday_weather_first, tv_otherday_weather_second,
                        tv_otherday_weather_third, tv_otherday_weather_fourth};
        tv_otherday_temperatures =
                new TextView[] {tv_otherday_temperature_first, tv_otherday_temperature_second,
                        tv_otherday_temperature_third, tv_otherday_temperature_fourth};
    }

    public void setWeatherInfo(WeatherInfo weatherInfo, String mCity, String updateTime) {
        this.mWeatherInfo = weatherInfo;

        WeatherDay todayWeather = weatherInfo.getWeatherDay(0);

        currentTemperature = todayWeather.getCurrentTemperature();
        weather = todayWeather.getWeather();
        lowestTemperature = todayWeather.getLowestTemperature();
        highestTemperature = todayWeather.getHighestTemperature();
        wind = todayWeather.getWind();
        pm25 = todayWeather.getPm25();
        carWashIndex = todayWeather.getCarWashIndex();
        airQuality = todayWeather.getAirQuality();

        // --------------------------------------------------------------------
        // 设置today
        textViewSetText(tv_weather_today_city, mCity);
        textViewSetText(tv_weather_today_currentTem, currentTemperature + "°C");

        String weatherDesc = weather + " " + lowestTemperature + "-" + highestTemperature + "°C";
        textViewSetText(tv_weather_today_weatherDesc, weatherDesc);

        textViewSetText(tv_weather_today_windDesc, wind);

        imageViewSetImageResource(iv_weather_today_weather,
                getWeatherImage(todayWeather.getImageTitle()));

        String pm25Desc = getResources().getString(R.string.weather_pm25) + pm25;
        textViewSetText(tv_weather_today_pm25, pm25Desc);

        String airQualityDesc = getResources().getString(R.string.weather_air_quality) + airQuality;
        textViewSetText(tv_weather_today_airquality, airQualityDesc);

        String carWashIndexDesc =
                getResources().getString(R.string.weather_car_wash) + carWashIndex;
        textViewSetText(tv_weather_today_carWashIndex, carWashIndexDesc);

        // -----------------------------------------------------------------------------
        // 设置其他日子
        textViewSetText(tv_weather_otherday_city, mCity);

        Calendar calendar = Calendar.getInstance();
        calendar.set(todayWeather.getYear(), todayWeather.getMonth() - 1, todayWeather.getDay());
        int days = Util.daysOfTwo(Calendar.getInstance(), calendar);

        for (int i = 0; i < TOTAL_NUMBER; i++) {
            otherDaySetUi(i);
        }
        // -----------------------------------------------------------------------------
        // today和其它日子的共同部分
        String updateTimeDesc = getResources().getString(R.string.weather_update_time) + updateTime;
        textViewSetText(tv_weather_today_updateTime, updateTimeDesc);
    }

    private void otherDaySetUi(int index) {
        WeatherDay tempDay = mWeatherInfo.getWeatherDay(index);
        if (tempDay.isFocusDay()) {
            relativeLayoutSetBackgroudColor(rl_weather_others[index],
                    getResources().getColor(R.color.weather_focus_day));
        }
        textViewSetText(tv_otherday_titles[index], getOtherDayTitle(mWeatherInfo, index));
        imageViewSetImageResource(iv_otherday_weathers[index],
                getWeatherImage(tempDay.getImageTitle()));
        textViewSetText(tv_otherday_weathers[index], tempDay.getWeather());
        String otherDay_temperatureDesc =
                tempDay.getLowestTemperature() + "-" + tempDay.getHighestTemperature() + "°C";
        textViewSetText(tv_otherday_temperatures[index], otherDay_temperatureDesc);
    }

    private String getOtherDayTitle(WeatherInfo weatherInfo, int index) {
        switch (index) {
            case 0:
                return getResources().getString(R.string.weather_today);
            case 1:
                return getResources().getString(R.string.weather_tomorrow);
            default:
                return sDayOfWeekNames[(mWeatherInfo.getWeatherDay(index).getDayOfWeek() + WEEK_TEMP_NUM)
                        % WEEKDAY_NUM];
        }
    }

    /**
     * get Weather Image resource id XD modify 20150729
     * 
     * @param imgTitle
     * @return
     */
    private Integer getWeatherImage(String imgTitle) {
        if (null == mBigImageNameIdMap) {
            Logger.e(TAG, "!--->mBigImageNameIdMap is null");
            return R.drawable.ic_weather_overcast;
        } else {
            return mBigImageNameIdMap.containsKey(imgTitle)
                    ? mBigImageNameIdMap.get(imgTitle)
                    : mBigImageNameIdMap.get(overcast);
        }
    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    @Override
    public void release() {
        Logger.d(TAG, "!--->release----");
        sDayOfWeekNames = null;
        /* < XD 20150729 added Begin */
        if (null != mBigImageNameIdMap) {
            mBigImageNameIdMap.clear();
            mBigImageNameIdMap = null;
        }
        rl_weather_others = null;
        tv_otherday_titles = null;
        iv_otherday_weathers = null;
        tv_otherday_weathers = null;
        tv_otherday_temperatures = null;
        /* XD 20150729 added End > */
    }

    private void textViewSetText(TextView mTextView, String content) {
        if (mTextView != null) {
            mTextView.setText(content);
        }
    }

    private void imageViewSetImageResource(ImageView mImageView, int resId) {
        if (mImageView != null) {
            mImageView.setImageResource(resId);
        }
    }

    private void relativeLayoutSetBackgroudColor(RelativeLayout mRelativeLayout, int colorId) {
        if (mRelativeLayout != null) {
            mRelativeLayout.setBackgroundColor(colorId);
        }
    }
}
