package com.unisound.unicar.gui.view;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.phone.Telephony;
import com.unisound.unicar.gui.utils.Logger;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author xiaodong
 * @date 20150710
 */
public class ReceiveCallView extends RelativeLayout implements ISessionView {

    public static final String TAG = "ReceiveCallView";

    // private Handler mSessionManagerHandler = null;

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private String mName, mNumber, mLocation;
    private Button mHangUpBtn, mAnswerBtn;

    private IReceiveCallViewListener mListener;

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.hangupBtn:
                    Telephony.endCall(mContext);
                    if (null != mListener) {
                        mListener.onRejectInComingCall();
                    }
                    break;
                case R.id.answerBtn:
                    Telephony.answerRingingCall(mContext);
                    if (null != mListener) {
                        mListener.onAnswerInComingCall();
                    }
                    break;
                default:
                    break;
            }
            // mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_ANSWER_CALL); //xd
            // delete 20150710
        }
    };

    /**
     * 
     * @param context
     * @param name
     * @param number
     * @param drawable
     * @param location
     */
    public ReceiveCallView(Context context, String name, String number, Drawable drawable,
            String location) {
        super(context);
        Logger.d(TAG, "!--->ReceiveCallView: name = " + name + "; number = " + number);
        mName = name;
        mNumber = number;
        mLocation = location;
        mContext = context;
        mLayoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        findViews();
    }

    // public ReceiveCallView(Context context,String name,String number, Handler
    // sessionManagerHandler) {
    // super(context);
    // Logger.d(TAG, "!--->ReceiveCallView: name = "+name+"; number = "+number);
    // mName = name;
    // mNumber = number;
    // mContext = context;
    // mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    // this.mSessionManagerHandler = sessionManagerHandler;
    // }

    private void findViews() {
        View view = mLayoutInflater.inflate(R.layout.call_incoming_view, this, true);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.linearLayoutCALLTxtItem);
        TextView textViewHead = (TextView) ll.findViewById(R.id.callViewHead);
        TextView tvLoaction = (TextView) ll.findViewById(R.id.tv_incall_location);
        mHangUpBtn = (Button) ll.findViewById(R.id.answerBtn);
        mAnswerBtn = (Button) ll.findViewById(R.id.hangupBtn);

        mHangUpBtn.setOnClickListener(mOnClickListener);
        mAnswerBtn.setOnClickListener(mOnClickListener);

        textViewHead.setText(mContext.getString(R.string.call_incoming_title_contact, mName));
        if (!TextUtils.isEmpty(mLocation)) {
            tvLoaction.setText(mLocation);
        }
    }


    public IReceiveCallViewListener getListener() {
        return mListener;
    }

    public void setListener(IReceiveCallViewListener listener) {
        this.mListener = listener;
    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    @Override
    public void release() {
        removeAllViews();
    }

    public static interface IReceiveCallViewListener {

        public void onRejectInComingCall();

        public void onAnswerInComingCall();

    }

}
