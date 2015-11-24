package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.sms.SmsItem;
import com.unisound.unicar.gui.utils.GuiProtocolUtil;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.ReceiveSMSView;
import com.unisound.unicar.gui.view.ReceiveSMSView.IReceiveSMSViewListener;

/**
 * Receive SMS Operator Session
 * Incoming SMS -> "Broadcast"、"FAST REPLY" & "CANCEL"
 * @author xiaodong
 * @date 20150717
 */
public class ReceiveSmsSession extends ContactSelectBaseSession {
	
	public static final String TAG = ReceiveSmsSession.class.getSimpleName();
	
	private ReceiveSMSView mReceiveSmsView;
	private String mName;
	private String mNumber;
	private String SCHEDULE_TYPE = SessionPreference.VALUE_SCHEDULE_TYPE_INCOMING_SMS;
	
	public ReceiveSmsSession(Context context, Handler handle) {
		super(context, handle);
		Logger.d(TAG, "!--->----ReceiveSmsSession()------");
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		Logger.d(TAG, "!--->--putProtocol()--jsonProtocol = "+jsonProtocol);
		Logger.d(TAG, "!--->--putProtocol()--mDataObject = "+mDataObject);
		String operator = JsonTool.getJsonValue(mDataObject, "operator", "");
		Logger.d(TAG, "!--->putProtocol:---operator = "+operator);
		
		String displayName = JsonTool.getJsonValue(mDataObject, "name");
		final String number = JsonTool.getJsonValue(mDataObject, "number");
		mName = displayName;
		mNumber = number;
		String content = JsonTool.getJsonValue(mDataObject, "content");//null
		Logger.d(TAG, "!--->displayName = "+displayName+"; number = "+number+"; content = "+content);
		
		SmsItem mSmsItem = new SmsItem();
		mSmsItem.setName(displayName);
		mSmsItem.setNumber(number);
		mSmsItem.setMessage(content);
		
		if (null == mReceiveSmsView) {
			mReceiveSmsView = new ReceiveSMSView(mContext, mSmsItem, SCHEDULE_TYPE);
			mReceiveSmsView.setListener(mReceiveSMSViewListener);
		}
		
		addSessionView(mReceiveSmsView);
	}

	private IReceiveSMSViewListener mReceiveSMSViewListener = new IReceiveSMSViewListener(){

		@Override
		public void onSmsBroadcast() {
			Logger.d(TAG, "!--->onSmsBroadcast---------");
			onUiProtocal(SessionPreference.EVENT_NAME_INCOMING_SMS, SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_OK);
			
		}

		@Override
		public void onSmsFastReply() {
			Logger.d(TAG, "!--->onSmsFastReply---------");
			String defaultContent = mContext.getString(R.string.sms_content_fast_reply);
			onUiProtocal(SessionPreference.EVENT_NAME_INCOMING_SMS_REPLY, 
					GuiProtocolUtil.getSmsFastReplyEventProtocol(SessionPreference.EVENT_PARAM_VALUE_INCOMING_SMS_FAST_REPLY, mName, mNumber,
							defaultContent));
			
		}

		@Override
		public void onCancel() {
			Logger.d(TAG, "!--->onCancel---------");
			// TODO Auto-generated method stub
			onUiProtocal(SessionPreference.EVENT_NAME_INCOMING_SMS, SessionPreference.EVENT_PROTOCAL_ON_CONFIRM_CANCEL_SMS); 
			mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		}

		@Override
		public void onSmsReply() {
			Logger.d(TAG, "!--->onSmsReply---------");
			onUiProtocal(SessionPreference.EVENT_NAME_INCOMING_SMS_REPLY, 
					GuiProtocolUtil.getSmsReplyEventProtocol(SessionPreference.EVENT_PARAM_VALUE_INCOMING_SMS_REPLY, mName, mNumber));
		}
		
	};


	
	@Override
	public void onTTSEnd() {
		Logger.d(TAG, "!--->onTTSEnd");
		super.onTTSEnd();
		
	}

	@Override
	public void release() {
		Logger.d(TAG, "!--->release");
		super.release();
		
	}
}
