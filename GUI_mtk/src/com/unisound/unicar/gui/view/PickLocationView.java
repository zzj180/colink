/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : PickLocationView.java
 * @ProjectName : uniCarSolotion
 * @PakageName : com.unisound.unicar.gui.view
 * @Author : Alieen
 * @CreateDate : 2015-07-08
 */
package com.unisound.unicar.gui.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.yunzhisheng.vui.assistant.WindowService;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.session.LocalSearchRouteConfirmShowSession;
import com.unisound.unicar.gui.utils.Gps;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.PositionUtil;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Alieen
 * @CreateDate : 2015-07-08
 * @ModifiedBy : Alieen
 * @ModifiedDate: 2015-07-08
 * @Modified: 2015-07-08: 实现基本功能
 */
public class PickLocationView extends PickBaseView {
    public static final String TAG = "PickLocationView";
    private Context mContext = null;

    private LinearLayout mLoadMoreLay;
    private boolean btn_more_use = false;
    private int numberContent = 0;
    private int pageMaxNumber = 5;
    private ArrayList<LocationInfo> mLocationInfos;

    private PickLocationViewListener mPickLocationViewListener;

    public void setPickLocationViewListener(PickLocationViewListener pickLocationViewListener) {
        mPickLocationViewListener = pickLocationViewListener;
    }

    public PickLocationView(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * 
     * @param locationInfos
     * @param locationToPoi: locationKeyword
     */
    public void initView(ArrayList<LocationInfo> locationInfos, String locationToPoi) {
        Logger.d(TAG, "initView---locationToPoi = " + locationToPoi);
        mLocationInfos = locationInfos;
        View header = mLayoutInflater.inflate(R.layout.pickview_header_location, this, false);
        TextView tvHead = (TextView) header.findViewById(R.id.tv_header_location_keyword);
        tvHead.setText(locationToPoi);

        ImageView ivEditLocation = (ImageView) header.findViewById(R.id.iv_edit_location);
        ivEditLocation.setOnClickListener(mOnClickListener);
        setHeader(header);

        Logger.d(TAG, "initView---locationInfos size = " + locationInfos.size()
                + "; pageMaxNumber = " + pageMaxNumber);
        // XD modify for FIX BUG-4468
        if (locationInfos.size() > pageMaxNumber) {
            for (int i = 0; i < pageMaxNumber; i++) {
                btn_more_use = true;
                numberContent += 1;
                addItem(getLocalItemView(locationInfos.get(i)));
            }
            addedMoreButton();
        } else if (locationInfos.size() <= pageMaxNumber) {
            for (int i = 0; i < locationInfos.size(); i++) {
                btn_more_use = false;
                numberContent += 1;
                addItem(getLocalItemView(locationInfos.get(i)));
            }
        }

        updateLayoutParams();
    }

    /**
     * added MoreButton
     * 
     * @author xiaodong.he
     * @date 20151102
     */
    @SuppressLint("InflateParams")
	private void addedMoreButton() {
        View buttomLay = mLayoutInflater.inflate(R.layout.list_item_load_more, null, false);
        mLoadMoreLay = (LinearLayout) buttomLay.findViewById(R.id.btn_more_local);
        /* < BUG-2792 XD 20150722 modify Begin */
        addBottomButton(buttomLay);
        // addItem(buttomLay);
        /* BUG-2792 20150722 XD modify End > */
        mLoadMoreLay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addMoreView();
            }
        });
    }

    public void addMoreView() {
        mLoadMoreLay.setVisibility(View.GONE);

        if (btn_more_use) {
            for (int i = pageMaxNumber; i < mLocationInfos.size(); i++) {
                numberContent += 1;
                btn_more_use = true;
                addItem(getLocalItemView(mLocationInfos.get(i)));
            }
        }
    }

    public View getLocalItemView(LocationInfo contactInfo) {
        View view = mLayoutInflater.inflate(R.layout.pickview_item_location, mContainer, false);
        TextView tvName = (TextView) view.findViewById(R.id.textViewName);
        TextView tvAddress = (TextView) view.findViewById(R.id.textViewAddress);
        TextView tvDistance = (TextView) view.findViewById(R.id.textViewDistance);
        String name = contactInfo.getName();
        if (TextUtils.isEmpty(name)) {
            tvName.setVisibility(View.GONE);
            tvAddress.setTextColor(getContext().getResources().getColor(R.color.black));
        } else {
            tvName.setText(name);
        }
        if (TextUtils.isEmpty(contactInfo.getAddress())) {
            // tvAddress.setVisibility(View.INVISIBLE);
            tvAddress.setText("暂无相应信息");
        } else {
            tvAddress.setText(contactInfo.getAddress());
        }
        double latitude = contactInfo.getLatitude();
        double longitude = contactInfo.getLongitude();
        if(latitude != 0.0 && longitude != 0.0 && WindowService.mLocationInfo!=null){
        	float distance;
        	LatLng endLatlng;
        	LatLng startLatlng;
        	
        	
        	Gps gcj02 = PositionUtil.bd09_To_Gcj02(WindowService.mLocationInfo.getLatitude(), WindowService.mLocationInfo.getLongitude());
        	startLatlng = new LatLng(gcj02.getWgLat(), gcj02.getWgLon());
        	if(LocalSearchRouteConfirmShowSession.isGaoDe){
	        	endLatlng = new LatLng(latitude, longitude);
        	}else{
        		Gps gcj = PositionUtil.bd09_To_Gcj02(longitude, longitude);
        		endLatlng = new LatLng(gcj.getWgLat(), gcj.getWgLon());
        	}
        	distance = AMapUtils.calculateLineDistance(startLatlng, endLatlng);
        	Log.e(TAG, "distance="+ distance);
        	distance = distance / 1000;
        	tvDistance.setText(mContext.getString(R.string.local_search_distance, distance));
        }
        TextView noText = (TextView) view.findViewById(R.id.textViewNo);
        noText.setText(String.valueOf(numberContent + ""));
        return view;
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_edit_location:
                    Logger.d(TAG, "!--->iv_edit_location click");
                    if (null != mPickLocationViewListener) {
                        mPickLocationViewListener.onEditLocationClick();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 
     * @author xiaodong
     * @date 20151016
     */
    public interface PickLocationViewListener {

        public void onEditLocationClick();

    }

}
