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
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2012-12-26
 * @ModifiedBy : Brant
 * @ModifiedDate: 2012-12-26
 * @Modified: 2012-12-26: 实现基本功能
 */
public class WaitingContentView extends FrameLayout implements ISessionView {
    public static final String TAG = "WaitingView";
    private TextView mTextViewTitle;
    private ImageView mImgBuffering;
    // private Button mBtnCancel;
    private IWaitingContentViewListener mListener;

    // private OnClickListener mOnClickListener = new OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // if (mListener != null) {
    // mListener.onCancel();
    // }
    // }
    // };

    public WaitingContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.waiting_content_view, this, true);
        mTextViewTitle = (TextView) findViewById(R.id.textViewTitle);
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

        // mBtnCancel = (Button) findViewById(R.id.btnWaitingCancel);
        // mBtnCancel.setOnClickListener(mOnClickListener);
    }

    public WaitingContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaitingContentView(Context context) {
        this(context, null);
    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    @Override
    public void release() {
        // TODO Auto-generated method stub

    }

    public void setTitle(String title) {
        mTextViewTitle.setText(title);
    }

    public void setTitle(int resid) {
        mTextViewTitle.setText(resid);
    }

    public void setLisener(IWaitingContentViewListener listener) {
        mListener = listener;
    }

    public interface IWaitingContentViewListener {
        public void onCancel();
    }
}
