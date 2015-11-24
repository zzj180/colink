/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : ILocate.java
 * @ProjectName : vui_datamodel
 * @PakageName : cn.yunzhisheng.vui.location.operation
 * @Author : Brant
 * @CreateDate : 2014-11-1
 */
package com.unisound.unicar.gui.search.interfaces;

import com.unisound.unicar.gui.data.operation.PoiDataModel;


/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-11-1
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-11-1
 * @Modified: 2014-11-1: 实现基本功能
 */
public interface ISearch {
    public void init();

    public void startSearch(PoiDataModel poiDataModel, IPoiListener mPoiListener);

    public void stopSearch();

    public void release();
}
