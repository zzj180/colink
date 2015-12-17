/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : GUIMainActivity.java
 * @ProjectName : uniCarGUI
 * @PakageName : com.unisound.unicar.gui.ui
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-06-08
 */
package com.unisound.unicar.gui.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.TrackInfo;
import com.unisound.unicar.gui.preference.CommandPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.utils.AutoHelpTextUpdateUtil;
import com.unisound.unicar.gui.utils.FunctionHelpUtil;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.Network;
import com.unisound.unicar.gui.view.AutoTextView;
import com.unisound.unicar.gui.view.EditWakeupWordPopWindow;

@SuppressLint("NewApi")
public class GUIMainActivity extends Activity implements OnClickListener {
    private final static String TAG = GUIMainActivity.class.getSimpleName();

    private Context mContext;

    private ImageView startSpeak;

    /** Help Text Layout wake up close */
    private LinearLayout mLayoutHelpTextAuto;
    private AutoTextView mAutoTextView;

    /** Help Text Layout wake up open */
    private LinearLayout mLayoutHelpTextWakeupOpen;
    private TextView mTvWakeupHelpText;
    private ImageView mIvEditWakeupword;

    private ArrayList<View> mViewList = new ArrayList<View>();
    private LayoutInflater mLayoutInflater;
    private ViewGroup indicatorViewGroup;

    private ImageView mImageView;
    private ImageView[] mImageViews;
    private ViewPager mViewPager;

    public static final String ACTION_FINISH_GUIMAINACTIVITY =
            "com.unisound.unicar.gui.ACTION_FINISH_GUIMAINACTIVITY";

    public static final int MSG_UPDATE_AUTO_TEXT_VIEW = 1001;
    /* show AutoHelpTextView delay XD added 20151030 */
    public static final int MSG_UPDATE_MAIN_TOP_HELP_TEXT = 1002;

    private static final int TIME_UPDATE_MAIN_TOP_HELP_TEXT_DELAY = 100;

    private ArrayList<String> mHelpTextList = new ArrayList<String>();
    private AutoHelpTextUpdateUtil mHelpTextUpdateMgr = null;

    private TextView mTvHelpTextCall;
    private TextView mTvHelpTextPoi;
    private TextView mTvHelpTextMusic;
    private TextView mTvHelpTextLocalSearch;

    /**
     * 
     */
    private void initMainHeadView() {
        startSpeak = (ImageView) findViewById(R.id.startSpeak);
        startSpeak.setOnClickListener(this);

        mLayoutHelpTextAuto = (LinearLayout) findViewById(R.id.ll_help_text_auto);
        mAutoTextView = (AutoTextView) findViewById(R.id.tvAutoText);

        mLayoutHelpTextWakeupOpen = (LinearLayout) findViewById(R.id.ll_help_text_wakeup_open);
        mTvWakeupHelpText = (TextView) findViewById(R.id.tvWakeupHelpText);
        mIvEditWakeupword = (ImageView) findViewById(R.id.iv_edit_wakeupword);

        if (GUIConfig.isSupportUpdateWakeupWordSetting) {
            // 下划线
            mTvWakeupHelpText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            // 抗锯齿 added by LP at 2015-11-05
            mTvWakeupHelpText.getPaint().setAntiAlias(true);
            mTvWakeupHelpText.setOnClickListener(mOnClickListener);
            mIvEditWakeupword.setOnClickListener(mOnClickListener);
        }
    }

    private void initViewPager() {

        if (mViewList.size() > 0) {
            mImageViews = new ImageView[mViewList.size()];
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);

        indicatorViewGroup = (LinearLayout) findViewById(R.id.viewGroup);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_dot_normal);
        if (mViewList.size() > 0) {
            for (int i = 0; i < mViewList.size(); i++) {
                mImageView = new ImageView(GUIMainActivity.this);
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
        }
        bitmap.recycle();
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

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
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);
        // SDKInitializer.initialize(this.getApplicationContext()); //XD delete 20150828
        mLayoutInflater = getLayoutInflater();

        mContext = getApplicationContext();
        initMainHeadView();// XD added 20150722

        // add layout
        mViewList.add(mLayoutInflater.inflate(R.layout.pager_layout1, null));
        mViewList.add(mLayoutInflater.inflate(R.layout.pager_layout2, null));
        initViewPager();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        startWindowService();// xd added 20150706

