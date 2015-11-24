package com.unisound.unicar.gui.location.operation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.LocationInfo;

/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 * 
 */
public class LocationOverlayActivity extends Activity {
	public static final String TAG_LAT = "TAG_LAT";
	public static final String TAG_LNG = "TAG_LNG";
	public static final String TAG_TITLE = "TAG_TITLE";
	public static final String TAG_CONTENT = "TAG_CONTENT";

	private MapView mMapView = null; // 地图View
	private LocationMode mCurrentMode;

	boolean isFirstLoc = true;// 是否首次定位

	private BaiduMap mBaiduMap;
	private BitmapDescriptor mCurrentMarker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locationoverlay);

		Intent intent = getIntent();
		double lat = Double.parseDouble(intent.getStringExtra(TAG_LAT));
		double lng = Double.parseDouble(intent.getStringExtra(TAG_LNG));
		String content = intent.getStringExtra(TAG_CONTENT);

		CharSequence titleLable = content;
		setTitle(titleLable);

		LocationInfo location = new LocationInfo();
		location.setLatitude(lat);
		location.setLongitude(lng);
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);

		mCurrentMode = LocationMode.NORMAL;
		mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));

		MyLocationData locData = new MyLocationData.Builder()
				.accuracy(location.getAccuracy())
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(0).latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();
		mBaiduMap.setMyLocationData(locData);
		if (isFirstLoc) {
			isFirstLoc = false;
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public static Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		return bitmap;
	}

}
