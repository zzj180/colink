package com.colink.zzj.txzassistant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.colink.zzj.txzassistant.util.Constants;
import com.colink.zzj.txzassistant.util.Gps;
import com.colink.zzj.txzassistant.util.Logger;
import com.colink.zzj.txzassistant.util.PositionUtil;
import com.colink.zzj.txzassistant.util.StringUtil;
import com.colink.zzj.txzassistant.util.UserPerferenceUtil;
import com.colink.zzj.txzassistant.util.UserPreference;
import com.colink.zzj.txzassistant.vendor.BDDH.BDDHOperate;
import com.colink.zzj.txzassistant.vendor.GD.GDOperate;
import com.colink.zzj.txzassistant.vendor.KLD.KLDOperate;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZCallManager;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZPowerManager;
import com.txznet.sdk.TXZTtsManager;
import com.txznet.sdk.TXZConfigManager.FloatToolType;

/**
 * @desc 开机ACC广播
 * @auth zzj
 * @date 2016-03-19
 */
public class BootReceiver extends BroadcastReceiver {

	// private static final String TELPHONE_SERVICE =
	// "cn.colink.service.Telphone_Service";
	private static final String ACTION_ACC_ON = "android.intent.action.ACC_ON_KEYEVENT";
	private static final String ACTION_ACC_OFF = "android.intent.action.ACC_OFF_KEYEVENT";
	public static final String ACTION_START_TALK = "android.intent.action.VUI_SPEAK_ON_KEYEVENT";
	private static final String ACTION_REMOTE_VOICE_CONTROL = "com.inet.remote.VOICE_CONTROL";
	public static final String ACTION_CLOSE_WAKEUP = "android.intent.action.CLOSE_WAKEUP";
	public static final String ACTION_OPEN_WAKEUP = "android.intent.action.OPEN_WAKEUP";
	private static final String PLAY_TTS = "com.wanma.action.PLAY_TTS";
	private static final String GL_PLAY_TTS = "com.glsx.tts.speaktext";
	private static final String TEMP_HIGH_KEYEVENT = "android.intent.action.TEMP_HIGH_KEYEVENT";
	private static final String TEMP_NORMAL_KEYEVENT = "android.intent.action.TEMP_NORMAL_KEYEVENT";
	private final static String PHONEBOOK_ACTION = "com.android.ecar.send";

	public static final String ACTION_REMOTE_NAVI = "action.colink.remotecommand";
	public static final String ACTION_REMOTE = "action.colink.remote_navi";
	public static final String ACTION_START_NAVI = "action.colink.remotestart";
	public static final String ACTION_ECAR_NAVI = "com.android.ecar.send";
	private static final String SPIT = ",";
	private static final String COM_GLSX_AUTONAVI = "com.glsx.bootup.send.autonavi";
	private static final String ACTION_ROMOTE_CLD = "action.colink.command_showway_cld";
	private static final String ACTION_PHONEBOOK = "com.zzj.phonebook.send";

	public static String _KEYS_ = "keySet";
	public static String _TYPE_STANDCMD_ = "standCMD";
	public static String _CMD_ = "ecarSendKey";
	public static String _TYPE_ = "cmdType";

