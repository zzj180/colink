package com.unisound.unicar.gui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.utils.Logger;

public class PositionContentView extends FrameLayout implements ISessionView {
    private static final String TAG = "PositionContentView";

    private static final double SCALE_NUM = 0.8;
    private Context mContext;
    private MapView mMapView;

    private LocationMode mCurrentMode;

    public PositionContentView(Context context) {
        super(context);
        Logger.d(TAG, "PositionContentView create");
        this.mContext = context;

        initView();
    }

    private void initView() {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.position_view, this, true);

        mMapView = (MapView) findViewById(R.id.bmapView);
    }

    public boolean updateMapView(LocationInfo mLocationInfo) {
        if (mMapView != null) {
            BaiduMap mBaiduMap = mMapView.getMap();

            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            // 构造定位数据
            MyLocationData locData =
                    new MyLocationData.Builder().direction(100)
                            .latitude(mLocationInfo.getLatitude())
                            .longitude(mLocationInfo.getLongitude()).build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            mCurrentMode = LocationMode.NORMAL;
            BitmapDescriptor mCurrentMarker =
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker);
            MyLocationConfiguration config =
                    new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
            mBaiduMap.setMyLocationConfigeration(config);
            // 当不需要定位图层时关闭定位图层
            mBaiduMap.setMyLocationEnabled(false);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isTemporary() {
        return false;
    }

    @Override
    public void release() {
        mMapView.onDestroy();
    }

}
