package com.unisound.unicar.gui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Microphone Control Base View
 * 
 * @author xiaodong
 * @date 20150827
 */
public class MicrophoneControlBaseView extends RelativeLayout {

    protected static final int TIME_MINISECONDS_MIC_FLICKER = 200; // 0.2s

    protected static final int TIME_MINISECONDS_DURATION_RECOGNIZE = 1000; // 1s

    public MicrophoneControlBaseView(Context context) {
        super(context);
    }

    public MicrophoneControlBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public MicrophoneControlBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onException() {}

    public void onPrepare() {};

    public void onIdle(boolean resetMicrophoneText) {};

    public void onRecording() {}

    public void onProcess() {}

    public void onDestroy() {}

    public void setAnswerText(String text) {}

    public void onStartRecordingFakeAnimation() {}

    public void showWakeupStatusOnMicView(boolean isWakeupOpen, String wakeupWord) {};

    // public void showChangeLocationView(boolean isShow, String location) {}// add tyz 20151013

}
