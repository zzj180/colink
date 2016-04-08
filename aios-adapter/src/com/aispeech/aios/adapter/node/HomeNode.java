package com.aispeech.aios.adapter.node;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.bean.TTS;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.util.PreferenceHelper;
import com.aispeech.aios.adapter.util.StringUtil;

/**
 * @desc Home节点，UI交互使用
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class HomeNode extends BaseNode {
    private static final String TAG = "AIOS-HomeNode";
    private boolean mIsMicClick = false;
    private boolean mIsNeedAsleep = false;
    private boolean mIsBootCompleted = false;
    private String mVadState = "idle";
    private static HomeNode mInstance;
    private Context mContext;
    private HomeNodeReceiver homeNodeReceiver;

    private HomeNode() {
        this.mContext = AdapterApplication.getContext();
    }

    public static synchronized HomeNode getInstance() {
        if (mInstance == null) {
            mInstance = new HomeNode();
        }

        return mInstance;
    }


    public class HomeNodeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AILog.i(TAG, "onReceive --> " + intent.getAction());

            if (TTS.ACTION_HOME_EXIT.equals(intent.getAction())) {
                String extra = intent.getStringExtra("exit");
                if ("exit".equals(extra)) {
                    UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
                }
            } else if (TTS.ACTION_TO_SPEAK.equals(intent.getAction())) {
                if (null != bc) {
                    String text = intent.getStringExtra("aios.intent.extra.TEXT");
                    int priority = intent.getIntExtra("aios.intent.extra.PRIORITY", 0);
                    bc.publish(AiosApi.Other.SPEAK, text, String.valueOf(priority));
                    AILog.i(TAG, "TTS text : " + text + "\n and priority : " + priority);
                }
            } else if (intent.getAction().equals("com.aispeech.acc.status")) {
                String status = intent.getStringExtra("status");
                if (status.equals("off")) {
                    AILog.i(TAG, "acc off");
                    stopRecorder();
                } else {
                    AILog.i(TAG, "acc on");
                    startRecorder();
                }
            } else if (intent.getAction().equals("aios.intent.action.RECORDER_STOP")) {
                stopRecorder();
            } else if (intent.getAction().equals("aios.intent.action.RECORDER_START")) {
                startRecorder();
            }
        }
    }

    private void startRecorder() {
        mIsNeedAsleep = false;
        if (bc != null) {
            bc.call("/keys/wakeup/allow", "set", "enable");
        }
        AILog.e(TAG, "acc on");
    }

    private void stopRecorder() {
        mIsNeedAsleep = true;
        if (bc != null) {
            bc.publish(AiosApi.Other.UI_PAUSE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bc.call("/keys/wakeup/allow", "set", "disable");
                }
            }, 500);
        }
        AILog.e(TAG, "acc off");
    }

    /**
     * 注册广播
     */
    public void register() { /*动态注册广播*/
        if (null == homeNodeReceiver) {
            homeNodeReceiver = new HomeNodeReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.aciton.HOME_EXIT");
        intentFilter.addAction("aios.intent.action.TO_SPEAK");
        intentFilter.addAction("com.aispeech.acc.status");
        intentFilter.addAction("aios.intent.action.RECORDER_START");
        intentFilter.addAction("aios.intent.action.RECORDER_STOP");
        mContext.registerReceiver(homeNodeReceiver, intentFilter);
    }

    /**
     * 解绑广播
     */
    public void unRegister() {

        if (homeNodeReceiver != null) {
            mContext.unregisterReceiver(homeNodeReceiver);
            homeNodeReceiver = null;
        }

    }

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public void onJoin() {
        bc.subscribe("wakeup.result");
        bc.subscribe("vad.state");
        bc.subscribe(AiosApi.Other.AIOS_STATE);
        bc.subscribe(AiosApi.Other.UI_MIC_CLICK);
        bc.subscribe("wechat.message");//订阅微信来消息信息
        AudioManager mAudioManager = (AudioManager) AdapterApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        PreferenceHelper.getInstance().setVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    @Override
    public void onMessage(String topic, byte[]... parts) throws Exception {
        super.onMessage(topic, parts);
        AILog.i(TAG, topic, parts);

        if (topic.equals(AiosApi.Other.AIOS_STATE)) {
            String aiosState = new String(parts[0], "utf-8");
            if ("awake".equals(aiosState)) {
                if (parts.length >= 2) {
                    if (!"notification.pickup".equals(new String(parts[1], "utf-8"))) {
                        UiEventDispatcher.notifyUpdateUI(UIType.Awake);
                    }
                } else {
                    UiEventDispatcher.notifyUpdateUI(UIType.Awake);
                }
                bc.subscribe(AiosApi.Other.CONTEXT_INPUT_TEXT);
                bc.subscribe(AiosApi.Other.CONTEXT_OUTPUT_TEXT);
            } else if ("asleep".equals(aiosState)) {
                bc.unsubscribe(AiosApi.Other.CONTEXT_OUTPUT_TEXT);
                bc.unsubscribe(AiosApi.Other.CONTEXT_INPUT_TEXT);
                UiEventDispatcher.notifyUpdateUI(UIType.StopListening);
                UiEventDispatcher.notifyUpdateUI(UIType.StopRecognition);
                UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
                if (!mIsBootCompleted) {
                    mIsBootCompleted = true;
                    if (mIsNeedAsleep) {
                        mIsNeedAsleep = false;
                        if (bc != null) {
                            bc.call("/recorder/stop");
                        }
                    }
                }

            }
        } else if (topic.equals("wechat.message")) {
            UiEventDispatcher.notifyUpdateUI(UIType.WeChat);

        } else if (topic.equals("wakeup.result")) {
            UiEventDispatcher.notifyUpdateUI(UIType.Awake);
        } else if (topic.equals("vad.state")) {
            mVadState = new String(parts[0], "utf-8");
            AILog.i(TAG, "vadState : " + mVadState);
            if (mVadState.equals("busy")) {
                AILog.i(TAG, "vad state is busy ");
                UiEventDispatcher.notifyUpdateUI(UIType.StartListening);

            } else if (mVadState.equals("wait")) {
                UiEventDispatcher.notifyUpdateUI(UIType.StopListening);
                UiEventDispatcher.notifyUpdateUI(UIType.StartRecognition);

            } else if (mVadState.equals("idle")) {
                if (mIsMicClick) {
                    UiEventDispatcher.notifyUpdateUI(UIType.StopListening);
                    UiEventDispatcher.notifyUpdateUI(UIType.StopRecognition);
                    mIsMicClick = false;
                }
            }
        } else if (topic.equals(AiosApi.Other.UI_MIC_CLICK)) {
            mIsMicClick = true;
        } else if (topic.equals("keys.aios.state")) {
            return;
        } else {
            if (topic.equals(AiosApi.Other.CONTEXT_INPUT_TEXT) && parts.length > 1 && parts[1] != null) {
                String micStop = new String(parts[1], "utf-8");
                if (micStop.equals("1")) {
                    UiEventDispatcher.notifyUpdateUI(UIType.StopRecognition);
                }
            }
            String context = StringUtil.getEncodedString(parts[0]);
            context = (context.length() - context.indexOf(",")) == 1 ? context.substring(0, context.length() - 1) : context;
            UiEventDispatcher.notifyUpdateUI(context);
        }
    }

    @Override
    public BusClient.RPCResult onCall(String url, byte[]... bytes) throws Exception {
        AILog.i(TAG, url, bytes);
        return null;
    }
}