        IntentFilter filter = new IntentFilter(ACTION_FINISH_GUIMAINACTIVITY);
        registerReceiver(mFinishReceiver, filter);
        // XD 20150722 added
        IntentFilter networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, networkFilter);

        UserPerferenceUtil.registerOnSharedPreferenceChangeListener(mContext,
                mPreferenceChangeListener);
    }

    /**
     * start WindowService xd added 20150706
     */
    private void startWindowService() {
        Logger.d(TAG, "!--->---startWindowService()-----");
        Intent i = new Intent(this, WindowService.class);
        i.setAction(WindowService.ACTION_START_REQUEST_MAKE_FINISHED);
        startService(i);
    }

    @Override
    public void onClick(View v) {
        int key = v.getId();
        switch (key) {
            case R.id.startSpeak:
                startTalk();
                break;
            default:
                break;
        }
    }

    private void startTalk() {
        Logger.d(TAG, "!--->startTalk----");
        Intent intent = new Intent(this, WindowService.class);
        intent.setAction(MessageReceiver.ACTION_START_TALK);
        startService(intent);
    }

    private void startCallOut() {
        Logger.d(TAG, "!--->startCallOut----");
        Intent intent = new Intent(this, WindowService.class);
        intent.setAction(MessageReceiver.ACTION_START_CALL_OUT);
        startService(intent);
    }

    private void startNavigation() {
        Logger.d(TAG, "!--->startNavigation----");
        Intent intent = new Intent(this, WindowService.class);
        intent.setAction(MessageReceiver.ACTION_START_NAVIGATION);
        startService(intent);
    }

    private void startMusic() {
        Logger.d(TAG, "!--->startMusic----");
        Intent intent = new Intent(this, WindowService.class);
        intent.setAction(MessageReceiver.ACTION_START_MUSIC);
        startService(intent);
    }

    private void startKWMusic() {
        Logger.d(TAG, "startKWMusic-----");
        MessageSender messageSender = new MessageSender(mContext);
        Intent musicIntent = new Intent(CommandPreference.ACTION_MUSIC_DATA);
        Bundle bundle = new Bundle();
        TrackInfo track = new TrackInfo();
        bundle.putParcelable("track", track);
        musicIntent.putExtras(bundle);
        messageSender.sendOrderedMessage(musicIntent, null);
    }


    private void startLocalSearch() {
        Logger.d(TAG, "!--->startLocalSearch----");
        Intent intent = new Intent(this, WindowService.class);
        intent.setAction(MessageReceiver.ACTION_START_LOCAL_SEARCH);
        startService(intent);
    }

    /**
     * show Edit Wakeupword PopWindow
     * 
     * @author xiaodong.he
     * @date 2015-11-01
     * @param context
     */
    private void showEditWakeupwordPopWindow(Context context) {
        Logger.d(TAG, "showEditWakeupwordPopWindow----");
        EditWakeupWordPopWindow pop = new EditWakeupWordPopWindow(context);
        pop.showPopWindow(mViewPager);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.tvWakeupHelpText:
                    Logger.d(TAG, "tvWakeupHelpText click");
                    showEditWakeupwordPopWindow(mContext);
                    break;
                case R.id.iv_edit_wakeupword:
                    Logger.d(TAG, "iv_edit_wakeupword click");
                    showEditWakeupwordPopWindow(mContext);
                    break;
                case R.id.bluetooth:
                    Logger.d(TAG, "bluetooth button onclicked");
                    startCallOut();
                    // startHelpScreen(0);
                    break;

                case R.id.music:
                    Logger.d(TAG, "music button onclicked");
                    // startMusic();
                    startKWMusic(); // XD modify 20151020
                    break;

                case R.id.poi:
                    Logger.d(TAG, "poi button onclicked");
                    startNavigation();
                    break;

                case R.id.main_local_search:
                    Logger.d(TAG, "incity search button onclicked");
                    startLocalSearch();
                    break;

                case R.id.setting:
                    Logger.d(TAG, "setting button onclicked");
                    Intent settingIntent =
                            new Intent(GUIMainActivity.this, SettingsViewPagerActivity.class);
                    startActivity(settingIntent);
                    // startHelpScreen(4);
                    break;

                // case R.id.weather:
                // Logger.d(TAG, "weather button onclicked");
                // startHelpScreen(5);
                // break;
                // case R.id.stock:
                // Logger.d(TAG, "stock button onclicked");
                // startHelpScreen(6);
                // break;

                case R.id.main_all_function:
                    Logger.d(TAG, "!--->main_all_function button onclicked");
                    Intent functionIntent =
                            new Intent(GUIMainActivity.this, AllFunctionsActivity.class);
                    startActivity(functionIntent);
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
                    FrameLayout bluetooth = (FrameLayout) findViewById(R.id.bluetooth);
                    FrameLayout poi = (FrameLayout) findViewById(R.id.poi);
                    FrameLayout music = (FrameLayout) findViewById(R.id.music);
                    FrameLayout incitySearch = (FrameLayout) findViewById(R.id.main_local_search);

                    bluetooth.setOnClickListener(mOnClickListener);
                    poi.setOnClickListener(mOnClickListener);
                    music.setOnClickListener(mOnClickListener);
                    incitySearch.setOnClickListener(mOnClickListener);

                    mTvHelpTextCall = (TextView) findViewById(R.id.tv_help_text_call);
                    mTvHelpTextPoi = (TextView) findViewById(R.id.tv_help_text_poi);
                    mTvHelpTextMusic = (TextView) findViewById(R.id.tv_help_text_music);
                    mTvHelpTextLocalSearch =
                            (TextView) findViewById(R.id.tv_help_text_local_search);
                    boolean isOnline = Network.isNetworkConnected(mContext);
                    updateDomainButtonHelpText(isOnline);
                    break;
                case 1:
                    /* < XiaoDong 20150721 added Begin */
                    if (childCount == 0) {
                        Logger.w(TAG, "!--->position is 1 but childCount is 0");
                        ((ViewPager) container).addView(mViewList.get(position), 0);
                    }
                    /* XiaoDong 20150721 added End > */

                    ((ViewPager) container).addView(mViewList.get(position), 1);
                    FrameLayout setting = (FrameLayout) findViewById(R.id.setting);
                    setting.setOnClickListener(mOnClickListener);
                    FrameLayout allFunction = (FrameLayout) findViewById(R.id.main_all_function);
                    allFunction.setOnClickListener(mOnClickListener);
                    break;
            }
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            Logger.d(TAG, "destroyItem-----position = " + position);
            if (mViewList != null && mViewList.size() > 0 && mViewList.size() >= position) {
                Logger.d(TAG, "!--->destroyItem---mViewList size" + mViewList.size());
                ((ViewPager) container).removeView(mViewList.get(position));
            }
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    };

    private void startHelpScreen(int position) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }


    /**
	 * 
	 */
    BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.d(TAG, "!--->mFinishReceiver action = " + action);
            if (ACTION_FINISH_GUIMAINACTIVITY.equals(action)) {
                // startMainActivity(); //xd delete 20150706
                GUIMainActivity.this.finish();
            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        Logger.d(TAG, "!--->dispatchKeyEvent()-------getKeyCode =" + event.getKeyCode());
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // this.finish();
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d(TAG, "!--->onResume...");
        if (mContext == null) {
            mContext = getApplicationContext();
            Logger.d(TAG, "mContext = " + mContext);
        }
        updateMainTopHelpText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d(TAG, "!--->onPause...");

    }

    /* < xiaodong.he 20151019 added for Help Text Begin */
    private OnSharedPreferenceChangeListener mPreferenceChangeListener =
            new OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                        String key) {
                    Logger.d(TAG, "!--->onSharedPreferenceChanged: key " + key);
                    if (UserPerferenceUtil.KEY_ENABLE_WAKEUP.equals(key)) {
                        // do updateMainTopHelpText(); 100ms delay
                        // mUIHandler.sendEmptyMessageDelayed(MSG_UPDATE_MAIN_TOP_HELP_TEXT,
                        // TIME_UPDATE_MAIN_TOP_HELP_TEXT_DELAY);
                    } else if (UserPerferenceUtil.KEY_WAKEUP_WORDS.equals(key)) {
                        boolean isWakeUpOpen = UserPerferenceUtil.isWakeupEnable(mContext);
                        if (isWakeUpOpen && null != mTvWakeupHelpText) {
                            mTvWakeupHelpText.setText(FunctionHelpUtil
                                    .addDoubleQuotationMarks(UserPerferenceUtil
                                            .getWakeupWord(mContext)));
                        }
                    }
                }
            };

    /**
     * 
     * @param mContext
     */
    private void updateMainTopHelpText() {
        boolean isWakeUpOpen = UserPerferenceUtil.isWakeupEnable(mContext);
        Logger.d(TAG, "updateMainTopHelpText--isWakeUpOpen = " + isWakeUpOpen);
        if (isWakeUpOpen) {
            mLayoutHelpTextWakeupOpen.setVisibility(View.VISIBLE);
            mLayoutHelpTextAuto.setVisibility(View.GONE);
            releaseAuotHelpTextUpdateMgr();
            mTvWakeupHelpText.setText(FunctionHelpUtil.addDoubleQuotationMarks(UserPerferenceUtil
                    .getWakeupWord(mContext)));
            if (GUIConfig.isSupportUpdateWakeupWordSetting) {
                mIvEditWakeupword.setVisibility(View.VISIBLE);
            } else {
                mIvEditWakeupword.setVisibility(View.GONE);
            }
        } else {
            showAutoHelpTextView();
        }
    }

    /**
     * 
     */
    private void showAutoHelpTextView() {
        Logger.d(TAG, "showAutoHelpTextView-----");
        mLayoutHelpTextWakeupOpen.setVisibility(View.GONE);
        mLayoutHelpTextAuto.setVisibility(View.VISIBLE);
        updateAutoHelpText(Network.isNetworkConnected(mContext));
    }

    /**
     * update Domain Button Help Text
     * 
     * @param isOnline
     */
    private void updateDomainButtonHelpText(boolean isOnline) {
        if (null != mTvHelpTextCall) {
            mTvHelpTextCall.setText(FunctionHelpUtil.getMainPageCallTelpText(mContext, isOnline));
        }
        if (null != mTvHelpTextPoi) {
            mTvHelpTextPoi.setText(FunctionHelpUtil.getMainPageRouteHelpText(mContext, isOnline));
        }
        if (null != mTvHelpTextMusic) {
            mTvHelpTextMusic.setText(FunctionHelpUtil.getMainPageMusicHelpText(mContext, isOnline));
        }
        if (null != mTvHelpTextLocalSearch) {
            mTvHelpTextLocalSearch.setText(FunctionHelpUtil.getMainPageLocalSearchHelpText(
                    mContext, isOnline));
        }
    }

    /* xiaodong.he 20151019 added for Help Text End > */

    /* < XiaodDong 20150721 added for Auto Help Text Begin */
    private Handler mUIHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_AUTO_TEXT_VIEW:
                    String text = (String) msg.obj;
                    // Logger.d(TAG, "!--->MSG_UPDATE_AUTO_TEXT_VIEW---text = "+text);
                    if (null != mAutoTextView) {
                        mAutoTextView.setText(text);
                    }
                    break;
                case MSG_UPDATE_MAIN_TOP_HELP_TEXT:
                    Logger.d(TAG, "MSG_UPDATE_MAIN_TOP_HELP_TEXT");
                    updateMainTopHelpText();
                    break;
                default:
                    break;
            }
        };
    };


    /**
     * update Help Text
     * 
     * @param isConnected
     */
    private void updateAutoHelpText(boolean isConnected) {
        mHelpTextList = FunctionHelpUtil.getMainPageHelpTextList(mContext, isConnected);
        Logger.d(TAG, "!--->updateHelpText---isConnected = " + isConnected
                + "; mHelpTextList size = " + mHelpTextList.size());
        if (null == mHelpTextUpdateMgr) {
            mHelpTextUpdateMgr = new AutoHelpTextUpdateUtil(mHelpTextList, mUIHandler);
        } else {
            Logger.d(TAG, "!--->mUpdateHelpTextThread interrupt.");
            mHelpTextUpdateMgr.stop();
            mHelpTextUpdateMgr.setHelpTextList(mHelpTextList);
        }
        mHelpTextUpdateMgr.start();
    }

    /**
	 * 
	 */
    private BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                boolean isConnected = Network.isNetworkConnected(mContext);
                boolean isWakeUpOpen = UserPerferenceUtil.isWakeupEnable(mContext);
                Logger.d(TAG, "!--->mNetworkStateReceiver--onReceive--isConnected:" + isConnected
                        + "; isWakeUpOpen = " + isWakeUpOpen);
                if (!isWakeUpOpen) {
                    updateAutoHelpText(isConnected);
                }

                updateDomainButtonHelpText(isConnected);// XD added 20151019
            }
        }
    };

    /* XiaodDong 20150721 added for Auto Help Text End */

    private void releaseAuotHelpTextUpdateMgr() {
        if (null != mHelpTextUpdateMgr) {
            mHelpTextUpdateMgr.stop();
            mHelpTextUpdateMgr.setHelpTextList(null);
            mHelpTextUpdateMgr = null;
        }
        if (null != mHelpTextList) {
            mHelpTextList.clear();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Logger.d(TAG, "!--->onDestroy...");

        releaseAuotHelpTextUpdateMgr();
        unregisterReceiver(mFinishReceiver);
        unregisterReceiver(mNetworkStateReceiver);// XD added 20150722

        UserPerferenceUtil.unregisterOnSharedPreferenceChangeListener(mContext,
                mPreferenceChangeListener);
    }

}
