package com.unisound.unicar.gui.session;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.unisound.unicar.gui.model.ContactInfo;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.PickPersonView;

/**
 * Multiple Contacts session view
 * 
 * @author xiaodong
 * 
 */
public class MultiplePersonsShowSession extends ContactSelectBaseSession {
    public static final String TAG = "MultiplePersonsShowSession";
    int personNum = 0;
    private PickPersonView mPickPersonView = null;

    public MultiplePersonsShowSession(Context context, Handler handle) {
        super(context, handle);
        Logger.d(TAG, "!--->MultiplePersonsShowSession()-------");

    }

    public void putProtocol(JSONObject jsonProtocol) {
        Logger.d(TAG, "!--->--putProtocol()--1" + jsonProtocol);
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "!--->--putProtocol()--2" + jsonProtocol);
        Logger.d(TAG, "--jsonProtocol-->" + jsonProtocol);
        JSONObject data = JsonTool.getJSONObject(jsonProtocol, "data");
        String contactsValue = JsonTool.getJsonValue(data, SessionPreference.KEY_DATA_CONTACTS, "");

        JSONArray dataArray = JsonTool.parseToJSONOArray(contactsValue);

        // JSONArray dataArray = getJsonArray(contactsJSON,
        // SessionPreference.KEY_DATA_CONTACTS);//"person" --> "contacts"
        Logger.d(TAG, "!--->dataArray = " + dataArray);
        if (dataArray != null) {
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = JsonTool.getJSONObject(dataArray, i);
                ContactInfo contactItem = new ContactInfo();
                contactItem.setPhotoId(Integer.parseInt(JsonTool.getJsonValue(item, "pic")));
                contactItem.setDisplayName(JsonTool.getJsonValue(item, "name"));
                mContactInfoList.add(contactItem);
                Logger.d(
                        TAG,
                        "!--->mDataItemProtocalList.add item = "
                                + JsonTool.getJsonValue(item, SessionPreference.KEY_TO_SELECT));
                mDataItemProtocalList.add(JsonTool.getJsonValue(item,
                        SessionPreference.KEY_TO_SELECT));
            }
            addAnswerViewText(mAnswer);

            Logger.d(TAG, "!--->mContactInfoList size = " + mContactInfoList.size()
                    + "; mPickPersonView = " + mPickPersonView);
            if (mPickPersonView == null) {
                mPickPersonView = new PickPersonView(mContext);

                mPickPersonView.initView(mContactInfoList);

                mPickPersonView.setPickListener(mPickViewListener);
            }

            addSessionView(mPickPersonView);
        }

        // playTTS(mTTS);
    }
}
