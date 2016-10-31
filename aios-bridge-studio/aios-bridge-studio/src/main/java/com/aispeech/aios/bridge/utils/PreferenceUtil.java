
package com.aispeech.aios.bridge.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.aispeech.aios.bridge.BridgeApplication;

public class PreferenceUtil {
    public static final String IS_AEC_ENABLED = "is_aec_enabled";
    public static final String IS_INTERRUPT_ENABLED = "is_interrupt_enabled";
    public static final String IS_AIOS_SWITCH = "is_aios_switch";
    public static final String IS_NATIVE_SHORTCUT_ENABLE = "is_native_shortcut_enable";
    public static final String AIOS_VOLUME = "aios_volume";
    public static final String IS_SHOW_GLOBAL_MIC = "is_show_global_mic";

    public static String getString(Context context, String key, String def) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(BridgeApplication.getContext());

        return sp.getString(key, def);
    }

    public static String getString(Context context, String key) {
        return getString(context, key, "");
    }


    public static void setString(Context context, String key,String value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(BridgeApplication.getContext());
        sp.edit().putString(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean def) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(BridgeApplication.getContext());
        return sp.getBoolean(key, def);
    }

    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(BridgeApplication.getContext());
        sp.edit().putBoolean(key, value).commit();
    }

    public static int getInteger(Context context, String key, int def){
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(BridgeApplication.getContext());
        return sp.getInt(key,def);
    }

    public static void setInteger(Context context, String key, int value){
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(BridgeApplication.getContext());
        sp.edit().putInt(key, value).apply();
    }

    public static float getFloat(Context context,String key, float defValue) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(BridgeApplication.getContext());
        return sp.getFloat(key, defValue);
    }

    public static void setFloat(Context context,String key, float value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(BridgeApplication.getContext());
        sp.edit().putFloat(key, value).apply();
    }

}
