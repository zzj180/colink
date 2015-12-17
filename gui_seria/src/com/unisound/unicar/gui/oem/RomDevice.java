/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : RomDevice.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.oem
 * @Author : Dancindream
 * @CreateDate : 2013-9-9
 */
package com.unisound.unicar.gui.oem;


import com.unisound.unicar.gui.route.baidu.BaiduMapSdk;
import com.unisound.unicar.gui.route.baidu.BaiduUriApi;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-9
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-9
 * @Modified:
 * 2013-9-9: 实现基本功能
 */
public class RomDevice {
	public static final String TAG = "RomDevice";
	private static final String INVALID_IMEI = "000000000000000";

	public static String getDeviceId(Context context) {
		String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

		if (deviceId == null) {
			deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		}

		return deviceId == null ? INVALID_IMEI : deviceId;
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";

		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

			versionName = packageInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return versionName;
	}
	
	public static boolean hasBluePhoneClient(Context context) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo("com.colink.bluetoolthe", 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean hasBaiduMapClient(Context context) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo("com.baidu.navi", 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public static void showLocation(Context context, String title, String content, String lat, String lng) {
		if (context == null) {
			return;
		}

		boolean mHasBaiduMapClient = hasBaiduMapClient(context);
		if (mHasBaiduMapClient) {
			BaiduUriApi.showLocation(context, title, content, lat, lng);
		} else {
			BaiduMapSdk.showLocation(context, title, content, lat, lng);
		}
	}

	public static void showRoute(Context context, String mode, double fromLat, double fromLng, String fromCity,
									String fromPoi, double toLat, double toLng, String toCity, String toPoi) {
		if (context == null) {
			return;
		}

		boolean mHasBaiduMapClient = hasBaiduMapClient(context);
		if (mHasBaiduMapClient) {
			BaiduUriApi.showRoute(context, mode, fromLat, fromLng, fromCity, fromPoi, toLat, toLng, toCity, toPoi);
		} else {
			BaiduMapSdk.showRoute(context, mHasBaiduMapClient,mode, fromLat, fromLng, fromCity, fromPoi, toLat, toLng, toCity, toPoi);
		}
	}
}
