/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : MicRecordingSessionView.java
 * @ProjectName : uniCarGUI
 * @PakageName : com.unisound.unicar.gui.view
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-7-09
 */
package com.unisound.unicar.gui.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.SupportDomain;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.ui.AllFunctionsActivity;
import com.unisound.unicar.gui.utils.ArithmeticUtil;
import com.unisound.unicar.gui.utils.ContactsUtil;
import com.unisound.unicar.gui.utils.FunctionHelpUtil;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;

/**
 * Microphone Recording Session View
 * 
 * @author xiaodong
 * @date 20150709
 */
public class MicRecordingSessionView extends LinearLayout implements
		ISessionView {

	private static final String TAG = MicRecordingSessionView.class
			.getSimpleName();

	private static final int COUNT_DEFAULT_SHOW = 2;

	private Context mContext;
	private TextView mTvHelpText1;
	private TextView mTvHelpText2;
	private TextView mTvHelpLink;

	private ImageView mIvTestVersionMark; // XD added 20150921

	private Handler mSessionManagerHandler;

	public MicRecordingSessionView(Context context) {
		this(context, null);
		mContext = context;

		initView();
	}

	public MicRecordingSessionView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 
	 * @param handler
	 */
	public void setSessionHandler(Handler handler) {
		Logger.d(TAG, "!--->setSessionHandler");
		mSessionManagerHandler = handler;
	}

	private void initView() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.session_mic_status_recording_view, this, true);

		mTvHelpText1 = (TextView) findViewById(R.id.tv_session_mic_status_recording_tips1);
		mTvHelpText2 = (TextView) findViewById(R.id.tv_session_mic_status_recording_tips2);
		mTvHelpLink = (TextView) findViewById(R.id.tv_recording_help_link);
		mTvHelpLink.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mTvHelpLink.setOnClickListener(mLinkClickListener);

		mIvTestVersionMark = (ImageView) findViewById(R.id.iv_test_version_mark);
		mIvTestVersionMark.setVisibility(GUIConfig.isTestVersion ? View.VISIBLE
				: View.GONE);

		ArrayList<String> showTexts = getShowTexts();
		if (showTexts.size() >= 2) {
			setHelpText1(showTexts.get(0));
			setHelpText2(showTexts.get(1));
		}

	}

	private void setHelpText1(String helpText) {
		mTvHelpText1.setText(helpText);
	}

	private void setHelpText2(String helpText) {
		mTvHelpText2.setText(helpText);
	}

	private OnClickListener mLinkClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_recording_help_link:
				Logger.d(TAG,
						"!--->tv_recording_help_link click. start AllFunctionsActivity");
				if (null != mSessionManagerHandler) {
					mSessionManagerHandler
							.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_CANCEL);
				}
				Intent intent = new Intent(mContext, AllFunctionsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public boolean isTemporary() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	/**
	 * get Random show text list which size is COUNT_DEFAULT_SHOW from
	 * SupportDomain list
	 * 
	 * @return
	 */
	private ArrayList<String> getShowTexts() {
		ArrayList<String> showTexts = new ArrayList<String>();
		/* < XD 20150807 modify begin */
		ArrayList<SupportDomain> supprotList = FunctionHelpUtil
				.getUnsupportedHelpTextList(mContext);
		/* XD 20150807 modify End > */
		int[] indexArray = ArithmeticUtil.getRandomArray(0,
				supprotList.size() - 1, COUNT_DEFAULT_SHOW);

		for (int i = 0; i < indexArray.length; i++) {
			SupportDomain sd = supprotList.get(indexArray[i]);
			String text = mContext.getResources().getStringArray(sd.resourceId)[0];
			if (SessionPreference.DOMAIN_CALL.equals(sd.type)
					|| SessionPreference.DOMAIN_SMS.equals(sd.type)) {
				/* < XD 20150715 modify for no contact with name find Begin */
				// text = ContactsUtil.getHelpTextWithContactName(mContext,
				// text);

				String contactName = ContactsUtil
						.getRandomContactName(mContext);
				if ("".equals(contactName) || null == contactName) {
					// if no contact find, show find no person
					text = mContext.getString(R.string.call_find_no_person);
				} else {
					Object[] nameFormatParam = new Object[1];
					nameFormatParam[0] = contactName;
					text = String.format(text, nameFormatParam);
				}
				/* XD 20150715 modify for no contact with name find End > */
			}
			text = FunctionHelpUtil.removeDoubleQuotationMarks(text);
			Logger.d(TAG, "!--->getShowTexts()--type :" + sd.type + "; text ="
					+ text);
			showTexts.add(text);
		}
		return showTexts;
	}

}
