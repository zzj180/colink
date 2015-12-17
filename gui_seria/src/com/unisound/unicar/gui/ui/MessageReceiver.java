package com.unisound.unicar.gui.ui;

import java.io.FileInputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.fm.UniDriveFmGuiService;
import com.unisound.unicar.gui.fm.XmFmGuiService;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.preference.UserPreference;
import com.unisound.unicar.gui.route.operation.GaodeMap;
import com.unisound.unicar.gui.route.operation.GoogleUriApi;
import com.unisound.unicar.gui.route.operation.KLDUriApi;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Gps;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.PositionUtil;

/**
 * 
 * @author xiaodong
 * 
 */
public class MessageReceiver extends BroadcastReceiver {

    private static final String TAG = "MessageReceiver";

    // public static final String ACTION_STOP_TALK = "com.unisound.intent.action.STOP_TALK";
    // public static final String ACTION_CANCEL_TALK = "com.unisound.intent.action.CANCEL_TALK";

    public static final String ACTION_COMPILE_GRAMMER =
            "com.unisound.intent.action.COMPILE_GRAMMER";

    public static final String ACTION_START_CALL_OUT = "com.unisound.intent.action.MAIN_CALL_OUT";
    public static final String ACTION_START_NAVIGATION =
            "com.unisound.intent.action.MAIN_NAVIGATION";
    public static final String ACTION_START_MUSIC = "com.unisound.intent.action.MAIN_MUSIC";
    public static final String ACTION_START_LOCAL_SEARCH =
            "com.unisound.intent.action.MAIN_LOCAL_SEARCH";

    public static final String ACTION_PLAY_TTS = "com.unisound.intent.action.PLAY_TTS";
    public static final String KEY_EXTRA_TTS_TEXT = "TTS_TEXT";

    /** XD added 20150923 for uniDriveFm */
    public static final String ACTION_START_UNIDRIVE_FM =
            "com.unisound.intent.action.START_UNIDRIVE_FM";
    public static final String ACTION_START_XM_FM = "com.unisound.intent.action.START_XM_FM";

    public static final String KEY_EXTRA_FM_SEARCH_TYPE = "FM_SEARCH_TYPE";
    public static final String KEY_EXTRA_FM_ARTIST = "FM_ARITST";
    public static final String KEY_EXTRA_FM_CATEGORY = "FM_CATEGORY";
    public static final String KEY_EXTRA_FM_KEYWORD = "FM_KEYWORD";
    public static final String KEY_EXTRA_FM_EPISODE = "FM_EPISODE";
    
    private static final String SPIT = ",";
	private static final String BDNAVI_PLAN_DEST = "bdnavi://plan?dest=";
	private static final String COM_GLSX_AUTONAVI = "com.glsx.bootup.send.autonavi";
	public static final String ACTION_START_TALK = "android.intent.action.VUI_SPEAK_ON_KEYEVENT";
	
