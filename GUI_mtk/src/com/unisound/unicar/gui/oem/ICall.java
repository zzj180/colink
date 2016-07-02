/**
 * Copyright (c) 2012-2015 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : ICall.java
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
public interface ICall {
    /**
     * 拨打电话
     * 
     * @Description : call
     * @Author : Brant
     * @CreateDate : 2015-1-4
     * @param context
     * @param phoneNumber:要拨打的电话号码
     */
    public void call(Context context, String phoneNumber);

    /**
     * 接听电话
     * 
     * @Description : answerCall
     * @Author : Brant
     * @CreateDate : 2015-1-4
     */
    public void answerCall();

    /**
     * 挂断电话
     * 
     * @Description : hangupCall
     * @Author : Brant
     * @CreateDate : 2015-1-4
     */
    public void hangupCall();

    /**
     * 拒接电话
     * 
     * @Description : rejectCall
     * @Author : Brant
     * @CreateDate : 2015-1-4
     */
    public void rejectCall();
}
