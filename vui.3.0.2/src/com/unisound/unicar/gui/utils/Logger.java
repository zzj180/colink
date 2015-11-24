package com.unisound.unicar.gui.utils;

import android.util.Log;

/**
 * 
 * @author xiaodong
 * @date 20150617
 */
public class Logger {

    private static final String TAG = "UniCarGUI";

    public static boolean DEBUG = true;

    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) Log.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (DEBUG) Log.v(tag, msg);
    }

    public static void d(Object obj) {
        if (DEBUG) Log.d(TAG, obj.toString());
    }

    // public static void d(Object paramObject, String msg) {
    // if (!DEBUG)
    // return;
    // if (paramObject == null) {
    // Log.d(TAG, msg);
    // return;
    // }
    // if ((paramObject instanceof Class)) {
    // Log.d(((Class) paramObject).getSimpleName(), msg);
    // return;
    // }
    // if ((paramObject instanceof String)) {
    // Log.d(paramObject.toString(), msg);
    // return;
    // }
    // Log.d(paramObject.getClass().getSimpleName(), msg);
    // }

    public static void d(String tag, String msg) {
        if (DEBUG) Log.d(tag, "!--->" + msg);
    }

    public static void d(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }

    public static void d(String tag, Exception e) {
        Log.d(tag, e.toString());
    }

    public static void w(String msg) {
        if (DEBUG) Log.w(TAG, msg);
    }

    public static void w(String tag, String string) {
        if (DEBUG) Log.w(tag, string);
    }

    public static void e(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG) Log.e(tag, msg, tr);
    }

    public static void printStackTrace(Exception e) {
        if (DEBUG) Log.e(TAG, e.toString());
    }


}
