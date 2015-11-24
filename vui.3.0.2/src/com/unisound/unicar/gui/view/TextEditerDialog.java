/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : TextEditerDialog.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-11-14
 */
package com.unisound.unicar.gui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.coogo.inet.vui.assistant.car.R;

public class TextEditerDialog extends Dialog {
	public static final String TAG = "TextEditerDialog";
	private Button mBtnOK, mBtnCancel;
	private EditText mEditTextInput;
	private boolean mTriggered = false;
	private OnDismissListener mDismssListener = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {
			if (mTriggered) {
				mTriggered = false;
			} else if (mTextEditorListener != null) {
				mTextEditorListener.onCancel();
			}
		}
	};
	private ITextEditorListener mTextEditorListener;

	// private OnClickListener mOnClickListener = new OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// // TODO Auto-generated method stub
	// @Override
	// public void onClick(dialog v) {
	// switch (v.getId()) {
	// case R.id.btn_sms_cancel:
	// closeSoftKeyboard();
	// if (mTextEditorListener != null) {
	// mTextEditorListener.onCancel();
	// }
	// mTriggered = true;
	// dismiss();
	// break;
	// case R.id.btn_sms_ok:
	// closeSoftKeyboard();
	// if (mTextEditorListener != null) {
	// mTextEditorListener.onResult(getText());
	// }
	// dismiss();
	// mTriggered = true;
	// break;
	// }
	// }
	// }
	// };

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_sms_cancel:
				closeSoftKeyboard();
				if (mTextEditorListener != null) {
					mTextEditorListener.onCancel();
				}
				mTriggered = true;
				dismiss();
				break;
			case R.id.btn_sms_ok:
				closeSoftKeyboard();
				if (mTextEditorListener != null) {
					mTextEditorListener.onResult(getText());
				}
				dismiss();
				mTriggered = true;
				break;
			}
		}

	};

	public TextEditerDialog(Context context, int theme) {
		super(context, theme);
		setCancelable(true);
		setOnDismissListener(mDismssListener);
		setContentView(R.layout.dialog_text_editor);
		getWindow()
				.setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
								| WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		mEditTextInput = (EditText) findViewById(R.id.editTextMessage);
		mBtnCancel = (Button) findViewById(R.id.btn_sms_cancel);
		mBtnOK = (Button) findViewById(R.id.btn_sms_ok);
		mBtnCancel.setOnClickListener(mOnClickListener);
		mBtnOK.setOnClickListener(mOnClickListener);
	}

	public TextEditerDialog(Context context) {
		this(context, 0);
	}

	public ITextEditorListener getTextEditorListener() {
		return mTextEditorListener;
	}

	public void setTextEditorListener(ITextEditorListener l) {
		this.mTextEditorListener = l;
	}

	public void setText(String text) {
		mEditTextInput.setText(text);
		mEditTextInput.setSelection(mEditTextInput.getText().length());
	}

	public String getText() {
		return mEditTextInput.getText().toString();
	}

	public void setOKButtonText(String text) {
		mBtnOK.setText(text);
	}

	public void setCancelButtonText(String text) {
		mBtnCancel.setText(text);
	}

	private void closeSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditTextInput.getWindowToken(), 0);
	}

	public static interface ITextEditorListener {
		public void onCancel();

		public void onResult(String text);
	}
}
