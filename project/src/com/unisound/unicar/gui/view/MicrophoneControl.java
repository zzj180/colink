/**
 * Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MicrophoneControl.java
 * @ProjectName : V Plus 1.0
 * @PakageName : cn.yunzhisheng.vui.assistant.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-5-24
 */
package com.unisound.unicar.gui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;

@SuppressLint({ "HandlerLeak", "NewApi" })
public class MicrophoneControl extends MicrophoneControlBaseView {
	public static final String TAG = "MicrophoneControl";
	
	private static final int TIME_MINISECONDS_MIC_FLICKER = 200; //0.2s  

	private ImageView mBtnMic;
	private CheckBox mCancelBtn;

	private ImageView mIvMicException;//xiaodong added 20150807

	private ImageView mIvMicRecognitionBg;
	private ImageView mImageViewMicRecognize;
	
	private ImageView mIvRecordingRecording;  //xiaodong added 20150615
	private ProgressBar mPbMicPrepare;   //xiaodong added 20150623
    private RelativeLayout mRlMic;
    
	private RoundVoiceLevelView mVoiceLevel;
	private RotateAnimation mRotateAnimationMicRecognize;
	private TextView mTextViewAnswer;
	private int mVolume;
//	private Timer mTimer;
//	private TimerTask mTimerTask;
	
	private long mLastVolumeUpdateTime = 0;

	private Handler mHandler = new Handler();
	
	private Context mContext;

	public MicrophoneControl(Context context) {
		this(context, null);
	}

