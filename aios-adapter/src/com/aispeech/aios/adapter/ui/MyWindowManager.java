package com.aispeech.aios.adapter.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.aispeech.aimusic.model.MusicBean;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.adapter.NearbyAdapter;
import com.aispeech.aios.adapter.bean.FmItem;
import com.aispeech.aios.adapter.bean.FmSearchParam;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.bean.StockBean;
import com.aispeech.aios.adapter.bean.VehicleRestrictionBean;
import com.aispeech.aios.adapter.bean.WeatherBean;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.node.HomeNode;
import com.aispeech.aios.adapter.node.MusicNode;
import com.aispeech.aios.adapter.node.PhoneNode;
import com.aispeech.aios.adapter.service.FloatWindowService;
import com.aispeech.aios.adapter.ui.FloatWindowSmallView.OnFmListItemClickListener;
import com.aispeech.aios.adapter.ui.FloatWindowSmallView.OnMusicListItemClickListener;
import com.aispeech.aios.adapter.ui.FloatWindowSmallView.OnNavigListItemClickListener;
import com.aispeech.aios.adapter.ui.FloatWindowSmallView.OnPhoneListItemClickListener;
import com.aispeech.aios.adapter.ui.FloatWindowSmallView.OnPhoneWaitFinishedListener;
import com.aispeech.aios.adapter.util.ImageBlur;
import com.aispeech.aios.adapter.util.LocationDBHelper;
import com.aispeech.aios.adapter.util.MapOperateUtil;
import com.aispeech.aios.adapter.util.SendBroadCastUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @desc 实现悬浮窗管理的类，在进行UI定制的时候，请实现此类的定制借口
 * @auth zzj
 * @date 2016-01-13
 */

public class MyWindowManager {
    private static final String TAG = "AIOS-Adapter-MyWindowManager";
    private static final int MSG_SET_BACKGROUD = 0x01;
    private static final int MSG_REMOVE_WINDOW = 0x02;
    private static final int MSG_LOADING_WINDOW = 0x03;

    /**
     * 小悬浮窗View的实例
     **/
    private FloatWindowSmallView smallWindow;

    /**
     * 小悬浮窗View的参数
     **/
    private LayoutParams smallWindowParams;

    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     **/
    private static WindowManager mWindowManager;

    private String mContactNumber = "";

    private boolean isListFirstPage = true;
    private boolean isListLastPage = false;
    /**是否已经打开控制笑话/故事/段子播放的悬浮窗 **/
    private boolean isVoiceWindow = false;

    private int LIST_ITEMS_PER_PAGE = 4;

