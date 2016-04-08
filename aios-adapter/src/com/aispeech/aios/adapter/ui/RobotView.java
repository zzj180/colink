
package com.aispeech.aios.adapter.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aispeech.aios.adapter.R;

/**
 * @desc 显示语音识别状态的MicphoneView。继承自RelativeLayout。
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class RobotView extends RelativeLayout {

    public static final String TAG = "AIOS-RobotView";
    private ImageView mRobotIV;
    private AnimationDrawable mListeningDrawable;
    private AnimationDrawable mRecognitionDrawable;

    private boolean mIsStared = false;

    public RobotView(Context context) {
        this(context, null);
    }

    public RobotView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RobotView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        mRobotIV = (ImageView) findViewById(R.id.i_assist);
        super.onFinishInflate();
    }

    /**
     * 启动录音机动画，倾听状态
     */
    public void startListening() {
        if (mIsStared) {
            return;
        }
        mIsStared = !mIsStared;
        mRobotIV.setImageResource(R.drawable.anim_mic_start);
        mListeningDrawable = (AnimationDrawable) mRobotIV.getDrawable();
        mListeningDrawable.start();
    }

    /**
     * 停止倾听动画效果
     */
    public void stopListening() {
        if (!mIsStared) {
            return;
        }
        mIsStared = !mIsStared;
        mListeningDrawable.stop();
    }

    /**
     * 显示识别动画
     */
    public void startRecognition() {
        mRobotIV.setImageResource(R.drawable.anim_mic_stop);
        mRecognitionDrawable = (AnimationDrawable) mRobotIV.getDrawable();
        mRecognitionDrawable.start();
    }

    /**
     * 停止识别动画
     */
    public void stopRecognition() {
        if (null != mRecognitionDrawable) {
            mRecognitionDrawable.stop();
        }
        mRobotIV.clearAnimation();
        mRobotIV.setImageResource(R.drawable.image_mic);
    }
}
