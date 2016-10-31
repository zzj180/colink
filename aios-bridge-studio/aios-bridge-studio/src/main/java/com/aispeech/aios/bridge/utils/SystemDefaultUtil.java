package com.aispeech.aios.bridge.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.sdk.AIOSForCarSDK;

import java.lang.reflect.Method;

/**
 * AIOS默认系统操作，用于实现 {@link com.aispeech.aios.sdk.listener.AIOSSystemListener AIOSSystemListener} 时使用。
 */
public class SystemDefaultUtil {
    private static final String TIPS_MUSIC_MAX = "音量已经调到最大了";
    private static final String TIPS_MUSIC_MIN = "音量已经调到最小了";
    private static final String TIPS_MUSIC_VOLUME = "音量已经调到";
    private static final String TIPS_BRIGHTNESS_MAX = "屏幕亮度已经是最亮了";
    private static final String TIPS_BRIGHTNESS_MIN = "屏幕亮度已经是最暗了";
    private static final String TIPS_BRIGHTNESS_VOLUME = "屏幕亮度已经调到";
    private static final int TIPS_BRIGHTNESS_MAX_VOLUME = 5;
    private static final int TIPS_BRIGHTNESS_MIN_VOLUME = 1;
    private static final int TIPS_MUSIC_LOWER = 1;//音量降低
    private static final int TIPS_MUSIC_RAISE = 2;//音量增大
    private static final int TIPS_MUSIC_SETMAX = 3;//音量最大
    private static final int TIPS_MUSIC_SETMIN = 4;//音量最小
    private static final int TIPS_BRIGHTNESS_RAISE = 5;
    private static final int TIPS_BRIGHTNESS_LOWER = 6;
    public static final String KEY_PLATFORM = "ro.os.version";
    private static SystemDefaultUtil myUtil;
    private String TAG = "AIOS-SDK-SystemOperateUtil";
    private boolean isMute = false;
    private Context con;
    private WifiManager mWifiManager;
    private AudioManager mAudioManager;
    private PreferenceHelper preferenceHelper;
    private int cur_volume;;

    public SystemDefaultUtil() {
        this.con = AIOSForCarSDK.getContext();
        this.mWifiManager = (WifiManager) con.getSystemService(Context.WIFI_SERVICE);
        this.mAudioManager = (AudioManager) con.getSystemService(Context.AUDIO_SERVICE);
        this.preferenceHelper = new PreferenceHelper();
    }

    /**
     * @return SystemOperateUtil实例
     */
    public static synchronized SystemDefaultUtil getInstance() {

        if (myUtil == null) {
            myUtil = new SystemDefaultUtil();
        }
        return myUtil;
    }

    /**
     * 设置WIFI开启关闭
     *
     * @param state true开启  false关闭
     */
    public String setWIFIState(boolean state) {
        if (state) {
            String spot = "";
            if (!mWifiManager.isWifiEnabled()) {
                if (getHotPortState()) {
                    spot = setHotSpotState(false);
                }
                mWifiManager.setWifiEnabled(true);
            }
            if ("".equals(spot)) {
                return play("WIFI已打开");
            } else {
                return play(spot + ", WIFI已打开");
            }

        } else {

            if (mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(false);
            }
            return play("WIFI已关闭");
        }

    }

