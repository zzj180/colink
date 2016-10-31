package com.aispeech.aios.adapter.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.config.Configs;

import java.io.IOException;

/**
 * @desc AIOS设置帮助类
 * @auth AISPEECH
 * @date 2016-02-19
 * @copyright aispeech.com
 */
public class PreferenceHelper {
    private static final String TAG = PreferenceHelper.class.getSimpleName();
    private static final String CONFIG_FILE_NAME = "AIOSAdapterConfigs";

    public static final String PREF_KEY_LAST_HOME = "pref_key_last_home";
    public static final String PREF_KEY_LAST_COMPANY = "pref_key_last_company";
    public static final String PREF_KEY_CURRENT_MAP_TYPE = "pref_current_map_type";//设置的地图类型
    public static final String PREF_KEY_DEFAULT_MUSIC_TYPE = "pref_key_default_music_type";//默认音乐类型
    public static final String PREF_KEY_WIFI_STATE = "pref_key_wifi_state";//wifi状态
    public static final String PREF_KEY_MEDIA_VOLUME = "pref_key_media_volume";//音量大小
    public static final String PREF_KEY_BRIGHTNESS = "pref_key_brightness";//亮度大小
    public static final String PREF_KEY_WAKEUP_SWITCH = "pref_key_wakeup_switch";//唤醒开关
    private static final String KEY_INPUT_VIEW_X = "KEY_INPUT_VIEW_X";
    private static final String KEY_INPUT_VIEW_Y = "KEY_INPUT_VIEW_Y";
    /**
     * 是否使用远程BusServer
     */
    public static final String KEY_IS_USE_REMOTE_BUS_SERVER = "is_use_remote_bus_server";

    /**
     * 远程BusServer
     */
    public static final String KEY_REMOTE_BUS_SERVER = "remote_bus_server";
    public static final String REMOTE_PACKAGE_NAME = "com.aispeech.aios";
    public static final String REMOTE_SHARED_PREFERENCE_NAME = REMOTE_PACKAGE_NAME + "_preferences";

    private static PreferenceHelper mInstance;

    private static SharedPreferences mSp = AdapterApplication.getContext().
            getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);

    public static PreferenceHelper getInstance() {
        if (null == mInstance) {
            mInstance = new PreferenceHelper();
        }
        return mInstance;
    }

    public void setTime(int year) {
        AILog.i(TAG, "store current year : " + year);
        mSp.edit().putInt("year", year).apply();
    }

    public int getTime() {
        int year = mSp.getInt("year", 0);
        AILog.i(TAG, "last store year : " + year);
        return year;
    }

    /**
     * 存储默认地图类型
     *
     * @param type 地图类型
     */
