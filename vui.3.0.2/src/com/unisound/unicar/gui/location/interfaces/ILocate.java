/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : ILocate.java
 * @ProjectName : vui_datamodel
 * @PakageName : cn.yunzhisheng.vui.location.operation
 * @Author : Brant
 * @CreateDate : 2014-11-1
 */
package com.unisound.unicar.gui.location.interfaces;


/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-11-1
 * @ModifiedBy : Zhuoran
 * @ModifiedDate: 2015-7-22
 * @Modified: 2014-11-1: 实现基本功能
 */
public interface ILocate {
    public void init();

    public void startLocate();

    public void stopLocate();

    public void release();
}
