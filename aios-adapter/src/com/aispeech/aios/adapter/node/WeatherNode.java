package com.aispeech.aios.adapter.node;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.bean.WeatherBean;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.util.StringUtil;
import com.aispeech.aios.adapter.control.UITimer;
import com.aispeech.aios.adapter.control.UITimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 天气领域对应节点
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class WeatherNode extends BaseNode {

    private static final String TAG = "AIOS-Adapter-WeatherNode";

    private static WeatherNode mInstance;

    public static synchronized WeatherNode getInstance() {
        if (mInstance == null) {
            mInstance = new WeatherNode();
        }

        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public String getName() {
        return AiosApi.Weather.NAME;
    }

    @Override
    public void onJoin() {
        AILog.i(TAG, "onJoin");
        super.onJoin();
        bc.subscribe("data.weather.query.result");
        bc.subscribe(AiosApi.Other.AIOS_STATE);
    }

    @Override
    public void onMessage(String topic, byte[]... parts) throws Exception {
        super.onMessage(topic, parts);
        AILog.i(TAG, topic, parts);
        if (topic.equals("data.weather.query.result")) {
            UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI);
            queryWeatherResult(parts);
        } else if (topic.equals(AiosApi.Player.STATE) && StringUtil.getEncodedString(parts[0]).equals(AiosApi.Player.STATE_WAIT)) {
            UITimer.getInstance().executeUITask(new UITimerTask() , UITimer.DELAY_SHORT , false);
            bc.unsubscribe(AiosApi.Player.STATE);
        } else if (topic.equals(AiosApi.Other.AIOS_STATE) && StringUtil.getEncodedString(parts[0]).equals("awake")) {
            UITimer.getInstance().cancelTask();
            bc.unsubscribe(AiosApi.Player.STATE);
        }

    }

    @Override
    public BusClient.RPCResult onCall(final String url, final byte[]... args) throws Exception {
        AILog.i(TAG, url, args);
        bc.call("/data" + url, args);
        UiEventDispatcher.notifyUpdateUI(UIType.LoadingUI);
        return null;
    }

    private void queryWeatherResult(byte[]... parts) throws Exception {
        int errId = Integer.valueOf(new String(parts[1]));
        if (errId == 0) {
            JSONObject jsonObject = new JSONObject(new String(parts[0]));
            JSONObject resultJson = jsonObject.optJSONObject("result");

            WeatherBean weatherBean = new WeatherBean();
            weatherBean.setSearchDay(resultJson.optString("searchDay"));

            JSONObject todayObject = resultJson.optJSONObject("today");
            WeatherBean.WeatherData today = new WeatherBean.WeatherData();
            today.setDate(todayObject.optString("date"));
            today.setWeather(todayObject.optString("weather"));
            today.setTemperature(todayObject.optString("temperature"));
            today.setWind(todayObject.optString("wind"));
            weatherBean.setToday(today);

            String area = resultJson.optString("area");
            weatherBean.setTitle(area.substring(area.lastIndexOf(">") + 1, area.length()));

            JSONArray weathers = resultJson.optJSONArray("future");
            JSONObject object;
            WeatherBean.WeatherData mWeatherData;
            List<WeatherBean.WeatherData> weatherDatas = new ArrayList<WeatherBean.WeatherData>();

            for (int i = 0; i < weathers.length(); i++) {
                object = (JSONObject) weathers.get(i);
                mWeatherData = new WeatherBean.WeatherData();
                mWeatherData.setDate(object.optString("date"));
                mWeatherData.setWeather(object.optString("weather"));
                mWeatherData.setTemperature(object.optString("temperature"));
                mWeatherData.setWind(object.optString("wind"));
                weatherDatas.add(mWeatherData);
            }
            weatherBean.setWeatherDatas(weatherDatas);
            bc.subscribe(AiosApi.Player.STATE);
            UiEventDispatcher.notifyUpdateUI(UIType.WeatherUI, weatherBean);
        } else {
            switch (errId) {
                case 1: //定位失败
                    bc.publish(AiosApi.Weather.QUERY_RESULT, "没有定位到当前城市，请稍后再试");
                    break;
                case 2: //查询日期已过期
                    bc.publish(AiosApi.Weather.QUERY_RESULT, "查询日期超出范围");
                    break;
                case 3: //查询时期超过7天
                    bc.publish(AiosApi.Weather.QUERY_RESULT, Configs.MarkedWords.DATE_TOO_FAR);
                    break;
                case 4: //网络请求失败
                    bc.publish(AiosApi.Weather.QUERY_RESULT, "网络不佳，请检查网络");
                    break;
                case 5: //没有查询到相应的天气结果
                    bc.publish(AiosApi.Weather.QUERY_RESULT, Configs.MarkedWords.NO_WEATHER_RESUALT);
                    break;
            }
            UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
        }
    }
}
