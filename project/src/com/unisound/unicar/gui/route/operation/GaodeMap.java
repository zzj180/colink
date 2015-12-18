/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
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
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Alieen
 * @CreateDate : 2015-07-08
 * @ModifiedBy : Alieen
 * @ModifiedDate: 2015-07-08
 * @Modified:
 * 2015-07-08: 实现基本功能
 */
public class GaodeMap {
	public static final String TAG = "GaodeMap";
	public static final String ROUTE_MODE_WALKING = "walking"; // 步行
	public static final String ROUTE_MODE_DRIVING = "driving"; // 驾车
	public static final String ROUTE_MODE_TRANSIT = "transit"; // 公交
	private static boolean mHasGaodeMapClient = false;

	public static boolean hasGaodeMapClient(Context context) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo("com.autonavi.minimap", 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	public static void showLocation(Context context, String title, String content, double lat, double lng) {
		if (context == null) {
			return;
		}

		mHasGaodeMapClient = hasGaodeMapClient(context);
		if (mHasGaodeMapClient) {
			GaodeUriApi.showLocation(context, title, content, lat, lng);
		} 
	}

	public static void showRoute(Context context, String mode, double toLat, double toLng, String toCity,
									String toPoi , int dev) {
		if (context == null) {
			Logger.e(TAG, "showRoute:context null!");
			return;
		}

		mHasGaodeMapClient = hasGaodeMapClient(context);
		if (mHasGaodeMapClient) {
			GaodeUriApi.startNavi(context, toLat, toLng, toPoi, 2, dev);
		} else {
			Toast.makeText(context, R.string.gaode_nofind_map, Toast.LENGTH_LONG).show();
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
