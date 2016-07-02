package com.colink.zzj.txzassistant.util;

import com.colink.zzj.txzassistant.R;

import android.content.Context;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.text.TextUtils;


/**
 * User Preference Util
 * 
 * @author xiaodong
 * @date 20150714
 */
public class UserPerferenceUtil {

	private static final String TAG = UserPerferenceUtil.class.getSimpleName();

    private static final String SP_NAME = "zzj_settings";

    public static final String KEY_ENABLE_WAKEUP = "KEY_ENABLE_WAKEUP";
    public static final boolean VALUE_DEFAULT_WAKEUP = true;

    public static final String KEY_MAP = "KEY_MAP";
   	/**0 baidu  1 gaode */
	public static  int VALUE_MAP_DEFAULT = 1;   // 0 baidu  1 gaode
	public static final int VALUE_MAP_AMAP = 1;
	public static final int VALUE_MAP_BAIDU = 2;
	public static final int VALUE_MAP_KLD = 3;
	public static final int VALUE_MAP_TUBA = 4;
	public static final int VALUE_MAP_MEIX = 5;

    public static final String KEY_TTS_SPEED = "KEY_TTS_SPEED";
    public static final int VALUE_TTS_SPEED_SLOWLY = -1;
    public static final int VALUE_TTS_SPEED_STANDARD = 70; // DEFAULT
    public static final int VALUE_TTS_SPEED_FAST = 1;
    
    
    public static int DEFAULT_ONE_NAVI = 0;
    
    public static final String KEY_WAKEUP_THRESHOLD = "KEY_WAKEUP_THRESHOLD";
    // float mic setting
    public static final String KEY_ENABLE_FLOAT_MIC = "KEY_ENABLE_FLOAT_MIC";
    public static final boolean VALUE_ENABLE_FLOAT_MIC_DEFAULT = false;

    // oneShot setting
    public static final String KEY_ENABLE_ONESHOT = "KEY_ENABLE_ONESHOT";
    public static final boolean VALUE_ENABLE_ONESHOT_DEFAULT = false;

    public static final String KEY_VERSION_MODE = "KEY_VERSION_LEVEL";
    /** experience version: series asr & oneshot close */
    public static final int VALUE_VERSION_MODE_EXP = -1;
    /** standard version: oneshot close */
    public static final int VALUE_VERSION_MODE_STANDARD = 0; // DEFAULT
    /** high version: oneshot open */
    public static final int VALUE_VERSION_MODE_HIGH = 1;
    public static int VALUE_VERSION_MODE_DEFAULT;

    // AEC setting
    public static final String KEY_ENABLE_AEC = "KEY_ENABLE_AEC";
    public static final boolean VALUE_ENABLE_AEC_DEFAULT = true;

    private static final String KEY_INPUT_VIEW_X = "KEY_INPUT_VIEW_X";
    private static final String KEY_INPUT_VIEW_Y = "KEY_INPUT_VIEW_Y";

    // WakeUp word
    public static final String KEY_WAKEUP_WORDS = "KEY_WAKEUP_WORDS";

