/**
 * Copyright (c) 2012-2013 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : WaitingDialog.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2013-3-11
 */
package com.unisound.unicar.gui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2013-3-11
 * @ModifiedBy : Brant
 * @ModifiedDate: 2013-3-11
 * @Modified:
 * 2013-3-11: 实现基本功能
 */
public class WaitingDialog extends Dialog {
	public static final String TAG = "WaitingDialog";
	private TextView mTextViewTitle;
	private ImageView mImgBuffering;
	private Button mBtnCancel;

	private OnDialogListener mOnDialogListener;
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnCancelDownload:
				dismiss();
				break;
			}
		}
	};

	private OnDismissListener mOnDismissListener = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {
			if (mOnDialogListener != null) {
				mOnDialogListener.onCancel();
			}
		}
	};

	@SuppressWarnings("deprecation")
	public WaitingDialog(Context context, int theme) {
		super(context, theme);
		setCancelable(true);
		setContentView(R.layout.dialog_waiting_content_view);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		findViews();
		setListener();
	}

	public WaitingDialog(Context context) {
		this(context, 0);
	}

	private void findViews() {
		mTextViewTitle = (TextView) findViewById(R.id.textViewTitle);
		mImgBuffering = (ImageView) findViewById(R.id.imageViewBuffering);
		mBtnCancel = (Button) findViewById(R.id.btnCancelDownload);
	}

	private void setListener() {
		setOnDismissListener(mOnDismissListener);
		mBtnCancel.setOnClickListener(mOnClickListener);
	}

	public void setDialogListener(OnDialogListener l) {
		mOnDialogListener = l;
	}

	public void setCancelButtonText(String text) {
		mBtnCancel.setText(text);
	}

	public void setCancelButtonText(int textRes) {
		mBtnCancel.setText(textRes);
	}

	public void setTitle(String title) {
		mTextViewTitle.setText(title);
	}

	public void setTitle(int titleRes) {
		mTextViewTitle.setText(titleRes);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		Drawable drawable = mImgBuffering.getDrawable();
		if (drawable != null && drawable instanceof AnimationDrawable) {
			if (hasFocus) {
				((AnimationDrawable) drawable).start();
			} else {
				((AnimationDrawable) drawable).stop();
			}
		}
	}
}
