package com.aispeech.aios.adapter.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.AdapterApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @desc 截屏工具类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class ImageBlur {
    private static final String TAG = "AIOS-ImageBlur";

    /**
     * 注意：需要系统签名
     * @return 截屏的图片
     */
    public static Bitmap screenshot() {
        Bitmap mScreenBitmap = null;
        WindowManager wmManager = (WindowManager) AdapterApplication
                .getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wmManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        @SuppressWarnings("rawtypes")
        Class testClass;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                testClass = Class.forName("android.view.SurfaceControl");
            }
            else {
                testClass = Class.forName("android.view.Surface");
            }
            AILog.i(TAG, "testClass : " + testClass);
            @SuppressWarnings("unchecked")
            Method saddMethod1 = testClass.getMethod("screenshot", int.class, int.class);
            AILog.i(TAG, "width : " + displayMetrics.widthPixels);
            AILog.i(TAG, "height : " + displayMetrics.heightPixels);
            mScreenBitmap = (Bitmap) saddMethod1.invoke(null, displayMetrics.widthPixels, displayMetrics.heightPixels);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return mScreenBitmap;
    }
}
