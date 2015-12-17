package com.unisound.unicar.gui.session;

import org.json.JSONObject;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.FlightAbnormalContentView;

import android.content.Context;
import android.os.Handler;

public class FlightAbnormalShowSession extends BaseSession {
    private static final String TAG = "FlightAbnormalShowSession";
    private Context mContext;

    public FlightAbnormalShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        Logger.d(TAG, "FlightAbnormalShowSession create ");
        this.mContext = context;
    }

    @Override
    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "putProtocal - jsonProtocal : " + jsonProtocol.toString());

        JSONObject data = JsonTool.getJSONObject(jsonProtocol, "data");
        String answer = JsonTool.getJsonValue(data, "answer");

        FlightAbnormalContentView flightAbnormalContentView =
                new FlightAbnormalContentView(mContext);
        flightAbnormalContentView.setFlightAbnormalInfo(answer);

        addAnswerView(flightAbnormalContentView, true);
    }

    @Override
    public void onTTSEnd() {
        super.onTTSEnd();
    }
}
