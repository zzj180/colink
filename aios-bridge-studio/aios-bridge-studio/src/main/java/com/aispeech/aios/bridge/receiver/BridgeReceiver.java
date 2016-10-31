package com.aispeech.aios.bridge.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import com.aispeech.aios.bridge.BridgeApplication;
import com.aispeech.aios.bridge.BridgeService;
import com.aispeech.aios.bridge.R;
import com.aispeech.aios.bridge.utils.Gps;
import com.aispeech.aios.bridge.utils.Logger;
import com.aispeech.aios.bridge.utils.PositionUtil;
import com.aispeech.aios.bridge.utils.PreferenceUtil;
import com.aispeech.aios.bridge.vendor.KLDOperate;
import com.aispeech.aios.sdk.manager.AIOSSystemManager;

import cn.kuwo.autosdk.api.KWAPI;

public class BridgeReceiver extends BroadcastReceiver {
    private static final String TAG = "Bridge - BridgeReceiver";

    private static final String ACTION_REMOTE_NAVI = "action.colink.remotecommand";
    private static final String ACTION_REMOTE = "action.colink.remote_navi";
    private static final String ACTION_ECAR_NAVI = "com.android.ecar.send";
    private static final String COM_GLSX_AUTONAVI = "com.glsx.bootup.send.autonavi";
    private static final String ACTION_ROMOTE_CLD = "action.colink.command_showway_cld";
    private static final String ACTION_MIC = "com.intent.action.MIC_CLICK";
    private static final String SPIT = ",";

    private static String _CMD_ = "ecarSendKey";

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, BridgeService.class));
        String action = intent.getAction();
//       if (SDKBroadcast.Action.BOOT_BRIDGE.equals(action)){
//            AIOSForCarSDK.bootAIOS(0);
//        }
        if (ACTION_REMOTE_NAVI.equals(action)) {
            float lat = intent.getFloatExtra("lat", 0f);
            float lng = intent.getFloatExtra("lng", 0f);
            String address = intent.getStringExtra("address");
            if (TextUtils.isEmpty(address)) {
                address = context.getString(R.string.prepnavi_address);
            }
            PreferenceUtil.setFloat(context,"lat", lat);
            PreferenceUtil.setFloat(context,"lng", lng);
            PreferenceUtil.setString(context,"address", address);
            boolean acc = Settings.System.getInt(context.getContentResolver(), BridgeApplication.ACC_STATE, 1) == 1;
            if (acc) {
                Intent windowService = new Intent(context,
                        BridgeService.class);
                windowService.setAction(BridgeService.ACTION_PRE_NAVI);
                context.startService(windowService);
            }
        } else if (ACTION_MIC.equals(action)){
            boolean show = Settings.System.getInt(context.getContentResolver(),"tts_show",0)==1;
            if(show){
                Logger.d("stopInteraction");
                AIOSSystemManager.getInstance().stopInteraction();
                Settings.System.putInt(context.getContentResolver(),"tts_show",0);
            }else{
                Logger.d("startInteraction");
                boolean acc = Settings.System.getInt(context.getContentResolver(),BridgeApplication.ACC_STATE, 1) == 1;
                if(acc) {
                    AIOSSystemManager.getInstance().startInteraction();
                }
            }
        }else if (COM_GLSX_AUTONAVI.equals(action)) {
            String poi = intent.getStringExtra("Destination");
            String point = intent.getStringExtra("DestPoint");
            String[] latlon = point.split(SPIT);
            // Gps gcj02 =
            // PositionUtil.gps84_To_Gcj02(Double.parseDouble(latlon[1]),
            // Double.parseDouble(latlon[0])); //delete by zzj
            boolean acc = Settings.System.getInt(context.getContentResolver(), BridgeApplication.ACC_STATE, 1) == 1;
            if (acc) {
                Intent windowService = new Intent(context,
                        BridgeService.class);
                windowService.setAction(BridgeService.STRAT_NAVI_ACTION);
                windowService.putExtra("lat", Double.parseDouble(latlon[1]));
                windowService.putExtra("lon", Double.parseDouble(latlon[0]));
                windowService.putExtra("address", poi);
                context.startService(windowService);
            }
        } else if (ACTION_REMOTE.equals(action)) {
            double lat = intent.getDoubleExtra("lat", 0f);
            double lng = intent.getDoubleExtra("lng", 0f);
            String type = intent.getStringExtra("type");
            boolean acc = Settings.System.getInt(context.getContentResolver(), BridgeApplication.ACC_STATE, 1) == 1;
            if (acc) {
                if (type.equals("bd")) {
                    Gps gcj02 = PositionUtil.bd09_To_Gcj02(lat, lng);
                    lat = gcj02.getWgLat();
                    lng = gcj02.getWgLon();
                }
                Intent windowService = new Intent(context,
                        BridgeService.class);
                windowService.setAction(BridgeService.STRAT_NAVI_ACTION);
                windowService.putExtra("lat", lat);
                windowService.putExtra("lon", lng);
                context.startService(windowService);
            }
        } else if (ACTION_ECAR_NAVI.equals(action)) {
            String cmd = intent.getStringExtra(_CMD_);
            if ("StartMap".equals(cmd)) {
                boolean acc = Settings.System.getInt(context.getContentResolver(), BridgeApplication.ACC_STATE, 1) == 1;
                if (acc) {
                    String poiName = intent.getStringExtra("gaode_poiName");
                    String toLat = intent.getStringExtra("gaode_latitude");
                    String toLon = intent.getStringExtra("gaode_longitude");
                    Intent windowService = new Intent(context,
                            BridgeService.class);
                    windowService.setAction(BridgeService.STRAT_NAVI_ACTION);
                    windowService.putExtra("lat", Double.parseDouble(toLat));
                    windowService.putExtra("lon", Double.parseDouble(toLon));
                    windowService.putExtra("address", poiName);
                    context.startService(windowService);
                }
            }
        } else if (ACTION_ROMOTE_CLD.equals(action)) {

            float lat = intent.getFloatExtra("lat", 0f);
            float lng = intent.getFloatExtra("lng", 0f);
            String address = intent.getStringExtra("address");
            KLDOperate.getInstance(context).startNavigation(lat, lng, address);
        }

    }
}
