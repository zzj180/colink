package com.aispeech.aios.adapter.util;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.client.AIOSNotificationNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spring on 2015/12/19.
 * to do:
 */
public class NotificationUtil implements AIOSNotificationNode.OnNotificationListener{
    private AIOSNotificationNode mNode;
    public static NotificationUtil mInstance;
    private String mTips;
    private JSONObject mPoi = new JSONObject();

    public void setPoi(String latitude, String longitude) {
        try {
            mPoi.put("name", "");
            mPoi.put("latitude", Double.parseDouble(latitude));
            mPoi.put("longitude", Double.parseDouble(longitude));
            mPoi.put("address", "");
            mPoi.put("distance", 0);
            mPoi.put("tel", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        [{"address":"广东省深圳市南山区","longitude":113.945319,"latitude":22.530555,"dst_name":"软件产业基地","distance":115}]
    }

    private NotificationUtil() {
        mNode = AIOSNotificationNode.getInstance();
        mNode.setListener(this);
    }

    /**
     * @return NotificationUtil实例
     */
    public static synchronized NotificationUtil getInstance() {
        if (mInstance == null) {
            mInstance = new NotificationUtil();
        }
        return mInstance;
    }

    /**
     * 通知内核播报文本内容
     *
     * @param tips 播抱文本
     */
    public void publishPickUp(String tips) {
        UiEventDispatcher.notifyUpdateUI(UIType.ShowPicker, null, null);
        AIOSNotificationNode.Message pickupMsg = new AIOSNotificationNode.Message("notification.pickup", new String[]{
                tips, "[" + mPoi.toString() + "]"});
        mTips = tips;
        AILog.d("tips=" + tips + ",poi=" + "[" + mPoi.toString() + "]");
        mNode.publishMessage(pickupMsg);
    }

    @Override
    public void onNotificationStart() {
        AILog.d("", "start ui");
        List<Object> jsonObjects = new ArrayList<Object>();
        jsonObjects.add(mPoi);
        UiEventDispatcher.notifyUpdateUI(UIType.ShowPickerUI, jsonObjects, mTips);
    }

    @Override
    public void onNotificationDone() {
        AILog.d("", "stop ui");
        UiEventDispatcher.notifyUpdateUI(UIType.DismissPicker);
    }
}
