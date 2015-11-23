/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : BaseLocate.java
 * @ProjectName : vui_datamodel
 * @PakageName : cn.yunzhisheng.vui.location.operation
 * @Author : Brant
 * @CreateDate : 2014-11-1
 */
package com.unisound.unicar.gui.location.operation;

import android.content.Context;

import com.unisound.unicar.gui.location.interfaces.ILocate;
import com.unisound.unicar.gui.location.interfaces.ILocationListener;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.search.operation.BaseAsyncSearchClient;
import com.unisound.unicar.gui.utils.DataModelErrorUtil;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-11-1
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-11-1
 * @Modified: 2014-11-1: 实现基本功能
 */
public abstract class BaseLocateClient extends BaseAsyncLocateClient implements ILocate {
    public static final String TAG = "BaseLocate";
    private static LocationInfo mLastLocation;
    protected ILocationListener mListener;

    public BaseLocateClient(Context context) {
        super(context);
    }

    public void setLocationListener(ILocationListener l) {
        mListener = l;
    }

    public LocationInfo getLastLocation() {
        return mLastLocation;
    }

    @Override
    public void release() {
        Logger.d(TAG, "release");
        super.release();
        mLastLocation = null;
        mListener = null;
    }

    @Override
    public void startLocate() {
        Logger.d(TAG, "start");
        requestLocate(new Runnable() {

            @Override
            public void run() {
                stopLocate();
                if (mListener != null) {
                    mListener.onLocationChanged(null,
                            DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.LOCATE_TIMEOUT));
                }
            }
        });

    }

    @Override
    public void stopLocate() {
        Logger.d(TAG, "stop");
        cancel();
    }

    protected final void onLocateResultReach(LocationInfo locationInfo) {
        Logger.d(TAG, "onLocateResultReach");
        super.onLocateResultReach();
        if (locationInfo != null) {
            mLastLocation = locationInfo;
        }
        stopLocate();
    }

    abstract LocationInfo parse2LocationInfo(Object obj);
}
