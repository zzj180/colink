package com.aispeech.aios.adapter.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.adapter.WeatherForecastAdapter;
import com.aispeech.aios.adapter.bean.WeatherBean;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.model.WeatherResource;
import com.aispeech.aios.adapter.node.WeatherNode;
import com.aispeech.aios.adapter.util.DateUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @desc 天气页面Layout
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class WeatherLayout extends LinearLayout {

    private static final String TAG = "WeatherLayout";

    private Context mContext;

    private TextView mAreaAndDate;
    private TextView mWeatherType;
    private TextView mTempRange;
    private TextView mWind;
    private ImageView mWeatherImg;
    private ImageView mWeatherImgAfter;
    private GridView mWeatherForecastGrid;
    private WeatherForecastAdapter mWeatherForecastAdapter;

    private String mCityFormat;
    private String mTypeFormat;

    private AlphaAnimation mWeatherImgFromAnim;
    private AlphaAnimation mWeatherImgToAnim;

    public WeatherLayout(Context context) {
        this(context, null);
    }

    public WeatherLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
        initFormat();
        initAnim();
    }

    private void initAnim() {
        mWeatherImgFromAnim = new AlphaAnimation(1, 0);
        mWeatherImgFromAnim.setDuration(1000);

        mWeatherImgToAnim = new AlphaAnimation(0, 1);
        mWeatherImgToAnim.setDuration(1000);
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_weather_subspend, this);
        mAreaAndDate = (TextView) findViewById(R.id.txt_weather_area_date);
        mWeatherType = (TextView) findViewById(R.id.txt_weather_type);
        mTempRange = (TextView) findViewById(R.id.txt_weather_temperature);
        mWind = (TextView) findViewById(R.id.txt_weather_wind);
        mWeatherImg = (ImageView) findViewById(R.id.weather_image);
        mWeatherImgAfter = (ImageView) findViewById(R.id.weather_image_after);
        mWeatherImgAfter = (ImageView) findViewById(R.id.weather_image_after);

        mWeatherForecastGrid = (GridView) findViewById(R.id.grid_weather_forecast);
        mWeatherForecastAdapter = new WeatherForecastAdapter(mContext);
        mWeatherForecastGrid.setAdapter(mWeatherForecastAdapter);
    }

    private void initFormat() {
        Resources res = mContext.getResources();
        mCityFormat = res.getString(R.string.weather_city);
        mTypeFormat = res.getString(R.string.weather_type);
    }

    /**
     * 显示天气界面UI
     *
     * @param weather 查询到的天气结果实体bean
     */
    public void showUI(WeatherBean weather) {
        int searchDay = weather.getSearchDay();

        WeatherBean.WeatherData searchDayWeatherData = weather.getWeatherDatas().get(searchDay);

        mAreaAndDate.setText(String.format(mCityFormat, weather.getTitle(),
                DateUtils.getWeekDate(mContext, searchDayWeatherData.getDate(), searchDay)));

        mWeatherType.setText(String.format(mTypeFormat, searchDayWeatherData.getWeather()));
        mTempRange.setText(searchDayWeatherData.getTemperature());
        String[] wind = searchDayWeatherData.getWind().split(",");
        mWind.setText(getResources().getString(R.string.weather_wind, wind[0], wind[1]));

        mWeatherForecastAdapter.setDay(searchDay);
        mWeatherForecastAdapter.setWeatherDatas(weather.getWeatherDatas());

        String speakWords = weather.getTitle() + searchDayWeatherData.getDate() + "天气,"
                + searchDayWeatherData.getWeather() + "," + searchDayWeatherData.getTemperature() + "," + searchDayWeatherData.getWind();
        WeatherNode.getInstance().getBusClient().publish(AiosApi.Weather.QUERY_RESULT, speakWords);

        setWeatherImage(searchDayWeatherData.getWeather());
    }

    private void setWeatherImage(String type) {
        String[] types = getTypes(type);
        AILog.d("天气---" + type);
        if (types.length == 1) {
            AILog.i(TAG, "type1--" + type);
            mWeatherImg.setVisibility(View.VISIBLE);
            mWeatherImgAfter.setVisibility(View.GONE);
            mWeatherImg.setImageResource(WeatherResource.getImgByWeather(type));
        } else if (types.length == 2) {
            mWeatherImg.setImageResource(WeatherResource.getImgByWeather(types[0]));

            mWeatherImg.setVisibility(View.VISIBLE);
            mWeatherImgAfter.setVisibility(GONE);
            mWeatherImgAfter.setImageResource(WeatherResource.getImgByWeather(types[1]));

            mWeatherImgFromAnim.setAnimationListener(new FromAnimListener());

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mWeatherImg.post(new Runnable() {
                        @Override
                        public void run() {
                            mWeatherImg.startAnimation(mWeatherImgFromAnim);
                        }
                    });
                }
            }, 2000);
        }
    }

    /**
     * 天气A状态到B状态变化过程中，A的动画监听
     */
    private class FromAnimListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mWeatherImg.setVisibility(GONE);
            mWeatherImgAfter.setVisibility(VISIBLE);
            mWeatherImgAfter.startAnimation(mWeatherImgToAnim);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    /**
     * eg："小到中雨","小雨到中雨","小转中雨","小雨转中雨",小雨-中雨"
     * 都能正确的分离出“小雨”-->“中雨”
     *
     * @param srcType 天气数据库返回的天气类型
     * @return 天气类型String数组
     */
    private String[] getTypes(String srcType) {

        String option = null;
        if (srcType.contains("转")) {
            option = "转";
        }
        AILog.i(TAG, "option--" + option);
        AILog.i(TAG, "srcType--" + srcType);

        if (option == null) {
            return new String[]{srcType};
        }

        String tail = srcType.substring(srcType.lastIndexOf(option) + 1, srcType.length());
        String head = srcType.substring(0, srcType.indexOf(option));
        AILog.i(TAG, "处理前:" + head + "-->" + tail);

        if (head.equals("雨") || head.equals("雪")) {
            head = "小" + head;
        }

        if (tail.equals("雨") || tail.equals("雪")) {
            tail = "小" + tail;
        }

        AILog.i(TAG, "处理后:" + head + "-->" + tail + "\n");
        return new String[]{head, tail};
    }
}
