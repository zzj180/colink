package com.aispeech.aios.adapter.node;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;

/**
 * @desc 当前定位节点
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class LocationNode extends BaseNode {
    private static final String TAG = "AIOS-Adapter-locationNode";
    private static LocationNode mInstance;
    public static synchronized LocationNode getInstance() {
        if (null == mInstance) mInstance = new LocationNode();
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public String getName() {
        return "mylocation";
    }

    @Override
    public BusClient.RPCResult onCall(String url, byte[]... bytes) throws Exception {

        AILog.i(TAG, "url:" + url);
        if (url.equals("/mylocation")) {
            //查询当前的位置，跳转到地图显示
            UiEventDispatcher.notifyUpdateUI(UIType.Location);
            UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
        }
        return null;
    }
}
