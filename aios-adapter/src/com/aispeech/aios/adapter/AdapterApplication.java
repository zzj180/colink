package com.aispeech.aios.adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.localScanService.service.LocalMusicScanService;
import com.aispeech.aios.adapter.service.FloatWindowService;
import com.aispeech.aios.adapter.util.PreferenceHelper;
import com.aispeech.aios.adapter.util.SendBroadCastUtil;
import com.aispeech.aios.adapter.util.SystemPropertiesProxy;
import com.aispeech.aios.adapter.util.TTSController;
import com.tencent.bugly.crashreport.CrashReport;

public class AdapterApplication extends Application {

    public static String TAG = "AdapterApplication";
    private String ACC_STATE = "acc_state";
	public static final String KEY_PLATFORM = "ro.os.version";
    private static Context mContext;
    public static boolean accState;
    public static boolean isMute;
    public static Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("Unknown Error");
        }
        return mContext;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        AILog.i(TAG, "AIOS Adapter Application onCreate...");
        CrashHandler.getInstance().init(getApplicationContext());
        TTSController.getInstance(getApplicationContext()).init();
        mContext = getApplicationContext();

        if (PreferenceHelper.getInstance().isUseRemoteBusServer(false)) {
            BusClient.DEFAULT_BUS_SERVER = PreferenceHelper.getInstance().getRemoteBusServer(BusClient.DEFAULT_BUS_SERVER);
        }
       /* int storageVolume = PreferenceHelper.getInstance().getVolume();
        if(storageVolume !=0){
			AudioManager am = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
			am.setStreamVolume(AudioManager.STREAM_NOTIFICATION,storageVolume, 0);
			am.setStreamVolume(AudioManager.STREAM_ALARM, storageVolume, 0);
			am.setStreamVolume(AudioManager.STREAM_MUSIC, storageVolume, 0);
        }*/
  //    getApplicationContext().getContentResolver().registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, false, new MusicObserver(new Handler()));
       /* List<SongsBean> list = new ArrayList<SongsBean>();
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
        while(cursor!=null && cursor.moveToNext()){
        	int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
        	String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        	String artist= cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        	String display_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
        	SongsBean bean = new SongsBean();
        	bean.setId(id);
        	bean.setTitle(title);
        	bean.setName(display_name);
        	bean.setArtist(artist);
        	list.add(bean);
        }
        Songs songs = new Songs();
        songs.setSongs(list);
        Gson gson = new Gson();
		String json = gson.toJson(songs);
        Log.d("songs", json);
        AIOSMusicDataNode.getInstance().postData(json);
        */
        CrashReport.initCrashReport(this, "900022415", false);
        CrashReport.setUserId("" + getMac(this));

        startService(new Intent(this, FloatWindowService.class));
        startService(new Intent(this, LocalMusicScanService.class));//启动本地音乐扫描服务
   //   startService(new Intent(this, KWMusicService.class));
        
        getContentResolver().registerContentObserver(Settings.System.getUriFor(ACC_STATE),
				false, new ContentObserver(new Handler()) {
					@Override
					public void onChange(boolean selfChange) {
						getACCState();
					}
				});
        
        getACCState();
    }
    
    private void getACCState() {
		accState = Settings.System.getInt(getContentResolver(),ACC_STATE, 1)==1;
		if(accState){
			 //唤醒
            AILog.e(TAG, "acc_on");
            int storageVolume = PreferenceHelper.getInstance().getVolume();
			if (storageVolume > 0) {
				String platform = SystemPropertiesProxy.get(this, KEY_PLATFORM);
				if(TextUtils.isEmpty(platform)){
					AudioManager am = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
					am.setStreamVolume(AudioManager.STREAM_NOTIFICATION,storageVolume, 0);
					am.setStreamVolume(AudioManager.STREAM_ALARM,storageVolume, 0);
					am.setStreamVolume(AudioManager.STREAM_MUSIC,storageVolume * 2, 0);
					PreferenceHelper.getInstance().setVolume(0);
				}
			}
            getApplicationContext().startService(new Intent(getApplicationContext(), FloatWindowService.class));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AILog.e(TAG, "acc_off__startservice");
                    SendBroadCastUtil.getInstance().sendBroadCast("com.aispeech.acc.status", "status", "on");
                }
            }, 300);
		}else{
			 //休眠
            AILog.e(TAG, "acc_off");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AILog.e(TAG, "acc_off__stopservice");
                    if(PreferenceHelper.getInstance().getVolume() == 0){
        				String platform = SystemPropertiesProxy.get(AdapterApplication.this, KEY_PLATFORM);
        				if(TextUtils.isEmpty(platform)){
        					AudioManager am = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        					int curValue = am.getStreamVolume(AudioManager.STREAM_ALARM);
        					am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
        					am.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
        					am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        					PreferenceHelper.getInstance().setVolume(curValue);
        				}
        			}
//                  HomeNode.getInstance(context).getBusClient().call("recorder", "/recorder/stop");
                    SendBroadCastUtil.getInstance().sendBroadCast("com.aispeech.acc.status", "status", "off");
                    getApplicationContext().stopService(new Intent(getApplicationContext(), FloatWindowService.class));
                }
            }, 3000);
		}
	}

    public static String getMac(Context context) {
        String mac = null;
        String filecachedMac = null;

        if (context == null) {
            return null;
        }

        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            if (info != null) {
                mac = info.getMacAddress();
                filecachedMac = syncWithFileCache(context, "mac", mac);
                if (filecachedMac != null) {
                    return filecachedMac;
                }
            }

            // in case of anr
            if (context.getMainLooper().getThread().equals(Thread.currentThread())) {
                return null;
            }

            boolean isWifiEnabled = wifi.isWifiEnabled();
            if (!isWifiEnabled) {
                wifi.setWifiEnabled(true);
                Thread.sleep(5000);
            }

            info = wifi.getConnectionInfo();
            mac = info.getMacAddress();
            if (!isWifiEnabled) {
                wifi.setWifiEnabled(false);
            }

            filecachedMac = syncWithFileCache(context, "mac", mac);

            return filecachedMac;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String syncWithFileCache(Context context, String key, String value) {
        File file = new File(context.getFilesDir(), "." + key);
        if (value == null) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                value = br.readLine().trim();
                if (value.equals("")) {
                    value = null;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        // ignore
                        e.printStackTrace();
                    }
                }
            }
        } else {
            FileOutputStream fo = null;
            try {
                fo = new FileOutputStream(file);
                fo.write(value.getBytes());
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                if (fo != null) {
                    try {
                        fo.close();
                    } catch (IOException e) {
                        // ignore
                        e.printStackTrace();
                    }
                }
            }
        }

        return value;
    }

}
