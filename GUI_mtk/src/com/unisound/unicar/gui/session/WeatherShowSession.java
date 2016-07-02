/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : WeatherShowSession.java
 * @ProjectName : UniCarGUI
 * @PakageName : com.unisound.unicar.gui.session
 * @Author : CavanShi
 * @CreateDate : 2013-4-22
 */
package com.unisound.unicar.gui.session;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.WeatherDay;
import com.unisound.unicar.gui.model.WeatherInfo;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.WeatherContentView;

/**
 * @Module :
 * @Comments :
 * @Author : CavanShi
 * @CreateDate : 2013-4-22
 * @ModifiedBy : xiaodong.he
 * @ModifiedDate: 2015-11-23
 * @Modified:
 */
public class WeatherShowSession extends BaseSession {
    public static final String TAG = "WeatherShowSession";

    private String mCity;
    private String mCityCode;

    private WeatherContentView mWeatherView;

    private JSONObject mWeatherResultJson;

    public WeatherShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);

    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        mWeatherResultJson = mDataObject;
        showWeather();
    }

    private WeatherDay getweatherItemFromJsonObject(JSONObject jObject) {
        WeatherDay w = new WeatherDay();
        String year = JsonTool.getJsonValue(jObject, "year", "0");
        String month = JsonTool.getJsonValue(jObject, "month", "0");
        String day = JsonTool.getJsonValue(jObject, "day", "0");
        String dayofweek = JsonTool.getJsonValue(jObject, "dayOfWeek", "2");
        String weather = JsonTool.getJsonValue(jObject, "weather", "");
        String highTemp = JsonTool.getJsonValue(jObject, "highestTemperature", "0");
        String lowTemp = JsonTool.getJsonValue(jObject, "lowestTemperature", "0");
        String currentTemp = JsonTool.getJsonValue(jObject, "currentTemperature", "0");
        String wind = JsonTool.getJsonValue(jObject, "wind", "");
        String pm25 = JsonTool.getJsonValue(jObject, "pm2_5", "0");
        String carWashIndex = JsonTool.getJsonValue(jObject, "carWashIndex", "");
        String airQuality = JsonTool.getJsonValue(jObject, "quality", "");

        w.setYear(Integer.parseInt(year));
        w.setMonth(Integer.parseInt(month));
        w.setDay(Integer.parseInt(day));
        /* 2013.05.13 added by shichao for amend weather */
        w.setDayOfWeek(Integer.parseInt(dayofweek));
        String s = modfifyWeatherImage(weather);
        w.setWeather(s);
        w.setImageTitle(s);
        /* end */
        w.setTemperatureRange(Integer.parseInt(highTemp), Integer.parseInt(lowTemp));
        w.setCurrentTemp(Integer.parseInt(currentTemp));
        w.setWind(wind, null);

        w.setPm25(Integer.parseInt(pm25));
        w.setCarWashIndex(carWashIndex);
        w.setAirQuality(airQuality);

        return w;
    }

    private String modfifyWeatherImage(String weather) {
        String weatherDao = mContext.getString(R.string.to);
        String weatherZhuan = mContext.getString(R.string.turn);

        if (weather != null && !(weather = weather.trim()).equals("")) {
            int zhuanIndex = -1;
            int daoIndex = -1;

            if ((zhuanIndex = weather.indexOf(weatherZhuan)) > 0) {
                weather = weather.substring(0, zhuanIndex);
            }

            if ((daoIndex = weather.indexOf(weatherDao)) > 0) {
                weather = weather.substring(daoIndex + weatherDao.length(), weather.length());
            }
        }
        return weather;
    }

    @SuppressWarnings("deprecation")
	private void showWeather() {
        mCity = JsonTool.getJsonValue(mWeatherResultJson, "cityName");
        mCityCode = JsonTool.getJsonValue(mWeatherResultJson, "cityCode");

        String cityStr = mContext.getString(R.string.city);
        if (mCity.endsWith(cityStr)) {
            mCity = mCity.substring(0, mCity.lastIndexOf(cityStr));
        }

        WeatherInfo weatherInfo = new WeatherInfo(mCity, mCityCode);

        JSONArray weatherArray = JsonTool.getJsonArray(mWeatherResultJson, "weatherDays");
        String updateTime = JsonTool.getJsonValue(mWeatherResultJson, "updateTime");
        String focusIndexString = JsonTool.getJsonValue(mWeatherResultJson, "focusDateIndex", "0");
        int focusIndex = Integer.parseInt(focusIndexString);

        weatherInfo.setUpdateTime(updateTime);

        Logger.d(TAG, "weatherArray --- " + weatherArray.toString());

        if (weatherArray != null && weatherArray.length() > 0) {
            for (int i = 0; i < weatherArray.length() && i < WeatherInfo.DAY_COUNT; i++) {
                JSONObject object = JsonTool.getJSONObject(weatherArray, i);
                WeatherDay day = getweatherItemFromJsonObject(object);
                if (focusIndex == i) {
                    day.setFocusDay();
                }
                weatherInfo.setWeatherDay(day, i);
            }
        } else {
            String answer = mContext.getString(R.string.no_weather_info);
            addAnswerViewText(answer);
            Logger.d(TAG, "--WeatherShowSession mAnswer : " + mAnswer + "--");
            return;
        }
        Logger.d(TAG, "weatherInfo --- " + weatherInfo.toString());
        mWeatherView = new WeatherContentView(mContext, focusIndex);
        mWeatherView.setWeatherInfo(weatherInfo, mCity, updateTime);
        addAnswerView(mWeatherView);
        addAnswerViewText(mAnswer);
    }

    @Override
    public void release() {
        if (null != mWeatherView) {
            mWeatherView.release(); // XD added 20150729
            mWeatherView = null;
        }
        super.release();
    }

    @Override
    public void onTTSEnd() {
        Logger.d(TAG, "onTTSEnd");
        super.onTTSEnd();
        // XD 20151123 modify
        if (UserPerferenceUtil.VALUE_VERSION_MODE_EXP == UserPerferenceUtil
                .getVersionMode(mContext)) {
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_RELEASE_ONLY);
        } else {
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE_DELAY);
        }
    }
}
