package com.unisound.unicar.gui.preference;

import com.unisound.unicar.gui.utils.Logger;

import android.content.Context;

/**
 * 
 * @author xiaodong
 * @date 20150623
 */
public class Constant {

    private static final String TAG = Constant.class.getSimpleName();

    private static final String SP_UNICAR_GUI = "sp_unicar_gui";

    public static final String SP_UNICAR_KEY_IS_FIRST_START = "is_first_start";

    /**
     * set Window service first start
     * 
     * @param context
     * @param isFirstStart
     */
    public static void setFirstStart(Context context, boolean isFirstStart) {
        SharedPreferencesHelper spHelper =
                SharedPreferencesHelper.getInstance(context, Constant.SP_UNICAR_GUI);
        Logger.d(TAG, "!--->setFirstStart: " + isFirstStart);
        spHelper.saveBooleanValue(SP_UNICAR_KEY_IS_FIRST_START, isFirstStart);
    }

    /**
     * is Window service first start
     * 
     * @param context
     * @return
     */
    public static boolean isFirstStart(Context context) {
        SharedPreferencesHelper spHelper =
                SharedPreferencesHelper.getInstance(context, Constant.SP_UNICAR_GUI);
        return spHelper.getBooleanValue(SP_UNICAR_KEY_IS_FIRST_START, true);
    }
}
