package com.unisound.unicar.gui.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author xiaodong
 * @date 20150819
 */
public class GuiProtocolUtil {

	public static final String VALUE_RECOGNIZER_TYPE_WAKEPU = "wakeup";
	public static final String VALUE_RECOGNIZER_TYPE_RECOGNIZER = "recognizer";

	/**
	 * 
	 * @param protocolName
	 * @param name
	 * @param number
	 * @return
	 */
	public static String getSmsReplyEventProtocol(String protocolName, String name,
			String number) {
		return "{\"service\":\"cn.yunzhisheng.sms\",\"semantic\":{\"intent\":{\"confirm\":\""
				+ protocolName
				+ "\",\"name\":\""
				+ name
				+ "\",\"number\":\""
				+ number 
				+ "\"}},\"code\":\"SMS_REPLY\",\"rc\":0}";
	}

	/**
	 * 
	 * @param protocolName
	 * @param name
	 * @param number
	 * @param content
	 * @return
	 */
	public static String getSmsFastReplyEventProtocol(String protocolName, String name,
			String number, String content) {
		return "{\"service\":\"cn.yunzhisheng.sms\",\"semantic\":{\"intent\":{\"confirm\":\""
				+ protocolName
				+ "\",\"name\":\""
				+ name
				+ "\",\"number\":\""
				+ number 
				+ "\",\"content\":\""
				+ content
				+ "\"}},\"code\":\"SMS_FAST_REPLY\",\"rc\":0}";
	}
	

	
	/**
	 * get PlayTTS Event Param
	 * @param ttsText
	 * @param recognizerType : "wakeup / recognizer
	 * @return
	 */
	public static String getPlayTTSEventParam(String ttsText, String recognizerType){
		JSONObject param = new JSONObject();
		try {
			param.put("recognizerType", recognizerType);
			param.put("ttsText", ttsText);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return param.toString();
	}
	
}
