/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : LocalSearchRouteConfirmShowSession.java
 * @ProjectName : uniCarSolution
 * @PakageName : com.unisound.unicar.gui.session
 * @Author : Alieen
 * @CreateDate : 2015-07-21
 */
package com.unisound.unicar.gui.session;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.unisound.unicar.gui.oem.RomCustomSetting;
import com.unisound.unicar.gui.oem.RomDevice;
import com.unisound.unicar.gui.oem.RomSystemSetting;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.StringUtil;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Alieen
 * @CreateDate : 2015-07-2
 * @ModifiedBy : Alieen
 * @ModifiedDate: 2015-07-2
 * @Modified: 2015-07-2: 实现基本功能
 */
public class LocalSearchCallShowSession extends BaseSession {
    public static final String TAG = "LocalSearchCallShowSession";
    private Context mContext;

    public LocalSearchCallShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        this.mContext = context;
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        addQuestionViewText(mQuestion);
        Logger.d(TAG, "putProtocol : " + jsonProtocol.toString());
        String phone = "";
        try {
            JSONObject data = jsonProtocol.getJSONObject("data");
            
            phone = StringUtil.clearSpecialCharacter(data.getString("phoneNum"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!TextUtils.isEmpty(phone)){
        	Logger.d(TAG, "phone : " + phone);
	        if(RomDevice.hasBluePhoneClient(mContext)){
				RomSystemSetting.RomCustomDialNumber(mContext, phone);
			}else{
				Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
				callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(callIntent);
			}
        }
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    }



    @Override
    public void onTTSEnd() {
        super.onTTSEnd();
        Logger.d(TAG, "!--->---onTTSEnd()-----");
        // mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    }

    @Override
    public void release() {
        Logger.d(TAG, "!--->release");
        super.release();
    }
}
