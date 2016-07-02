/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : MicrophoneControl.java
 * @ProjectName : V1.0
 * @PakageName : com.unisound.unicar.gui.view
 * @Author : xiaodong.he
 * @CreateDate : 2015-6-15
 */
package com.unisound.unicar.gui.view;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.FunctionHelpUtil;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;

@SuppressLint({"HandlerLeak", "NewApi"})
public class MicrophoneControl extends MicrophoneControlBaseView {
    public static final String TAG = "MicrophoneControl";

    private ImageView mBtnMic, mCancelBtn;

    private MicrophoneViewClickListener mMicrophoneViewClickListener;

    public void setMicrophoneViewClickListener(MicrophoneViewClickListener listener) {
        mMicrophoneViewClickListener = listener;
    }

    private ImageView mIvMicException;// xiaodong added 20150807

    private ImageView mIvMicRecognitionBg;
    private ImageView mImageViewMicRecognize;

    private ImageView mIvRecordingRecording; // xiaodong added 20150615
    private ProgressBar mPbMicPrepare; // xiaodong added 20150623
    private RelativeLayout mRlMic;

    private RoundVoiceLevelView mVoiceLevel;
    private RotateAnimation mRotateAnimationMicRecognize;
    private TextView mTextViewAnswer;

    /* < xiaodong.he 20151019 added Begin */
    private LinearLayout mWakeupWordLayout;
    private TextView mTvWakeupStatus;
    private TextView mTvWakeupWord;
    private ImageView ivEditWakeupword;
    /* xiaodong.he 20151019 added End > */

 //   private int mVolume;
    // private Timer mTimer;
    // private TimerTask mTimerTask;

    private Timer mFakeRecordingTimer;
    private TimerTask mFakeRecordingTimerTask;

    private long mLastVolumeUpdateTime = 0;

    private Handler mHandler = new Handler();

    public MicrophoneControl(Context context) {
        this(context, null);
    }

