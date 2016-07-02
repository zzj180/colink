package com.colink.zzj.txzassistant.vendor.BD;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.colink.zzj.txzassistant.R;

/**
 * 此demo用来展示如何用自己的数据构造一条路线在地图上绘制出来
 * 
 */
public class RouteOverlayActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "RouteOverlayActivity";

    public static final String TAG_MAP = "TAG_MAP";
    public static final String TAG_MODE = "TAG_MODE";
    public static final String TAG_FROM_LAT = "TAG_FROM_LAT";
    public static final String TAG_FROM_LNG = "TAG_FROM_LNG";
    public static final String TAG_FROM_CITY = "TAG_FROM_CITY";
    public static final String TAG_FROM_POI = "TAG_FROM_POI";
    public static final String TAG_TO_LAT = "TAG_TO_LAT";
    public static final String TAG_TO_LNG = "TAG_TO_LNG";
    public static final String TAG_TO_CITY = "TAG_TO_CITY";
    public static final String TAG_TO_POI = "TAG_TO_POI";

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_route);

        setTitle(R.string.route_planning);
        btn = (Button) findViewById(R.id.nav);
        btn.setOnClickListener(this);
        computeRoute();
    }

    @Override
    public void onClick(View v) {
        startNavi(null);
    }


    double fromLat;
    double fromLng;
    double toLat;
    double toLng;
    String fromName;
    String toName;
    boolean isNeedToNav;
    boolean hasMap;

    private void computeRoute() {
        Intent intent = getIntent();

        if (intent != null) {
            isNeedToNav = true;
            fromLat = intent.getDoubleExtra(TAG_FROM_LAT, 0);
            fromLng = intent.getDoubleExtra(TAG_FROM_LNG, 0);
            toLat = intent.getDoubleExtra(TAG_TO_LAT, 0);
            toLng = intent.getDoubleExtra(TAG_TO_LNG, 0);
            fromName = intent.getStringExtra(TAG_FROM_POI);
            toName = intent.getStringExtra(TAG_TO_POI);
            hasMap = intent.getBooleanExtra(TAG_MAP, false);
        }
    }

    public void startNavi(View v) {
        LatLng start = new LatLng(fromLat, fromLng);
        LatLng end = new LatLng(toLat, toLng);
        NaviPara params = new NaviPara();
        params.startPoint = start;
        params.endPoint = end;
        params.endName = toName;
        params.startName = fromName;
        if (hasMap)
            BaiduMapNavigation.openBaiduMapNavi(params, this);
        else
            BaiduMapNavigation.openWebBaiduMapNavi(params, this);
        finish();
    }

    @Override
    protected void onPause() {
        // mMapView.onPause();
        super.onPause();
    }

    public void onStart() {
        super.onStart();
        if (isNeedToNav) {
            btn.performClick();
            isNeedToNav = false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
