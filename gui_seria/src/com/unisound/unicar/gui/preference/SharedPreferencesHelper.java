package com.unisound.unicar.gui.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.text.TextUtils;

/**
 * 
 * @author He_xd
 * @E-mail
 * @date 20150623
 */
public class SharedPreferencesHelper {

    private static final String TAG = SharedPreferencesHelper.class.getSimpleName();

    private static SharedPreferencesHelper instance = null;
    private SharedPreferences sp;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    private SharedPreferencesHelper(Context context, String name) {
        mContext = context;
        sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        mEditor = sp.edit();
    }

    public static synchronized SharedPreferencesHelper getInstance(Context context, String name) {
        if (null == instance) {
            instance = new SharedPreferencesHelper(context, name);
        }
        return instance;
    }

    public boolean saveStringValue(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            mEditor.putString(key, value);
        }
        return mEditor.commit();
    }

    public String getStringValue(String key, String defalut) {
        return sp.getString(key, defalut);
    }

    public boolean saveBooleanValue(String key, boolean booleanValue) {
        if (!TextUtils.isEmpty(key)) {
            mEditor.putBoolean(key, booleanValue);
        }
        return mEditor.commit();
    }

    public boolean getBooleanValue(String key, boolean bool) {
        return sp.getBoolean(key, bool);
    }

    public boolean saveIntValue(String key, int intValue) {
        if (!TextUtils.isEmpty(key)) {
            // Logger.d(TAG, "!--->saveIntValue--"+intValue);
            mEditor.putInt(key, intValue);
        }
        return mEditor.commit();
    }

    public int getIntValue(String key) {
        return sp.getInt(key, 0);
    }

    /**
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public int getIntValue(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public boolean saveLongValue(String key, int longValue) {
        if (!TextUtils.isEmpty(key)) {
            mEditor.putLong(key, longValue);
        }
        return mEditor.commit();
    }

    public long getLongValue(String key) {
        return sp.getLong(key, 0);
    }

    /**
     * register OnSharedPreferenceChangeListener XD added 20150806
     * 
     * @param listener
     */
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * unregister OnSharedPreferenceChangeListener XD added 20150806
     * 
     * @param listener
     */
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

}
