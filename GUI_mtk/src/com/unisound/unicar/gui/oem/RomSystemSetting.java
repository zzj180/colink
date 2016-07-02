/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : RomSystemSetting.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.oem
 * @Author : Dancindream
 * @CreateDate : 2013-9-9
 */
package com.unisound.unicar.gui.oem;

import android.app.WallpaperManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-9
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-9
 * @Modified: 2013-9-9: 实现基本功能
 */
public class RomSystemSetting {
	private static final String ACTION_BLUETOOTH_CALLLOG = "android.intent.action.BLUETOOTH_CALLLOG";

	public static final String TAG = "RomSystemSetting";

	private static final String MATCH_BLUETOOTH = "android.intent.action.MATCH_BLUETOOTH";
	private static final String CLOSE_BLUETOOTH = "android.intent.action.CLOSE_BLUETOOTH";
	private static final String ACTION_REDIAL = "com.colink.service.TelphoneService.TelephoneReDialReceive";
	private static final String ACTION_HANGUP = "com.colink.service.TelphoneService.TelephoneHandupReceive";
	private static final String ACTION_ANSWER = "com.colink.service.TelphoneService.TelephoneAnswerReceive";
	private static final String ACTION_DIAL = "com.colink.service.TelphoneService.TelephoneReceive";

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
			mAudioManager
					.setRingerMode(vibrate ? AudioManager.RINGER_MODE_VIBRATE
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

	// 音量加减----vain
	public static int RaiseOrLowerVolume(Context context, boolean isAdd,int volumeValue) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (isAdd) {
			for (int i = 0; i < volumeValue; i++) {// FLAG_REMOVE_SOUND_AND_VIBRATE
				if(am.getStreamVolume(AudioManager.STREAM_ALARM) > 11){
					return 12;
				}
				am.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);// FLAG_SHOW_UI
				am.adjustStreamVolume(AudioManager.STREAM_ALARM,AudioManager.ADJUST_RAISE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);//FLAG_SHOW_UI    FX_FOCUS_NAVIGATION_UP
				am.adjustStreamVolume(AudioManager.STREAM_RING,AudioManager.ADJUST_RAISE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);//FLAG_SHOW_UI    FX_FOCUS_NAVIGATION_UP
			}
		} else {
			for (int i = 0; i < volumeValue; i++) {
				am.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				am.adjustStreamVolume(AudioManager.STREAM_ALARM,AudioManager.ADJUST_LOWER,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				am.adjustStreamVolume(AudioManager.STREAM_RING,AudioManager.ADJUST_LOWER,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			}
		}
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_ALARM);
	}

	// 最大音量--vain
	public static int setMaxVolume(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC,12, 0);
		am.setStreamVolume(AudioManager.STREAM_ALARM, 12, 0);
		am.setStreamVolume(AudioManager.STREAM_RING, 12 , 0);
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_ALARM);
	}

	// 最小音量--vain
	public static int setMinVolume(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		am.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
		am.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_ALARM);
	}

	// 设置到某个音量值--vain
	public static int setVolume(Context context, int volumeValue) {
		if(volumeValue > 12){
			volumeValue = 12;
		}
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC,volumeValue, 0);
		am.setStreamVolume(AudioManager.STREAM_ALARM,volumeValue, 0);
		am.setStreamVolume(AudioManager.STREAM_RING, volumeValue, 0);
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
	
	private static int mStreamVolume = 0;
	
	public static void setMute(Context context){
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
	}
	
	public static void setUnMute(Context context){
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		//隐藏音乐进度条
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}
}
