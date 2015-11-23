/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : IPOIListener.java
 * @ProjectName : vui_location
 * @PakageName : cn.yunzhisheng.vui.interfaces
 * @Author : Dancindream
 * @CreateDate : 2013-8-23
 */
package com.unisound.unicar.gui.search.interfaces;

import java.util.List;

import com.unisound.unicar.gui.model.PoiInfo;
import com.unisound.unicar.gui.utils.ErrorUtil;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-8-23
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-8-23
 * @Modified: 2013-8-23: 实现基本功能
 */
public interface IPoiListener {
    public void onPoiSearchResult(List<PoiInfo> infos, ErrorUtil errorUtil);
}
