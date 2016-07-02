package com.unisound.unicar.gui.route.baidu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.coogo.inet.vui.assistant.car.R;

/**
 * 此demo用来展示如何进行驾车、步行、公交路线搜索并在地图使用RouteOverlay、TransitOverlay绘制 同时展示如何进行节点浏览并弹出泡泡
 */
public class RouteOverlayNoMap extends Activity
        implements
            BaiduMap.OnMapClickListener,
            OnGetRoutePlanResultListener {

    public static final String TAG_FROM_CITY = "TAG_FROM_CITY";
    public static final String TAG_FROM_POI = "TAG_FROM_POI";
    public static final String TAG_TO_CITY = "TAG_TO_CITY";
    public static final String TAG_TO_POI = "TAG_TO_POI";

    public static final String TAG_TO_LAT = "TAG_TO_LAT";
    public static final String TAG_TO_LNG = "TAG_TO_LNG";
    public static final String TAG_FROM_LAT = "TAG_FROM_LAT";
    public static final String TAG_FROM_LNG = "TAG_FROM_LNG";

    // 浏览路线节点相关
    Button mBtnPre = null;// 上一个节点
    Button mBtnNext = null;// 下一个节点
    int nodeIndex = -1;// 节点索引,供浏览节点时使用
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    private TextView popupText = null;// 泡泡view

    // 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    // 如果不处理touch事件，则无需继承，直接使用MapView即可
    MapView mMapView = null; // 地图View
    BaiduMap mBaidumap = null;
    // 搜索相关
    RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    String startCity;
    String destinationCity;
    String startPoi;
    String destinationPoi;

    double fromLat;
    double fromLng;
    double toLat;
    double toLng;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan_no_map);
        setTitle(getString(R.string.luxianguihua));
        // 初始化地图

        Intent intent = getIntent();
        startCity = intent.getStringExtra(TAG_FROM_CITY);
        destinationCity = intent.getStringExtra(TAG_TO_CITY);
        startPoi = intent.getStringExtra(TAG_FROM_POI);
        destinationPoi = intent.getStringExtra(TAG_TO_POI);

        fromLat = intent.getDoubleExtra(TAG_FROM_LAT, 0);
        fromLng = intent.getDoubleExtra(TAG_FROM_LNG, 0);
        toLat = intent.getDoubleExtra(TAG_TO_LAT, 0);
        toLng = intent.getDoubleExtra(TAG_TO_LNG, 0);

        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();
        // 地图点击事件处理
        mBaidumap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        SearchButtonProcess();
    }

    /**
     * 发起路线规划搜索示例
     * 
     * @param v
     */
    public void SearchButtonProcess() {
        // 重置浏览节点的路线数据
        route = null;
        mBaidumap.clear();
        // 设置起终点信息，对于tranist search 来说，城市名无意义
        /*
         * PlanNode stNode = PlanNode.withCityNameAndPlaceName(startCity, startPoi); PlanNode enNode
         * = PlanNode.withCityNameAndPlaceName(destinationCity, destinationPoi);
         */

        PlanNode stNode = PlanNode.withLocation(new LatLng(fromLat, fromLng));
        PlanNode enNode = PlanNode.withLocation(new LatLng(toLat, toLng));

        mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
    }

    /**
     * 节点浏览示例
     * 
     * @param v
     */
    public void nodeClick(View v) {
        if (route == null || route.getAllStep() == null) {
            return;
        }

        // 获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        // 移动节点至中心
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(RouteOverlayNoMap.this);
        // popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

    }

    /**
     * 切换路线图标，刷新地图使其生效 注意： 起终点图标使用中心对齐.
     */
    public void changeRouteIcon(View v) {
        if (routeOverlay == null) {
            return;
        }
        if (useDefaultIcon) {
            ((Button) v).setText("自定义起终点图标");
            Toast.makeText(this, "将使用系统起终点图标", Toast.LENGTH_SHORT).show();

        } else {
            ((Button) v).setText("系统起终点图标");
            Toast.makeText(this, "将使用自定义起终点图标", Toast.LENGTH_SHORT).show();

        }
        useDefaultIcon = !useDefaultIcon;
        routeOverlay.removeFromMap();
        routeOverlay.addToMap();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteOverlayNoMap.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            /*
             * mBtnPre.setVisibility(View.VISIBLE); mBtnNext.setVisibility(View.VISIBLE);
             */
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
            routeOverlay = overlay;
            mBaidumap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }


    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
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
    protected void onDestroy() {
        mSearch.destroy();
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
        // TODO Auto-generated method stub

    }

}