	private int mTtsTaskId;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Logger.d(action);
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			context.startService(new Intent(context, AssistantService.class));
		} else if (ACTION_START_TALK.equals(action)) {
			if (TXZConfigManager.getInstance().isInitedSuccess()) {
				TXZAsrManager.getInstance().start("有什么可以帮您");
			}
		} else if (ACTION_REMOTE_VOICE_CONTROL.equals(action)) {
			if (TXZConfigManager.getInstance().isInitedSuccess()) {
				if (AdapterApplication.mAcc) {
					String keyword = intent.getStringExtra("content");
					keyword = StringUtil.clearSpecialCharacter(keyword);
					Logger.d(keyword);
					TXZAsrManager.getInstance().startWithRawText(keyword);
				}
			}
		} else if (ACTION_CLOSE_WAKEUP.equals(action)) {

			/*Set<String> words = AdapterApplication.getContext().getSharedPreferences(Constants.PRE_NAME, Context.MODE_PRIVATE).getStringSet(Constants.KEY_PRE_WAKEUPWORDS, null);
			String[] strSet = new String[words.size()];
			words.toArray(strSet);
			TXZConfigManager.getInstance().setWakeupKeywordsNew(strSet);
			boolean micEnable = UserPerferenceUtil.getFloatMicEnable(context);
			TXZTtsManager.getInstance().setVoiceSpeed(UserPerferenceUtil.getTTSSpeed(context));
			TXZConfigManager.getInstance().showFloatTool(micEnable ? FloatToolType.FLOAT_NORMAL : FloatToolType.FLOAT_NONE);
			TXZConfigManager.getInstance().setWakeupThreshhold(UserPerferenceUtil.getWakeupThreshold(context));
			TXZConfigManager.getInstance().enableWakeup(UserPerferenceUtil.getAECEnable(context));
			TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
			TXZPowerManager.getInstance().releaseTXZ();*/

		} else if (ACTION_OPEN_WAKEUP.equals(action)) {

			/*TXZPowerManager.getInstance().reinitTXZ(new Runnable() {
				@Override
				public void run() {
					TXZPowerManager.getInstance().notifyPowerAction(
							TXZPowerManager.PowerAction.POWER_ACTION_WAKEUP);
				}
			});*/

		} else if (PLAY_TTS.equals(action)) {

			String playText = intent.getStringExtra("content");
			Logger.d(playText);
		//	TXZTtsManager.getInstance().cancelSpeak(mTtsTaskId);
			mTtsTaskId = TXZTtsManager.getInstance().speakText(StringUtil.clearSpecial(playText.replace("usraud","")));
		} else if (GL_PLAY_TTS.equals(action)) {

			String playText = intent.getStringExtra("ttsText");
			TXZTtsManager.getInstance().cancelSpeak(mTtsTaskId);
			mTtsTaskId = TXZTtsManager.getInstance().speakText(playText);

		} else if (ACTION_ACC_ON.equals(action)) {
			AdapterApplication.mAcc = true;
			TXZPowerManager.getInstance().reinitTXZ(new Runnable() {
				@Override
				public void run() {
					TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_WAKEUP);
				}
			});
		} else if (ACTION_ACC_OFF.equals(action)) {
			AdapterApplication.mAcc =false;
			TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
			TXZPowerManager.getInstance().releaseTXZ();
		} else if (action.equals(TEMP_HIGH_KEYEVENT)) {
			TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
			TXZPowerManager.getInstance().releaseTXZ();
		} else if (action.equals(TEMP_NORMAL_KEYEVENT)) {
			TXZPowerManager.getInstance().reinitTXZ(new Runnable() {
				@Override
				public void run() {
					TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_WAKEUP);
				}
			});
		}

		else if (ACTION_REMOTE_NAVI.equals(action)) {
			float lat = intent.getFloatExtra("lat", 0f);
			float lng = intent.getFloatExtra("lng", 0f);
			String address = intent.getStringExtra("address");
			Logger.d("address= " + address + "---lat=" + lat + "----lon=" + lng);
			if (TextUtils.isEmpty(address)) {
				address = context.getString(R.string.prepnavi_address);
			}
			UserPreference userPreference = new UserPreference(context);
			userPreference.putFloat("lat", lat);
			userPreference.putFloat("lng", lng);
			userPreference.putString("address", address);
			if (AdapterApplication.mAcc) {
				Intent windowService = new Intent(context,
						AssistantService.class);
				windowService.setAction(ACTION_START_NAVI);
				context.startService(windowService);
			}
		} else if (COM_GLSX_AUTONAVI.equals(action)) {
			String poi = intent.getStringExtra("Destination");
			String point = intent.getStringExtra("DestPoint");
			String[] latlon = point.split(SPIT);
			// Gps gcj02 =
			// PositionUtil.gps84_To_Gcj02(Double.parseDouble(latlon[1]),
			// Double.parseDouble(latlon[0])); //delete by zzj
			startNavigation(context, Double.parseDouble(latlon[1]),
					Double.parseDouble(latlon[0]), poi);
		} else if (ACTION_REMOTE.equals(action)) {
			double lat = intent.getDoubleExtra("lat", 0f);
			double lng = intent.getDoubleExtra("lng", 0f);
			String type = intent.getStringExtra("type");
			if (AdapterApplication.mAcc) {
				if (type.equals("bd")) {
					Gps gcj02 = PositionUtil.bd09_To_Gcj02(lat, lng);
					lat = gcj02.getWgLat();
					lng = gcj02.getWgLon();
				}
				startNavigation(context, lat, lng, " ");
			}
		} else if (ACTION_ECAR_NAVI.equals(action)) {
			String cmd = intent.getStringExtra(_CMD_);
			Logger.d("cmd = " + cmd);
			if ("StartMap".equals(cmd)) {
				if (AdapterApplication.mAcc) {
					String poiName;
					String toLat;
					String toLon;
					switch (AdapterApplication.mapType) {
					case 1:
						poiName = intent.getStringExtra("gaode_poiName");
						toLat = intent.getStringExtra("gaode_latitude");
						toLon = intent.getStringExtra("gaode_longitude");
						GDOperate.getInstance(context).startNavigation(
								Double.parseDouble(toLat),
								Double.parseDouble(toLon));
						break;
					case 3:
						poiName = intent.getStringExtra("stand_poiName");
						toLat = intent.getStringExtra("stand_latitude");
						toLon = intent.getStringExtra("stand_longitude");
						break;
					case 2:
						poiName = intent.getStringExtra("stand_poiName");
						toLat = intent.getStringExtra("stand_latitude");
						toLon = intent.getStringExtra("stand_longitude");
						KLDOperate.getInstance(context).startNavigation(
								Double.parseDouble(toLat),
								Double.parseDouble(toLon), poiName);
						break;
					case 4:
						toLat = intent.getStringExtra("stand_latitude");
						toLon = intent.getStringExtra("stand_longitude");
						break;
					case 5:
						poiName = intent.getStringExtra("gaode_poiName");
						toLat = intent.getStringExtra("gaode_latitude");
						toLon = intent.getStringExtra("gaode_longitude");
						Intent i = new Intent("com.mxnavi.mxnavi.PTT_DEST_ACTION");
						String dest_string = "(TND,2,0, " + toLat + "," + toLon + "," + poiName + ")";
						i.putExtra("data", dest_string);
						i.setFlags(32);
						context.sendBroadcast(i);
						break;
					default:
						poiName = intent.getStringExtra("baidu_poiName");
						toLat = intent.getStringExtra("baidu_latitude");
						toLon = intent.getStringExtra("baidu_longitude");
						BDDHOperate.getInstance(context).startNavigation(
								Double.parseDouble(toLat),
								Double.parseDouble(toLon));
						break;
					}

				}
			} else if ("UpdateContacts".equals(cmd)) {
				try {
					context.startService(new Intent(context,
							PhoneBookService.class));
				} catch (Exception e) {
				}
			}
		} else if(ACTION_PHONEBOOK.equals(action)){
			Intent pb= new Intent(context,PhoneBookService.class);
			pb.setAction("bluetooth");
			ArrayList<HashMap<String, String>> li = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("phone");
			Logger.d("phone="+li.size());
			pb.putExtra("phone", li);
			context.startService(pb);
		}else if (ACTION_ROMOTE_CLD.equals(action)) {

			float lat = intent.getFloatExtra("lat", 0f);
			float lng = intent.getFloatExtra("lng", 0f);
			String address = intent.getStringExtra("address");
			Gps gcj = PositionUtil.bd09_To_Gps84(lat, lng);
			KLDOperate.getInstance(context).startNavigation(gcj.getWgLat(),gcj.getWgLon(), address);
		} 
	}

	private void startNavigation(Context mContext, double lat, double lon,
			String name) {
		if (AdapterApplication.mapType == 1) {// 高德
			GDOperate.getInstance(mContext).startNavigation(lat, lon);
		} else if (AdapterApplication.mapType == 2) {
			KLDOperate.getInstance(mContext).startNavigation(lat, lon, name);
		} else if (AdapterApplication.mapType == 0) {// 百度导航开始导航
			Gps bd09 = PositionUtil.gcj02_To_Bd09(lat, lon);
			BDDHOperate.getInstance(mContext).startNavigation(bd09.getWgLat(),bd09.getWgLon());
		}
	}

}
