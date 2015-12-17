package com.unisound.unicar.gui.utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

/**
 * 语音播报组件
 * 
 */
public class TTSController implements SynthesizerListener {
	private static final String STOP = "stop";
	private static final String START = "start";
	private static final String KEY_ID = "id";
	private static final String KEY = "state";
	private static TTSController ttsManager;
	private Context mContext;
	private boolean isfinish = true;
	private int id;
	private final static String ACTION_TTS_STATE= "com.coogo.action.STATE_TTS";
	// 合成对象.
	private SpeechSynthesizer mSpeechSynthesizer;
	private InitListener minitListener = new InitListener() {
		@Override
		public void onInit(int arg0) {
		}
	};

	TTSController(Context context) {
		mContext = context;
	}

	public static TTSController getInstance(Context context) {
		if (ttsManager == null) {
			ttsManager = new TTSController(context);
		}
		return ttsManager;
	}
	public void init() {
		if(mSpeechSynthesizer==null){
			SpeechUtility.createUtility(mContext, SpeechConstant.APPID + "=54b9e699");
			// 初始化合成对象.
			mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext,minitListener);
			initSpeechSynthesizer();
		}
	}
	/**
	 * 使用SpeechSynthesizer合成语音，不弹出合成Dialog.
	 * @param
	 */
	public void playText(String playText) {
		Logger.d("tts", "!--->isfinish = " + isfinish);
		if (!isfinish) { return;}
		
		// 创建合成对象.
		init();
		id = -1;
		mSpeechSynthesizer.startSpeaking(playText, this);
	}
	
	public void playText(String playText,int id) {
		Logger.d("tts", "!--->isfinish = " + isfinish);
		if (!isfinish) { return; }
		
		// 创建合成对象.
		init();
		this.id = id;
		mSpeechSynthesizer.startSpeaking(playText, this);
	}
	
	public void stopSpeaking() {
		if (mSpeechSynthesizer != null)
			mSpeechSynthesizer.stopSpeaking();
	}

	private void initSpeechSynthesizer() {
		// 设置引擎类型
		mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE,SpeechConstant.TYPE_LOCAL);
		// 设置发音人
		mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME,"xiaoyan");
		mSpeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE,AudioManager.STREAM_MUSIC+"");
		// 设置语速
		mSpeechSynthesizer.setParameter(SpeechConstant.SPEED,"63");
		// 设置音量
		mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "63");
		// 设置语调
		mSpeechSynthesizer.setParameter(SpeechConstant.PITCH,"50");

	}

	public void destroy() {
		if (mSpeechSynthesizer != null) {
		//	mSpeechSynthesizer.destroy();
		}
	}

	@Override
	public void onCompleted(SpeechError arg0) {
		isfinish = true;
		sendTTSState(STOP);
	}

	@Override
	public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

	}

	@Override
	public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {

	}

	@Override
	public void onSpeakBegin() {
		isfinish = false;
		sendTTSState(START);
	}

	@Override
	public void onSpeakPaused() {
		isfinish = true;
	}

	@Override
	public void onSpeakProgress(int arg0, int arg1, int arg2) {

	}

	@Override
	public void onSpeakResumed() {
	}
	
	private void sendTTSState(String state){
		Intent intent= new Intent(ACTION_TTS_STATE);
		intent.putExtra(KEY, state);
		intent.putExtra(KEY_ID, id);
		mContext.sendBroadcast(intent);
	}
}
