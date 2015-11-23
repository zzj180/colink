package com.unisound.unicar.gui.oem;

import android.content.Context;
import android.content.Intent;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.unisound.unicar.gui.utils.Logger;

/**
 * 用于处理客户处理协议
 * @author tyz
 *
 */

public class RomCustomerProcessing {
	
	private static final String TAG = "RomCustomerProcessing";
	private static final String APP_ACTION = "com.unisound.unicar.app.action.close";
	
	/**
	 * 关闭app发广播给对接方
	 * @param context
	 * @param packageName
	 */
	public static void exitAppForOther(Context context, String packageName,String className) {
		Logger.d(TAG, "action = "+APP_ACTION +";uninstallApp packageName=" + packageName+";className = " + className);
		
		if (context != null) {
			Intent intent = new Intent();
			intent.setAction(APP_ACTION);
			intent.putExtra("packageName", packageName);
			intent.putExtra("className", className);
			context.sendBroadcast(intent);
		}
	}
	/**
	 * answer
	 */
	private static final String CALL_ACTION = "com.unisound.unicar.call";
	public static void sendMessageToOtherCall(Context context,String  phoneNum){
		Logger.d(TAG,"action = "+CALL_ACTION+ ";phoneNum = " +  phoneNum);
		Intent intent = new Intent();
		intent.setAction(CALL_ACTION);
		intent.putExtra("type", "Answer");
		intent.putExtra("phoneNum", phoneNum);
		context.sendBroadcast(intent);
		
	}
	
	/**
	 * hang up
	 */
	public static void sendMessageToOtherHangUp(Context context,String  phoneNum){
		Logger.d(TAG,"action = "+CALL_ACTION+";phoneNum = " +  phoneNum);
		Intent intent = new Intent();
		intent.setAction(CALL_ACTION);
		intent.putExtra("type", "HangUP");
		intent.putExtra("phoneNum", phoneNum);
		context.sendBroadcast(intent);
		
	}
	
	/**
	 * setting
	 */
	
	private static final String SETTING_ACTION = "com.unisound.unicar.setting.action";
	public static void sendMessageToOtherSetting(Context context,String operands ,String operator){
		Logger.d(TAG,"action = "+SETTING_ACTION+";operands = "+ operands + ";operator = " +  operator);
		Intent intent = new Intent();
		intent.setAction(SETTING_ACTION);
		intent.putExtra("operands", operands);
		intent.putExtra("operator", operator);
		context.sendBroadcast(intent);
	}
	/**
	 * broadcast
	 */
	
	private static final String ACTION_BROADCAST = "com.unisound.unicar.broadcast.action";
	
	public static void sendMessageToOtherBroadcast(Context context,String type ,String freq){
		Logger.d(TAG,"action = "+ACTION_BROADCAST+";type = "+ type + ";freq = " +  freq);
		Intent intent = new Intent();
		intent.setAction(ACTION_BROADCAST);
		intent.putExtra("type", type);
		intent.putExtra("freq", freq);
		context.sendBroadcast(intent);
	}
	
	/**
	 * play tts
	 */
	public static final int TTS_END_WAKEUP_START = 1;
	public static final int TTS_END_RECOGNIER_START = 2;
	
	public static void playtts(WindowService context,String ttsContent,int type){
		context.playTTs(ttsContent, type);
	}
}