    /**
     * 
     * @param context
     * @param listener
     */
    public static void registerOnSharedPreferenceChangeListener(Context context,
            OnSharedPreferenceChangeListener listener) {
        SharedPreferencesHelper.getInstance(context, SP_NAME).registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * 
     * @param context
     * @param listener
     */
    public static void unregisterOnSharedPreferenceChangeListener(Context context,
            OnSharedPreferenceChangeListener listener) {
        SharedPreferencesHelper.getInstance(context, SP_NAME)
                .unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static void setWakeupEnable(Context context, boolean isEnable) {
        SharedPreferencesHelper.getInstance(context, SP_NAME).saveBooleanValue(KEY_ENABLE_WAKEUP,
                isEnable);
    }

    public static boolean isWakeupEnable(Context context) {
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
        return sph.getBooleanValue(KEY_ENABLE_WAKEUP, VALUE_DEFAULT_WAKEUP);
    }


    public static int getMapChoose(Context context) {
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
        return sph.getIntValue(KEY_MAP, VALUE_MAP_AMAP);
    }

    public static void setFloatMicEnable(Context context, boolean isEnable) {
        SharedPreferencesHelper.getInstance(context, SP_NAME).saveBooleanValue(
                KEY_ENABLE_FLOAT_MIC, isEnable);
    }

    public static boolean getFloatMicEnable(Context context) {
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
        return sph.getBooleanValue(KEY_ENABLE_FLOAT_MIC, VALUE_ENABLE_FLOAT_MIC_DEFAULT);
    }

    public static void setOneShotEnable(Context context, boolean isEnable) {
        SharedPreferencesHelper.getInstance(context, SP_NAME).saveBooleanValue(KEY_ENABLE_ONESHOT,
                isEnable);
    }

    public static boolean getOneShotEnable(Context context) {
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
        return sph.getBooleanValue(KEY_ENABLE_ONESHOT, VALUE_ENABLE_ONESHOT_DEFAULT);
    }

    /**
     * 
     * @param context
     * @param level: {@link UserPerferenceUtil#VALUE_VERSION_MODE_EXP} or
     *        {@link UserPerferenceUtil#VALUE_VERSION_MODE_STANDARD} or
     *        {@link UserPerferenceUtil#VALUE_VERSION_MODE_HIGH}
     */
    public static void setVersionMode(Context context, int level) {
        SharedPreferencesHelper.getInstance(context, SP_NAME).saveIntValue(KEY_VERSION_MODE, level);
    }

    /**
     * 
     * @param context
     * @return {@link UserPerferenceUtil#VALUE_VERSION_MODE_EXP} or
     *         {@link UserPerferenceUtil#VALUE_VERSION_MODE_STANDARD} or
     *         {@link UserPerferenceUtil#VALUE_VERSION_MODE_HIGH}
     */
    public static int getVersionMode(Context context) {
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
        
        return sph.getIntValue(KEY_VERSION_MODE, VALUE_VERSION_MODE_DEFAULT);
    }

    public static void setAECEnable(Context context, boolean isEnable) {
        SharedPreferencesHelper.getInstance(context, SP_NAME).saveBooleanValue(KEY_ENABLE_AEC,
                isEnable);
    }

    public static boolean getAECEnable(Context context) {
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
        return sph.getBooleanValue(KEY_ENABLE_AEC, VALUE_ENABLE_AEC_DEFAULT);
    }

    public static void setTTSSpeed(Context context, int speed) {
        SharedPreferencesHelper.getInstance(context, SP_NAME).saveIntValue(KEY_TTS_SPEED, speed);
    }

    public static int getTTSSpeed(Context context) {
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
        return sph.getIntValue(KEY_TTS_SPEED, VALUE_TTS_SPEED_STANDARD);
    }
    
    public static void setWakeupThreshold(Context context, float threshold) {
        SharedPreferencesHelper.getInstance(context, SP_NAME).saveFloatValue(KEY_WAKEUP_THRESHOLD, threshold);
    }

    public static float getWakeupThreshold(Context context) {
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
        return sph.getFloatValue(KEY_WAKEUP_THRESHOLD);
    }

    public static void setInputViewX(Context context, int x) {
        SharedPreferencesHelper.getInstance(context, SP_NAME).saveIntValue(KEY_INPUT_VIEW_X, x);
    }

    public static int getInputViewX(Context context, int defaultX) {
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
        return sph.getIntValue(KEY_INPUT_VIEW_X, defaultX);
    }

    public static void setInputViewY(Context context, int y) {
        SharedPreferencesHelper.getInstance(context, SP_NAME).saveIntValue(KEY_INPUT_VIEW_Y, y);
    }

    public static int getInputViewY(Context context, int defaultY) {
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
        return sph.getIntValue(KEY_INPUT_VIEW_Y, defaultY);
    }

    public static void setWakeupWords(Context context, String wakeupWords) {
        Logger.d(TAG, "setWakeupWords---wakeupWords:" + wakeupWords);
        SharedPreferencesHelper.getInstance(context, SP_NAME).saveStringValue(KEY_WAKEUP_WORDS,
                wakeupWords);
    }

    /**
     * get WakeUp Words
     * 
     * @param context
     * @return
     */
    public static String getWakeupWords(Context context) {
        SharedPreferencesHelper sph = SharedPreferencesHelper.getInstance(context, SP_NAME);
        String wakeupWords = sph.getStringValue(KEY_WAKEUP_WORDS,"");
        return wakeupWords;
    }

    /**
     * get first WakeUp Word
     * 
     * @param context
     * @return
     */
    public static String getWakeupWord(Context context) {
        String wakeupWord = context.getResources().getStringArray(
				R.array.txz_sdk_init_wakeup_keywords)[0];
        String wakeupWords = getWakeupWords(context);
        if (!TextUtils.isEmpty(wakeupWords)) {
            String[] wakeupWordArray = wakeupWords.split("#");
            if (wakeupWordArray.length > 0) {
                wakeupWord = wakeupWordArray[0].trim();
            }
        }
        return wakeupWord;
    }

}
