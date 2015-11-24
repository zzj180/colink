/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : BaiduMapSdk.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.baidu
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package com.unisound.unicar.gui.location.operation;

import android.content.Context;
import android.content.Intent;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified:
 * 2013-9-6: 实现基本功能
 */
public class BaiduMapSdk {
	public static final String TAG = "BaiduMapSdk";

	public static void showLocation(Context context, String title, String content, String lat, String lng) {
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

	public static void showRoute(Context context, String mode, double fromLat, double fromLng, String fromCity,
									String fromPoi, double toLat, double toLng, String toCity, String toPoi) {
		if (context == null) {
			return;
		}

		Intent intentRoutePlan = new Intent(context, RouteOverlayActivity.class);
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
}
