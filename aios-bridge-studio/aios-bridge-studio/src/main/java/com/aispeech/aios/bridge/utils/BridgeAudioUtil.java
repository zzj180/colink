package com.aispeech.aios.bridge.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;

import com.aispeech.aios.bridge.BridgeApplication;

/**
 * 音频管理类，可根据具体情况自行改写
 */
public class BridgeAudioUtil {
    private static final String TAG = "Bridge - BridgeAudioUtil";
    private static AudioManager mAudioManager;
    private static int muteCnt = 0;
    private static Handler mHandler = new Handler(BridgeApplication.getContext().getMainLooper());

    public static void setStreamMute(final Context context, final boolean isMute) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (context == null) {
                    return;
                }
                mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (mAudioManager != null) {
                    if (isMute) {
                        muteCnt++;
                        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                        mAudioManager.setStreamMute(AudioManager.STREAM_DTMF, true);
                    } else {

                        if (muteCnt == 0) {
                            return;
                        }
                        for (int i = 0; i < getMuteCnt(); i++) {
                            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                            mAudioManager.setStreamMute(AudioManager.STREAM_DTMF, false);
                        }
                        muteCnt = 0;
                    }
                }
            }
        });
    }

    public static int getMuteCnt() {
        return muteCnt;
    }
}
