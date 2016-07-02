package com.aispeech.aios.adapter.node;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.bean.TTS;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.util.DeviceUtil;
import com.aispeech.aios.adapter.util.PreferenceHelper;
import com.aispeech.aios.adapter.util.StringUtil;

/**
 * @desc Home节点，UI交互使用
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class HomeNode extends BaseNode {
    private static final String TAG = "HomeNode";
    private boolean mIsMicClick = false;
    private boolean mIsNeedAsleep = false;
    private boolean mIsBootCompleted = false;
    private String mVadState = "idle";
    private static HomeNode mInstance;
    private Context mContext;
    private HomeNodeReceiver homeNodeReceiver;
    private static String aiosState = "unknown";

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
            String action = intent.getAction();
			Log.i(TAG, "onReceive --> " + action);

            if (TTS.ACTION_HOME_EXIT.equals(action)) {
                String extra = intent.getStringExtra("exit");
                if ("exit".equals(extra)) {
                    UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
                }
            } else if (TTS.ACTION_TO_SPEAK.equals(action)) {
                if (null != bc) {
                    String text = intent.getStringExtra("aios.intent.extra.TEXT");
                    int priority = intent.getIntExtra("aios.intent.extra.PRIORITY", 0);
                    bc.publish(AiosApi.Other.SPEAK, text, String.valueOf(priority));
                    Log.i(TAG, "TTS text : " + text + "\n and priority : " + priority);
                }
            } else if (action.equals("com.aispeech.acc.status")) {
                String status = intent.getStringExtra("status");
                if (status.equals("off")) {
                    Log.i(TAG, "acc off");
                    stopRecorder();
                } else {
                    Log.i(TAG, "acc on");
                    startRecorder();
                }
            } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
				goHomePage();
			}
        }
    }
    
    void goHomePage() {
		Intent mHomeIntent = new Intent(Intent.ACTION_MAIN, null);
		mHomeIntent.addCategory(Intent.CATEGORY_HOME);
		mHomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		mContext.startActivity(mHomeIntent);
	}

	private void startRecorder() {
		mIsNeedAsleep = false;
		if (bc != null) {
			bc.call("/keys/wakeup/allow", "set", "enable"); // 打开语音唤醒开关
		}
		Log.e(TAG, "acc on");
	}

    private void stopRecorder() {
        mIsNeedAsleep = true;
        if (bc != null) {
            bc.publish(AiosApi.Other.UI_PAUSE);
            new Handler(AdapterApplication.getContext().getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    bc.call("/keys/wakeup/allow", "set", "disable");
                }
            }, 500);
        }
        Log.e(TAG, "acc off");
    }

	public void setWakeUp(boolean enable) {
		if (enable) {
			if (bc != null) {
				bc.call("/keys/wakeup/allow", "set", "enable"); // 打开语音唤醒开关
			}
		} else {
			if (bc != null) {
				bc.call("/keys/wakeup/allow", "set", "disable");
			}
		}
	}

	/**
	 * 注册广播
	 */
	public void register() { /* 动态注册广播 */
		if (null == homeNodeReceiver) {
			homeNodeReceiver = new HomeNodeReceiver();
		}
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.aciton.HOME_EXIT");
		intentFilter.addAction("aios.intent.action.TO_SPEAK");
		intentFilter.addAction("com.aispeech.acc.status");
		intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
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
        bc.subscribe(AiosApi.Other.CONTEXT_INPUT_TEXT);
        bc.subscribe(AiosApi.Other.CONTEXT_OUTPUT_TEXT);
        bc.subscribe("wakeup.result");
        bc.subscribe("vad.state");
        bc.subscribe(AiosApi.Other.AIOS_STATE);
        bc.subscribe(AiosApi.Other.UI_MIC_CLICK);
        bc.subscribe("wechat.message");//订阅微信来消息信息
        new Handler(AdapterApplication.getContext().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                AudioManager mAudioManager = (AudioManager) AdapterApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
                PreferenceHelper.getInstance().setVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        });


    }

    @Override
    public void onMessage(String topic, byte[]... parts) throws Exception {
        super.onMessage(topic, parts);
        Log.i(TAG, topic+parts);

		if (topic.equals(AiosApi.Other.AIOS_STATE)) {
			aiosState = new String(parts[0], "utf-8");
			if ("awake".equals(aiosState)) {
				mContext.sendBroadcast(new Intent("action.coogo.QUITE_SCREENOFF"));
				DeviceUtil.controlFan("0");
				if (parts.length >= 2) {
					if (!"notification.pickup".equals(new String(parts[1],"utf-8")) && !"phone.incomingcall".equals(new String(parts[1], "utf-8"))) {
						UiEventDispatcher.notifyUpdateUI(UIType.ShowWindow);
					}else {
                        ChatNode.getInstance().cancelTask();
                    }
				} else {
					UiEventDispatcher.notifyUpdateUI(UIType.ShowWindow);
				}
			
			} else if ("asleep".equals(aiosState)) {
				DeviceUtil.controlFan("1");
				
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
		}else if (topic.equals("nodes.ready")) {
			boolean wakeUpEnable = PreferenceHelper.getInstance().getWakeUpEnable();
			HomeNode.getInstance().setWakeUp(wakeUpEnable);

		} else if (topic.equals("wechat.message")) {
			UiEventDispatcher.notifyUpdateUI(UIType.WeChat);

		} else if (topic.equals("wakeup.result")) {
			UiEventDispatcher.notifyUpdateUI(UIType.Awake);
		} else if (topic.equals("vad.state")) {
			mVadState = new String(parts[0], "utf-8");
			Log.i(TAG, "vadState : " + mVadState);
			if (mVadState.equals("busy")) {
				Log.i(TAG, "vad state is busy ");
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
			if (topic.equals(AiosApi.Other.CONTEXT_INPUT_TEXT)	&& parts.length > 1 && parts[1] != null) {
				String micStop = new String(parts[1], "utf-8");
				if (micStop.equals("1")) {
					UiEventDispatcher.notifyUpdateUI(UIType.StopRecognition);
				}
			}
			if(UiEventDispatcher.isShowWindow() || (topic.equals(AiosApi.Other.CONTEXT_OUTPUT_TEXT) && "awake".equals(aiosState))) {
                String context = StringUtil.getEncodedString(parts[0]);
                context = (context.length() - context.indexOf(",")) == 1 ? context.substring(0, context.length() - 1) : context;
                UiEventDispatcher.notifyUpdateUI(context);
            }
        }
    }

    @Override
    public BusClient.RPCResult onCall(String url, byte[]... bytes) throws Exception {
        Log.d(TAG, url+ bytes);
        return null;
    }
}