package com.unisound.unicar.gui.route.baidu;

import java.lang.reflect.Method;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.unisound.unicar.gui.utils.DataTool;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified: 2013-9-6: 实现基本功能
 */
public class BaiduUriApi {
    public static final String TAG = "BaiduUriApi";

    private static String act = Intent.ACTION_VIEW;
    private static String cat = Intent.CATEGORY_DEFAULT;
    private static String pkg = "com.autonavi.minimap";

    // intent://map/marker?location=40.047669,116.313082&title=我的位置&content=百度奎科大厦&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end
    private static final String LOCATION_SEARCH_INTNET_FORMAT =
            "intent://map/marker?location={0}&title={1}&content={2}&src=YunZhiSheng|Assistant#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";

    // "intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving&region=西安&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end"
    private static final String ROUTE_POI_DATA_FORMAT = "{0}";
    private static final String ROUTE_INTENT_FORMAT =
            "intent://map/direction?origin={0}&destination={1}&mode={2}&origin_region={3}&destination_region={4}&src=YunZhiSheng|Assistant#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";

    public static void showLocation(Context context, String title, String content, String lat,
            String lng) {
        String gps = lat + "," + lng;
        String intentAction =
                DataTool.formatString(LOCATION_SEARCH_INTNET_FORMAT, gps, title, content);
        showBaiduMap(context, intentAction);
    }

    private static void forceStopPackage(String pkgName, Context context)
			throws Exception {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		Method method = Class.forName("android.app.ActivityManager").getMethod(
				"forceStopPackage", String.class);
		method.invoke(am, pkgName);
	}

    
    public static void showRoute(Context context, String mode, double fromLat, double fromLng,
            String fromCity, String fromPoi, double toLat, double toLng, String toCity, String toPoi) {
    	
    	try {
			forceStopPackage("com.baidu.navi", context);
		} catch (Exception e) {
			e.printStackTrace();
		}
        // add by ch 此方式只适用于安装了百度地图 (URI API)
      /*  String data =
                "intent://map/direction?" + "&origin=latlng:" + fromLat + "," + fromLng + "|name:"
                        + fromPoi + "&destination=latlng:" + toLat + "," + toLng + "|name:" + toPoi
                        + "&mode=driving" + "&region=" + toCity + "&src="
                        + "yunzhisheng|vui_car_assistant#Intent;"
                        + "scheme=bdapp;package=com.baidu.BaiduMap;end";
        Log.d(TAG, "showRoute data :" + data);

        // String dataUri = "http://api.map.baidu.com/direction?"+"&origin=latlng:" + fromLat
        // + "," + fromLng + "|name:" + fromPoi + "&destination=latlng:"
        // + toLat + "," + toLng + "|name:" + toPoi + "&mode=driving"
        // + "&region="+toCity+"&output=html"
        // + "&src=yunzhisheng|vui_car_assistant";
        try {
            Intent intent = Intent.getIntent(data);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    	
    	Intent intent = new Intent();
		intent.setData(Uri.parse("bdnavi://plan?start=" + fromLat + "," + fromLng + ","+fromPoi
				 + "&dest=" + toLat+","+toLng+","+toPoi));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
    }

    private static void showBaiduMap(Context context, String action) {
        if (context == null) {
            return;
        }

        try {
            Intent intent = Intent.getIntent(action);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
