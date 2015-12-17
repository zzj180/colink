 package com.zzj.coogo.screenoff;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LEDView extends LinearLayout {

	public TextView timeView;
	public TextView dateView;
	public static final String FONT_DIGITAL_7 = "fonts" + File.separator
			+ "digital-7.ttf";

	private static final String DATE_FORMAT = "%02d:%02d:%02d";
	
	private String mFormat = "yyyy.M.d E";
	private static final int REFRESH_DELAY = 500;

	private final Handler mHandler = new Handler();
	private final Runnable mTimeRefresher = new Runnable() {

		@Override
		public void run() {
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
			final Date d = new Date();
			calendar.setTime(d);

			int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
			timeView.setText(String.format(DATE_FORMAT,hourOfDay, calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND)));
			
			if(hourOfDay < 6 || hourOfDay >19 ){
				mBrightnessListening.dateListening(15);
			}else if(hourOfDay > 17){
				mBrightnessListening.dateListening(200);
			}else if(hourOfDay > 9){
				mBrightnessListening.dateListening(255);
			}else{
				mBrightnessListening.dateListening(200);
			}
			dateView.setText(DateFormat.format(mFormat, calendar));
			mHandler.postDelayed(this, REFRESH_DELAY);
		}
	};

	@SuppressLint("NewApi")
	public LEDView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public LEDView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LEDView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.ledview, this);
		if(view == null) return;
		timeView = (TextView) view.findViewById(R.id.ledview_clock_time);
		dateView = (TextView) view.findViewById(R.id.ledview_clock_date);
		AssetManager assets = context.getAssets();
		final Typeface font = Typeface.createFromAsset(assets, FONT_DIGITAL_7);
		timeView.setTypeface(font);// 设置字体
		dateView.setTypeface(font);// 设置字体
		if(!ScrrenoffActivity.CUSTIOM_002.equals(MainApplication.custom_code)){
			timeView.setTextSize(80f);
			dateView.setTextSize(40f);
			timeView.setTextColor(Color.WHITE);
			dateView.setTextColor(Color.WHITE);
			timeView.setShadowLayer(10, 0, 0, 0xff00ff00);
			dateView.setShadowLayer(10, 0, 0, 0xff00ff00);
		}
	}

	public void start() {
		mHandler.post(mTimeRefresher);
	}

	public void stop() {
		mHandler.removeCallbacks(mTimeRefresher);
	}
	
	BrightnessListening mBrightnessListening;
	
	public void setOnDateListening(BrightnessListening brightnessListening){
		mBrightnessListening=brightnessListening;
	};
}
