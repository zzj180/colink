package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents.Insert;

public class ContactAddSession extends CommBaseSession {
    public static final String TAG = "ContactAddSession";

    ContactAddSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        // TODO Auto-generated constructor stub
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        String name = JsonTool.getJsonValue(mDataObject, "name");
        String phone = JsonTool.getJsonValue(mDataObject, "number");
        Intent i = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        i.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        i.putExtra(Insert.NAME, name);
        i.putExtra(Insert.PHONE, phone);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
        Logger.d(TAG, "--ContactAddSession mAnswer : " + mAnswer);
    }
}
