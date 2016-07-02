/**
 * Copyright (c) 2012-2012 Mango(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : SessionContainer.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-11-10
 */
package com.unisound.unicar.gui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.Logger;


public class SessionContainer extends ScrollView {
    public static final String TAG = "SessionContainer";
    private final static int SESSION_TYPE_INVALID = -1;
    private final static int SESSION_TYPE_QUESTION = 1;
    private final static int SESSION_TYPE_ANSWER = 2;
    private int mLastestSessionType = SESSION_TYPE_INVALID;
    private String mLastestSession;
    private LinearLayout mSessionContainer;
    private ImageView mImageViewLastTTS;
    private LayoutInflater mLayoutInflater = null;
    private boolean mRequestFullScroll = true;
    private boolean mScrollable = true;

    public static final int UNKNOWN = 0;
    public static final int PLAYING = 1;
    public static final int BUFFERING = 2;
    public static final int STOPPED = 3;
    public static final int UNENABLED = 4;

    // private static ArrayList<Integer> mViewShowTimeList = new
    // ArrayList<Integer>();
    //
    // public static void addViewNow(int view_hashCode) {
    // mViewShowTimeList.add(view_hashCode);
    // }

    // private Context mContext; //XD delete 20150727
    // private UserPreference mPreference; //XD delete 20150727
    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

        }
    };

    public SessionContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Logger.d(TAG, "!--->----SessionContainer()-------");
        mLayoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSessionContainer = new LinearLayout(context);
        mSessionContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mSessionContainer.setOrientation(LinearLayout.VERTICAL);
        mSessionContainer.setGravity(Gravity.CENTER_VERTICAL);// XD added 20150921

        // Resources res = getResources();
        // int left = res.getDimensionPixelSize(R.dimen.session_padding_left);
        // int right = res.getDimensionPixelSize(R.dimen.session_padding_right);
        // int top = res.getDimensionPixelSize(R.dimen.session_padding_top);
        // int bottom = res.getDimensionPixelSize(R.dimen.session_padding_bottom);
        // mSessionContainer.setPadding(left, top, right, bottom);

        addView(mSessionContainer);

        // mContext = context;
        // mPreference = new UserPreference(context);
        //
        // mViewShowTimeList.clear();
    }

    public SessionContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SessionContainer(Context context) {
        this(context, null);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mRequestFullScroll) {
            mRequestFullScroll = false;
            fullScroll(ScrollView.FOCUS_UP);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent ev) {
        Logger.d(TAG, "!--->onTouchEvent---action = " + ev.getAction() + "; mScrollable = "
                + mScrollable);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScrollable) {
                    return super.onTouchEvent(ev);
                }
                return false;
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Logger.d(TAG, "!--->onInterceptTouchEvent----action=" + ev.getAction()
                + "; ; mScrollable = " + mScrollable);
        if (mScrollable) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }

    public LinearLayout getContentView() {
        return mSessionContainer;
    }

    public void clearTemporaryViews() {
        Logger.d(TAG, "clearTemporaryViews");
        // mViewShowTimeList.clear();
        for (int i = 0; i < mSessionContainer.getChildCount(); i++) {
            View child = mSessionContainer.getChildAt(i);
            if (child instanceof ISessionView && ((ISessionView) child).isTemporary()) {
                mSessionContainer.removeViewAt(i);
            }
        }
    }

    public void removeAllSessionViews() {
        Logger.d(TAG, "!--->removeAllSessionViews-----");
        mLastestSession = "";
        // mViewShowTimeList.clear();
        mSessionContainer.removeAllViews();
    }

    public void removeSessionView(View view) {
        // mViewShowTimeList.clear();
        mSessionContainer.removeView(view);
    }

    public void addSessionView(View view) {
        addSessionView(view, true);
    }

    public void addSessionView(View view, boolean fullScroll) {
        Logger.d(TAG, "!--->addSessionView---fullScroll = " + fullScroll);
        if (view == null) {
            Logger.w(TAG, "addSessionView: view null,return!");
            return;
        }
        // if (view instanceof ISessionView) {
        // Integer hashCode = view.hashCode();
        // if (mViewShowTimeList.contains(hashCode)) {
        // mViewShowTimeList.remove((Integer) hashCode);
        // } else {
        // return;
        // }
        // }
        setScrollingEnabled(true);
        mRequestFullScroll = fullScroll;
        mSessionContainer.addView(view);

    }

    /**
     * xd 20150716 added for SessionListView
     * 
     * @param view
     */
    public void addSessionListView(View view) {
        if (view == null) {
            Logger.w(TAG, "addSessionView: view null,return!");
            return;
        }
        // 禁止滚动
        setScrollingEnabled(false);

        mRequestFullScroll = false;
        mSessionContainer.addView(view);
    }


    public void addSessionViewWithoutScrolling(View view) {
        if (view == null) {
            Logger.w(TAG, "addSessionViewWithoutScrolling: view null,return!");
            return;
        }
        // addSessionView(view, true); //xd remove

        // 禁止滚动
        setScrollingEnabled(false);

        // 将视图宽设成和container一致，视图高设成与弹出框一致
        int height = getHeight();
        int width = mSessionContainer.getWidth();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        mSessionContainer.addView(view, params);
    }

    public void needFullScrollNextTime() {
        mRequestFullScroll = true;
    }

    public void addQustionView(String question) {
        if (TextUtils.isEmpty(question)
                || (mLastestSessionType == SESSION_TYPE_QUESTION && question
                        .equals(mLastestSession))) {
            return;
        }
        mLastestSessionType = SESSION_TYPE_QUESTION;
        mLastestSession = question;

        TextView tv2 =
                (TextView) mLayoutInflater.inflate(R.layout.session_question_view,
                        mSessionContainer, false);
        tv2.setText(question);

        if (Logger.DEBUG) {
            tv2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String text = ((TextView) v).getText().toString();
                    // UMengPreference.recordErrorText(getContext(), text);
                    Toast.makeText(getContext(),
                            getResources().getString(R.string.feedback_error_) + text,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        addSessionView(tv2, true);
    }

    public void addAnswerView(String answer) {
        Logger.d(TAG, "!--->addAnswerView answer=" + answer);
        if (TextUtils.isEmpty(answer)
                || (mLastestSessionType == SESSION_TYPE_ANSWER && answer.equals(mLastestSession))) {
            return;
        }
        mLastestSessionType = SESSION_TYPE_ANSWER;
        mLastestSession = answer;
        hideLastTTSView();
        View v = mLayoutInflater.inflate(R.layout.session_answer_view, mSessionContainer, false);
        mImageViewLastTTS = (ImageView) v.findViewById(R.id.imageViewTTSStatus);
        mImageViewLastTTS.setOnClickListener(mOnClickListener);
        TextView tv1 = (TextView) v.findViewById(R.id.textViewSessionAnswer);
        tv1.setText(answer);
        addSessionView(v);
        // if (mPreference.getBoolean(UserPreference.KEY_ENABLE_TTS, true)) {
        // } else {
        // // mImageViewLastTTS.setImageResource(R.drawable.ic_talk_off);
        // }
    }


    public void addAnswerViewEx(String text, String imgURL, String imgAlt, String url, String urlAlt) {
        Logger.d(TAG, "addAnswerViewEx text=" + text);
        if (TextUtils.isEmpty(text) && TextUtils.isEmpty(imgURL) && TextUtils.isEmpty(url)) {
            return;
        }

        if (!TextUtils.isEmpty(text) && TextUtils.isEmpty(imgURL) && TextUtils.isEmpty(url)) {
            addAnswerView(text);
            return;
        }

        url = TextUtils.isEmpty(url) ? null : url.replaceAll(" ", "%20");
        if (!TextUtils.isEmpty(text)
                && (mLastestSessionType == SESSION_TYPE_ANSWER && text.equals(mLastestSession))) {
            return;
        }
        mLastestSessionType = SESSION_TYPE_ANSWER;
        mLastestSession = text;
        hideLastTTSView();

        GeneralAnswerView v = new GeneralAnswerView(getContext());
        mImageViewLastTTS = (ImageView) v.findViewById(R.id.imageViewTTSStatus);
        mImageViewLastTTS.setOnClickListener(mOnClickListener);
        v.setGeneralData(text, imgURL, imgAlt, url, urlAlt);
        addSessionView(v);

        // if (mPreference.getBoolean(UserPreference.KEY_ENABLE_TTS, true)) {
        // } else {
        // // mImageViewLastTTS.setImageResource(R.drawable.ic_talk_off);
        // }
    }

    private void hideLastTTSView() {
        if (mImageViewLastTTS != null) {
            mImageViewLastTTS.setOnClickListener(null);
            mImageViewLastTTS.setVisibility(View.GONE);
        }
    }
}
