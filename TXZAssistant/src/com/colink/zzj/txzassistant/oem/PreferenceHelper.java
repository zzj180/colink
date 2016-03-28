package com.colink.zzj.txzassistant.oem;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.colink.zzj.txzassistant.AdapterApplication;

/**
 * @desc 请在此添加类描述
 * @auth zzj
 * @date 2016-03-16
 */
public class PreferenceHelper {
    private static final String CONFIG_FILE_NAME = "AIOSAdapterConfigs";

    public static final String PREF_KEY_LAST_HOME = "pref_key_last_home";
    public static final String PREF_KEY_LAST_COMPANY = "pref_key_last_company";
    public static final String IS_AIOS_CONTACTSNODE_EXIST = "is_aios_contactsnode_exist";
    public static final String PREF_KEY_CURRENT_MAP_TYPE = "pref_current_map_type";//设置的地图类型
    public static final String PREF_KEY_CURRENT_POI_KEY = "pref_current_poi_key";//设置poi检索key
    public static final String PREF_KEY_DEFAULT_MUSIC_TYPE = "pref_key_default_music_type";//默认音乐类型
    public static final String PREF_KEY_WIFI_STATE = "pref_key_wifi_state";//wifi状态
    public static final String PREF_KEY_MEDIA_VOLUME = "pref_key_media_volume";//音量大小
    public static final String PREF_KEY_BRIGHTNESS = "pref_key_brightness";//亮度大小

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

    /**
     * 设置默认poi检索key
     *
     * @return poi检索key
     */
    public void setCurrentPoiKey(String key) {
        mSp.edit().putString(PREF_KEY_CURRENT_POI_KEY, key).apply();
    }


    public void setTime(int year) {
        mSp.edit().putInt("year", year).apply();
    }

    public int getTime() {
        return mSp.getInt("year", 0);
    }

    /**
     * 存储默认地图类型
     *
     * @param type    地图类型
     */
    public void setCurrentMapType(int type) {
        mSp.edit().putInt(PREF_KEY_CURRENT_MAP_TYPE, type).apply();
    }



    /**
     * 存储导航到家
     *
     * @param poi 家的POI地址
     */
    public void setHomePoi(String poi) {
        mSp.edit().putString(PREF_KEY_LAST_HOME, poi).apply();
    }

    /**
     * 获取导航到家
     *
     * @return 家的POI地址
     */
    public String getHomePoi() {
        return mSp.getString(PREF_KEY_LAST_HOME, "");
    }

    /**
     * 存储导航到公司
     *
     * @param poi 公司POI地址
     */
    public void setCompanyPoi(String poi) {
        mSp.edit().putString(PREF_KEY_LAST_COMPANY, poi).apply();
    }

    /**
     * 获取导航到公司
     *
     * @return 公司POI地址
     */
    public String getCompanyPoi() {
        return mSp.getString(PREF_KEY_LAST_COMPANY, "");
    }

    /**
     * 获取系统媒体类型音量
     *
     * @return 音量值，默认为最大15
     */
    public int getVolume() {
        return mSp.getInt(PREF_KEY_MEDIA_VOLUME, 15);
    }

    public void setVolume(int volume) {
        mSp.edit().putInt(PREF_KEY_MEDIA_VOLUME, volume).apply();
    }

    /**
     * 获取系统媒体类型音量
     * @return 音量值，默认为最大255
     */
    public int getBrightness() {
        return mSp.getInt(PREF_KEY_BRIGHTNESS, 255);
    }

    public void setBrightness(int brightness) {
        mSp.edit().putInt(PREF_KEY_BRIGHTNESS, brightness).apply();
    }

    /**
     * 获取热点打开前，wifi的状态
     * @return true:开  false:关
     */
    public boolean getWifiState() {
        return mSp.getBoolean(PREF_KEY_WIFI_STATE, false);
    }

    public void setWifiState(boolean state) {
        mSp.edit().putBoolean(PREF_KEY_WIFI_STATE, state).apply();
    }

    public boolean isAIOSContactsNodeExist() {
        return mSp.getBoolean(IS_AIOS_CONTACTSNODE_EXIST, false);
    }

    public void setAIOSContactsNodeExist(boolean isAIOSContactsNodeExist) {
        mSp.edit().putBoolean(IS_AIOS_CONTACTSNODE_EXIST, isAIOSContactsNodeExist).apply();
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
}