    private boolean getHotPortState() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");
            int i = (Integer) method.invoke(mWifiManager);
            AILog.i(TAG, "wifi AP state:  " + i);
            if (i == 13 || i == 14) { //AP state enable or enabling
                return true;
            }
        } catch (Exception e) {
            AILog.e(TAG, "Cannot get WiFi AP state" + e);
        }
        return false;
    }

    /**
     * 设置热点开启关闭
     *
     * @param state
     * @return 开启或者关闭   成功与否
     */
    public String setHotSpotState(boolean state) {
        if (state) {//打开
//            this.setWIFIState(false);//先关闭WIFI
            boolean wifiState = mWifiManager.isWifiEnabled();
            preferenceHelper.setWifiState(wifiState);
            if (wifiState) {
                mWifiManager.setWifiEnabled(false);
            }
        }

        try {
            // 热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            // 配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = "AndroidAP";
            // 配置热点的密码
            apConfig.preSharedKey = "12345678";
            // 安全：WPA2_PSK
            apConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            // 通过反射调用设置热点
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            // 返回热点打开状态
            boolean hotPortState = (Boolean) method.invoke(mWifiManager, apConfig, state);

            if (state) {
                if (hotPortState) {
                    return play("热点已打开");
                } else {
                    return play("热点打开失败，wifi已关闭");
                }
            } else {
                if (hotPortState) {
                    mWifiManager.setWifiEnabled(preferenceHelper.getWifiState());
                    return play("热点已关闭");
                } else {
                    return play("热点关闭失败");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 降低屏幕亮度 51
     */
    public String setScreenBrightnessDown() throws Settings.SettingNotFoundException {
        int currentBrightness = preferenceHelper.getBrightness() - 51;
        Settings.System.putInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);//设置屏幕亮度调节方式为手动模式

        if (currentBrightness < 51) {
            currentBrightness = 51;
        }
        return playBrightnessTips(TIPS_BRIGHTNESS_LOWER, currentBrightness);
    }

    /**
     * 提高屏幕亮度 51
     */
    public String setScreenBrightnessUp() throws Settings.SettingNotFoundException {
        int currentBrightness = preferenceHelper.getBrightness() + 51;
        Settings.System.putInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);//设置屏幕亮度调节方式为手动模式
        if (currentBrightness > 255) {
            currentBrightness = 255;
        }
        return playBrightnessTips(TIPS_BRIGHTNESS_RAISE, currentBrightness);
    }


    /**
     * 屏幕亮度调到最暗
     */
    public String setScreenBrightnessMin() throws Settings.SettingNotFoundException {
        return playBrightnessTips(TIPS_BRIGHTNESS_LOWER, 51);
    }

    /**
     * 屏幕亮度调到最亮
     */
    public String setScreenBrightnessMax() throws Settings.SettingNotFoundException {
        return playBrightnessTips(TIPS_BRIGHTNESS_RAISE, 255);
    }


    /**
     * 点亮屏幕c
     */
    public String closeScreen() {
        PowerManager pm=(PowerManager) con.getSystemService(Context.POWER_SERVICE);

            if (pm.isScreenOn()) pm.goToSleep(SystemClock.uptimeMillis());

    //    Settings.System.putInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 160);
        return "屏幕已关闭";
    }

    /**
     * 关闭屏幕cx
     */
    public String openScreen() {
        PowerManager pm=(PowerManager) con.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bridge");
        //点亮屏幕
        wl.acquire();
        wl.release();
     //   Settings.System.putInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        return "屏幕已打开";
    }


    /**
     * 增大多媒体音量
     */
    public String setVolumeUp() {
        return playSoundTips(TIPS_MUSIC_RAISE);
    }

    /**
     * 降低多媒体音量
     */
    public String setVolumeDown() {
        return playSoundTips(TIPS_MUSIC_LOWER);
    }

    /**
     * 设置静音
     */
    public String setMuteVolume() {
        setMute(true);
        return "媒体音已静音";
    }

    /**
     * 取消静音
     */
    public String setUnMuteVolume() {
        setMute(false);
        return "媒体音已取消静音";
    }

    private void setMute(boolean mute) {
        if(mute){
            int pre_volune = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
            if(pre_volune!=0){
                cur_volume = pre_volune;
            }
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0 , 0);
            mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0 , 0);
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,0 , 0);
            mAudioManager.setStreamVolume(AudioManager.STREAM_RING,0 , 0);
            mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,0 , 0);
            mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,0 , 0);
            mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,0 , 0);
        }else{
            if(Build.VERSION.SDK_INT < 23) {
                mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            }
            int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if(currentVolume==0){
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,cur_volume, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, cur_volume, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,cur_volume, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING,cur_volume, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,cur_volume, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,cur_volume, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,cur_volume/2, 0);
            }
        }
    }

    /**
     * 音量最大
     */

    public String setMaxVolume() {
        return playSoundTips(TIPS_MUSIC_SETMAX);
    }

    /***
     * 音量最小
     */
    public String setMinVolume() {
        return playSoundTips(TIPS_MUSIC_SETMIN);
    }


    //音量调节播报
    private String playSoundTips(final int type) {
        setMute(false);
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM); // 系统当前音量/3
        AILog.d("volume=" + currentVolume);
        String platform = SystemPropertiesProxy.get(con,KEY_PLATFORM);
        switch (type) {
            case TIPS_MUSIC_RAISE:// 增大音量
                if (TextUtils.isEmpty(platform)) {
                    if (currentVolume >= 7) {
                        currentVolume = 7;
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume * 2, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
                        return play(TIPS_MUSIC_MAX);
                    } else {
                        currentVolume++;
                        // PreferenceHelper.getInstance().setVolume(currentVolume *
                        // 3);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume * 2, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
                        return play(TIPS_MUSIC_VOLUME + currentVolume);
                    }
                } else {
                    currentVolume = currentVolume/3;
                    if (currentVolume > 4) {
                        currentVolume = 15;
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume, 0);
                        return play(TIPS_MUSIC_MAX);
                    } else {
                        currentVolume++;
                        currentVolume = currentVolume * 3;
                        // PreferenceHelper.getInstance().setVolume(currentVolume *
                        // 3);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currentVolume/2, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume, 0);
                        return play(TIPS_MUSIC_VOLUME + currentVolume);
                    }
                }
            case TIPS_MUSIC_LOWER:// 降低音量
                if (TextUtils.isEmpty(platform)) {
                    if (currentVolume <= 0) {
                        currentVolume = 0;
                        // 静音了播报无作用
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume * 2, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
                        return play(TIPS_MUSIC_MIN);
                    } else {
                        --currentVolume;
                        // PreferenceHelper.getInstance().setVolume(currentVolume *
                        // 3);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume * 2, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
                        return play(TIPS_MUSIC_VOLUME + currentVolume);

                    }
                } else {
                    currentVolume = currentVolume/3;
                    if (currentVolume <= 0) {
                        currentVolume = 0;
                        // 静音了播报无作用
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume, 0);
                        return play(TIPS_MUSIC_MIN);
                    } else {
                        --currentVolume;
                        currentVolume = currentVolume * 3;
                        // PreferenceHelper.getInstance().setVolume(currentVolume *
                        // 3);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currentVolume/2, 0);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume, 0);
                        return play(TIPS_MUSIC_VOLUME + currentVolume);

                    }
                }
            case TIPS_MUSIC_SETMAX:// 音量最大
                if (TextUtils.isEmpty(platform)) {
                    currentVolume = 7;
                    // PreferenceHelper.getInstance().setVolume(currentVolume * 3);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume * 2, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,currentVolume, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
                } else {
                    currentVolume = 15;
                    // PreferenceHelper.getInstance().setVolume(currentVolume * 3);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,currentVolume, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume, 0);
                }
                return play(TIPS_MUSIC_MAX);
            case TIPS_MUSIC_SETMIN:// 音量最小
                currentVolume = 0;
                // PreferenceHelper.getInstance().setVolume(currentVolume * 3);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        currentVolume, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
                        currentVolume, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                        currentVolume, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currentVolume, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume, 0);
                return play(TIPS_MUSIC_MIN);
            default:
                break;
        }
        return null;

    }

    //屏幕亮度调节播报
    private String playBrightnessTips(int type, int brightness) {
        brightness = brightness / 51;
        if (brightness < TIPS_BRIGHTNESS_MIN_VOLUME) {
            brightness = TIPS_BRIGHTNESS_MIN_VOLUME;
        }
        AILog.i(TAG, "currentBrightness：" + (brightness * 51));
        Settings.System.putInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness * 51);//设置亮度值
        preferenceHelper.setBrightness(brightness * 51);
        switch (type) {
            case TIPS_BRIGHTNESS_RAISE:
                if (brightness == TIPS_BRIGHTNESS_MAX_VOLUME) {
                    return play(TIPS_BRIGHTNESS_MAX);
                } else {
                    return play(TIPS_BRIGHTNESS_VOLUME + brightness);
                }
            case TIPS_BRIGHTNESS_LOWER:
                if (brightness == TIPS_BRIGHTNESS_MIN_VOLUME) {
                    return play(TIPS_BRIGHTNESS_MIN);
                } else {
                    return play(TIPS_BRIGHTNESS_VOLUME + brightness);
                }
            default:
                break;
        }
        return null;
    }

    private String play(String text) {
        //等待播报语音可以播报的接口
        AILog.d("", "text=" + text);
        return text;
    }

    public class PreferenceHelper {
        public static final String TAG = "AIOS-SDK-PreferenceHelper";
        public static final String CONFIG_FILE_NAME = "AIOSSDKConfigs";
        public static final String PREF_KEY_WIFI_STATE = "pref_key_wifi_state";//wifi状态
        public static final String PREF_KEY_MEDIA_VOLUME = "pref_key_media_volume";//音量大小
        public static final String PREF_KEY_BRIGHTNESS = "pref_key_brightness";//亮度大小

        private SharedPreferences mSp;

        public PreferenceHelper() {
            mSp = AIOSForCarSDK.getContext().getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        }

        /**
         * 获取系统媒体类型音量
         *
         * @return 音量值，默认为最大15
         */
        public int getVolume() {
            int volume = mSp.getInt(PREF_KEY_MEDIA_VOLUME, 15);
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
                int systemBrightness = Settings.System.getInt(AIOSForCarSDK.getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);//读取系统当前屏幕亮度值
                if (systemBrightness == 0 && curBrightness != 0) {//如果系统当前屏幕亮度是 0 && 缓存屏幕亮度不是 0 使用缓存亮度值
                    return curBrightness;
                } else if (curBrightness != systemBrightness) {
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
         *
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
    }
}
