/**
 * Copyright (c) 2012-2015 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : IRomControl.java
 * @ProjectName : vui_car_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.oem
 * @Author : Brant
 * @CreateDate : 2015-1-4
 */
package com.unisound.unicar.gui.oem;

import android.content.Context;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2015-1-4
 * @ModifiedBy : Brant
 * @ModifiedDate: 2015-1-4
 * @Modified: 2015-1-4: 实现基本功能
 */
public interface IRomControl {

    public void execApp(Context context, String action, String packageName);

    public void execSetting(Context context, String act, String obj);

    public void playMusic(String artist, String album, String song);

    public void playVideo(String video);

    public void navi(double latStart, double lngStart, double latEnd, double lngEnd);
}
