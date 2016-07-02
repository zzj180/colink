package com.aispeech.aios.adapter.control;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.aispeech.aios.adapter.bean.BaseBean;
import com.aispeech.aios.adapter.bean.StockBean;
import com.aispeech.aios.adapter.bean.VehicleRestrictionBean;
import com.aispeech.aios.adapter.bean.WeatherBean;
import com.aispeech.aios.adapter.node.PhoneNode;
import com.aispeech.aios.adapter.ui.MyWindowManager;
import com.aispeech.aios.adapter.util.MapOperateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 实现UI管理的类，统一进行UI调度
 * @auth AISPEECH
 * @date 2016-02-15
 * @copyright aispeech.com
 */
public class UiEventDispatcher {
    private static final String TAG = "UiEventDispatcher";
    private static boolean isShowWindow = false;//悬浮窗是否能移除,只在限行，股本，天气设置为true，其他领域不要调用,true为不能移除状态
    private static boolean isShowContext = false;//上下文是否显示,其他地方不要调用,true为不显示

    /**
     * 更新UI显示，用于更新列表，如音乐、电话联系人、FM、导航、附近
     *
     * @param type        所要更新的UI类型
     * @param mObjectList 列表list
     * @param mTitle      标题，用于显示在搜索结果上方
     */
    public static void notifyUpdateUI(UIType type, List<Object> mObjectList, String mTitle) {
        notifyUpdateUI(type, null, mObjectList, mTitle, 0);
    }

    /**
     * 更新UI显示，用于只传过来一个实体Bean的情况，如天气、限行、股票
     *
     * @param type     所要更新的UI类型
     * @param baseBean 实体Bean
     */
    public static void notifyUpdateUI(UIType type, BaseBean baseBean) {
        notifyUpdateUI(type, baseBean, null, baseBean.getTitle(), 0);
    }

    /**
     * 延时更新UI，主要用于延时移除悬浮窗
     *
     * @param type  所要更新的UI类型
     * @param delay 延时时间
     */
    public static void notifyUpdateUI(UIType type, long delay) {
        notifyUpdateUI(type, null, null, null, delay);
    }

    /**
     * 更新上下文显示，包括语音识别或者合成的结果
     *
     * @param context 上下文内容
     */
    public static void notifyUpdateUI(String context) {
        notifyUpdateUI(UIType.Context, null, null, context, 0);
    }

    /**
     * 更新UI显示，主要用于没有参数传递的UI更新，如上/下一页，显示/移除Loading界面等
     *
     * @param type 要更新的UI类型
     */
    public static void notifyUpdateUI(UIType type) {
        notifyUpdateUI(type, null, null, null, 0);
    }

    /**
     * 列表当前是否在第一页
     *
     * @return true 是； false 否。
     */
    public static boolean isListViewFirstPage() {
        return MyWindowManager.getInstance().isListFirstPage();
    }

    /**
     * 列表当前是否在最后一页
     *
     * @return true 是； false 否。
     */
    public static boolean isListViewLastPage() {
        return MyWindowManager.getInstance().isListLastPage();
    }

    public static boolean isShowWindow() {
        return MyWindowManager.getInstance().isShowing();
    }

    private synchronized static void notifyUpdateUI(UIType type, BaseBean baseBean, List<Object> objectList, String title, long delay) {

        Message msg = myHandler.obtainMessage();

        Bundle bundle = new Bundle();
        bundle.putParcelable("basebean", baseBean);
        bundle.putString("title", title);
        bundle.putLong("delay", delay);

        msg.setData(bundle);
        msg.obj = objectList;

        msg.what = type.ordinal();
        msg.sendToTarget();

    }

