package com.colink.zzj.txzassistant.node;

import android.content.Context;
import android.media.AudioManager;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.oem.RomSystemSetting;
import com.colink.zzj.txzassistant.util.Logger;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZSysManager;
import com.txznet.sdk.TXZSysManager.VolumeMgrTool;

/**
 * @desc 系统节点
 * @auth zzj
 * @date 2016-03-19
 */
public class SystemNode {

	private Context mContext;
	private static SystemNode mInstance;
	private int cur_volume;

	private SystemNode() {
		this.mContext = AdapterApplication.getContext();
	}

	public void init() {
		TXZSysManager.getInstance().setVolumeMgrTool(mVolumeMgrTool);
	}

	public static synchronized SystemNode getInstance() {
		if (mInstance == null) {
			mInstance = new SystemNode();
		}
		return mInstance;
	}

	private VolumeMgrTool mVolumeMgrTool = new VolumeMgrTool() {
		@Override
		public void mute(boolean enable) {
			Logger.d("mute="+enable);
			AudioManager audioManager = (AudioManager) mContext
					.getSystemService(Context.AUDIO_SERVICE);
		//	audioManager.setStreamMute(AudioManager.STREAM_MUSIC, enable);
			if(enable){
				int pre_volune = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
				if(pre_volune!=0){
					cur_volume = pre_volune;
				}
				RomSystemSetting.playSoundTips(mContext,RomSystemSetting.TIPS_MUSIC_SETMIN);
			}else{
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
				int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				if(currentVolume==0){
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,cur_volume, 0);
					audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, cur_volume, 0);
					audioManager.setStreamVolume(AudioManager.STREAM_ALARM,cur_volume, 0);
					audioManager.setStreamVolume(AudioManager.STREAM_RING,cur_volume, 0);
					audioManager.setStreamVolume(AudioManager.STREAM_DTMF,cur_volume, 0);
					audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,cur_volume, 0);
					audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,cur_volume/2, 0);
				}
			}
			// TODO 静音控制
		}

		@Override
		public void minVolume() {
			Logger.d("minVolume");

			// TODO 最小音量
			// RomSystemSetting.setMinVolume(mContext);

			RomSystemSetting.playSoundTips(mContext,RomSystemSetting.TIPS_MUSIC_SETMIN);
		}

		@Override
		public void maxVolume() {
			// TODO 最大音量
			// RomSystemSetting.setMaxVolume(mContext);

			RomSystemSetting.playSoundTips(mContext,RomSystemSetting.TIPS_MUSIC_SETMAX);
		}

		@Override
		public void incVolume() {
			// TODO 增加音量
			// RomSystemSetting.RaiseOrLowerVolume(mContext, true, 2);

			RomSystemSetting.playSoundTips(mContext,RomSystemSetting.TIPS_MUSIC_RAISE);
		}

		@Override
		public void decVolume() {
			// TODO 减小音量
			// RomSystemSetting.RaiseOrLowerVolume(mContext, false, 2);

			RomSystemSetting.playSoundTips(mContext,RomSystemSetting.TIPS_MUSIC_LOWER);
		}
	};

}
