/**
 * Copyright (c) 2012-2015 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : IRadio.java
 * @ProjectName : vui_car_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.oem
 * @Author : Brant
 * @CreateDate : 2015-1-4
 */
package com.unisound.unicar.gui.oem;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2015-1-4
 * @ModifiedBy : Brant
 * @ModifiedDate: 2015-1-4
 * @Modified: 2015-1-4: 实现基本功能
 */
public interface IRadio {
    public void openRadio(String channel, String frequency);

    public void closeRadio();
}
