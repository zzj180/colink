package com.colink.zzj.txzassistant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.colink.zzj.txzassistant.db.WakeUpDB;
import com.colink.zzj.txzassistant.db.WakeUpSwitch;
import com.colink.zzj.txzassistant.node.AudioNode;
import com.colink.zzj.txzassistant.node.CustomAsAsrNode;
import com.colink.zzj.txzassistant.node.CustomNode;
import com.colink.zzj.txzassistant.node.MusicNode;
import com.colink.zzj.txzassistant.node.NavigationNode;
import com.colink.zzj.txzassistant.node.PhoneNode;
import com.colink.zzj.txzassistant.node.SenceToolNode;
import com.colink.zzj.txzassistant.node.SystemNode;
import com.colink.zzj.txzassistant.util.Constants;
import com.colink.zzj.txzassistant.util.Logger;
import com.colink.zzj.txzassistant.util.SystemPropertiesProxy;
import com.colink.zzj.txzassistant.util.UserPerferenceUtil;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZConfigManager.ActiveListener;
import com.txznet.sdk.TXZConfigManager.AsrEngineType;
import com.txznet.sdk.TXZConfigManager.AsrMode;
import com.txznet.sdk.TXZConfigManager.FloatToolType;
import com.txznet.sdk.TXZConfigManager.InitListener;
import com.txznet.sdk.TXZConfigManager.InitParam;
import com.txznet.sdk.TXZConfigManager.TtsEngineType;
import com.txznet.sdk.TXZConfigManager.UIConfigListener;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZTtsManager;

/**
 * @desc AdapterApplication
 * @auth zzj
 * @date 2016-03-19
 */
