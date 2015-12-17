/**
 * 
 * @FileName : SmsContentView.java
 * @ProjectName :
 * @PakageName : com.unisound.unicar.gui.view
 * @Author :
 * @CreateDate :
 */
package com.unisound.unicar.gui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.TextEditerDialog.ITextEditorListener;

/**
 * 
 * @author xiaodong
 * 
 */
public class SmsContentView extends FrameLayout implements ISessionView {

    public static final String TAG = "SmsContentView";

    private Context mContext;
    // private ImageView mImageViewAvatar;
    private TextView mTvTitle;
    private TextView mTextViewName, mTextViewPhoneNumber;
    // private TextView mTextViewMessageContentLength;
    private EditText mEditTextSmsInput;

    private FrameLayout mFlSend;
    private TextView mTvSendSMS;
    private RelativeLayout mBtnCancel;
    private Button mBtnReput;

    // private Button mBtnClearSmsContent;
    // private String mName, mPhoneNumber;
    private ISmsContentViewListener mListener;
    private TextEditerDialog mTextEditerDialog;

    private CountDownTimer mCountDownTimer;
    private TextView mTvCallOutCancelTime;
    private ProgressBar mProgressBarWaiting;

    /** isCountDownTimerStart */
    private boolean isCountDownTimerStart = false; // XD added 20150812

    private OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Logger.d(TAG, "--onTouch--");

            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (mTextEditerDialog == null) {
                    mTextEditerDialog =
                            new TextEditerDialog(getContext(), R.style.TextEditorDialog);
                    mTextEditerDialog.setTextEditorListener(new ITextEditorListener() {

                        @Override
                        public void onResult(String text) {
                            Logger.d(TAG, "--onResult text : " + text);
                            setMessage(text);
                            if (mListener != null) {
                                Logger.d(TAG, "--mListener != null--");
                                mListener.onEndEdit(text);
                            }
                        }

                        @Override
                        public void onCancel() {
                            Logger.d(TAG, "--onCancel--");
                            if (mListener != null) {
                                Logger.d(TAG, "--mListener != null--");
                                mListener.onEndEdit(getMessage());
                            }
                        }
                    });
                }
                Logger.d(TAG, "--mTextEditerDialog.setText(getMessage())--");

                mTextEditerDialog.setText(getMessage());

