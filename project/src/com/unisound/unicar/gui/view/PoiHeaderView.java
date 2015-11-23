/**
 * Copyright (c) 2012-2013 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : PoiHeaderView.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2013-2-21
 */
package com.unisound.unicar.gui.view;

import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2013-2-21
 * @ModifiedBy : Brant
 * @ModifiedDate: 2013-2-21
 * @Modified:
 * 2013-2-21: 实现基本功能
 */
public class PoiHeaderView extends FrameLayout {
	public static final String TAG = "PoiHeaderView";
	private LayoutInflater mInflater;
	private View mViewSort;
	private TextView mTextViewTitle, mTextViewSort;
	private PopupWindow mPopupWindowSort;
	private LinearLayout mPopWindowSortContentView;
	private OnSortChangedLisener mSortChangedLisener;
	private int mDividerHeight;
	private int mColor;
	private SortItem[] mItems;
	private Map<String, String> mParams;
	private int mSelectdIndex = -1;

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.textViewPoiSort:
				if (mItems == null || mItems.length == 0) {
					return;
				}

				if (mPopupWindowSort == null) {
					Context context = getContext();
					mPopupWindowSort = new PopupWindow(context);
					mPopupWindowSort.setWidth(LayoutParams.WRAP_CONTENT);
					mPopupWindowSort.setHeight(LayoutParams.WRAP_CONTENT);
					mPopWindowSortContentView = new LinearLayout(context);
					mPopWindowSortContentView.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
					mPopWindowSortContentView.setOrientation(LinearLayout.VERTICAL);

					for (int i = 0; i < mItems.length; i++) {
						View item = mInflater.inflate(R.layout.poi_sort_list_item, mPopWindowSortContentView, false);
						item.setTag(i);
						item.setOnClickListener(mOnClickListener);
						TextView tv = (TextView) item.findViewById(R.id.textViewPoiSortItemName);
						tv.setText(mItems[i].name);
						if (i == 0) {
							mPopWindowSortContentView.addView(item);
						} else {
							mPopWindowSortContentView.addView(createDivierView());
							mPopWindowSortContentView.addView(item);
						}
					}
					mPopupWindowSort.setContentView(mPopWindowSortContentView);
					mPopupWindowSort.setOutsideTouchable(true);
					mPopupWindowSort.setFocusable(true);
				}
				int[] loc = new int[2];
				mTextViewSort.getLocationOnScreen(loc);

				mPopupWindowSort.showAtLocation(
					mTextViewSort,
					Gravity.LEFT | Gravity.TOP,
					loc[0] + mTextViewSort.getWidth()
							- getContext().getResources().getDimensionPixelSize(R.dimen.poi_sort_list_item_width),
					loc[1] + mTextViewSort.getHeight());
				break;
			case R.id.linearLayoutPoiListItem:
				if (mSortChangedLisener != null) {
					Integer index = (Integer) v.getTag();
					setSortSelectedItem(index);
				}
				mPopupWindowSort.dismiss();
				break;
			}

		}
	};

	public PoiHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.poi_list_header, this, true);
		findViews();
		setLisener();
	}

	public PoiHeaderView(Context context) {
		this(context, null);
	}

	private void findViews() {
		mViewSort = findViewById(R.id.frarmeLayoutPoiListSort);
		mTextViewTitle = (TextView) findViewById(R.id.textViewPoiHeader);
		mTextViewSort = (TextView) mViewSort.findViewById(R.id.textViewPoiSort);
	}

	private void setLisener() {
		mTextViewSort.setOnClickListener(mOnClickListener);
	}

	@SuppressWarnings("deprecation")
	private View createDivierView() {
		View v = new View(getContext());
		v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, mDividerHeight));
		v.setBackgroundColor(mColor);
		return v;
	}

	public void setTitle(String title) {
		mTextViewTitle.setText(title);
	}

	public void setDividerHeight(int height) {
		mDividerHeight = height;
	}

	public void setDividerColor(int color) {
		mColor = color;
	}

	public void setSortItems(SortItem[] items) {
		mItems = items;
	}

	public void setSortSelectedItem(int index) {
		finishLoading();
		if (mSelectdIndex == index) {
			return;
		}
		mSelectdIndex = index;
		mTextViewSort.setText(mItems[mSelectdIndex].name);
		if (mSortChangedLisener != null) {
			mSortChangedLisener.onChanged(mSelectdIndex);
		}
	}

	public void setSortChangedListener(OnSortChangedLisener listener) {
		mSortChangedLisener = listener;
	}

	public void onLoading() {
		// mPoiLoading.setVisibility(View.VISIBLE);
		// mViewSort.setVisibility(View.GONE);
	}

	public void finishLoading() {
		// mPoiLoading.setVisibility(View.GONE);
		// mViewSort.setVisibility(View.VISIBLE);
	}

	public Map<String, String> getParams() {
		return mParams;
	}

	public void setParams(Map<String, String> mParams) {
		this.mParams = mParams;
	}

	public static class SortItem {
		public String name;
		public String value;
		public boolean focus;
		public String onSelected;

		public SortItem(String name, String value, boolean focus, String onSelected) {
			this.name = name;
			this.value = value;
			this.focus = focus;
			this.onSelected = onSelected;
		}
	}

	public static interface OnSortChangedLisener {
		void onChanged(int index);
	}
}
