/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : FlightShowSession.java
 * @ProjectName : uniCarGui
 * @PakageName : com.unisound.unicar.gui.session
 * @version : 1.0
 * @Author :
 * @CreateDate : 2015-08-19
 */
package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.FlightContentView;

public class FlightShowSession extends BaseSession {
    private static final String TAG = "FlightShowSession";
    private Context mContext;

    public FlightShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        Logger.d(TAG, "FlightShowSession create");
        this.mContext = context;
    }

    @Override
    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "putProtocal -- jsonProtocol : " + jsonProtocol.toString());

        JSONObject data = JsonTool.getJSONObject(jsonProtocol, "data");
        String flightUrl = JsonTool.getJsonValue(data, "flightUrl");
        String origin = JsonTool.getJsonValue(data, "origin");
        String destination = JsonTool.getJsonValue(data, "destination");
        String answer = mAnswer;

        // 显示FlightContentView
        FlightContentView mFlightContentView =
                new FlightContentView(mContext, mSessionManagerHandler, origin, destination);
        mFlightContentView.updateUI(flightUrl, answer);
        addAnswerView(mFlightContentView, true);
    }

    @Override
    public void onTTSEnd() {
        Logger.d(TAG, "onTTSEnd");
        // 是否需要dismiss 需要定夺
        // mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE_DELAY);
        super.onTTSEnd();
    }

}
