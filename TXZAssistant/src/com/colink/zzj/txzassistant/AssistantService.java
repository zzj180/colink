package com.colink.zzj.txzassistant;

import com.android.kwmusic.KWMusicService;
import com.colink.zzj.txzassistant.util.Logger;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZPowerManager;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.IBinder;
import android.provider.Settings;

/**
 * @desc 语音助手服务
 * @auth zzj
 * @date 2016-03-19
 */
public class AssistantService extends Service{

    private static final String ACTION_START_TALK = "cn.yunzhisheng.intent.action.START_TALK";
	private String MAP_INDEX = "MAP_INDEX";
    private String ACC_STATE = "acc_state";
    
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		
		super.onCreate();
		
		AdapterApplication.mapType = getMapType();
		
		startKWMusicService();
		
		getContentResolver().registerContentObserver(
				Settings.System.getUriFor(MAP_INDEX),
				false, new ContentObserver(AdapterApplication.uiHandler) {
					@Override
					public void onChange(boolean selfChange) {
						AdapterApplication.mapType = getMapType();
						Logger.d("mapType = "+ AdapterApplication.mapType);
					}
				});
		
		txzACC();
		
        getContentResolver().registerContentObserver(
				Settings.System.getUriFor(ACC_STATE),
				false, new ContentObserver(AdapterApplication.uiHandler) {
					@Override
					public void onChange(boolean selfChange) {
							 //唤醒
							AdapterApplication.runOnUiGround(new Runnable() {
				                @Override
				                public void run() {
				                	txzACC();
				                }
				            }, 1000);
					}
				});
	}
	
	private void startKWMusicService() {
		Intent intentMusic = new Intent(this, KWMusicService.class);
		this.startService(intentMusic);
	}
	
	private int getMapType() {
		return Settings.System.getInt(getContentResolver(),MAP_INDEX, 0);
	}
	
	private void txzACC() {
		AdapterApplication.mAcc = Settings.System.getInt(getContentResolver(),ACC_STATE, 1)==1;
		if(AdapterApplication.mAcc){
			TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_WAKEUP);
		}else{
			TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
		//	TXZPowerManager.getInstance().releaseTXZ();
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if (intent != null) {
			String action = intent.getAction();
			Logger.d(action);
			if(ACTION_START_TALK.equals(action)){
				if (TXZConfigManager.getInstance().isInitedSuccess()) {
					TXZAsrManager.getInstance().start("有什么可以帮您");
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
}