	public static final String ACTION_ACC_ON = "android.intent.action.ACC_ON_KEYEVENT";
	public static final String ACTION_ACC_OFF = "android.intent.action.ACC_OFF_KEYEVENT";
	public static final String ACTION_VUI_START = "android.intent.action.VUI_SPEAK_ON_KEYEVENT";
	public static final String ACTION_REMOTE_NAVI = "action.colink.remotecommand";
	public static final String ACTION_REMOTE = "action.colink.remote_navi";
	public static final String ACTION_START_NAVI = "action.colink.remotestart";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "onReceive:intent " + intent);
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            intent.setClass(context, WindowService.class);
            context.startService(intent);
        } else if (ACTION_START_TALK.equals(action)) {
        	if(WindowService.accSwitch){
				Intent windowService = new Intent(context, WindowService.class);
				windowService.setAction(MessageReceiver.ACTION_START_TALK);
				context.startService(windowService);
			}
        } else if (ACTION_PLAY_TTS.equals(action)) {
            // XD added 20150914
            intent.setClass(context, WindowService.class);
            intent.setAction(MessageReceiver.ACTION_PLAY_TTS);
            context.startService(intent);
        } else if (ACTION_START_UNIDRIVE_FM.equals(action)) {
            intent.setClass(context, UniDriveFmGuiService.class);
            intent.setAction(ACTION_START_UNIDRIVE_FM);
            context.startService(intent);
        } else if (ACTION_START_XM_FM.equals(action)) {
            intent.setClass(context, XmFmGuiService.class);
            intent.setAction(ACTION_START_XM_FM);
            context.startService(intent);
        }else if(ACTION_REMOTE_NAVI.equals(action)){
			float lat=intent.getFloatExtra("lat", 0f);
			float lng=intent.getFloatExtra("lng", 0f);
			String address=intent.getStringExtra("address");
			Logger.d(TAG, "address= " + address+"---lat="+lat+"----lon="+lng);
			if(TextUtils.isEmpty(address)){
				address=context.getString(R.string.prepnavi_address);
			}
			UserPreference userPreference = new UserPreference(context);
			userPreference.putFloat("lat", lat);
			userPreference.putFloat("lng", lng);
			userPreference.putString("address", address);
			if(readAccFile()){
				Intent windowService = new Intent(context, WindowService.class);
				windowService.setAction(ACTION_START_NAVI);
				context.startService(windowService);
			}
		}else if(COM_GLSX_AUTONAVI.equals(action)){
			String poi = intent.getStringExtra("Destination");
			String point = intent.getStringExtra("DestPoint");
			String [] latlon=point.split(SPIT);
		//	Gps gcj02 = PositionUtil.gps84_To_Gcj02(Double.parseDouble(latlon[1]), Double.parseDouble(latlon[0])); //delete by zzj
			int mapIndex = UserPerferenceUtil.getMapChoose(context);
			switch (mapIndex) {
			case 1:
				GaodeMap.showRoute(context, GaodeMap.ROUTE_MODE_DRIVING,Double.parseDouble(latlon[1]),Double.parseDouble(latlon[0]), "", poi ,0);
				break;
			case 3:
				KLDUriApi.startNavi(context, Double.parseDouble(latlon[1]),Double.parseDouble(latlon[0]), poi);
				break;
			case 4:
				Gps gps = PositionUtil.gcj_To_Gps84(Double.parseDouble(latlon[1]),Double.parseDouble(latlon[0]));
	        	GoogleUriApi.showRoute(context, gps.getWgLat(), gps.getWgLon());
	        	break;
			default:
				Gps bd09 = PositionUtil.gcj02_To_Bd09(Double.parseDouble(latlon[1]),Double.parseDouble(latlon[0]));
				Intent navi = new Intent();
				navi.setData(Uri.parse(BDNAVI_PLAN_DEST + bd09.getWgLat()+SPIT+bd09.getWgLon()+SPIT+poi));
				navi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				try {
					context.startActivity(navi);
				} catch (Exception e) {
				}
				break;	
			}
		}else if(ACTION_REMOTE.equals(action)){
			double lat=intent.getDoubleExtra("lat", 0f);
			double lng=intent.getDoubleExtra("lng", 0f);
			String type=intent.getStringExtra("type");
			int mapIndex = UserPerferenceUtil.getMapChoose(context);
			if(readAccFile()){
				if(type.equals("bd")){
					switch (mapIndex) {
					case 1:
						Gps gcj02 = PositionUtil.bd09_To_Gcj02(lat, lng);
						GaodeMap.showRoute(context, GaodeMap.ROUTE_MODE_DRIVING,gcj02.getWgLat(),gcj02.getWgLon(), "", "" ,0);
						break;
					case 3:
						Gps gcj = PositionUtil.bd09_To_Gcj02(lat, lng);
						KLDUriApi.startNavi(context, gcj.getWgLat(),gcj.getWgLon(), "");
						break;
					default:
						Intent navi = new Intent();
						navi.setData(Uri.parse(BDNAVI_PLAN_DEST + lat+SPIT+lng+SPIT+""));
						navi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
						try {
							context.startActivity(navi);
						} catch (Exception e) {
						}
						break;	
					}
				}else{
					switch (mapIndex) {
					case 1:
						GaodeMap.showRoute(context, GaodeMap.ROUTE_MODE_DRIVING,lat,lng, "", "" ,0);
						break;
					case 3:
						KLDUriApi.startNavi(context, lat,lng, "");
						break;
					default:
						Gps bd09 = PositionUtil.gcj02_To_Bd09(lat,lng);
						Intent navi = new Intent();
						navi.setData(Uri.parse(BDNAVI_PLAN_DEST + bd09.getWgLat()+SPIT+bd09.getWgLon()+SPIT+""));
						navi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
						try {
							context.startActivity(navi);
						} catch (Exception e) {
						}
						break;	
					}
				}
			}
		}
        
    }
    
    @SuppressWarnings("finally")
	public static boolean readAccFile() {

		FileInputStream fis = null;
		byte[] rBuf = new byte[10];
		boolean accOn = true;
		try {
			fis = new FileInputStream("/sys/class/accdriver_cls/accdriver/accdriver");
			fis.read(rBuf);
			fis.close();
			if (rBuf[0] == (byte) 0) {
				accOn = false;
			} else if (rBuf[0] == (byte) 1) {
				accOn = true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			return accOn;
		}
	}
}
