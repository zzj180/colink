/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : EditLocationPopWindow.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.view
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-11-18
 */
package com.unisound.unicar.gui.view;

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
import cn.yunzhisheng.vui.assistant.WindowService;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * 
 * @author xiaodong.he
 * @date 2015-11-18
 */
public class EditLocationPopWindow extends PopupWindow {

    private static final String TAG = EditLocationPopWindow.class.getSimpleName();

    private Context mContext;

    private EditText mEtInputText;
    private Button mBtnOk;

    private OnEditLocationPopListener mOnEditLocationPopListener;

    /**
     * 
     * @param context
     */
    public EditLocationPopWindow(Context context) {
        mContext = context;

        LayoutInflater mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = mLayoutInflater.inflate(R.layout.layout_edit_location, null);

        this.setContentView(contentView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        // set PopupWindow clickable
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // update state
        this.update();

        mEtInputText = (EditText) contentView.findViewById(R.id.et_change_location);
        mEtInputText.setHint(R.string.location_change_default_hint);

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
        Logger.d(TAG, "showPopWindow-----");
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
            Logger.d(TAG, "mPopEditTextWatcher--onTextChanged--s=" + s + "; count = " + count
                    + "; start = " + start + "; before = " + before);
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
            Logger.d(TAG, "mPopEditTextWatcher--beforeTextChanged--s=" + s + "; count = " + count
                    + "; start = " + start + "; after = " + after);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_change_location_ok:
                    Logger.d(TAG, "mOnClickListener-OK--");
                    if (null != mEtInputText) {
                        String newText = mEtInputText.getText().toString();
                        Logger.d(TAG, "mOnClickListener--OK--newText = " + newText);
                        if (!TextUtils.isEmpty(newText)) {
                            if (null != mOnEditLocationPopListener) {
                                mOnEditLocationPopListener.onOkClick(newText);
                            } else {
                                sendUpdateLocation(mContext, newText);
                            }
                        }
                    }
                    EditLocationPopWindow.this.dismiss();
                    break;
                case R.id.btn_change_location_cancel:
                    Logger.d(TAG, "mPopupWindowOnClickListener--Cancel click");
                    if (null != mOnEditLocationPopListener) {
                        mOnEditLocationPopListener.onCancelClick();
                    }
                    EditLocationPopWindow.this.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 
     * @param location
     */
    private void sendUpdateLocation(Context mContext, String location) {
        Logger.d(TAG, "sendUpdateLocation--location:" + location);
        Intent intent = new Intent(mContext, WindowService.class);
        intent.setAction(WindowService.ACTION_UPDATE_LOCATION);
        intent.putExtra(WindowService.EXTRA_KEY_LOCATION, location);
        mContext.startService(intent);
    }

    public void setOnEditWakeupwordPopListener(OnEditLocationPopListener listener) {
        mOnEditLocationPopListener = listener;
    }

    /**
     * 
     * @author xiaodong.he
     * @date 2015-11-18
     */
    public interface OnEditLocationPopListener {

        public void onOkClick(String location);

        public void onCancelClick();

    }
}