    public MicrophoneControl(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MicrophoneControl(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View.inflate(context, R.layout.mic_control, this);
        mBtnMic = (ImageView) findViewById(R.id.btnMic);
        mCancelBtn = (ImageView) findViewById(R.id.cancelBtn);
        mVoiceLevel = (RoundVoiceLevelView) findViewById(R.id.my_voice_progress_bar);
        mTextViewAnswer = (TextView) findViewById(R.id.text_answer);
        mTextViewAnswer.setOnClickListener(mOnClickListener); // XD added 20151014

        mWakeupWordLayout = (LinearLayout) findViewById(R.id.ll_wakeup_word);
        mTvWakeupStatus = (TextView) findViewById(R.id.tv_wake_up_status);
        mTvWakeupWord = (TextView) findViewById(R.id.tv_wake_up_word);
        ivEditWakeupword = (ImageView) findViewById(R.id.iv_edit_wakeupword);

        if (GUIConfig.isSupportUpdateWakeupWordSetting) {
            // 下划线
            mTvWakeupWord.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            // 抗锯齿 added by LP at 2015-11-05
            mTvWakeupWord.getPaint().setAntiAlias(true);
            mTvWakeupWord.setOnClickListener(mOnClickListener);
            ivEditWakeupword.setOnClickListener(mOnClickListener);
            ivEditWakeupword.setVisibility(View.VISIBLE);
        } else {
            ivEditWakeupword.setVisibility(View.GONE);
        }

        mIvMicRecognitionBg = (ImageView) findViewById(R.id.ivMicRecognitionBtn);
        mImageViewMicRecognize = (ImageView) findViewById(R.id.imageViewRecognize);

        mIvRecordingRecording = (ImageView) findViewById(R.id.iv_recording_recording);
        mIvMicException = (ImageView) findViewById(R.id.iv_mic_exception);
        mPbMicPrepare = (ProgressBar) findViewById(R.id.pb_mic_prepare);
        mRlMic = (RelativeLayout) findViewById(R.id.rl_mic);

        mRotateAnimationMicRecognize =
                new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimationMicRecognize.setDuration(TIME_MINISECONDS_DURATION_RECOGNIZE);
        mRotateAnimationMicRecognize.setInterpolator(new LinearInterpolator());
        mRotateAnimationMicRecognize.setRepeatCount(Animation.INFINITE);

        // mTimer = new Timer(TAG + "_Volume_Timer");
        mFakeRecordingTimer = new Timer(TAG + "_Fake_Volume_Timer");
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mBtnMic.setOnClickListener(l);
        mIvMicRecognitionBg.setOnClickListener(l);
        if (l != null) {
            mCancelBtn.setOnClickListener(l);
        }
        mVoiceLevel.setOnClickListener(l);
    }

    /**
     * XD added 20151014
     */
    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_edit_wakeupword:
                case R.id.tv_wake_up_word:
                    if (null != mMicrophoneViewClickListener) {
                        mMicrophoneViewClickListener.onWakeupWordClick();
                    }
                    break;
                case R.id.text_answer:
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * show mic result text
     * 
     * @param text
     */
    public void setAnswerText(String text) {
        Logger.d(TAG, "!--->---setAnswerText:text " + text);
        hideWakeupStatusView();

        mTextViewAnswer.setText(text);
        mTextViewAnswer.setTextColor(getResources().getColor(R.color.color_asr_result_text));
    }

    /**
     * show Wakeup Status On Mic View when onRecording session view show
     * 
     * @param isWakeupOpen
     * @param wakeupWord
     */
    @Override
    public void showWakeupStatusOnMicView(boolean isWakeupOpen, String wakeupWord) {
        Logger.d(TAG, "!--->showWakeupStatusOnMicView--isWakeupOpen=" + isWakeupOpen
                + "; wakeupWord = " + wakeupWord);
        mWakeupWordLayout.setVisibility(View.VISIBLE);
        mTextViewAnswer.setVisibility(View.GONE);
        if (isWakeupOpen) {
            mTvWakeupStatus.setText(R.string.wakeup_status_mic_open);
            mTvWakeupWord.setVisibility(View.VISIBLE);
            if (GUIConfig.isSupportUpdateWakeupWordSetting) {
                ivEditWakeupword.setVisibility(View.VISIBLE);
            } else {
                ivEditWakeupword.setVisibility(View.GONE);
            }
            mTvWakeupWord.setText(FunctionHelpUtil.addDoubleQuotationMarks(wakeupWord));
        } else {
            mTvWakeupStatus.setText(R.string.wakeup_status_mic_closed);
            mTvWakeupWord.setVisibility(View.GONE);
            ivEditWakeupword.setVisibility(View.GONE);
        }
    }

    /**
     * hide Wakeup Status view
     */
    private void hideWakeupStatusView() {
        if (null != mWakeupWordLayout && mWakeupWordLayout.getVisibility() == View.VISIBLE) {
            mWakeupWordLayout.setVisibility(View.GONE);
        }
        if (null != mTextViewAnswer && mTextViewAnswer.getVisibility() != View.VISIBLE) {
            mTextViewAnswer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * show mic result text
     * 
     * @param textRes
     */
    public void setAnswerText(int textRes) {
        Logger.d(TAG, "setAnswerText");
        mTextViewAnswer.setText(textRes);
    }

    @Override
    public void setEnabled(boolean enable) {
        Logger.d(TAG, "setEnabled---enable = " + enable);
        // onIdle(false);
        if (enable) {
            mBtnMic.setClickable(true);
            mBtnMic.setEnabled(true);
            mBtnMic.setBackgroundResource(R.drawable.mic_exception);
        } else {
            mBtnMic.setClickable(false);
            mBtnMic.setEnabled(false);
            mBtnMic.setBackgroundResource(R.drawable.btn_mic);
        }
    }

    /**
     * show Mic Exception View XD added 20150807
     * 
     * @param isShow
     */
    private void showMicExceptionView(boolean isShow) {
        Logger.d(TAG, "!--->showMicExcepView---isShow:" + isShow);
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
     * 
     * @param isShow
     */
    private void showMicPrepareView(boolean isShow) {
        Logger.d(TAG, "!--->showMicPrepareView---isShow:" + isShow);
        if (isShow) {
            mPbMicPrepare.setVisibility(View.VISIBLE);
            showMicExceptionView(false);
            mRlMic.setVisibility(View.INVISIBLE);
        } else {
            mPbMicPrepare.setVisibility(View.INVISIBLE);
            mRlMic.setVisibility(View.VISIBLE);
        }
    }

    /**
     * show 正在倾听 MicRecordingView
     * 
     * @param isShow
     */
    private void showMicRecordingView(boolean isShow) {
        Logger.d(TAG, "!--->showMicRecordingView---isShow:" + isShow);
        if (isShow) {
            mVoiceLevel.setVisibility(View.VISIBLE);
            mIvRecordingRecording.setVisibility(View.VISIBLE);
            setFlickerAnimation(mIvRecordingRecording);
        } else {
            mVoiceLevel.setVisibility(View.GONE);
            mIvRecordingRecording.clearAnimation();
            mIvRecordingRecording.setVisibility(View.GONE);
        }
    }


    private void showMicNormalBtn(boolean isShow) {
        Logger.d(TAG, "!--->showMicNormalBtn---isShow:" + isShow);
        if (isShow) {
            mBtnMic.setVisibility(View.VISIBLE);
            mBtnMic.setEnabled(true);
        } else {
            mBtnMic.setVisibility(View.GONE);
            mBtnMic.setEnabled(false);
        }
    }

    /**
     * show 正在识别
     * 
     * @param isShow
     */
    private void showMicRecognizeView(boolean isShow) {
        Logger.d(TAG, "!--->showMicRecognizeView---isShow:" + isShow);
        if (isShow) {
            mIvMicRecognitionBg.setVisibility(View.VISIBLE);
            showMicNormalBtn(false);

            mImageViewMicRecognize.setVisibility(View.VISIBLE);
            mImageViewMicRecognize.startAnimation(mRotateAnimationMicRecognize);
        } else {
            mIvMicRecognitionBg.setVisibility(View.GONE);

            mImageViewMicRecognize.clearAnimation();
            mImageViewMicRecognize.setVisibility(View.GONE);
        }
    }

    /**
     * Exception XD 20150807 added
     */
    public void onException() {
        showMicExceptionView(true);
    }

    /**
     * Mic Preparing
     */
    public void onPrepare() {
        Logger.d(TAG, "!--->---onPrepare()-----");
        showMicPrepareView(true);

    }

    /**
     * Mic Prepared ok
     * 
     * @param resetMicrophoneText
     */
    public void onIdle(boolean resetMicrophoneText) {
        Logger.d(TAG, "!--->----onIdle()----Mic Prepared ok----");
        // if (mTimerTask != null) {
        // mTimerTask.cancel();
        // }
        stopRecordingFakeAnimation();

        showMicExceptionView(false);
        showMicPrepareView(false);
        showMicRecordingView(false);
        showMicRecognizeView(false);

        showMicNormalBtn(true);
        // mTextViewAnswer.setText(R.string.mic_prepare);//xd added 20150703
    }

    /**
     * 开始接收声音 正在倾听
     */
    public void onRecording() {
        Logger.d(TAG, "!--->---onRecording()-正在倾听----");
        showMicExceptionView(false);
        showMicPrepareView(false);

        showMicNormalBtn(false);
        showMicRecognizeView(false);
        showMicRecordingView(true);

        stopRecordingFakeAnimation();
        // mTextViewAnswer.setText(R.string.mic_recording);

        // if (mTimerTask != null) {
        // mTimerTask.cancel();
        // }
        // mTimerTask = new TimerTask() {
        //
        // @Override
        // public void run() {
        // // updateVolume((int) AssistantPreference.mRecordingVoiceVolume);
        // }
        // };
        // mTimer.scheduleAtFixedRate(mTimerTask, 0, 300);
    }

    /**
     * onStartRecordingFakeAnimation XD added 20151015
     */
    @Override
    public void onStartRecordingFakeAnimation() {
        Logger.d(TAG, "onStartRecordingFakeAnimation---do nothing--");
        // XD delete 20151020
        /*
         * onRecording(); startRecordingFakeAnimation();
         */
    }

    /**
     * 正在识别
     */
    public void onProcess() {
        // if (mTimerTask != null) {
        // mTimerTask.cancel();
        // }
        Logger.d(TAG, "!--->---onProcess()-正在识别----");
        stopRecordingFakeAnimation();

        showMicExceptionView(false);
        showMicPrepareView(false);
        showMicRecordingView(false);
        showMicRecognizeView(true);

        hideWakeupStatusView(); // xiaodong.he added 20151019
        // mTextViewAnswer.setText(R.string.mic_processing);
    }

    /**
     * startRecordingFakeAnimation XD added 20151015
     */
  /*  private void startRecordingFakeAnimation() {
        Logger.d(TAG, "startRecordingFakeAnimation-----");
        stopRecordingFakeAnimation();
        mFakeRecordingTimerTask = new TimerTask() {

            @Override
            public void run() {
                Random rd = new Random();
                setVoiceLevel(rd.nextInt(30));
            }
        };

        mFakeRecordingTimer.scheduleAtFixedRate(mFakeRecordingTimerTask, 0, 200);
    }
*/
    /**
     * stopRecordingFakeAnimation XD added 20151015
     */
    private void stopRecordingFakeAnimation() {
        if (mFakeRecordingTimerTask != null) {
            Logger.d(TAG, "stopRecordingFakeAnimation-----");
            mFakeRecordingTimerTask.cancel();
        }
    }

    /**
     * update mic volume
     * 
     * @param volume
     */
  /*  private void updateVolume(int volume) {
        mVolume = volume;
        Logger.d(TAG, "!--->volume = " + volume);
        setVoiceLevel(mVolume);
    }*/

    /**
     * 
     * @param level
     */
    public void setVoiceLevel(final int level) {
        long time = System.currentTimeMillis();
        if (time - mLastVolumeUpdateTime < GUIConfig.TIME_MIC_VOLUME_UPDATE) {
            Logger.d(TAG, "!--->setVoiceLevel---less than  " + GUIConfig.TIME_MIC_VOLUME_UPDATE
                    + "ms, do not update. level = " + level);
            return;
        }
        mLastVolumeUpdateTime = time;
        Logger.d(TAG, "!--->setVoiceLevel---level = " + level);

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // Logger.d(TAG, "!--->setVoiceLevel = "+level);
                mVoiceLevel.setPercent(level);
                // mMicSoundRender.startVoiceAnim();
                // mImageViewMicRecognizeBg.setVisibility(View.VISIBLE);

                // mImageViewMicRecognize.clearAnimation();
                mImageViewMicRecognize.setVisibility(View.GONE);
            }
        });

    }

    public void onDestroy() {
        // mMicSoundRender.onDestroy();
        // mMicSoundRender = null;
        setOnClickListener(null);

        // if (mTimerTask != null) {
        // mTimerTask.cancel();
        // }
        // if (mTimer != null) {
        // mTimer.cancel();
        // mTimer = null;
        // }

        stopRecordingFakeAnimation();
        if (mFakeRecordingTimer != null) {
            mFakeRecordingTimer.cancel();
            mFakeRecordingTimer = null;
        }

        mRotateAnimationMicRecognize = null;
    }

    /**
     * Image Flicker Animation
     * 
     * @param imageView
     */
    private void setFlickerAnimation(ImageView imageView) {
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to
                                                              // invisible
        animation.setDuration(TIME_MINISECONDS_MIC_FLICKER); // duration - 1/5 a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); //
        imageView.setAnimation(animation);
    }

    public interface MicrophoneViewClickListener {
        public void onWakeupWordClick();
    }

}
