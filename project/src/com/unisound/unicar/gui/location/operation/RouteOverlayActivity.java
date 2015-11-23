package com.unisound.unicar.gui.location.operation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.route.operation.GaodeMap;

/**
 * 此demo用来展示如何用自己的数据构造一条路线在地图上绘制出来
 * 
 */
public class RouteOverlayActivity extends Activity {
	public static final String TAG = "RouteOverlayActivity";
	public static final String TAG_MODE = "TAG_MODE";
	public static final String TAG_FROM_LAT = "TAG_FROM_LAT";
	public static final String TAG_FROM_LNG = "TAG_FROM_LNG";
	public static final String TAG_FROM_CITY = "TAG_FROM_CITY";
	public static final String TAG_FROM_POI = "TAG_FROM_POI";
	public static final String TAG_TO_LAT = "TAG_TO_LAT";
	public static final String TAG_TO_LNG = "TAG_TO_LNG";
	public static final String TAG_TO_CITY = "TAG_TO_CITY";
	public static final String TAG_TO_POI = "TAG_TO_POI";

	// 地图相关
	private MapView mMapView = null; // 地图View
	private RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private com.baidu.mapapi.map.BaiduMap mBaidumap;
	private OnGetRoutePlanResultListener mRoutePlanResultListener = new OnGetRoutePlanResultListener() {

		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
			mBaidumap.clear();
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				return;
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo()
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaidumap);
				overlay.setData((WalkingRouteLine) (result.getRouteLines().get(0)));
				overlay.addToMap();
				overlay.zoomToSpan();
			}
		}

		@Override
		public void onGetTransitRouteResult(TransitRouteResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				return;
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo()
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				TransitRouteOverlay overlay = new TransitRouteOverlay(mBaidumap);
				mBaidumap.setOnMarkerClickListener(overlay);
				overlay.setData((TransitRouteLine) (result.getRouteLines().get(0)));
				overlay.addToMap();
				overlay.zoomToSpan();
			}

		}

		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				return;
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo()
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaidumap);
				overlay.setData((DrivingRouteLine) (result.getRouteLines().get(0)));
				overlay.addToMap();
				overlay.zoomToSpan();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baidu_route);

		setTitle(R.string.route_planning);
		// 初始化地图
		mMapView = (MapView) findViewById(R.id.bmapView);
		// mMapView.getController().setZoom(12);
		// mMapView.getController().enableClick(true);
		mBaidumap = mMapView.getMap();

		// 地图点击事件处理
		// mBaidumap.setOnMapClickListener(this);
		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(mRoutePlanResultListener);
		computeRoute();
	}

	private void computeRoute() {
		Intent intent = getIntent();
		String mode = intent.getStringExtra(TAG_MODE);
		double fromLat = intent.getDoubleExtra(TAG_FROM_LAT, 0);
		double fromLng = intent.getDoubleExtra(TAG_FROM_LNG, 0);
		String fromCity = intent.getStringExtra(TAG_FROM_CITY);
		String fromPoi = intent.getStringExtra(TAG_FROM_POI);
		double toLat = intent.getDoubleExtra(TAG_TO_LAT, 0);
		double toLng = intent.getDoubleExtra(TAG_TO_LNG, 0);
		String toCity = intent.getStringExtra(TAG_TO_CITY);
		String toPoi = intent.getStringExtra(TAG_TO_POI);
		// 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
		PlanNode stNode, enNode;

		if (fromLat != 0 && fromLng != 0) {
			stNode = PlanNode.withLocation(new LatLng(fromLat, fromLng));
		} else {
			stNode = PlanNode.withCityNameAndPlaceName(fromCity, fromPoi);
		}

		if (toLat != 0 && toLng != 0) {
			enNode = PlanNode.withLocation(new LatLng(toLat, toLng));
		} else {
			enNode = PlanNode.withCityNameAndPlaceName(toCity, toPoi);
		}
		//先清空之前描画的路线
		mBaidumap.clear();
		showRoute(mode, stNode, enNode);
	}

	private void showRoute(String mode, PlanNode from, PlanNode to) {
		if (GaodeMap.ROUTE_MODE_DRIVING.equals(mode)) {
			DrivingRoutePlanOption option = new DrivingRoutePlanOption();
			option.from(from);
			option.to(to);
			mSearch.drivingSearch(option);
		} else if (GaodeMap.ROUTE_MODE_TRANSIT.equals(mode)) {
			TransitRoutePlanOption option = new TransitRoutePlanOption();
			option.from(from);
			option.to(to);
			mSearch.transitSearch(option);
		} else if (GaodeMap.ROUTE_MODE_WALKING.equals(mode)) {
			WalkingRoutePlanOption option = new WalkingRoutePlanOption();
			option.from(from);
			option.to(to);
			mSearch.walkingSearch(option);
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		computeRoute();
	}

	@Override
	protected void onDestroy() {
		mSearch.destroy();
		mMapView.onDestroy();
		super.onDestroy();
	}

}
