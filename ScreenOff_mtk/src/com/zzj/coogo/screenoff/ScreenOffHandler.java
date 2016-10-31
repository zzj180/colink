package com.zzj.coogo.screenoff;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;

public class ScreenOffHandler extends Handler {
	private static final String TTS_SHOW = "tts_show";
	private static final String SCREEN_KEEP_ON = "screen_keep_on";
	
	public static final int WHAT_NO_DISTURB = 1;
	public static final int WHAT_GO_SLEEP = 3;

	public ScreenOffHandler(Looper looper) {
		super(looper);
	}

	/**
	 * Handle incoming transaction requests. The incoming requests are initiated
	 * by the MMSC Server or by the MMS Client itself.
	 */
	@Override
	public void handleMessage(Message msg) {
		//导航，屏幕常亮 并且在导航界面或者语音界面不进入免打扰
		if (msg.what == WHAT_NO_DISTURB) {
			if (ScrrenoffActivity.screen == null && !MainApplication.mScreenOff
					&& MainApplication.autoScreenOff && MainApplication.acc_state && !MainApplication.isBackCar) {
				
				boolean keep_screen_on = Settings.System.getInt(MainApplication.app.getContentResolver(), SCREEN_KEEP_ON, 1) == 1;
				boolean tts_show = Settings.System.getInt(MainApplication.app.getContentResolver(), TTS_SHOW, 0) == 1;
				String name = MainApplication.app.topActivity();
				if((isNavi() && keep_screen_on && (Constant.BAIDU_NAVI_ACTIVITY.equals(name) 
						|| Constant.GAODE_MAP_ACTIVITY.equals(name) || Constant.GAODE_CAR_ACTIVITY.equals(name))) || tts_show){
						MainApplication.app.goNoDiturb();
					return;
				}else{
					MainApplication.app.sendBroadcast(new Intent(Constant.NO_DISTURB_ACTION));
					Intent intent2 = new Intent(MainApplication.app,ScrrenoffActivity.class);
					intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					boolean isCamera = Constant.CAMERA_ACTIVITY.equals(name);
					intent2.putExtra("isCamera", isCamera);
					MainApplication.app.startActivity(intent2);
				}
			}
		} else if (msg.what == WHAT_GO_SLEEP) {
			if (!MainApplication.autoScreenOff && !MainApplication.isBackCar){
				boolean keep_screen_on = Settings.System.getInt(MainApplication.app.getContentResolver(), SCREEN_KEEP_ON, 1) == 1;
				boolean tts_show = Settings.System.getInt(MainApplication.app.getContentResolver(), TTS_SHOW, 0) == 1;
				String name = MainApplication.app.topActivity();
				if((isNavi() && keep_screen_on && (Constant.BAIDU_NAVI_ACTIVITY.equals(name)
						|| Constant.GAODE_MAP_ACTIVITY.equals(name) || Constant.GAODE_CAR_ACTIVITY.equals(name))) || tts_show){
					MainApplication.app.goNoDiturb();
					return;
				}else{
					((MainApplication) MainApplication.app).goToSleep();
				}
			}
		}
	}
	
	private static final Uri uri_navi = Uri.parse("content://com.zzj.softwareservice.NaviProvider/navi");
	private static final String ISNAVING = "is_naving";
	private Cursor query;
	private boolean isNavi() {
		boolean isNaviing = false;
		try {
			query = MainApplication.app.getContentResolver().query(uri_navi, null, null, null, null);
			if (query.moveToNext()) {
				isNaviing = query.getInt(query.getColumnIndex(ISNAVING)) == 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (query != null)
				query.close();
			return isNaviing;
		}
	}
}
