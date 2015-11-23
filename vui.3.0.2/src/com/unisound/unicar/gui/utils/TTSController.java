package com.unisound.unicar.gui.utils;

import android.content.Context;
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
	private static TTSController ttsManager;
	private Context mContext;
	private boolean isfinish = true;
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
		if (!isfinish) { return; }
		
		// 创建合成对象.
		init();
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
		mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "78");
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

}
