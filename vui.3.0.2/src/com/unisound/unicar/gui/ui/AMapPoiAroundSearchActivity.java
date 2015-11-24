/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : AMapPoiAroundSearchActivity.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.ui
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-22
 */
package com.unisound.unicar.gui.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.msg.ISystemCallTransitionListener;
import com.unisound.unicar.gui.msg.SystemCallTransition;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.AMapPoiAroundSearchView;
import com.unisound.unicar.gui.view.MicrophoneControl;
import com.unisound.unicar.gui.view.SessionContainer;
import com.unisound.unicar.gui.view.SessionLinearLayout;

/**
 * TODO:
 * 
 * @author xiaodong.he
 * @date 20151022
 */
@SuppressLint("NewApi")
public class AMapPoiAroundSearchActivity extends Activity {

    private static final String TAG = AMapPoiAroundSearchActivity.class.getCanonicalName();

    private Context mContext;

    private AMapPoiAroundSearchView mAMapAroundSearchView;

    private SessionLinearLayout mViewRoot;
    private SessionContainer mSessionContainer;
    private MicrophoneControl mMicrophoneControl;

    private SystemCallTransition messageTransCenter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;

        mViewRoot = (SessionLinearLayout) View.inflate(this, R.layout.window_service_main, null);
        if (DeviceTool.getDeviceSDKVersion() > 13) {
            mViewRoot.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);// XD added
        } else {
            Logger.d(TAG, "!--->DeviceSDKVersion <= 13");
        }
        findViews(savedInstanceState);

        setContentView(mViewRoot);

        messageTransCenter = new SystemCallTransition();
        messageTransCenter.setMessageTransListener(mMessageTransListener);
    }

    private void findViews(Bundle savedInstanceState) {
        mSessionContainer = (SessionContainer) mViewRoot.findViewById(R.id.sessionContainer);
        mMicrophoneControl = (MicrophoneControl) mViewRoot.findViewById(R.id.microphoneControl);
        mMicrophoneControl.setOnClickListener(mMicrophoneClickListener);

        if (mAMapAroundSearchView == null) {
            mAMapAroundSearchView = new AMapPoiAroundSearchView(mContext);
            mAMapAroundSearchView.onCreate(savedInstanceState);
        }

        addAnswerView(mAMapAroundSearchView);
    }

    private void addAnswerView(View view) {
        mSessionContainer.removeAllSessionViews();
        mSessionContainer.addSessionView(mAMapAroundSearchView, true);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mAMapAroundSearchView.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        mAMapAroundSearchView.onSaveInstanceState(outState);
    }


    private ISystemCallTransitionListener mMessageTransListener =
            new ISystemCallTransitionListener() {

                @Override
                public void setState(int type) {}

                @Override
                public void onTalkRecordingPrepared() {
                    Logger.d(TAG, "!--->---onTalkRecordingPrepared---");
                    Message msg = new Message();
                    msg.what = WindowService.MSG_ON_RECORDING_PREPARED;
                    mUIHandler.sendMessage(msg);
                }

                @Override
                public void onTalkRecordingException() {
                    mUIHandler.sendEmptyMessage(WindowService.MSG_ON_RECORDING_EXCEPTION);
                }

                @Override
                public void onTalkRecordingStart() {
                    Logger.d(TAG, "!--->---onTalkRecordingStart---");
                    Message msg = new Message();
                    msg.what = WindowService.MSG_ON_RECORDING_START;
                    mUIHandler.sendMessage(msg);
                }

                @Override
                public void onTalkRecordingStop() {
                    Logger.d(TAG, "!--->---onTalkRecordingStop---");
                    Message msg = new Message();
                    msg.what = WindowService.MSG_ON_RECORDING_STOP;
                    mUIHandler.sendMessage(msg);
                }

                @Override
                public void onTalkResult(String result) {
                    Logger.d(TAG, "!--->onTalkResult---result = " + result);
                    Message msg = new Message();
                    msg.what = WindowService.MSG_ON_RECORDING_RESULT;
                    msg.obj = result;
                    mUIHandler.sendMessage(msg);
                }

                @Override
                public void onSessionProtocal(String protocol) {
                    Message msg = new Message();
                    msg.what = WindowService.MSG_ON_SESSION_PROTOCAL;
                    msg.obj = protocol;
                    mUIHandler.sendMessage(msg);
                }

                @Override
                public void onSendMsg(String msg) {
                    // sendMsg(msg);
                }

                @Override
                public void onPlayEnd() {
                    Message msg = new Message();
                    msg.what = WindowService.MSG_ON_TTS_PLAY_END;
                    mUIHandler.sendMessage(msg);
                }

                @Override
                public void onUpdateVolume(int volume) {
                    // Logger.d(TAG, "!--->onUpdateVolume---volume = "+volume);
                    mMicrophoneControl.setVoiceLevel(volume);
                }

                @Override
                public void onRecognizerTimeout() {
                    Message msg = new Message();
                    msg.what = WindowService.MSG_ON_RECOGNIZER_TIMEOUT;
                    mUIHandler.sendMessage(msg);
                }

                @Override
                public void onCTTCancel() {
                    Message msg = new Message();
                    msg.what = WindowService.MSG_ON_CTT_CANCEL;
                    mUIHandler.sendMessage(msg);
                }

                @Override
                public void onOneShotRecognizerTimeout() {
                    Logger.d(TAG, "!--->---onOneShotRecognizerTimeout---");
                    Message msg = new Message();
                    msg.what = WindowService.MSG_ON_ONESHOT_RECOGNIZER_TIMEOUT;
                    mUIHandler.sendMessage(msg);
                }

                @Override
                public void onStartFakeAnimation() {
                    Logger.d(TAG, "!--->onStartFakeAnimation---");
                    Message msg = new Message();
                    msg.what = WindowService.MSG_ON_START_RECORDING_FAKE_ANIMATION;
                    mUIHandler.sendMessage(msg);
                }

                @Override
                public void onGetWakeupWords(String wakeupWords) {}

                @Override
                public void onClickMainActionButton(int style) {}

                @Override
                public void onControlWakeupSuccess(String wakeupWord) {
                    // 唤醒成功
                    Logger.d(TAG, "!--->VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS wakeupWord : "
                            + wakeupWord);
                    Message msg = new Message();
                    msg.what = WindowService.MSG_ON_CONTROL_WAKEUP_SUCCESS;
                    msg.obj = wakeupWord;
                    mUIHandler.sendMessage(msg);
                }

                @Override
                public void onUpdateWakeupWordsStatus(String status) {}

                @Override
                public void onTalkRecodingIdle() {
                    Logger.d(TAG, "!--->onTalkRecodingIdle");
                    Message msg = new Message();
                    msg.what = WindowService.MSG_ON_RECORDING_IDLE;
                    mUIHandler.sendMessage(msg);
                }
            };

    private Handler mUIHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case WindowService.MSG_ON_RECORDING_PREPARED:
                    Logger.d(TAG, "!--->MSG_ON_RECORDING_PREPARED----");
                    // getGeneralGPS();
                    // mSessionManager.onTalkRecordingPrepared();
                    onTalkRecordingPrepared();
                    break;
                case WindowService.MSG_ON_RECORDING_EXCEPTION:
                    Logger.d(TAG, "!--->MSG_ON_RECORDING_EXCEPTION-----");
                    // mSessionManager.onTalkRecordingException();
                    break;
                case WindowService.MSG_ON_RECORDING_START:
                    Logger.d(TAG, "!--->MSG_RECORDING_START----");
                    onTalkRecordingStart();
                    break;
                case WindowService.MSG_ON_RECORDING_STOP:
                    Logger.d(TAG, "!--->MSG_ON_RECORDING_STOP----");
                    // 正在识别
                    // mSessionManager.onTalkRecordingStop();
                    onTalkRecordingStop();
                    break;
                default:
                    break;
            }
        }
    };

    private void onTalkRecordingPrepared() {
        mMicrophoneControl.setVisibility(View.VISIBLE);
        mMicrophoneControl.onPrepare();
    }

    private void onTalkRecordingStart() {
        mMicrophoneControl.setVisibility(View.VISIBLE);
        mMicrophoneControl.onRecording();
    }

    private void onTalkRecordingStop() {
        mMicrophoneControl.setVisibility(View.VISIBLE);
        mMicrophoneControl.onProcess();
        mMicrophoneControl.setEnabled(true);
    }

    private View.OnClickListener mMicrophoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Logger.d(TAG, "!--->mMicrophoneClickListener--onClick()---v.getId() = " + v.getId());
            if (v.getId() == R.id.cancelBtn) {
                Logger.d(TAG, "!--->----cancel Btn click");
                // cancelTalk();
            } else {
                // R.id.rl_mic Microphone button Click

                // mWindowService.sendPTTByWakeupSuccessType(); // XD modify 20150825
                // releaseOneShotSession();// xd added 20150731

            }
        }
    };

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mAMapAroundSearchView.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mAMapAroundSearchView.onDestroy();
    }


}