                if (mListener != null) {
                    mListener.onBeginEdit();
                }
            }

            return false;
        }
    };

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_send_sms_cancel:
                    Logger.d(TAG, "!--->mOnClickListener---onCancel");
                    if (mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                    }
                    if (mListener != null) {
                        mListener.onCancel();
                    }
                    break;
                case R.id.fl_send_sms_ok:
                    Logger.d(TAG, "!--->mOnClickListener---onOk() click---");
                    if (mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                    }
                    if (mListener != null) {
                        mListener.onOk();
                    }
                    break;
                case R.id.btnReput:
                    Logger.d(TAG, "!--->mOnClickListener---btnReput---");
                    if (mListener != null) {
                        mListener.onClearMessage();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            updateButtonsStatus();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };


    public SmsContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sms_content_view, this, true);
        findViews();
        setListener();
    }

    public SmsContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmsContentView(Context context) {
        this(context, null);
        mContext = context;
    }

    private void findViews() {
        // mImageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
        mTvTitle = (TextView) findViewById(R.id.tv_title_send_sms_name);
        mTextViewName = (TextView) findViewById(R.id.textViewContactName);
        mTextViewPhoneNumber = (TextView) findViewById(R.id.textViewPhoneNumber);
        mEditTextSmsInput = (EditText) findViewById(R.id.editTextSmsInput);

        // mTextViewMessageContentLength = (TextView) findViewById(R.id.textViewSmsContentLength);
        // mBtnClearSmsContent = (Button) findViewById(R.id.btnClearSmsContent);

        mFlSend = (FrameLayout) findViewById(R.id.fl_send_sms_ok);
        mTvSendSMS = (TextView) findViewById(R.id.tv_send_sms_ok);
        mBtnCancel = (RelativeLayout) findViewById(R.id.rl_send_sms_cancel);
        mBtnReput = (Button) findViewById(R.id.btnReput);

        mProgressBarWaiting = (ProgressBar) findViewById(R.id.progressBarWaiting);
        mTvCallOutCancelTime = (TextView) findViewById(R.id.tv_send_sms_cancel_time);

        updateButtonsStatus();
    }

    /**
	 * 
	 */
    private void updateButtonsStatus() {
        if (TextUtils.isEmpty(getMessage())) {
            setSendBtnDisable(true);
            setReputBtnDisable(true);
        } else {
            setSendBtnDisable(false);
            setReputBtnDisable(false);
        }
    }

    public void setSendBtnDisable(boolean isDisable) {
        if (isDisable) {
            mTvSendSMS.setTextColor(getResources().getColor(R.color.grey));
            mFlSend.setEnabled(false);
        } else {
            mTvSendSMS.setTextColor(getResources().getColor(R.color.grey_white));
            mFlSend.setEnabled(true);
        }
    }

    public void setReputBtnDisable(boolean isDisable) {
        if (isDisable) {
            mBtnReput.setTextColor(getResources().getColor(R.color.grey));
            mBtnReput.setEnabled(false);
        } else {
            mBtnReput.setTextColor(getResources().getColor(R.color.grey_white));
            mBtnReput.setEnabled(true);
        }
    }

    private void setListener() {
        mFlSend.setOnClickListener(mOnClickListener);
        mBtnCancel.setOnClickListener(mOnClickListener);
        mBtnReput.setOnClickListener(mOnClickListener);

        mEditTextSmsInput.setOnClickListener(mOnClickListener);
        mEditTextSmsInput.setOnTouchListener(mOnTouchListener);
        mEditTextSmsInput.addTextChangedListener(textWatcher);
        // mEditTextSmsInput.setOnFocusChangeListener(mFocusChangeListener);
    }

    public void initRecipient(Drawable drawable, String name, String phoneNumber) {
        Logger.d(TAG, "!--->initRecipient()-----name = " + name + "; phoneNumber = " + phoneNumber);
        // if (drawable != null) {
        // mImageViewAvatar.setImageDrawable(drawable);
        // } else {
        // mImageViewAvatar.setImageResource(R.drawable.ic_contact_avatar_new);
        // }
        mTvTitle.setText(mContext.getString(R.string.sms_to, name));
        mTextViewName.setText(name);
        mTextViewPhoneNumber.setText(phoneNumber);
    }

    public void clearMessage() {
        mEditTextSmsInput.setText("");
    }

    public void setMessage(String msg) {
        mEditTextSmsInput.setText(msg);
        int length = mEditTextSmsInput.getText().length();
        mEditTextSmsInput.setSelection(length);

        // mTextViewMessageContentLength.setText(String.valueOf(length));
    }

    public String getMessage() {
        return mEditTextSmsInput.getText().toString();
    }

    public ISmsContentViewListener getListener() {
        return mListener;
    }

    public void setListener(ISmsContentViewListener l) {
        this.mListener = l;
    }

    public boolean becomeFirstRespond() {
        mEditTextSmsInput.setFocusable(true);
        mEditTextSmsInput.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
        mEditTextSmsInput.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
        boolean tag = mEditTextSmsInput.requestFocus();
        return tag;

    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    @Override
    public void release() {
        if (mTextEditerDialog != null) {
            mTextEditerDialog.cancel();
            mTextEditerDialog = null;
        }
        mOnClickListener = null;
    }

    /**
     * 
     * @param countDownMillis
     */
    public void startCountDownTimer(final long countDownMillis) {
        if (!GUIConfig.isShowSmsConfirmTimer) {
            return;
        }
        if (isCountDownTimerStart) {
            return;
        }
        Logger.d(TAG, "!--->startCountDownTimer----");
        mProgressBarWaiting.setVisibility(View.VISIBLE);
        mTvCallOutCancelTime.setText(((countDownMillis + 1) / 1000) + "S");

        mCountDownTimer = new CountDownTimer(countDownMillis, 100) {

            public void onTick(long millisUntilFinished) {

                int progress =
                        (int) ((countDownMillis - millisUntilFinished) / (float) countDownMillis * mProgressBarWaiting
                                .getMax());
                // Logger.d(TAG, "!--->startCountDownTimer()--progress = "+progress);
                // mProgressBarWaiting.setProgress(mProgressBarWaiting.getMax() - progress);
                mProgressBarWaiting.setProgress(progress);

                mTvCallOutCancelTime.setText(((millisUntilFinished + 1) / 1000) + "S");
            }

            public void onFinish() {
                mProgressBarWaiting.setProgress(0);
                if (mListener != null) {
                    Logger.d(TAG, "!--->mCountDownTimer---onFinish()");
                    mListener.onTimeUp(); // xd modify 20150704
                }
            }
        }.start();
        isCountDownTimerStart = true;
    }

    /**
	 * 
	 */
    public void cancelCountDownTimer() {
        if (!GUIConfig.isShowSmsConfirmTimer) {
            return;
        }
        Logger.d(TAG, "!--->---cancelCountDownTimer()-----");
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        isCountDownTimerStart = false;
        /* < XD 20150716 added Begin */
        if (null != mProgressBarWaiting) {
            mProgressBarWaiting.setProgress(0);
        }

        if (null != mTvCallOutCancelTime) {
            mTvCallOutCancelTime.setText("");
        }
        /* XD 20150716 added End > */
    }

    public static interface ISmsContentViewListener {
        public void onBeginEdit();

        public void onEndEdit(String msg);

        public void onCancel();

        public void onOk();

        public void onTimeUp();

        public void onClearMessage();
    }

}
