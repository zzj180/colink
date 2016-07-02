package com.zzj.softwareservice;

import com.android.fm.radio.FMRadioService;
import com.zzj.softwareservice.bd.BNRService;
import com.zzj.softwareservice.util.APPUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

public class SoftwareManager extends Service {

	private final static String APP_MANAGER = "com.zzj.software.APP_MANAGER";
	private final static String ONE_NAVI_APP = "ONE_NAVI_OPEN";
	private final static String NAVI_APP = "NAVI_OPEN";
	private final static String VOICE_APP = "VOICE_START";
	private final static String MUSIC_APP = "MUSIC_OPEN";
	private final int BD_VALUE = 0;
	private final int GD_VALUE = 1;
	private final int KLD_VALUE = 2;
	private final int MX_VALUE = 3;

	private String app_name;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		startService(new Intent(this, BNRService.class));
		startService(new Intent(this, FMRadioService.class));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		if (APP_MANAGER.equals(action)) {
			app_name = intent.getStringExtra("control");
			if (ONE_NAVI_APP.equals(app_name)) {
				openONENavi();
			} else if (NAVI_APP.equals(app_name)) {
				openMap();
			} else if (VOICE_APP.equals(app_name)) {

			} else if (MUSIC_APP.equals(app_name)) {
				openMusic();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 一键导航
	 */
	public void openONENavi() {
		int navi = Settings.System.getInt(getContentResolver(),
				APPUtil.ONE_NAVI, 0);
		boolean enable = false;
		switch (navi) {
		case 1:
			if (APPUtil.isInstalled(this, APPUtil.ECAR_2)) {
				enable = APPUtil.openApplication(this, APPUtil.ECAR_2);
			} else {
				enable = APPUtil.openApplication(this, APPUtil.ECAR_1);
			}
			break;
		default:
			boolean support = APPUtil.supportBT(this);
			if (support && APPUtil.isInstalled(this, APPUtil.TIANAN_APP)) {
				enable = APPUtil.openApplication(this, APPUtil.TIANAN_APP);
			} else {
				enable = APPUtil.openApplication(this, APPUtil.DDBOX_APP);
			}
			break;
		}
		if (!enable) {
			Toast.makeText(this, "未找到指定的一键导航", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 简单的打开地图操作
	 */
	public void openMap() {
		int mapType = Settings.System.getInt(getContentResolver(),
				APPUtil.MAP_INDEX, 0);
		boolean enable = true;
		switch (mapType) {
		case GD_VALUE:
			if (APPUtil.isInstalled(this, APPUtil.GDMAPFORCAT_APP)) {
				enable = APPUtil.openApplication(this, APPUtil.GDMAPFORCAT_APP);
			} else {
				enable = APPUtil.openApplication(this, APPUtil.GDMAP_APP);
			}
			break;
		case BD_VALUE:
			enable = APPUtil.openApplication(this, APPUtil.BDDH_APP);
			break;
		case KLD_VALUE:
			Intent i = new Intent("android.NaviOne.AutoStartReceiver");
			i.putExtra("CMD", "Start");
			sendBroadcast(i);
			break;
		case MX_VALUE:
			enable = APPUtil.openApplication(this, APPUtil.MXMAP_APP);
			break;
		default:
			break;
		}
		if (!enable) {
			Toast.makeText(this, "未找到指定的导航", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void openMusic() {
		boolean enable = false;
		if (APPUtil.isInstalled(this, APPUtil.KUWO_MUSIC)) {
			enable = APPUtil.openApplication(this, APPUtil.KUWO_MUSIC);
		} 
		if (!enable) {
			Toast.makeText(this, "未找到指定的酷我音乐", Toast.LENGTH_SHORT).show();
		}
	}

}
