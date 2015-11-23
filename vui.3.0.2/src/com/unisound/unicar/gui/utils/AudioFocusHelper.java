/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : AudioFocusHelper.java
 * @ProjectName : uniCarPlatform
 * @PakageName : com.unisound.unicar.framework.audio
 * @Author : Alieen
 * @CreateDate : 2015-07-18
 */
package com.unisound.unicar.gui.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Alieen
 * @CreateDate : 2015-07-18
 * @ModifiedBy : Alieen
 * @ModifiedDate: 2015-07-18
 * @Modified: 2015-07-18: 实现基本功能
 */
public class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
    public static final String TAG = "AudioFocusHelper";
    private AudioManager mAudioManager;

    public AudioFocusHelper(Context context) {
        // Get AudioManager
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void requestAudioFocus() {

        if (mAudioManager != null) {
            Logger.d(TAG, "requestAudioFocus");
            int result = mAudioManager.requestAudioFocus(this, // Hint: the music stream.
                    AudioManager.STREAM_MUSIC, // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Logger.e(TAG, "request audio focus fail:" + result);
            }
        }

    }

    public void abandonAudioFocus() {

        if (mAudioManager != null) {
            Logger.d(TAG, "abandonAudioFocus");
            int result = mAudioManager.abandonAudioFocus(this);
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Logger.e(TAG, "abandon audio focus fail:" + result);
            }
        }

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Logger.d(TAG, "onAudioFocusChange:focusChange " + focusChange);
    }
}
