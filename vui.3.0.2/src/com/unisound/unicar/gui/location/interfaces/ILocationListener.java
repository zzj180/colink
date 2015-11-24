/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : ILocationListener.java
 * @ProjectName : vui_location
 * @PakageName : cn.yunzhisheng.vui.interfaces
 * @Author : Dancindream
 * @CreateDate : 2013-8-13
 */
package com.unisound.unicar.gui.location.interfaces;

import java.util.List;

import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.utils.ErrorUtil;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-8-13
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-8-13
 * @Modified: 2013-8-13: 实现基本功能
 */
public interface ILocationListener {
    public void onLocationChanged(LocationInfo info, ErrorUtil code);

    public void onLocationResult(List<LocationInfo> infos, ErrorUtil code);
}
