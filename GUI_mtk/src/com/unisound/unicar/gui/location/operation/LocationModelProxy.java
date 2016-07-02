package com.unisound.unicar.gui.location.operation;

import android.content.Context;

import com.unisound.unicar.gui.location.interfaces.ILocate;
import com.unisound.unicar.gui.location.interfaces.ILocationListener;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名 定位
 * @Comments : 描述 使用代理执行定位功能
 * @ModifiedBy : Zhuoran
 * @ModifiedDate: 2015-7-22
 * @Modified:
 */

public class LocationModelProxy implements ILocate {
    private static final String TAG = "LocationModel";
    public static LocationModelProxy mInstance = null;

    private LocationModelProxy() {}

    private static BaseLocateClient mLocate;
    protected static Object mObjLock = new Object();

    private static void initProxy(Context context) {
        Logger.d(TAG, "initProxy");
        mLocate = createLocateClient(context);
        mLocate.init();
    }

    private static BaseLocateClient createLocateClient(Context context) {
   /*     if (BAIDU_INDEX == UserPerferenceUtil.getMapChoose(context)) {
            return new BaiduLocateClient(context);
        } else {
            return new AMapLocateClient(context);
        }*/
        return new BaiduLocateClient(context);
    }

    public static synchronized LocationModelProxy getInstance(Context context) {
        synchronized (mObjLock) {
            Logger.d(TAG, "getInstance");
            mInstance = new LocationModelProxy();
            initProxy(context);
        }
        return mInstance;
    }

    public void setLocationListener(ILocationListener locationListener) {
        Logger.d(TAG, "setLocationListener locationListener : " + locationListener);
        mLocate.setLocationListener(locationListener);
    }

    public void startLocate() {
        Logger.d(TAG, "startLocate");
        mLocate.startLocate();
    }

    public void stopLocate() {
        Logger.d(TAG, "stopLocate");
        if (mLocate != null) {
            mLocate.setLocationListener(null);
            mLocate.stopLocate();
        }
    }

    public LocationInfo getLastLocation() {
        Logger.d(TAG, "getLastLocation");
        if (mLocate != null) {
            return mLocate.getLastLocation();
        } else {
            return null;
        }
    }

    @Override
    public void init() {}

    @Override
    public void release() {
        mLocate.release();
        mLocate = null;
    }
}
