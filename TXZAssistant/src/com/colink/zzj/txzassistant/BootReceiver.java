package com.colink.zzj.txzassistant;

import com.colink.zzj.txzassistant.util.Logger;
import com.colink.zzj.txzassistant.util.StringUtil;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZPowerManager;
import com.txznet.sdk.TXZTtsManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
	private int mTtsTaskId;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Logger.d(action);
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			context.startService(new Intent(context, AssistantService.class));
		} else if (ACTION_ACC_ON.equals(action)) {
			TXZPowerManager.getInstance().notifyPowerAction(
					TXZPowerManager.PowerAction.POWER_ACTION_WAKEUP);
		} else if (ACTION_ACC_OFF.equals(action)) {
			TXZPowerManager.getInstance().notifyPowerAction(
					TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
			TXZPowerManager.getInstance().releaseTXZ();
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

			TXZPowerManager.getInstance().notifyPowerAction(
					TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
	//		TXZPowerManager.getInstance().releaseTXZ();

		} else if (ACTION_OPEN_WAKEUP.equals(action)) {

			TXZPowerManager.getInstance().notifyPowerAction(
					TXZPowerManager.PowerAction.POWER_ACTION_WAKEUP);

		} else if (PLAY_TTS.equals(action)) {

			String playText = intent.getStringExtra("content");
			Logger.d(playText);
			TXZTtsManager.getInstance().cancelSpeak(mTtsTaskId);
			mTtsTaskId = TXZTtsManager.getInstance().speakText(playText.replace("usraud", ""));

		} else if (GL_PLAY_TTS.equals(action)) {

			String playText = intent.getStringExtra("ttsText");
			TXZTtsManager.getInstance().cancelSpeak(mTtsTaskId);
			mTtsTaskId = TXZTtsManager.getInstance().speakText(playText);

		}
	}

}
