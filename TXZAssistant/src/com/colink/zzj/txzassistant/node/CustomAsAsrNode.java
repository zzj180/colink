package com.colink.zzj.txzassistant.node;


import android.content.Intent;

import com.android.kwmusic.CommandPreference;
import com.android.kwmusic.KWMusicService;
import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.R;
import com.colink.zzj.txzassistant.oem.RomSystemSetting;
import com.colink.zzj.txzassistant.util.APPUtil;
import com.colink.zzj.txzassistant.util.Constants;
import com.colink.zzj.txzassistant.util.Logger;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZTtsManager;
import com.txznet.sdk.TXZAsrManager.AsrComplexSelectCallback;

/**
 * @desc  拍照场景节点
 * @auth zzj
 * @date 2016-03-19
 */
public class CustomAsAsrNode {
	private static CustomAsAsrNode mInstance;
	private static final boolean USE_ASR = true;

	private CustomAsAsrNode() {
	}

	public static synchronized CustomAsAsrNode getInstance() {
		if (mInstance == null) {
			mInstance = new CustomAsAsrNode();
		}
		return mInstance;
	}
	
	public void useWakeupAsAsr(){
		if(USE_ASR){
			TXZAsrManager.getInstance().useWakeupAsAsr(new AsrComplexSelectCallback() {
				
				@Override
				public void onCommandSelected(String type, String command) {
					Logger.d(type);
					if(Constants.CAPTURE_PICTURE_ASR.equals(type)){
						if(APPUtil.getInstance().isInstalled(APPUtil.CAMERA_PKG)){
							AdapterApplication.getContext().sendBroadcast(new Intent("action.colink.take.picture"));
						}else {
							AdapterApplication.getContext().sendBroadcast(new Intent("android.intent.action.TAKE_PICTURE"));
						}
					}else if(Constants.PLAY_MUSIC_ASR.equals(type)){
						Intent intent = new Intent(AdapterApplication.getContext(),KWMusicService.class);
						intent.setAction(CommandPreference.SERVICECMD);
						intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDOPEN_MUSIC);
						AdapterApplication.getContext().startService(intent);
					}else if(Constants.NEXT_MUSIC_ASR.equals(type)){
						Intent intent = new Intent(AdapterApplication.getContext(),KWMusicService.class);
						intent.setAction(CommandPreference.NEXT_ACTION);
						AdapterApplication.getContext().startService(intent);
					}else if(Constants.PREV_MUSIC_ASR.equals(type)){
						Intent intent = new Intent(AdapterApplication.getContext(),KWMusicService.class);
						intent.setAction(CommandPreference.PREVIOUS_ACTION);
						AdapterApplication.getContext().startService(intent);
					}else if(Constants.PAUSE_MUSIC_ASR.equals(type)){
						Intent intent = new Intent(AdapterApplication.getContext(),KWMusicService.class);
						intent.setAction(CommandPreference.PAUSE_ACTION);
						AdapterApplication.getContext().startService(intent);
					}else if(Constants.RAISE_VOLUME_ASR.equals(type)){
						TXZTtsManager.getInstance().speakText(RomSystemSetting.playSoundTips(AdapterApplication.getContext(), RomSystemSetting.TIPS_MUSIC_RAISE));
					}else if(Constants.LOWER_VOLUME_ASR.equals(type)){
						TXZTtsManager.getInstance().speakText(RomSystemSetting.playSoundTips(AdapterApplication.getContext(), RomSystemSetting.TIPS_MUSIC_LOWER));
					}
				};
				
				@Override
				public boolean needAsrState() {
					
					return false;
				}
				
				@Override
				public String getTaskId() {
					return "asrTask";
				}
			}.addCommand(Constants.CAPTURE_PICTURE_ASR, AdapterApplication.getContext().getResources()
						.getStringArray(R.array.capture_wakeup))
					.addCommand(Constants.PLAY_MUSIC_ASR, AdapterApplication.getContext().getResources()
							.getStringArray(R.array.play_music))
					.addCommand(Constants.NEXT_MUSIC_ASR, AdapterApplication.getContext().getResources()
							.getStringArray(R.array.next_music))
					.addCommand(Constants.PREV_MUSIC_ASR, AdapterApplication.getContext().getResources()
							.getStringArray(R.array.prev_music))
					.addCommand(Constants.PAUSE_MUSIC_ASR, AdapterApplication.getContext().getResources()
							.getStringArray(R.array.pause_music))
					.addCommand(Constants.RAISE_VOLUME_ASR, AdapterApplication.getContext().getResources()
							.getStringArray(R.array.raise_volume))
					.addCommand(Constants.LOWER_VOLUME_ASR, AdapterApplication.getContext().getResources()
							.getStringArray(R.array.lower_volume)));
		}
		
	}
	
	public void recover(){
		if(USE_ASR)
		TXZAsrManager.getInstance().recoverWakeupFromAsr("asrTask");
	}
	
}