/*    public void setCurrentMapType(int type) {
        AILog.i(TAG, "store current map type : " + type);
        mSp.edit().putInt(PREF_KEY_CURRENT_MAP_TYPE, type).apply();
    }

    *//**
     * 获取默认地图类型
     *
     * @return 地图类型
     *//*
    public int getCurrentMapType() {
        int defaultMapType = Configs.MapConfig.GDMAP;
        try {
            defaultMapType = PropertyUtil.getInstance().loadProperty().getDefaultMapType();
        } catch (IOException e) {
            e.printStackTrace();
        }
        defaultMapType = mSp.getInt(PREF_KEY_CURRENT_MAP_TYPE, defaultMapType);
        AILog.i(TAG, "current map type : " + defaultMapType);
        return defaultMapType;
    }*/

    /**
     * 设置默认音乐应用
     *
     * @param type 音乐应用：1，AIOS音乐；2，酷我音乐
     */
    public void setDefaultMusicType(int type) {
        AILog.i(TAG, "store current music type : " + type);
        mSp.edit().putInt(PREF_KEY_DEFAULT_MUSIC_TYPE, type).apply();
    }

    /**
     * 获取默认音乐应用
     *
     * @return 音乐应用：1，AIOS音乐；2，酷我音乐  默认为 2
     */
    public int getDefaultMusicType() {
        int defaultMusicType = 2;
        try {
            defaultMusicType = PropertyUtil.getInstance().loadProperty().getDefaultMusicType();
        } catch (IOException e) {
            e.printStackTrace();
        }
        defaultMusicType = mSp.getInt(PREF_KEY_DEFAULT_MUSIC_TYPE, defaultMusicType);
        AILog.i(TAG, "current music type : " + defaultMusicType);
        return defaultMusicType;
    }

    /**
     * 存储导航到家
     *
     * @param poi 家的POI地址
     */
    public void setHomePoi(String poi) {
        AILog.i(TAG, "store home poi : " + poi);
        mSp.edit().putString(PREF_KEY_LAST_HOME, poi).apply();
    }

    /**
     * 获取导航到家
     *
     * @return 家的POI地址
     */
    public String getHomePoi() {
        String homePoi = mSp.getString(PREF_KEY_LAST_HOME, "");
        AILog.i(TAG, "stored home poi : " + homePoi);
        return homePoi;
    }

    /**
     * 存储导航到公司
     *
     * @param poi 公司POI地址
     */
    public void setCompanyPoi(String poi) {
        AILog.i(TAG, "store company poi : " + poi);
        mSp.edit().putString(PREF_KEY_LAST_COMPANY, poi).apply();
    }

    /**
     * 获取导航到公司
     *
     * @return 公司POI地址
     */
    public String getCompanyPoi() {
        String companyPoi = mSp.getString(PREF_KEY_LAST_COMPANY, "");
        AILog.i(TAG, "stored company poi : " + companyPoi);
        return companyPoi;
    }

    /**
     * 获取系统媒体类型音量
     *
     * @return 音量值，默认为最大7
     */
   public int getVolume() {
        int volume = mSp.getInt(PREF_KEY_MEDIA_VOLUME, 7);
        AILog.i(TAG, "current volume : " + volume);
        return volume;
    }

    public void setVolume(int volume) {
        AILog.i(TAG, "store current volume : " + volume);
        mSp.edit().putInt(PREF_KEY_MEDIA_VOLUME, volume).apply();
    }

    /**
     * 获取系统媒体类型音量
     *
     * @return 音量值，默认为最大255
     */
    public int getBrightness() {
        int curBrightness = mSp.getInt(PREF_KEY_BRIGHTNESS, 255);
        try {
            int systemBrightness = Settings.System.getInt(AdapterApplication.getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);//读取系统当前屏幕亮度值
            if(systemBrightness == 0 && curBrightness != 0){//如果系统当前屏幕亮度是 0 && 缓存屏幕亮度不是 0 使用缓存亮度值
                return curBrightness;
            }else if (curBrightness != systemBrightness) {
                curBrightness = systemBrightness;//如果当前缓存本地亮度值不等于系统亮度值，设置亮度值为系统亮度值
                setBrightness(curBrightness);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        AILog.d(TAG, "return  brightness : " + curBrightness);
        return curBrightness;
    }

    public void setBrightness(int brightness) {
        AILog.i(TAG, "store current brightness : " + brightness);
        mSp.edit().putInt(PREF_KEY_BRIGHTNESS, brightness).apply();
    }

    /**
     * 获取热点打开前，wifi的状态
     * @return true:开  false:关
     */
    public boolean getWifiState() {
        boolean wifiState = mSp.getBoolean(PREF_KEY_WIFI_STATE, false);
        AILog.i(TAG, "current wifi : " + wifiState);
        return wifiState;
    }

    public void setWifiState(boolean state) {
        AILog.i(TAG, "store wifi : " + state);
        mSp.edit().putBoolean(PREF_KEY_WIFI_STATE, state).apply();
    }

    public boolean isUseRemoteBusServer(boolean def) {
        Context otherAppsContext = null;
        try {
            otherAppsContext = AdapterApplication.getContext().createPackageContext(REMOTE_PACKAGE_NAME,
                    Context.MODE_PRIVATE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (otherAppsContext == null) {
            return def;
        }

        SharedPreferences sharedPreferences = otherAppsContext
                .getSharedPreferences(REMOTE_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_USE_REMOTE_BUS_SERVER, def);
    }

    public String getRemoteBusServer(String def) {
        String defaultString = def;
        try {
            Context otherAppsContext = AdapterApplication.getContext().createPackageContext(REMOTE_PACKAGE_NAME,
                    Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences = otherAppsContext
                    .getSharedPreferences(REMOTE_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

            if (null != sharedPreferences) {
                defaultString = sharedPreferences.getString(KEY_REMOTE_BUS_SERVER, def);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return defaultString;
    }
    
    public void setWakeUpEnable(boolean enable){
    	 mSp.edit().putBoolean(PREF_KEY_WAKEUP_SWITCH, enable).apply();
    }
    
    public boolean getWakeUpEnable(){
    	return mSp.getBoolean(PREF_KEY_WAKEUP_SWITCH, true);
    }
    
    public static void setInputViewX(Context context, int x) {
    	mSp.edit().putInt(KEY_INPUT_VIEW_X, x).apply();
    }

    public static int getInputViewX(Context context, int defaultX) {
        return mSp.getInt(KEY_INPUT_VIEW_X, defaultX);
    }

    public static void setInputViewY(Context context, int y) {
    	mSp.edit().putInt(KEY_INPUT_VIEW_Y, y).apply();
    }

    public static int getInputViewY(Context context, int defaultY) {
    	 return mSp.getInt(KEY_INPUT_VIEW_Y, defaultY);
    }
}
