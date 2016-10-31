package com.aispeech.aios.bridge.oem;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import com.aispeech.aios.bridge.BridgeApplication;
import com.aispeech.aios.bridge.utils.APPUtil;

/**
 * @Module : 隶属模块名
 * @Comments : 实现基本功能
 * @Author : zzj
 * @CreateDate : 2016-03-17
 */
public class RomSystemSetting {
	private static final String ACTION_BLUETOOTH_CALLLOG = "android.intent.action.BLUETOOTH_CALLLOG";

	private static final String ACTION_REDIAL = "com.colink.service.TelphoneService.TelephoneReDialReceive";
	private static final String ACTION_HANGUP = "com.colink.service.TelphoneService.TelephoneHandupReceive";
	private static final String ACTION_ANSWER = "com.colink.service.TelphoneService.TelephoneAnswerReceive";
	private static final String ACTION_DIAL = "com.colink.service.TelphoneService.TelephoneReceive";

	private static final String MATCH_BLUETOOTH = "android.intent.action.MATCH_BLUETOOTH";
	private static final String CLOSE_BLUETOOTH = "android.intent.action.CLOSE_BLUETOOTH";
	private static final String ONE_NAVI = "ONE_NAVI";

	private static void startActivityFromService(Context context, Intent intent) {
		try {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void openCallLog(Context context) {
		Intent carkit = new Intent();
		carkit.setAction(ACTION_BLUETOOTH_CALLLOG);
		context.sendBroadcast(carkit);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(android.provider.CallLog.Calls.CONTENT_URI);
		// intent.putExtra("android.provider.extra.CALL_TYPE_FILTER",
		// CallLog.Calls.MISSED_TYPE);
		startActivityFromService(context, intent);
	}
	
	// 打电话
	public static void RomCustomDialNumber(Context context, String number) {
		Intent intent = new Intent();
		intent.setAction(ACTION_DIAL);
		intent.putExtra("number", number);
		context.sendBroadcast(intent);
	}

	// 接听电话
	public static void RomCustomAnswerCall(Context context) {
		Intent intent = new Intent();
		intent.setAction(ACTION_ANSWER);
		context.sendBroadcast(intent);
	}

	// 挂断电话
	public static void RomCustomHANGUP(Context context) {
		Intent intent = new Intent();
		intent.setAction(ACTION_HANGUP);
		context.sendBroadcast(intent);
	}

	// 重新拨号
	public static void RomCustomReDial(Context context) {
		Intent intent = new Intent();
		intent.setAction(ACTION_REDIAL);
		context.sendBroadcast(intent);
	}
	
	
/*	public static void setMute(Context context){
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
	}
	
	public static void setUnMute(Context context){
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		//隐藏音乐进度条
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}*/
	
	  /**
     * 打开FM
     */
    public static void openFM(Context context) {
    	Settings.System.putInt(context.getContentResolver(), "fm_switch", 0);
    }

    /**
     * 关闭FM
     */
    public static void closeFM(Context context) {
    	Settings.System.putInt(context.getContentResolver(), "fm_switch", 1);
    	context.sendBroadcast(new Intent("action.colink.stopFM"));
    }
    
    /**
     * 发射FM
     */
    public static void launchFM(int fm,Context context) {
    	Settings.System.putInt(context.getContentResolver(), "fm_freg", fm);
    	Settings.System.putInt(context.getContentResolver(), "fm_switch", 0);
    	
    	Intent intent=new Intent("action.colink.startFM");
		intent.putExtra("fm_fq",  (fm*1.0f/1000));
		context.sendBroadcast(intent);
    }

    /**
     * 打开电子狗
     */
    public static void openEDog(Context context) {
    	Intent intent = new Intent();
		intent.setAction("com.wanma.action.EDOG_ON");
		context.sendBroadcast(intent);
    }


	public static String openDriveRecorder(Context context,String label) {
		PackageManager pm = context.getPackageManager(); //获得PackageManager对象
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> resolveInfos = pm
				.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
		// 调用系统排序 ， 根据name排序
		// 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
		Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));
			for (ResolveInfo reInfo : resolveInfos) {
				String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
				// 为应用程序的启动Activity 准备Intent
				if(label.equals(appLabel)) {
					String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
					Intent intent = pm.getLaunchIntentForPackage(pkgName);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					return  "行车记录仪已打开";
				}
				// 创建一个AppInfo对象，并赋值
			}
		return "暂不支持此功能";
	}
    /**
     * 关闭电子狗
     */
    public static void closeEDog(Context context) {
    	Intent intent = new Intent();
		intent.setAction("com.wanma.action.EDOG_OFF");
		context.sendBroadcast(intent);
    }
    
