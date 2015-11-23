/**
 * Copyright (c) 2012-2012 Mango(Shanghai) Co.Ltd. All right reserved.
 * @FileName : PickView.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-11-7
 */
package com.unisound.unicar.gui.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.Logger;

public class PickBaseView extends LinearLayout implements ISessionView {
	public static final String TAG = "PickBaseView";
//	protected static final int PICK_VIEW_TAG_BUTTON_CANCEL = -1;
	private boolean mHasHeader;
	private boolean mHasBottom; //xd added 20150722

	// protected Button mBtnCancel;
	protected IPickListener mPickListener;
	protected LinearLayout mContainer;
	protected LayoutInflater mLayoutInflater;
	protected OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			/*< XD 20150805 modify Begin */
			Object tagObj = v.getTag();
			Logger.d(TAG, "onClicked!!!---tagObj = " + tagObj);
			if(null == tagObj || !(tagObj instanceof Integer)){
				return;
			}
			Integer itemIndex = (Integer) tagObj;
			onViewClick(itemIndex);
			/* XD 20150805 modify End > */
		}
	};

	@SuppressWarnings("deprecation")
	public PickBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setOrientation(VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		mContainer = new LinearLayout(context);
		mContainer.setOrientation(VERTICAL);
		// mBtnCancel = new Button(context);
		// mBtnCancel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		// height));
		//
		// mBtnCancel.setText(R.string.cancel);
		// mBtnCancel.setTextSize(textSize);
		// mBtnCancel.setBackgroundResource(R.drawable.btn_call_cancle_bg);
		addView(mContainer);
		// addView(mBtnCancel);
		// mBtnCancel.setTag(PICK_VIEW_TAG_BUTTON_CANCEL);
		// mBtnCancel.setOnClickListener(mOnClickListener);
		/*
		 * mContainer = new LinearLayout(context);
		 * mContainer.setOrientation(VERTICAL); addView(mContainer);
		 * mLayoutInflater = (LayoutInflater)
		 * context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 * mLayoutInflater.inflate(R.layout.pickphone_content_view, this, true);
		 * mBtnCancel = (Button) findViewById(R.id.btnCancel);
		 * mBtnCancel.setOnClickListener(mOnClickListener);
		 */
	}

	public PickBaseView(Context context) {
		this(context, null);
	}

	public void setHeader(View view) {
		mContainer.addView(view, 0);
		mHasHeader = true;
	}

	public void removeHeader() {
		if (mHasHeader) {
			mContainer.removeViewAt(0);
			mHasHeader = false;
		}
	}

	protected void addItem(View view) {
		Logger.d(TAG, "!--->addItem:--mHasHeader="+mHasHeader+"; childCount = "+mContainer.getChildCount());
		mContainer.addView(view);
		view.setOnClickListener(mOnClickListener);
		
		int itemIndex = getItemCount() -1;
		Logger.d(TAG, "!--->addItem--itemIndex = "+itemIndex);
		//view.setTag(mHasHeader ? mContainer.getChildCount() - 2 : mContainer.getChildCount() - 1);// 设置tag
		view.setTag(itemIndex);
		
	}

	/*< BUG-2792 XD 20150722 added Begin */
	protected void addButtom(View view) {
		Logger.d(TAG, "!--->addButtom:--------");
		mHasBottom = true;
		mContainer.addView(view);
		view.setOnClickListener(mOnClickListener);
//		view.setTag(mHasHeader ? mContainer.getChildCount() - 2 : mContainer
//				.getChildCount() - 1);// 设置tag
	}
	/* BUG-2792 XD 20150722 added End > */
	
	/*< BUG-2792 XD 20150722 modify Begin  */
	public int getItemCount() {
//		if (mHasHeader) {
//			return mContainer.getChildCount() - 1;
//		} else {
//			return mContainer.getChildCount();
//		}
		int count = mContainer.getChildCount();
		if (mHasHeader) {
			count--;
		}
		if(mHasBottom){
			count--;
		}
		Logger.d(TAG, "!--->getItemCount--count = "+count);
		return count;
	}
	/* BUG-2792 XD 20150722 modify End > */

	public View getItem(int index) {
		int count = getItemCount();
		if (index >= 0 && index < count) {
			return mHasHeader ? mContainer.getChildAt(index + 1) : mContainer
					.getChildAt(index);
		}
		return null;
	}

	public IPickListener getPickListener() {
		return mPickListener;
	}

	public void setPickListener(IPickListener mPickListener) {
		this.mPickListener = mPickListener;
	}

	@Override
	public boolean isTemporary() {
		return true;
	}

	protected void onViewClick(int tag) {
		Logger.d(TAG, "!--->onViewClick tag = " + tag);
		/*
		 * if (tag == PICK_VIEW_TAG_BUTTON_CANCEL) { if (mPickListener != null)
		 * { mPickListener.onPickCancel(); } return; } else
		 */if (tag >= 0) {
			if (mPickListener != null) {
				mPickListener.onItemPicked(tag);
			}
			return;
		}
	}

	@Override
	public void release() {

	}

	public static interface IPickListener {
		void onItemPicked(int position);

		void onPickCancel();
		void onNext();
		void onPre();
	}

	public void getPageNumber() {

	}

    public void updateLayoutParams() {
        Resources res = getResources();
        if (getItemCount() < 3) {
            setLayoutParams(new LayoutParams(res
                    .getDimensionPixelSize(R.dimen.window_width), res
                    .getDimensionPixelSize(R.dimen.session_contain_height)));
        } else {
            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }
}
