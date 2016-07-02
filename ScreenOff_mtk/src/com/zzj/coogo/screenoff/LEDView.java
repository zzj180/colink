 package com.zzj.coogo.screenoff;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LEDView extends LinearLayout {

	public TextView timeView;
	public TextView dateView;

	public static final String DATE_FORMAT = "%02d:%02d";
	
	private String mFormat = "yyyy.M.d  EEEE";
	
	private static final int REFRESH_DELAY = 20000;

	private final Runnable mTimeRefresher = new Runnable() {

		@Override
		public void run() {
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
			final Date d = new Date();
			calendar.setTime(d);

			int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
			timeView.setText(String.format(DATE_FORMAT,hourOfDay, calendar.get(Calendar.MINUTE)));
			dateView.setText(DateFormat.format(mFormat, calendar));
			if(hourOfDay < 6 || hourOfDay >19 ){
				mBrightnessListening.dateListening(15);
			}else if(hourOfDay > 17){
				mBrightnessListening.dateListening(200);
			}else if(hourOfDay > 9){
				mBrightnessListening.dateListening(255);
			}else{
				mBrightnessListening.dateListening(200);
			}
			MainApplication.getHander().postDelayed(this, REFRESH_DELAY);
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
		
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		final Date d = new Date();
		calendar.setTime(d);
		int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		timeView.setText(String.format(DATE_FORMAT,hourOfDay, calendar.get(Calendar.MINUTE)));
		dateView.setText(DateFormat.format(mFormat, calendar));
		int date_show = Settings.System.getInt(context.getApplicationContext().getContentResolver(), "screen_off_date_switch", 0);
		if(date_show==0){
			dateView.setVisibility(View.VISIBLE);
		}else{
			dateView.setVisibility(View.GONE);
		}
	}

	public void start() {
		MainApplication.getHander().post(mTimeRefresher);
	}

	public void stop() {
		MainApplication.getHander().removeCallbacks(mTimeRefresher);
	}
	
	BrightnessListening mBrightnessListening;
	
	public void setOnDateListening(BrightnessListening brightnessListening){
		mBrightnessListening=brightnessListening;
	};
}
