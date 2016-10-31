package com.aispeech.aios.bridge.utils;

import android.util.Log;

/**
 * 
 * @author ZZJ
 * @date 20150617
 */
public class Logger {

    private static final String TAG = "Logger";

    public static boolean DEBUG = true;

    public static void i(String msg) {
        if (DEBUG && msg!=null) {
            Log.i(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG && msg!=null) Log.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (DEBUG && msg!=null) Log.v(tag, msg);
    }

    public static void d(Object obj) {
        if (DEBUG && obj!=null) Log.d(TAG, obj.toString());
    }


    public static void d(String tag, String msg) {
        if (DEBUG && msg!=null) Log.d(tag, "!--->" + msg);
    }

    public static void d(String msg) {
        if (DEBUG && msg!=null) Log.d(TAG, msg);
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
        if (DEBUG && msg!=null) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG && msg!=null) Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG && msg!=null) Log.e(tag, msg, tr);
    }

    public static void printStackTrace(Exception e) {
        if (DEBUG) Log.e(TAG, e.toString());
    }


}
