/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : RomControl.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.oem
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package com.unisound.unicar.gui.oem;

import android.content.Context;

import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified: 2013-9-6: 实现基本功能
 */
public class RomControl {
    public static final String TAG = "RomControl";
    public static final String ROM_APP_LAUNCH = "ROM_APP_LAUNCH";
    public static final String ROM_APP_UNINSTALL = "ROM_APP_UNINSTALL";
    public static final String ROM_APP_EXIT = "ROM_APP_EXIT";
    public static final String ROM_CALL = "ROM_CALL";
    public static final String ROM_BROWSER_URL = "ROM_BROWSER_URL";
    public static final String ROM_SEND_SMS = "ROM_SEND_SMS";

    public static final String ROM_OPEN_3G = "ROM_OPEN_3G";
    public static final String ROM_CLOSE_3G = "ROM_CLOSE_3G";
    public static final String ROM_OPEN_DISPLAY_SETTINGS = "ROM_OPEN_DISPLAY_SETTINGS";
    public static final String ROM_OPEN_BLUETOOTH = "ROM_OPEN_BLUETOOTH";
    public static final String ROM_CLOSE_BLUETOOTH = "ROM_CLOSE_BLUETOOTH";
    public static final String ROM_OPEN_TIME_SETTINGS = "ROM_OPEN_TIME_SETTINGS";
    public static final String ROM_OPEN_MODEL_INAIR = "ROM_OPEN_MODEL_INAIR";
    public static final String ROM_CLOSE_MODEL_INAIR = "ROM_CLOSE_MODEL_INAIR";
    public static final String ROM_OPEN_MODEL_MUTE = "ROM_OPEN_MODEL_MUTE";
    public static final String ROM_CLOSE_MODEL_MUTE = "ROM_CLOSE_MODEL_MUTE";
    public static final String ROM_OPEN_MODEL_VIBRA = "ROM_OPEN_MODEL_VIBRA";
    public static final String ROM_CLOSE_MODEL_VIBRA = "ROM_CLOSE_MODEL_VIBRA";
    public static final String ROM_OPEN_SOUND_SETTINGS = "ROM_OPEN_SOUND_SETTINGS";
    public static final String ROM_OPEN_ROTATION = "ROM_OPEN_ROTATION";
    public static final String ROM_CLOSE_ROTATION = "ROM_CLOSE_ROTATION";
    public static final String ROM_OPEN_WALLPAPER_SETTINGS = "ROM_OPEN_WALLPAPER_SETTINGS";
    public static final String ROM_OPEN_WIFI = "ROM_OPEN_WIFI";
    public static final String ROM_CLOSE_WIFI = "ROM_CLOSE_WIFI";
    public static final String ROM_OPEN_WIFI_SPOT = "ROM_OPEN_WIFI_SPOT";
    public static final String ROM_CLOSE_WIFI_SPOT = "ROM_CLOSE_WIFI_SPOT";

    public static final String ROM_INCREASE_VOLUMNE = "ROM_INCREASE_VOLUMNE";
    public static final String ROM_DECREASE_VOLUMNE = "ROM_DECREASE_VOLUMNE";
    public static final String ROM_VOLUMNE_MAX = "ROM_VOLUMNE_MAX";
    public static final String ROM_VOLUMNE_MIN = "ROM_VOLUMNE_MIN";
    public static final String ROM_VOLUMNE_SET = "ROM_VOLUMNE_SET";

    public static final String START_ASSISTANT = "START_ASSISTANT";

    public static final String OEM_VIDEO_SHOW = "OEM_VIDEO_SHOW";

    public static final int SVOLUME_CHANGE_STEP = 3;

