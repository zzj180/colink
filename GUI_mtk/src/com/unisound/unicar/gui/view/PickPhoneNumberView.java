package com.unisound.unicar.gui.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.PhoneNumberInfo;
import com.unisound.unicar.gui.utils.Logger;

/**
 * One person has multiple number
 * 
 * @author xiaodong
 * 
 */
public class PickPhoneNumberView extends PickBaseView {

    private static final String TAG = PickPhoneNumberView.class.getSimpleName();

    private Context mContext;

    private ProgressBar mProgressBarWaiting;

    private RelativeLayout mRlCallOutCancel;
    private TextView mTvCallOutCancelTime;

    private CountDownTimer mCountDownTimer;

    private IPickPhoneNumberViewListener mListener;

    /** isCountDownTimerStart */
    private boolean isCountDownTimerStart = false; // XD added 20150812

    public PickPhoneNumberView(Context context) {
        super(context);
        mContext = context;
        // mContainer.setBackgroundResource(R.drawable.function_bg);
        Resources res = getResources();
        int left = (int) (res.getDimension(R.dimen.pick_phone_number_padding_left) + 0.5);
        int right = (int) (res.getDimension(R.dimen.pick_phone_number_padding_right) + 0.5);
        int top = (int) (res.getDimension(R.dimen.pick_phone_number_padding_top) + 0.5);
        int bottom = (int) (res.getDimension(R.dimen.pick_phone_number_padding_bottom) + 0.5);
        mContainer.setPadding(left, top, right, bottom);
    }

    public void initView(String contactName, long contactPhotoId,
            ArrayList<PhoneNumberInfo> phoneNumberInfos) {
        View header = mLayoutInflater.inflate(R.layout.pickview_header_contact, this, false);
        TextView tvName = (TextView) header.findViewById(R.id.textViewContactName);
        String title =
                mContext.getString(R.string.call_out_title_name_muti_number, contactName,
                        phoneNumberInfos.size());
        Logger.d(TAG, "!--->initView()-----title =" + title);
        tvName.setText(title);

        // ImageView imageViewAvatar = (ImageView) header.findViewById(R.id.imageViewAvatar);
        // Drawable drawable = RomContact.loadContactDrawable(getContext(), contactPhotoId);
        // if (drawable != null) {
        // imageViewAvatar.setImageDrawable(drawable);
        // } else {
        // imageViewAvatar.setImageResource(R.drawable.ic_contact_avatar_new);
        // }

        setHeader(header);

        for (int i = 0; i < phoneNumberInfos.size(); i++) {
            PhoneNumberInfo phoneNumberInfo = phoneNumberInfos.get(i);

            View view =
                    mLayoutInflater.inflate(R.layout.pickview_item_muti_numbers, mContainer, false);// 导入布局

            TextView tvPhoneNumber = (TextView) view.findViewById(R.id.textViewPhoneNumber_call);// 号码资源
            tvPhoneNumber.setText(phoneNumberInfo.getNumber());// 设置号码

            TextView noText = (TextView) view.findViewById(R.id.textViewNo_call);// 序号资源
            noText.setText((i + 1) + "");// 设置序号

            TextView tvPhoneNumberNote =
                    (TextView) view.findViewById(R.id.textViewPhoneNumberNote_call);// 归属地资源
            String attribution = phoneNumberInfo.getAttribution();// 设置归属地
            tvPhoneNumberNote.setText(" (" + attribution + ")");

            if (TextUtils.isEmpty(attribution)) {
                tvPhoneNumberNote.setVisibility(View.GONE);
            } else {
                tvPhoneNumberNote.setVisibility(View.VISIBLE);
            }

            // View divider = view.findViewById(R.id.divider);
            // if (getItemCount() == phoneNumberInfos.size() - 1) {
            // divider.setVisibility(View.GONE);
            // }
            addItem(view);
        }

        // add cancel button
        View buttom = mLayoutInflater.inflate(R.layout.pickview_buttom_muti_numbers, this, false);
        addBottomButton(buttom);// xd modify

        mRlCallOutCancel = (RelativeLayout) buttom.findViewById(R.id.rl_call_out_cancel);
        mRlCallOutCancel.setOnClickListener(mOnClickListener);

        mProgressBarWaiting = (ProgressBar) findViewById(R.id.progressBarWaiting);

        mTvCallOutCancelTime = (TextView) findViewById(R.id.tv_call_out_cancel_time);

    }


    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_call_out_cancel:
                    Logger.d(TAG, "!--->onClick()----rl_call_out_cancel");
                    if (mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                    }
                    if (mListener != null) {
                        mListener.onCancel();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public IPickPhoneNumberViewListener getListener() {
        return mListener;
    }

    public void setListener(IPickPhoneNumberViewListener mListener) {
        this.mListener = mListener;
    }

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
                mProgressBarWaiting.setProgress(progress);

                mTvCallOutCancelTime.setText(((millisUntilFinished + 1) / 1000) + "S");
            }

            public void onFinish() {
                mProgressBarWaiting.setProgress(0);
                Logger.d(TAG, "!--->onFinish()----->mListener = " + mListener);
                if (mListener != null) {
                    Logger.d(TAG, "!--->onFinish()----->mListener.onTimeUp()");
                    mListener.onTimeUp();
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


    public static interface IPickPhoneNumberViewListener {
        public void onCancel();

        public void onOk();

        public void onTimeUp();// xd added 20150704
    }


}
