/**
 * Copyright (c) 2012-2015 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : GaodeMapSdk.java
 * @ProjectName : UnicarGUI
 * @PakageName : com.unisound.unicar.gui.route.operation
 * @Author : xiaodong
 * @CreateDate : 20150911
 */
package com.unisound.unicar.gui.route.operation;

import com.unisound.unicar.gui.utils.Logger;

import android.content.Context;
import android.content.Intent;

/**
 * Use Gaode Map Sdk to show UI
 * 
 * @author xiaodong
 * @date 20150911
 */
public class GaodeMapSdk {

    public static final String TAG = "GaodeMapSdk";

    /**
     * show Location Activity without Gaode map
     * 
     * @param context
     * @param title
     * @param content
     * @param lat
     * @param lng
     */
    public static void showLocation(Context context, String title, String content, double lat,
            double lng) {
        if (context == null) {
            return;
        }

        Intent intentRoutePlan = new Intent(context, GaodeLocationActivity.class);
        intentRoutePlan.putExtra(GaodeLocationActivity.TAG_LAT, lat);
        intentRoutePlan.putExtra(GaodeLocationActivity.TAG_LNG, lng);
        intentRoutePlan.putExtra(GaodeLocationActivity.TAG_TITLE, title);
        intentRoutePlan.putExtra(GaodeLocationActivity.TAG_CONTENT, content);
        intentRoutePlan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentRoutePlan);
    }

    /**
     * show Route Activity without Gaode map
     * 
     * @param context
     * @param mode
     * @param fromLat
     * @param fromLng
     * @param fromCity
     * @param fromPoi
     * @param toLat
     * @param toLng
     * @param toCity
     * @param toPoi
     */
    public static void showRoute(Context context, String mode, double fromLat, double fromLng,
            String fromCity, String fromPoi, double toLat, double toLng, String toCity, String toPoi) {
        if (context == null) {
            return;
        }
        Logger.d(TAG, "!--->showRoute--start---GaodeRouteActivity");
        Intent intentRoutePlan = new Intent(context, GaodeRouteActivity.class);
        intentRoutePlan.putExtra(GaodeRouteActivity.TAG_MODE, mode);
        intentRoutePlan.putExtra(GaodeRouteActivity.TAG_FROM_LAT, fromLat);
        intentRoutePlan.putExtra(GaodeRouteActivity.TAG_FROM_LNG, fromLng);
        intentRoutePlan.putExtra(GaodeRouteActivity.TAG_FROM_CITY, fromCity);
        intentRoutePlan.putExtra(GaodeRouteActivity.TAG_FROM_POI, fromPoi);
        intentRoutePlan.putExtra(GaodeRouteActivity.TAG_TO_LAT, toLat);
        intentRoutePlan.putExtra(GaodeRouteActivity.TAG_TO_LNG, toLng);
        intentRoutePlan.putExtra(GaodeRouteActivity.TAG_TO_CITY, toCity);
        intentRoutePlan.putExtra(GaodeRouteActivity.TAG_TO_POI, toPoi);
        intentRoutePlan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentRoutePlan);
    }
}
