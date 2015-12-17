/**
 * Copyright (c) 2012-2015 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : CallWaitingDialog.java
 * @ProjectName : UnicarGUI
 * @PakageName : com.unisound.unicar.gui.view
 * @Author : xiaodong
 * @CreateDate : 20150625
 */
package com.unisound.unicar.gui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
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
 * Call out view
 * 
 * @author xiaodong
 * 
 */
public class CallContentView extends FrameLayout implements ISessionView {
    public static final String TAG = CallContentView.class.getSimpleName();
    private TextView mTextViewName, mTextViewPhoneNumber, mTextViewAttribution;

    // xiaodong modify
    private FrameLayout mFlCallOutOk;
    private RelativeLayout mRlCallOutCancel;
    private TextView mTvCallOutCancelTime;

    private ImageView mImageViewAvatar;

    private ICallContentViewListener mListener;
    private CountDownTimer mCountDownTimer;
    private ProgressBar mProgressBarWaiting;

    private TextView tvTitleCallOutName;

    /** isCountDownTimerStart */
    private boolean isCountDownTimerStart = false; // XD added 20150812

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fl_call_out_ok:
                    Logger.d(TAG, "!--->onClick()-----R.id.fl_call_out_ok");
                    if (mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                    }
                    if (mListener != null) {
                        mListener.onOk();
                    }
                    break;
                case R.id.rl_call_out_cancel:
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

    public CallContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Logger.d(TAG, "!--->-----CallContentView()--------");
        Resources res = getResources();

        int left = (int) (res.getDimension(R.dimen.call_content_view_margin_left) + 0.5);
        int right = (int) (res.getDimension(R.dimen.call_content_view_margin_right) + 0.5);
        int top = (int) (res.getDimension(R.dimen.call_content_view_margin_top) + 0.5);
        int bottom = (int) (res.getDimension(R.dimen.call_content_view_margin_bottom) + 0.5);

        setPadding(left, top, right, bottom);
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.call_content_view, this, true);
        findViews();
        mFlCallOutOk.setOnClickListener(mOnClickListener);
        mRlCallOutCancel.setOnClickListener(mOnClickListener);

    }

    public CallContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CallContentView(Context context) {
        this(context, null);
    }

    private void findViews() {

        tvTitleCallOutName = (TextView) findViewById(R.id.tv_title_call_out_name);
        mImageViewAvatar = (ImageView) findViewById(R.id.iv_contact_avatar);
        mTextViewName = (TextView) findViewById(R.id.callDialogName);
        mTextViewPhoneNumber = (TextView) findViewById(R.id.callDialogNumber);
        // mTextViewAttribution = (TextView) findViewById(R.id.callDialogAttribution);
        mProgressBarWaiting = (ProgressBar) findViewById(R.id.progressBarWaiting);

        mRlCallOutCancel = (RelativeLayout) findViewById(R.id.rl_call_out_cancel);
        mTvCallOutCancelTime = (TextView) findViewById(R.id.tv_call_out_cancel_time);
        mFlCallOutOk = (FrameLayout) findViewById(R.id.fl_call_out_ok);

    }

    public ICallContentViewListener getListener() {
        return mListener;
    }

    public void setListener(ICallContentViewListener mListener) {
        Logger.d(TAG, "!--->setListener()---mListener = " + mListener);
        this.mListener = mListener;
    }

    public void setModeConfirm() {
        // if (mBtnCallOk != null) {
        // mBtnCallOk.setText("确\t\t定");
        // }
    }

    public void setModeNomal() {
        // if (mBtnCallOk != null) {
        // //mBtnCallOk.setText("立即拨打");
        // mBtnCallOk.setVisibility(View.GONE);
        // }
        //
        // if (mBtnCancel != null) {
        // //mBtnCallOk.setText("立即拨打");
        // mBtnCancel.setVisibility(View.GONE);
        // }
    }

    public void initView(Drawable drawable, String contactName, String phoneNumber,
            String attribution) {
        // if (drawable != null) {
        // // mImageViewAvatar.setImageDrawable(drawable);
        // } else {
        // // mImageViewAvatar.setImageResource(R.drawable.ic_contact_avatar_new);
        // }


        tvTitleCallOutName.setText(getResources().getString(R.string.call_out_name, contactName));
        mTextViewName.setText(contactName);
        Logger.d(TAG, "!--->initView()----phoneNumber = " + phoneNumber);
        mTextViewPhoneNumber.setText(phoneNumber);
        // if (TextUtils.isEmpty(attribution)) {
        // mTextViewAttribution.setText(R.string.unknown);
        // } else {
        // /**2014-11-14 yujun*/
        // mTextViewAttribution.setTextSize(18);
        // /**-------------------*/
        // mTextViewAttribution.setText("("+attribution+")");
        // }

    }

    /**
     * if mCountDownTimer has Start, do not start again XD 20150812 modify
     * 
     * @param countDownMillis
     */
    public void startCountDownTimer(final long countDownMillis) {
        if (isCountDownTimerStart) {
            return;
        }
        mProgressBarWaiting.setVisibility(View.VISIBLE);
        mTvCallOutCancelTime.setText(((countDownMillis + 1) / 1000) + "S");

        mCountDownTimer = new CountDownTimer(countDownMillis, 100) {

            public void onTick(long millisUntilFinished) {

                int progress =
                        (int) ((countDownMillis - millisUntilFinished) / (float) countDownMillis * mProgressBarWaiting
                                .getMax());
                // Logger.d(TAG, "!--->startCountDownTimer()--progress = "+progress);
                // mProgressBarWaiting.setProgress(mProgressBarWaiting.getMax() - progress);
                mProgressBarWaiting.setProgress(progress);

                mTvCallOutCancelTime.setText(((millisUntilFinished + 1) / 1000) + "S");
            }

            public void onFinish() {
                mProgressBarWaiting.setProgress(100);
                mProgressBarWaiting.setProgress(0);
                if (mListener != null) {
                    Logger.d(TAG, "!--->mCountDownTimer---onFinish()");
                    mListener.onTimeUp(); // xd modify 20150704
                }
            }
        }.start();
        isCountDownTimerStart = true;
    }

    public void cancelCountDownTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        isCountDownTimerStart = false;
    }

    @Override
    public void release() {

    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    public static interface ICallContentViewListener {
        /**
         * click cancel
         */
        public void onCancel();

        /**
         * click ok
         */
        public void onOk();

        /**
         * time up
         */
        public void onTimeUp();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
