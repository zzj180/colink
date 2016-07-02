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

import com.coogo.inet.vui.assistant.car.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2012-12-26
 * @ModifiedBy : Brant
 * @ModifiedDate: 2012-12-26
 * @Modified: 2012-12-26: 实现基本功能
 */
public class LocalSearchWaitingContentView extends FrameLayout implements ISessionView {
    public static final String TAG = "LocalSearchWaitingContentView";
    private ImageView mImgBuffering;
    private LinearLayout mLayCancel;
    private TextView mPoiTextView;
    private IRouteWaitingContentViewListener mListener;
    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onCancel();
            }
        }
    };

    public LocalSearchWaitingContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.localsearch_waiting_content_view, this, true);
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

        mPoiTextView = (TextView) findViewById(R.id.endPOIText);

        mLayCancel = (LinearLayout) findViewById(R.id.layCancel);
        mLayCancel.setOnClickListener(mOnClickListener);

    }

    public void setPoiText(String text) {
        mPoiTextView.setText(text);
    }

    public LocalSearchWaitingContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalSearchWaitingContentView(Context context) {
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
        public void onCancel();
    }

    // public void setStartPOI(String text){
    // mTextStartPOI.setText(text);
    // }
    //
    // public void setEndPOI(String text){
    // mTextEndPOI.setText(text);
    // }
}