public class AdapterApplication extends Application implements InitListener,
		ActiveListener, Constants {

	public static String TAG = "AdapterApplication";

	private static Context mContext;
	public static int mapType;
	public static boolean mAcc;
	private InitParam mInitParam;
	public static Handler uiHandler;

	private static AdapterApplication instance;

	public static AdapterApplication getApp() {
		return instance;
	}

	public static Context getContext() {
		if (mContext == null) {
			throw new RuntimeException("Unknown Error");
		}
		return mContext;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		uiHandler = new Handler(Looper.getMainLooper());
		mContext = getApplicationContext();
		ContentValues con = new ContentValues();
		con.put(WakeUpDB.SWITCH, 0);
		getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null, null);
		init();
		CrashHandler.getInstance().init(getApplicationContext());
	}

	public void init() {
		{
			String appId = this.getResources().getString(
					R.string.txz_sdk_init_app_id);
			String appToken = this.getResources().getString(
					R.string.txz_sdk_init_app_token);
			mInitParam = new InitParam(appId, appToken);
		}

		{
			// TODO 设置识别和tts引擎类型
			int asr = Settings.System.getInt(getContentResolver(), "asr", 1);
			Logger.d("asr = " + asr);
			if (asr == 0) {
				mInitParam.setAsrType(AsrEngineType.ASR_IFLY).setTtsType(
						TtsEngineType.TTS_YUNZHISHENG);
			} else {
				mInitParam.setAsrType(AsrEngineType.ASR_YUNZHISHENG)
						.setTtsType(TtsEngineType.TTS_SYSTEM);
			}
		}

		{
			// TODO 设置唤醒词
			Set<String> words = getSharedPreferences(PRE_NAME,Context.MODE_PRIVATE).getStringSet(KEY_PRE_WAKEUPWORDS,null);
			if (words == null) {
				/*
				 * String[] wakeupKeywords =
				 * this.getResources().getStringArray(R
				 * .array.txz_sdk_init_wakeup_keywords);
				 */
				String keywords = SystemPropertiesProxy.get(
						getApplicationContext(), KEY_WAKEUP_WORDS);
				mInitParam.setWakeupKeywordsNew(new String[] { keywords });
				Set<String> set = new HashSet<>();
				set.add(keywords);
				set.remove("");
				getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE).edit()
						.putStringSet(KEY_PRE_WAKEUPWORDS, set).apply();
			} else {
				String[] strSet = new String[words.size()];
				words.toArray(strSet);
				mInitParam.setWakeupKeywordsNew(strSet);
			}
		}

		
		{
			// TODO 可以按需要设置自己的对话模式
			mInitParam.setAsrMode(AsrMode.ASR_MODE_CHAT);
			// TODO 设置识别模式，默认自动模式即可
			// mInitParam.setAsrServiceMode(AsrServiceMode.ASR_SVR_MODE_AUTO);
			// TODO 设置是否允许启用服务号
			mInitParam.setEnableServiceContact(true);

			mInitParam.enableBlackHole(true);
			String platform = SystemPropertiesProxy.get(this,AssistantService.KEY_PLATFORM);
			if (!TextUtils.isEmpty(platform)) {
				mInitParam.setTxzStream(AudioManager.STREAM_SYSTEM);
			}
			// TODO 设置开启回音消除模式
			mInitParam.setFilterNoiseType(1);
			// TODO 其他设置
		}

		// TODO 初始化在这里
		TXZConfigManager.getInstance().initialize(this, mInitParam, this, this);
	}

	public static void runOnUiGround(Runnable r, long delay) {
		if (delay > 0) {
			uiHandler.postDelayed(r, delay);
		} else {
			uiHandler.post(r);
		}
	}

	@Override
	public void onFirstActived() {
	}

	@Override
	public void onError(int errCode, String errDesc) {
		// TODO 初始化出错
		Toast.makeText(getApplicationContext(), "语音初始化失败：" + errCode,
				Toast.LENGTH_LONG).show();
		runOnUiGround(new Runnable() {
			@Override
			public void run() {
				init();
			}
		}, 60000);
	}

	@Override
	public void onSuccess() {
		// TODO 初始化成功，可以在这里根据需要执行一些初始化操作
		// TODO 设置一些参数()
		// TODO 注册指令(参考CustomNode)
		// TODO 设置电话(参考PhoneNode)、音乐(参考MusicNode)、导航(参考NavigationNode)工具
		// TODO 同步联系人(参考PhoneNode)
		String keywords = null;
		Set<String> words = getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE)
				.getStringSet(KEY_PRE_WAKEUPWORDS, null);
		if (words != null) {
			keywords = new ArrayList<>(words).toString();
		} else {
			keywords = SystemPropertiesProxy.get(getApplicationContext(),
					KEY_WAKEUP_WORDS);
		}

		ContentValues con = new ContentValues();
		con.put(WakeUpDB.SWITCH, UserPerferenceUtil.getAECEnable(getApplicationContext()) ? 1 : 0);
		getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null, null);
		
		TXZTtsManager.getInstance().speakText(
				getString(R.string.playvoice, keywords));
		TXZTtsManager.getInstance().setVoiceSpeed(
				UserPerferenceUtil.getTTSSpeed(getApplicationContext()));
		boolean micEnable = UserPerferenceUtil
				.getFloatMicEnable(getApplicationContext());
		TXZConfigManager.getInstance().showFloatTool(
				micEnable ? FloatToolType.FLOAT_NORMAL
						: FloatToolType.FLOAT_NONE);
		TXZConfigManager.getInstance().setWakeupThreshhold(
				UserPerferenceUtil.getWakeupThreshold(getApplicationContext()));
		TXZConfigManager.getInstance().enableWakeup(
				UserPerferenceUtil.getAECEnable(getApplicationContext()));

		TXZConfigManager.getInstance().enableSettings(true);
		TXZResourceManager.getInstance().setTextResourceString(
				ASR_HINT,
				this.getResources().getStringArray(
						R.array.rs_voice_asr_start_hint));

		// ERROR 关闭日志 DEBUG 打开日志
		TXZConfigManager.getInstance().setUIConfigListener(
				new UIConfigListener() {
					@Override
					public void onConfigChanged(String arg0) {
						Logger.d("onConfigChanged=" + arg0);
						try {
							JSONObject jsonObject = new JSONObject(arg0);
							// TODO 获取设置里各项参数
							JSONArray array = jsonObject.getJSONArray(KEY_PRE_WAKEUPWORDS);
							String floatTool = jsonObject.getString(KEY_FLOAT_TOOL);
							int voiceSpeed = jsonObject.getInt(KEY_VOICE_SPEED);
							float wakeupThreshold = Float.parseFloat(String.valueOf(jsonObject
											.getDouble(KEY_WAKEUP_THRESHOLD)));
							boolean wakeupSound = jsonObject.getBoolean(KEY_WAKEUP_SOUND);

							// TODO 保存设置里各项参数
							if ("FLOAT_NONE".equals(floatTool)) {
								UserPerferenceUtil.setFloatMicEnable(getApplicationContext(), false);
							} else {
								UserPerferenceUtil.setFloatMicEnable(getApplicationContext(), true);
							}
							UserPerferenceUtil.setTTSSpeed(getApplicationContext(), voiceSpeed);
							UserPerferenceUtil.setAECEnable(getApplicationContext(), wakeupSound);
							UserPerferenceUtil.setWakeupThreshold(getApplicationContext(), wakeupThreshold);
							ContentValues con = new ContentValues();
							con.put(WakeUpDB.SWITCH, wakeupSound ? 1 : 0);
							getContentResolver().update(WakeUpSwitch.CONTENT_URI, con, null, null);
							Set<String> set = new HashSet<>();
							for (int i = 0; i < array.length(); i++) {
								set.add(array.getString(i));
							}
							set.remove("");

							// TODO 保存出厂设置唤醒词
							String keywords = SystemPropertiesProxy.get(getApplicationContext(), KEY_WAKEUP_WORDS);
							if (!TextUtils.isEmpty(keywords)&& !set.contains(keywords)) {
								set.add(keywords);
								String[] strSet = new String[set.size()];
								set.toArray(strSet);
								TXZConfigManager.getInstance().setWakeupKeywordsNew(strSet);
							}

							getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE).edit()
									.putStringSet(KEY_PRE_WAKEUPWORDS, set).apply();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
		TXZConfigManager.getInstance().setLogLevel(Log.DEBUG);
		SenceToolNode.getInstance().init();
		SystemNode.getInstance().init();
		CustomNode.getInstance().init();
		MusicNode.getInstance().init();
		AudioNode.getInstance().init();
		NavigationNode.getInstance().init();
		CustomAsAsrNode.getInstance().useWakeupAsAsr();
		startService(new Intent(getApplicationContext(), PhoneBookService.class));
		startService(new Intent(getApplicationContext(), AssistantService.class));
		PhoneNode.getInstance().init();
		Logger.d("success");
	}
}
