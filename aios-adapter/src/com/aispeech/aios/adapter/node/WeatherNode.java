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
    }

    @Override
    public void onMessage(String topic, byte[]... parts) throws Exception {
        super.onMessage(topic, parts);
        AILog.i(TAG, topic, parts);
        if (AiosApi.Player.STATE.equals(topic) && "wait".equals(StringUtil.getEncodedString(parts[0]))) {
            bc.unsubscribe(AiosApi.Player.STATE);
        }

        if (topic.equals("data.weather.query.result")) {
            UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI);
            queryWeatherResult(parts);
        }
    }

    @Override
    public BusClient.RPCResult onCall(final String url, final byte[]... args) throws Exception {
        AILog.i(TAG, url, args);
        bc.call("/data" + url, args);
        UiEventDispatcher.notifyUpdateUI(UIType.LoadingUI);
        return null;
    }

    private void queryWeatherResult(byte[]... parts) throws Exception{
        int errId = Integer.valueOf(new String(parts[1]));
        if (errId == 0) {
            JSONObject jsonObject = new JSONObject(new String(parts[0]));
            JSONObject resultJson = jsonObject.optJSONObject("result");
            WeatherBean weatherBean = new WeatherBean();
            weatherBean.setSearchDay(resultJson.optInt("day"));

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

            UiEventDispatcher.notifyUpdateUI(UIType.WeatherUI, weatherBean);
        } else {
            switch (errId) {
                case 1: //定位失败
                    bc.publish(AiosApi.Weather.QUERY_RESULT, "没有定位到当前城市，请稍后再试");
                    break;
                case 2: //查询日期已过期
                    bc.publish(AiosApi.Weather.QUERY_RESULT, Configs.MarkedWords.DATE_PASSED);
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
