package com.aispeech.aios.adapter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.bean.WeatherBean;
import com.aispeech.aios.adapter.model.WeatherResource;
import com.aispeech.aios.adapter.util.DateUtils;

import java.util.List;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-02-17
 * @copyright aispeech.com
 */
public class WeatherForecastAdapter extends BaseAdapter {

    private int mDay;
    private String[] mWeeks;
    private Context mContext;
    private List<WeatherBean.WeatherData> mWeatherDatas;

    public WeatherForecastAdapter(Context mContext) {
        this.mContext = mContext;
        this.mWeeks = mContext.getResources().getStringArray(R.array.week);
    }

    public void setDay(int day) {
        this.mDay = day;
    }

    public void setWeatherDatas(List<WeatherBean.WeatherData> mWeatherDatas) {
        this.mWeatherDatas = mWeatherDatas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null != mWeatherDatas) {
            return mWeatherDatas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mWeatherDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.weather_forecast_item, null);
            viewHolder.dateText = (TextView) convertView.findViewById(R.id.weather_forecast_date);
            viewHolder.typeImage = (ImageView) convertView.findViewById(R.id.weather_forecast_image);
            viewHolder.tempText = (TextView) convertView.findViewById(R.id.weather_forecast_temp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String dateString = DateUtils.getWeekDate(mContext, mWeatherDatas.get(position).getDate(), position);
        viewHolder.dateText.setText(dateString);

        String weather = mWeatherDatas.get(position).getWeather();
        if (weather.contains("转")) {
            weather = weather.substring(weather.lastIndexOf("转") + 1, weather.length());
        }

        viewHolder.typeImage.setImageResource(WeatherResource.getImgSmallByWeather(weather));
        viewHolder.tempText.setText(mWeatherDatas.get(position).getTemperature().replace("~", "/"));

        if (position == mDay) {
            viewHolder.dateText.setTextColor(mContext.getResources().getColor(R.color.text_hightlight));
        } else {
            viewHolder.dateText.setTextColor(mContext.getResources().getColor(R.color.text_white));
        }

        return convertView;
    }

    class ViewHolder {
        TextView dateText;
        ImageView typeImage;
        TextView tempText;
    }
}
