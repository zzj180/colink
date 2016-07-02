package com.aispeech.aios.adapter.control;

/**
 * Created by Spring on 2016/2/15.
 * to do:
 */
public enum UIType {
    ShowWindow("awakeui"),
    Awake("awake"),
    //股票
    StockNodeUI("stock"),
    //导航
    NavigationUI("navigation"),
    //附近
    NearbyUI("nearby"),
    //天气
    WeatherUI("weahter"),
    //音乐
    MusicUI("music"),
    //phone
    Phone("phone"),
    //定位
    Location("location"),
    //限行
    VehiclerestrictionUI("vehiclerestriction"),
    WeChat("wechat"),
    //移除悬浮窗，可以在子线程调用
    DismissWindow("dismissWindow"),
    /**
     * 切换到音频伴随悬浮窗
     **/
    SwitchVoiceWindow("switchVoiceWindow"),
    /**
     * 退出音频伴随悬浮窗
     **/
    ExitVoiceWindow("exitVoiceWindow"),
    /**
     * 退出音频伴随悬浮窗，并恢复主悬浮窗
     **/
    RestoreMainWindow("restoreMainWindow"),
    //限行移除大图view
    VehiclerestrictionLargeImage("vehiclerestrictionLargeImage"),
    //共用loading处理
    LoadingUI("loading"),
    //停止loading界面
    CancelLoadingUI("cancelloading"),
    //显示Context
    Context("context"),
    //显示微信接人
    ShowPicker("showpicker"),
    ShowPickerUI("showpicker"),

    //取消微信接人
    DismissPicker("dismisspicker"),
    StartListening("startlistening"),
    StopListening("stoplistening"),
    StartRecognition("startrecognition"),
    StopRecognition("stoprecognition"),
    ListViewPrePage("listviewprepage"),
    ListViewNextPage("listviewnextpage"),

    PhoneAnswerStatus("phoneAnswer"),
    PhoneList("phoneList"),
    PhoneStopWait("stopwait"),
    PhoneUpdateWait("updatewait");


    private String type;

    UIType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


}
