/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : SettingsViewPagerActivity.java
 * @ProjectName : uniCarGui
 * @PakageName : com.unisound.unicar.gui.ui
 * @version : 1.2
 * @Author : Xiaodong.He
 * @CreateDate : 2015-06-10
 * @modifyDate : 2015-11-10
 */
package com.colink.zzj.txzassistant.setting;

import java.util.ArrayList;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.R;
import com.colink.zzj.txzassistant.util.Logger;
import com.colink.zzj.txzassistant.util.StringUtil;
import com.colink.zzj.txzassistant.util.UserPerferenceUtil;
import com.colink.zzj.txzassistant.view.EditWakeupWordPopWindow;
import com.colink.zzj.txzassistant.view.EditWakeupWordPopWindow.OnEditWakeupwordPopListener;
import com.colink.zzj.txzassistant.view.SettingHelpPopupWindow;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZPowerManager;
import com.txznet.sdk.TXZTtsManager;
import com.txznet.sdk.TXZConfigManager.FloatToolType;
import com.txznet.sdk.TXZConfigManager.UserConfigListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * 
 * @author zzj
 * @date 20160330
 */
@SuppressLint("InflateParams")
public class SettingsViewPagerActivity extends Activity implements OnEditWakeupwordPopListener {

	private Context mContext;

	private ArrayList<View> mViewList = new ArrayList<View>();
	private LayoutInflater mLayoutInflater;
	private ViewGroup indicatorViewGroup;

	private ImageView mImageView;
	private ImageView[] mImageViews;
	private ViewPager mViewPager;

	// --page-1
	// Wake up
	private CheckBox mCBWakeup;
	private TextView mTvWakeupStatusClose;
	private TextView mTvWakeupStatusOpen;
	private ImageView mIvEditWakeupword;

	// TTS
	private RadioGroup mRgTTSSpeed;
	private RadioButton mRbTTSSlowly;
	private RadioButton mRbTTSStandard;
	private RadioButton mRbTTSFast;
	private TextView mTvTTSSpeedStatus;

	// --page-2
	// FloatMic
	private CheckBox mCBFloatMic;
	private TextView mTvFloatMicStatus;

	
	EditWakeupWordPopWindow mPop;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_settings_view_pager);
		mLayoutInflater = getLayoutInflater();
		mContext = getApplicationContext();

		TextView tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTopTitle.setText(R.string.common_control);
		ImageButton returnBtn = (ImageButton) findViewById(R.id.backBtn);
		returnBtn.setOnClickListener(mReturnListerner);

		// 添加layout
		mViewList.add(mLayoutInflater.inflate(R.layout.pager_settings_layout_1,
				null));

		initViewPager();
		mPop = new EditWakeupWordPopWindow(this);
		mPop.setOnEditWakeupwordPopListener(this);
		
