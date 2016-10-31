package com.zzj.coogo.screenoff;

import java.lang.reflect.Method;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class ScreenOffService extends Service{

	private static final String FORCE_STOP_PACKAGE = "forceStopPackage";
	private static final String ACTIVITY_MANAGER = "android.app.ActivityManager";
	private static final String BACK_CAR_STATE = "back_car_state";
	private static final String ACC_STATE = "acc_state";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		getContentResolver().registerContentObserver(
				Settings.System.getUriFor(ACC_STATE), true,
				new ContentObserver(MainApplication.getHander()) {
					@Override
					public void onChange(boolean selfChange) {
						MainApplication.acc_state = Settings.System.getInt(
								getContentResolver(), ACC_STATE, 1) == 1;
						if (MainApplication.acc_state) {
							String device = SystemPropertiesProxy.get(MainApplication.app, "ro.product.device");
							if(!device.contains("8665")){
								startService(new Intent(getApplicationContext(),SwitchServeice.class));
							}
							MainApplication.app.goNoDiturb();
						} else {
							MainApplication.getHander().removeMessages(1);

							if (ScrrenoffActivity.screen != null) {
								ScrrenoffActivity.screen.finish();
							}
							closeOneNaviApp();
							closeNaviApp();
							closeMediaApp();
							String device = SystemPropertiesProxy.get(MainApplication.app, "ro.product.device");
							if(!device.contains("8665")){
								stopService(new Intent(getApplicationContext(),SwitchServeice.class));
							}
							stopService(new Intent(getApplicationContext(),ScreenOffService.class));
							
							android.os.Process.killProcess(android.os.Process.myPid());
						}
					}
				});

		isBackCar();
		getContentResolver().registerContentObserver(
				Settings.System.getUriFor(BACK_CAR_STATE), true,
				new ContentObserver(new Handler(getMainLooper())) {
					@Override
					public void onChange(boolean selfChange) {
						isBackCar();
					}

				});

		getContentResolver().registerContentObserver(
				Settings.System.getUriFor(MainApplication.NO_DIDTURB), true,
				new ContentObserver(new Handler(getMainLooper())) {
					@Override
					public void onChange(boolean selfChange) {
						MainApplication.app.goNoDiturb();
					}
				});

		getContentResolver().registerContentObserver(
				Settings.System.getUriFor("ONE_NAVI"), true,
				new ContentObserver(new Handler(getMainLooper())) {
					@Override
					public void onChange(boolean selfChange) {
						closeOneNaviApp();
					}
				});

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent!=null){
			boolean boot = intent.getBooleanExtra("bootreceive", false);
			Log.d("zzj", "boot="+boot);
			if(boot){
				MainApplication.getHander().postDelayed(new Runnable() {
					@Override
					public void run() {
						closeNaviApp();
						closeOneNaviApp();
						closeMediaApp();
						try {
							forceStopPackage(Constant.WEB_CHAT_PKG,getApplicationContext());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 8000);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	
	/**
	 * 关闭一键导航应用
	 */
	private void closeOneNaviApp(){
		try {
			forceStopPackage(Constant.DDBOX_PKG, getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			forceStopPackage(Constant.AUTONAVI_PKG, getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			forceStopPackage(Constant.TIANAN_PKG, getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			forceStopPackage(Constant.VOIP_PKG, getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			forceStopPackage(Constant.ECAR_PKG, getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭导航应用
	 */
	private void closeNaviApp() {
		try {
			forceStopPackage(Constant.BAIDU_NAVI,getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			forceStopPackage(Constant.GAODE_MAP,getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*try {
			forceStopPackage(Constant.GD_CARJ_PKG,getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		Intent intent = new Intent("AUTONAVI_STANDARD_BROADCAST_RECV");
		intent.putExtra("KEY_TYPE",10018);
		sendBroadcast(intent);
		try {
			forceStopPackage(Constant.GD_CAR_PKG,getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			forceStopPackage(Constant.BAIDU_MAP,getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭媒体应用
	 */
	private void closeMediaApp() {
		try {
			forceStopPackage(Constant.KUWO_MUSIC,getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			forceStopPackage(Constant.IMUSIC_PKG,getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			forceStopPackage(Constant.XMLY_FM,getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			forceStopPackage(Constant.MOVIE_PKG,getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			forceStopPackage(Constant.MOVIE1_PKG,getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 后拉状态
	 */
	private void isBackCar() {
		MainApplication.isBackCar = Settings.System.getInt(getContentResolver(),
				BACK_CAR_STATE, 0) == 1;
		if (MainApplication.isBackCar) {
			MainApplication.getHander().removeMessages(1);
			if (ScrrenoffActivity.screen != null) {
				ScrrenoffActivity.screen.finish();
			}
		}
	}

	private void forceStopPackage(String pkgName, Context context)
			throws Exception {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		Method method = Class.forName(ACTIVITY_MANAGER).getMethod(
				FORCE_STOP_PACKAGE, String.class);
		method.invoke(am, pkgName);
	}
}
