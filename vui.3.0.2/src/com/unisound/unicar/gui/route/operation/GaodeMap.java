/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : GaodeMap.java
 * @ProjectName : uniCarSolution
 * @PakageName : com.unisound.unicar.gui.route.operation
 * @Author : Alieen
 * @CreateDate : 2015-07-08
 */
package com.unisound.unicar.gui.route.operation;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Alieen
 * @CreateDate : 2015-07-08
 * @ModifiedBy : Alieen
 * @ModifiedDate: 2015-07-08
 * @Modified: 2015-07-08: 实现基本功能
 */
public class GaodeMap {
    public static final String TAG = "GaodeMap";
    public static final String ROUTE_MODE_WALKING = "walking"; // 步行
    public static final String ROUTE_MODE_DRIVING = "driving"; // 驾车
    public static final String ROUTE_MODE_TRANSIT = "transit"; // 公交
    private static boolean mHasGaodeMapClient = false;
    private static boolean mHasGaodeNavClient = false;

    public static boolean hasGaodeMapClient(Context context) {
        return DeviceTool.checkApkExist(context, "com.autonavi.minimap");
    }


    public static boolean hasGaodeNavClient(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo =
                    context.getPackageManager().getPackageInfo("com.autonavi.xmgd.navigator", 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public static void showRoute(Context context, String mode, double toLat, double toLng,
            String toCity, String toPoi, int style) {
        if (context == null) {
            Logger.e(TAG, "showRoute:context null!");
            return;
        }

        mHasGaodeMapClient = hasGaodeMapClient(context);
        mHasGaodeNavClient = hasGaodeNavClient(context);
        if (mHasGaodeNavClient) {
            GaodeUriApi.startNavi2(context, toLat, toLng, toPoi, style, 0);
        } else if (mHasGaodeMapClient) {
            GaodeUriApi.startNavi(context, toLat, toLng, toPoi, style, 0);
        } else {
            Toast.makeText(context, R.string.gaode_nofind_map, Toast.LENGTH_LONG).show();
            // TODO:
            // GaodeMapSdk.showRoute(context, mode, fromLat, fromLng, "", "", toLat, toLng, toCity,
            // toPoi);
        }
    }

    /**
     * showRoute XD added 20180911
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
            String fromCity, String fromPoi, double toLat, double toLng, String toCity,
            String toPoi, int style) {
        if (context == null) {
            Logger.e(TAG, "showRoute--new--:context null!");
            return;
        }

        mHasGaodeMapClient = hasGaodeMapClient(context);
        mHasGaodeNavClient = hasGaodeNavClient(context);
        if (mHasGaodeNavClient) {
            GaodeUriApi.startNavi2(context, toLat, toLng, toPoi, style, 0);
        } else if (mHasGaodeMapClient) {
            GaodeUriApi.startNavi(context, toLat, toLng, toPoi, style, 0);
        } else {
            Toast.makeText(context, R.string.gaode_nofind_map, Toast.LENGTH_LONG).show();
            GaodeMapSdk.showRoute(context, mode, fromLat, fromLng, fromCity, fromPoi, toLat, toLng,
                    toCity, toPoi);
        }

    }


    public static void openAMapClient(Context context) {
        if (context == null) {
            return;
        }

        mHasGaodeMapClient = hasGaodeMapClient(context);
        if (mHasGaodeMapClient) {
            GaodeUriApi.openAMap(context);
        } else {
            Toast.makeText(context, R.string.gaode_nofind_map, Toast.LENGTH_LONG).show();
        }
    }
}
