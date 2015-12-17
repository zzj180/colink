package com.unisound.unicar.gui.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyTrafficStyle;
import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.Logger;

public class AmapTrafficShowActivity extends Activity implements OnMarkerClickListener {
    private static final String TAG = "AmapTrafficShowActivity";
    private MapView mapView;
    private AMap aMap;
    private UiSettings mUiSettings;
    private Marker marker2;// 有跳动效果的marker对象（实时路况点）
    private LatLng showLatLng = null;// 标记点坐标
    private String road = null;// 路况名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amap_traffic_activity);
        initData();// 初始化地图数据
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径， 则需要在离线地图下载和使用地图页面都进行路径设置
         */
        // Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
        // MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        init();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        Double lat = intent.getDoubleExtra("lat", 39.989614);
        Double lng = intent.getDoubleExtra("lng", 116.481763);
        road = intent.getStringExtra("road");
        showLatLng = new LatLng(lat, lng);
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        mUiSettings = aMap.getUiSettings();
        setUpMap();
    }

    /**
     * 地图设置
     */
    private void setUpMap() {
        // 设置ui
        mUiSettings.setScaleControlsEnabled(true);// 显示缩放比例
        mUiSettings.setAllGesturesEnabled(true);// 设置所以手势
        mUiSettings.setZoomControlsEnabled(true);// 放大缩小控件
        // 设置地图属性
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setTrafficEnabled(true);// 显示实时路况
        aMap.setMyLocationEnabled(false);// 实时路况时---取消定位
        MyTrafficStyle myTrafficStyle = new MyTrafficStyle();
        myTrafficStyle.setSeriousCongestedColor(0xff92000a);
        myTrafficStyle.setCongestedColor(0xffea0312);
        myTrafficStyle.setSlowColor(0xffff7508);
        myTrafficStyle.setSmoothColor(0xff00a209);
        aMap.setMyTrafficStyle(myTrafficStyle);
        // 绘制前清除标记
        aMap.clear();
        // 添加标记
        addMarkersToMap();// 往地图上添加实时路况marker
    }

    /**
     * 添加实时路况标注
     */
    private void addMarkersToMap() {
        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        giflist.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        giflist.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

        MarkerOptions markerOption1 =
                new MarkerOptions().anchor(0.5f, 0.5f).position(showLatLng).title("实时路况信息:")
                        .snippet(road + ":" + showLatLng.latitude + "," + showLatLng.longitude)
                        .icons(giflist).draggable(true).period(50);
        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
        // markerOptionlst.add(markerOption);
        markerOptionlst.add(markerOption1);
        List<Marker> markerlst = aMap.addMarkers(markerOptionlst, true);
        marker2 = markerlst.get(0);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        /*
         * 此处需要finish 因为是single task模式启动. 此处不处理：如果界面没被finish下次进来还是上次的activity不会刷新界面
         */
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(marker2)) {
            if (aMap != null) {
                jumpPoint(marker);// 跳动
            }
        }
        return false;
    }

    /**
     * marker点击时跳动效果
     */
    public void jumpPoint(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        Point startPoint = proj.toScreenLocation(showLatLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * showLatLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * showLatLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Logger.d(TAG, "onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
        init();
    }
}
