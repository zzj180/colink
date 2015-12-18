package com.unisound.unicar.gui.msg;
import org.json.JSONObject;

import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

public class SystemCallTransition {
	
	private static final String TAG = SystemCallTransition.class.getSimpleName();
	
	private ISystemCallTransitionListener mMessageTransListener = null;
	
	/**
	 * handle System Call
	 * @param callBackJson
	 */
	public void handlerSystemCall(String callBackJson){
		JSONObject obj = JsonTool.parseToJSONObject(callBackJson);
		if (obj != null) {
			JSONObject jobj = JsonTool.parseToJSONObject(callBackJson);
			JSONObject data = JsonTool.getJSONObject(jobj, SessionPreference.KEY_DATA);
			JSONObject dataParams = JsonTool.getJSONObject(data, SessionPreference.KEY_PARAMS); //xd added
			String functionName = JsonTool.getJsonValue(data, SessionPreference.KEY_FUNCTION_NAME, "");//xd added
			Logger.d(TAG, "!--->onCallBackFunction---mFunctionName = " + functionName);
			
			if (SessionPreference.VALUE_FUNCTION_NAME_SET_STATE.equals(functionName)) {
				//JSONObject dataParams = JsonTool.getJSONObject(data, SessionPreference.KEY_PARAMS); //xd delete
				String sType = JsonTool.getJsonValue(dataParams, SessionPreference.KEY_TYPE, "");
				int type = Integer.parseInt(sType);
				if(mMessageTransListener != null){
					mMessageTransListener.setState(type);
				}
			} else if (SessionPreference.VALUE_FUNCTION_NAME_ON_RECORDING_PREPARED.equals(functionName)) {
				if(mMessageTransListener != null){
					mMessageTransListener.onTalkRecordingPrepared();
				}
			} else if (SessionPreference.VALUE_FUNCTION_NAME_ON_RECORDING_EXCEPTION.equals(functionName)) {
				if(mMessageTransListener != null){
					mMessageTransListener.onTalkRecordingException();
				}
			} else if (SessionPreference.VALUE_FUNCTION_NAME_ON_RECORDING_START.equals(functionName)) {
				if(mMessageTransListener != null){
					mMessageTransListener.onTalkRecordingStart();
				}
			} else if (SessionPreference.VALUE_FUNCTION_NAME_ON_RECORDING_STOP.equals(functionName)) {
				if(mMessageTransListener != null){
					mMessageTransListener.onTalkRecordingStop();
				}
			} else if (SessionPreference.VALUE_FUNCTION_NAME_ON_TALK_RESULT.equals(functionName)) {
				//XD added 20150730
				Logger.d(TAG, "!--->VALUE_FUNCTION_NAME_ON_TALK_RESULT");
				String result = JsonTool.getJsonValue(dataParams, SessionPreference.KEY_TYPE, "");
				if(mMessageTransListener != null){
					mMessageTransListener.onTalkResult(result);
				}
			} else if (SessionPreference.VALUE_FUNCTION_NAME_ON_TALK_PROTOCOL.equals(functionName)) {
				//get protocol value
				//JSONObject dataParams = JsonTool.getJSONObject(data, SessionPreference.KEY_PARAMS);  //xd delete
				String sessionProtocol = JsonTool.getJsonValue(dataParams, SessionPreference.KEY_TYPE, "");
				if(mMessageTransListener != null){
					mMessageTransListener.onSessionProtocal(sessionProtocol);
				}
			} else if (SessionPreference.VALUE_FUNCTION_NAME_ON_TTS_PLAY_END.equals(functionName)) {
				if(mMessageTransListener != null){
					mMessageTransListener.onPlayEnd();
				}
			} else if (SessionPreference.VALUE_FUNCTION_NAME_ON_UPDATE_VOLUME.equals(functionName)) {
				int volume = JsonTool.getJsonValue(dataParams, SessionPreference.KEY_VOLUME, 0);
				if (mMessageTransListener != null) {
					mMessageTransListener.onUpdateVolume(volume);
				}
			} else if (SessionPreference.VALUE_FUNCTION_NAME_ON_RECOGNIZER_TIMEOUT.equals(functionName)) {
				Logger.d(TAG, "!--->VALUE_FUNCTION_NAME_ON_RECOGNIZER_TIMEOUT");
				if(mMessageTransListener != null){
					mMessageTransListener.onRecognizerTimeout();
				}
			} else if (SessionPreference.VALUE_FUNCTION_NAME_ON_CTT_CANCEL.equals(functionName)) {
				Logger.d(TAG, "!--->VALUE_FUNCTION_NAME_ON_CTT_CANCEL");
				if(mMessageTransListener != null){
					mMessageTransListener.onCTTCancel();
				}
			} else if (SessionPreference.VALUE_FUNCTION_NAME_FETCH_WAKEUP_WORDS_DONE.equals(functionName)) {
				Logger.d(TAG, "!--->VALUE_FUNCTION_NAME_FETCH_WAKEUP_WORDS_DONE");
				Logger.d(TAG, "Here is wakeup info : " + dataParams.toString());
				JSONObject words = JsonTool.getJSONObject(dataParams, "wakeup_words_params");
				if(mMessageTransListener != null){
					mMessageTransListener.getWAKEUPWORDS(JsonTool.getJsonValue(words, "wakeup_word"));
				}
			}
		}
	}
	
	/**
	 * send Response to VUI service
	 * @param message: call Back Json
	 */
	public void handlertSendResponse(String message){
		Logger.d(TAG, "handlertSendResponse message : " + message);
		JSONObject jobj = JsonTool.parseToJSONObject(message);
		JSONObject data = JsonTool.getJSONObject(jobj, SessionPreference.KEY_DATA);
		String callName = JsonTool.getJsonValue(data, SessionPreference.KEY_FUNCTION_NAME);
		String callID = JsonTool.getJsonValue(jobj, SessionPreference.KEY_CALL_ID);
		String domain = null;//add tyz 0714
		Logger.d(TAG,"!--->testSendResponse()---callName = "+callName+"; callID = "+callID);
		JSONObject dataParams = JsonTool.getJSONObject(data, SessionPreference.KEY_PARAMS); //xd added
		String param = dataParams.toString(); //TODO：param
		Logger.d(TAG, "!--->handlertSendResponse()-----param = "+param);
		String state = "SUCCESS";//TODO: SUCCESS / FAIL
		sendResponse(callID, callName, state,domain, param);
	}
	
	/**
	 * send Response to VUI service
	 * @param callID
	 * @param callName
	 * @param state
	 * @param param
	 */
	public void sendResponse(String callID, String callName, String state,String domain, String params){
		Logger.d(TAG, "!--->sendResponse()-----state = " + state);
		String response = "{\"type\":\"RESP\",\"data\":{\"moduleName\":\"GUI\",\"callID\":\""+callID+"\",\"callName\":\""+callName+"\",\"domain\":\""+domain+"\",\"state\":\""+state+"\",\"params\":"+params+"}}";
		Logger.d(TAG, "sendResponse()-----response = "+response);
		
		if(mMessageTransListener != null){
			mMessageTransListener.onSendMsg(response);
		}
	}
	
	/**
	 * 
	 * @param ISystemCallTransitionListener
	 */
	public void setMessageTransListener(ISystemCallTransitionListener listener){
		mMessageTransListener = listener;
	}
	
}
