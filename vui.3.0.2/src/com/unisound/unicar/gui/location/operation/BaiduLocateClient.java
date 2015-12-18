package com.unisound.unicar.gui.location.operation;

import android.content.Context;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.preference.PrivatePreference;
import com.unisound.unicar.gui.utils.DataModelErrorUtil;
import com.unisound.unicar.gui.utils.Logger;

public class BaiduLocateClient extends BaseLocateClient {
    private final String TAG = "BaiduLocateClient";

    private LocationClient mLocationClient = null;

    public BaiduLocateClient(Context context) {
        super(context);
        Logger.d(TAG, "BaiduLocateClient init ...");
    }

    private BDLocationListener mBDLocationListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (isTimeout() || isCanceled()) {
                return;
            }
            Logger.d(TAG, "onReceiveLocation:location " + location);
            LocationInfo info = parse2LocationInfo(location);
            onLocateResultReach(info);
            if (info != null) {
                if (mListener != null) {
                    mListener.onLocationChanged(info, null);
                }
            } else {
                if (mListener != null) {
                    mListener.onLocationChanged(null,
                            DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.LOCATE_NO_RESULT));
                }
            }
        }
    };

    @Override
    public void init() {
        Logger.d(TAG, "init");
        mLocationClient = new LocationClient(mContext); // 声明LocationClient类
        mLocationClient.registerLocationListener(mBDLocationListener); // 注册监听函数

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(PrivatePreference.getBooleanValue("locate_gps_enable", true));
        option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(5000);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 可选，默认false,设置是否使用gps
        option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(option);
    }

    @Override
    public void startLocate() {
        Logger.d(TAG, "start");
        super.startLocate();

        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        mLocationClient.requestLocation();
    }

    @Override
    public void stopLocate() {
        Logger.d(TAG, "stop");
        super.stopLocate();
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    @Override
    protected LocationInfo parse2LocationInfo(Object obj) {
        Logger.d(TAG, "parse2LocationInfo:obj " + obj);
        if (obj instanceof BDLocation) {
            BDLocation location = (BDLocation) obj;

            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append(",lontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\ndirection : ");
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append(location.getDirection());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                // 运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
            }

            Logger.d(TAG, sb.toString());
            LocationInfo info = new LocationInfo();
            info.setProvider(location.getNetworkLocationType());
            info.setType(LocationInfo.GPS_BAIDU);
            info.setAccuracy(location.getRadius());
            info.setAltitude(location.getAltitude());
            info.setLatitude(location.getLatitude());
            info.setLongitude(location.getLongitude());
            info.setProvince(location.getProvince());
            info.setCity(location.getCity());
            info.setCityCode(location.getCityCode());
            info.setDistrict(location.getDistrict());
            info.setAddress(location.getAddrStr());

            info.setBearing(location.getDirection());
            info.setSpeed(location.getSpeed());
            return info;
        }
        Logger.e(TAG, "Cann't parse non BDLocation to LocationInfo!");
        return null;
    }
}
