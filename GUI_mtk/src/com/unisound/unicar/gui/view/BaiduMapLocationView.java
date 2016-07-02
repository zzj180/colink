/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : BaiduMapLocationView.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.view
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-21
 */
package com.unisound.unicar.gui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @author zzj
 * 
 */
public class BaiduMapLocationView extends BaseMapLocationView {

    private static final String TAG = BaiduMapLocationView.class.getSimpleName();

    private Context mContext;

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    BitmapDescriptor mCurrentMarker;

    MapView mMapView;
    BaiduMap mBaiduMap;

    // UI相关
    OnCheckedChangeListener radioButtonListener;
    Button requestLocButton;
    boolean isFirstLoc = true;// 是否首次定位

    private LocationInfo mLocationInfo = null;
    private double mFromLat = 0.0;
    private double mFromLng = 0.0;
    private String mFromeCity = "";
    private String mFromAddress = "";

    /**
     * @param context
     */
    public BaiduMapLocationView(Context context) {
        this(context, null);
        mContext = context;
        getCurrentLocation();
        initView();
    }


    /**
     * @param context
     * @param attrs
     */
    public BaiduMapLocationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void getCurrentLocation() {
        mLocationInfo = WindowService.mLocationInfo;
        if (mLocationInfo != null) {
            mFromLat = mLocationInfo.getLatitude();
            mFromLng = mLocationInfo.getLongitude();
            mFromeCity = mLocationInfo.getCity();
            mFromAddress = mLocationInfo.getAddress();
            Logger.d(TAG, "mFromeCity = " + mFromeCity + "; mFromAddress = " + mFromAddress
                    + "; mFromLat = " + mFromLat + "; mFromLng = " + mFromLng);
        }
    }

    private void initView() {
        
       LayoutInflater.from(mContext).inflate(R.layout.view_location_baidu_map, this, true);

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);

        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            Logger.d(TAG, "onReceiveLocation----location = " + location);
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) return;
            MyLocationData locData =
                    new MyLocationData.Builder().accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(100).latitude(location.getLatitude())
                            .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {}
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.unisound.unicar.gui.view.ISessionView#isTemporary()
     */
    @Override
    public boolean isTemporary() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.unisound.unicar.gui.view.ISessionView#release()
     */
    @Override
    public void release() {
        Logger.d(TAG, "release---");

        if (null != mLocClient) {
            // 退出时销毁定位
            mLocClient.stop();
        }
        if (null != mBaiduMap) {
            // 关闭定位图层
            mBaiduMap.setMyLocationEnabled(false);
        }
        if (null != mMapView) {
            mMapView.onDestroy();
            mMapView = null;
        }
    }


}