    /**
     * 打开雷达
     */
    public static void openRADAR(Context context) {
    	Intent intent = new Intent();
		intent.setAction("com.wanma.action.RADAR_ON");
		context.sendBroadcast(intent);
    }

    /**
     * 关闭雷达
     */
    public static void closeRADAR(Context context) {
    	Intent intent = new Intent();
		intent.setAction("com.wanma.action.RADAR_OFF");
		context.sendBroadcast(intent);
    }
    
    /**
     * 一键导航
     */
    public static void openONENavi(Context context) {
    	int navi = Settings.System.getInt(context.getContentResolver(), ONE_NAVI, 0);
		switch (navi) {
		case 1:
			if (APPUtil.getInstance().isInstalled("com.coagent.ecar")) {
				Intent tmpIntent = new Intent("com.android.ecar.recv");
				tmpIntent.putExtra("ecarSendKey", "MakeCall");
				tmpIntent.putExtra("cmdType", "standCMD");
				tmpIntent.putExtra("keySet", "");
				context.sendBroadcast(tmpIntent);
			}else{
				ComponentName componetName = new ComponentName("com.coagent.app","com.coagent.activity.MainActivity");
				Intent ecar = new Intent();
				ecar.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ecar.setComponent(componetName);
				
				try {
					context.startActivity(ecar);
				} catch (Exception e) {
				}
			}
			break;
		default:
			
			int enable = 1;
			try {
				Cursor query = context.getContentResolver().query(Uri.parse("content://com.colink.bluetoothe/bluetootheonline"),null, null, null, null);
				if (query != null) {
					if (query.moveToNext()) {
						enable = query.getInt(query.getColumnIndex("support"));
					}
					query.close();
				}
			} catch (Exception e) {
			}
			if(enable == 1){
				if (APPUtil.getInstance().isInstalled("com.share.android")) {
					Intent intent = new Intent("tianan.cloudcall.action.CALL");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try {
						context.startActivity(intent);
					} catch (Exception e) {
					}
				}else{
					Intent intent = new Intent("com.glsx.bootup.receive.autonavi");
					intent.putExtra("autonaviType", 1); // autonaviType为1：表示直接发起导航请求，
														// autonaviType为2：只进入导航主页面（不发起请求）;
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try {
						context.startActivity(intent);
					} catch (Exception e) {
					}
				}
			}else{
				Intent intent = new Intent("com.glsx.bootup.receive.autonavi");
				intent.putExtra("autonaviType", 1); // autonaviType为1：表示直接发起导航请求，
													// autonaviType为2：只进入导航主页面（不发起请求）;
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					context.startActivity(intent);
				} catch (Exception e) {
				}
			}
			
			break;
		}
    }
    
    /**
     * 免打扰模式
     */
    public static void openNoDisturb(Context context) {
    	Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClassName("com.zzj.coogo.screenoff", "com.zzj.coogo.screenoff.ScrrenoffActivity");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
		}
    }
    
    public static void homePage(Context context){
    	
    	Intent mHomeIntent = new Intent(Intent.ACTION_MAIN, null);
		mHomeIntent.addCategory(Intent.CATEGORY_HOME);
		mHomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		try {
			context.startActivity(mHomeIntent);
		} catch (Exception e) {
		}
		Intent intent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		intent.putExtra("reason", "homekey");
		context.sendBroadcast(intent);
    	
    }

	public static void closeNavi(final Context context){
		if (APPUtil.getInstance().isInstalled(APPUtil.GD_CARJ_PKG)) {
			Intent mIntent = new Intent("AUTONAVI_STANDARD_BROADCAST_RECV");
			mIntent.putExtra("KEY_TYPE",10010);
			context.sendBroadcast(mIntent);

			BridgeApplication.uiHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					APPUtil.forceStopPackage(APPUtil.GD_CARJ_PKG, context);
				}
			}, 1000);
		}
		if (APPUtil.getInstance().isInstalled(APPUtil.GD_CAR_PKG)) {
			Intent intent = new Intent("AUTONAVI_STANDARD_BROADCAST_RECV");
			intent.putExtra("KEY_TYPE", 10021);
			context.sendBroadcast(intent);

			BridgeApplication.uiHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					APPUtil.forceStopPackage(APPUtil.GD_CAR_PKG, context);
				}
			}, 1500);
		}
		if (APPUtil.getInstance().isInstalled(APPUtil.GD_MAP_PKG)) {
			context.sendBroadcast(new Intent("com.amap.stopnavi"));
			Intent mIntent = new Intent("com.autonavi.minimap.carmode.command");
			mIntent.putExtra("NAVI", "APP_EXIT");
			context.sendBroadcast(mIntent);

			BridgeApplication.uiHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					APPUtil.forceStopPackage(APPUtil.GD_MAP_PKG, context);
				}
			}, 1000);
		}
		if(APPUtil.getInstance().isInstalled(APPUtil.BD_NAVI_PKG)){
			context.sendBroadcast(new Intent("com.baidu.navi.quitnavi"));

			BridgeApplication.uiHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					APPUtil.forceStopPackage(APPUtil.BD_NAVI_PKG, context);
				}
			}, 1000);
		}
		if(APPUtil.getInstance().isInstalled( APPUtil.BD_MAP_PKG)){
			APPUtil.forceStopPackage(APPUtil.BD_MAP_PKG, context);
		}
		if(APPUtil.getInstance().isInstalled(APPUtil.KLD_MAP_PKG)){
			Intent i = new Intent("android.NaviOne.AutoExitReceiver");
			context.sendBroadcast(i);

			BridgeApplication.uiHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					APPUtil.forceStopPackage(APPUtil.KLD_MAP_PKG, context);
				}
			}, 1000);
		}
		if(APPUtil.getInstance().isInstalled(APPUtil.MX_MAP_PKG)){
			APPUtil.forceStopPackage(APPUtil.MX_MAP_PKG, context);
		}
		if(APPUtil.getInstance().isInstalled(APPUtil.GG_MAP_PKG)){
			APPUtil.forceStopPackage(APPUtil.GG_MAP_PKG, context);
		}
	}
    
    /**
     * 通过包名检测APP是否安装
     *
     * @param packageName 包名
     * @return true or false
     */
    public static boolean isInstalled(Context context,String packageName) {

        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }

        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * 听过包名检测APP是否运行
     *
     * @param packName
     * @return
     */
    @SuppressWarnings("deprecation")
	public static boolean isRunning(Context context,String packName) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packName) || info.baseActivity.getPackageName().equals(packName)) {
                isAppRunning = true;
                break;
            }
        }
        return isAppRunning;
    }
    
    
    /**
	 * 强制停止应用程序
	 * 
	 * @param pkgName
	 */
	public static  void forceStopPackage(String pkgName)
			throws Exception {
		ActivityManager am = (ActivityManager) BridgeApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
		method.invoke(am, pkgName);
	}

	public static void setBluetoothEnabled(Context context, boolean enabled) {
		if (enabled) {
			Intent intent = new Intent();
			intent.setAction(MATCH_BLUETOOTH);
			// romCustomLog(TAG,mIntent.toString(),flag);
			context.sendBroadcast(intent);
		} else {
			Intent intent = new Intent();
			intent.setAction(CLOSE_BLUETOOTH);
			// romCustomLog(TAG,mIntent.toString(),flag);
			context.sendBroadcast(intent);
		}
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null) {
			if (adapter.isEnabled() != enabled) {
				if (enabled) {
					adapter.enable();
				} else {
					adapter.disable();
				}
			}
		}
	}

}
