package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.KnowledgeMode;
import com.unisound.unicar.gui.phone.PhoneStateReceiver;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.preference.UserPreference;
import com.unisound.unicar.gui.sms.SmsItem;
import com.unisound.unicar.gui.sms.SmsNewObserver;
import com.unisound.unicar.gui.sms.SmsNewObserver.ISMSReceiver;
import com.unisound.unicar.gui.ui.GUIMainActivity;
import com.unisound.unicar.gui.ui.SettingsViewPagerActivity;
import com.unisound.unicar.gui.ui.WelcomeActivity;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.FunctionHelpUtil;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.GuiProtocolUtil;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.PackageUtil;
import com.unisound.unicar.gui.view.FunctionHelpHeadListView;
import com.unisound.unicar.gui.view.IKnowButtonContentView;
import com.unisound.unicar.gui.view.IKnowButtonContentView.IIKonwButtonContentViewListener;
import com.unisound.unicar.gui.view.LoadViewLinearLayout;
import com.unisound.unicar.gui.view.LoadViewLinearLayout.DispatchKeyEventListener;
import com.unisound.unicar.gui.view.MicRecordingSessionView;
import com.unisound.unicar.gui.view.MicrophoneControl;
import com.unisound.unicar.gui.view.MicrophoneControlBaseView;
import com.unisound.unicar.gui.view.SessionContainer;

/**
 * @Module : Session层核心类
 * @Comments : 根据协议进行Session生成和协议分发，该类为Session层的核心枢纽
 * @Author : Dancindream
 * @CreateDate : 2014-4-1
 * @ModifiedBy : xiaodong
 * @ModifiedDate: 2015-6-23
 * @Modified: 2015-6-23: 实现基本功能
 */
/*
 * TTS完成度:接听电话,产品形态
 */
@SuppressLint({"HandlerLeak", "InflateParams", "NewApi"})
public class GUISessionManager {
    // public class SessionManager {
    private static final String TAG = GUISessionManager.class.getSimpleName();
    WindowService mWindowService = null;
    /* < xiaodong 20150623 added for mic status view begin */
    private View mMicStatusView = null;
    TextView mTvSessionMicStatus = null;
    /* xiaodong 20150623 added for mic status view End > */
    private FunctionHelpHeadListView mFunctionView = null; // FunctionView xd modify 20150703
    private SessionContainer mSessionViewContainer = null;
    private MicrophoneControlBaseView mMicrophoneControl = null;
    private BaseSession mCurrentSession = null;
    private UserPreference mPreference;
    private boolean mLastSessionDone = false;
    private String mSessionStatus = "";
    private String mType = "";
    private String mOriginType = null;

    LoadViewLinearLayout mLoadingView; // xd modify 20150706
    // View mWelcomeView; //xd added 20150706
    private boolean isOpenDoubleClick = false;
    private long firstTimeBackKeyClick = 0;
    private static final long TIME_DOUBLE_CLICK_BACK = 2000; // 2s

    private SmsNewObserver mSMSObserver;
    private SmsItem mReveivedMessage;
    /* < XD 20150817 added Begin */
    private int mSmsSendStatus = GUIConfig.SMS_STATUS_SENDING;
    private IKnowButtonContentView mSmsSendStatusView = null;
    /* XD 20150817 added End > */

    private String mNumber;
    private int mPhoneState;

    /**
     * if waiting session is canceled, do not create GUI Sesion anymore XD added 20150805
     */
    private boolean isWaitingSessionCanceled = false;

    /* < XD 20150906 added for ASR partial result Begin */
    private String mASRResultText = "";
    private View mASRResultSessionView = null;

    /* XD 20150906 added for ASR partial result End > */

    /**
     * setMicrophoneControl XD added 20150827
     * 
     * @param micControl
     */
    public void setMicrophoneControl(MicrophoneControlBaseView micControl) {
        mMicrophoneControl = micControl;
        mMicrophoneControl.setOnClickListener(mMicrophoneClickListener);
    }

    /**
     * onStartRecordingFakeAnimation XD added 20151015
     */
    private boolean answerTxtNoPhoneNum = true;

    public void onStartRecordingFakeAnimation() {
        Logger.d(TAG, "!--->onStartRecordingFakeAnimation--------");
        setEnableMicrophoneControl(false);
        mMicrophoneControl.onStartRecordingFakeAnimation();
        String answerTxt = "";
        if (SessionPreference.VALUE_TYPE_SOME_PERSON.equals(mType)
                || SessionPreference.VALUE_TYPE_SOME_NUMBERS.equals(mType)
                || SessionPreference.VALUE_TYPE_MUTIPLE_LOCALSEARCH.equals(mType)
                || SessionPreference.VALUE_MUTIPLE_LOCATION.equals(mType)) {
            answerTxt = mWindowService.getString(R.string.mutiple_ansewer_text);
        } else if (SessionPreference.VALUE_TYPE_CALL_ONE_NUMBER.equals(mType)) {
            answerTxt = mWindowService.getString(R.string.call_confirm_ansewer_text);
        } else if (SessionPreference.VALUE_SCHEDULE_TYPE_LOCALSEARCH_SHOW_ITEM.equals(mType)) {
            if (answerTxtNoPhoneNum) {
                answerTxt =
                        mWindowService
                                .getString(R.string.localSearch_confirm_no_phone_ansewer_text);
            } else {
                answerTxt = mWindowService.getString(R.string.localSearch_confirm_ansewer_text);
            }

        }
        showAsrResultTextOnMicView(answerTxt);
    }

    /**
     * 
     * @param isEnable
     */
    public void setEnableMicrophoneControl(boolean isEnable) {
        Logger.d(TAG, "setEnableMicrophoneControl--isEnable = " + isEnable);
        if (isEnable) {
            mMicrophoneControl.setEnabled(true);
        } else {
            mMicrophoneControl.setEnabled(false);
            // SC added 20151014
        }
        setEnableMicrophoneControlListener(isEnable);
    }

    /**
     * set Enable MicrophoneControlListener
     * 
     * @description only set Enable MicrophoneControl Listener
     * @author xiaodong.he
     * @date 2015-11-9
     * @param isEnable : if false, show Microphone normal status and remove Click Listener
     */
    public void setEnableMicrophoneControlListener(boolean isEnable) {
        Logger.d(TAG, "setEnableMicrophoneControlListener--isEnable = " + isEnable);
        if (isEnable) {
            mMicrophoneControl.setOnClickListener(mMicrophoneClickListener);
        } else {
            mMicrophoneControl.setOnClickListener(null);
        }
    }

