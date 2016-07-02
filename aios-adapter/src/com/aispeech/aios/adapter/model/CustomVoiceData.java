package com.aispeech.aios.adapter.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.util.PropertyUtil;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * @desc 自定义控制命令, 把只要控制命令按照<命令,URL>放进Json就可以说命令，回调URL了
 * @auth AISPEECH
 * @date 2016-01-14
 * @copyright aispeech.com
 */
public class CustomVoiceData {

    private static final String TAG = "AIOS-Adapter-CustomVoiceData";

    /**
     * @return 各种应用打开&关闭的说法Json串
     */
    public String getStatementJson() {

        Context context = AdapterApplication.getContext();

        JSONObject json = new JSONObject();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infoList = context.getPackageManager().queryIntentActivities(mainIntent, 0);

        try {
            for (ResolveInfo info : infoList) {
                String appName = info.activityInfo.loadLabel(context.getPackageManager()).toString();
                String pkg = info.activityInfo.packageName;

              /*  if (appName.contains("电话")) {
                    json.put("打开电话", CallBack.APP_OPEN + info.activityInfo.packageName);
                    json.put("打开蓝牙电话", CallBack.APP_OPEN + info.activityInfo.packageName);
                    json.put("关闭电话", CallBack.APP_CLOSE + info.activityInfo.packageName);
                    json.put("关闭蓝牙电话", CallBack.APP_CLOSE + info.activityInfo.packageName);
                    continue;
                }*/
                json.put("打开" + appName, CallBack.APP_OPEN + pkg);
                json.put("关闭" + appName, CallBack.APP_CLOSE + pkg);
            }

        } catch (JSONException exp) {
            AILog.e(TAG, exp);
        }

        try {
            Document doc = PropertyUtil.getDocument(context, "custom_cmd.xml");
            NodeList urlNodes = doc.getElementsByTagName("url");
            for (int i = 0, lenOuter = urlNodes.getLength(); i < lenOuter; ++i) {
                Element urlElement = (Element) urlNodes.item(i);
                String url = urlElement.getAttribute("name");
                NodeList commondNodes = urlElement.getChildNodes();
                for (int j = 0, lenInner = commondNodes.getLength(); j < lenInner; ++j) {
                    Node commondNode = commondNodes.item(j);
                    if (commondNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element commondElement = (Element) commondNode;
                        json.put(commondElement.getAttribute("name"), url);
                    }
                }
            }

        } catch (Exception e) {
            AILog.e(TAG, e);
        }

        String finalJsonString = StringEscapeUtils.unescapeJson(json.toString());
        AILog.i(TAG, "自定义命令如下：");
        AILog.json(TAG, finalJsonString);

        return finalJsonString;
    }

    public static final class CallBack {
        public static final String APP_OPEN = "/customize/open/app/";
        public static final String APP_CLOSE = "/customize/close/app/";
    }

}
