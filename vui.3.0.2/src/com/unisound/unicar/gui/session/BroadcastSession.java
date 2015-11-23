/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : BroadcastSession.java
 * @ProjectName : CarPlay
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Brant
 * @CreateDate : 2014-9-18
 */
package com.unisound.unicar.gui.session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.unisound.unicar.gui.oem.RomCustomerProcessing;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.FmContentView;
import com.unisound.unicar.gui.view.FmContentView.IRouteWaitingContentViewListener;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-9-18
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-9-18
 * @Modified: 2014-9-18: 实现基本功能
 */
public class BroadcastSession extends BaseSession {
    public static final String TAG = "BroadcastSession";

    private Context mContext;

    public BroadcastSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        this.mContext = context;
    }
    String freq = null;

    @Override
    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "--jsonProtocol-->" + jsonProtocol);
        String type = null;
        try {
            JSONObject data = jsonProtocol.getJSONObject("data");
            JSONArray channelList = data.getJSONArray("channelList");

            if (channelList != null && channelList.length() > 0) {
                JSONObject fredata = (JSONObject) channelList.get(0);

                JSONArray frequencyList = fredata.getJSONArray("frequencyList");
                if (frequencyList != null && frequencyList.length() > 0) {
                    JSONObject datafre = (JSONObject) frequencyList.get(0);
                    type = datafre.getString("type");
                    freq = datafre.getString("frequency");
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Logger.d(TAG, "--jsonProtocol--type-->" + type);
        Logger.d(TAG, "--jsonProtocol--freq-->" + freq);
        IRouteWaitingContentViewListener listener = new IRouteWaitingContentViewListener() {

            @Override
            public void onCancel() {
                mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_CANCEL);

            }
        };

        if (type.equals("FM")) {
     //       RomCustomerProcessing.sendMessageToOtherBroadcast(mContext, "FM", freq);
            FmContentView view = new FmContentView(mContext, "调频：" + freq);
            view.setLisener(listener);
            addAnswerView(view);
        } else {
      //      RomCustomerProcessing.sendMessageToOtherBroadcast(mContext, "FM", freq);
            FmContentView view = new FmContentView(mContext, "调幅：" + freq);
            view.setLisener(listener);
            addAnswerView(view);
        }

    }


    @Override
    public void onTTSEnd() {
        super.onTTSEnd();
        try {
			Intent intent=new Intent("action.colink.startFM");
			intent.putExtra("fm_fq",  Float.parseFloat(freq));
			mContext.sendBroadcast(intent);
		} catch (Exception e) {
		}finally{
			mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		}
    }
}
