/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : GaodeLocationActivity.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.gaode
 * @Author : Conquer
 * @CreateDate : 2014-2-24
 */
package com.unisound.unicar.gui.route.operation;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.Query;
import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.Logger;

/**
 * GaoDe Location Activity
 * 
 * @author xiaodong
 * @date 20150911
 */
public class GaodeLocationActivity extends Activity
        implements
            LocationSource,
            OnPoiSearchListener,
            OnMapLoadedListener {
    public static final String TAG = "GaodeLocationActivity";
    public static final String TAG_LAT = "TAG_LAT";
    public static final String TAG_LNG = "TAG_LNG";
    public static final String TAG_TITLE = "TAG_TITLE";
    public static final String TAG_CONTENT = "TAG_CONTENT";

    private AMap mMap;
    private MapView mMapView;
    private OnLocationChangedListener mListener;
    private UiSettings mUiSetting;
    private LocationManagerProxy mLocation = null;
    private Query mContentQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaode_location);
        mMapView = (MapView) findViewById(R.id.gaode_location);
        mMapView.onCreate(savedInstanceState);
        if (mMap == null) {
            mMap = mMapView.getMap();
            mUiSetting = mMap.getUiSettings();
        }
        mMap.setLocationSource(this);
        mMap.setOnMapLoadedListener(this);
        // 显示定位按钮
        mUiSetting.setMyLocationButtonEnabled(true);
        // 默认显示定位层
        mMap.setMyLocationEnabled(true);
        queryLocation();
    }

    private void queryLocation() {
        Intent start = getIntent();
        String title = "";
        String content = "";
        double lat = start.getDoubleExtra(TAG_LAT, 0);
        double lng = start.getDoubleExtra(TAG_LNG, 0);
        title = start.getStringExtra(TAG_TITLE);
        content = start.getStringExtra(TAG_CONTENT);

        MarkerOptions option = new MarkerOptions();
        Logger.d(TAG, "queryLocation: title " + title + ", content: " + content + "\n" + ",lat: "
                + lat + ", lng: " + lng);
        option.anchor(0.5f, 0.5f);
        option.title(title);
        option.draggable(false);
        // option.visible(true);
        option.snippet(content);
        option.position(new LatLng(lat, lng));
        mMap.addMarker(option);

        // mContentQuery = new
        // PoiSearch.Query(start.getStringExtra(LocationOverlayActivity.TAG_CONTENT),""
        // ,start.getStringExtra(LocationOverlayActivity.TAG_TITLE));
        // mContentQuery.setPageNum(1);
        // mContentQuery.setPageSize(10);
        // PoiSearch search = new PoiSearch(this, mContentQuery);
        // search.setOnPoiSearchListener(this);
        // search.searchPOIAsyn();
    }

    @Override
    protected void onResume() {
        Logger.d(TAG, "onResume");
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        Logger.d(TAG, "onPause");
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Logger.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapLoaded() {
        Logger.d(TAG, "onMapLoaded");
        Intent start = getIntent();
        double lat = start.getDoubleExtra(TAG_LAT, 0);
        double lng = start.getDoubleExtra(TAG_LNG, 0);
        // 设置所有maker显示在当前可视区域地图中
        LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(lat, lng)).build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
    }

    @Override
    protected void onDestroy() {
        Logger.d(TAG, "onDestroy");
        super.onDestroy();
        mMapView.onDestroy();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void activate(OnLocationChangedListener listener) {
        // TODO Auto-generated method stub
        mListener = listener;
        mLocation = LocationManagerProxy.getInstance(GaodeLocationActivity.this);
        mLocation.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10,
                new AMapLocationListener() {

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onLocationChanged(Location location) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onLocationChanged(AMapLocation arg0) {
                        // TODO Auto-generated method stub
                        if (mListener != null) {
                            mListener.onLocationChanged(arg0);
                        }
                    }
                });
    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        // TODO Auto-generated method stub
        if (rCode == 0) {
            if (result != null && result.getQuery() != null) {
                if (result.getQuery().equals(mContentQuery)) {
                    if (result.getPois().size() > 0) {
                        // PoiItem item = result.getPois().get(0);
                        mMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(mMap, result.getPois());
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    }
                }
            } else {
                Toast.makeText(this, R.string.gaode_result_nofind, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, R.string.gaode_check_net, Toast.LENGTH_LONG).show();
        }
    }

}
