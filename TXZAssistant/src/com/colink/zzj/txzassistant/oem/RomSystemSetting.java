package com.colink.zzj.txzassistant.oem;

import java.lang.reflect.Method;
import java.util.List;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.AssistantService;
import com.colink.zzj.txzassistant.util.APPUtil;
import com.colink.zzj.txzassistant.util.Logger;
import com.colink.zzj.txzassistant.util.SystemPropertiesProxy;

import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * @Module : 隶属模块名
 * @Comments : 实现基本功能
 * @Author : zzj
 * @CreateDate : 2016-03-17
 */
public class RomSystemSetting {
	private static final String ACTION_BLUETOOTH_CALLLOG = "android.intent.action.BLUETOOTH_CALLLOG";

	private static final String MATCH_BLUETOOTH = "android.intent.action.MATCH_BLUETOOTH";
	private static final String CLOSE_BLUETOOTH = "android.intent.action.CLOSE_BLUETOOTH";
	private static final String ACTION_REDIAL = "com.colink.service.TelphoneService.TelephoneReDialReceive";
	private static final String ACTION_HANGUP = "com.colink.service.TelphoneService.TelephoneHandupReceive";
	private static final String ACTION_ANSWER = "com.colink.service.TelphoneService.TelephoneAnswerReceive";
	private static final String ACTION_DIAL = "com.colink.service.TelphoneService.TelephoneReceive";
	
	public static final int TIPS_MUSIC_LOWER = 1;// 音量降低
	public static final int TIPS_MUSIC_RAISE = 2;// 音量增大
	public static final int TIPS_MUSIC_SETMAX = 3;// 音量最大
	public static final int TIPS_MUSIC_SETMIN = 4;// 音量最小
	private static final String TIPS_MUSIC_MAX = "音量已经调到最大了";
	private static final String TIPS_MUSIC_MIN = "音量已经调到最小了";
	private static final String TIPS_MUSIC_VOLUME = "音量已经调到";
	
	private static final String ONE_NAVI = "ONE_NAVI";

