/**
 * Copyright (c) 2012-2013 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : CustomDialog.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2013-3-7
 */
package com.unisound.unicar.gui.view;

import com.coogo.inet.vui.assistant.car.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2013-3-7
 * @ModifiedBy : Brant
 * @ModifiedDate: 2013-3-7
 * @Modified: 2013-3-7: 实现基本功能
 */
public class FunctionShowDialog extends Dialog {
	public static final String TAG = "FunctionShowDialog";
	private TextView mTextViewTitle, mTextViewContent;
	private View mParent;

	public FunctionShowDialog(Context context, int theme) {
		super(context, theme);
		setCancelable(true);
		setContentView(R.layout.function_show_dialog_content_view);

		WindowManager.LayoutParams wl = getWindow().getAttributes();
		wl.gravity = Gravity.RIGHT;
		wl.x = 5;
		getWindow().setAttributes(wl);

		setCanceledOnTouchOutside(true);

		findViews();
	}

	public FunctionShowDialog(Context context) {
		this(context, 0);
	}

	private void findViews() {
		mTextViewTitle = (TextView) findViewById(R.id.textViewTitle);
		mTextViewContent = (TextView) findViewById(R.id.textViewContent);
		mParent = (View) findViewById(R.id.lyFunctionShow);

		mParent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	public void setTitle(String title) {
		mTextViewTitle.setText(title);
	}

	public void setTitle(int titleRes) {
		mTextViewTitle.setText(titleRes);
	}

	public void setContent(String content) {
		mTextViewContent.setText(content);
	}

	public void setContent(int contentRes) {
		mTextViewContent.setText(contentRes);
	}
}
