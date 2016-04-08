package com.aispeech.aios.adapter.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.adapter.HelpInfoAdapter;
import com.aispeech.aios.adapter.adapter.SearchFmAdapter;
import com.aispeech.aios.adapter.adapter.SearchMusicAdapter;
import com.aispeech.aios.adapter.bean.FmItem;
import com.aispeech.aios.adapter.bean.FmSearchParam;
import com.aispeech.aios.adapter.bean.StockBean;
import com.aispeech.aios.adapter.bean.VehicleRestrictionBean;
import com.aispeech.aios.adapter.bean.WeatherBean;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.node.PhoneNode;
import com.aispeech.aios.adapter.node.TTSNode;
import com.aispeech.aios.adapter.ui.animation.Rotate3dAnimation;
import com.aispeech.aios.adapter.ui.widget.SettingsHelpLayout;
import com.aispeech.aios.adapter.ui.widget.StockLayout;
import com.aispeech.aios.adapter.ui.widget.VehLayout;
import com.aispeech.aios.adapter.ui.widget.WeatherLayout;
import com.aispeech.aios.adapter.util.StringUtil;

import java.util.List;

/**
 * @desc 悬浮窗展示UI
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class FloatWindowSmallView extends LinearLayout implements Rotate3dAnimation.OnFlipChangeListener {
    private static final String TAG = "AIOS-FloatWindowSmallView";
    private static int i_phone = 4;
    private static final int TIME_GAP_PHONE = 1000;

    private int mTipsMode;
    private boolean isHelpImageClicked = false;
    private boolean isMicListening = false;

    private Context mContext;
    //显示查询的等待过程的...
    private StringBuilder mSearchLoadingSB = new StringBuilder();
    //悬浮窗左上角的关闭按钮
    private ImageView mBackImage;
    // 悬浮窗左边的Mic按钮
    private RobotView mMicView;
    // 悬浮窗左边的帮助按钮
    private ImageView mHelpImage;

    //显示上下文的TextView
    private TextView mContextTextView;
    private RelativeLayout mResualtLayout;
    private ListView mResultListView;
    //电话确定按钮
    private Button mConfirmButton;
    //电话取消按钮
    private Button mCancleButton;
    //联系人姓名
    private TextView mContactName;
    //联系人电话号码
    private TextView mContactNumber;
    //联系人号码归属地
    private TextView mContactAddress;
    private LinearLayout mWaitLayout;
    //板块
    private WeatherLayout mWeatherLayout;
    private VehLayout mVehLayout;
    private StockLayout mStockLayout;
    private TextView mContentTitle;
    private SettingsHelpLayout mSettingHelpLayout;//设置界面
    private ImageView mSettingCloseImage;
    private LinearLayout mContextLayout;
    private ImageView mSearchLoadingImage;

    public FloatWindowSmallView(Context context) {
        super(context);

        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
        initViews();
    }

    private void initViews() {

        mWaitLayout = (LinearLayout) findViewById(R.id.ll_wait);
        mWeatherLayout = (WeatherLayout) findViewById(R.id.linlayout_weather);
        mVehLayout = (VehLayout) findViewById(R.id.linlayout_veh);
        mStockLayout = (StockLayout) findViewById(R.id.linlayout_stock);
        mBackImage = (ImageView) findViewById(R.id.iv_close);
        mMicView = (RobotView) findViewById(R.id.mic_view);
        mHelpImage = (ImageView) findViewById(R.id.iv_help);
        mContextTextView = (TextView) findViewById(R.id.tv_result);
        mConfirmButton = (Button) findViewById(R.id.wait_ok);
        mCancleButton = (Button) findViewById(R.id.wait_cancel);
        mContactName = (TextView) findViewById(R.id.contact_name);
        mContactNumber = (TextView) findViewById(R.id.contact_number);
        mContactAddress = (TextView) findViewById(R.id.contact_address);

        mContextLayout = (LinearLayout) findViewById(R.id.linLayout_context);
        mSearchLoadingImage = (ImageView) findViewById(R.id.img_search_loading);

        mResualtLayout = (RelativeLayout) findViewById(R.id.resualtLayout);
        mResultListView = (ListView) findViewById(R.id.resualtListView);
        mContentTitle = (TextView) findViewById(R.id.txt_Content_title);

        mSettingHelpLayout = (SettingsHelpLayout) findViewById(R.id.linLayout_settings_help);
        mSettingCloseImage = (ImageView) findViewById(R.id.setting_bt_close_home);
    }

    private String mString;
    /**
     * 设置联网progressBar显示
     */
    public void setProgressBarShow() {
        ((AnimationDrawable) mSearchLoadingImage.getDrawable()).start();
        mString = mContextTextView.getText().toString();
        mHandler.post(mSearchLoadingRunnable);
        mSearchLoadingImage.setVisibility(VISIBLE);
    }

    /**
     * 设置联网progressBar不显示
     */
    public void setProgressBarCancle() {
        mString = "";
        mSearchLoadingImage.clearAnimation();
        mHandler.removeCallbacks(mSearchLoadingRunnable);
    }

    /**
     * 设置帮助/设置页面是否被点击。需要在移除悬浮窗回归到初始状态
     * @param helpImageClicked 是否已被点击 true：是；false：否。默认为false
     */
    public void setHelpImageClicked(boolean helpImageClicked) {
        isHelpImageClicked = helpImageClicked;
    }

    /**
     * 设置众view的点击事件
     *
     * @param listener 要设置的listener
     */
    public void setOnClickListener(OnClickListener listener) {
        mBackImage.setOnClickListener(listener);
        mMicView.setOnClickListener(listener);
        mConfirmButton.setOnClickListener(listener);
        mCancleButton.setOnClickListener(listener);
        mHelpImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onHelpImageClicked();
            }
        });

        mSettingCloseImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingsCloseImageClicked();
            }
        });
    }

    private void onHelpImageClicked() {
        removeMainViews();

        if (isMicListening) {
            TTSNode.getInstance().getBusClient().publish(AiosApi.Other.UI_MIC_CLICK);
        }

        mBackImage.setVisibility(GONE);
        mSettingHelpLayout.setVisibility(VISIBLE);
        mSettingHelpLayout.onHelpSettingsClicked(mTipsMode, isHelpImageClicked);
  //      isHelpImageClicked = !isHelpImageClicked;

        float cx = mHelpImage.getWidth() / 2.0f;
        float cy = mHelpImage.getHeight() / 2.0f;

        Rotate3dAnimation animation = new Rotate3dAnimation(0, 180, cx, cy, 0);
        animation.setFillAfter(true);
        animation.setDuration(500);
        animation.setOnFlipChangeListener(this);
        mHelpImage.startAnimation(animation);
    }

    /**
     * 帮助/设置页面右上角的X关闭按钮点击事件。
     * 如果在帮助页面点击麦克风，需要调用此方法，回归初始状态
     */
    public void onSettingsCloseImageClicked() {
        removeMainViews();

        mBackImage.setVisibility(VISIBLE);
        isHelpImageClicked = false;

        switch (mTipsMode) {
            case HelpInfoAdapter.HELP_TYPE_HOME:
                mContextLayout.setVisibility(VISIBLE);
                break;
            case HelpInfoAdapter.HELP_TYPE_PHONE:
            case HelpInfoAdapter.HELP_TYPE_MUSIC:
            case HelpInfoAdapter.HELP_TYPE_NAVIGATION:
            case HelpInfoAdapter.HELP_TYPE_FM:
            case HelpInfoAdapter.HELP_TYPE_WEATHER:
            case HelpInfoAdapter.HELP_TYPE_VEH:
            case HelpInfoAdapter.HELP_TYPE_STOCK:
                mResualtLayout.setVisibility(VISIBLE);
                break;
        }
    }

    /**
     * 屏蔽Back键，处理按下导航栏Back键时的事件
     *
     * @param event 按键事件
     * @return 如果为Back键则返回true，否则继续分发
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //不响应按键时抬起的动作  条件判断
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
            onKeyDown(event.getKeyCode(), event);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 对系统返回键的监控
     *
     * @param keyCode 按键值
     * @param event   按键事件
     * @return true
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AILog.e(TAG, "=========" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyWindowManager.getInstance().removeSmallWindow();
                    isHelpImageClicked = false;
                }
            }, 800);

            BusClient bc = TTSNode.getInstance().getBusClient();
            if (null != bc) {
                bc.publish(AiosApi.Other.UI_PAUSE);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    /**
     * 设置电话等待界面确认按钮点击事件。
     * 因为在来电和去电同用一个等待界面，所以在来电和去电时须分别设置相应的listener
     * @param listener 确认按钮点击事件
     */
    public void setPhoneConfirmButtonClickListener(OnClickListener listener) {
        mConfirmButton.setOnClickListener(listener);
    }

    /**
     * 设置电话等待界面取消按钮点击事件。
     * 因为在来电和去电同用一个等待界面，所以在来电和去电时须分别设置相应的listener
     * @param listener 取消按钮点击事件
     */
    public void setPhoneCancelButtonClickListener(OnClickListener listener, String text) {
        mCancleButton.setText(text);
        mCancleButton.setOnClickListener(listener);
    }

    /**
     * 设置所有ListView的OnScrollListener
     *
     * @param listener 要设置的listener
     */
    public void setOnListViewScrollListener(OnScrollListener listener) {
        mResultListView.setOnScrollListener(listener);
    }

    /**
     * 设置搜索到的音乐列表中，item的点击事件
     *
     * @param listener 要设置的音乐列表item点击事件
     */
    public void setOnMusicListClickListener(OnMusicListItemClickListener listener) {
        this.mMusicListClickListener = listener;
    }

    /**
     * 设置搜索到的电台列表中，item的点击事件
     *
     * @param listener 要设置的音乐列表item点击事件
     */
    public void setOnFmListClickListener(OnFmListItemClickListener listener) {
        this.mOnFmListItemClickListener = listener;
    }

    /**
     * 设置搜索到的导航地址列表中，item的点击事件
     *
     * @param listener 要设置的导航地址列表item点击事件
     */
    public void setOnNavigListClickListener(OnNavigListItemClickListener listener) {
        this.mNavigListClickListener = listener;
    }

    /**
     * 设置当拨打电话倒计时结束时的任务事件
     *
     * @param listener 要设置的任务
     */
    public void setOnPhoneWaitFinishedListener(OnPhoneWaitFinishedListener listener) {
        this.mPhoneWaitFinishedListener = listener;
    }

    /**
     * 设置搜索到联系人列表中，item的点击事件
     *
     * @param listener 要设置的联系人列表点击事件
     */
    public void setOnPhoneListItemClickListener(OnPhoneListItemClickListener listener) {
        this.mOnPhoneListItemClickListener = listener;
    }

    /**
     * 更新对话上下文的显示内容
     * 注意：此上下文并未Android代码中的Context，而是实实在在的与AIOS对话产生的上下文
     *
     * @param context 要更新的内容
     */
    public void updateContext(String context) {
        mTipsMode = HelpInfoAdapter.HELP_TYPE_HOME;
        removeMainViews();

        mContextTextView.setText(context);
        mContextTextView.setMovementMethod(ScrollingMovementMethod.getInstance());//设置滚动条可以滑动
        mContextTextView.scrollTo(0, 0);//将滚动条定位到顶部
        mContextLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 显示天气UI
     * @param weather Weather实体对象
     * @param title UI上要显示的搜索关键字
     */
    public void showWeatherUI(WeatherBean weather, String title) {
        mTipsMode = HelpInfoAdapter.HELP_TYPE_WEATHER;
        removeMainViews();
        removeResultViews();

        mWeatherLayout.showUI(weather);

        String keyword = title + "的天气";
        setTitle(HelpInfoAdapter.HELP_TYPE_WEATHER, keyword);

        mWeatherLayout.setVisibility(VISIBLE);
        mResualtLayout.setVisibility(VISIBLE);
    }

    /**
     * 搜索到音乐列表后，更新音乐列表
     *
     * @param list  搜到的音乐列表
     * @param title UI上要显示的搜索关键字
     */
    public void showMusicListUI(final List<Object> list, String title) {
        mTipsMode = HelpInfoAdapter.HELP_TYPE_MUSIC;
        removeMainViews();
        removeResultViews();
        setTitle(HelpInfoAdapter.HELP_TYPE_MUSIC, title);

        mResultListView.setAdapter(new SearchMusicAdapter(mContext, list, title));
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mMusicListClickListener) {
                    mMusicListClickListener.onItemClick(list, position);
                }
            }
        });

        mResultListView.setVisibility(VISIBLE);
        mResualtLayout.setVisibility(VISIBLE);
    }

    /**
     * 搜索到电台列表后，更新电台列表
     *
     * @param list  搜到的电台列表
     * @param param FmSearchParam 对象
     */
    public void showFmListUI(final List<FmItem> list, FmSearchParam param) {
        mTipsMode = HelpInfoAdapter.HELP_TYPE_FM;
        removeMainViews();
        removeResultViews();

        String highNightWord = param.getKeyWord();
        setTitle(HelpInfoAdapter.HELP_TYPE_FM, highNightWord);

        mResultListView.setAdapter(new SearchFmAdapter(mContext, list, param));
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mOnFmListItemClickListener) {
                    mOnFmListItemClickListener.onItemClick(list, position);
                }
            }
        });

        mResultListView.setVisibility(VISIBLE);
        mResualtLayout.setVisibility(VISIBLE);
    }

    /**
     * 更新显示搜索到的导航列表
     *
     * @param pois    搜索到的列表
     * @param title   导航关键词
     * @param adapter 列表适配器
     */
    public void showNavigationUI(final List<Object> pois, String title, BaseAdapter adapter) {
        mTipsMode = HelpInfoAdapter.HELP_TYPE_NAVIGATION;
        removeMainViews();
        removeResultViews();
        setTitle(HelpInfoAdapter.HELP_TYPE_NAVIGATION, title);
        mResultListView.setAdapter(adapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mNavigListClickListener.onItemClick(pois, position);
            }
        });

        mResultListView.setVisibility(VISIBLE);
        mResualtLayout.setVisibility(VISIBLE);
    }

    /**
     * 更新显示电话列表
     *
     * @param phoneNum 联系人电话号码列表
     * @param title 搜索关键字
     * @param adapter 适配器
     */
    public void showPhoneListUI(final List<PhoneNode.PhoneItem> phoneNum, String title, SimpleAdapter adapter) {
        mTipsMode = HelpInfoAdapter.HELP_TYPE_PHONE;
        removeMainViews();
        removeResultViews();

        setTitle(HelpInfoAdapter.HELP_TYPE_PHONE, title);
        // StringUtil.highNightKeyword(mContext, mContentTitle, mContext.getResources().getString(R.string.list_title_result, title), title);
        mResultListView.setAdapter(adapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnPhoneListItemClickListener.onItemClick(phoneNum.get(position).pNumber);
            }
        });

        mResultListView.setVisibility(VISIBLE);
        mResualtLayout.setVisibility(VISIBLE);
    }

    /**
     * 显示去电等待界面，确认和取消
     * @param name 联系人姓名
     * @param number 联系人电话
     * @param address 联系人归属地
     */
    public void updatePhoneWait(String name, String number, String address) {
        removeMainViews();

        i_phone = 4;
        mConfirmButton.setText("确定(5s)");
        mContactName.setText(name);
        mContactNumber.setText(number);
        mContactAddress.setText(address);

        mHandler.postDelayed(mPhoneWaitRunnable, TIME_GAP_PHONE);
        mWaitLayout.setVisibility(VISIBLE);
    }

    /**
     * 取消显示去电等待界面
     * @param hide 是否移除悬浮窗
     */
    public void stopPhoneWait(boolean hide) {
        if (hide) {
            removeMainViews();
        }

        i_phone = 4;
        mHandler.removeCallbacks(mPhoneWaitRunnable);
    }

    /**
     * 是否接听来电UI更新
     *
     * @param name 联系人姓名
     * @param number 联系人电话号码
     * @param address 联系人归属地
     */
    public void isAcceptPhone(String name, final String number, String address) {
        removeMainViews();

        mConfirmButton.setText("接听");
        mContactName.setText(name);
        mContactNumber.setText(number);
        mContactAddress.setText(address);

        mWaitLayout.setVisibility(VISIBLE);
    }

    /**
     * 删除已启动的线程任务
     */
    public void removeCallBacks() {
        try {
            if (mHandler != null) {
                mHandler.removeCallbacks(mPhoneWaitRunnable);
                mHandler.removeCallbacks(mSearchLoadingRunnable);
                mHandler.removeCallbacks(mStartListeningRunnable);
                mHandler.removeCallbacks(mStopListeningRunnable);
                mHandler.removeCallbacks(mStartRecognitionRunnable);
                mHandler.removeCallbacks(mStopRecognitionRunnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取显示搜索到的列表ListView
     *
     * @return ListView
     */
    public ListView getResultListView() {
        return mResultListView;
    }

    /**
     * 开始倾听动画
     */
    public void startListening() {
        setProgressBarCancle();
        isMicListening = true;
        mHandler.post(mStartListeningRunnable);
    }

    /**
     * 停止倾听动画
     */
    public void stopListening() {
        isMicListening = false;
        mHandler.post(mStopListeningRunnable); // 麦克风动画停止
    }

    /**
     * 开始识别动画
     */
    public void startRecognition() {
        mHandler.post(mStartRecognitionRunnable); // 麦克风动画停止
    }

    /**
     * 停止识别动画
     */
    public void stopRecognition() {
        mHandler.post(mStopRecognitionRunnable); // 麦克风动画停止
    }

    private Handler mHandler = new Handler();

    /**
     * 开始动画的线程
     */
    private Runnable mStartListeningRunnable = new Runnable() {

        @Override
        public void run() {
            if (null != mMicView) {
                AILog.i(TAG, "startListening");
                mMicView.startListening();
            }
        }
    };

    /**
     * 停止倾听动画的线程
     */
    private Runnable mStopListeningRunnable = new Runnable() {

        @Override
        public void run() {
            AILog.i(TAG, "stopListening");
            mMicView.stopListening();
        }
    };

    /**
     * 开始识别动画的线程
     */
    private Runnable mStartRecognitionRunnable = new Runnable() {

        @Override
        public void run() {
            AILog.i(TAG, "startRecognition");
            mMicView.startRecognition();
        }
    };

    /**
     * 停止识别动画的线程
     */
    private Runnable mStopRecognitionRunnable = new Runnable() {

        @Override
        public void run() {
            AILog.i(TAG, "stopRecognition");
            mMicView.stopRecognition();
        }
    };

    private Runnable mPhoneWaitRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                mHandler.postDelayed(this, TIME_GAP_PHONE);
                mConfirmButton.setText("确定" + "(" + Integer.toString(i_phone--) + "s)");
                if (i_phone == -1) {
                    mHandler.removeCallbacks(mPhoneWaitRunnable); //倒计时结束
                    mPhoneWaitFinishedListener.onTimeFinished();
                    i_phone = 4;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable mSearchLoadingRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mHandler.postDelayed(this, TIME_GAP_PHONE);
                mSearchLoadingSB.append(".");
                mContextTextView.setText(mString + mSearchLoadingSB.toString());

                if (mSearchLoadingSB.length() == 3) {
                    mSearchLoadingSB = new StringBuilder();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private OnMusicListItemClickListener mMusicListClickListener;
    private OnFmListItemClickListener mOnFmListItemClickListener;
    private OnNavigListItemClickListener mNavigListClickListener;//Nav
    private OnPhoneWaitFinishedListener mPhoneWaitFinishedListener;
    private OnPhoneListItemClickListener mOnPhoneListItemClickListener;

    /**
     * 音乐搜索列表item的点击监听事件接口
     */
    public interface OnMusicListItemClickListener {
        void onItemClick(List<Object> musicList, int position);
    }

    /**
     * 电台搜索列表item的点击监听事件接口
     */
    public interface OnFmListItemClickListener {
        void onItemClick(List<FmItem> fmList, int position);
    }

    /**
     * 导航搜索列表item的点击监听事件接口
     */
    public interface OnNavigListItemClickListener {//Nav

        void onItemClick(List<Object> navigList, int position);
    }

    /**
     * 电话搜索列表item的点击监听事件接口
     */
    public interface OnPhoneListItemClickListener {
        void onItemClick(String phoneNum);
    }

    /**
     * 去电等待界面，倒计时结束时的监听事件接口
     */
    public interface OnPhoneWaitFinishedListener {
        void onTimeFinished();
    }


    //hide ALl ui right of floatwindow
    private void removeMainViews() {
        mContextLayout.setVisibility(GONE);
        mSearchLoadingImage.setVisibility(GONE);
        mWaitLayout.setVisibility(GONE);
        mResualtLayout.setVisibility(GONE);
        mSettingHelpLayout.setVisibility(GONE);

        mBackImage.setVisibility(VISIBLE);
    }


    private void removeResultViews() {
        mResultListView.setVisibility(GONE);
        mVehLayout.setVisibility(GONE);
        mStockLayout.setVisibility(GONE);
        mWeatherLayout.setVisibility(GONE);
    }

    private void setTitle(int type, String text) {
        Drawable navi_title = null;
        switch (type) {
            case HelpInfoAdapter.HELP_TYPE_NAVIGATION:
                navi_title = getResources().getDrawable(R.drawable.icon_nav_mapmark);
                break;
            case HelpInfoAdapter.HELP_TYPE_PHONE:
                navi_title = getResources().getDrawable(R.drawable.icon_phone_mapmark);
                break;
            case HelpInfoAdapter.HELP_TYPE_MUSIC:
                navi_title = getResources().getDrawable(R.drawable.icon_music_mapmark);
                break;
            case HelpInfoAdapter.HELP_TYPE_VEH:
                navi_title = getResources().getDrawable(R.drawable.icon_veh_mapmark);
                break;
            case HelpInfoAdapter.HELP_TYPE_STOCK:
                navi_title = getResources().getDrawable(R.drawable.icon_stock_mapmark);
                break;
            case HelpInfoAdapter.HELP_TYPE_WEATHER:
                navi_title = getResources().getDrawable(R.drawable.icon_weather_mapmark);
                break;
            case HelpInfoAdapter.HELP_TYPE_FM:
                navi_title = getResources().getDrawable(R.drawable.icon_fm_mark);
                break;
            default:
                break;
        }
        navi_title.setBounds(0, 0, navi_title.getMinimumWidth(), navi_title.getMinimumHeight());
        mContentTitle.setCompoundDrawables(navi_title, null, null, null);
        StringUtil.highNightWords(mContext, mContentTitle,
                mContext.getResources().getString(R.string.list_title_result, text), text);
    }

    /**
     * 显示股票查询结果UI
     * @param stockBean 股票实体bean
     * @param title UI上要显示的股票的关键字
     */
    public void showStockUI(StockBean stockBean, String title) {
        mTipsMode = HelpInfoAdapter.HELP_TYPE_STOCK;
        removeMainViews();
        removeResultViews();
        setTitle(HelpInfoAdapter.HELP_TYPE_STOCK, title);
        mStockLayout.showStockUI(stockBean);
        mResualtLayout.setVisibility(VISIBLE);
        mStockLayout.setVisibility(VISIBLE);

    }

    /**
     * 显示限行查询结果UI
     * @param vehBean 限行实体bean
     * @param title UI上要显示的搜索限行的关键字
     */
    public void showVehiclerestrictionUI(VehicleRestrictionBean vehBean, String title) {
        mTipsMode = HelpInfoAdapter.HELP_TYPE_VEH;
        removeMainViews();
        removeResultViews();
        mResualtLayout.setVisibility(VISIBLE);
        mVehLayout.setVisibility(VISIBLE);
        setTitle(HelpInfoAdapter.HELP_TYPE_VEH, title + "的限行");
        mVehLayout.showUI(vehBean);
    }

    /**
     * 移除限行大的详细图
     */
    public void removeVehLargeImage() {
        if (mVehLayout != null)
            mVehLayout.removeVehLargeImage();
    }

    @Override
    public void onFlipChange(boolean flipStatus) {
        if (flipStatus) {
            if (!isHelpImageClicked) {
                mHelpImage.setImageResource(R.drawable.bt_tips_normal);
            } else {
                mHelpImage.setImageResource(R.drawable.bt_tips_setting);
            }
        }
    }
}
