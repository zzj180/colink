package com.aispeech.aios.adapter.node;

import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.config.AiosApi;

import java.io.UnsupportedEncodingException;

/**
 * @desc POI信息点
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class CommonPoiNode extends BaseNode {

    static final String TAG = "CommonPoiNode";

    String aiosState = "unknown";

    String intentText = "";
    private static volatile CommonPoiNode mInstance;

    public CommonPoiNode() {
    }


    public static synchronized CommonPoiNode getInstance() {
        if (mInstance == null) {
            mInstance = new CommonPoiNode();
        }
        return mInstance;
    }

    @Override
    public String getName() {
        return "commonpoi";
    }

    @Override
    public void onJoin() {
        super.onJoin();
        bc.subscribe(AiosApi.Other.KEYS_AIOS_STATE);
        BusClient.RPCResult ret = bc.call("/keys/aios/state", "get");
        try {
            if (ret != null) {
                aiosState = new String((ret.retval == null) ? "unknown".getBytes() : ret.retval, "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        handlePendingText();
    }

    @Override
    public void onMessage(String topic, byte[]... parts) throws Exception {
        super.onMessage(topic, parts);
        AILog.i(TAG, topic, parts);

        if (topic.equals(AiosApi.Other.KEYS_AIOS_STATE)) {
            aiosState = new String(parts[0], "utf-8");
        }
    }

    @Override
    public BusClient.RPCResult onCall(String url, byte[]... args) throws Exception {
        AILog.i(TAG, url, args);
        return null;
    }

    private void handlePendingText() {
        if (!TextUtils.isEmpty(intentText)) {
            go(intentText);
        }
    }

    public void go(String intentText) {
        this.intentText = intentText;
        AILog.d(TAG, "go " + intentText + "  state:" + aiosState);
        if (aiosState.equals("asleep")) {
            bc.publish("ui.text.in", intentText);
            AILog.d(TAG, "publish ui.text.in");
        }
    }
}
