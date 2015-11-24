package com.unisound.unicar.gui.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * 
 * @author XiaoDong
 * @date 20150724
 */
public class ResourceUtils {

    private static TypedValue mTmpValue = new TypedValue();

    private ResourceUtils() {

    }

    /**
     * get the dimens.xml defined text size
     * 
     * @param context
     * @param resourceId
     * @return if R.dimen.id defined is dp size return the dp size; if R.dimen.id defined is sp size
     *         return thesp size
     */
    public static int getDimenXmlDefSize(Context context, int resourceId) {
        synchronized (mTmpValue) {
            TypedValue value = mTmpValue;
            context.getResources().getValue(resourceId, value, true);
            return (int) TypedValue.complexToFloat(value.data);
        }
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * 
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * 
     * @param dipValue
     * @param scale
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * 
     * @param pxValue
     * @param fontScale
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * 
     * @param spValue
     * @param fontScale
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
