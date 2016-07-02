package com.aispeech.aios.adapter.util;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.aispeech.ailog.AILog;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @desc 音频播放管理器
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class AudioPlayManager implements OnCompletionListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener {

    private static final String TAG = "WcAudioPlayManager";

    private static AudioPlayManager mWcAudioPlayManager;

    private MediaPlayer mPlayer;

    private ExecutorService worker;

    public interface PlayListener {
        void onAudioStart();

        void onComplete();

        void onError();
    }

    private ConcurrentLinkedQueue<PlayListener> mPlayListenerLists;

    enum State {
        PLAYING, PAUSE, STOPED,ERROR
    }

    private State mState = State.STOPED;

    /**
     * 构造方法
     */
    public AudioPlayManager() {
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayListenerLists = new ConcurrentLinkedQueue<PlayListener>();

        worker = Executors.newSingleThreadExecutor();
    }

    public static synchronized AudioPlayManager getInstance() {
        if (mWcAudioPlayManager == null) {
            mWcAudioPlayManager = new AudioPlayManager();
        }
        return mWcAudioPlayManager;
    }

    /**
     * 打开并播放一段音频
     * @param filePath 音频路径
     */
    public void openAndPlay(final String filePath) {
        //Don't do time-consuming operation in ui thread
        worker.execute(new Runnable() {
            @Override
            public void run() {
                File file = new File(filePath);
                if (!filePath.startsWith("http") && !file.exists()) {
                    onStateChange(State.STOPED);
                    dispatchCompleteListener();
                }
                try {
                    mPlayer.reset();
                    mPlayer.setDataSource(filePath);
                    mPlayer.prepareAsync();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    onStateChange(State.STOPED);
                } catch (Exception e) {
                    e.printStackTrace();
                    onStateChange(State.ERROR);
                    dispatchErrorListenner();
                }
            }
        });
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mState == State.PLAYING) {
            worker.execute(new Runnable() {
                @Override
                public void run() {
                    onStateChange(State.PAUSE);
                    mPlayer.pause();
                }
            });
        }
    }

    /**
     * 继续播放
     */
    public void resume() {
        if (mState == State.PAUSE) {
            worker.execute(new Runnable() {
                @Override
                public void run() {
                    onStateChange(State.PLAYING);
                    mPlayer.start();
                }
            });
        }

    }

    /**
     * 停止播放
     */
    public void stop() {
        try {
            if (mState !=null) {
                worker.execute(new Runnable() {
                    @Override
                    public void run() {
                        onStateChange(State.STOPED);
                        mPlayer.stop();
                        mPlayer.reset();
                    }
                });
            }
        } catch (IllegalStateException e) {
            onStateChange(State.STOPED);
            e.printStackTrace();
        }catch (Exception e){
            onStateChange(State.STOPED);
            e.printStackTrace();
        }
    }

    private void onStateChange(State state) {
        this.mState = state;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onStateChange(State.STOPED);
        dispatchCompleteListener();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if(mPlayer!=null){
            mPlayer.start();
            onStateChange(State.PLAYING);
            dispatchStartListener();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        if(i == 1){
            AILog.e(TAG , "捕获播放器异常");
            onStateChange(State.ERROR);
            dispatchErrorListenner();
        }
        return true;
    }

    /**
     * 注册播放状态监听器
     * @param playListener 播放状态监听器
     */
    public void registerListener(PlayListener playListener) {
        if (playListener != null) {
            if (!mPlayListenerLists.contains(playListener)) {
                mPlayListenerLists.add(playListener);
            }
        }
    }

    /**
     * 注册播放状态监听器
     * @param playListener 播放状态监听器
     */
    public void unRegisterListener(PlayListener playListener) {
        if (playListener != null) {
            if (mPlayListenerLists.contains(playListener)) {
                mPlayListenerLists.remove(playListener);
            }
        }
    }


    private void dispatchErrorListenner(){
        AILog.e(TAG , "dispatchErrorListenner excute!");
        Iterator<PlayListener> iter = mPlayListenerLists.iterator();
        if(iter.hasNext()){
            PlayListener listener = iter.next();
            if (listener != null) {
                listener.onError();
            }
        }
    }

    private void dispatchCompleteListener() {
        Iterator<PlayListener> iter = mPlayListenerLists.iterator();
        while (iter.hasNext()) {
            PlayListener listener = iter.next();
            if (listener != null) {
                listener.onComplete();
            }
        }
    }

    private void dispatchStartListener() {
        Iterator<PlayListener> iter = mPlayListenerLists.iterator();
        while (iter.hasNext()) {
            PlayListener listener = iter.next();
            if (listener != null) {
                listener.onAudioStart();
            }
        }
    }

    /**
     * 释放音频资源
     *
     * deprecated
    public synchronized void release() {
        mPlayListenerLists.clear();
        mWcAudioPlayManager = null;
    }
    */

}
