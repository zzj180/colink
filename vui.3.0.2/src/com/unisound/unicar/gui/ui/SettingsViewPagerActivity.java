/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : SettingsViewPagerActivity.java
 * @ProjectName : uniCarGui
 * @PakageName : com.unisound.unicar.gui.ui
 * @version : 1.1
 * @Author : Xiaodong.He
 * @CreateDate : 2015-06-10
 */
package com.unisound.unicar.gui.ui;

import java.util.ArrayList;

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
import cn.yunzhisheng.vui.assistant.WindowService;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.utils.FunctionHelpUtil;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.EditWakeupWordPopWindow;
import com.unisound.unicar.gui.view.ImageViewWithText;

/**
 * 
 * @author xiaodong
 * @date 20150610
 */
@SuppressLint("InflateParams")
public class SettingsViewPagerActivity extends Activity {

    private static final String TAG = SettingsViewPagerActivity.class.getSimpleName();

    private Context mContext;

    private ArrayList<View> mViewList = new ArrayList<View>();
    private LayoutInflater mLayoutInflater;
    private ViewGroup indicatorViewGroup;

    private ImageView mImageView;
    private ImageView[] mImageViews;
    private ViewPager mViewPager;

    // Wake up
    private CheckBox mCBWakeup;
    private TextView mTvWakeupStatusClose;
    private TextView mTvWakeupStatusOpen;
    private ImageView mIvEditWakeupword;

    // Map
    private RadioGroup mRgMapChoose;
    private RadioButton mRbMapGaode;
    private RadioButton mRbMapBaidu;
    private RadioButton mBbMapMore;
    private TextView mTvMapChooseStatus;

    // TTS
    private RadioGroup mRgTTSSpeed;
    private RadioButton mRbTTSSlowly;
    private RadioButton mRbTTSStandard;
    private RadioButton mRbTTSFast;
    private TextView mTvTTSSpeedStatus;

    // --page2
    // FloatMic
    private CheckBox mCBFloatMic;
    private TextView mTvFloatMicStatus;

    // oneShot
    private CheckBox mCBOneShot;
    private TextView mTvOneShotStatus;
    private TextView mTvOneshotTelpText;

    // AEC
    private LinearLayout mSettingAECLayout;
    private CheckBox mCBAEC;
    private TextView mTvAECStatus;
    private TextView mTvAECTelpText;

    private ImageViewWithText mIvtOta;
    private ImageViewWithText mIvtAboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.d(TAG, "!--->onCreate()----");
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
        mViewList.add(mLayoutInflater.inflate(R.layout.pager_settings_layout_1, null));
        mViewList.add(mLayoutInflater.inflate(R.layout.pager_settings_layout_2, null));

        initViewPager();

