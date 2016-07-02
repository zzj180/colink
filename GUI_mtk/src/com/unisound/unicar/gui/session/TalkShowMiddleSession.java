package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.NoPerSonContentView;

/**
 * “打电话”-->“打电话给谁？”
 * 
 * @author xiaodong
 * 
 */
public class TalkShowMiddleSession extends CommBaseSession {
    public static final String TAG = "TalkShowMiddleSession";

    TalkShowMiddleSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        Logger.d(TAG, "!--->TalkShowMiddleSession()-------");
        // TODO Auto-generated constructor stub
    }

    @SuppressWarnings("deprecation")
	public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "!--->putProtocol()---jsonProtocol = " + jsonProtocol);
        // addQuestionViewText(mAnswer);

        if (SessionPreference.DOMAIN_CALL.equals(mOriginType)
                || SessionPreference.DOMAIN_SMS.equals(mOriginType)) {
            NoPerSonContentView view = new NoPerSonContentView(mContext);
            if (SessionPreference.DOMAIN_CALL.equals(mOriginType)) {
                view.setShowTitle(R.string.call_out_input_contact_title);
            } else if (SessionPreference.DOMAIN_SMS.equals(mOriginType)) {
                view.setShowTitle(R.string.sms_input_contact_title);
            }

            addAnswerView(view, true);

            addAnswerViewText(mAnswer);
        } else {
            addAnswerViewText(mAnswer);
        }

    }
}
