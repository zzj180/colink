/**
 * Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : UserPreference.java
 * @ProjectName : V Plus 1.0
 * @PakageName : cn.yunzhisheng.vui.assistant.preference
 * @Author : Dancindream
 * @CreateDate : 2012-6-6
 */
package com.colink.zzj.txzassistant.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class UserPreference {
    private SharedPreferences mPreferences;

    public UserPreference(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences sharedUserPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getString(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }

    public void putString(String key, String value) {
        Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public int getInt(String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    public void putInt(String key, int value) {
        Editor editor = mPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public long getLong(String key, long defValue) {
        return mPreferences.getLong(key, defValue);
    }

    public void putLong(String key, long value) {
        Editor editor = mPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public float getFloat(String key, float defValue) {
        return mPreferences.getFloat(key, defValue);
    }

    public void putFloat(String key, float value) {
        Editor editor = mPreferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

}
