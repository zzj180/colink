package com.aispeech.aios.adapter.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.math.BigDecimal;

/**
 * @desc 数学相关工具类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class MathUtil {

    private static MathUtil mMathUtil;

    /**
     * @return MathUtil实例
     */
    public static synchronized MathUtil getInstance() {
        if (mMathUtil == null) {
            mMathUtil = new MathUtil();
        }

        return mMathUtil;
    }

    /**
     * m转换成km,小数四舍五入保留两位，不够1000直接返回
     *
     * @param m 多少米
     * @return 多少km
     */
    public String convertToKm(long m) {
        String data = "";
        if (m < 1000) {//小于1000m直接返回
            data = String.valueOf(m) + "m";
        } else {
            BigDecimal bd = new BigDecimal(m / 1000.0);
            data = String.valueOf(bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()) + "Km";
        }
        return data;
    }

    /**
     * 把dip转换成px
     * @param ctx Context object
     * @param dpValue  dip值
     * @return px值
     */
    public int dip2px(Context ctx, int dpValue) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        float scale = metrics.density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 把px转换成dip
     * @param ctx Context object
     * @param pxValue px值
     * @return dip值
     */
    public int px2dip(Context ctx, int pxValue) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        float scale = metrics.density;
        return (int) (pxValue / scale + 0.5f);
    }
}