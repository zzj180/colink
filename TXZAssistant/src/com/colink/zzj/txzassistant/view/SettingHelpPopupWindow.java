/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : SettingHelpPopupWindow.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.view
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-11-10
 */
package com.colink.zzj.txzassistant.view;

import com.colink.zzj.txzassistant.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;


/**
 * @author xiaodong.he
 * @date 2015-11-10
 */
public class SettingHelpPopupWindow extends PopupWindow {

    private Context mContext;

    private TextView mTextViewTitle, mTextViewContent;
    private View mParent;
    private TextView mBtnIKnow;

    @SuppressLint("InflateParams")
	public SettingHelpPopupWindow(Context context) {
        mContext = context;

        LayoutInflater mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = mLayoutInflater.inflate(R.layout.pop_setting_help, null);

        this.setContentView(contentView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        // set PopupWindow clickable
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // update state
        this.update();

        findViews(contentView);

    }

    private void findViews(View contentView) {
        mTextViewTitle = (TextView) contentView.findViewById(R.id.tv_pop_title);
        mTextViewContent = (TextView) contentView.findViewById(R.id.tv_pop_content);
        mParent = (View) contentView.findViewById(R.id.ll_setting_pop);
        mBtnIKnow = (TextView) contentView.findViewById(R.id.tv_i_know);

        mBtnIKnow.setOnClickListener(mOnClickListener);
        mParent.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_i_know:
                    dismiss();
                    break;
                case R.id.ll_setting_pop:
                    dismiss();
                    break;
                default:
                    break;
            }

        }
    };

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

    /**
     * showChangeTextPopWindow
     */
    public void showPopWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.TOP, 0, 0);
        }
    }

}
