/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : PickLocationView.java
 * @ProjectName : uniCarSolotion
 * @PakageName : com.unisound.unicar.gui.view
 * @Author : Alieen
 * @CreateDate : 2015-07-08
 */
package com.unisound.unicar.gui.view;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.LocationInfo;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Alieen
 * @CreateDate : 2015-07-08
 * @ModifiedBy : Alieen
 * @ModifiedDate: 2015-07-08
 * @Modified:
 * 2015-07-08: 实现基本功能
 */
public class PickLocationView extends PickBaseView {
	public static final String TAG = "PickLocationView";
	private Context mContext = null;
	
	private LinearLayout mLoadMoreLay;
	private boolean btn_more_use = false;
	private int numberContent = 0 ;
	private int pageMaxNumber = 5;
	private ArrayList<LocationInfo> mLocationInfos;
	public PickLocationView(Context context) {
		super(context);
		mContext = context;
	}

	public void initView(ArrayList<LocationInfo> locationInfos) {
		mLocationInfos = locationInfos;
		View header = mLayoutInflater.inflate(R.layout.pickview_header_location, this, false);
		TextView tvHead = (TextView) header.findViewById(R.id.tv_header_muti_location);
		tvHead.setText(mContext.getString(R.string.location_heard_title));
		setHeader(header);
		
		if (locationInfos.size() >= pageMaxNumber) {
			for (int i = 0; i < pageMaxNumber; i++) {
				btn_more_use = true;
				numberContent += 1;
				addItem(getLocalItemView(locationInfos.get(i)));
			}
			
			View buttomLay = mLayoutInflater.inflate(R.layout.list_item_load_more, null,false);
			mLoadMoreLay = (LinearLayout)buttomLay.findViewById(R.id.btn_more_local);
			/*< BUG-2792 XD 20150722 modify Begin */
			addButtom(buttomLay);
			//addItem(buttomLay);
			/* BUG-2792 20150722 XD modify End >*/
			mLoadMoreLay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					mLoadMoreLay.setVisibility(View.GONE);
					addMoreView();
				}
			});
			
		}else if (locationInfos.size() < pageMaxNumber) {
			for (int i = 0; i < locationInfos.size(); i++) {
				btn_more_use = false;
				numberContent += 1;
				addItem(getLocalItemView(locationInfos.get(i)));
			}
		}
		
		updateLayoutParams();
	}
	
	private void addMoreView(){
		if (btn_more_use) {
			for (int i = pageMaxNumber; i < mLocationInfos.size(); i++) {
				numberContent += 1;
				btn_more_use = true;
				addItem(getLocalItemView(mLocationInfos.get(i)));
			}
		}
	}
	public View getLocalItemView(LocationInfo contactInfo){
		View view = mLayoutInflater.inflate(R.layout.pickview_item_location, mContainer, false);
		TextView tvName = (TextView) view.findViewById(R.id.textViewName);
		TextView tvAddress = (TextView) view.findViewById(R.id.textViewAddress);
		String name = contactInfo.getName();
		if (TextUtils.isEmpty(name)) {
			tvName.setVisibility(View.GONE);
			tvAddress.setTextColor(getContext().getResources().getColor(R.color.black));
		} else {
			tvName.setText(name);
		}
		if (TextUtils.isEmpty(contactInfo.getAddress())) {
			//tvAddress.setVisibility(View.INVISIBLE);
			tvAddress.setText("暂无相应信息");
		} else {
			tvAddress.setText(contactInfo.getAddress());
		}
		TextView noText = (TextView) view.findViewById(R.id.textViewNo);
		noText.setText(String.valueOf(numberContent+""));
		return view;
	}
}