        UserPerferenceUtil.registerOnSharedPreferenceChangeListener(mContext, mPreferenceChangeListener);
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
        Logger.d(TAG, "onResume");
        initMapUIStatus();

    }

    private void initViewPager() {
        mImageViews = new ImageView[mViewList.size()];

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);

        indicatorViewGroup = (LinearLayout) findViewById(R.id.viewGroup);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_dot_normal);
        for (int i = 0; i < mViewList.size(); i++) {
            mImageView = new ImageView(SettingsViewPagerActivity.this);
            mImageView.setLayoutParams(new LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
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
                    mImageViews[i].setBackgroundResource(R.drawable.icon_dot_selected);
                } else {
                    mImageViews[i].setBackgroundResource(R.drawable.icon_dot_normal);
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
            int childCount = ((ViewPager) container).getChildCount();
            if (mViewList == null) {
                Logger.e(TAG, "!--->PagerAdapter instantiateItem error, mViewList is null.");
                return null;
            }
            if (null != mViewList && mViewList.size() < position) {
                Logger.e(TAG,
                        "!--->PagerAdapter instantiateItem error, return null. ViewList size = "
                                + mViewList.size() + "; position = " + position);
                return null;
            }
            Logger.d(TAG, "!--->PagerAdapter--position:" + position + "; childCount:" + childCount
                    + "; mViewList.size:" + mViewList.size());
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

                    // Map Choose setting
                    mRgMapChoose = (RadioGroup) findViewById(R.id.rg_setting_map_choose);
                    mRbMapGaode = (RadioButton) findViewById(R.id.rBtn_map_gaode);
                    mRbMapBaidu = (RadioButton) findViewById(R.id.rBtn_map_baidu);
                    mBbMapMore = (RadioButton) findViewById(R.id.rBtn_map_more);
                    mBbMapMore.setVisibility(GUIConfig.isSupportMoreMapSetting
                            ? View.VISIBLE
                            : View.GONE);
                    mTvMapChooseStatus = (TextView) findViewById(R.id.tv_status_map_choose);

                    // TTS setting
                    mRgTTSSpeed = (RadioGroup) findViewById(R.id.rg_setting_tts_speed);
                    mRbTTSSlowly = (RadioButton) findViewById(R.id.rBtn_tts_slowly);
                    mRbTTSStandard = (RadioButton) findViewById(R.id.rBtn_tts_standard);
                    mRbTTSFast = (RadioButton) findViewById(R.id.rBtn_tts_fast);
                    mTvTTSSpeedStatus = (TextView) findViewById(R.id.tv_status_tts_play);

                    // update UI Status
                    initWakeupUIStatus();
                    initMapUIStatus();
                    initTtsSpeedUIStatus();

                    // set listener
                    mCBWakeup.setOnCheckedChangeListener(mCbListener);
                    mRgMapChoose.setOnCheckedChangeListener(mRgCheckedChangeListener);
                    mRgTTSSpeed.setOnCheckedChangeListener(mRgCheckedChangeListener);
                    mBbMapMore.setOnClickListener(mOnClickListener);
                    mIvEditWakeupword.setOnClickListener(mOnClickListener);
                    break;

                case 1:
                    /* < XiaoDong 20150721 added Begin */
                    if (childCount == 0) {
                        Logger.w(TAG, "!--->position is 1 but childCount is 0");
                        ((ViewPager) container).addView(mViewList.get(position), 0);
                    }
                    /* XiaoDong 20150721 added End > */
                    ((ViewPager) container).addView(mViewList.get(position), 1);
                    // Float mic setting
                    mCBFloatMic = (CheckBox) findViewById(R.id.cb_float_mic);
                    mTvFloatMicStatus = (TextView) findViewById(R.id.tv_status_float_mic);

                    // OneShot setting
                    mCBOneShot = (CheckBox) findViewById(R.id.cb_oneshot);
                    mTvOneShotStatus = (TextView) findViewById(R.id.tv_status_oneshot);
                    mTvOneshotTelpText = (TextView) findViewById(R.id.tv_setting_oneshot_help_text);
                    mTvOneshotTelpText.setText(getString(R.string.setting_title_oneshot_hint,
                            UserPerferenceUtil.getWakeupWord(mContext)));

                    // AEC setting
                    mSettingAECLayout = (LinearLayout) findViewById(R.id.ll_setting_aec);
                    mCBAEC = (CheckBox) findViewById(R.id.cb_aec);
                    mTvAECStatus = (TextView) findViewById(R.id.tv_status_aec);
                    mTvAECTelpText = (TextView) findViewById(R.id.tv_setting_aec_help_text);
                    mTvAECTelpText.setText(getString(R.string.setting_title_aec_hint,
                            UserPerferenceUtil.getWakeupWord(mContext)));

                    mIvtOta = (ImageViewWithText) findViewById(R.id.ivt_setting_ota);
                    mIvtAboutUs = (ImageViewWithText) findViewById(R.id.ivt_setting_about_us);

                    // update UI Status
                    initFloatMicSettingUIStatus();
                    initOneShotSettingUIStatus();
                    initAECSettingUIStatus();

                    // set listener
                    mCBFloatMic.setOnCheckedChangeListener(mCbListener);
                    mCBOneShot.setOnCheckedChangeListener(mCbListener);
                    mCBAEC.setOnCheckedChangeListener(mCbListener);
                    mIvtOta.setOnClickListener(mOnClickListener);
                    mIvtAboutUs.setOnClickListener(mOnClickListener);
                    break;
            }

            return mViewList.get(position);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            Logger.d(TAG, "destroyItem-----position = " + position);
            if (mViewList != null && mViewList.size() > 0 && mViewList.size() >= position) {
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
        Logger.d(TAG, "!--->initWakeupUIStatus---isWakeUp = " + isWakeUp);
        updateWakeupwordTextView(isWakeUp, UserPerferenceUtil.getWakeupWord(mContext));
        mCBWakeup.setChecked(isWakeUp);
    }

    private void initTtsSpeedUIStatus() {
        int ttsSpeed = UserPerferenceUtil.getTTSSpeed(mContext);
        Logger.d(TAG, "!--->initTtsSpeedUIStatus---ttsSpeed = " + ttsSpeed);
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
        Logger.d(TAG, "!--->initFloatMicSettingUIStatus---isEnableFloatMic = " + isEnableFloatMic);
        mTvFloatMicStatus.setText(isEnableFloatMic
                ? R.string.setting_float_mic_open
                : R.string.setting_float_mic_closed);
        mCBFloatMic.setChecked(isEnableFloatMic);
    }

    private void initOneShotSettingUIStatus() {
        boolean isEnableOneShot = UserPerferenceUtil.getOneShotEnable(mContext);
        Logger.d(TAG, "!--->isEnableOneShot : " + isEnableOneShot);
        mTvOneShotStatus.setText(isEnableOneShot
                ? R.string.setting_float_mic_open
                : R.string.setting_float_mic_closed);
        mCBOneShot.setChecked(isEnableOneShot);
    }

    private void initAECSettingUIStatus() {
        if (GUIConfig.isSupportAECSetting) {
            mSettingAECLayout.setVisibility(View.VISIBLE);
            boolean isEnableAEC = UserPerferenceUtil.getAECEnable(mContext);
            Logger.d(TAG, "!--->isEnableAEC : " + isEnableAEC);
            mTvAECStatus.setText(isEnableAEC
                    ? R.string.setting_float_mic_open
                    : R.string.setting_float_mic_closed);
            mCBAEC.setChecked(isEnableAEC);
        } else {
            mSettingAECLayout.setVisibility(View.GONE);
        }
    }



    /**
	 * 
	 */
    private android.widget.CompoundButton.OnCheckedChangeListener mCbListener =
            new android.widget.CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    switch (buttonView.getId()) {
                        case R.id.cb_wakeup:
                            Logger.d(TAG, "onCheckedChanged---cb_wakeup--isChecked = " + isChecked);
                            if (isChecked) {
                                updateWakeupwordTextView(true,
                                        UserPerferenceUtil.getWakeupWord(mContext));
                            } else {
                                updateWakeupwordTextView(false, "");
                            }
                            UserPerferenceUtil.setWakeupEnable(mContext, isChecked);
                            Logger.d(TAG, "onCheckedChanged--cb_wakeup-----End.");
                            break;
                        case R.id.cb_float_mic:
                            Logger.d(TAG, "!--->cb_float_mic isChecked = " + isChecked);
                            if (isChecked) {
                                mTvFloatMicStatus.setText(R.string.setting_float_mic_open);
                            } else {
                                mTvFloatMicStatus.setText(R.string.setting_float_mic_closed);
                            }
                            UserPerferenceUtil.setFloatMicEnable(mContext, isChecked);
                            break;
                        case R.id.cb_oneshot:
                            Logger.d(TAG, "!--->cb_oneshot isChecked = " + isChecked);
                            if (isChecked) {
                                mTvOneShotStatus.setText(R.string.setting_float_mic_open);
                            } else {
                                mTvOneShotStatus.setText(R.string.setting_float_mic_closed);
                            }
                            UserPerferenceUtil.setOneShotEnable(mContext, isChecked);
                            sendOneShotConfigure();
                            break;
                        case R.id.cb_aec:
                            Logger.d(TAG, "!--->cb_aec isChecked = " + isChecked);
                            if (isChecked) {
                                mTvAECStatus.setText(R.string.setting_float_mic_open);
                            } else {
                                mTvAECStatus.setText(R.string.setting_float_mic_closed);
                            }
                            UserPerferenceUtil.setAECEnable(mContext, isChecked);
                            sendAECConfigure();
                            break;
                        default:
                            break;
                    }
                }
            };

    /**
	 * 
	 */
    private android.widget.RadioGroup.OnCheckedChangeListener mRgCheckedChangeListener =
            new android.widget.RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Logger.d(TAG,
                            "!--->---mRgCheckedChangeListener---onCheckedChanged----checkedId = "
                                    + checkedId);
                    switch (checkedId) {
                        case R.id.rBtn_map_gaode:
                            Logger.d(TAG, "!--->map gaode---");
                            mTvMapChooseStatus.setText(R.string.setting_map_choose_gaode);
                            UserPerferenceUtil.setMapChoose(mContext,
                                    UserPerferenceUtil.VALUE_MAP_AMAP);
                            break;
                        case R.id.rBtn_map_baidu:
                            Logger.d(TAG, "!--->map baidu----");
                            mTvMapChooseStatus.setText(R.string.setting_map_choose_baidu);
                            UserPerferenceUtil.setMapChoose(mContext,
                                    UserPerferenceUtil.VALUE_MAP_BAIDU);
                            break;
                        case R.id.rBtn_map_more:
                            Logger.d(TAG,
                                    "!--->mRgCheckedChangeListener---rBtn_map_more--do nothing--");
                            break;
                        case R.id.rBtn_tts_slowly:
                            Logger.d(TAG, "!--->tts slowly----");
                            mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_slowly);
                            UserPerferenceUtil.setTTSSpeed(mContext,
                                    UserPerferenceUtil.VALUE_TTS_SPEED_SLOWLY);
                            sendTTSSpeedConfigure();
                            break;
                        case R.id.rBtn_tts_standard:
                            Logger.d(TAG, "!--->tts standard----");
                            mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_standard);
                            UserPerferenceUtil.setTTSSpeed(mContext,
                                    UserPerferenceUtil.VALUE_TTS_SPEED_STANDARD);
                            sendTTSSpeedConfigure();
                            break;
                        case R.id.rBtn_tts_fast:
                            Logger.d(TAG, "!--->tts fast----");
                            mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_fast);
                            UserPerferenceUtil.setTTSSpeed(mContext,
                                    UserPerferenceUtil.VALUE_TTS_SPEED_FAST);
                            sendTTSSpeedConfigure();
                            break;
                        default:
                            break;
                    }
                }
            };

    private void sendWakeupConfigure() {
        Logger.d(TAG, "sendWakeupConfigure---");
        sendConfigure(WindowService.ACTION_SET_WAKEUP);
    }

    private void sendTTSSpeedConfigure() {
        sendConfigure(WindowService.ACTION_SET_TTSSPEED);
    }

    private void sendOneShotConfigure() {
        sendConfigure(WindowService.ACTION_SET_ONESHOT);
    }

    private void sendAECConfigure() {
        sendConfigure(WindowService.ACTION_SET_AEC);
    }

    private void sendConfigure(String action) {
        Logger.d(TAG, "!--->---sendConfigure-----");
        Intent intent = new Intent(mContext, WindowService.class);
        intent.setAction(action);
        startService(intent);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Logger.d(TAG, "!--->onClick = " + v.getId());
            switch (v.getId()) {
                case R.id.ivt_setting_ota:
                    Logger.d(TAG, "!--->---click OTA----");
                    Intent OTAIntent =
                            new Intent(SettingsViewPagerActivity.this, OTAActivity.class);
                    startActivity(OTAIntent);
                    break;
                case R.id.ivt_setting_about_us:
                    Logger.d(TAG, "!--->---click About us----");
                    Intent intent = new Intent(SettingsViewPagerActivity.this, AboutActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rBtn_map_more:
                    Logger.d(TAG, "!--->---mOnClickListener--click rBtn_map_more----");
                    Intent intentMore =
                            new Intent(SettingsViewPagerActivity.this,
                                    SettingMapViewPagerActivity.class);
                    startActivityForResult(intentMore, GUIConfig.ACTIVITY_REQUEST_CODE_CHOOSE_MAP);
                    break;
                case R.id.tv_status_wakeup_open:
                    Logger.d(TAG, "!--->---click tv_status_wakeup----");
                    showChangeTextPopWindow(mContext);
                    break;
                case R.id.iv_setting_edit_wakeupword:
                    Logger.d(TAG, "!--->---click edit_wakeupword----");
                    showChangeTextPopWindow(mContext);
                    break;
                default:
                    break;
            }
        }
    };

    private void initMapUIStatus() {
        int mapType = UserPerferenceUtil.getMapChoose(mContext);
        Logger.d(TAG, "!--->initMapUIStatus---mapType = " + mapType);
        if (null == mTvMapChooseStatus) {
            Logger.d(TAG, "!--->mTvMapChooseStatus is null, No need update Map UI Status.");
            return;
        }
        switch (mapType) {
            case UserPerferenceUtil.VALUE_MAP_AMAP:
                mTvMapChooseStatus.setText(R.string.setting_map_choose_gaode);
                mRbMapGaode.setChecked(true);
                break;
            case UserPerferenceUtil.VALUE_MAP_BAIDU:
                mTvMapChooseStatus.setText(R.string.setting_map_choose_baidu);
                mRbMapBaidu.setChecked(true);
                break;
            case UserPerferenceUtil.VALUE_MAP_TUBA:
                mTvMapChooseStatus.setText(R.string.setting_map_choose_tuba);
                mBbMapMore.setChecked(true);
                break;
            case UserPerferenceUtil.VALUE_MAP_DAODAOTONG:
                mTvMapChooseStatus.setText(R.string.setting_map_choose_daodaotong);
                mBbMapMore.setChecked(true);
                break;
            default:
                break;
        }

    }

    /* < xiaodong.he 20151028 added for wakeUp word change Begin */
    /**
     * show Edit Wakeupword PopWindow
     * 
     * @author xiaodong.he
     * @date 2015-11-01
     * @param context
     */
    private void showChangeTextPopWindow(Context context) {
        Logger.d(TAG, "showEditWakeupwordPopWindow----");
        EditWakeupWordPopWindow pop = new EditWakeupWordPopWindow(context);
        pop.showPopWindow(mViewPager);
    }

    private OnSharedPreferenceChangeListener mPreferenceChangeListener =
            new OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                        String key) {
                    Logger.d(TAG, "!--->onSharedPreferenceChanged: key " + key);
                    if (UserPerferenceUtil.KEY_WAKEUP_WORDS.equals(key)) {
                        String wakeupWord = UserPerferenceUtil.getWakeupWord(mContext);
                        boolean isWakeUpOpen = UserPerferenceUtil.isWakeupEnable(mContext);
                        if (isWakeUpOpen) {
                            updateWakeupwordTextView(isWakeUpOpen, wakeupWord);
                        }
                        if (null != mTvOneshotTelpText) {
                            mTvOneshotTelpText.setText(getString(
                                    R.string.setting_title_oneshot_hint, wakeupWord));
                        }
                    } else if (UserPerferenceUtil.KEY_ENABLE_WAKEUP.equals(key)) {
                        // XD added 20151029
                        sendWakeupConfigure();
                    }
                }
            };

    /**
     * 
     * @param isWakeupOpen
     * @param wakeupWord: if WakeUp is Open show wakeupWord
     */
    private void updateWakeupwordTextView(boolean isWakeupOpen, String wakeupWord) {
        if (null == mTvWakeupStatusOpen) {
            return;
        }
        Logger.d(TAG, "updateWakeupwordTextView--isWakeupOpen = " + isWakeupOpen + "; showText = "
                + wakeupWord);
        if (isWakeupOpen) {
            wakeupWord = FunctionHelpUtil.addDoubleQuotationMarks(wakeupWord);
            mTvWakeupStatusOpen.setText(wakeupWord);

            mTvWakeupStatusClose.setVisibility(View.GONE);
            mTvWakeupStatusOpen.setVisibility(View.VISIBLE);
            mTvWakeupStatusOpen.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            mIvEditWakeupword.setVisibility(View.VISIBLE);

        } else {
            mTvWakeupStatusOpen.setVisibility(View.GONE);
            mIvEditWakeupword.setVisibility(View.GONE);
            mTvWakeupStatusClose.setVisibility(View.VISIBLE);
            mTvWakeupStatusClose.setText(R.string.setting_wakeup_status_closed);
        }
    }

    /* xiaodong.he 20151028 added for wakeUp word change End > */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG, "!--->onActivityResult-----resultCode = " + resultCode);
        if (resultCode == GUIConfig.ACTIVITY_RESULT_CODE_SETTING_MAP_FINISH) {
            // initMapUIStatus(); //do it onResume()
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        UserPerferenceUtil.unregisterOnSharedPreferenceChangeListener(mContext,
                mPreferenceChangeListener);
    }

}
