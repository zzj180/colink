package com.unisound.unicar.gui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.Logger;

/**
 * 
 * eg： “打电话”-->“打电话给谁？”-->"zhang san" --> No person--> I konw
 * @author xiaodong
 * @date 20150623
 */
public class IKnowButtonContentView extends FrameLayout implements ISessionView {
	public static final String TAG = IKnowButtonContentView.class.getSimpleName();

	private TextView mTvTitle;
	private Button mBtnIKnow;
	private IIKonwButtonContentViewListener mListener;
	
	public IKnowButtonContentView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_i_know_button, this, true);
		findViews();
		
	}

	private void findViews() {
		mTvTitle = (TextView) findViewById(R.id.tv_title_i_know);
		//mTvTitle.setText(R.string.call_find_no_person);
		
		mBtnIKnow = (Button) findViewById(R.id.btn_i_know);
		mBtnIKnow.setOnClickListener(mOnClickListener);
	}
	
	public void setShowText(String s) {
		mTvTitle.setText(s);
	}
	
	public void setShowText(int id) {
		mTvTitle.setText(id);
	}

	public void setListener(IIKonwButtonContentViewListener listener) {
		this.mListener = listener;
	}
	

	@Override
	public boolean isTemporary() {
		return false;
	}

	@Override
	public void release() {

	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_i_know:
				Logger.d(TAG, "!--->btn_i_know clicked");
				if (null != mListener) {
					mListener.onOk();
				} else {
					Logger.e(TAG, "!--->mOnClickListener is Null !");
				}
				break;
			default:
				break;
			}
			
		}
		
	};

	
	public static interface IIKonwButtonContentViewListener {
		public void onOk();
	}
   
}
