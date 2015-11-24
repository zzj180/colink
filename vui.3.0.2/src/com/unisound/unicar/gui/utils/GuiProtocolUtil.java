/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : GuiProtocolUtil.java
 * @ProjectName : uniCarGui
 * @PakageName : com.unisound.unicar.gui.utils
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-08-219
 */
package com.unisound.unicar.gui.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.unisound.unicar.gui.preference.SessionPreference;

/**
 * 
 * @author xiaodong
 * @date 20150819
 */
public class GuiProtocolUtil {

    public static final String EVENT_PARAM_KEY_TTS_END_RECOGNIZER = "RECOGNIZER";
    public static final String EVENT_PARAM_KEY_TTS_END_WAKEUP = "WAKEUP";

    /**
     * 
     * @param protocolName
     * @param name
     * @param number
     * @return
     */
    public static String getSmsReplyEventProtocol(String protocolName, String name, String number) {
        return "{\"service\":\"cn.yunzhisheng.sms\",\"semantic\":{\"intent\":{\"confirm\":\""
                + protocolName + "\",\"name\":\"" + name + "\",\"number\":\"" + number
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
                + protocolName + "\",\"name\":\"" + name + "\",\"number\":\"" + number
                + "\",\"content\":\"" + content + "\"}},\"code\":\"SMS_FAST_REPLY\",\"rc\":0}";
    }



    /**
     * get PlayTTS Event Param
     * 
     * @param ttsText
     * @param recognizerType : "wakeup / recognizer
     * @return
     */
    public static String getPlayTTSEventParam(String ttsText, String recognizerType) {
        JSONObject param = new JSONObject();
        try {
            param.put(SessionPreference.EVENT_PARAM_KEY_TTS_END_KEY_RECOGNIZER_TYPE, recognizerType);
            param.put(SessionPreference.EVENT_PARAM_KEY_TTS_TEXT, ttsText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param.toString();
    }

    /**
     * 
     * @param type
     * @return
     */
    public static String getTypeParamProtocol(String type) {
        JSONObject protocol = new JSONObject();
        try {
            protocol.put(SessionPreference.EVENT_PARAM_KEY_TYPE, type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return protocol.toString();
    }

    /**
     * 
     * @param type: SessionPreference.EVENT_PARAM_SWITCH_CLOSE /
     *        SessionPreference.EVENT_PARAM_SWITCH_OPEN
     * @return
     */
    public static String getTimeOutParamProtocol(String type) {
        return getTypeParamProtocol(type);
    }

    /**
     * get Recording Control Param Protocol
     * 
     * @param param: SessionPreference.PARAM_RECORDING_CONTROL_START /
     *        SessionPreference.PARAM_RECORDING_CONTROL_STOP
     * @param domain
     * @return
     */
    public static String getRecordingControlParamProtocol(String param, String domain) {
        JSONObject protocol = new JSONObject();
        try {
            protocol.put(SessionPreference.EVENT_PARAM_KEY_TYPE, param);
            protocol.put(SessionPreference.KEY_DOMAIN, domain);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return protocol.toString();
    }

    /**
     * get Change Location Param Protocol
     * 
     * @param keyword
     * @return
     */
    public static String getChangeLocationParamProtocol(String keyword) {
        JSONObject protocol = new JSONObject();
        try {
            protocol.put(SessionPreference.EVENT_PARAM_KEY_LOACTION_KEYWORD, keyword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return protocol.toString();
    }

    /**
     * 
     * @param poiSearchType
     * @return
     */
    public static String getUpdateAroundSearchParamProtocol(String poiSearchType) {
        return getChangeLocationParamProtocol(poiSearchType);
    }


}
