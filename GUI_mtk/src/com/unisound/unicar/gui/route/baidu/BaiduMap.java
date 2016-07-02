package com.unisound.unicar.gui.route.baidu;

import android.content.Context;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : xiaodong
 * @ModifiedDate: 2015-9-14
 * @Modified:
 */
public class BaiduMap {

    public static final String TAG = "BaiduMap";

    public static final String ROUTE_MODE_WALKING = "walking"; // 步行
    public static final String ROUTE_MODE_DRIVING = "driving"; // 驾车
    public static final String ROUTE_MODE_TRANSIT = "transit"; // 公交
    private static boolean mHasBaiduMapClient = false;

    public static void init(Context context) {
        mHasBaiduMapClient = hasBaiduMapClient(context);
    }

    /**
     * 
     * @param context
     * @return
     */
    private static boolean hasBaiduMapClient(Context context) {
        return DeviceTool.checkApkExist(context, "com.baidu.BaiduMap");

        /*
         * PackageInfo packageInfo; try { packageInfo =
         * context.getPackageManager().getPackageInfo("com.baidu.BaiduMap", 0); } catch
         * (NameNotFoundException e) { packageInfo = null; } if (packageInfo == null) { return
         * false; } else { return true; }
         */
    }

    public static void showLocation(Context context, String title, String content, String lat,
            String lng) {
        if (context == null) {
            return;
        }

        mHasBaiduMapClient = hasBaiduMapClient(context);
        if (mHasBaiduMapClient) {
            BaiduUriApi.showLocation(context, title, content, lat, lng);
        } else {
            BaiduMapSdk.showLocation(context, title, content, lat, lng);
        }
    }

    /**
     * start Navi with Baidu map apk
     * 
     * @param context
     * @param fromLat
     * @param fromLon
     * @param fromPoi
     * @param toLat
     * @param toLon
     * @param toPoi
     */
    private static void startNavi(Context context, double fromLat, double fromLon, String fromPoi,
            double toLat, double toLon, String toPoi) {
        Logger.i(TAG, "-starNav-from--(" + fromLat + ", " + fromLon + ") to (" + toLat + ", "
                + toLon + ")");

        LatLng start = new LatLng(fromLat, fromLon);
        LatLng end = new LatLng(toLat, toLon);

        NaviPara params = new NaviPara();
        params.startName = fromPoi; // "从这里开始";
        params.startPoint = start;

        params.endName = toPoi; // "到这里结束";
        params.endPoint = end;
        BaiduMapNavigation.openBaiduMapNavi(params, context);
    }

    /**
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
            Logger.e(TAG, "showRoute:context null!");
            return;
        }

        // ch:最新的百度sdk 不需要判断是否安装,百度接口默认自己判断
        mHasBaiduMapClient = hasBaiduMapClient(context);
        Logger.d(TAG, "!--->showRoute--mHasBaiduMapClient = " + mHasBaiduMapClient);
        if (!mHasBaiduMapClient) {
            BaiduMapSdk.showRoute(context, mHasBaiduMapClient, mode, fromLat, fromLng, fromCity,
                    fromPoi, toLat, toLng, toCity, toPoi);
            // BaiduMapSdk.showRouteNoBaiduMap(context, mode, fromLat, fromLng, fromCity, fromPoi,
            // toLat, toLng, toCity, toPoi);
        } else {
            startNavi(context, fromLat, fromLng, fromPoi, toLat, toLng, toPoi);
            // BaiduUriApi.showRoute(context, mode, fromLat, fromLng, fromCity, fromPoi, toLat,
            // toLng, toCity, toPoi);
        }
    }

}
