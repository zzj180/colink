package com.aispeech.aios.adapter.model;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.util.PropertyUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @desc 自定义合成音, 在aios启动时发布该消息, aios内部的tts节
 *       点会根据自定义配置读取已录制的音频作为合成音
 * @auth AISPEECH
 * @date 2016-01-14
 * @copyright aispeech.com
 */
public class CustomTTSData {

    public static final String TAG = "CustomTTSData";

    public String getCustomTTSs(){

        JSONObject ttsSoundJson = new JSONObject();

        AILog.i(TAG, "开始获取各个领域自定义说法列表Json串");

        Document doc = null;

        try {
            doc = PropertyUtil.getDocument(AdapterApplication.getContext(), "custom_tts.xml");
        } catch (Exception e) {
            AILog.i(TAG, e);
        }

        if (null != doc) {
            init(doc,ttsSoundJson);
        }

        String result = ttsSoundJson.toString();

        AILog.i(TAG,"自定义说法和对应音频位置如下：");
        AILog.json(TAG,result);

        return result;
    }

    private void init(Document document,JSONObject jsonObject){

        NodeList tipNodeList = document.getElementsByTagName("tip");

        for (int j = 0, len = tipNodeList.getLength(); j < len; ++j) {
            Node tipsNode = tipNodeList.item(j);

            if (tipsNode.getNodeType() == Node.ELEMENT_NODE) {

                Element tipElement = (Element) tipsNode;

                try {
                    jsonObject.put(tipElement.getAttribute("tip"),tipElement.getAttribute("path"));
                } catch (JSONException e) {
                    AILog.e(TAG,e);
                }
            }
        }
    }

}
