package com.colink.zzj.txzassistant.vendor.BDDH;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.oem.RomSystemSetting;
import com.colink.zzj.txzassistant.util.APPUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * @desc 这是百度导航的Operate
 * @auth zzj
 * @date 2016-03-17
 */

public class BDDHOperate {

	private Context context;
	private static BDDHOperate mInstance;

	public BDDHOperate(Context context) {
		this.context = context;
	}

	public static synchronized BDDHOperate getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new BDDHOperate(context);
		}
		return mInstance;
	}

	/**
	 * 开始导航
	 */
	public void startNavigation(double lat, double lon) {
		Uri uri = Uri.parse("bdnavi://plan?&dest=" + lat + "," + lon);
		Intent intent = new Intent("com.baidu.navi.action.START", uri);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (RomSystemSetting.isInstalled(context, "com.baidu.navi")) {
			context.startActivity(intent);
		} else {
			Toast.makeText(context, "请先安装百度导航", Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * 当前位置
	 */
	public void locate() {

		Uri uri = Uri.parse("bdnavi://where");
		Intent i = new Intent("com.baidu.navi.action.START", uri);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (RomSystemSetting.isInstalled(context, "com.baidu.navi")) {
			context.startActivity(i);

		} else {
			Toast.makeText(context, "请先安装百度导航", Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * 退出百度导航
	 */
	public void closeMap() {
		context.sendBroadcast(new Intent("com.baidu.navi.quitnavi"));

		AdapterApplication.runOnUiGround(new Runnable() {

			@Override
			public void run() {
				try {
					RomSystemSetting.forceStopPackage(APPUtil.BD_NAVI_PKG);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, 1000);

	}

	public void openMap() {
		Uri uri = Uri.parse("bdnavi://launch");
		Intent i = new Intent("com.baidu.navi.action.START", uri);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

}
