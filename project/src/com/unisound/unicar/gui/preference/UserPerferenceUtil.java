package com.unisound.unicar.gui.preference;

import com.unisound.unicar.gui.utils.Logger;

import android.content.Context;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * User Preference Util
 * @author xiaodong
 * @date 20150714
 */
public class UserPerferenceUtil {
	
	private static final String TAG = UserPerferenceUtil.class.getSimpleName();
	
	private static final String SP_NAME = "unicar_user_settings";
	
	public static final String KEY_ENABLE_WAKEUP = "KEY_ENABLE_WAKEUP";
	public static final boolean VALUE_DEFAULT_WAKEUP = true;
	
	public static final String KEY_MAP = "KEY_MAP";
	/**0 baidu  1 gaode */
	public static  int VALUE_MAP_DEFAULT = 1;   // 0 baidu  1 gaode
	public static final int VALUE_MAP_AMAP = 1;
	public static final int VALUE_MAP_BAIDU = 2;
	public static final int VALUE_MAP_KLD = 3;
	public static final int VALUE_MAP_TUBA = 4;
	public static final int VALUE_MAP_DAODAOTONG = 5;
	
	public static final String KEY_TTS_SPEED = "KEY_TTS_SPEED";
	public static final int VALUE_TTS_SPEED_SLOWLY = -1;
	public static final int VALUE_TTS_SPEED_STANDARD = 0; //DEFAULT
	public static final int VALUE_TTS_SPEED_FAST = 1;
	
	//float mic setting
	public static final String KEY_ENABLE_FLOAT_MIC = "KEY_ENABLE_FLOAT_MIC";
	public static final boolean VALUE_ENABLE_FLOAT_MIC_DEFAULT = false;
	
	private static final String KEY_INPUT_VIEW_X = "KEY_INPUT_VIEW_X";
	private static final String KEY_INPUT_VIEW_Y = "KEY_INPUT_VIEW_Y";
	
	/**
	 * 
	 * @param context
	 * @param listener
	 */
	public static void registerOnSharedPreferenceChangeListener(Context context, OnSharedPreferenceChangeListener listener) {
		SharedPreferencesHelper.getInstance(context, SP_NAME).registerOnSharedPreferenceChangeListener(listener);
	}

	/**
	 * 
	 * @param context
	 * @param listener
	 */
	public static void unregisterOnSharedPreferenceChangeListener(Context context, OnSharedPreferenceChangeListener listener) {
		SharedPreferencesHelper.getInstance(context, SP_NAME).unregisterOnSharedPreferenceChangeListener(listener);
	}
	
	public static void setWakeupEnable(Context context, boolean isEnable){
		SharedPreferencesHelper.getInstance(context, SP_NAME).saveBooleanValue(KEY_ENABLE_WAKEUP, isEnable);
	}
	
	public static boolean isWakeupEnable(Context context){
		SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
		return sph.getBooleanValue(KEY_ENABLE_WAKEUP, VALUE_DEFAULT_WAKEUP);
	}
	
	public static void setMapChoose(Context context, int mapType){
		boolean status = SharedPreferencesHelper.getInstance(context, SP_NAME).saveIntValue(KEY_MAP, mapType);
		Logger.d(TAG, "!--->setMapChoose--save status = "+status);
	}
	
	public static int getMapChoose(Context context){
		SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
		return sph.getIntValue(KEY_MAP, VALUE_MAP_AMAP);
	}
	
	public static void setFloatMicEnable(Context context, boolean isEnable){
		SharedPreferencesHelper.getInstance(context, SP_NAME).saveBooleanValue(KEY_ENABLE_FLOAT_MIC, isEnable);
	}
	public static boolean isFloatMicEnable(Context context){
		SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
		return sph.getBooleanValue(KEY_ENABLE_FLOAT_MIC, VALUE_ENABLE_FLOAT_MIC_DEFAULT);
	}
	
	public static void setTTSSpeed(Context context, int speed){
		SharedPreferencesHelper.getInstance(context, SP_NAME).saveIntValue(KEY_TTS_SPEED, speed);
	}
	
	public static int getTTSSpeed(Context context){
		SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
		return sph.getIntValue(KEY_TTS_SPEED, VALUE_TTS_SPEED_STANDARD);
	}
	
	public static void setInputViewX(Context context, int x){
		SharedPreferencesHelper.getInstance(context, SP_NAME).saveIntValue(KEY_INPUT_VIEW_X, x);
	}
	public static int getInputViewX(Context context, int defaultX){
		SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
		return sph.getIntValue(KEY_INPUT_VIEW_X, defaultX);
	}

	public static void setInputViewY(Context context, int y){
		SharedPreferencesHelper.getInstance(context, SP_NAME).saveIntValue(KEY_INPUT_VIEW_Y, y);
	}
	public static int getInputViewY(Context context, int defaultY){
		SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
		return sph.getIntValue(KEY_INPUT_VIEW_Y, defaultY);
	}
}
