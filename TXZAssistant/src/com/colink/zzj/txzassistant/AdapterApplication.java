package com.colink.zzj.txzassistant;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.colink.zzj.txzassistant.node.CameraNode;
import com.colink.zzj.txzassistant.node.CustomNode;
import com.colink.zzj.txzassistant.node.MusicNode;
import com.colink.zzj.txzassistant.node.NavigationNode;
import com.colink.zzj.txzassistant.node.PhoneNode;
import com.colink.zzj.txzassistant.node.SystemNode;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZConfigManager.ActiveListener;
import com.txznet.sdk.TXZConfigManager.AsrEngineType;
import com.txznet.sdk.TXZConfigManager.AsrMode;
import com.txznet.sdk.TXZConfigManager.AsrServiceMode;
import com.txznet.sdk.TXZConfigManager.FloatToolType;
import com.txznet.sdk.TXZConfigManager.InitListener;
import com.txznet.sdk.TXZConfigManager.InitParam;
import com.txznet.sdk.TXZConfigManager.TtsEngineType;
import com.txznet.sdk.TXZTtsManager;

/**
 * @desc AdapterApplication
 * @auth zzj
 * @date 2016-03-19
 */
public class AdapterApplication extends Application implements InitListener,
		ActiveListener {

	public static String TAG = "AdapterApplication";

	private static Context mContext;
	public static int mapType;
	public static boolean mAcc;
	private InitParam mInitParam;
	protected static Handler uiHandler = new Handler(Looper.getMainLooper());

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
		mContext = getApplicationContext();
		init();
		CrashHandler.getInstance().init(getApplicationContext());
	}

	public void init() {
		{
			// TODO 获取接入分配的appId和appToken
			String appId = this.getResources().getString(
					R.string.txz_sdk_init_app_id);
			String appToken = this.getResources().getString(
					R.string.txz_sdk_init_app_token);
			// TODO 设置初始化参数
			mInitParam = new InitParam(appId, appToken);
			// TODO 可以设置自己的客户ID，同行者后台协助计数/鉴权
			// mInitParam.setAppCustomId("ABCDEFG");
			// TODO 可以设置自己的硬件唯一标识码
			// mInitParam.setUUID("0123456789");
		}

		{
			// TODO 设置识别和tts引擎类型
			mInitParam.setAsrType(AsrEngineType.ASR_YUNZHISHENG).setTtsType(
					TtsEngineType.TTS_YUNZHISHENG);
			/*
			 * int asr = Settings.System.getInt(getContentResolver(), "asr", 0);
			 * Logger.d("asr = "+ asr); if(asr ==0){
			 * mInitParam.setAsrType(AsrEngineType
			 * .ASR_IFLY).setTtsType(TtsEngineType.TTS_YUNZHISHENG); }else{
			 * mInitParam
			 * .setAsrType(AsrEngineType.ASR_YUNZHISHENG).setTtsType(TtsEngineType
			 * .TTS_YUNZHISHENG); }
			 */
		}

		{
			// TODO 设置唤醒词
			String[] wakeupKeywords = this.getResources().getStringArray(
					R.array.txz_sdk_init_wakeup_keywords);
			mInitParam.setWakeupKeywordsNew(wakeupKeywords);
		}

		{
			// TODO 可以按需要设置自己的对话模式
			mInitParam.setAsrMode(AsrMode.ASR_MODE_CHAT);
			// TODO 设置识别模式，默认自动模式即可
			mInitParam.setAsrServiceMode(AsrServiceMode.ASR_SVR_MODE_AUTO);
			// TODO 设置是否允许启用服务号
			mInitParam.setEnableServiceContact(true);
			// TODO 设置开启回音消除模式
			// mInitParam.setFilterNoiseType(1);
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
		// TODO 首次联网激活，如果需要出厂激活提示，可以在这里完成
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
		TXZTtsManager.getInstance().speakText("语音助手初始化成功");
		TXZConfigManager.getInstance().showFloatTool(FloatToolType.FLOAT_NONE);
		//ERROR 关闭日志  DEBUG 打开日志
        TXZConfigManager.getInstance().setLogLevel(Log.ERROR);
		CameraNode.getInstance().init();
		CustomNode.getInstance().init();
		MusicNode.getInstance().init();
		NavigationNode.getInstance().init();
		PhoneNode.getInstance().init();
		SystemNode.getInstance().init();

		startService(new Intent(getApplicationContext(), AssistantService.class));

		// DebugUtil.showTips("同行者引擎初始化成功");
	}

}
