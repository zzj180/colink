/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : LocalSearchItemShowView.java
 * @ProjectName : uniCarSolution_dev_xd_1010
 * @PakageName : com.unisound.unicar.gui.view
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-28
 */
package com.unisound.unicar.gui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.PoiInfo;
import com.unisound.unicar.gui.utils.Logger;

/**
 * 
 * @author xiaodong.he
 * 
 */
public class LocalSearchItemShowView extends FrameLayout implements
		ISessionView {

	private static final String TAG = LocalSearchItemShowView.class
			.getSimpleName();

	private Context mContext;
	private LinearLayout mCallBtnLayout;
	private TextView mTvCallBtn;
	private LinearLayout mRouteBtnLayout;

	private LocalSearchItemViewListener mViewListener;
	public static final int TYPE_BUTTON_CALL = 1;
	public static final int TYPE_BUTTON_NAVI = 2;

	private TextView mTvTitle;
	private TextView has_deal;
	private TextView has_online_reservation;
	private TextView has_coupon;

	private TextView mTvDistance;
	private TextView mTvAddr;
	private TextView mTvCategory;
	private TextView mTvRating;
	private RatingBar mRbRating;
	private TextView mTvAvgPice;

	private PoiInfo mPoiInfo;

	public LocalSearchItemShowView(Context context) {
		super(context);
		mContext = context;
		initView(context);
	}

	private void initView(Context context) {
		Logger.d(TAG, "initView-----");
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.localsearch_item_show_layout, this, true);

		mTvTitle = (TextView) findViewById(R.id.tv_localsearch_title);

		has_deal = (TextView) findViewById(R.id.has_deal);// 是否有团购
		has_online_reservation = (TextView) findViewById(R.id.has_online_reservation);// //是否在线订购
		has_coupon = (TextView) findViewById(R.id.has_coupon);// 是否有优惠

		mTvDistance = (TextView) findViewById(R.id.tv_localsearch_distance);
		mTvAddr = (TextView) findViewById(R.id.tv_localsearch_addr);
		mTvCategory = (TextView) findViewById(R.id.tv_localsearch_category);
		mTvRating = (TextView) findViewById(R.id.tv_rating);
		mRbRating = (RatingBar) findViewById(R.id.rb_rating);
		mTvAvgPice = (TextView) findViewById(R.id.tv_localsearch_avg_pice);

		mCallBtnLayout = (LinearLayout) findViewById(R.id.ll_localsearch_call);
		mTvCallBtn = (TextView) findViewById(R.id.tv_localsearch_call);
		mRouteBtnLayout = (LinearLayout) findViewById(R.id.ll_localsearch_route);

		mCallBtnLayout.setOnClickListener(mOnClickListener);
		mRouteBtnLayout.setOnClickListener(mOnClickListener);
	}

	public void initData(PoiInfo poiInfo) {
		mPoiInfo = poiInfo;
		Logger.d(TAG, "initData--" + mPoiInfo);
		if (null == mPoiInfo) {
			return;
		}
		String branchName = poiInfo.getBranchName();
		if (branchName != null && branchName.length() > 0) {
			mTvTitle.setText(poiInfo.getName() + "(" + branchName + ")");
		} else {
			mTvTitle.setText(poiInfo.getName());
		}

		has_coupon.setVisibility(poiInfo.isHas_coupon() ? View.VISIBLE
				: View.GONE);
		has_deal.setVisibility(poiInfo.isHas_deal() ? View.VISIBLE : View.GONE);
		has_online_reservation
				.setVisibility(poiInfo.isHas_online_reservation() ? View.VISIBLE
						: View.GONE);

		double dist = mPoiInfo.getDistance() / 1000.00;
		Logger.d(
				TAG,
				"!--->--dist = " + dist + "KM; getDistance = "
						+ mPoiInfo.getDistance());
		mTvDistance.setText(mContext.getString(R.string.local_search_distance,
				dist));

		String[] regions = mPoiInfo.getRegions();
		String addr = "";
		for (int i = 0; i < regions.length; i++) {
			addr = addr + regions[i] + " ";
		}
		;
		mTvAddr.setText(addr);

		String[] categorys = mPoiInfo.getCategories();
		String category = "";
		for (int i = 0; i < categorys.length; i++) {
			category = category + categorys[i] + " ";
		}
		mTvCategory.setText(category);

		mTvRating.setText(mContext.getString(R.string.local_search_rating_code,
				mPoiInfo.getRating()));
		mRbRating.setRating(mPoiInfo.getRating() + 0.07f);
		if (mPoiInfo.getAvg_price() > 0) {
			mTvAvgPice.setVisibility(View.VISIBLE);
			mTvAvgPice.setText(mContext
					.getString(R.string.local_search_food_price)
					+ mPoiInfo.getAvg_price());
		} else {
			mTvAvgPice.setVisibility(View.GONE);
		}

		String phoneNum = poiInfo.getTel();
		Logger.d(TAG, "initData--tel:" + phoneNum);
		if (TextUtils.isEmpty(phoneNum)) {
			Drawable phoneD = getResources().getDrawable(
					R.drawable.icon_near_unablecall);
			phoneD.setBounds(0, 0, phoneD.getMinimumWidth(),
					phoneD.getMinimumHeight());
			mTvCallBtn.setCompoundDrawables(phoneD, null, null, null);
			mTvCallBtn.setText(mContext
					.getString(R.string.local_search_no_number));
			mTvCallBtn.setTextColor(Color.argb(76, 246, 246, 246));
			mCallBtnLayout.setEnabled(false);
		} else {
			mTvCallBtn.setText(phoneNum);
			mCallBtnLayout.setEnabled(true);
		}

	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.ll_localsearch_call:
				if (mViewListener != null) {
					mViewListener.onButtonClick(TYPE_BUTTON_CALL);
				}
				break;
			case R.id.ll_localsearch_route:
				if (mViewListener != null) {
					mViewListener.onButtonClick(TYPE_BUTTON_NAVI);
				}
				break;
			default:
				break;
			}
		}
	};

	// public void setText(String content) {
	// if (mTvContent != null) {
	// mTvContent.setText(content);
	// }
	// }

	// public void setBtnCallVisiable(boolean isCall) {
	// if (isCall) {
	// if (mBtnCall != null) {
	// mBtnCall.setVisibility(View.GONE);
	// }
	// }
	// }

	public void setViewListener(LocalSearchItemViewListener listener) {
		this.mViewListener = listener;
	}

	@Override
	public boolean isTemporary() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void release() {

	}

	public interface LocalSearchItemViewListener {
		void onButtonClick(int type);
	}
}