    private static Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (UIType.values()[msg.what]) {
                case ShowWindow:
                    //重置所有的状态
                    isShowWindow = false;
                    isShowContext = false;
                    MyWindowManager.getInstance().showWindow();
                    break;
                case Awake:
                    //重置所有的状态
                    isShowWindow = false;
                    isShowContext = false;

                    break;

                case StockNodeUI:
                    isShowWindow = true;
                    isShowContext = true;

                    StockBean stockBean = msg.getData().getParcelable("basebean");
                    String stockTitle = msg.getData().getString("title");
                    MyWindowManager.getInstance().showStockUI(stockBean, stockTitle);
                    break;

                case NavigationUI:
                    isShowContext = true;
                    List<Object> navList = (List<Object>) (msg.obj);
                    String navTitle = msg.getData().getString("title");
                    MyWindowManager.getInstance().showNavigationUI(navList, navTitle);
                    break;

                case NearbyUI:
                    isShowContext = true;
                    List<Object> nList = (List<Object>) (msg.obj);
                    String nTitle = msg.getData().getString("title");
                    MyWindowManager.getInstance().showNearbyUI(nList, nTitle);
                    break;

                case WeatherUI:
                    isShowContext = true;
                    isShowWindow = true;
                    WeatherBean weatherBean = msg.getData().getParcelable("basebean");
                    String weatherTitle = msg.getData().getString("title");
                    MyWindowManager.getInstance().showWeatherUI(weatherBean, weatherTitle);
                    break;

                case MusicUI:
                    isShowContext = true;
                    List<Object> musicList = (List<Object>) (msg.obj);
                    String musicTitle = msg.getData().getString("title");
                    MyWindowManager.getInstance().showMusicListUI(musicList, musicTitle);
                    break;
                case Location:
                    isShowContext = true;
                    MapOperateUtil.getInstance().locateByMap();
                    break;
                case VehiclerestrictionUI:
                    isShowWindow = true;
                    isShowContext = true;
                    String vehTitle = msg.getData().getString("title");
                    VehicleRestrictionBean vehBean = msg.getData().getParcelable("basebean");
                    MyWindowManager.getInstance().showVehiclerestrictionUI(vehBean, vehTitle);
                    break;

                case Phone:
                    isShowContext = true;
                    break;
                case WeChat:
                    isShowContext = true;
                    break;
                case VehiclerestrictionLargeImage:
                    MyWindowManager.getInstance().removeVehLargeImage();
                    break;

                case DismissWindow:
                    //在限行、股票、天气 isShowWindow=ture,即不移除悬浮窗，其他领域都移除
                    if (!isShowWindow) {
                        long delayTime = msg.getData().getLong("delay");
                        if (delayTime > 0) {
                            MyWindowManager.getInstance().removeSmallWindow(delayTime);
                        } else {
                            MyWindowManager.getInstance().removeSmallWindow();
                        }
                    }
                    break;

                case SwitchVoiceWindow:
                    MyWindowManager.getInstance().switchVoiceWindow();
                    MyWindowManager.getInstance().startListening();

                    break;

                case ExitVoiceWindow:
                    MyWindowManager.getInstance().exitVoiceWindow();
                    break;

                case RestoreMainWindow:
                    MyWindowManager.getInstance().restoreMainWindow();
                    break;
                case LoadingUI:
                    MyWindowManager.getInstance().setSearchProgressBarShow();
                    break;

                case CancelLoadingUI:
                    MyWindowManager.getInstance().setSearchProgressBarCancle();
                    break;

                case Context:
                    //在微信接人的时候不弹出悬浮窗
                    if (!isShowContext) {
                        String context = msg.getData().getString("title");
                        MyWindowManager.getInstance().showContextUI(context);
                    }
                    break;
//                case ShowPicker:
//                    //微信接人的时候标示为true
//                    isShowContext = true;
//                    isPicker = true;
//                    break;
                case ShowPickerUI:
                    isShowContext = true;
                    List<Object> list = (List<Object>) msg.obj;
                    String pickerTitle = msg.getData().getString("title");
                    MyWindowManager.getInstance().ShowPickerUI(list, pickerTitle);
                    break;
                case DismissPicker:
                    MyWindowManager.getInstance().removePickerWindow();
                    break;
                case StartListening:
                    MyWindowManager.getInstance().startListening();
                    if (!isShowContext) {
                        MyWindowManager.getInstance().showContextUI("正在倾听...");
                    }
                    break;

                case StopListening:
                    MyWindowManager.getInstance().stopListening();
                    break;

                case StartRecognition:
                    MyWindowManager.getInstance().startRecognition();
                    break;

                case StopRecognition:
                    MyWindowManager.getInstance().stopRecognition();
                    break;

                case ListViewPrePage:
                    MyWindowManager.getInstance().updateListPrePage();
                    break;

                case ListViewNextPage:
                    MyWindowManager.getInstance().updateListNextPage();
                    break;

                case PhoneAnswerStatus:
                    isShowContext = true;
                    String name = ((List<String>) msg.obj).get(0);
                    String number = ((List<String>) msg.obj).get(1);
                    String addressIn = ((List<String>) msg.obj).get(2);
                    MyWindowManager.getInstance().isAcceptPhone(name, number, addressIn);
                    break;

                case PhoneStopWait:
                    isShowContext = false;
                    long isWait = msg.getData().getLong("delay");
                    if (isWait > 0) {
                        MyWindowManager.getInstance().stopPhoneWait(true);
                    } else {
                        MyWindowManager.getInstance().stopPhoneWait(false);
                    }
                    break;

                case PhoneUpdateWait:
                    isShowContext = true;
                    List<String> result = (ArrayList<String>) msg.obj;
                    String phoneName = result.get(0);
                    String phoneNum = result.get(1);
                    String address = result.get(2);

                    if (TextUtils.isEmpty(phoneNum)) {
                        phoneNum = "未知号码";
                    }
                    MyWindowManager.getInstance().updatePhoneWait(phoneName, phoneNum, address);
                    break;


                case PhoneList:
                    isShowContext = true;
                    List<PhoneNode.PhoneItem> phoneNames = (ArrayList<PhoneNode.PhoneItem>) msg.obj;
                    String phoneTitle = msg.getData().getString("title");
                    MyWindowManager.getInstance().showPhoneListUI(phoneNames, phoneTitle);
                    break;
                default:
                    break;
            }
        }
    };


}
