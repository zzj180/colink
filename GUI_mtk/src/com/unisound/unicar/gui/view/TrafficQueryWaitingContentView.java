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
 * @Comments : 描述 路况查询waiting界面
 * @Author : Chenhao
 * @CreateDate : 2015-7-28
 * @ModifiedBy : Chenhao
 * @ModifiedDate: 2015-7-28
 * @Modified: 2012-12-26: 实现基本功能
 */
public class TrafficQueryWaitingContentView extends FrameLayout implements ISessionView {
    public static final String TAG = "TrafficQueryWaitingContentView";
    private ImageView mImgBuffering;
    private LinearLayout mLayCancel;
    private TextView mPoiTextView;
    private ITrafficQueryContentViewListener mListener;
    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onCancel();
            }
        }
    };

    public TrafficQueryWaitingContentView(Context context, AttributeSet attrs, int defStyle) {
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

    public TrafficQueryWaitingContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrafficQueryWaitingContentView(Context context) {
        this(context, null);
    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    @Override
    public void release() {

    }

    public void setLisener(ITrafficQueryContentViewListener listener) {
        mListener = listener;
    }

    public interface ITrafficQueryContentViewListener {
        public void onCancel();
    }

}
