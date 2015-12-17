package com.unisound.unicar.gui.utils;



import cn.yunzhisheng.vui.assistant.WindowService;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
  
public class SoundPlayer {

    private static SoundPlayer instance = new SoundPlayer();
    private Context context;
    private SoundPlayer() {
    }

    public static SoundPlayer getInstance() {
        return instance;
    }

    private MediaPlayer mPlayer;

    public synchronized void start(Context context ,int raw) {

        if (mPlayer != null) {
            stop();
        }
        this.context = context;
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        mPlayer = MediaPlayer.create(context, raw);
        mPlayer.setOnCompletionListener(mOnCompletionListener);
        mPlayer.start();
    }

    public MediaPlayer getmPlayer() {
    	if(mPlayer==null){
    		mPlayer = new MediaPlayer();
    	}
		return mPlayer;
	}


	private void stop() {
        if (mPlayer != null) {
           /* try {
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            mPlayer.release();
        }
    }

    private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer arg0) {
            // TODO Auto-generated method stub
            if (mPlayer != null) {
                stop();
            }
        }
    };
    private AudioManager getAudioManager() {
		return (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
	}
}
