/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : AMapLocationView.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.view
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-20
 */
package com.unisound.unicar.gui.view;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.MapView;
import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.StringUtil;

/**
 * @author xiaodong
 * @date 20151020
 */
public class AMapLocationViewBake extends BaseMapLocationView {

    private static final String TAG = AMapLocationViewBake.class.getSimpleName();

    private Context mContext;

    private EditText mEtSearchLocation, mEtFocus;

    private LinearLayout mButtonsLayout;
    private Button mBtnOk;
    private Button mBtnCancel;
    private ImageView mIvSearchLocation;
    private MapLocationViewListener mMapLocationViewListener;

    private MapView mapView;

    private AMap aMap;
    private OnLocationChangedListener mListener;

    private LocationInfo mLocationInfo = null;

    private boolean isEditLocationFocusByUser = false;

    /**
     * @param context
     */
    public AMapLocationViewBake(Context context) {
        this(context, null);
        mContext = context;

        getCurrentLocation();
        initView();
    }

    public AMapLocationViewBake(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setMapLocationViewListener(MapLocationViewListener mapLocationViewListener) {
        mMapLocationViewListener = mapLocationViewListener;
    }

    private void getCurrentLocation() {
        mLocationInfo = WindowService.mLocationInfo;
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_location_amap, this, true);
        mEtSearchLocation = (EditText) findViewById(R.id.et_around_search_keyword);
        mButtonsLayout = (LinearLayout) findViewById(R.id.ll_edit_buttons);
        mBtnOk = (Button) findViewById(R.id.btn_edit_ok);
        mBtnCancel = (Button) findViewById(R.id.btn_edit_cancel);
        mIvSearchLocation = (ImageView) findViewById(R.id.iv_search_location);

        mEtFocus = (EditText) findViewById(R.id.et_focus);

        mEtSearchLocation.setOnFocusChangeListener(mOnFocusChangeListener);
        mEtSearchLocation.addTextChangedListener(mEditTextWatcher);
        mBtnOk.setOnClickListener(mOnClickListener);
        mBtnCancel.setOnClickListener(mOnClickListener);
        mIvSearchLocation.setOnClickListener(mOnClickListener);
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径， 则需要在离线地图下载和使用地图页面都进行路径设置
         */
        // Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
        // MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(new Bundle());// 此方法必须重写

        // initAMap();
        initAMapAsync();
    }



    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.et_around_search_keyword:
                    Logger.d(TAG, "onFocusChange---hasFocus = " + hasFocus
                            + "; isEditLocationFocusByUser = " + isEditLocationFocusByUser);
                    if (hasFocus) {
                        onEditFucus();
                    }
                    isEditLocationFocusByUser = true;
                    break;
                default:
                    break;
            }
        }
    };

    private void onEditFucus() {
        if (null != mMapLocationViewListener) {
            mMapLocationViewListener.onEditLocationFocus();
        }
        mButtonsLayout.setVisibility(View.VISIBLE);
    }

    private TextWatcher mEditTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Logger.d(TAG, "mEditTextWatcher--onTextChanged--s=" + s + "; count = " + count
                    + "; start = " + start + "; before = " + before);
            if (null == mBtnOk) {
                return;
            }
            if (TextUtils.isEmpty(s)) {
                mBtnOk.setEnabled(false);
            } else {
                mBtnOk.setEnabled(true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Logger.d(TAG, "mEditTextWatcher--beforeTextChanged--s=" + s + "; count = " + count
                    + "; start = " + start + "; after = " + after);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.et_around_search_keyword:
                    Logger.d(TAG, "et_search_location click");
                    onEditFucus();
                    break;
                case R.id.btn_edit_ok:
                    onSearchClick();
                    break;
                case R.id.btn_edit_cancel:
                    if (null != mMapLocationViewListener) {
                        mMapLocationViewListener.onConfirmLocationCancel();
                    }
                    resetEditFocus();
                    mButtonsLayout.setVisibility(View.GONE);
                    DeviceTool.showEditTextKeyboard(mEtSearchLocation, false);
                    break;
                case R.id.iv_search_location:
                    onSearchClick();
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * onSearchClick
     */
    private void onSearchClick() {
        String input = mEtSearchLocation.getText().toString();
        Logger.d(TAG, "onSearchClick---input = " + input);
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(mContext, R.string.toast_input_location, Toast.LENGTH_SHORT).show();
            return;
        }
        String location = StringUtil.clearSpecialCharacter(input);
        Logger.d(TAG, "onSearchClick---location = " + location);
        if (TextUtils.isEmpty(location)) {
            mEtSearchLocation.setText("");
            Toast.makeText(mContext, R.string.toast_input_location_error, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (null != mMapLocationViewListener) {
            mMapLocationViewListener.onConfirmLocationOk(location);
        }
        mButtonsLayout.setVisibility(View.GONE);
        DeviceTool.showEditTextKeyboard(mEtSearchLocation, false);
    }

    /**
     * XD added 20151027
     */
    private void initAMapAsync() {
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                initAMap();
            }
        };
        t.start();
    }

    /**
     * 初始化
     */
    private void initAMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private LocationSource mLocationSource = new LocationSource() {

        /**
         * 激活定位
         */
        @Override
        public void activate(OnLocationChangedListener listener) {
            Logger.d(TAG, "-activate-");
            mListener = listener;
            if (mLocationInfo != null) {
                if (mListener != null) {
                    mListener.onLocationChanged(parse2AMapLocation(mLocationInfo));// 显示系统小蓝点
                }
            }

        }

        @Override
        public void deactivate() {
            Logger.d(TAG, "-deactivate-");
        }

    };

    private void resetEditFocus() {
        mEtSearchLocation.clearFocus();
        mEtSearchLocation.setText("");
        mEtFocus.requestFocus();
        mEtFocus.requestFocusFromTouch();
    }


    protected AMapLocation parse2AMapLocation(Object obj) {
        Logger.d(TAG, "parse2AMapLocation obj : " + obj);
        if (obj instanceof LocationInfo) {
            LocationInfo location = (LocationInfo) obj;
            AMapLocation amplLocation = new AMapLocation(location.getProvider());
            amplLocation.setProvider(location.getProvider());
            amplLocation.setAccuracy(location.getAccuracy());
            amplLocation.setAltitude(location.getAltitude());
            amplLocation.setLatitude(location.getLatitude());
            amplLocation.setLongitude(location.getLongitude());
            amplLocation.setCity(location.getCity());
            amplLocation.setCityCode(location.getCityCode());
            amplLocation.setDistrict(location.getDistrict());
            amplLocation.setBearing(location.getBearing());
            amplLocation.setSpeed(location.getSpeed());
            amplLocation.setTime(location.getTime());
            return amplLocation;
        }
        Logger.e(TAG, "Cann't parse non LocationInfo to AMapLocation!");
        return null;
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        Logger.d(TAG, "setUpMap---------Begin");
        aMap.setLocationSource(mLocationSource);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        Logger.d(TAG, "setUpMap---------End");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Logger.d(TAG, "onInterceptTouchEvent---action-" + ev.getAction());
        if (MotionEvent.ACTION_MOVE == ev.getAction() && mMapLocationViewListener != null) {
            mMapLocationViewListener.onMapViewMove();
        }
        return super.onInterceptTouchEvent(ev);
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
        // TODO Auto-generated method stub
        Logger.d(TAG, "release-----");
        if (null != mapView) {
            // mapView.onDestroy();
            mapView = null;
        }
    }

}
