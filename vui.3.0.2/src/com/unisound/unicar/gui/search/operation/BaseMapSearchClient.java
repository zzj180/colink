/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : BaseMapSearch.java
 * @ProjectName : vui_datamodel
 * @PakageName : cn.yunzhisheng.vui.location.operation
 * @Author : Brant
 * @CreateDate : 2014-10-31
 */
package com.unisound.unicar.gui.search.operation;

import android.content.Context;

import com.unisound.unicar.gui.data.operation.PoiDataModel;
import com.unisound.unicar.gui.location.interfaces.ILocationListener;
import com.unisound.unicar.gui.search.interfaces.IPoiListener;
import com.unisound.unicar.gui.search.interfaces.ISearch;
import com.unisound.unicar.gui.utils.DataModelErrorUtil;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-10-31
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-10-31
 * @Modified: 2014-10-31: 实现基本功能
 */
public class BaseMapSearchClient extends BaseAsyncSearchClient implements ISearch {
    public static final String TAG = "BaseMapSearchClient";

    protected IPoiListener mPoiListener;
    protected ILocationListener mLocationListener;

    @Override
    public void init() {}

    public BaseMapSearchClient(Context context) {
        super(context);
    }

    @Override
    public void startSearch(PoiDataModel poiDataModel, IPoiListener poiListener) {
        Logger.d(TAG, "start");
        mPoiListener = poiListener;

        requestSearch(new Runnable() {
            @Override
            public void run() {
                stopSearch();
                if (mPoiListener != null) {
                    mPoiListener.onPoiSearchResult(null,
                            DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.SEARCH_POI_TIMEOUT));
                }
            }
        });

    }

    @Override
    public void stopSearch() {
        Logger.d(TAG, "stop");
        cancel();
    }

    @Override
    public void release() {
        Logger.d(TAG, "release");
        super.release();
        mPoiListener = null;
        mLocationListener = null;
    }
}