	public MicrophoneControl(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MicrophoneControl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		View.inflate(context, R.layout.mic_control, this);
		mBtnMic = (ImageView) findViewById(R.id.btnMic);
		mCancelBtn = (CheckBox) findViewById(R.id.cancelBtn);
		mVoiceLevel = (RoundVoiceLevelView) findViewById(R.id.my_voice_progress_bar);
		mTextViewAnswer = (TextView) findViewById(R.id.text_answer);
//		int normal_text_size = 24;
//		mTextViewAnswer.setTextSize(normal_text_size);
		mContext = context;
		mIvMicRecognitionBg = (ImageView) findViewById(R.id.ivMicRecognitionBtn);
		mImageViewMicRecognize = (ImageView) findViewById(R.id.imageViewRecognize);
		
		mIvRecordingRecording = (ImageView) findViewById(R.id.iv_recording_recording);
		mIvMicException = (ImageView) findViewById(R.id.iv_mic_exception);
		mPbMicPrepare = (ProgressBar) findViewById(R.id.pb_mic_prepare);
		mRlMic = (RelativeLayout) findViewById(R.id.rl_mic);
		
		mRotateAnimationMicRecognize = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		mRotateAnimationMicRecognize.setDuration(1000);
		mRotateAnimationMicRecognize.setInterpolator(new LinearInterpolator());
		mRotateAnimationMicRecognize.setRepeatCount(Animation.INFINITE);
		boolean isWakeUp = UserPerferenceUtil.isWakeupEnable(mContext);
		mCancelBtn.setChecked(isWakeUp);
//		mTimer = new Timer(TAG + "_Volume_Timer");

	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		mBtnMic.setOnClickListener(l);
		mIvMicRecognitionBg.setOnClickListener(l);
		mCancelBtn.setOnCheckedChangeListener(mCbListener);
		mVoiceLevel.setOnClickListener(l);
	}

	
	private android.widget.CompoundButton.OnCheckedChangeListener mCbListener = 
			new android.widget.CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				Logger.d(TAG, "!--->cb_wakeup isChecked = "+isChecked);
				UserPerferenceUtil.setWakeupEnable(mContext, isChecked);
				sendWakeupConfigure();
		}
	};

	
	private void sendWakeupConfigure(){
		Intent intent = new Intent(mContext, WindowService.class);
		intent.setAction(WindowService.ACTION_SET_WAKEUP);
		mContext.startService(intent);
	}
	/**
	 * show mic result text
	 * @param text
	 */
	public void setAnswerText(String text) {
		Logger.d(TAG, "!--->---setAnswerText:text " + text);
		mTextViewAnswer.setText(text);
	}

	/**
	 * show mic result text
	 * @param textRes
	 */
	public void setAnswerText(int textRes) {
		Logger.d(TAG, "setAnswerText");
		mTextViewAnswer.setText(textRes);
	}

	public void setEnabled(boolean enable) {
		if (enable) {
			mBtnMic.setEnabled(true);
		} else {
			mBtnMic.setEnabled(false);
		}
	}

	/**
	 * show Mic Exception View
	 * XD added 20150807
	 * @param isShow
	 */
	private void showMicExceptionView(boolean isShow){
		Logger.d(TAG, "!--->showMicExceptionView---isShow:"+isShow);
		if (isShow) {
			mIvMicException.setVisibility(View.VISIBLE);
			mPbMicPrepare.setVisibility(View.INVISIBLE);
			mRlMic.setVisibility(View.INVISIBLE);
		} else {
			mIvMicException.setVisibility(View.GONE);
		}
	}
	
	/**
	 * showMicPrepareView
	 * @param isShow
	 */
	private void showMicPrepareView(boolean isShow){
		Logger.d(TAG, "!--->showMicPrepareView---isShow:"+isShow);
		if(isShow){
			mPbMicPrepare.setVisibility(View.VISIBLE);
			showMicExceptionView(false);
			mRlMic.setVisibility(View.INVISIBLE);
		}else{
			mPbMicPrepare.setVisibility(View.INVISIBLE);
			mRlMic.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * show 正在倾听 MicRecordingView
	 * @param isShow
	 */
	private void showMicRecordingView(boolean isShow){
		Logger.d(TAG, "!--->showMicRecordingView---isShow:"+isShow);
		if(isShow){
			mVoiceLevel.setVisibility(View.VISIBLE);
			mIvRecordingRecording.setVisibility(View.VISIBLE);
			setFlickerAnimation(mIvRecordingRecording);
		}else{
			mVoiceLevel.setVisibility(View.GONE);
			mIvRecordingRecording.clearAnimation();
			mIvRecordingRecording.setVisibility(View.GONE);
		}
	}
	
	
	private void showMicNormalBtn(boolean isShow){
		Logger.d(TAG, "!--->showMicNormalBtn---isShow:"+isShow);
		if(isShow){
			mBtnMic.setVisibility(View.VISIBLE);
			mBtnMic.setEnabled(true);
		}else{
			mBtnMic.setVisibility(View.GONE);
			mBtnMic.setEnabled(false);
		}
	}
	
	/**
	 * show 正在识别
	 * @param isShow
	 */
	private void showMicRecognizeView(boolean isShow){
		Logger.d(TAG, "!--->showMicRecognizeView---isShow:"+isShow);
		if (isShow) {
			mIvMicRecognitionBg.setVisibility(View.VISIBLE);
			showMicNormalBtn(false);
			
			mImageViewMicRecognize.setVisibility(View.VISIBLE);
			mImageViewMicRecognize.startAnimation(mRotateAnimationMicRecognize);
		}else{
			mIvMicRecognitionBg.setVisibility(View.GONE);
			
			mImageViewMicRecognize.clearAnimation();
			mImageViewMicRecognize.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Exception
	 * XD 20150807 added
	 */
	public void onException(){
		showMicExceptionView(true);
	}

	/**
	 * Mic Preparing
	 */
	public void onPrepare() {
		showMicPrepareView(true);
		
	}

	/**
	 * Mic Prepared ok
	 * @param resetMicrophoneText
	 */
	public void onIdle(boolean resetMicrophoneText) {
//		if (mTimerTask != null) {
//			mTimerTask.cancel();
//		}
		
		showMicExceptionView(false);
		showMicPrepareView(false);
		showMicRecordingView(false);
		showMicRecognizeView(false);
		
		showMicNormalBtn(true);
	//	mTextViewAnswer.setText(R.string.mic_prepare);//xd added 20150703
	}

	/**
	 * 开始接收声�? 正在倾听
	 */
	public void onRecording() {
		showMicExceptionView(false);
		showMicPrepareView(false);
		
		showMicNormalBtn(false);
		showMicRecognizeView(false);
		showMicRecordingView(true);

	//	mTextViewAnswer.setText(R.string.mic_recording);
		
//		if (mTimerTask != null) {
//			mTimerTask.cancel();
//		}
//		mTimerTask = new TimerTask() {
//
//			@Override
//			public void run() {
////				updateVolume((int) AssistantPreference.mRecordingVoiceVolume);
//			}
//		};
//		mTimer.scheduleAtFixedRate(mTimerTask, 0, 300);
	}
	
	
	/**
	 * 正在识别
	 */
	public void onProcess() {
//		if (mTimerTask != null) {
//			mTimerTask.cancel();
//		}
		Logger.d(TAG, "!--->---onProcess()-正在识别----");
		showMicExceptionView(false);
		showMicPrepareView(false);
		showMicRecordingView(false);
		showMicRecognizeView(true);
		
	//	mTextViewAnswer.setText(R.string.mic_processing);
	}

	/**
	 * update mic volume
	 * @param volume
	 */
	@SuppressWarnings("unused")
	private void updateVolume(int volume) {
		mVolume = volume;
		Logger.d(TAG, "!--->volume = "+volume);
		setVoiceLevel(mVolume);
	}

	/**
	 * 
	 * @param level
	 */
	public void setVoiceLevel(final int level) {
		long time=System.currentTimeMillis();
		if (time - mLastVolumeUpdateTime < GUIConfig.TIME_MIC_VOLUME_UPDATE) {
			Logger.d(TAG, "!--->setVoiceLevel---less than  "+GUIConfig.TIME_MIC_VOLUME_UPDATE+"ms, do not update. level = "+level);
			return;
		}
		mLastVolumeUpdateTime = time;
		Logger.d(TAG, "!--->setVoiceLevel---level = "+level);
		
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				//Logger.d(TAG, "!--->setVoiceLevel = "+level);
				mVoiceLevel.setPercent(level);
				// mMicSoundRender.startVoiceAnim();
				// mImageViewMicRecognizeBg.setVisibility(View.VISIBLE);
				
				//mImageViewMicRecognize.clearAnimation();
				mImageViewMicRecognize.setVisibility(View.GONE);
			}
		});

	}

	public void onDestroy() {
		// mMicSoundRender.onDestroy();
		// mMicSoundRender = null;
		setOnClickListener(null);
		
//		if (mTimerTask != null) {
//			mTimerTask.cancel();
//		}
//		if (mTimer != null) {
//			mTimer.cancel();
//			mTimer = null;
//		}

		mRotateAnimationMicRecognize = null;
	}
	
	/**
	 * Image Flicker Animation
	 * @param imageView
	 */
	private void setFlickerAnimation(ImageView imageView) {
		final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
		animation.setDuration(TIME_MINISECONDS_MIC_FLICKER); // duration - 1/5 a second
		animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		animation.setRepeatMode(Animation.REVERSE); // 
		imageView.setAnimation(animation);
    }
}