    /**
     * @Description : TODO 提供主线程的方法调用
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     */
    private Handler mSessionManagerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Logger.d(TAG, "handleMessage:what " + SessionPreference.getMessageName(msg.what));
            switch (msg.what) {
                case SessionPreference.MESSAGE_START_LOCALSEARCH_NAVI:
                    mWindowService.sendStartLocalSearchNaviEvent();// xd added 20150701
                    onGUISessionDone();
                    break;
                case SessionPreference.MESSAGE_START_LOCALSEARCH_CALL:
                    mWindowService.sendStartLocalSearchCallEvent();
                    onGUISessionDone();
                    break;
                // 开始语音识别流程
                case SessionPreference.MESSAGE_SESSION_CANCEL:
                    // GUI Clicked Cancel button
                    /* < XD 20150813 modify for cancel button click begin */
                    // mWindowService.sendCancelEvent();
                    // onGUISessionDone();
                    mWindowService.onCancelTalk();
                    /* XD 20150813 modify for cancel button click End > */
                    break;
                case SessionPreference.MESSAGE_WAITING_SESSION_CANCEL:
                    // XD 20150813 modify for cancel button click on waiting session

                    onWaitingSessionCancel();
                    break;
                // Session操作流程已完成
                case SessionPreference.MESSAGE_SESSION_DONE:
                    Logger.d(TAG, "!--->mSessionManagerHandler----MESSAGE_SESSION_DONE");
                    onGUISessionDone();
                    break;
                // Session操作流程已完成 - Delay dismiss window service
                case SessionPreference.MESSAGE_SESSION_DONE_DELAY:
                    Logger.d(TAG, "!--->mSessionManagerHandler----MESSAGE_SESSION_DONE_DELAY");
                    onGUISessionDoneDelay();
                    break;
                case SessionPreference.MESSAGE_SESSION_RELEASE_ONLY:
                    // release current session don't dismiss window, XD 20151123 added for
                    // experience mode
                    releaseCurrentSessionOnly();
                    break;
                case SessionPreference.MESSAGE_SESSION_DISMISS_WINDOW_SERVICE:
                    Logger.d(TAG,
                            "!--->mSessionManagerHandler----MESSAGE_SESSION_DISMISS_WINDOW_SERVICE");
                    dismissWindowServiceOnly();
                    break;
                // TODO 添加答句文字（语音魔方回答的文字）
                case SessionPreference.MESSAGE_ADD_ANSWER_TEXT:
                    String text = (String) msg.obj;
                    Logger.d(TAG, "!--->---MESSAGE_ADD_ANSWER_TEXT---text:" + text);
                    // mSessionViewContainer.addAnswerView(text);
                    // mMicrophoneControl.setAnswerText(text); //XD delete 20150730
                    break;
                // TODO 添加回答的View（语音魔方需要展现的View）
                case SessionPreference.MESSAGE_ADD_ANSWER_VIEW:
                    View view = (View) msg.obj;
                    boolean fullScroll = msg.arg1 != 0;
                    Logger.d(TAG, "!--->----------MESSAGE_ADD_ANSWER_VIEW----msg.arg1 = "
                            + msg.arg1 + "-- fullScroll = " + fullScroll);
                    mSessionViewContainer.removeAllSessionViews();
                    mSessionViewContainer.addSessionView(view, fullScroll);
                    // LayoutParams params = new LayoutParams(width, height,
                    // gravity);
                    // mSessionViewContainer.addSessionView(view, params);
                    break;
                case SessionPreference.MESSAGE_ADD_SESSION_LIST_VIEW:
                    // xd added 20150716
                    Logger.d(TAG, "!--->---MESSAGE_ADD_SESSION_LIST_VIEW");
                    View listView = (View) msg.obj;
                    mSessionViewContainer.removeAllSessionViews();
                    mSessionViewContainer.addSessionListView(listView);
                    break;
                // TODO 添加问句文字（用户说的话）
                case SessionPreference.MESSAGE_ADD_QUESTION_TEXT:
                    // mSessionViewContainer.addQustionView((String) msg.obj);
                    // mMicrophoneControl.setAnswerText((String) msg.obj);
                    break;
                // TODO 返还给语音魔方的协议（用于业务流程跳转）
                case SessionPreference.MESSAGE_UI_OPERATE_PROTOCAL:
                    Bundle bundle = (Bundle) msg.obj;
                    String eventName = bundle.getString(SessionPreference.KEY_BUNDLE_EVENT_NAME);
                    String protocal = bundle.getString(SessionPreference.KEY_BUNDLE_PROTOCAL);
                    Logger.d(TAG, "!--->MESSAGE_UI_OPERATE_PROTOCAL eventName=" + eventName
                            + "; protocal : " + protocal);
                    mWindowService.sendProtocolEvent(eventName, protocal); // xd added 20150702
                    break;
                case SessionPreference.MESSAGE_SEND_RESP_PROTOCAL:
                    Bundle bundleResp = (Bundle) msg.obj;
                    String respParam = bundleResp.getString(SessionPreference.KEY_PARAMS);

                    String respState =
                            bundleResp.getString(SessionPreference.PARAM_KEY_STATE_INCOMING);
                    Logger.d(TAG, "!--->MESSAGE_SEND_RESP_PROTOCAL respParam : " + respParam
                            + ";respState = " + respState);
                    if (mCurrentSession == null) {
                        respParam = null;
                    }
                    // >0有问题
                    if (respParam != null) {
                        mWindowService.sendResponse(respParam, respState);
                    } else {
                        mWindowService.sendResponse(respParam);
                    }
                    break;
                case SessionPreference.MESSAGE_SMS_SENDING:
                    Logger.d(TAG, "!--->MESSAGE_SMS_SENDING");
                    showSmsSendStatusView(GUIConfig.SMS_STATUS_SENDING);
                    releaseCurrentSession();
                    break;
                case SessionPreference.MESSAGE_ANSWER_CALL:
                    Logger.d(TAG, "!--->MESSAGE_ANSWER_CALL");
                    onGUISessionDone();
                    break;
                case SessionPreference.MESSAGE_REJECT_CALL:
                    Logger.d(TAG, "!--->MESSAGE_REJECT_CALL");
                    onGUISessionDone();
                    break;
                case SessionPreference.MESSAGE_SHOW_EDIT_LOCATION_POP:
                    Logger.d(TAG, "!--->MESSAGE_SHOW_EDIT_LOCATION_POP");
                    mWindowService.showEditLocationPopWindow(mWindowService);
                    break;
                case SessionPreference.MESSAGE_UPDATE_MIC_EANBLE_STATE:
                    boolean isEnable = (Boolean) msg.obj;
                    Logger.d(TAG, "MESSAGE_UPDATE_MIC_EANBLE_STATE--isEnable = " + isEnable);
                    setEnableMicrophoneControl(isEnable);
                    break;
                case SessionPreference.MESSAGE_DIMISS_WELCOME_ACTIVITY:
                    // XD added 20151102
                    sendDismissWelcomeActivityBroadcast();
                    break;
                default:
                    break;
            }
        }

    };


    /**
     * The bell is not quiet
     */

    public void onNormalWakeUpSuccess() {
        Logger.d(TAG, "!--->onNormalWakeUpSuccess");
        // mSessionManagerHandler.removeMessages(SessionPreference.MESSAGE_SESSION_DISMISS_WINDOW_SERVICE);
        // //XD added 20150819

        releaseCurrentSession(); // XD added 20151029

        mWindowService.show();
        mMicrophoneControl.setVisibility(View.VISIBLE);

        // mMicrophoneControl.onIdle(true);
        mMicrophoneControl.onPrepare();
        if (isNeedShowMicStatusSessionView()) {
            // mSessionViewContainer.removeAllSessionViews(); //XD 20150821 delete
            // showHelpView(); //xd modify
            showMicStatusView(mWindowService.getString(R.string.mic_prepare));// xd added
        }
    }

    public void onControlWakeUpSuccess(String wakeupWord) {
        Logger.d(TAG, "!--->onControlWakeUpSuccess wakeupWord : " + wakeupWord);
        Toast.makeText(mWindowService, "wakeupWord : " + wakeupWord, Toast.LENGTH_LONG).show();
    }

    public void onWakeUpSuccessDoreso() {
        Logger.d(TAG, "!--->onWakeUpSuccessDoreso");
        mWindowService.show();
        mMicrophoneControl.setVisibility(View.VISIBLE);
        mMicrophoneControl.onPrepare();
        if (isNeedShowMicStatusSessionView()) {
            showMicStatusView(mWindowService.getString(R.string.mic_prepare_doreso));
        }
    }

    /**
     * @Description : TODO 唤醒功能抽象回调
     * @Author : Dancindream
     * @CreateDate : 2014-4-1
     */
    public void onWakeUpInitDone() {
        Logger.d(TAG, "!--->mWakeupListener.onInitDone");
        showInitView(false); // xd added 20150623
    }

    public GUISessionManager(WindowService windowService, SessionContainer sessionViewContainer,
            MicrophoneControl microphoneControl) {
        mWindowService = windowService;
        mSessionViewContainer = sessionViewContainer;
        mMicrophoneControl = microphoneControl;
        init();
        setListener();

    }

    /**
     * 
     * bind framework service & framework init GUI init show init prepare view
     */
    private void init() {
        mPreference = new UserPreference(mWindowService);
        /* < xd modify 20150706 Begin */
        // mWelcomeView = View.inflate(mWindowService, R.layout.activity_welcome, null);
        mSMSObserver = new SmsNewObserver(mWindowService);

        mLoadingView =
                (LoadViewLinearLayout) View.inflate(mWindowService, R.layout.view_loading, null);
        if (DeviceTool.getDeviceSDKVersion() > 13) {
            mLoadingView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);// XD added
        } else {
            Logger.d(TAG, "!--->DeviceSDKVersion <= 13");
        }
        mLoadingView.setDispatchKeyEventListener(mLoadViewDispatchKeyEventListener);
        /* xd modify 20150706 End > */
        showMicStatusView(mWindowService.getString(R.string.mic_prepare));
    }

    /**
     * mLoadView DispatchKeyEventListener xd added 20150706
     */
    private DispatchKeyEventListener mLoadViewDispatchKeyEventListener =
            new DispatchKeyEventListener() {

                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    Logger.d(TAG,
                            "!--->loadingView dispatchKeyEvent()---keyCode = " + event.getKeyCode());
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        long secondTime = System.currentTimeMillis();
                        if (isOpenDoubleClick
                                && (secondTime - firstTimeBackKeyClick > TIME_DOUBLE_CLICK_BACK)) {
                            Logger.d(TAG, "!--->--SHIW TOAST--");
                            Toast.makeText(mWindowService, R.string.toast_click_again_to_exit,
                                    Toast.LENGTH_SHORT).show();
                            firstTimeBackKeyClick = secondTime;
                            return true;
                        } else {
                            quitFromLoadingView();
                            return true;
                        }
                    }
                    return false;
                }
            };

    /**
     * xd added 20150706 quit from LoadingView
     */
    private void quitFromLoadingView() {
        Logger.d(TAG, "!--->quitFromLoadingView()----");
        showLoadingView(false, false, "", "");

        dismissWelcomeActivity();
        // showWelcomeView(false);

        dismissGUIMainActivity();
    }

    /**
     * show WelcomeView
     * 
     * @param isShow
     */
    // public void showWelcomeView(boolean isShow) {
    // Logger.d(TAG, "!--->showWelcomeView()------isShow = " + isShow);
    // if (!isShow) {
    // mWindowService.dimissView(mWelcomeView);
    // Logger.d(TAG, "!--->showWelcomeView() dismiss");
    // } else {
    // Logger.d(TAG, "!--->showWelcomeView() removeAllSessionViews");
    //
    // mSessionViewContainer.removeAllSessionViews();
    // mMicrophoneControl.setVisibility(View.GONE);
    // mMicrophoneControl.onPrepare();
    // mSessionViewContainer.removeAllSessionViews();
    // TextView tvVersion = (TextView)
    // mWelcomeView.findViewById(R.id.tv_version);
    // tvVersion.setText("V"+DeviceTool.getAppVersionName(mWindowService));
    // mWindowService.addPrepareView(mWelcomeView);
    // }
    // }

    /**
     * show init Loading View (数据准备中...)
     * 
     * @param isShow
     */
    public void showInitView(boolean isShow) {
        Logger.d(TAG, "!--->showInitView()------isShow = " + isShow);
        showLoadingView(isShow, false, mWindowService.getString(R.string.grammar_data_compile), "");
    }

    /**
     * show init failed View XD added 20150804
     * 
     * @param isShow
     * @param errorMsg
     */
    public void showInitFailedView(String errorMsg) {
        Logger.d(TAG, "!--->showInitFailedView()---------");
        showLoadingView(true, true, mWindowService.getString(R.string.error_init_title_failed),
                errorMsg);
    }

    /**
     * show VUI Service Not Installed View xd added 20150706
     * 
     * @param isShow
     */
    // public void showVUIServiceNotInstalledView(boolean isShow) {
    // showLoadingView(isShow,
    // mWindowService.getString(R.string.error_vui_service_not_install),"");
    // }

    /**
     * show init Loading View
     * 
     * @param isShow
     * @param isFailed
     * @param titleText
     * @param tipText
     */
    private void showLoadingView(boolean isShow, boolean isFailed, String titleText, String tipText) {
        Logger.d(TAG, "!--->showLoadingView()---isShow = " + isShow + "; isFailed = " + isFailed
                + "; titleText = " + titleText + "; tipText = " + tipText);
        if (isShow) {
            Logger.d(TAG, "!--->showLoadingView() removeAllSessionViews");
            dismissWelcomeActivity(); // xd added 20150706
            // showWelcomeView(false); //xd added 20150706

            mSessionViewContainer.removeAllSessionViews();
            mMicrophoneControl.setVisibility(View.GONE);
            mMicrophoneControl.onPrepare();
            mSessionViewContainer.removeAllSessionViews();

            // XD 20150921 added for test version
            ImageView testVersionMark =
                    (ImageView) mLoadingView.findViewById(R.id.iv_test_version_mark);
            testVersionMark.setVisibility(GUIConfig.isTestVersion ? View.VISIBLE : View.GONE);

            TextView tvTite = (TextView) mLoadingView.findViewById(R.id.init_text);
            tvTite.setText(titleText);

            ProgressBar pbInit = (ProgressBar) mLoadingView.findViewById(R.id.progress_bar_init);
            TextView tvTips = (TextView) mLoadingView.findViewById(R.id.init_text_tips);
            if (isFailed) {
                pbInit.setVisibility(View.GONE);
            } else {
                pbInit.setVisibility(View.VISIBLE);
            }
            if (TextUtils.isEmpty(tipText)) {
                tvTips.setVisibility(View.GONE);
            } else {
                tvTips.setVisibility(View.VISIBLE);
                tvTips.setText(tipText);
            }

            mWindowService.addPrepareView(mLoadingView);
        } else {
            mWindowService.dimissView(mLoadingView);
            Logger.d(TAG, "!--->loadingView  dismiss");
        }
    }


    /**
     * dismiss WelcomeActivity xd added 20150706
     */
    private void dismissWelcomeActivity() {
        String topActivity = PackageUtil.getTopActivityName(mWindowService);
        Logger.d(TAG, "dismissWelcomeActivity----topActivity = " + topActivity);
        if (WelcomeActivity.class.getName().equals(topActivity)) {
            sendDismissWelcomeActivityBroadcast();
        } else {
            Logger.w(TAG,
                    "dismissWelcomeActivity---WelcomeActivity is not show, dismiss it 5s delay.");
            mSessionManagerHandler.sendEmptyMessageDelayed(
                    SessionPreference.MESSAGE_DIMISS_WELCOME_ACTIVITY, 5000);
        }
    }

    /**
     * 
     */
    private void sendDismissWelcomeActivityBroadcast() {
        Logger.d(TAG, "sendDismissWelcomeActivityBroadcast----");
        mWindowService.sendBroadcast(new Intent(WelcomeActivity.ACTION_FINISH_WELCOMEACTIVITY));
    }

    /**
     * dismiss GUIMainActivity xd added 20150706
     */
    private void dismissGUIMainActivity() {
        Logger.d(TAG, "!--->disGUIMainActivity()----------");
        mWindowService.sendBroadcast(new Intent(GUIMainActivity.ACTION_FINISH_GUIMAINACTIVITY));
    }

    /**
     * 
     * @param status
     */
    private void showMicStatusView(String status) {
        Logger.d(TAG, "!--->showMicStatusView()-----status = " + status + "; mCurrentSession = "
                + mCurrentSession);
        if (mCurrentSession != null && !(mCurrentSession instanceof IKnowButtonSession)) {
            Logger.d(TAG,
                    "!--->mCurrentSession is not null && is not IKnowButtonSession, no need show MicStatusView");
            return;
        }
        View view = null;
        mSessionViewContainer.removeAllSessionViews();
        view = getMicStatusView(status);
        view.setVisibility(View.VISIBLE);

        addAnswerView(view);
    }

    /**
     * show FunctionHelp View
     */
    private void showHelpView() {
        Logger.d(TAG, "!--->showHelpView----");
        View view = null;
        mSessionViewContainer.removeAllSessionViews();
        view = getFunctionView();
        view.setVisibility(View.VISIBLE);

        addSessionViewWithoutScrolling(view);
    }

    /**
     * xd added 20150716 for FunctionHelpHeadListView
     * 
     * @param view
     */
    private void addSessionViewWithoutScrolling(View view) {
        Logger.d(TAG, "!--->----addSessionViewWithoutScrolling()---view = " + view);
        Message msg = new Message();
        msg.what = SessionPreference.MESSAGE_ADD_SESSION_LIST_VIEW;
        msg.obj = view;
        mSessionManagerHandler.sendMessage(msg);
    }

    /**
     * XD 20150817 added
     * 
     * @param eventName
     * @param protocal
     */
    protected void onUiProtocal(String eventName, String protocal) {
        Logger.d(TAG, "onUiProtocal : " + protocal);
        Message msg = new Message();
        msg.what = SessionPreference.MESSAGE_UI_OPERATE_PROTOCAL;

        Bundle bundle = new Bundle();
        bundle.putString(SessionPreference.KEY_BUNDLE_EVENT_NAME, eventName);
        bundle.putString(SessionPreference.KEY_BUNDLE_PROTOCAL, protocal);
        msg.obj = bundle;

        mSessionManagerHandler.sendMessage(msg);
    }

    /**
     * show Sms SentSuccess View xd added 20150709 XD 20150818 modify for SMS status show & status
     * to VUI
     */
    public void showSmsSendStatusView(int status) {
        Logger.d(TAG, "!--->showSmsSendStatusView()------status = " + status);
        mSmsSendStatus = status;
        String msg = mWindowService.getString(R.string.sms_sending);
        switch (status) {
            case GUIConfig.SMS_STATUS_SENDING:
                msg = mWindowService.getString(R.string.sms_sending);
                break;
            case GUIConfig.SMS_STATUS_SEND_SUCCESS:
                msg = mWindowService.getString(R.string.sms_sent_success);
                // send sms sent status to VUI play TTS
                // onUiProtocal(SessionPreference.EVENT_NAME_SMS_SENT_STATUS,
                // SessionPreference.EVENT_PROTOCAL_SMS_STATUS_SUCCESS);
                onUiProtocal(SessionPreference.EVENT_NAME_PLAY_TTS,
                        GuiProtocolUtil.getPlayTTSEventParam(msg,
                                GuiProtocolUtil.EVENT_PARAM_KEY_TTS_END_WAKEUP));
                break;
            case GUIConfig.SMS_STATUS_SEND_FAIL:
                msg = mWindowService.getString(R.string.sms_sent_fail);
                // send sms sent status to VUI play TTS
                // onUiProtocal(SessionPreference.EVENT_NAME_SMS_SENT_STATUS,
                // SessionPreference.EVENT_PROTOCAL_SMS_STATUS_FAIL);
                onUiProtocal(SessionPreference.EVENT_NAME_PLAY_TTS,
                        GuiProtocolUtil.getPlayTTSEventParam(msg,
                                GuiProtocolUtil.EVENT_PARAM_KEY_TTS_END_WAKEUP));
                break;
            default:
                break;
        }

        boolean isNeedAddView = false;
        if (status == GUIConfig.SMS_STATUS_SENDING) {
            mSmsSendStatusView = new IKnowButtonContentView(mWindowService);
            isNeedAddView = true;
        } else {
            if (mSmsSendStatusView == null) {
                mSmsSendStatusView = new IKnowButtonContentView(mWindowService);
                isNeedAddView = true;
            }
        }

        mSmsSendStatusView.setShowText(msg);
        mSmsSendStatusView.setListener(mIKonwButtonListener);
        if (isNeedAddView) {
            addAnswerView(mSmsSendStatusView);
        }
    }

    private IIKonwButtonContentViewListener mIKonwButtonListener =
            new IIKonwButtonContentViewListener() {
                @Override
                public void onOk() {
                    Logger.d(TAG, "!--->----onOk()---");
                    mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
                }
            };

    /**
     * xd added 20150709
     * 
     * @param view
     */
    private void addAnswerView(View view) {
        Logger.d(TAG, "!--->----addAnswerView()---view = " + view);
        Message msg = new Message();
        msg.what = SessionPreference.MESSAGE_ADD_ANSWER_VIEW;
        msg.obj = view;
        mSessionManagerHandler.sendMessage(msg);
    }

    private void setListener() {
        Logger.d(TAG, "!--->setListener()-------");
        mMicrophoneControl.setOnClickListener(mMicrophoneClickListener);

        /* < xd added 20150710 begin */
        PhoneStateReceiver.registerPhoneStateListener(mPhoneStateListener);
        mSMSObserver.setMessageReveiverListener(mSMSReceiverListener);
        mSMSObserver.registReceiver();
        /* xd added 20150710 End > */
    }

    /**
     * Phone State Listener xd added 20150710
     */
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

        public void onCallStateChanged(int state, String incomingNumber) {
            Logger.d(TAG, "mPhoneStateListener--onCallStateChanged-- state =" + state
                    + ",incomingNumber = " + incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Logger.d(TAG, "!--->--TelephonyManager.CALL_STATE_RINGING--");
                    if (mCurrentSession != null) {
                        if (mCurrentSession instanceof RouteWaitingSession
                                || mCurrentSession instanceof LocalsearchWaitingSession) {
                            Message msg = new Message();
                            msg.what = SessionPreference.MESSAGE_SEND_RESP_PROTOCAL;
                            Bundle bundle = new Bundle();
                            bundle.putString(SessionPreference.KEY_PARAMS, "");
                            msg.obj = bundle;
                            mSessionManagerHandler.sendMessageDelayed(msg, 0);
                        }
                    }
                    mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);// XD
                                                                                                    // modify
                                                                                                    // 20150805
                    // TODO: send event to VUI service param:incomingNumber
                    // after received callback Json then show UI
                    // String personName = mTalkServicePresentor.getName(incomingNumber);
                    // if (TextUtils.isEmpty(personName)) {
                    // text = incomingNumber +
                    // mWindowService.getString(R.string.phone_answer_cancel);
                    // mName = mWindowService.getString(R.string.stranger);
                    // } else {
                    // text = personName + mWindowService.getString(R.string.phone_answer_cancel);
                    // mName = personName;
                    // }
                    // Logger.d(TAG, "--personName-->" + personName);
                    mNumber = incomingNumber;

                    // {"type":"EVENT","data":{"moduleName":"GUI","eventName":"INCOMING_CALL"},"param":{"service":"cn.yunzhisheng.setting","semantic":{"intent":{"confirm":"INCOMING_CALL"}},"code":"SETTING_EXEC","number":"10086"}}
                    String eventName = SessionPreference.EVENT_NAME_INCOMING_CALL;
                    // "{\"service\":\"cn.yunzhisheng.setting\",\"semantic\":{\"intent\":{\"confirm\":\"INCOMING_CALL\"}},\"code\":\"SETTING_EXEC\",\"type\":\"10086\"}"
                    String protocal =
                            "{\"service\":\"cn.yunzhisheng.setting\",\"semantic\":{\"intent\":{\"confirm\":\"INCOMING_CALL\"}},\"code\":\"SETTING_EXEC\",\"number\":\""
                                    + mNumber + "\",\"rc\":0}";
                    Logger.d(TAG, "!--->--RINGING--eventName=" + eventName + "; protocal : "
                            + protocal);
                    mWindowService.sendProtocolEvent(eventName, protocal); // xd added 20150702

                    mWindowService.setNeedHideMicFloatView(true);// xd added 20150811
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Logger.d(TAG, "!--->--TelephonyManager.CALL_STATE_OFFHOOK--");
                    // mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    mWindowService.sendStopWakeupWordEvent();
                    mWindowService.setNeedHideMicFloatView(true);// xd added 20150811
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Logger.d(TAG, "!--->--TelephonyManager.CALL_STATE_IDLE--mPhoneState = "
                            + mPhoneState);
                    mWindowService.sendResetWakeupWordEvent();
                    mWindowService.setNeedHideMicFloatView(false);// xd added 20150811
                    if (mPhoneState == TelephonyManager.CALL_STATE_RINGING) {
                        // cancelTalk(false);
                        // text = mWindowService.getString(R.string.call_canceled);
                        // mMicrophoneControl.setAnswerText(text);
                        // playTTSWithEndRunnable(text, new Runnable() {
                        // @Override
                        // public void run() {
                        // cancelSession();
                        // }
                        // });
                    }

                    // mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    break;
            }
            mPhoneState = state;
        };
    };

    /**
     * SMS Receiver Listener xd added 20150710
     */
    private ISMSReceiver mSMSReceiverListener = new ISMSReceiver() {

        @Override
        public void onMessageReveived(SmsItem msg) {
            // cancelSession();

            Logger.d(TAG, "!--->--onMessageReveived:msg " + msg);
            mReveivedMessage = msg;
            String tts = "";
            String name = msg.getName();
            String number = msg.getNumber();
            String content = msg.getMessage();
            if (name != null && !name.equals("")) {
                tts = msg.getName();
            } else {
                tts = msg.getNumber();
                mReveivedMessage.setNumber(tts);
            }
            Logger.d(TAG, "!--->--Receive message--name = " + name + "; number" + number
                    + "; content = " + tts);
            sendReceivedSmsEvent(number, content);
        }
    };

    /**
     * 
     * @param number
     * @param content
     */
    private void sendReceivedSmsEvent(String number, String content) {
        String eventName = SessionPreference.EVENT_NAME_INCOMING_SMS;
        // "{\"service\":\"cn.yunzhisheng.setting\",\"semantic\":{\"intent\":{\"confirm\":\"INCOMING_CALL\"}},\"code\":\"SETTING_EXEC\",\"type\":\"10086\"}"
        String protocal =
                "{\"service\":\"cn.yunzhisheng.setting\",\"semantic\":{\"intent\":{\"confirm\":\"INCOMING_SMS\"}},\"code\":\"SETTING_EXEC\",\"number\":\""
                        + number + "\",\"content\":\"" + content + "\",\"rc\":0}";
        Logger.d(TAG, "!--->MESSAGE_UI_OPERATE_PROTOCAL eventName=" + eventName + "; protocal : "
                + protocal);
        mWindowService.sendProtocolEvent(eventName, protocal); // xd added 20150702
    }

    /**
	 * 
	 */
    private View.OnClickListener mMicrophoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Logger.d(TAG, "!--->Microphone--onClick--id = " + v.getId());
            switch (v.getId()) {
                case R.id.cancelBtn:
                    Logger.d(TAG, "!--->Microphone--onClick--cancel Btn");
                    cancelTalk();
                   if(mWindowService!=null){
	                   Intent intent = new Intent(mWindowService, SettingsViewPagerActivity.class);
	           		   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                   mWindowService.startActivity(intent);
                   }
                   
                    break;
                case R.id.my_voice_progress_bar:
                    Logger.d(TAG, "!--->Microphone--onClick--voice Btn");
                    onMicBtnClick();
                    break;
                case R.id.btnMic:
                    Logger.d(TAG, "!--->Microphone--onClick--btnMic");
                    onMicBtnClick();
                    break;
                case R.id.ivMicRecognitionBtn:
                    Logger.d(TAG, "!--->Microphone--onClick--ivMicRecognitionBtn");
                    onMicBtnClick();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 
     * @author xiaodong.he
     * @date 2015-11-16
     */
    private void onMicBtnClick() {
        Logger.d(TAG, "onMicBtnClick----");
        // XD added 20151019
        if (mCurrentSession != null && mCurrentSession instanceof IKnowButtonSession) {
            mCurrentSession.onNewSessionStart();
        }

        mWindowService.sendPTTByWakeupSuccessType(); // XD modify 20150825
        releaseOneShotSession();// xd added 20150731
    }

    /**
     * isOneShotSession XD added 20150731
     * 
     * @param currentSession
     * @return
     */
    private boolean isOneShotSession(BaseSession currentSession) {
        if (currentSession == null) {
            return false;
        }
        if (currentSession instanceof WeatherShowSession
                || currentSession instanceof SettingSession
                || currentSession instanceof StockShowSession
                || currentSession instanceof TrafficShowSession
                || currentSession instanceof TrafficControlShowSession) {
            return true;
        }
        return false;
    }

    /**
     * release One Shot Session When Click Session Mic Btn XD added 20150731
     */
    private void releaseOneShotSession() {
        if (null == mCurrentSession) {
            return;
        }
        if (isOneShotSession(mCurrentSession)) {
            Logger.d(TAG, "!--->releaseOneShotSession----mCurrentSession = " + mCurrentSession);
            releaseCurrentSession();
        }
    }

    public void startTalk() {
        mWindowService.sendPTT();
    }

    /**
     * This method called on mic cancel button click or back key click. send cancel event: CTT
     * cancel GUI session dismiss WindowService XD 20150813 modify do it on WindowService mUIHandler
     * to keep message sequence
     */
    public void cancelTalk() {
        // mMicrophoneControl.showChangeLocationView(false, "");
        Logger.d(TAG, "!--->cancelTalk()----mCurrentSession = " + mCurrentSession);
        if (mCurrentSession != null && (mCurrentSession instanceof RouteWaitingSession)
                || mCurrentSession instanceof LocalsearchWaitingSession) {
            sendWaitingSessionCancelResp();
            onWaitingSessionCancel();
        } else {
            mWindowService.onCancelTalk();

            // mWindowService.sendCancelEvent();
            // cancelSession();
            // mWindowService.dismiss();
        }
    }

    /**
     * cancel GUI session dismiss WindowService xiaodong added 20150701 & 20150819 replaced by
     * onGUISessionDone()
     */
    /*
     * public void dismissWindowService() { Logger.d(TAG, "!--->dismissWindowService-----");
     * showAsrResultTextOnMicView(""); //xd added 20150731 cancelSession();
     * mWindowService.dismiss(); }
     */

    /**
     * CALLED : MESSAGE_SESSION_DONE GUI session end : cancel GUI session dismiss WindowService XD
     * 20150819 modify
     */
    private void onGUISessionDone() {
        Logger.d(TAG, "!--->onGUISessionDone---");
        // setSessionEnd(true); //releaseCurrentSession()---> setSessionEnd(false);
        // releaseCurrentSession(); //this method has been called on cancelSession()
        // dismissWindowService();

        // mMicrophoneControl.showChangeLocationView(false, "");// add tyz 20151013
        resetRecordingResultText();
        showAsrResultTextOnMicView(""); // xd added 20150731
        cancelSession();
        mWindowService.dismiss();

        // XD 20150930 added
        // sendPlayerControlCmd(mWindowService, CommandPreference.CMD_NAME_PLAY,
        // CommandPreference.CMD_FROM_ON_SESSION_DONE);
    }

    /* < XD 20150819 added for GUI session done delay Begin */
    /**
     * onGUISessionDoneDelay
     */
    private void onGUISessionDoneDelay() {
        Logger.d(TAG, "!--->onGUISessionDoneDelay---");
        resetRecordingResultText();
        showAsrResultTextOnMicView("");
        releaseCurrentSession();
        mSessionManagerHandler.sendEmptyMessageDelayed(
                SessionPreference.MESSAGE_SESSION_DISMISS_WINDOW_SERVICE,
                GUIConfig.TIME_DELAY_DISMISS_VIEW_ON_TTS_END);
    }

    /**
     * release current session only but don't dismiss window for experience mode
     * 
     * @author xiaodong.he
     * @date 2015-11-23
     */
    private void releaseCurrentSessionOnly() {
        Logger.d(TAG, "releaseCurrentSessionOnly---");
        resetRecordingResultText();
        showAsrResultTextOnMicView("");
        releaseCurrentSession();
    }

    /**
     * dismiss Window Service Only for GUI session has done
     */
    private void dismissWindowServiceOnly() {
        Logger.d(TAG, "!--->dismissWindowServiceOnly---");
        if (null != mSessionViewContainer) {
            mSessionViewContainer.removeAllSessionViews();
        }
        mWindowService.dismiss();
    }

    /* XD 20150819 added for GUI session done delay End > */

    /**
     * session canceled on Waiting Session XD 20150813 added
     */
    private void onWaitingSessionCancel() {
        Logger.d(TAG, "!--->onWaitingSessionCancel------");
        isWaitingSessionCanceled = true;
        mWindowService.onWaittingSessionCancel();// TYZ added 20150811, XD 20150813 modify
        // onGUISessionDone(); //do this onCTTCancel() system call
    }

    /**
     * sendWaitingSessionCancelResp XD 20150813 added
     */
    private void sendWaitingSessionCancelResp() {
        Logger.d(TAG, "!--->sendWaitingSessionCancelResp----");
        Message msg = new Message();
        msg.what = SessionPreference.MESSAGE_SEND_RESP_PROTOCAL;
        Bundle bundle = new Bundle();
        bundle.putString(SessionPreference.KEY_PARAMS, "");
        bundle.putString(SessionPreference.PARAM_KEY_STATE_INCOMING, "FAIL");
        msg.obj = bundle;
        mSessionManagerHandler.sendMessage(msg);
    }

    // /**
    // * @Description : TODO 主动停止录音
    // * @Author : Dancindream
    // * @CreateDate : 2014-4-1
    // */
    // public void stopTalk() {
    // // TODO:
    // Logger.d(TAG, "stopTalk");
    // waitForRecognitionResult();
    // }

    public void cancelSession() {
        Logger.d(TAG, "!--->cancelSession");
        releaseCurrentSession();
        if (null != mSessionViewContainer) {
            mSessionViewContainer.removeAllSessionViews();
        }
    }

    /**
     * is Need Keep Window on Experience Version Mode
     * 
     * if true, when user say "cancel" on these session, the Window should not dismiss
     * 
     * @author xiaodong.he
     * @date 2015-11-24
     * @return
     */
    private boolean isNeedKeepWindowOnExpVersionMode() {
        if (UserPerferenceUtil.getVersionMode(mWindowService) == UserPerferenceUtil.VALUE_VERSION_MODE_EXP
                && mCurrentSession != null
                && (mCurrentSession instanceof MainMapLocationSession
                        || mCurrentSession instanceof MainMapAroundSearchSession
                        || mCurrentSession instanceof MultipleLocationSession
                        || mCurrentSession instanceof LocalSearchShowSession
                        || mCurrentSession instanceof LocalSearchItemShowSession
                        || mCurrentSession instanceof RouteWaitingSession
                        || mCurrentSession instanceof LocalsearchWaitingSession
                        || mCurrentSession instanceof TalkShowMiddleSession
                        || mCurrentSession instanceof MultiplePersonsShowSession
                        || mCurrentSession instanceof MultipleNumbersShowSession || mCurrentSession instanceof CallConfirmShowSession)) {
            return true;
        } else {
            return false;
        }
    }

    // /**
    // * @Description : TODO 等待识别结果流程
    // * @Author : Dancindream
    // * @CreateDate : 2014-4-1
    // */
    // private void waitForRecognitionResult() {
    // Logger.d(TAG, "!--->waitForRecognitionResult");
    // mMicrophoneControl.onProcess();
    // mMicrophoneControl.setEnabled(true);
    // }

    public void onPause() {
        Logger.d(TAG, "onPause");
    }

    public void onResume() {
        Logger.d(TAG, "onResume");
        // if (mTalkServicePresentor != null) {
        // mTalkServicePresentor.onResume();
        // requestStartWakeup(mWakeupInitDone, mRecognitionInitDone,
        // mRecognitionRecordingState);
        // }
    }

    // 初始化完成
    public void onTalkInitDone() {
        Logger.d(TAG, "!--->onTalkInitDone");
        mMicrophoneControl.onIdle(true);
        // mMicrophoneControl.setEnabled(true);

        if (mFunctionView != null) {
            mFunctionView.initFunctionViews();
        }
    }

    /**
     * 语法数据编译完成 dimissView loadingView & show GUIMainActivity, if is first start, first start has
     * finished.
     */
    public void onTalkDataDone() {
        Logger.d(TAG, "!--->onTalkDataDone()");
        // if ((TalkService.TALK_INITDONE|mRecognitionIintDone) &&
        // mRecognitionDataDone) { //xd delete
        if (mLoadingView.isShown()) {
            Logger.d(TAG, "!--->loadingView.isShown()---dimissView");
            mWindowService.dimissView(mLoadingView);
        }
        // else {
        // mWindowService.startFloatFirst();
        // }

        // start GUIMainActivity & finish WelcomeActivity
        Logger.d(TAG, "!--->onTalkDataDone()----sendBroadcast finish WelcomeActivity");
        dismissWelcomeActivity();
    }

    /**
     * is Need Show Mic Status Session View XD 20150724 added for Mic Recording view do not show
     * sometimes
     * 
     * @return
     */
    private boolean isNeedShowMicStatusSessionView() {
        Logger.d(TAG, "!--->isNeedShowMicStatusSessionView()----mLastSessionDone = "
                + mLastSessionDone + "; mSessionStatus = " + mSessionStatus + "; mType = " + mType);
        if (mLastSessionDone
                || TextUtils.isEmpty(mSessionStatus)
                || SessionPreference.VALUE_SESSION_END.equals(mSessionStatus)
                || (SessionPreference.VALUE_SESSION_SHOW.equals(mSessionStatus) && SessionPreference.VALUE_TYPE_UNSUPPORT_END_SHOW
                        .equals(mType))) {
            Logger.d(TAG, "!--->isNeedShowMicStatusSessionView: true");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Mic exception
     */
    public void onTalkRecordingException() {
        Logger.d(TAG, "!--->onTalkRecordingException---------");
        mWindowService.show();
        mMicrophoneControl.setVisibility(View.VISIBLE);
        mMicrophoneControl.onException();
        if (isNeedShowMicStatusSessionView()) {
            // mSessionViewContainer.removeAllSessionViews(); //XD 20150821 delete
            showMicStatusView(mWindowService.getString(R.string.mic_exception));
        }
    }

    /**
     * 正在准备
     */
    public void onTalkRecordingPrepared() {
        Logger.d(TAG, "!--->onTalkRecordingPrepared---------");
        // XD added20150824
        mSessionManagerHandler
                .removeMessages(SessionPreference.MESSAGE_SESSION_DISMISS_WINDOW_SERVICE);

        mWindowService.show();
        mMicrophoneControl.setVisibility(View.VISIBLE);
        mMicrophoneControl.onPrepare(); // onIdle(true); XD modify 20150723
        if (isNeedShowMicStatusSessionView()) {
            // mSessionViewContainer.removeAllSessionViews(); //XD 20150821 delete
            // showHelpView(); //xd modify
            showMicStatusView(mWindowService.getString(R.string.mic_prepare));
        }
    }

    /**
     * oneShot VAD timeOut
     */
    public void onOneShotRecognizerTimeOut() {
        Logger.d(TAG, "!--->onOneShotRecognizerTimeOut()---------");
        mWindowService.show();
        mMicrophoneControl.setVisibility(View.VISIBLE);
        mMicrophoneControl.onPrepare();
        if (isNeedShowMicStatusSessionView()) {
            String wakeupWord = UserPerferenceUtil.getWakeupWord(mWindowService);
            showMicStatusView(mWindowService.getString(R.string.mic_oneshot_recoginzer_timeout,
                    wakeupWord));
        }
    }

    /**
     * 正在倾听
     */
    public void onTalkRecordingStart() {
        Logger.d(TAG, "!--->onTalkRecordingStart()----isCanShowChangeLocationView = "
                + isCanShowChangeLocationView());
        mSessionManagerHandler
                .removeMessages(SessionPreference.MESSAGE_SESSION_DISMISS_WINDOW_SERVICE); // LP
                                                                                           // added
                                                                                           // 2050824
        resetRecordingResultText();// XD added 20150906

        if (!isCanShowChangeLocationView()) {// XD added 20151015
            showAsrResultTextOnMicView("");// XD added 20150731
        }

        // sendPlayerControlCmd(mWindowService,
        // CommandPreference.CMD_NAME_PAUSE,
        // CommandPreference.CMD_FROM_ON_RECORDING_START); //XD added 20150929

        mWindowService.show();
        mMicrophoneControl.setVisibility(View.VISIBLE);
        mMicrophoneControl.onRecording();
        if (isNeedShowMicStatusSessionView()) {
            // mSessionViewContainer.removeAllSessionViews(); //XD 20150821 delete
            String statusText = mWindowService.getString(R.string.mic_recording);
            if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO == mWindowService
                    .getWakeupSuccessType()) {
                statusText = mWindowService.getString(R.string.music_recording);
            }
            showMicStatusView(statusText);// xd added
            showWakeupStatusOnMicView(); // xiaodong.he 20151019 added
        }
    }

    public void onTalkRecordingIdle() {
        Logger.d(TAG, "!--->onTalkRecordingIdle()---------");
        mMicrophoneControl.setVisibility(View.VISIBLE);
        mMicrophoneControl.onIdle(true);
    }

    /**
     * showWakeupStatusOnMicView
     * 
     * xiaodong.he 20151019 added Begin
     */
    private void showWakeupStatusOnMicView() {
        if (mCurrentSession != null && !(mCurrentSession instanceof IKnowButtonSession)) {
            return;
        }
        Logger.d(TAG, "showWakeupStatusOnMicView");
        boolean isWakeupOpen = UserPerferenceUtil.isWakeupEnable(mWindowService);
        String wakeupWord =
                FunctionHelpUtil.addDoubleQuotationMarks(UserPerferenceUtil
                        .getWakeupWord(mWindowService));
        mMicrophoneControl.showWakeupStatusOnMicView(isWakeupOpen, wakeupWord);
    }

    /**
     * send Player Control Cmd XD 20150930 added {@see CommandPreference}
     * 
     * @param context
     * @param cmdName: CommandPreference.CMD_NAME_PAUSE, CommandPreference.CMD_NAME_PLAY
     * @param cmdForm: CommandPreference.CMD_FROM_ON_RECORDING_START or ...
     */
  /*  private void sendPlayerControlCmd(Context context, String cmdName, String cmdForm) {
        Intent intent = new Intent(CommandPreference.SERVICECMD);
        intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDPAUSE);
        intent.putExtra(CommandPreference.CMD_KEY_FROM, cmdForm);
        context.sendBroadcast(intent);
    }*/


    /**
     * 正在识别...
     */
    public void onTalkRecordingStop() {
        Logger.d(TAG, "!--->onTalkRecordingStop()----");
        mMicrophoneControl.onProcess();
        // mMicrophoneControl.setEnabled(true);

        // XD mark: why mMicrophoneControl.setOnClickListener(mMicrophoneClickListener); here?
        setEnableMicrophoneControl(true);

        String statusText = mWindowService.getString(R.string.mic_processing);
        if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO == mWindowService
                .getWakeupSuccessType()) {
            statusText = mWindowService.getString(R.string.music_processing);
        }
        showMicStatusView(statusText);// xd added
    }

    /**
     * ASR Recording Result XD added 20150731
     * 
     * @param result
     * @param isPartial
     */
    public void onTalkRecordingResult(String result, boolean isPartial) {
        if (!GUIConfig.isShowASRRecordResult) {
            Logger.d(TAG, "onTalkRecordingResult--isShowASRRecordResult false---result = " + result);
            return;
        }
        result = getRecordingResultByResultType(result, isPartial);// XD added 20150906
        Logger.d(TAG, "onTalkRecordingResult----result = " + result + "; isPartial = " + isPartial
                + "; isNeedShowMicStatusSessionView = " + isNeedShowMicStatusSessionView());
        if (isNeedShowMicStatusSessionView()) {
            showAsrResultTextOnSessionView(result, isPartial);
        } else {
            showAsrResultTextOnMicView(result);
        }

        if (!isPartial) {
            mMicrophoneControl.onIdle(true); // XD added 20150820
        }
    }

    /**
     * XD added 20150906
     * 
     * @param text
     * @param isPartial
     * @return
     */
    private String getRecordingResultByResultType(String text, boolean isPartial) {
        if (isPartial) {
            mASRResultText = mASRResultText + text;
        } else {
            mASRResultText = text;
        }
        return mASRResultText;
    }

    /**
     * XD added 20150906
     */
    private void resetRecordingResultText() {
        mASRResultText = "";
        mASRResultSessionView = null;
    }

    public void onTTSPlayEnd() {
        Logger.d(TAG, "!--->onTTSPlayEnd---mCurrentSession = " + mCurrentSession + "; mType = "
                + mType + "; mSessionStatus = " + mSessionStatus);
        if (mCurrentSession != null) {
            mCurrentSession.onTTSEnd();
        }

        /* < XD 20150817 added for SMS send status Begin */
        if (mCurrentSession == null && mSmsSendStatusView != null
                && mSmsSendStatus != GUIConfig.SMS_STATUS_SENDING) {
            Logger.d(TAG, "!--->onTTSPlayEnd dismiss mSmsSendStatus view.");
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
            mSmsSendStatusView = null;
        }
        /* XD 20150817 added for SMS send status End > */
        /* < XD 20151104 modify for fix BUG-4534 received SMS SCHEDULE_ERROR SESSION_END Begin */
        // if (isCurrentSesssonReleased()) {
        // Logger.d(TAG, "!--->onTTSPlayEnd session has been released, GUISessionDone.");
        // mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        // }
        /* < XD 20150824 added for received SMS SCHEDULE_ERROR Begin */
        // XD 20151104 mark: ReceiveSmsSession has been released, no use anymore
        if (isReceiveSmsSessionTimeOut()) {
            Logger.d(TAG, "!--->onTTSPlayEnd ReceiveSmsSession SCHEDULE_ERROR, GUISessionDone.");
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        }
        /* XD 20150824 added for received SMS SCHEDULE_ERROR End > */
        /* XD 20151104 modify for fix BUG-4534 received SMS SCHEDULE_ERROR SESSION_END End > */
    }

    /**
     * isReceiveSmsSession
     * 
     * @author xiaodong.he
     * @time 2015-11-10
     * @return
     */
    private boolean isReceiveSmsSession() {
        Logger.d(TAG, "isReceiveSmsSession--mType = " + mType + "; mCurrentSession = "
                + mCurrentSession);
        if (mCurrentSession != null && mCurrentSession instanceof ReceiveSmsSession
                && SessionPreference.VALUE_SCHEDULE_TYPE_INCOMING_SMS.equals(mType)) {
            Logger.d(TAG, "isReceiveSmsSessionTimeOut true");
            return true;
        } else {
            return false;
        }
    }

    /**
     * isReceiveSmsSessionTimeOut
     * 
     * @author xiaodong.he
     * @time 2015-11-9
     * @return
     */
    private boolean isReceiveSmsSessionTimeOut() {
        Logger.d(TAG, "isReceiveSmsSessionTimeOut--mType = " + mType + "; mCurrentSession = "
                + mCurrentSession);
        if (mCurrentSession != null && mCurrentSession instanceof ReceiveSmsSession
                && SessionPreference.VALUE_SCHEDULE_TYPE_SCHEDULE_ERROR.equals(mType)) {
            Logger.d(TAG, "isReceiveSmsSessionTimeOut true");
            return true;
        } else {
            return false;
        }
    }

    /**
	 * 
	 */
    public void onSessionProtocal(String protocol) {
        Logger.d(TAG, "!--->onSessionProtocal: protocol " + protocol);
        createSession(protocol);
    }

    public void onRecognizerTimeout() {
        Logger.d(TAG, "!--->onRecognizerTimeout()---MESSAGE_SESSION_DONE");
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    }

    public void onCTTCancel() {
        Logger.d(TAG, "!--->onCTTCancel()");
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    }

    private void releaseCurrentSession() {
        Logger.d(TAG, "releaseCurrentSession--mCurrentSession = " + mCurrentSession + "; mType = "
                + mType + "; mSessionStatus = " + mSessionStatus);
        setSessionEnd(true);// TODO: why setSessionEnd false? Is any problem?
        if (mCurrentSession != null) {
            mCurrentSession.release();
            mCurrentSession = null;
            /* XD 20150724 added begin > */
            mType = "";
            mSessionStatus = "";
            /* XD 20150724 added End > */
        }
    }

    /**
     * has CurrentSessson been Released
     * 
     * @author xiaodong.he
     * @date 2015-11-4
     * @return
     */
 /*   private boolean isCurrentSesssonReleased() {
        Logger.d(TAG, "!--->isCurrentSesssonReleased---mCurrentSession = " + mCurrentSession
                + "; mType = " + mType + "; mSessionStatus = " + mSessionStatus
                + "; mLastSessionDone = " + mLastSessionDone);
        if (mCurrentSession == null
                && mSessionStatus == ""
                && mLastSessionDone == true
                && (mType == "" || SessionPreference.VALUE_SCHEDULE_TYPE_SCHEDULE_ERROR
                        .equals(mType))) {
            return true;
        } else {
            return false;
        }
    }*/

    public void onDestroy() {
        Logger.d(TAG, "onDestroy");
        cancelSession();
        mSessionViewContainer.removeAllSessionViews();
        mMicrophoneControl.onDestroy();
        mMicrophoneControl = null;

        if (mFunctionView != null) {
            mFunctionView.release();
            mFunctionView = null;
        }

        /* < xd added 20150710 begin */
        PhoneStateReceiver.unregisterPhoneStateListener(mPhoneStateListener);
        mPhoneStateListener = null;
        mSMSObserver.unregistReceiver();
        mSMSObserver.setMessageReveiverListener(null);
        mSMSReceiverListener = null;
        mSMSObserver = null;
        mReveivedMessage = null;
        /* xd added 20150710 End> */
    }

    public boolean onBackPressed() {
        Logger.d(TAG, "!--->onBackPressed");
        boolean result = false;
        // cancelTTS();
        if (mCurrentSession != null) {
            mCurrentSession = null;
            mSessionViewContainer.removeAllSessionViews();
            String ttsString =
                    KnowledgeMode.getKnowledgeAnswer(mWindowService,
                            KnowledgeMode.KNOWLEDGE_STAGE_HELP);
            mSessionViewContainer.addAnswerView(ttsString);
            View view = getMicStatusView(mWindowService.getString(R.string.mic_prepare));
            mSessionViewContainer.addSessionView(view, false);
            result = true;
        }

        return result;
    }

    public void onNetWorkChanged() {
        Logger.d(TAG, "!--->onNetWorkChanged");
        if (mFunctionView != null) {
            mFunctionView.initFunctionViews();
        }
    }

    private void setSessionEnd(boolean end) {
        Logger.d(TAG, "setSessionEnd:end " + end);
        mLastSessionDone = end;
    }

    /**
     * @Description : TODO 核心方法，根据协议生成Session对象，并处理协议分发
     * @Author :
     * @CreateDate : 2014-4-1
     * @param protocal
     */


    private void createSession(String protocal) {
        Logger.d(TAG, "!--->createSession--:protocal " + protocal);
        // mMicrophoneControl.showChangeLocationView(false, "");
        JSONObject obj = JsonTool.parseToJSONObject(protocal);
        BaseSession base = null;
        boolean isNeedClear = true;
        mLastSessionDone = false;

        setEnableMicrophoneControlListener(true); // XD added 20151109
        if (obj != null) {
            JSONObject data = JsonTool.getJSONObject(obj, SessionPreference.KEY_DATA);
            mSessionStatus = JsonTool.getJsonValue(obj, SessionPreference.KEY_DOMAIN, "");
            if (SessionPreference.VALUE_SESSION_END.equals(mSessionStatus)) {
                String sType =
                        JsonTool.getJsonValue(data, SessionPreference.KEY_DATA_SCHEDULE_TYPE, "");
                if (isReceiveSmsSession()) {
                    Logger.d(TAG,
                            "createSession--isReceiveSmsSession, do not release last Session.");
                } else if (SessionPreference.VALUE_SCHEDULE_TYPE_ACTION_CANCEL.equals(sType)
                        && UserPerferenceUtil.getVersionMode(mWindowService) == UserPerferenceUtil.VALUE_VERSION_MODE_EXP) {
                    // XD 20151124 added for Experience Version Mode
                    Logger.d(TAG, "createSession--ACTION_CANCEL on Experience Version Mode.");
                } else {
                    Logger.d(TAG,
                            "createSession--the incoming session domain is SESSION_END, release last session");
                    releaseCurrentSession();
                }
            }
            // XD 20151110 mark: this code must after releaseCurrentSession
            mType = JsonTool.getJsonValue(data, SessionPreference.KEY_DATA_SCHEDULE_TYPE, "");
            mOriginType = JsonTool.getJsonValue(obj, SessionPreference.KEY_ORIGIN_TYPE, "");

            /* < xd 20150730 added Begin */
            String resultText = JsonTool.getJsonValue(data, SessionPreference.KEY_TEXT, "");
            // showAsrResultTextOnMicView(resultText);
            /* xd 20150730 added End > */
            Logger.d(TAG, "!--->createSession---mType = " + mType + "; resultText = " + resultText
                    + "; mSessionStatus = " + mSessionStatus);
            // TODO 如果是一个新领域流程的开始，是否需要将历史记录清理（参数控制）
            if (SessionPreference.VALUE_SESSION_BENGIN.equals(mSessionStatus)
                    && mPreference.getEnableCleanViewWhenSessionBegin()) {
                mSessionViewContainer.removeAllSessionViews();
            }

            // TODO 根据type进行实例创建
            if (SessionPreference.VALUE_SCHEDULE_TYPE_UNSUPPORT.equals(mType)) {
                Logger.d(TAG, "!--->-----VALUE_SCHEDULE_TYPE_UNSUPPORT----");
                // base = new FunctionHelpSession(mWindowService, mSessionManagerHandler);
                isNeedClear = false;
                // base = new UnsupportShowSession(mWindowService, mSessionManagerHandler);
                if (mCurrentSession == null) {
                    showHelpView();
                }
                // showHelpView();
                // releaseCurrentSession();
                // mMicrophoneControl.onIdle(true); //added 20150703
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_SCHEDULE_ERROR.equals(mType)) {
                Logger.d(TAG, "!--->--TODO:---VALUE_SCHEDULE_TYPE_SCHEDULE_ERROR----");
                if (mCurrentSession != null) {
                    /*
                     * if ((SessionPreference.DOMAIN_ROUTE).equals(mOriginType)) {
                     * mMicrophoneControl.showChangeLocationView(true, ""); }
                     */
                    Logger.d(TAG, "!--->mCurrentSession is not null, SCHEDULE_ERROR return;");
                    return;
                }
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_SCHEDULE_ERROR_END.equals(mType)) {
                Logger.d(TAG, "!--->-----VALUE_SCHEDULE_TYPE_SCHEDULE_ERROR_END----");
                mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);

            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_ONLINE_ERROR.equals(mType)
                    || SessionPreference.VALUE_SCHEDULE_TYPE_UNKNOWN_ERROR.equals(mType)
                    || SessionPreference.VALUE_SCHEDULE_TYPE_WEATHER_ERROR_SHOW.equals(mType)
                    || SessionPreference.VALUE_SCHEDULE_TYPE_NO_PERSON.equals(mType)
                    || SessionPreference.VALUE_SCHEDULE_TYPE_NO_NUMBER.equals(mType)
                    || SessionPreference.VALUE_SCHEDULE_TYPE_NO_APP.equals(mType)
                    || SessionPreference.VALUE_SCHEDULE_TYPE_LOCATION_NO_RESULT.equals(mType)
                    || SessionPreference.VALUE_SCHEDULE_TYPE_LOCATION_RESULT_ERROR.equals(mType)
                    || SessionPreference.VALUE_SCHEDULE_TYPE_STOCK_NO_RESULT.equals(mType)
                    || SessionPreference.VALUE_LOCATION_RESULT_TIMEOUT.equals(mType)) {

                Logger.d(TAG, "!--->---createSession:--IKnowButtonSession-----");
                base = new IKnowButtonSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_SOME_NUMBERS.equals(mType)) {
                Logger.d(TAG, "!--->---createSession()-VALUE_TYPE_SOME_NUMBERS-----");
                base = new MultipleNumbersShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_SOME_PERSON.equals(mType)) {
                Logger.d(TAG, "!--->---VALUE_TYPE_SOME_PERSON------");
                base = new MultiplePersonsShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_CALL_ONE_NUMBER.equals(mType)) {
                Logger.d(TAG, "!--->VALUE_TYPE_CALL_ONE_NUMBER---");
                base = new CallConfirmShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_ACTION_CANCEL.equals(mType)) {
                Logger.d(TAG, "!--->ACTION_CANCEL--mCurrentSession = " + mCurrentSession);
                // XD 20151124 modify
                if (isNeedKeepWindowOnExpVersionMode()) {
                    releaseCurrentSessionOnly();
                } else {
                    onGUISessionDone();
                }
            } else if (SessionPreference.VALUE_TYPE_CALL_OK.equals(mType)) {
                Logger.d(TAG, "!--->VALUE_TYPE_CALL_OK------");
                base = new CallShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_INCOMING_CALL.equals(mType)) {
                // xd added 20150716
                // 收到来新短信协议，先显示窗体
                mWindowService.show();
                Logger.d(TAG, "!--->VALUE_SCHEDULE_TYPE_INCOMING_CALL------");
                base = new ReceiveCallSession(mWindowService, mSessionManagerHandler);

            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_INCOMING_CALL_OPERATOR.equals(mType)) {
                // xd added 20150716
                Logger.d(TAG, "!--->VALUE_SCHEDULE_TYPE_INCOMING_CALL_OPERATOR------");
                base = new ReceiveCallOperatorSession(mWindowService, mSessionManagerHandler);

            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_INPUT_CONTENT_SMS.equals(mType)
                    || SessionPreference.VALUE_SCHEDULE_TYPE_INPUT_SMS_CONFIRM.equals(mType)) {
                Logger.d(TAG, "!--->----VALUE_SCHEDULE_TYPE_INPUT_SMS_CONTENT------");
                /* < XD 20150825 added for 4.4 default Sms App begin */
                if (GUIConfig.isAllowGUIRequestAsDefaultSmsApp) {
                    DeviceTool.changeGUIToDefaultSmsApp(mWindowService);
                }
                /* XD 20150825 added for 4.4 default Sms App End > */
                if (mCurrentSession != null && mCurrentSession instanceof SmsInputShowSession) {
                    base = mCurrentSession;
                    base.mIsNeedAddTextView = false;
                    isNeedClear = false;
                    /* < XD 20150821 added for SMS REINPUT Begin */
                    JSONObject dataObject = JsonTool.getJSONObject(obj, SessionPreference.KEY_DATA);
                    if (!dataObject.has(SessionPreference.KEY_CONTENT)) {
                        Logger.d(TAG, "!--->---SMS content is null, reinput!");
                        base = new SmsInputShowSession(mWindowService, mSessionManagerHandler);
                    }
                    /* XD 20150821 added for SMS REINPUT End > */
                } else {
                    base = new SmsInputShowSession(mWindowService, mSessionManagerHandler);
                }
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_INCOMING_SMS.equals(mType)) {
                Logger.d(TAG, "!--->----VALUE_SCHEDULE_TYPE_INCOMING_SMS------");
                // 收到来新短信协议，先显示窗体
                mWindowService.show();
                base = new ReceiveSmsSession(mWindowService, mSessionManagerHandler);

            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_INCOMING_SMS_OPERATOR.equals(mType)) {
                Logger.d(TAG, "!--->----VALUE_SCHEDULE_TYPE_INCOMING_SMS_OPERATOR------");
                base = new ReceiveSmsOperatorSession(mWindowService, mSessionManagerHandler);

            } else if (SessionPreference.VALUE_TYPE_MUTIPLE_UNSUPPORT.equals(mType)
                    || SessionPreference.VALUE_TYPE_CONFIRM_UNSUPPORT.equals(mType)) {
                isNeedClear = false;
                base = new UnsupportShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_UNSUPPORT_BEGIN_SHOW.equals(mType)) {
                showHelpView();
                releaseCurrentSession();
                base = new UnsupportEndSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_UNSUPPORT_END_SHOW.equals(mType)) {
                showHelpView();
                mSessionViewContainer.removeAllSessionViews();
                releaseCurrentSession();
                base = new UnsupportEndSession(mWindowService, mSessionManagerHandler);
            } /*
               * else if (SessionPreference.VALUE_TYPE_CONTACT_SHOW.equals(type)) { base = new
               * ContactShowSession(mWindowService, mSessionManagerHandler); }
               */
            else if (SessionPreference.VALUE_SCHEDULE_TYPE_SMS_OK.equals(mType)) {
                Logger.d(TAG, "!--->VALUE_TYPE_SMS_OK---");
                base = new SmsShowSession(mWindowService, mSessionManagerHandler);

            } else if (SessionPreference.VALUE_TYPE_WAITING.equals(mType)) {
                String originType = JsonTool.getJsonValue(obj, SessionPreference.KEY_ORIGIN_TYPE);
                if (SessionPreference.DOMAIN_ROUTE.equals(originType)) {
                    isWaitingSessionCanceled = false;

                    showAsrResultTextOnMicView(resultText);// add tyz 20151012
                    base = new RouteWaitingSession(mWindowService, mSessionManagerHandler);
                } else if (SessionPreference.DOMAIN_NEARBY_SEARCH.equals(originType)) {
                    isWaitingSessionCanceled = false;
                    base = new LocalsearchWaitingSession(mWindowService, mSessionManagerHandler);// add
                                                                                                 // tyz
                                                                                                 // 0714
                } else {
                    base = new WaitingSession(mWindowService, mSessionManagerHandler);
                }
                setEnableMicrophoneControlListener(false); // XD added 20151109
            } else if (SessionPreference.VALUE_TYPE_WEATHER_SHOW.equals(mType)) {
                base = new WeatherShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_WEB_SHOW.equals(mType)) {
                base = new WebShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_TRANSLATION_SHOW.equals(mType)) {
                // base = new TranslationShowSession(mWindowService,
                // mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_STOCK_SHOW.equals(mType)) {
                // mMicrophoneControl.setAnswerText("");//XD delete 20150730
                base = new StockShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_MUTIPLE_LOCALSEARCH.equals(mType)) {// add tyz
                                                                                        // 0721
                if (isWaitingSessionCanceled) {
                    isWaitingSessionCanceled = false;
                    mType = "";
                    mSessionStatus = "";
                    return;
                }
                base = new LocalSearchShowSession(mWindowService, mSessionManagerHandler);
                // setEnableMicrophoneControlListener(true); // XD added 20151109
                Logger.d(TAG, "tyz---------" + "MUTIPLE_LOCALSEARCH");

            }
            // TODO add by ch Traffic
            else if (SessionPreference.VALUE_TYPE_TRAFFIC_SHOW.equals(mType)) {
                // mMicrophoneControl.setAnswerText("");//XD delete 20150730
                base = new TrafficShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_TRAFFIC_CONTROL_SHOW.equals(mType)) {
                // mMicrophoneControl.setAnswerText("");//XD delete 20150730
                base = new TrafficControlShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_APP_LAUNCH.equals(mType)) {
                base = new AppLaunchSession(mWindowService, mSessionManagerHandler);
                Logger.d(TAG, "tyz---------" + "localsearch ok");

            } else if (SessionPreference.VALUE_TYPE_FM_SHOW.equals(mType)) {
                base = new BroadcastSession(mWindowService, mSessionManagerHandler);
                Logger.d(TAG, "tyz---------" + "localsearch ok");

            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_LOCALSEARCH_SHOW.equals(mType)// route
                    || SessionPreference.VALUE_TYPE_ROUTE_SHOW.equals(mType)) {
                base = new LocalSearchRouteShowSession(mWindowService, mSessionManagerHandler);
                Logger.d(TAG, "createSession---LocalSearchRouteShowSession---");

            } else if (SessionPreference.VALUE_TYPE_LOCALSEARCH_CALL_SHOW.equals(mType)) {
                base = new LocalSearchCallShowSession(mWindowService, mSessionManagerHandler);
                Logger.d(TAG, "tyz---------" + "localsearch call ok");
            } else if (SessionPreference.VALUE_TYPE_LOCALSEARCH_CONFIRM.equals(mType)
                    || SessionPreference.VALUE_TYPE_ROUTE_CONFIRM.equals(mType)) {
                base =
                        new LocalSearchRouteConfirmShowSession(mWindowService,
                                mSessionManagerHandler);
                Logger.d(TAG, "createSession---LocalSearchRouteConfirmShowSession---");

            } else if (SessionPreference.VALUE_TYPE_MUSIC_SHOW.equals(mType)) {
                // base = new MusicShowSession(mWindowService,
                // mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_POSITION_SHOW.equals(mType)) {
                Logger.d(TAG, "VALUE_TYPE_POSITION_SHOW");
                base = new PositionShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_APP_UNINSTALL.equals(mType)) {
                // base = new AppUninstallSession(mWindowService,
                // mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_APP_EXIT.equals(mType)) {
                base = new AppExitSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_SETTING.equals(mType)) {
                Logger.d(TAG, "!--->----VALUE_TYPE_SETTING------");
                base = new SettingSession(mWindowService, mSessionManagerHandler);

            } else if (SessionPreference.VALUE_TYPE_REMINDER_CONFIRM.equals(mType)) {
                // base = new ReminderConfirmSession(mWindowService,
                // mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_REMINDER_OK.equals(mType)) {
                // base = new ReminderOkSession(mWindowService,
                // mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_INPUT_CONTACT.equals(mType)) {
                Logger.d(TAG, "!--->--VALUE_TYPE_INPUT_CONTACT----TalkShowMiddleSession()---");
                base = new TalkShowMiddleSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_MAIN_ACTION_NAVI.equals(mType)) {
                // TODO: XD 20151021 added
                Logger.d(TAG, "!--->createSession----MAIN_ACTION_NAVI-------");
                if (mCurrentSession == null && !(mCurrentSession instanceof MainMapLocationSession)) {
                    base = new MainMapLocationSession(mWindowService, mSessionManagerHandler);
                }
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_MAIN_ACTION_AROUND.equals(mType)) {
                Logger.d(TAG, "!--->createSession----MAIN_ACTION_AROUND-------");
                base = new MainMapAroundSearchSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_TALK_SHOW.equals(mType)) {
                showHelpView();
                base = new TalkShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_POI_SHOW.equals(mType)) {
                // if (mCurrentSession != null
                // && mCurrentSession instanceof PoiShowSession) {
                // base = mCurrentSession;
                // isNeedClear = false;
                // } else {
                // base = new PoiShowSession(mWindowService,
                // mSessionManagerHandler);
                // }
            } else if (SessionPreference.VALUE_TYPE_MULTIPLE_SHOW.equals(mType)) {
                base = new MultipleShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_ERROR_SHOW.equals(mType)) {
                base = new ErrorShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_ALARM_SHOW.equals(mType)) {
                // base = new AlarmSettingSession(mWindowService,
                // mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_CONTACT_ADD.equals(mType)) {
                base = new ContactAddSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_APP_MUTIPLEP_SHOW.equals(mType)) {
                // base = new MultipleApplicationShowSession(mWindowService,
                // mSessionManagerHandler);
            } else if (SessionPreference.VALUE_TYPE_BROADCAST_SHOW.equals(mType)) {
                // base = new BroadcastSession(mWindowService,
                // mSessionManagerHandler);
            } else if (SessionPreference.VALUE_MUTIPLE_LOCATION.equals(mType)) {// //////tyz
                Logger.d(TAG, "!--->VALUE_MUTIPLE_LOCATION---isWaitingSessionCanceled = "
                        + isWaitingSessionCanceled + "; resultText = " + resultText);
                // if is canceled return;
                if (isWaitingSessionCanceled) {
                    isWaitingSessionCanceled = false;
                    mType = "";
                    mSessionStatus = "";
                    return;
                }
                showAsrResultTextOnMicView(resultText);
      //          String poi = JsonTool.getJsonValue(data, "locationToPoi", "");
                // mMicrophoneControl.showChangeLocationView(true, poi);
                base = new MultipleLocationSession(mWindowService, mSessionManagerHandler);

                // setEnableMicrophoneControlListener(true); // XD added 20151109
            } else if (SessionPreference.VALUE_TYPE_UI_HANDLE_SHOW.equals(mType)) {
                base = new UIHandleShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_MUSIC_RESULT_SHOW.equals(mType)) {
                Logger.d(TAG, "!--->-VALUE_SCHEDULE_TYPE_MUSIC_RESULT_SHOW");
                base = new MusicResultShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_LOCATION_NO_RESULT.equals(mType)
                    || SessionPreference.VALUE_SCHEDULE_TYPE_LOCATION_RESULT_ERROR.equals(mType)
                    || SessionPreference.VALUE_TYPE_FM_NO_SHOW.equals(mType)) {
                Logger.d(TAG, "!--->---createSession()- " + mType + "-----");
                base = new IKnowButtonSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_CHAT.equals(mType)
                    || SessionPreference.VALUE_SCHEDULE_TYPE_CALENDAR_SHOW.equals(mType)
                    || SessionPreference.VALUE_SCHEDULE_TYPE_WEB_SEARCH_SHOW.equals(mType)) {
                Logger.d(TAG, "!--->-VALUE_SCHEDULE_TYPE_CHAT");
                base = ChatShowSession.getInstance(mWindowService, mSessionManagerHandler);
                showAsrResultTextOnMicView(mWindowService
                        .getString(R.string.help_text_chat_exit_command)); // XD 20151102 added
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_CHAT_WEB_SHOW.equals(mType)) {
                Logger.d(TAG, "!--->-VALUE_SCHEDULE_TYPE_CHAT_WEB_SHOW");
                // TODO:
                base = ChatWebShowSession.getInstance(mWindowService, mSessionManagerHandler);
                showAsrResultTextOnMicView(mWindowService
                        .getString(R.string.help_text_chat_exit_command));
            } else if (SessionPreference.VALUE_FLIGHT_ONEWAY.equals(mType)) {
                Logger.d(TAG, "!--->-VALUE_FLIGHT_ONEWAY");
                base = new FlightShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_FLIGHT_ABNORMAL.equals(mType)) {
                Logger.d(TAG, "!--->-VALUE_FLIGHT_ABNORMAL");
                base = new FlightAbnormalShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_AUDIO_SHOW.equals(mType)) {
                Logger.d(TAG, "!--->-VALUE_SCHEDULE_TYPE_AUDIO_SHOW");
                base = new FmAudioShowSession(mWindowService, mSessionManagerHandler);
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_HAND_CHOOSE.equals(mType)) {
                Logger.d(TAG, "!--->-VALUE_SCHEDULE_TYPE_HAND_CHOOSE");

                if (mCurrentSession != null) {
                    setEnableMicrophoneControl(false);
                    Logger.d(TAG, "---currentsession:" + mCurrentSession);
                    if (mCurrentSession instanceof MultipleLocationSession) {
                        ((MultipleLocationSession) mCurrentSession).loadMoreItem();
                    } else if (mCurrentSession instanceof LocalSearchShowSession) {
                        ((LocalSearchShowSession) mCurrentSession).loadMoreItem();
                    }
                }
                String handChooseText =
                        mWindowService.getString(R.string.mutiple_hand_choose_ansewer_text);
                showAsrResultTextOnMicView(handChooseText);
            } else if (SessionPreference.VALUE_SCHEDULE_TYPE_LOCALSEARCH_SHOW_ITEM.equals(mType)) {
                Logger.d(TAG, "!--->-VALUE_SCHEDULE_TYPE_LOCALSEARCH_SHOW_ITEM");
                base = new LocalSearchItemShowSession(mWindowService, mSessionManagerHandler);
                JSONObject mDataObject = JsonTool.getJSONObject(obj, SessionPreference.KEY_DATA);
                String phoneNum = "";
                if (mDataObject != null) {
                    phoneNum = JsonTool.getJsonValue(mDataObject, "phoneNum");
                }
                if (!TextUtils.isEmpty(phoneNum)) {
                    answerTxtNoPhoneNum = false;
                } else {
                    answerTxtNoPhoneNum = true;
                }

            }
        }

        mMicrophoneControl.onIdle(true); // added 20150701
        if (base != null) {
            if (isNeedClear) {
                mSessionViewContainer.clearTemporaryViews();
            }
            if (isNeedReleaseLastSession(mCurrentSession, base)) {// xd modify 20150817
                Logger.d(TAG, "!--->release last confirm session to cancel timer.");
                mCurrentSession.release();
            }
            mCurrentSession = base;
            Logger.d(TAG, "!--->--mCurrentSession putProtocol---mCurrentSession = "
                    + mCurrentSession);
            if (mCurrentSession instanceof MultiplePersonsShowSession) {
                Logger.d(TAG, "!--->mCurrentSession is MultiplePersonsShowSession");
            }
            mCurrentSession.putProtocol(obj);
        }
    }

    /**
     * is need release last session(the confirm session) before new session to cancel timer. XD
     * added 20150817
     * 
     * @param lastSession: mCurrentSession
     * @param newSession: base
     * @return
     */
    private boolean isNeedReleaseLastSession(BaseSession lastSession, BaseSession newSession) {
        if (lastSession == null) {
            return false;
        }
        if ((lastSession instanceof CallConfirmShowSession)
                || (lastSession instanceof MultipleNumbersShowSession)
                || ((lastSession instanceof SmsInputShowSession) && (newSession instanceof SmsShowSession))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * show Asr Result Text On Mic View XD added 20150730
     * 
     * @param result
     */
    private void showAsrResultTextOnMicView(String result) {
        Logger.d(TAG, "!--->showAsrResultTextOnMicView--result = " + result);
        if (null != mMicrophoneControl) {
            mMicrophoneControl.setAnswerText(result);
        }
    }


    /**
     * show Asr Result Text On Session View XD added 20150730 XD modify 20150906
     * 
     * @param result
     * @param isPartial
     */
    private void showAsrResultTextOnSessionView(String result, boolean isPartial) {
        Logger.d(TAG, "!--->showAsrResultTextOnSessionView = " + result + "; isPartial = "
                + isPartial + "; mAsrResultSessionView = " + mASRResultSessionView);
        showAsrResultTextOnMicView("");
        if (mASRResultSessionView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) mWindowService
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mASRResultSessionView = layoutInflater.inflate(R.layout.session_asr_result_view, null);
            TextView mTvSessionMicStatus =
                    (TextView) mASRResultSessionView.findViewById(R.id.tv_session_asr_result);
            mTvSessionMicStatus.setText(result);
            // added View
            mSessionViewContainer.removeAllSessionViews();
            mASRResultSessionView.setVisibility(View.VISIBLE);
            addAnswerView(mASRResultSessionView);
        } else {
            TextView mTvSessionMicStatus =
                    (TextView) mASRResultSessionView.findViewById(R.id.tv_session_asr_result);
            mTvSessionMicStatus.setText(result);
        }
    }


    /**
     * 
     * @param status
     * @return
     */
    public View getMicStatusView(String status) {
        if (mWindowService == null) {
            return mMicStatusView;
        }
        // XD 20150828 modify for Music Doreso
        if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS == mWindowService
                .getWakeupSuccessType()
                && mWindowService.getString(R.string.mic_recording).equals(status)) {
            mMicStatusView = new MicRecordingSessionView(mWindowService);
            ((MicRecordingSessionView) mMicStatusView).setSessionHandler(mSessionManagerHandler);
        } else {
            LayoutInflater layoutInflater =
                    (LayoutInflater) mWindowService
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mMicStatusView = layoutInflater.inflate(R.layout.session_mic_status_view, null);
            mTvSessionMicStatus =
                    (TextView) mMicStatusView.findViewById(R.id.tv_session_mic_status);
            mTvSessionMicStatus.setText(status);

            Button btnCancel = (Button) mMicStatusView.findViewById(R.id.cancelBtn);
            btnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    cancelTalk();
                }
            });
            if (SessionPreference.VALUE_SET_STATE_TYPE_WAKEUP_SUCCESS_DORESO == mWindowService
                    .getWakeupSuccessType()) {
                btnCancel.setVisibility(View.VISIBLE);
            } else {
                btnCancel.setVisibility(View.GONE);
            }
        }
        return mMicStatusView;
    }

    public FunctionHelpHeadListView getFunctionView() {

        mFunctionView = new FunctionHelpHeadListView(mWindowService);
        return mFunctionView;
    }

    // public FunctionHelpView getFunctionView() {
    // Logger.d(TAG, "!--->getFunctionView--");
    // mFunctionView = new FunctionHelpView(mWindowService);
    //
    //
    // // mFunctionView = new FunctionView(mWindowService);
    //
    // // LayoutInflater layoutInflater = (LayoutInflater) mWindowService
    // // .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    //
    // /*
    // * FrameLayout functionLauout= (FrameLayout)
    // * layoutInflater.inflate(R.layout.funtion_main, null);
    // * if(functionLauout != null) { mFunctionView = (FunctionView)
    // * functionLauout.findViewById(R.id.main_rl); FunctionView view =
    // * (FunctionView) functionLauout.getChildAt(0); Logger.i(TAG ,
    // * "--functionLauout not null--" +functionLauout.getId() + " "
    // * +functionLauout.getChildCount()); if(view != null) { mFunctionView =
    // * view; } else { Logger.i(TAG , "--mFunctionView2 null--"); } }
    // */
    //
    // // mFunctionView = (FunctionView) layoutInflater.inflate(
    // // R.layout.funtion_main, null);
    //
    // // if (mFunctionView != null) {
    // // mFunctionView.setTextViews();
    // // } else {
    // // Logger.i(TAG, "--mFunctionView null--");
    // // }
    // return mFunctionView;
    // }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCurrentSession != null) {
            mCurrentSession.onActivityResult(requestCode, resultCode, data);
        }
    }

 /*   private void onSessionDone() {
        Logger.d(TAG, "!--->onSessionDone");
        // resetAutoDismissTimer();
    }*/

    /**
     * XD added 20151014
     * 
     * @return
     */
    public boolean isCanShowChangeLocationView() {
        if (SessionPreference.VALUE_MUTIPLE_LOCATION.equals(mType)
                || (SessionPreference.VALUE_SCHEDULE_TYPE_SCHEDULE_ERROR.equals(mType) && (SessionPreference.DOMAIN_ROUTE)
                        .equals(mOriginType))) {
            return true;
        } else {
            return false;
        }
    }
}
