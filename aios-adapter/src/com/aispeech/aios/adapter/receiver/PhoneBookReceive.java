package com.aispeech.aios.adapter.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.config.Configs.MapConfig;
import com.aispeech.aios.adapter.node.TTSNode;
import com.aispeech.aios.adapter.service.FloatWindowService;
import com.aispeech.aios.adapter.service.PhoneBookService;
import com.aispeech.aios.adapter.util.APPUtil;
import com.aispeech.aios.adapter.util.Gps;
import com.aispeech.aios.adapter.util.PositionUtil;
import com.aispeech.aios.adapter.util.PreferenceHelper;
import com.aispeech.aios.adapter.util.SendBroadCastUtil;
import com.aispeech.aios.adapter.util.SystemPropertiesProxy;
import com.aispeech.aios.adapter.util.TTSController;
import com.aispeech.aios.adapter.util.UserPreference;
import com.aispeech.aios.adapter.vendor.BD.BDOperate;
import com.aispeech.aios.adapter.vendor.BDDH.BDDHOperate;
import com.aispeech.aios.adapter.vendor.GD.GDOperate;
import com.aispeech.aios.adapter.vendor.GDCAR.GDCAROperator;
import com.aispeech.aios.adapter.vendor.GOOGLE.GGOperate;
import com.aispeech.aios.adapter.vendor.KLD.KLDOperate;
import com.aispeech.aios.adapter.vendor.MX.MXOperate;
import com.aispeech.aios.adapter.vendor.TB.TBOperate;

public class PhoneBookReceive extends BroadcastReceiver {

	private final static String PHONEBOOK_ACTION = "com.colink.zzj.contact.done";
	private static final String PLAY_TTS = "com.wanma.action.PLAY_TTS";
	private static final String GL_PLAY_TTS = "com.glsx.tts.speaktext";

	private static final String ACTION_ACC_ON = "android.intent.action.ACC_ON_KEYEVENT";
	private static final String ACTION_ACC_OFF = "android.intent.action.ACC_OFF_KEYEVENT";
	public static final String ACTION_START_TALK = "android.intent.action.VUI_SPEAK_ON_KEYEVENT";
	private static final String ACTION_REMOTE_VOICE_CONTROL = "com.inet.remote.VOICE_CONTROL";
	public static final String ACTION_CLOSE_WAKEUP = "android.intent.action.CLOSE_WAKEUP";
	public static final String ACTION_OPEN_WAKEUP = "android.intent.action.OPEN_WAKEUP";
	private static final String TEMP_HIGH_KEYEVENT = "android.intent.action.TEMP_HIGH_KEYEVENT";
	private static final String TEMP_NORMAL_KEYEVENT = "android.intent.action.TEMP_NORMAL_KEYEVENT";

	public static final String ACTION_REMOTE_NAVI = "action.colink.remotecommand";
	public static final String ACTION_REMOTE = "action.colink.remote_navi";
	public static final String ACTION_START_NAVI = "action.colink.remotestart";
	public static final String ACTION_ECAR_NAVI = "com.android.ecar.send";
	private static final String SPIT = ",";
	private static final String COM_GLSX_AUTONAVI = "com.glsx.bootup.send.autonavi";
	private static final String ACTION_ROMOTE_CLD = "action.colink.command_showway_cld";

