package com.unisound.unicar.gui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.sms.SmsItem;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;

/**
 * 
 * @author xiaodong
 * @date 20150717
 */
public class ReceiveSMSView extends LinearLayout implements ISessionView {

	public static final String TAG = ReceiveSMSView.class.getSimpleName();

	private Context mContext;
	private LayoutInflater mLayoutInflater;

	private String mName = "", mNumber = "", mMessage = "";
	private Button mBtnBroadcast, mBtnFastReply, mBtnCancel;
	private Button mBtnReply; // XD added 20150819

	TextView mTvReceivedSMSContent;

	private IReceiveSMSViewListener mListener;

	private String mScheduleType = "";

	@SuppressLint("NewApi")
	public ReceiveSMSView(Context context, SmsItem mSmsItem, String scheduleType) {
		super(context);
		mContext = context;
		mScheduleType = scheduleType;

		if (mSmsItem != null) {
			String name = mSmsItem.getName();
			if (name != null && !name.isEmpty() && !"0".equals(name)) {
				mName = name;
			}
			mNumber = mSmsItem.getNumber();
			mMessage = mSmsItem.getMessage();
		}
		Logger.d(TAG, "!--->ReceiveSMSView----mScheduleType=" + mScheduleType
				+ "; mName = " + mName + "; mNumber = " + mNumber
				+ "; mMessage = " + mMessage);

		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// setOrientation(VERTICAL);

		findViews();
	}

	private void findViews() {
		Logger.d(TAG, "!--->findViews-------begin--");
		View view = mLayoutInflater.inflate(R.layout.sms_received_view, this,
				true);
		LinearLayout ll = (LinearLayout) view
				.findViewById(R.id.ll_received_sms_view);

		mTvReceivedSMSContent = (TextView) ll
				.findViewById(R.id.tv_received_sms_content);
		mBtnBroadcast = (Button) ll.findViewById(R.id.btn_sms_broadcast);
		mBtnReply = (Button) ll.findViewById(R.id.btn_sms_reply);
		mBtnFastReply = (Button) ll.findViewById(R.id.btn_sms_fast_reply);
		mBtnCancel = (Button) ll.findViewById(R.id.btn_sms_received_cancel);

		mBtnBroadcast.setOnClickListener(mOnClickListener);
		mBtnReply.setOnClickListener(mOnClickListener);
		mBtnFastReply.setOnClickListener(mOnClickListener);
		mBtnCancel.setOnClickListener(mOnClickListener);

		/* < XD 20150826 modify begin */
		if (SessionPreference.VALUE_SCHEDULE_TYPE_INCOMING_SMS
				.equals(mScheduleType)) {
			mBtnBroadcast.setVisibility(View.VISIBLE);
			if (GUIConfig.isShowSmsReplyButton) {
				mBtnReply.setVisibility(View.GONE);
			}
		} else if (SessionPreference.VALUE_SCHEDULE_TYPE_INCOMING_SMS_OPERATOR
				.equals(mScheduleType)) {
			mBtnBroadcast.setVisibility(View.GONE);
			if (GUIConfig.isShowSmsReplyButton) {
				mBtnReply.setVisibility(View.VISIBLE);
			}
		}

		if (!GUIConfig.isShowSmsReplyButton) {
			mBtnReply.setVisibility(View.GONE);
			mBtnFastReply.setVisibility(View.GONE);
		}
		/* XD 20150826 modify End > */

		if (mName != null && mName.length() > 0) {
			setContent(mContext.getString(R.string.sms_received_title_from,
					mName));
		} else if (mNumber != null && mNumber.length() > 0) {
			setContent(mContext.getString(R.string.sms_received_title_from,
					mNumber));
		}

		Logger.d(TAG, "!--->findViews-------end--");
	}

	public void setContent(String showText) {
		if (null != mTvReceivedSMSContent) {
			mTvReceivedSMSContent.setText(showText);
		}
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_sms_broadcast:
				Logger.d(TAG, "!--->btn_sms_broadcast");
				if (null != mListener) {
					mListener.onSmsBroadcast();
				}
				break;
			case R.id.btn_sms_reply:
				Logger.d(TAG, "!--->btn_sms_reply");
				if (null != mListener) {
					mListener.onSmsReply();
				}
				break;
			case R.id.btn_sms_fast_reply:
				Logger.d(TAG, "!--->btn_sms_fast_reply");
				if (null != mListener) {
					mListener.onSmsFastReply();
				}
				break;
			case R.id.btn_sms_received_cancel:
				Logger.d(TAG, "!--->btn_sms_received_cancel");
				if (null != mListener) {
					mListener.onCancel();
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public boolean isTemporary() {
		return true;
	}

	@Override
	public void release() {
		Logger.d(TAG, "!--->release---");
		removeAllViews();
	}

	public IReceiveSMSViewListener getListener() {
		return mListener;
	}

	public void setListener(IReceiveSMSViewListener listener) {
		this.mListener = listener;
	}

	public static interface IReceiveSMSViewListener {

		public void onSmsBroadcast();

		public void onSmsReply();

		public void onSmsFastReply();

		public void onCancel();

	}

}
