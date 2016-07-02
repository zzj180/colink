/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : EditWakeupWordPopWindow.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.view
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-11-1
 */
package com.colink.zzj.txzassistant.view;

import com.colink.zzj.txzassistant.AssistantService;
import com.colink.zzj.txzassistant.R;
import com.colink.zzj.txzassistant.util.DeviceTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

/**
 * @author zzj
 * @date 2015-11-1
 */
public class EditWakeupWordPopWindow extends PopupWindow {

    private Context mContext;

    private EditText mEtInputText;
    private Button mBtnOk;

    private OnEditWakeupwordPopListener mOnEditWakeupwordPopListener;

    /**
     * 
     * @param context
     */
    @SuppressLint("InflateParams")
	public EditWakeupWordPopWindow(Context context) {
        mContext = context;

        LayoutInflater mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = mLayoutInflater.inflate(R.layout.pop_edit_wakeupword_layout, null);

        this.setContentView(contentView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        // set PopupWindow clickable
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // update state
        this.update();

        mEtInputText = (EditText) contentView.findViewById(R.id.et_change_location);

        mEtInputText.setFocusable(true);
        mBtnOk = (Button) contentView.findViewById(R.id.btn_change_location_ok);
        Button btnCancel = (Button) contentView.findViewById(R.id.btn_change_location_cancel);
        mBtnOk.setOnClickListener(mOnClickListener);
        mBtnOk.setEnabled(false);
        btnCancel.setOnClickListener(mOnClickListener);

        mEtInputText.addTextChangedListener(mPopEditTextWatcher);

    }

    /**
     * showChangeTextPopWindow
     */
    public void showPopWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.TOP, 0, 0);
            // show Input Keypad
            mEtInputText.requestFocus();
            DeviceTool.showEditTextKeyboard(mEtInputText, true);
        }
    }

    private TextWatcher mPopEditTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (null == mBtnOk) {
                return;
            }
            if (TextUtils.isEmpty(s)) {
                mBtnOk.setEnabled(false);
            } else {
                mBtnOk.setEnabled(true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_change_location_ok:
                    if (null != mEtInputText) {
                        String newText = mEtInputText.getText().toString();
                        if (!TextUtils.isEmpty(newText)) {
                            if (null != mOnEditWakeupwordPopListener) {
                                mOnEditWakeupwordPopListener.onOkClick(newText);
                            } else {
                                sendUpdateWakeupWord(mContext, newText);
                            }
                        }
                    }
                    EditWakeupWordPopWindow.this.dismiss();
                    break;
                case R.id.btn_change_location_cancel:
                    if (null != mOnEditWakeupwordPopListener) {
                        mOnEditWakeupwordPopListener.onCancelClick();
                    }
                    EditWakeupWordPopWindow.this.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 
     * @param wakeupWord
     */
    private void sendUpdateWakeupWord(Context mContext, String wakeupWord) {
        // Intent intent = new Intent(WindowService.ACTION_UPDATE_WAKEUP_WORD);
        // intent.putExtra(WindowService.EXTRA_KEY_WAKEUP_WORD, newText);
        // sendBroadcast(intent);

        Intent intent = new Intent(mContext, AssistantService.class);
        intent.setAction(AssistantService.ACTION_UPDATE_WAKEUP_WORD);
        intent.putExtra(AssistantService.EXTRA_KEY_WAKEUP_WORD, wakeupWord);
        mContext.startService(intent);
    }

    public void setOnEditWakeupwordPopListener(OnEditWakeupwordPopListener listener) {
        mOnEditWakeupwordPopListener = listener;
    }

    /**
     * 
     * @author xiaodong.he
     * @date 2015-11-02
     */
    public interface OnEditWakeupwordPopListener {

        public void onOkClick(String wakeupWord);

        public void onCancelClick();

    }
}
