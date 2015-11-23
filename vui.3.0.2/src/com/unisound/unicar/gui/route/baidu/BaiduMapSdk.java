package com.unisound.unicar.gui.route.baidu;

import android.content.Context;
import android.content.Intent;

/**
 * BaiduMapSdk Util
 * 
 * @author xiaodong
 * @date 20150914
 */
public class BaiduMapSdk {

    public static final String TAG = BaiduMapSdk.class.getSimpleName();

    /**
     * Location without Baidu map
     * 
     * @param context
     * @param title
     * @param content
     * @param lat
     * @param lng
     */
    public static void showLocation(Context context, String title, String content, String lat,
            String lng) {
        if (context == null) {
            return;
        }

        Intent intentRoutePlan = new Intent(context, LocationOverlayActivity.class);
        intentRoutePlan.putExtra(LocationOverlayActivity.TAG_LAT, lat);
        intentRoutePlan.putExtra(LocationOverlayActivity.TAG_LNG, lng);
        intentRoutePlan.putExtra(LocationOverlayActivity.TAG_TITLE, title);
        intentRoutePlan.putExtra(LocationOverlayActivity.TAG_CONTENT, content);
        intentRoutePlan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentRoutePlan);
    }

    /**
     * Navigation without Baidu map (web show)
     * 
     * @param context
     * @param hasMap
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
    public static void showRoute(Context context, boolean hasMap, String mode, double fromLat,
            double fromLng, String fromCity, String fromPoi, double toLat, double toLng,
            String toCity, String toPoi) {
        if (context == null) {
            return;
        }

        Intent intentRoutePlan = new Intent(context, RouteOverlayActivity.class);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_MAP, hasMap);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_MODE, mode);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_FROM_LAT, fromLat);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_FROM_LNG, fromLng);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_FROM_CITY, fromCity);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_FROM_POI, fromPoi);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_TO_LAT, toLat);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_TO_LNG, toLng);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_TO_CITY, toCity);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_TO_POI, toPoi);
        intentRoutePlan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentRoutePlan);
    }

    /**
     * Navigation without Baidu map (local View show route but can't navigation)
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
    public static void showRouteNoBaiduMap(Context context, String mode, double fromLat,
            double fromLng, String fromCity, String fromPoi, double toLat, double toLng,
            String toCity, String toPoi) {
        if (context == null) {
            return;
        }

        Intent intentRoutePlan = new Intent(context, RouteOverlayNoMap.class);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_FROM_LAT, fromLat);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_FROM_LNG, fromLng);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_FROM_CITY, fromCity);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_FROM_POI, fromPoi);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_TO_LAT, toLat);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_TO_LNG, toLng);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_TO_CITY, toCity);
        intentRoutePlan.putExtra(RouteOverlayActivity.TAG_TO_POI, toPoi);
        intentRoutePlan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentRoutePlan);
    }

}