    private static MyWindowManager mInstance;
    private Context mContext;
    private boolean isShowing = false;
    private boolean isShowPicker = false;
    private Bitmap mWindowBgBitmap;
    private PickerView mView;
    private Handler mHandler = new Handler(AdapterApplication.getContext().getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_BACKGROUD:
                    smallWindow.setBackground(new BitmapDrawable(mContext.getResources(), mWindowBgBitmap));
                    break;
                case MSG_REMOVE_WINDOW:
                    removeWindow();
                    break;
                case MSG_LOADING_WINDOW:
                    smallWindow.setProgressBarShow();
                    break;
                default:
                    break;
            }
        }
    };

    private MyWindowManager(Context context) {
        this.mContext = context;
        smallWindow = new FloatWindowSmallView(mContext);

        initLayoutParams();

        smallWindow.setLayoutParams(smallWindowParams);
        smallWindow.setOnListViewScrollListener(mListViewScrollListener);
        smallWindow.setOnClickListener(mOnClickListener);
        smallWindow.setOnMusicListClickListener(mMusicListClickListener);
        smallWindow.setOnFmListClickListener(mFmListClickListener);
        smallWindow.setOnNavigListClickListener(mNavigListClickListener);
        smallWindow.setOnPhoneListItemClickListener(mOnPhoneListClickListener);
        smallWindow.setOnPhoneWaitFinishedListener(mPhoneWaitFinishedListener);
    }

    /**
     * 获取WindowManager的单例
     *
     * @return WindowManager单例
     */
    public static synchronized MyWindowManager getInstance() {
        if (mInstance == null || mInstance.smallWindow == null) {
            Log.e(TAG, "getInstance");
            //Context为从全局获取Service
            mInstance = new MyWindowManager(FloatWindowService.getRunningService());
        }
        return mInstance;
    }

    /**
     * 初始化悬浮窗的位置、大小
     */
    private void initLayoutParams() {
            smallWindowParams = new LayoutParams();
            smallWindowParams.type = 2009; //TYPE_PHONE;
            smallWindowParams.format = PixelFormat.RGBA_8888;
            smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
            smallWindowParams.x = 0; //起点x
            smallWindowParams.y = 0; //起点y
            smallWindowParams.width = LayoutParams.MATCH_PARENT; //长
            smallWindowParams.height = LayoutParams.MATCH_PARENT; //宽
            smallWindowParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    /**
     * 初始化播放声音时的悬浮窗
     */
    private void initVoiceLayoutParams(){
            smallWindowParams.type = 2009; //TYPE_PHONE;
            smallWindowParams.format = PixelFormat.RGBA_8888;
            smallWindowParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
            smallWindowParams.x = 0; //起点x
            smallWindowParams.y = 0; //起点y
            smallWindowParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
            smallWindowParams.width = LayoutParams.MATCH_PARENT; //长
            smallWindowParams.height = LayoutParams.WRAP_CONTENT; //宽
            smallWindowParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            BusClient bc = HomeNode.getInstance().getBusClient();
            switch (v.getId()) {
                case R.id.mic_view:
                    if (null != bc) {
                        bc.publish(AiosApi.Other.UI_MIC_CLICK);
                    }
                    smallWindow.onSettingsCloseImageClicked();
//                    UiEventDispatcher.notifyUpdateUI(UIType.Awake);
                    UiEventDispatcher.notifyUpdateUI(UIType.Context, null, "您有什么需要？");
                    break;
                case R.id.iv_close:
                    UiEventDispatcher.notifyUpdateUI(UIType.Awake);
                    UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
                    removeVehLargeImage();
                    if (null != bc) {
                        bc.publish(AiosApi.Other.UI_PAUSE);
                    }
                    break;
                case R.id.rl_voice:
                    if (null != bc) {
                        bc.publish(AiosApi.Other.UI_PAUSE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 联网搜索进度条显示
     */
    public void setSearchProgressBarShow() {
        mHandler.sendEmptyMessage(MSG_LOADING_WINDOW);
    }

    /**
     * 联网搜索进度条取消
     */
    public void setSearchProgressBarCancle() {
        smallWindow.setProgressBarCancle();
    }

    /**
     * 电话列表和导航地址列表的ScrollListener，请在此处添加两个ListView的Id
     */
    private AbsListView.OnScrollListener mListViewScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                if (view.getId() == R.id.resualtListView) {
                    int firstIndex = view.getFirstVisiblePosition();
                    int lastIndex = view.getLastVisiblePosition();
                    //滚动到底部
                    if (lastIndex == view.getCount() - 1) {
                        isListLastPage = !isViewCovered(view.getChildAt(lastIndex - firstIndex));
                    } else if (firstIndex == 0) {
                        isListFirstPage = !isViewCovered(view.getChildAt(0));
                    }

                    if (firstIndex > 0) {
                        isListFirstPage = false;
                    }

                    if (lastIndex < view.getCount() - 1) {
                        isListLastPage = false;
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            //There is nothing to do
        }
    };

    private OnMusicListItemClickListener mMusicListClickListener = new OnMusicListItemClickListener() {
        @Override
        public void onItemClick(List<Object> musicList, int position) {
            MusicNode.getInstance().getBusClient().publish(AiosApi.Other.UI_CLICK);//告知AIOS   语音交互停止

            ArrayList<MusicBean> sendList = new ArrayList<MusicBean>();
            sendList.add((MusicBean) musicList.get(position));
            MusicNode.getInstance().onItemSelect(new Gson().toJson(sendList));
        }
    };

    private OnFmListItemClickListener mFmListClickListener = new OnFmListItemClickListener() {

        @Override
        public void onItemClick(List<FmItem> fmList, int position) {
            FmItem fmItem = fmList.get(position);
        }
    };

    private OnNavigListItemClickListener mNavigListClickListener = new OnNavigListItemClickListener() {
        @Override
        public void onItemClick(List<Object> navigList, int position) {
            Log.d(TAG, "navigList:" + navigList);
            HomeNode.getInstance().getBusClient().publish(AiosApi.Other.UI_CLICK);//语音交互停止

            try {
                MapOperateUtil.getInstance().startNavigation((PoiBean) navigList.get(position));//开始导航
            } catch (Exception e) {
                e.printStackTrace();
            }
            removeSmallWindow();//移除悬浮窗
        }
    };

    private OnPhoneListItemClickListener mOnPhoneListClickListener = new OnPhoneListItemClickListener() {
        @Override
        public void onItemClick(String phoneNum) {
            sendBroadCastToCallPhone(phoneNum);
        }
    };

    private OnPhoneWaitFinishedListener mPhoneWaitFinishedListener = new OnPhoneWaitFinishedListener() {
        @Override
        public void onTimeFinished() {
            sendBroadCastToCallPhone(mContactNumber);
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blur() {

        float radius = 4f;
        int mBitmapWidth = mWindowBgBitmap.getWidth();
        int mBitmapHeith = mWindowBgBitmap.getHeight();
        Log.i(TAG, "bitmap width = " + mBitmapWidth);
        Log.i(TAG, "bitmap height = " + mBitmapHeith);

        Matrix matrix = new Matrix();
        matrix.postScale(0.2f, 0.2f); // 长和宽放大缩小的比例
        mWindowBgBitmap = Bitmap.createBitmap(mWindowBgBitmap, 0, 0, mBitmapWidth, mBitmapHeith, matrix, true);

        RenderScript rs = RenderScript.create(mContext);

//        Bitmap bitmap = mWindowBgBitmap.copy(mWindowBgBitmap.getConfig(), true);
        Allocation input = Allocation.createFromBitmap(rs, mWindowBgBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius /* e.g. 3.f */);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(mWindowBgBitmap);

        mHandler.sendEmptyMessage(MSG_SET_BACKGROUD);

        rs.destroy();
    }

    /**
     * 设置悬浮窗背景高斯模糊
     */
    public void setBackgroundBlur() {
        if (isShowing) return;
        mWindowBgBitmap = ImageBlur.screenshot();
        if (mWindowBgBitmap != null) {
            blur();
        } else {
            Log.i(TAG, "mWindowBgBitmap is null");
        }
    }

    /**
     * 创建悬浮窗
     * 这个方法必须实现，在AIOS唤醒时调用
     */
    public void createSmallWindow() {
        if (null != smallWindow && null != smallWindowParams && !isShowing) {
            smallWindow.removeMainViews();
            if(isVoiceWindow){
                isVoiceWindow = false;
                isShowing = true;
                initLayoutParams();
                smallWindow.switchToMainView();
                getWindowManager().addView(smallWindow, smallWindowParams);
                return ;
            }

            smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
            smallWindowParams.width = LayoutParams.MATCH_PARENT;
            getWindowManager().addView(smallWindow, smallWindowParams);
            isShowing = true;
        }
    }

    public void ShowPickerUI(List<Object> list, String mTips) {
        smallWindowParams.gravity = Gravity.CENTER;
        smallWindowParams.width = 600;
        if (isShowing) {
            removeWindow();
        }
        mView = new PickerView(AdapterApplication.getContext(), list, mTips);
        if (null != smallWindowParams && !isShowPicker) {
            getWindowManager().addView(mView, smallWindowParams);
            isShowPicker = true;
        }
    }

    public void removePickerWindow() {
        Log.i(TAG, "removeSmallWindow");
        if (isShowPicker) {
            try {
                if (mView != null) {
                    getWindowManager().removeView(mView);
                    isShowPicker = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void removeWindow() {
        Log.i(TAG, "removeSmallWindow");
        if (isShowing) {
            smallWindow.removeCallBacks();
            try {
                getWindowManager().removeView(smallWindow);
                isShowing = false;
//                smallWindow.setBackgroundColor(0xb0000000);
                mWindowBgBitmap = null;
                smallWindow.setHelpImageClicked(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从全布局的悬浮窗切换到语音伴随悬浮窗
     */
    public void switchVoiceWindow(){
        if ( ! isVoiceWindow ){
            isVoiceWindow = true;
            initVoiceLayoutParams();
            smallWindow.switchToVoiceView();
            mWindowManager.updateViewLayout(smallWindow,smallWindowParams);
        }
    }

    /**
     * 停止播放，关闭所有悬浮窗
     */
    public void exitVoiceWindow(){
        if( isVoiceWindow ){
            Log.e(TAG,"createSmallWindow");
            showContextUI("正在倾听…");
            startListening();
            removeWindow();
        }
    }

    /**
     * 停止播放，如果在音频伴随悬浮窗则恢复到主悬浮窗
     */
    public void restoreMainWindow(){

        if( isVoiceWindow ){
            isVoiceWindow = false;
            showContextUI("正在倾听…");
            startListening();

            initLayoutParams();
            smallWindow.switchToMainView();
            mWindowManager.updateViewLayout(smallWindow,smallWindowParams);
        }
    }
    /**
     * 将小悬浮窗从屏幕上移除。
     */
    public void removeSmallWindow() {
        mHandler.sendEmptyMessage(MSG_REMOVE_WINDOW);
    }

    /**
     * 延时将悬浮窗从屏幕上移除
     *
     * @param time 延时时间
     */
    public void removeSmallWindow(long time) {
        mHandler.sendEmptyMessageDelayed(MSG_REMOVE_WINDOW, time);
    }

    /**
     * 悬浮窗是否已显示
     *
     * @return 是否已显示
     */
    public boolean isShowing() {
        return isShowing;
    }

    /**
     * 开始倾听动画效果
     */
    public void startListening() {
        Log.i(TAG, "startListening");
        smallWindow.startListening();
    }

    /**
     * 停止倾听动画效果
     */
    public void stopListening() {
        Log.i(TAG, "stopListening");
        smallWindow.stopListening();
    }

    /**
     * 显示识别动画
     */
    public void startRecognition() {
        Log.i(TAG, "startRecognition");
        smallWindow.startRecognition();
    }

    /**
     * 停止识别动画
     */
    public void stopRecognition() {
        Log.i(TAG, "stopRecognition");
        smallWindow.stopRecognition();
    }

    /**
     * 附近列表填充
     *
     * @param pois  搜索到的poi列表
     * @param title UI显示的title
     */
    public void showNearbyUI(List<Object> pois, String title) {
        createSmallWindow();
        isListFirstPage = true;
        isListLastPage = pois.size() <= LIST_ITEMS_PER_PAGE;
        smallWindow.showNavigationUI(pois, title, new NearbyAdapter(mContext, pois));
    }

    /**
     * 导航搜索到的列表
     *
     * @param pois  搜索到的poi list
     * @param title UI显示的title
     */
    public void showNavigationUI(List<Object> pois, String title) {
        createSmallWindow();
        isListFirstPage = true;
        isListLastPage = false;

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map;
        for (int i = 0; i < pois.size(); i++) {
            map = new HashMap<String, Object>();
            map.put("nav_index",String.valueOf(i + 1) + ". ");
            map.put("nav_address",  ((PoiBean) (pois.get(i))).getName());
            map.put("nav_area", ((PoiBean) (pois.get(i))).getAddress());
            map.put("nav_distance", ((PoiBean) (pois.get(i))).getDisplayDistance());
            listItem.add(map);
        }
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listItem, R.layout.nav_item,
                new String[]{"nav_index","nav_address", "nav_area", "nav_distance"},
                new int[]{R.id.nav_index,R.id.nav_address, R.id.nav_area, R.id.nav_distance});

        if (pois.size() <= LIST_ITEMS_PER_PAGE) {
            isListLastPage = true;
        }
        smallWindow.showNavigationUI(pois, title, mSimpleAdapter);
    }

    /**
     * 显示悬浮窗
     */
    public void showWindow(){
        createSmallWindow();
    }
    /**
     * 显示对话上下文
     *
     * @param str 识别/合成的文本
     */
    public void showContextUI(String str) {
        createSmallWindow();
        smallWindow.updateContext(str);
    }

    /**
     * 显示天气UI
     *
     * @param weather Weather实体对象
     * @param title   UI上要显示的title
     */
    public void showWeatherUI(WeatherBean weather, String title) {
        createSmallWindow();
        smallWindow.showWeatherUI(weather, title);
    }

    /**
     * 显示音乐搜索列表
     *
     * @param list  搜索到的音乐列表
     * @param title UI上标题要显示的关键字
     */
    public void showMusicListUI(final List<Object> list, String title) {
        createSmallWindow();
        isListFirstPage = true;
        isListLastPage = false;
        smallWindow.showMusicListUI(list, title);
    }

    public void showFmListUI(final ArrayList<FmItem> list, FmSearchParam param) {
        createSmallWindow();
        isListFirstPage = true;
        isListLastPage = false;
        smallWindow.showFmListUI(list, param);
    }

    /**
     * 显示搜索结果列表的下一页
     */
    public void updateListNextPage() {
        createSmallWindow();
        ListView mListView = smallWindow.getResultListView();

        int firstIndex = mListView.getFirstVisiblePosition();
        int lastIndex = mListView.getLastVisiblePosition();
        int mNavListCount = mListView.getCount();
        View firtsItem = mListView.getChildAt(0); // 可见区域的第0个
        View lastItem = mListView.getChildAt(lastIndex - firstIndex);

        int top = 0 - ((firtsItem == null) ? 0 : firtsItem.getTop()); // 第一个元素隐藏部分高度
        int index_add = isViewCovered(lastItem) ? (lastIndex - firstIndex) : (lastIndex - firstIndex + 1);

        if (top == 0 && firstIndex == 0) {
            isListFirstPage = true;
        }
        if (mNavListCount > lastIndex + 1) {
            if ((firstIndex + index_add + index_add) > mNavListCount - 1) {// last page
                if (mNavListCount >= lastIndex + 1) {
                    mListView.setSelection(mNavListCount);
                    isListFirstPage = false;
                } else {
                    mListView.setSelection(0);
                    isListFirstPage = true;
                }
            } else {
                mListView.setSelection(firstIndex + index_add);
                isListFirstPage = false;
            }
            isListLastPage = firstIndex + index_add + index_add > mNavListCount - 1;
        } else {
            if (isViewCovered(lastItem)) {
                mListView.setSelection(mNavListCount);
                isListFirstPage = false;
            }
            isListLastPage = true;
        }
    }

    /**
     * 显示搜索结果列表的上一页
     */
    public void updateListPrePage() {
        createSmallWindow();
        ListView mListView = smallWindow.getResultListView();
        int firstIndex = mListView.getFirstVisiblePosition();
        int lastIndex = mListView.getLastVisiblePosition();
        View firstView = mListView.getChildAt(0);

        int length;
        int size = (int) Math.ceil((double) mListView.getHeight() / (double) firstView.getHeight());
        length = size;
        int index_sub = size - 1;
        if (isViewCovered(mListView.getChildAt(0))) {
            index_sub = lastIndex - firstIndex - 1;
        }
        if (mListView.getCount() > index_sub) {
            if (firstIndex / length == 0) {
                mListView.setSelection(0);
                isListFirstPage = true;
                isListLastPage = false;
            } else {
                if (firstIndex - index_sub == 0) {
                    isListFirstPage = true;
                    isListLastPage = false;
                } else {
                    isListFirstPage = false;
                    isListLastPage = false;
                }
                mListView.setSelection(firstIndex - index_sub);
            }
        } else {
            mListView.setSelection(0);
            isListFirstPage = true;
            isListLastPage = true;
        }
    }

    /**
     * 更新小悬浮窗的电话搜索列表
     */
    public void showPhoneListUI(List<PhoneNode.PhoneItem> list, String title) {
        createSmallWindow();
        isListFirstPage = true;
        isListLastPage = false;

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        LocationDBHelper dbHelper = LocationDBHelper.getInstance();
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("phone_index", String.valueOf(i + 1) + ". ");
            map.put("phone_name",  list.get(i).pName);
            map.put("phone_number", list.get(i).pNumber);
            String addr = dbHelper.findPhoneAreaByNumber(list.get(i).pNumber);
            if (null != addr) {
                map.put("phone_area", addr);
            } else {
                map.put("phone_area", "未知");
            }
            listItem.add(map);
        }
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listItem, R.layout.phone_item,
                new String[]{"phone_index","phone_name", "phone_number", "phone_area"},
                new int[]{R.id.phone_index,R.id.phone_name, R.id.phone_num, R.id.phone_area});

        Log.e(TAG, "number size ---- > " + list.size());
        if (listItem.size() <= LIST_ITEMS_PER_PAGE) {
            isListLastPage = true;
        }

        smallWindow.showPhoneListUI(list, title, mSimpleAdapter);
    }

    /**
     * 去电时显示确认取消按钮
     *
     * @param name    要拨打的联系人姓名
     * @param number  要拨打的联系人电话号码
     * @param address 要拨打的联系人归属地
     */
    public void updatePhoneWait(final String name, final String number, String address) {
        createSmallWindow();
        mContactNumber = number;
        smallWindow.updatePhoneWait(name, number, address);

        smallWindow.setPhoneConfirmButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadCastToCallPhone(mContactNumber);
            }
        });

        smallWindow.setPhoneCancelButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "点击取消按钮执行");
                stopPhoneWait(true);
                PhoneNode.getInstance().getBusClient().publish(AiosApi.Other.UI_CLICK);//停掉AIOS交互，
                removeSmallWindow();//移除悬浮窗
            }
        }, "取消");
    }

    /**
     * 取消显示去电确认取消按钮
     *
     * @param hide 是否移除悬浮窗
     */
    public void stopPhoneWait(Boolean hide) {
        smallWindow.stopPhoneWait(hide);
    }

    /**
     * 是否接听来电
     *
     * @param name    姓名
     * @param number  电话号码
     * @param address 归属地
     */
    public void isAcceptPhone(final String name, final String number, String address) {
        createSmallWindow();
        mContactNumber = number;
        smallWindow.isAcceptPhone(name, number, address);

        smallWindow.setPhoneConfirmButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadCastToAcceptPhone(mContactNumber);
            }
        });

        smallWindow.setPhoneCancelButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadCastToRejectPhone(mContactNumber);
            }
        }, "拒绝");
    }

    /**
     * 发送拨打电话广播给蓝牙，开始拨打电话，并在拨打电话后终止AIOS的交互
     *
     * @param phoneNum 要拨打的电话号码
     */
    public void sendBroadCastToCallPhone(String phoneNum) {
        SendBroadCastUtil.getInstance().sendBroadCast("com.colink.service.TelphoneService.TelephoneReceive", "number", phoneNum);
        PhoneNode.getInstance().getBusClient().publish(AiosApi.Other.UI_CLICK);
        removeSmallWindow();
    }

    /**
     * 发送接听电话广播给蓝牙
     *
     * @param phoneNum 要接听的电话号码
     */
    public void sendBroadCastToAcceptPhone(String phoneNum) {
        SendBroadCastUtil.getInstance().sendBroadCast("com.colink.service.TelphoneService.TelephoneAnswerReceive", "number", phoneNum);
        PhoneNode.getInstance().getBusClient().publish(AiosApi.Other.UI_CLICK);
        removeSmallWindow();
    }

    /**
     * 发送拒绝电话广播给蓝牙
     *
     * @param phoneNum 要拒绝的电话号码
     */
    public void sendBroadCastToRejectPhone(String phoneNum) {
        SendBroadCastUtil.getInstance().sendBroadCast("com.colink.service.TelphoneService.TelephoneHandupReceive", "number", phoneNum);
        PhoneNode.getInstance().getBusClient().publish(AiosApi.Other.UI_CLICK);
        removeSmallWindow();
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private WindowManager getWindowManager() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * view是否被遮挡
     *
     * @param view 要判断的view
     * @return 是否view被遮挡
     */
    public boolean isViewCovered(final View view) {
        Rect currentViewRect = new Rect();
        view.getGlobalVisibleRect(currentViewRect);

        int visibleHeight = currentViewRect.bottom - currentViewRect.top;
        float scale = 0.8f;
        float totalHeight = view.getHeight();
        float lastScale = visibleHeight / totalHeight;
        return lastScale < scale;
    }

    /**
     * 是否当前显示为列表的第一页，即列表的第一项是否显示在UI顶端
     *
     * @return 是否是在第一页 true：当前已经是第一页；false：当前不是第一页
     */
    public boolean isListFirstPage() {
        return isListFirstPage;
    }

    /**
     * 是否当前显示为列表的最后一页，即列表的最后一项是否显示在UI的底部
     *
     * @return 是否是在最后一页 true：当前已经是最后一页；false：当前不是最后一页
     */
    public boolean isListLastPage() {
        return isListLastPage;
    }

    /**
     * 显示股票查询结果UI
     *
     * @param stockBean 股票实体bean
     * @param title     UI上要显示的股票的关键字
     */
    public void showStockUI(StockBean stockBean, String title) {
        smallWindow.showStockUI(stockBean, title);
    }

    /**
     * 显示限行查询结果UI
     *
     * @param vehBean 限行实体bean
     * @param title   UI上要显示的搜索限行的关键字
     */
    public void showVehiclerestrictionUI(VehicleRestrictionBean vehBean, String title) {
        smallWindow.showVehiclerestrictionUI(vehBean, title);
    }

    /**
     * 移除限行大的详细图
     */
    public void removeVehLargeImage() {
        smallWindow.removeVehLargeImage();
    }

    /**
     * 判断当前是否处在帮助/设置界面，用于决定是否开启计时器
     */
    public boolean isHelpOrSettingPage(){
        return smallWindow.isHelpOrSettingPage();
    }
}
