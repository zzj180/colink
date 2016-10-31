package com.aispeech.aios.bridge.listener;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;


import com.aispeech.aios.bridge.BridgeApplication;
import com.aispeech.aios.bridge.utils.BridgeAudioUtil;
import com.aispeech.aios.bridge.utils.Logger;
import com.aispeech.aios.sdk.SDKBuild;
import com.aispeech.aios.sdk.listener.AIOSAudioListener;

/**
 * 音频管理监听器实现类
 */
public class BridgeAudioListener implements AIOSAudioListener {

    /**
     * 执行静音操作，静音通道等参数请根据实际情况设置。AIOS需要静音时回调该方法
     */
    @Override
    public void onMute() {

     //   BridgeAudioUtil.setStreamMute(BridgeApplication.getContext(),true);

        BridgeApplication.uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Logger.d("onMute");
                AudioManager audioManager = (AudioManager) BridgeApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                        boolean show = Settings.System.getInt(BridgeApplication.getContext().getContentResolver(),"tts_show",0)==1;
                        if(show) {
                            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

                        }
                }
            }
        },0);
    }

    /**
     * 执行取消静音操作，静音通道等参数请根据实际情况设置。AIOS需要取消静音时回调该方法。
     */
    @Override
    public void onUnMute() {


       BridgeApplication.uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.d("onUnMute");
                AudioManager audioManager = (AudioManager) BridgeApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                   if(Build.VERSION.SDK_INT < 23) {
                           audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

                   }
                }
            }
        },0);
    }
}
