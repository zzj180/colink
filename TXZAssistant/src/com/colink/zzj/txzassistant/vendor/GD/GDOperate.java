package com.colink.zzj.txzassistant.vendor.GD;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.oem.RomSystemSetting;
import com.colink.zzj.txzassistant.util.APPUtil;

/**
 * @desc 高德接口类
 * @auth zzj
 * @date 2016-03-17
 */
public class GDOperate {

	private Context context;
	private static GDOperate mInstance;

	public GDOperate(Context context) {
		this.context = context;
	}

	public static synchronized GDOperate getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new GDOperate(context);
		}
		return mInstance;
	}

	public void startNavigation(double lat, double lon) {
		// this.checkGDReNavigation();//删除poi遗留
		if (APPUtil.getInstance().isInstalled(APPUtil.GD_CAR_PKG)) {
			String cat = "android.intent.category.DEFAULT";
			String dat = "androidauto://navi?sourceApplication=TXZAssistant&lat="
					+ lat + "&lon=" + lon + "&dev=0&style=2";
			Intent i = new Intent();
			i.setData(android.net.Uri.parse(dat));
			i.setPackage(APPUtil.GD_CAR_PKG);
			i.addCategory(cat);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			context.startActivity(i);
		} else if (APPUtil.getInstance().isInstalled(APPUtil.GD_MAP_PKG)) {
			String cat = "android.intent.category.DEFAULT";
			String dat = "androidamap://navi?sourceApplication=TXZAssistant&lat="
					+ lat + "&lon=" + lon + "&dev=0&style=2";
			Intent i = new Intent();
			i.setData(android.net.Uri.parse(dat));
			i.setPackage(APPUtil.GD_MAP_PKG);
			i.addCategory(cat);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			context.startActivity(i);
		} else {
			Toast.makeText(context, "请先安装高德地图", Toast.LENGTH_LONG).show();
		}
	}

	public void closeMap() {// 强制结束掉高德APP，需要root权限
		if (APPUtil.getInstance().isInstalled(APPUtil.GD_CAR_PKG)) {
			closeCarMap();
		}
		if (APPUtil.getInstance().isInstalled(APPUtil.GD_MAP_PKG)) {
			closeGDMap();
		}

	}
	
	public void closeMapBy() {// 强制结束掉高德APP，需要root权限
		if (APPUtil.getInstance().isInstalled(APPUtil.GD_CAR_PKG)) {
			AdapterApplication.runOnUiGround(new Runnable() {

				@Override
				public void run() {
					try {
						RomSystemSetting.forceStopPackage(APPUtil.GD_MAP_PKG);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 1000);
		}
		if (APPUtil.getInstance().isInstalled(APPUtil.GD_MAP_PKG)) {
			closeGDMap();
		}

	}

	/**
	 * 退出高德
	 */
	private void closeGDMap() {// 强制结束掉高德APP，需要root权限

		context.sendBroadcast(new Intent("com.amap.stopnavi"));
		Intent mIntent = new Intent("com.autonavi.minimap.carmode.command");
		mIntent.putExtra("NAVI", "APP_EXIT");
		context.sendBroadcast(mIntent);

		AdapterApplication.runOnUiGround(new Runnable() {

			@Override
			public void run() {
				try {
					RomSystemSetting.forceStopPackage(APPUtil.GD_MAP_PKG);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1000);

	}

	/**
	 * 当前位置，并在地图定位
	 */
	public void locate() {
		if (APPUtil.getInstance().isInstalled(APPUtil.GD_CAR_PKG)) {
			String act = "android.intent.action.VIEW";
			String cat = "android.intent.category.DEFAULT";
			String data = "androidauto://myLocation?sourceApplication=TXZAssistant";
			Intent i = new Intent();
			i.setAction(act);
			i.addCategory(cat);
			i.setData(android.net.Uri.parse(data));
			i.setPackage(APPUtil.GD_CAR_PKG);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			context.startActivity(i);
		} else if (APPUtil.getInstance().isInstalled(APPUtil.GD_MAP_PKG)) {
			String act = "android.intent.action.VIEW";
			String cat = "android.intent.category.DEFAULT";
			String data = "androidamap://myLocation?sourceApplication=TXZAssistant";
			Intent i = new Intent();
			i.setAction(act);
			i.addCategory(cat);
			i.setData(android.net.Uri.parse(data));
			i.setPackage(APPUtil.GD_MAP_PKG);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			context.startActivity(i);
		} else {
			Toast.makeText(context, "请先安装高德地图", Toast.LENGTH_LONG).show();
		}

	}

	public void closeCarMap() {// 强制结束掉高德APP，需要root权限
		Intent intent = new Intent("android.intent.action.VIEW",
				android.net.Uri.parse("androidauto://naviExit?sourceApplication=TXZAssistant"));
		intent.setPackage("com.autonavi.amapauto");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(intent);
		AdapterApplication.runOnUiGround(new Runnable() {
			@Override
			public void run() {
				try {
					RomSystemSetting.forceStopPackage(APPUtil.GD_CAR_PKG);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1500);

	}

}