    public static void enterControl(Context context, String type, Object... params) {
        String logParams = "";
        if (params != null && params.length > 0) {
            for (Object obj : params) {
                logParams += "; p:" + String.valueOf(obj);
            }
        }
        Logger.d(TAG, "TYPE:" + type + logParams);

        if (ROM_APP_LAUNCH.equals(type)) {
            RomApp.lanchApp(context, (String) params[0], (String) params[1]);
        } else if (ROM_APP_UNINSTALL.equals(type)) {
            RomApp.uninstallApp(context, (String) params[0]);
        } else if (ROM_OPEN_3G.equals(type)) {
            RomNetWork.setMobileDataStatus(context, true);
        } else if (ROM_CLOSE_3G.equals(type)) {
            RomNetWork.setMobileDataStatus(context, false);
        } else if (ROM_OPEN_DISPLAY_SETTINGS.equals(type)) {
            RomSystemSetting.openDisplaySettings(context);
        } else if (ROM_OPEN_BLUETOOTH.equals(type)) {
            RomSystemSetting.setBluetoothEnabled(true);
        } else if (ROM_CLOSE_BLUETOOTH.equals(type)) {
            RomSystemSetting.setBluetoothEnabled(false);
        } else if (ROM_OPEN_TIME_SETTINGS.equals(type)) {
            RomSystemSetting.openTimeSettings(context);
        } else if (ROM_OPEN_MODEL_INAIR.equals(type)) {
            RomSystemSetting.setFlightModeEnabled(context, true);
        } else if (ROM_CLOSE_MODEL_INAIR.equals(type)) {
            RomSystemSetting.setFlightModeEnabled(context, false);
        } else if (ROM_OPEN_MODEL_MUTE.equals(type)) {
            RomSystemSetting.setRingerMode(context, true, false);
        } else if (ROM_CLOSE_MODEL_MUTE.equals(type)) {
            RomSystemSetting.setRingerMode(context, false, false);
        } else if (ROM_OPEN_MODEL_VIBRA.equals(type)) {
            RomSystemSetting.setRingerMode(context, false, true);
        } else if (ROM_CLOSE_MODEL_VIBRA.equals(type)) {
            RomSystemSetting.setRingerMode(context, false, false);
        } else if (ROM_OPEN_SOUND_SETTINGS.equals(type)) {
            RomSystemSetting.openSoundSettings(context);
        } else if (ROM_OPEN_ROTATION.equals(type)) {
            RomSystemSetting.setAutoOrientationEnabled(context, true);
        } else if (ROM_CLOSE_ROTATION.equals(type)) {
            RomSystemSetting.setAutoOrientationEnabled(context, false);
        } else if (ROM_OPEN_WALLPAPER_SETTINGS.equals(type)) {
            RomSystemSetting.openWallPaperSettings(context);
        } else if (ROM_OPEN_WIFI.equals(type)) {
            RomNetWork.setWifiEnabled(context, true);
        } else if (ROM_CLOSE_WIFI.equals(type)) {
            RomNetWork.setWifiEnabled(context, false);
        } else if (ROM_OPEN_WIFI_SPOT.equals(type)) {
            RomNetWork.setWifiSpotEnabled(context, true);
        } else if (ROM_CLOSE_WIFI_SPOT.equals(type)) {
            RomNetWork.setWifiSpotEnabled(context, false);
        } else if (ROM_BROWSER_URL.equals(type)) {
            RomSystemSetting.openUrl(context, (String) params[0]);
        } else if (ROM_DECREASE_VOLUMNE.equals(type)) {
            RomSystemSetting.RaiseOrLowerVolume(context, false, (Integer) params[0]);
        } else if (ROM_INCREASE_VOLUMNE.equals(type)) {
            RomSystemSetting.RaiseOrLowerVolume(context, true, (Integer) params[0]);
        } else if (ROM_VOLUMNE_MAX.equals(type)) {
            RomSystemSetting.setMaxVolume(context);
        } else if (ROM_VOLUMNE_MIN.equals(type)) {
            RomSystemSetting.setMinVolume(context);
        } else if (ROM_VOLUMNE_SET.equals(type)) {
            RomSystemSetting.setVolume(context, (Integer) params[0]);
        } else if (START_ASSISTANT.equals(type)) {

        } else if (OEM_VIDEO_SHOW.equals(type)) {

        } else if (ROM_APP_EXIT.equals(type)) {
            RomCustomerProcessing.exitAppForOther(context, (String) params[0], (String) params[1]);
        }
    }
}
