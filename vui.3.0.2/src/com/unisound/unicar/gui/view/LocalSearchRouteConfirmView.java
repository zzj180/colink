/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : WaitingView.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-12-26
 */
package com.unisound.unicar.gui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2012-12-26
 * @ModifiedBy : Brant
 * @ModifiedDate: 2012-12-26
 * @Modified: 2012-12-26: 实现基本功能
 */
public class LocalSearchRouteConfirmView extends FrameLayout implements
		ISessionView {
	public static final String TAG = "RouteWaitingContentView";
	private ImageView mImgBuffering;
	private TextView mTextStartPOI, mTextEndPOI;

	private FrameLayout mFlRouteOk;
	private TextView mTvRouteCancelTime;
	private RelativeLayout mRlRouteCancel;
	private CountDownTimer mCountDownTimer;
	private ProgressBar mProgressBarWaiting;

	private IRouteWaitingContentViewListener mListener;

	public LocalSearchRouteConfirmView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		Resources res = getResources();

		int left = (int) (res
				.getDimension(R.dimen.call_content_view_margin_left) + 0.5);
		int right = (int) (res
				.getDimension(R.dimen.call_content_view_margin_right) + 0.5);
		int top = (int) (res.getDimension(R.dimen.call_content_view_margin_top) + 0.5);
		int bottom = (int) (res
				.getDimension(R.dimen.call_content_view_margin_bottom) + 0.5);

		setPadding(left, top, right, bottom);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.localsearch_route_content_view, this, true);
		mImgBuffering = (ImageView) findViewById(R.id.imageViewBuffering);
		mImgBuffering.post(new Runnable() {
			@Override
			public void run() {
				Drawable drawable = mImgBuffering.getDrawable();
				if (drawable != null && drawable instanceof AnimationDrawable) {
					((AnimationDrawable) drawable).start();
				}
			}
		});

		mTextStartPOI = (TextView) findViewById(R.id.startPOIText);
		mTextEndPOI = (TextView) findViewById(R.id.endPOIText);
		findViews();
		mFlRouteOk.setOnClickListener(mOnClickListener);
		mRlRouteCancel.setOnClickListener(mOnClickListener);
	}

	private void findViews() {
		mProgressBarWaiting = (ProgressBar) findViewById(R.id.progressBarWaiting);
		mRlRouteCancel = (RelativeLayout) findViewById(R.id.rl_route_cancel);
		mTvRouteCancelTime = (TextView) findViewById(R.id.tv_route_cancel_time);
		mFlRouteOk = (FrameLayout) findViewById(R.id.fl_route_ok);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.fl_route_ok:
				if (mCountDownTimer != null) {
					mCountDownTimer.cancel();
				}
				if (mListener != null) {
					mListener.onOk();
				}
				break;
			case R.id.rl_route_cancel:
				if (mCountDownTimer != null) {
					mCountDownTimer.cancel();
				}

				if (mListener != null) {
					mListener.onCancel();
				}
				break;
			}

		}
	};

	public LocalSearchRouteConfirmView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LocalSearchRouteConfirmView(Context context) {
		this(context, null);
	}

	@Override
	public boolean isTemporary() {
		return true;
	}

	@Override
	public void release() {

	}

	public void setLisener(IRouteWaitingContentViewListener listener) {
		mListener = listener;
	}

	public interface IRouteWaitingContentViewListener {
		public void onOk();

		public void onCancel();

		public void onTimeUp();
	}

	public void setStartPOI(String text) {
		mTextStartPOI.setText(text);
	}

	public void setEndPOI(String text) {
		mTextEndPOI.setText(text);
	}

	public void setListener(IRouteWaitingContentViewListener listener) {
		Logger.d(TAG, "!--->setListener()---mListener = " + listener);
		mListener = listener;
	}

	public void startCountDownTimer(final long countDownMillis) {
		mProgressBarWaiting.setVisibility(View.VISIBLE);
		mTvRouteCancelTime.setText(((countDownMillis + 1) / 1000) + "S");

		mCountDownTimer = new CountDownTimer(countDownMillis, 100) {

			public void onTick(long millisUntilFinished) {

				int progress = (int) ((countDownMillis - millisUntilFinished)
						/ (float) countDownMillis * mProgressBarWaiting
						.getMax());
				mProgressBarWaiting.setProgress(progress);
				mTvRouteCancelTime.setText(((millisUntilFinished + 1) / 1000)
						+ "S");
			}

			public void onFinish() {
				mProgressBarWaiting.setProgress(0);
				if (mListener != null) {
					mListener.onTimeUp();
				}
			}
		}.start();
	}

	public void cancelCountDownTimer() {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
	}
}
