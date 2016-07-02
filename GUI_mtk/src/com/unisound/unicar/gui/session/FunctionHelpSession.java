package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.FunctionHelpHeadListView;

/**
 * Example Session Function Help Session view
 * 
 * @author xiaodong
 * 
 */
public class FunctionHelpSession extends ContactSelectBaseSession {
    public static final String TAG = "FunctionHelpSession";

    private FunctionHelpHeadListView mFunctionHelpView = null;

    public FunctionHelpSession(Context context, Handler handle) {
        super(context, handle);
        Logger.d(TAG, "!--->FunctionHelpSession()-------");

    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "!--->--putProtocol()--1" + jsonProtocol);

        if (mFunctionHelpView == null) {
            mFunctionHelpView = new FunctionHelpHeadListView(mContext);

            // mFunctionHelpView.setPickListener(mPickViewListener);
        }

        addSessionViewContainsListView(mFunctionHelpView);
    }
}
