/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : GaoDeLocation.java
 * @ProjectName : vui_datamodel
 * @PakageName : cn.yunzhisheng.vui.location.operation
 * @Author : Conquer
 * @CreateDate : 2014-2-21
 */
package com.unisound.unicar.gui.location.operation;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.utils.DataModelErrorUtil;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Conquer
 * @CreateDate : 2014-2-21
 * @ModifiedBy : Conquer
 * @ModifiedDate: 2014-2-21
 * @Modified: 2014-2-21: 实现基本功能
 */
public class AMapLocateClient extends BaseLocateClient {
    public static final String TAG = "AMapLocate";
    private LocationManagerProxy mLocationManager = null;
    private boolean isGpsCallBack = false;// 标志混合定位时 GPS是否回调

    public AMapLocateClient(Context context) {
        super(context);
    }

    private AMapLocationListener mLocationListener = new AMapLocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onLocationChanged(AMapLocation location) {
            Logger.d(TAG, "onLocationChanged");
            if (isTimeout() || isCanceled()) {
                return;
            }
            Logger.d(TAG, "onLocationChanged --> location " + location);

            LocationInfo info = null;

            // 只在操作正确情况下，执行回调。
            Logger.d(TAG, "location.getAMapException().getErrorCode() = "
                    + location.getAMapException().getErrorCode());
            if (location != null && location.getAMapException().getErrorCode() == 0) {
                info = parse2LocationInfo(location);
                if (info != null) {
                    // 定位回调函数
                    if (mListener != null) {
                        mListener.onLocationChanged(info, null);
                    }
                } else {
                    if (mListener != null) {
                        // mListener.onLocationChanged(null,DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.LOCATE_NO_RESULT));
                        // 由于高德混合定位，GPS或NetWork失败会回调两次，为了避免其中一次失败就取消，特加一个标志位，保证GPS和NetWork都失败后，回调定位失败
                        if (isGpsCallBack) {
                            mListener.onLocationChanged(null, DataModelErrorUtil
                                    .getErrorUtil(DataModelErrorUtil.LOCATE_NO_RESULT));
                            isGpsCallBack = false;
                        } else {
                            isGpsCallBack = true;
                        }
                    }
                }
            } else {
                Logger.e(TAG, "locate error ...");
                mListener.onLocationChanged(null,
                        DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.LOCATE_NO_RESULT));
                isGpsCallBack = false;
            }

            onLocateResultReach(info);
        }
    };


    @Override
    public void init() {
        Logger.d(TAG, "init");
        if (mLocationManager == null) {
            mLocationManager = LocationManagerProxy.getInstance(mContext);
            mLocationManager.setGpsEnable(true);
        }
    }

    @Override
    public void startLocate() {
        Logger.d(TAG, "start");
        super.startLocate();
        if (mLocationManager == null) {
            mLocationManager = LocationManagerProxy.getInstance(mContext);
            mLocationManager.setGpsEnable(true);
        }
        mLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 2000, 10,
                mLocationListener);
    }

    @Override
    public void stopLocate() {
        Logger.d(TAG, "stop");
        super.stopLocate();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
            destoryLocationManager();
        } else {
            Logger.w(TAG, "mLocationManager has been null!");
        }
    }

    @Override
    protected LocationInfo parse2LocationInfo(Object obj) {
        Logger.d(TAG, "parse2LocationInfo:obj " + obj);
        if (obj instanceof AMapLocation) {
            AMapLocation location = (AMapLocation) obj;
            LocationInfo info = new LocationInfo();
            info.setProvider(location.getProvider());
            info.setType(LocationInfo.GPS_GAODE);
            info.setAccuracy(location.getAccuracy());
            info.setAltitude(location.getAltitude());
            info.setLatitude(location.getLatitude());
            info.setLongitude(location.getLongitude());
            info.setProvince(location.getProvince());
            info.setCity(location.getCity());
            info.setCityCode(location.getCityCode());
            info.setDistrict(location.getDistrict());
            info.setAddress(location.getExtras().getString("desc"));
            info.setBearing(location.getBearing());
            info.setSpeed(location.getSpeed());
            info.setTime(location.getTime());
            return info;
        }
        Logger.e(TAG, "Cann't parse non AMapLocation to LocationInfo!");
        return null;
    }

    @SuppressWarnings("deprecation")
    private void destoryLocationManager() {
        Logger.d(TAG, "destoryLocationManager");
        if (mLocationManager != null) {
            mLocationManager.destory();
            mLocationManager = null;
        }
    }

    @Override
    public void release() {
        Logger.d(TAG, "release");
        super.release();
        stopLocate();
        mLocationListener = null;
    }

}