	public static String _KEYS_ = "keySet";
	public static String _TYPE_STANDCMD_ = "standCMD";
	public static String _CMD_ = "ecarSendKey";
	public static String _TYPE_ = "cmdType";

	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		Log.d("TTSReceive","action=" +action +"--ismute = "+ AdapterApplication.isMute);
		if (PHONEBOOK_ACTION.equals(action)) {
			try {
				Intent service = new Intent(context,PhoneBookService.class);
				service.putExtra("uri", ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
				context.startService(service);
			} catch (Exception e) {
			}
		} else if (PLAY_TTS.equals(action)) {
			if (!AdapterApplication.isMute) {
				String playText = intent.getStringExtra("content");
				int id = intent.getIntExtra("id", -1);
				TTSController.getInstance(context).playText(playText, id);
			}
		} else if (GL_PLAY_TTS.equals(action)) {
			if (!AdapterApplication.isMute) {
				String playText = intent.getStringExtra("ttsText");
				TTSController.getInstance(context).playText(playText);
			}
		} else if (ACTION_ACC_ON.equals(action)) {
			AdapterApplication.accState = true;
				int storageVolume = PreferenceHelper.getInstance().getVolume();
				if (storageVolume > 0) {
					String platform = SystemPropertiesProxy.get(context,AdapterApplication.KEY_PLATFORM);
					if (TextUtils.isEmpty(platform)) {
						AudioManager am = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
						am.setStreamVolume(AudioManager.STREAM_NOTIFICATION,storageVolume, 0);
						am.setStreamVolume(AudioManager.STREAM_ALARM,storageVolume, 0);
						am.setStreamVolume(AudioManager.STREAM_MUSIC,storageVolume * 2, 0);
					}
					PreferenceHelper.getInstance().setVolume(0);
			}
			context.startService(new Intent(context, FloatWindowService.class));
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					context.sendBroadcast(new Intent("com.android.action_acc_on"));
					SendBroadCastUtil.getInstance().sendBroadCast("com.aispeech.acc.status", "status", "on");
				}
			}, 300);
		} else if (ACTION_ACC_OFF.equals(action)) {
			if (PreferenceHelper.getInstance().getVolume() == 0) {
				String platform = SystemPropertiesProxy.get(context,AdapterApplication.KEY_PLATFORM);
				if (TextUtils.isEmpty(platform)) {
					AudioManager am = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
					int curValue = am.getStreamVolume(AudioManager.STREAM_ALARM);
					am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
					am.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
					am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
					PreferenceHelper.getInstance().setVolume(curValue);
				}
			}
			AdapterApplication.accState = false;
			// HomeNode.getInstance(context).getBusClient().call("recorder", "/recorder/stop");
			context.sendBroadcast(new Intent("com.android.action_acc_off"));
			SendBroadCastUtil.getInstance().sendBroadCast("com.aispeech.acc.status", "status", "off");
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					context.stopService(new Intent(context,FloatWindowService.class));
				}
			}, 300);
		} else if (action.equals(TEMP_HIGH_KEYEVENT)) {
		} else if (action.equals(TEMP_NORMAL_KEYEVENT)) {
		}

		else if (ACTION_REMOTE_NAVI.equals(action)) {
			float lat = intent.getFloatExtra("lat", 0f);
			float lng = intent.getFloatExtra("lng", 0f);
			Log.d("TTSReceive", "lat = " + lat + "--- lng =" + lng);
			String address = intent.getStringExtra("address");
			if (TextUtils.isEmpty(address)) {
				address = context.getString(R.string.prepnavi_address);
			}
			UserPreference userPreference = new UserPreference(context);
			userPreference.putFloat("lat", lat);
			userPreference.putFloat("lng", lng);
			userPreference.putString("address", address);
			if (AdapterApplication.accState) {
				Intent windowService = new Intent(context,FloatWindowService.class);
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
			int mapType = Settings.System.getInt(context.getContentResolver(),"MAP_INDEX", MapConfig.BDDH);

			if (APPUtil.getInstance().isInstalled(Configs.getMapPackage(mapType))) {
				PoiBean bean = new PoiBean();
				bean.setLatitude(Double.parseDouble(latlon[1]));
				bean.setLongitude(Double.parseDouble(latlon[0]));
				bean.setName(poi);
				switch (mapType) {
				case Configs.MapConfig.GDMAP:
					if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAPFORCAT)){
	            		GDCAROperator.getInstance(context).startNavigation(bean);
	            	}else if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)){
	            		GDOperate.getInstance(context).startNavigation(bean);
	            	}
					break;
				case Configs.MapConfig.BDDH:
					Gps bd09 = PositionUtil.gcj02_To_Bd09(Double.parseDouble(latlon[1]),Double.parseDouble(latlon[0]));
					PoiBean bea = new PoiBean();
					bea.setLatitude(bd09.getWgLat());
					bea.setLongitude(bd09.getWgLon());
					bea.setName(poi);
					BDDHOperate.getInstance(context).startNavigation(bea);
					break;
				case Configs.MapConfig.KLDMAP:
					KLDOperate.getInstance(context).startNavigation(bean);
					break;
				case Configs.MapConfig.MXMAP:
					MXOperate.getInstance(context).startNavigation(bean);
					break;
				case Configs.MapConfig.GGMAP:
					GGOperate.getInstance(context).startNavigation(bean);
					break;
				case Configs.MapConfig.BDMAP:
					BDOperate.getInstance(context).startNavigation(bean);
					break;
				case Configs.MapConfig.TBMAP:
					TBOperate.getInstance(context).startNavigation(bean);
					break;
				case Configs.MapConfig.GDMAPFORCAT:
					GDCAROperator.getInstance(context).startNavigation(bean);
					break;
				}
			} else {
				TTSNode.getInstance().play(
						"没有找到" + Configs.getMapName(mapType) + "，无法导航");
			}
		} else if (ACTION_REMOTE.equals(action)) {
			double lat = intent.getDoubleExtra("lat", 0f);
			double lng = intent.getDoubleExtra("lng", 0f);
			String type = intent.getStringExtra("type");
			if (AdapterApplication.accState) {
				if (type.equals("bd")) {
					Gps gcj02 = PositionUtil.bd09_To_Gcj02(lat, lng);
					lat = gcj02.getWgLat();
					lng = gcj02.getWgLon();
				}
				int mapType = Settings.System.getInt(context.getContentResolver(), "MAP_INDEX",MapConfig.GDMAP);

				if (APPUtil.getInstance().isInstalled(Configs.getMapPackage(mapType))) {
					PoiBean bean = new PoiBean();
					bean.setLatitude(lat);
					bean.setLongitude(lng);
					switch (mapType) {
					case Configs.MapConfig.GDMAP:
						if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAPFORCAT)){
		            		GDCAROperator.getInstance(context).startNavigation(bean);
		            	}else if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)){
		            		GDOperate.getInstance(context).startNavigation(bean);
		            	}
						break;
					case Configs.MapConfig.BDDH:
						Gps bd09 = PositionUtil.gcj02_To_Bd09(lat, lng);
						PoiBean bea = new PoiBean();
						bea.setLatitude(bd09.getWgLat());
						bea.setLongitude(bd09.getWgLon());
						BDDHOperate.getInstance(context).startNavigation(bea);
						break;
					case Configs.MapConfig.KLDMAP:
						KLDOperate.getInstance(context).startNavigation(bean);
						break;
					case Configs.MapConfig.MXMAP:
						MXOperate.getInstance(context).startNavigation(bean);
						break;
					case Configs.MapConfig.GGMAP:
						GGOperate.getInstance(context).startNavigation(bean);
						break;
					case Configs.MapConfig.BDMAP:
						BDOperate.getInstance(context).startNavigation(bean);
						break;
					case Configs.MapConfig.TBMAP:
						TBOperate.getInstance(context).startNavigation(bean);
						break;
					case Configs.MapConfig.GDMAPFORCAT:
						GDCAROperator.getInstance(context).startNavigation(bean);
						break;
					}
				} else {
					TTSNode.getInstance().play("没有找到" + Configs.getMapName(mapType) + "，无法导航");
				}
			}
		} else if (ACTION_ECAR_NAVI.equals(action)) {
			String cmd = intent.getStringExtra(_CMD_);
			if ("StartMap".equals(cmd)) {
				if (AdapterApplication.accState) {
					String poiName;
					String toLat;
					String toLon;
					PoiBean bean = new PoiBean();
					int mapType = Settings.System.getInt(context.getContentResolver(), "MAP_INDEX",MapConfig.GDMAP);

					if (APPUtil.getInstance().isInstalled(Configs.getMapPackage(mapType))) {
						switch (mapType) {
						case Configs.MapConfig.GDMAP:
							poiName = intent.getStringExtra("gaode_poiName");
							toLat = intent.getStringExtra("gaode_latitude");
							toLon = intent.getStringExtra("gaode_longitude");
							bean.setLatitude(Double.parseDouble(toLat));
							bean.setLongitude(Double.parseDouble(toLon));
							bean.setName(poiName);
							if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAPFORCAT)){
			            		GDCAROperator.getInstance(context).startNavigation(bean);
			            	}else if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)){
			            		GDOperate.getInstance(context).startNavigation(bean);
			            	}
							break;
						case Configs.MapConfig.BDDH:
							poiName = intent.getStringExtra("baidu_poiName");
							toLat = intent.getStringExtra("baidu_latitude");
							toLon = intent.getStringExtra("baidu_longitude");
							bean.setLatitude(Double.parseDouble(toLat));
							bean.setLongitude(Double.parseDouble(toLon));
							bean.setName(poiName);
							BDDHOperate.getInstance(context).startNavigation(bean);
							break;
						case Configs.MapConfig.KLDMAP:
							poiName = intent.getStringExtra("gaode_poiName");
							toLat = intent.getStringExtra("gaode_latitude");
							toLon = intent.getStringExtra("gaode_longitude");
							bean.setLatitude(Double.parseDouble(toLat));
							bean.setLongitude(Double.parseDouble(toLon));
							bean.setName(poiName);
							KLDOperate.getInstance(context).startNavigation(bean);
							break;
						case Configs.MapConfig.MXMAP:
							poiName = intent.getStringExtra("gaode_poiName");
							toLat = intent.getStringExtra("gaode_latitude");
							toLon = intent.getStringExtra("gaode_longitude");
							bean.setLatitude(Double.parseDouble(toLat));
							bean.setLongitude(Double.parseDouble(toLon));
							bean.setName(poiName);
							MXOperate.getInstance(context).startNavigation(bean);
							break;
						case Configs.MapConfig.GGMAP:
							poiName = intent.getStringExtra("gaode_poiName");
							toLat = intent.getStringExtra("gaode_latitude");
							toLon = intent.getStringExtra("gaode_longitude");
							bean.setLatitude(Double.parseDouble(toLat));
							bean.setLongitude(Double.parseDouble(toLon));
							bean.setName(poiName);
							GGOperate.getInstance(context).startNavigation(bean);
							break;
						case Configs.MapConfig.BDMAP:
							BDOperate.getInstance(context).startNavigation(bean);
							break;
						case Configs.MapConfig.TBMAP:
							poiName = intent.getStringExtra("gaode_poiName");
							toLat = intent.getStringExtra("gaode_latitude");
							toLon = intent.getStringExtra("gaode_longitude");
							bean.setLatitude(Double.parseDouble(toLat));
							bean.setLongitude(Double.parseDouble(toLon));
							bean.setName(poiName);
							TBOperate.getInstance(context).startNavigation(bean);
							break;
						case Configs.MapConfig.GDMAPFORCAT:
							poiName = intent.getStringExtra("gaode_poiName");
							toLat = intent.getStringExtra("gaode_latitude");
							toLon = intent.getStringExtra("gaode_longitude");
							bean.setLatitude(Double.parseDouble(toLat));
							bean.setLongitude(Double.parseDouble(toLon));
							bean.setName(poiName);
							GDCAROperator.getInstance(context).startNavigation(bean);
							break;
						}
					} else {
						TTSNode.getInstance().play("没有找到" + Configs.getMapName(mapType) + "，无法导航");
					}

				}
			}else if ("UpdateContacts".equals(cmd)) {
				try {
					Intent service = new Intent(context,PhoneBookService.class);
					service.putExtra("uri", Uri.parse("content://com.android.ecar.provider.contacts/Contacts"));
					context.startService(service);
				} catch (Exception e) {
				}
			}
		} else if (ACTION_ROMOTE_CLD.equals(action)) {

			float lat = intent.getFloatExtra("lat", 0f);
			float lng = intent.getFloatExtra("lng", 0f);
			String address = intent.getStringExtra("address");
			Gps gcj = PositionUtil.bd09_To_Gps84(lat, lng);
			PoiBean bean = new PoiBean();
			bean.setLatitude(lat);
			bean.setLongitude(lng);
			KLDOperate.getInstance(context).startNavigation(bean);
		}
	}

}
