/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : GaodeUriApi.java
 * @ProjectName : uniCarSolution
 * @PakageName : com.unisound.unicar.gui.route.operation
 * @Author : Alieen
 * @CreateDate : 2015-07-08
 */
package com.unisound.unicar.gui.route.operation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

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
public class GaodeUriApi {
	public static final String TAG = "GaodeUriApi";

	private static String act = Intent.ACTION_VIEW;
	private static String cat = Intent.CATEGORY_DEFAULT;
	private static String pkg = "com.autonavi.minimap";

	public static void showLocation(Context context, String title, String content, double lat, double lng) {
		Logger.d(TAG, "showLocation title : " + title + " content :　" + content);
		String data = "androidamap://viewMap?sourceApplication=ishuoshuo&poiname=" + content + "&lat=" + lat + "&lon="
						+ lng + "&dev=0";
		Log.d(TAG, "URI " + data);
		Intent intent = new Intent(act);
		intent.addCategory(cat);
		intent.setPackage(pkg);
		intent.setData(Uri.parse(data.trim()));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void showRoute(Context context, String model, double slat, double slng, String sName, String sCity,
									double dLat, double dLng, String dName, String dCity) {
		Logger.d(TAG, "showRoute model :　" + model + " sName :　" + sName + " dName : " + dName);
		// dev 起终点是否偏移（0：lat 和lon 是已经加密后的，不需要国测加密 1：需要国测加密）
		// m 驾车方式 =0（速度快）=1（费用少） =2（路程短）=3 不走高速 =4（躲避拥堵）=5（不走高速且避免收费）
		// =6（不走高速且躲避拥堵） =7（躲避收费和拥堵） =8（不走高速躲避收费和拥堵）。
		// 公交 =0（速度快）=1（费用少） =2（换乘较少）=3（步行少）=4（舒适）=5（不乘地铁）
		// t = 1(公交) =2（驾车） =4(步行)
		// showType 显示方式 show=0 (列表) =1(地图)
		int m = 2;
		String data = "androidamap://route?sourceApplication=vui_car_assistant&" + "slat=" + slat + "&slon=" + slng
						+ "&sname=" + sName + "&dlat=" + dLat + "&dlon=" + dLng + "&dname=" + dName + "&"
						+ "dev=0&m=2&t=" + m + "&showType=1";
		Log.d(TAG, "showRoute data :" + data);
		Intent intent = new Intent(act);
		intent.addCategory(cat);
		intent.setPackage(pkg);
		intent.setData(Uri.parse(data.trim()));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 
	 * @Description : startNavi
	 * @Author : zzj
	 * @CreateDate : 2015-1-16
	 * @param context
	 * @param toLat:目的地纬度
	 * @param toLng:目的地经度
	 * @param name:POI名称
	 * @param style:导航方式(0 速度快; 1 费用少; 2 路程短; 3 不走高速；4 躲避拥堵；5 不走高速且避免收费；6
	 * 不走高速且躲避拥堵；7 躲避收费和拥堵；8 不走高速躲避收费和拥堵))
	 * @param dev:是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
	 */
	public static void startNavi(Context context, double toLat, double toLng, String name, int style, int dev) {
		Logger.d(TAG, "startNavi:toLat " + toLat + ",toLng " + toLng + ",name " + name + ",style " + style + ",dev " + dev);
		
		AMapUri uri = new AMapUri("navi");
		uri.addParam("sourceApplication", "uniCarSolution");
		uri.addParam("poiname", name);
		uri.addParam("lat", toLat);
		uri.addParam("lon", toLng);
		uri.addParam("dev", dev);
		uri.addParam("style", style);

		String dat = uri.getDatString();

		Intent intent = new Intent();
		intent.addCategory(cat);
		intent.setPackage(pkg);
		intent.setData(Uri.parse(dat));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void openAMap(Context context) {
		/*String data = "androidamap://route?sourceApplication=uniCarSolution";
		Intent intent = new Intent(act);
		intent.addCategory(cat);
		intent.setPackage(pkg);
		intent.setData(Uri.parse(data.trim()));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);*/
		try {
			PackageManager packageManager = context.getPackageManager();
			Intent intent = packageManager.getLaunchIntentForPackage(pkg);
			context.startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(context, "未找到程序", Toast.LENGTH_SHORT).show();
		}
	}
}
