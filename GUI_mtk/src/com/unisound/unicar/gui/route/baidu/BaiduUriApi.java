package com.unisound.unicar.gui.route.baidu;

/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : BaiduUriApi.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.baidu
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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

//	private static String act = Intent.ACTION_VIEW;
//	private static String cat = Intent.CATEGORY_DEFAULT;
	// private static String pkg = "com.autonavi.minimap";

	// intent://map/marker?location=40.047669,116.313082&title=我的位置&content=百度奎科大厦&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end
//	private static final String LOCATION_SEARCH_INTNET_FORMAT = "intent://map/marker?location={0}&title={1}&content={2}&src=YunZhiSheng|Assistant#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";

	// "intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving&region=西安&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end"
//	private static final String ROUTE_POI_DATA_FORMAT = "{0}";
//	private static final String ROUTE_INTENT_FORMAT = "intent://map/direction?origin={0}&destination={1}&mode={2}&origin_region={3}&destination_region={4}&src=YunZhiSheng|Assistant#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";

	public static void showLocation(Context context, String title,
			String content, String lat, String lng) {
	/*	String gps = lat + "," + lng;
		String intentAction = DataTool.formatString(
				LOCATION_SEARCH_INTNET_FORMAT, gps, title, content);
		showBaiduMap(context, intentAction);*/
		

		Intent intent=new Intent();
		intent.setData(Uri.parse("bdnavi://where?"));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}
/*	private static void forceStopPackage(String pkgName, Context context)
			throws Exception {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		Method method = Class.forName("android.app.ActivityManager").getMethod(
				"forceStopPackage", String.class);
		method.invoke(am, pkgName);
	}*/

	public static void showRoute(Context context, String mode, double fromLat,
			double fromLng, String fromCity, String fromPoi, double toLat,
			double toLng, String toCity, String toPoi) {
		/*try {
			forceStopPackage("com.baidu.navi", context);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	/*	String data = "intent://map/direction?" + "&origin=latlng:" + fromLat
				+ "," + fromLng + "|name:" + fromPoi + "&destination=latlng:"
				+ toLat + "," + toLng + "|name:" + toPoi + "&mode=driving"
				+ "&src=" + "yunzhisheng|vui_car_assistant#Intent;"
				+ "scheme=bdapp;package=com.baidu.BaiduMap;end";
		Log.d(TAG, "showRoute data :" + data);

		try {
			Intent intent = Intent.getIntent(data);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		// intent.addCategory(cat);
		// //intent.setPackage(pkg);
		// intent.setData(Uri.parse(data.trim()));
		
		Intent intent = new Intent();
		intent.setData(Uri.parse("bdnavi://plan?start=" + fromLat + "," + fromLng + ","+fromPoi
				 + "&dest=" + toLat+","+toLng+","+toPoi));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}

	@SuppressWarnings({ "deprecation", "unused" })
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
	
	public static void openBaiduNavi(Context context,String action){
		Intent intent = new Intent();
		intent.setData(Uri.parse(action));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}
}