	private static void startActivityFromService(Context context, Intent intent) {
		try {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void openDisplaySettings(Context context) {
		startActivityFromService(context, new Intent(
				Settings.ACTION_DISPLAY_SETTINGS));
	}

	public static void openTimeSettings(Context context) {
		startActivityFromService(context, new Intent(
				Settings.ACTION_DATE_SETTINGS));
	}

	public static void openSoundSettings(Context context) {
		startActivityFromService(context, new Intent(
				Settings.ACTION_SOUND_SETTINGS));
	}

	public static void openWallPaperSettings(Context context) {
		startActivityFromService(context, new Intent(
				WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
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

	@SuppressWarnings("deprecation")
	public static void setFlightModeEnabled(Context context, boolean enabled) {
		Settings.System.putInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, enabled ? 1 : 0);
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", enabled);
		context.sendBroadcast(intent);
	}

	public static void setAutoOrientationEnabled(Context context,
			boolean enabled) {
		/*Settings.System.putInt(context.getContentResolver(),
				Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);*/
	}

	@SuppressWarnings("deprecation")
	public static void setRingerMode(Context context, boolean silent,
			boolean vibrate) {
		AudioManager mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (silent) {
			mAudioManager
					.setRingerMode(vibrate ? AudioManager.RINGER_MODE_VIBRATE
							: AudioManager.RINGER_MODE_SILENT);
		} else {
			mAudioManager.setRingerMode(vibrate ? AudioManager.RINGER_MODE_VIBRATE
							: AudioManager.RINGER_MODE_NORMAL);
			mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
					vibrate ? AudioManager.VIBRATE_SETTING_ON
							: AudioManager.VIBRATE_SETTING_OFF);
		}
	}

	public static void openUrl(Context context, String url) {
		try {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri contentUri = Uri.parse(url);
			intent.setData(contentUri);
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
	
	public static String playSoundTips(Context context,final int type) {
		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
		int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM); // 系统当前音量/3
		Logger.d("currentVolume="+currentVolume);
		String platform = SystemPropertiesProxy.get(context, AssistantService.KEY_PLATFORM);
		switch (type) {
		case TIPS_MUSIC_RAISE:// 增大音量
			if (TextUtils.isEmpty(platform)) {
				if (currentVolume >= 7) {
					currentVolume = 7;
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume * 2, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
					return TIPS_MUSIC_MAX;
				} else {
					currentVolume++;
					// PreferenceHelper.getInstance().setVolume(currentVolume * 3);
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							currentVolume * 2, 0);
					mAudioManager.setStreamVolume(
							AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
							currentVolume, 0);
					return TIPS_MUSIC_VOLUME + currentVolume;
				}
			} else {
				currentVolume = currentVolume/3;
				if (currentVolume > 4) {
					currentVolume = 5;
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,15, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 15, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,15, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_RING,15, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,15, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,15, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,6, 0);
					return TIPS_MUSIC_MAX;
				} else {
					currentVolume++;
				 // PreferenceHelper.getInstance().setVolume(currentVolume * 3);
					int pre_volume = currentVolume * 3;
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,pre_volume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, pre_volume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,pre_volume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_RING,pre_volume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,pre_volume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,pre_volume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,pre_volume/2, 0);
					return TIPS_MUSIC_VOLUME + currentVolume;
				}
			}
		case TIPS_MUSIC_LOWER:// 降低音量
			if (TextUtils.isEmpty(platform)) {
				if (currentVolume <= 0) {
					currentVolume = 0;
					// 静音了播报无作用
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume * 2, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
					return TIPS_MUSIC_MIN;
				} else {
					--currentVolume;
					// PreferenceHelper.getInstance().setVolume(currentVolume *
					// 3);
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume * 2, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
					return  TIPS_MUSIC_VOLUME + currentVolume;

				}
			} else {
				currentVolume = currentVolume/3;
				if (currentVolume <= 0) {
					currentVolume = 0;
					// 静音了播报无作用
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume , 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume , 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume , 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume , 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currentVolume , 0);
					return TIPS_MUSIC_MIN;
				} else {
					--currentVolume;
					// PreferenceHelper.getInstance().setVolume(currentVolume *
					// 3);
					int pre_volume = currentVolume * 3;
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,pre_volume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, pre_volume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,pre_volume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_RING,pre_volume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,pre_volume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,pre_volume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,pre_volume/2, 0);
					return TIPS_MUSIC_VOLUME + currentVolume;

				}
			}
		case TIPS_MUSIC_SETMAX:// 音量最大
			if (TextUtils.isEmpty(platform)) {
				currentVolume = 7;
				// PreferenceHelper.getInstance().setVolume(currentVolume * 3);
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						currentVolume * 2, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
						currentVolume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
						currentVolume, 0);
			} else {
				currentVolume = 15;
				// PreferenceHelper.getInstance().setVolume(currentVolume * 3);
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,currentVolume , 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume , 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,6, 0);
			}
			return  TIPS_MUSIC_MAX;
		case TIPS_MUSIC_SETMIN:// 音量最小
			currentVolume = 0;
			// PreferenceHelper.getInstance().setVolume(currentVolume * 3);
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume , 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume , 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume , 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume , 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,currentVolume , 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume , 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currentVolume , 0);
			return TIPS_MUSIC_MIN;
		default:
			break;
		}
		return null;

	}

	// 音量加减----vain
	public static int RaiseOrLowerVolume(Context context, boolean isAdd,int volumeValue) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		String platform = SystemPropertiesProxy.get(context, AssistantService.KEY_PLATFORM);
		if (isAdd) {
			for (int i = 0; i < volumeValue; i++) {// FLAG_REMOVE_SOUND_AND_VIBRATE
				if(am.getStreamVolume(AudioManager.STREAM_ALARM) > 14){
					return 15;
				}
				if(TextUtils.isEmpty(platform)){
					am.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				}
				am.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				am.adjustStreamVolume(AudioManager.STREAM_ALARM,AudioManager.ADJUST_RAISE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);//FLAG_SHOW_UI    FX_FOCUS_NAVIGATION_UP
				am.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION,AudioManager.ADJUST_RAISE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);//FLAG_SHOW_UI    FX_FOCUS_NAVIGATION_UP
			}
		} else {
			for (int i = 0; i < volumeValue; i++) {
				if(TextUtils.isEmpty(platform)){
					am.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				}
				am.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				am.adjustStreamVolume(AudioManager.STREAM_ALARM,AudioManager.ADJUST_LOWER,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				am.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION,AudioManager.ADJUST_LOWER,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			}
		}
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_ALARM);
	}

	// 最大音量--vain
	public static int setMaxVolume(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		String platform = SystemPropertiesProxy.get(context, AssistantService.KEY_PLATFORM);
		if(TextUtils.isEmpty(platform)){
			am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
			am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
			am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
		}else{
			am.setStreamVolume(AudioManager.STREAM_MUSIC,15, 0);
			am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 15, 0);
			am.setStreamVolume(AudioManager.STREAM_ALARM,15, 0);
			am.setStreamVolume(AudioManager.STREAM_RING,15, 0);
			am.setStreamVolume(AudioManager.STREAM_DTMF,15, 0);
			am.setStreamVolume(AudioManager.STREAM_SYSTEM,15, 0);
			am.setStreamVolume(AudioManager.STREAM_VOICE_CALL,6, 0);
		}
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_ALARM);
	}

	// 最小音量--vain
	public static int setMinVolume(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC,0, 0);
		am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
		am.setStreamVolume(AudioManager.STREAM_ALARM,0, 0);
		am.setStreamVolume(AudioManager.STREAM_RING,0, 0);
		am.setStreamVolume(AudioManager.STREAM_DTMF,0, 0);
		am.setStreamVolume(AudioManager.STREAM_SYSTEM,0, 0);
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_ALARM);
	}

	// 设置到某个音量值--vain
	public static int setVolume(Context context, int volumeValue) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		String platform = SystemPropertiesProxy.get(context, AssistantService.KEY_PLATFORM);
		if(TextUtils.isEmpty(platform)){
			if(volumeValue > 7){
				volumeValue = 7;
			}
			am.setStreamVolume(AudioManager.STREAM_MUSIC,volumeValue * 2, 0);
			am.setStreamVolume(AudioManager.STREAM_ALARM,volumeValue, 0);
			am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volumeValue, 0);
		}else{
			if(volumeValue > 15){
				volumeValue = 15;
			}
			am.setStreamVolume(AudioManager.STREAM_MUSIC,volumeValue, 0);
			am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volumeValue, 0);
			am.setStreamVolume(AudioManager.STREAM_ALARM,volumeValue, 0);
			am.setStreamVolume(AudioManager.STREAM_RING,volumeValue, 0);
			am.setStreamVolume(AudioManager.STREAM_DTMF,volumeValue, 0);
			am.setStreamVolume(AudioManager.STREAM_SYSTEM,volumeValue, 0);
		}
		
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_ALARM);
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
		ActivityManager am = (ActivityManager) AdapterApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
		method.invoke(am, pkgName);
	}

}
