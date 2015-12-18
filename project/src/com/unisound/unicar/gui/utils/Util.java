/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : CalendarUtil.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.util
 * @Author : Brant
 * @CreateDate : 2012-11-21
 */
package com.unisound.unicar.gui.utils;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.content.Context;

import com.coogo.inet.vui.assistant.car.R;

public class Util {
	public static final String TAG = "CalendarUtil";

	public static int daysOfTwo(Calendar before, Calendar after) {
		int beforeYear = before.get(Calendar.YEAR);
		int beforeMonth = before.get(Calendar.MONTH);
		int beforeDay = before.get(Calendar.DAY_OF_MONTH);

		int afterYear = after.get(Calendar.YEAR);
		int afterMonth = after.get(Calendar.MONTH);
		int afterDay = after.get(Calendar.DAY_OF_MONTH);

		return daysOfTwo(beforeYear, beforeMonth, beforeDay, afterYear, afterMonth, afterDay);
	}

	public static int daysOfTwo(int beforeYear, int beforeMonth, int beforeDay, int afterYear, int afterMonth, int afterDay) {
		Calendar before = Calendar.getInstance();
		before.set(beforeYear, beforeMonth, beforeDay, 0, 0, 0);
		before.clear(Calendar.MILLISECOND);
		Calendar after = Calendar.getInstance();
		after.set(afterYear, afterMonth, afterDay, 0, 0, 0);
		after.clear(Calendar.MILLISECOND);

		long m = after.getTimeInMillis() - before.getTimeInMillis();

		long n = m / 86400000;

		return (int) n;
	}

	public static String getReadableDateTime(Context context, Calendar calendar) {
		int days = daysOfTwo(Calendar.getInstance(), calendar);
		switch (days) {
		case 2:
			return context.getString(R.string.readable_time_day_after_tomorrow);
		case 1:
			return context.getString(R.string.readable_time_tomorrow);
		case 0:
			return context.getString(R.string.readable_time_today);
		case -1:
			return context.getString(R.string.readable_time_yesterday);
		case -2:
			return context.getString(R.string.readable_time_day_before_yesterday);
		default:
			return "";
		}
	}

	private static final double EARTH_RADIUS = 6378137.0;
	private static final double BASE_KB = 1024.00;
	private static final double BASE_MB = BASE_KB * 1024;
	private static final double BASE_GB = BASE_MB * 1024;
	private static final double BASE_KM = 1000.00;

	/**
	 * 求2个经纬度直接的距离
	 * @Description : gps2m
	 * @Author : Brant
	 * @CreateDate : 2013-3-15
	 * @param latA
	 * @param lngA
	 * @param latB
	 * @param lngB
	 * @return
	 */
	public static double gps2m(double latA, double lngA, double latB, double lngB) {
		double radLat1 = (latA * Math.PI / 180.0);
		double radLat2 = (latB * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lngA - lngB) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	public static String trans2Length(double lengthInMeter) {
		if (lengthInMeter < BASE_KM) {
			return lengthInMeter + "m";
		} else {
			return new DecimalFormat("#.00").format(lengthInMeter / BASE_KM) + "Km";
		}
	}

	/**
	 * 将字节数转换成合适的单位
	 * @Description : trans2Unit
	 * @Author : Brant
	 * @CreateDate : 2013-3-15
	 * @param size
	 * @return
	 */
	public static String trans2Unit(long size) {
		String result = "";
		if (size > 0 && size < 1024) {
			result = size + "B";
		} else if (size >= 1024 && size < 1024 * 1024) {
			result = new DecimalFormat("#.00").format(size / BASE_KB) + "KB";
		} else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
			result = new DecimalFormat("#.00").format(size / BASE_MB )+ "MB";
		} else if (size >= 1024 * 1024 * 1024) {
			result = new DecimalFormat("#.00").format(size / BASE_GB )+ "GB";
		}
		return result;
	}
}