//		UserPerferenceUtil.registerOnSharedPreferenceChangeListener(this, mPreferenceChangeListener);
	}

	private OnClickListener mReturnListerner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			onBackPressed();
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void initViewPager() {
		mImageViews = new ImageView[mViewList.size()];

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(pagerAdapter);

		indicatorViewGroup = (LinearLayout) findViewById(R.id.viewGroup);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_dot_normal);
		for (int i = 0; i < mViewList.size(); i++) {
			mImageView = new ImageView(SettingsViewPagerActivity.this);
			mImageView.setLayoutParams(new LayoutParams(bitmap.getWidth(),
					bitmap.getHeight()));
			mImageView.setPadding(0, 20, 0, 20);

			if (i == 0) {
				mImageView.setBackgroundResource(R.drawable.icon_dot_selected);
			} else {
				mImageView.setBackgroundResource(R.drawable.icon_dot_normal);
			}
			mImageViews[i] = mImageView;
			indicatorViewGroup.addView(mImageViews[i]);
		}
		bitmap.recycle();
		mViewPager.setOnPageChangeListener(mPageChangeLinstener);
	}

	private OnPageChangeListener mPageChangeLinstener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			for (int i = 0; i < mImageViews.length; i++) {
				if (i == arg0) {
					mImageViews[i]
							.setBackgroundResource(R.drawable.icon_dot_selected);
				} else {
					mImageViews[i]
							.setBackgroundResource(R.drawable.icon_dot_normal);
				}
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			switch (arg0) {
			case ViewPager.SCROLL_STATE_IDLE:

				break;
			case ViewPager.SCROLL_STATE_DRAGGING:

				break;
			default:
				break;
			}
		}
	};

	PagerAdapter pagerAdapter = new PagerAdapter() {

		@Override
		public int getCount() {
			return mViewList.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			/* < XiaoDong 20150721 added begin */
			if (mViewList == null) {
				return null;
			}
			if (null != mViewList && mViewList.size() < position) {
				return null;
			}
			/* XiaoDong 20150721 added End > */
			switch (position) {
			case 0:
				((ViewPager) container).addView(mViewList.get(position), 0);
				// Wake up setting
				mCBWakeup = (CheckBox) findViewById(R.id.cb_wakeup);
				mTvWakeupStatusClose = (TextView) findViewById(R.id.tv_status_wakeup_close);
				mTvWakeupStatusOpen = (TextView) findViewById(R.id.tv_status_wakeup_open);
				mIvEditWakeupword = (ImageView) findViewById(R.id.iv_setting_edit_wakeupword);
				mTvWakeupStatusOpen.setOnClickListener(mOnClickListener);

				// TTS setting
				mRgTTSSpeed = (RadioGroup) findViewById(R.id.rg_setting_tts_speed);
				mRbTTSSlowly = (RadioButton) findViewById(R.id.rBtn_tts_slowly);
				mRbTTSStandard = (RadioButton) findViewById(R.id.rBtn_tts_standard);
				mRbTTSFast = (RadioButton) findViewById(R.id.rBtn_tts_fast);
				mTvTTSSpeedStatus = (TextView) findViewById(R.id.tv_status_tts_play);

				// update UI Status
				initWakeupUIStatus();
				initTtsSpeedUIStatus();

				// set listener
				mCBWakeup.setOnCheckedChangeListener(mCbListener);
				mRgTTSSpeed.setOnCheckedChangeListener(mRgCheckedChangeListener);
				mIvEditWakeupword.setVisibility(View.VISIBLE);
				mIvEditWakeupword.setOnClickListener(mOnClickListener);

				mCBFloatMic = (CheckBox) findViewById(R.id.cb_float_mic);
				mTvFloatMicStatus = (TextView) findViewById(R.id.tv_status_float_mic);
				initFloatMicSettingUIStatus();
				mCBFloatMic.setOnCheckedChangeListener(mCbListener);
				break;
			}

			return mViewList.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			if (mViewList != null && mViewList.size() > 0
					&& mViewList.size() >= position) {
				((ViewPager) container).removeView(mViewList.get(position));
			}
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	};

	private void initWakeupUIStatus() {
		boolean isWakeUp = UserPerferenceUtil.isWakeupEnable(mContext);
		updateWakeupwordTextView(isWakeUp,
				UserPerferenceUtil.getWakeupWord(mContext));
		mCBWakeup.setChecked(isWakeUp);
	}

	private void initTtsSpeedUIStatus() {
		int ttsSpeed = UserPerferenceUtil.getTTSSpeed(mContext);
		if (UserPerferenceUtil.VALUE_TTS_SPEED_SLOWLY == ttsSpeed) {
			mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_slowly);
			mRbTTSSlowly.setChecked(true);
		} else if (UserPerferenceUtil.VALUE_TTS_SPEED_STANDARD == ttsSpeed) {
			mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_standard);
			mRbTTSStandard.setChecked(true);
		} else if (UserPerferenceUtil.VALUE_TTS_SPEED_FAST == ttsSpeed) {
			mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_fast);
			mRbTTSFast.setChecked(true);
		}
	}

	private void initFloatMicSettingUIStatus() {
		boolean isEnableFloatMic = UserPerferenceUtil.getFloatMicEnable(mContext);
		mTvFloatMicStatus.setText(isEnableFloatMic ? R.string.setting_float_mic_open
						: R.string.setting_float_mic_closed);
		mCBFloatMic.setChecked(isEnableFloatMic);
	}

	/**
	 * 
	 */
	private android.widget.CompoundButton.OnCheckedChangeListener mCbListener = new android.widget.CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.cb_wakeup:
				if (isChecked) {
					updateWakeupwordTextView(true,UserPerferenceUtil.getWakeupWord(mContext));
					showHelpTextPopWindow(mContext,R.string.setting_title_wakeup,R.string.setting_help_text_wakeup);
					TXZPowerManager.getInstance().reinitTXZ(new Runnable() {
						@Override
						public void run() {
							TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_WAKEUP);
						}
					});
				} else {
					updateWakeupwordTextView(false, null);
					TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
				}
				UserPerferenceUtil.setWakeupEnable(mContext, isChecked);
				break;
			case R.id.cb_float_mic:
				if (isChecked) {
					mTvFloatMicStatus.setText(R.string.setting_float_mic_open);
					showHelpTextPopWindow(mContext,R.string.setting_title_float_mic,R.string.setting_help_text_float_mic);
					TXZConfigManager.getInstance().showFloatTool(FloatToolType.FLOAT_NORMAL);
				} else {
					mTvFloatMicStatus.setText(R.string.setting_float_mic_closed);
					TXZConfigManager.getInstance().showFloatTool(FloatToolType.FLOAT_NONE);
				}
				UserPerferenceUtil.setFloatMicEnable(mContext, isChecked);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 
	 */
	private android.widget.RadioGroup.OnCheckedChangeListener mRgCheckedChangeListener = new android.widget.RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {

			case R.id.rBtn_tts_slowly:
				mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_slowly);
				UserPerferenceUtil.setTTSSpeed(mContext,UserPerferenceUtil.VALUE_TTS_SPEED_SLOWLY);
				TXZTtsManager.getInstance().setVoiceSpeed(55);
				break;
			case R.id.rBtn_tts_standard:
				mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_standard);
				UserPerferenceUtil.setTTSSpeed(mContext,UserPerferenceUtil.VALUE_TTS_SPEED_STANDARD);
				TXZTtsManager.getInstance().setVoiceSpeed(70);
				break;
			case R.id.rBtn_tts_fast:
				mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_fast);
				UserPerferenceUtil.setTTSSpeed(mContext,UserPerferenceUtil.VALUE_TTS_SPEED_FAST);
				TXZTtsManager.getInstance().setVoiceSpeed(85);
				break;

			default:
				break;
			}
		}
	};


	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_status_wakeup_open:
				showChangeTextPopWindow(mContext);
				break;
			case R.id.iv_setting_edit_wakeupword:
				showChangeTextPopWindow(mContext);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * show Edit Wakeupword PopWindow
	 * 
	 * @author zzj
	 * @date 2016-03-30
	 * @param context
	 */
	private void showChangeTextPopWindow(Context context) {
		mPop.showPopWindow(mViewPager);
	}

	/*private OnSharedPreferenceChangeListener mPreferenceChangeListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if (UserPerferenceUtil.KEY_WAKEUP_WORDS.equals(key)) {
				String wakeupWord = UserPerferenceUtil.getWakeupWord(mContext);
				boolean isWakeUpOpen = UserPerferenceUtil.isWakeupEnable(mContext);
				if (isWakeUpOpen) {
					updateWakeupwordTextView(isWakeUpOpen, wakeupWord);
				}
			} else if (UserPerferenceUtil.KEY_ENABLE_WAKEUP.equals(key)) {
				sendWakeupConfigure();
			}
		}
	};*/

	/**
	 * 
	 * @param isWakeupOpen
	 * @param wakeupWord
	 *            : if WakeUp is Open show wakeupWord
	 */
	private void updateWakeupwordTextView(boolean isWakeupOpen,
			String wakeupWord) {
		if (null == mTvWakeupStatusOpen) {
			return;
		}
		if (isWakeupOpen) {
			wakeupWord = StringUtil.addDoubleQuotationMarks(wakeupWord);
			mTvWakeupStatusOpen.setText(wakeupWord);

			mTvWakeupStatusClose.setVisibility(View.GONE);
			mTvWakeupStatusOpen.setVisibility(View.VISIBLE);
			// 下划线
			mTvWakeupStatusOpen.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			// 抗锯齿 added by LP at 2015-11-05
			mTvWakeupStatusOpen.getPaint().setAntiAlias(true);
			mIvEditWakeupword.setVisibility(View.VISIBLE);
		} else {
			mTvWakeupStatusOpen.setVisibility(View.GONE);
			mIvEditWakeupword.setVisibility(View.GONE);
			mTvWakeupStatusClose.setVisibility(View.VISIBLE);
			mTvWakeupStatusClose.setText(R.string.setting_wakeup_status_closed);
		}
	}


	/**
	 * showHelpTextPopWindow
	 * @author zzj
	 * @date 2016-03-30
	 * @param context
	 */
	private void showHelpTextPopWindow(Context context, int titleRes,
			int contentRes) {
		SettingHelpPopupWindow pop = new SettingHelpPopupWindow(context);
		pop.setTitle(titleRes);
		pop.setContent(contentRes);
		pop.showPopWindow(mViewPager);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	//	UserPerferenceUtil.unregisterOnSharedPreferenceChangeListener(mContext,mPreferenceChangeListener);
	}

	@Override
	public void onOkClick(String wakeupWord) {
		boolean isWakeUpOpen = UserPerferenceUtil.isWakeupEnable(mContext);
		Logger.d("isWakeUpOpen = " + isWakeUpOpen+"---wakeupWord="+wakeupWord);
		if (isWakeUpOpen) {
			if(!TextUtils.isEmpty(wakeupWord)){
				TXZConfigManager.getInstance().setWakeupKeywordsNew(new String[]{wakeupWord});
				UserPerferenceUtil.setWakeupWords(AdapterApplication.getApp(), wakeupWord);
				updateWakeupwordTextView(isWakeUpOpen, wakeupWord);
			}
		}
	}

	@Override
	public void onCancelClick() {
	}

}
