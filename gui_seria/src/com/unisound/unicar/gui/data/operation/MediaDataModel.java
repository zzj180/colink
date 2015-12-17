/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : MediaDataModel.java
 * @ProjectName : uniCarSolution
 * @PakageName : com.unisound.unicar.gui.data.operation
 * @Author : Alieen
 * @CreateDate : 2015-06-26
 */
package com.unisound.unicar.gui.data.operation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.unisound.unicar.gui.data.interfaces.IBaseListener;
import com.unisound.unicar.gui.model.MediaInfo;
import com.unisound.unicar.gui.preference.MediaPreference;
import com.unisound.unicar.gui.preference.PrivatePreference;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : database
 * @Comments : 描述
 * @Author : Alieen
 * @CreateDate : 2015-06-26
 * @ModifiedBy : Alieen
 * @ModifiedDate: 2015-06-26
 * @Modified: 2015-06-26: 实现基本功能
 */
@SuppressLint("HandlerLeak")
public class MediaDataModel {
    public static final String TAG = "MediaDataModel";
    private static final int mDataInit = 0;
    private static final int mDataUpdate = 1;
    private int mDataType = mDataInit;
    private Thread mWorkThread = null;
    private ArrayList<MediaInfo> mMediaInfoList = new ArrayList<MediaInfo>();
    private ArrayList<MediaInfo> mMediaInfoList_Syc = new ArrayList<MediaInfo>();
    private Uri mAudioUri;
    private File mMediaSavedFile = null;

    private final String mMediaCOPName = "datamedia.cop";

    private final static int mCompileCount = 100;
    private int mCount = 0;

    private IBaseListener mediaModelListener;

    public MediaDataModel(Context context) {
        mContext = context;
    }

    /**
     * @Description : init
     * @Author : Alieen
     * @CreateDate : 2015-06-26
     */
    public void init() {
        mMediaSavedFile = new File(Environment.getExternalStorageDirectory(), mMediaCOPName);
        MediaPreference.init();
        mAudioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        sycMedias();
        Logger.d(TAG, "media init end");
    }

    public void registerObserver() {
        startSystemOnChangeThread();
        registerContentObserver(mAudioUri);
    }

    public void update() {
        Logger.d(TAG, "update start");
        mDataType = mDataUpdate;
        sycMedias();
        Logger.d(TAG, "update end");
    }

    private void onDataDone() {
        Logger.d(TAG, "onDataDone");

        if (mDataType == mDataInit) {
            mediaModelListener.onDataDone(SessionPreference.SAVE_MEDIA_DATA_DONE);
        } else if (mDataType == mDataUpdate) {
            mediaModelListener.onDataDone(SessionPreference.SAVE_UPDATE_MEDIA_DATA_DONE);
        }


    }

    /**
     * @Description : setListener
     * @Author : Alieen
     * @CreateDate : 2015-06-26
     */
    public void setListener(IBaseListener l) {
        mediaModelListener = l;
    }

    /**
     * @Description : release
     * @Author : Alieen
     * @CreateDate : 2015-06-26
     */
    public void release() {
        Logger.d(TAG, "release");
        mAudioUri = null;
        mediaModelListener = null;

        if (mMediaInfoList_Syc != null) {
            mMediaInfoList_Syc.clear();
            mMediaInfoList_Syc = null;
        }

        synchronized (mMediaInfoList) {
            if (mMediaInfoList != null) {
                mMediaInfoList.clear();
                mMediaInfoList = null;
            }
        }

        mHandler.removeMessages(0);
        mHandler = null;
        unregisterContentObserver();
        mContext = null;
        mContentObserver = null;
    }


    /**
     * @Description : sysMedias
     * @Author : Alieen
     * @CreateDate : 2015-06-26
     */
    private void sycMedias() {
        Logger.d(TAG, "sycMedias");
        if (mWorkThread == null || !mWorkThread.isAlive()) {
            Runnable runnable = null;
            if ("SYSTEM".equals(MediaPreference.MEDIA_TYPE)) {
                runnable = new SyncRunnable();
            } else if ("CUSTOM".equals(MediaPreference.MEDIA_TYPE)) {
                runnable = new SyncCustomRunnable();
            } else {
                Logger.e(TAG, "Unsupported contact type!");
                return;
            }

            mWorkThread = new Thread(runnable);
            mWorkThread.setPriority(Thread.MIN_PRIORITY);
            mWorkThread.setName(TAG);
            mWorkThread.start();
        }
    }

    private class SyncRunnable implements Runnable {
        @Override
        public void run() {
            sysMedia();
        }
    }

    private class SyncCustomRunnable implements Runnable {
        @Override
        public void run() {
            Logger.d(TAG, "SyncCustomRunnable");
        }
    }

    private void clearCache() {
        Logger.d(TAG, "clearCache");
        if (mMediaInfoList_Syc != null) {
            mMediaInfoList_Syc.clear();
        }
    }

    private void copyCache() {
        Logger.d(TAG, "copyCache");
        if (mMediaInfoList != null) {
            mMediaInfoList.clear();
        }
        mMediaInfoList.addAll(mMediaInfoList_Syc);
        Logger.d(TAG, "mMediaInfoList:size=" + (mMediaInfoList == null ? 0 : mMediaInfoList.size()));

        saveMediasToFile(mMediaInfoList);
    }

    private void sysMedia() {
        clearCache();
        Cursor cursor =
                mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            String vuiRecordPath = PrivatePreference.getValue("recording_folder", "");
            Logger.d(TAG, "sysMedia:audio cursor count " + cursor.getCount());
            int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int pathIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int isMusicIndex = cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
            while (cursor.moveToNext() && mCount <= mCompileCount) {
                mCount++;
                String title = cursor.getString(titleIndex);
                String artist = cursor.getString(artistIndex);
                String album = cursor.getString(albumIndex);
                String duration = cursor.getString(durationIndex);
                String path = cursor.getString(pathIndex);
                String isMusic = cursor.getString(isMusicIndex);
                Logger.d(TAG, "vuiRecordPath : " + vuiRecordPath + ";path : " + path);
                if (path != null && path.length() > 0) {
                    if (vuiRecordPath != null && vuiRecordPath.length() > 0
                            && path.contains(vuiRecordPath)) {
                        break;
                    }
                    MediaInfo info = new MediaInfo(title, artist, album, duration, path);
                    if (isMusic != null && !TextUtils.isEmpty(isMusic)
                            && Integer.parseInt(isMusic) != 0) {
                        Logger.d(TAG, "isMusic : " + isMusic);
                        mMediaInfoList_Syc.add(info);
                    }
                }
            }
            mCount = 0;
            cursor.close();
            cursor = null;
        }
        copyCache();
    }

    private void saveMediasToFile(ArrayList<MediaInfo> mediaInfoList) {
        try {
            Logger.i(TAG, "--saveMediasToFile--" + mMediaSavedFile.getPath().toString() + " "
                    + Environment.getExternalStorageDirectory());

            if (!mMediaSavedFile.exists()) {
                mMediaSavedFile.createNewFile();
            } else {
                mMediaSavedFile.delete();
                mMediaSavedFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(mMediaSavedFile);
            for (int i = 0; i < mediaInfoList.size(); i++) {
                MediaInfo info = mediaInfoList.get(i);
                long id = info.getId();
                String title = info.getTitle();
                String artist = info.getArtist();
                String album = info.getAlbum();
                String duration = info.getDuration();
                String path = info.getPath();

                JSONObject jObject = new JSONObject();
                JsonTool.putJSONObjectData(jObject, "jsonType", "MEDIA");
                JsonTool.putJSONObjectData(jObject, "id", String.valueOf(id));
                JsonTool.putJSONObjectData(jObject, "title", title);
                JsonTool.putJSONObjectData(jObject, "artist", artist);
                JsonTool.putJSONObjectData(jObject, "album", album);
                JsonTool.putJSONObjectData(jObject, "duration", duration);
                JsonTool.putJSONObjectData(jObject, "path", path);
                Logger.d(TAG, "-medias-" + jObject.toString());
                fos.write((jObject.toString() + "\n").getBytes());
            }
            mediaInfoList.clear();
            fos.close();
            onDataDone();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void onChange(int onChangeCount) {
        Logger.d(TAG, "MediaDataModel onChange onChangeCount : " + onChangeCount);
        update();
    }

    public void setDataModelListener(IBaseListener l) {
        mediaModelListener = l;
    }

    protected Context mContext = null;
    public static final int FLAG_UPDATE = 0;
    private int mOnChangeCount = 0;
    private static Queue<Integer> mSyncTaskQueue = new LinkedList<Integer>();
    private Thread mSystemOnChangeThread = null;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Logger.d(TAG, "handleMessage mOnChangeCount :  " + mOnChangeCount);
            onChange(mOnChangeCount);
        };
    };

    private ContentObserver mContentObserver = new ContentObserver(mHandler) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            mOnChangeCount++;
            Logger.d(TAG, "onChange mOnchangeCount :  " + mOnChangeCount);
            synchronized (mSyncTaskQueue) {
                mSyncTaskQueue.offer(mOnChangeCount);
                Logger.d(TAG, "onChange mSyncTaskQueue size : " + mSyncTaskQueue.size());
                if (mSyncTaskQueue != null) {
                    mSyncTaskQueue.notifyAll();
                }
            }
        }

    };

    protected void registerContentObserver(Uri uri) {
        Logger.d(TAG, "registerContentObserver:uri " + uri);
        ContentResolver resolver = mContext.getContentResolver();
        resolver.registerContentObserver(uri, true, mContentObserver);
    }

    protected void unregisterContentObserver() {
        Logger.d(TAG, "unregisterContentObserver");
        ContentResolver resolver = mContext.getContentResolver();
        resolver.unregisterContentObserver(mContentObserver);
    }



    public static int popSyncTaskQueue() {
        synchronized (mSyncTaskQueue) {
            if (mSyncTaskQueue != null) {
                Iterator<Integer> iterable = mSyncTaskQueue.iterator();
                if (iterable.hasNext()) {
                    int taskQueue = iterable.next();
                    Logger.d(TAG, "taskQueue : " + taskQueue);
                    mSyncTaskQueue.clear();
                    return taskQueue;
                }
            }
        }
        return 0;
    }


    private class SyncSystemRunnable implements Runnable {
        @Override
        public void run() {
            Logger.d(TAG, "SyncSystemRunnable run");
            while (true) {
                synchronized (mSyncTaskQueue) {
                    if (mSyncTaskQueue != null && mSyncTaskQueue.isEmpty()) {
                        Logger.d(TAG, "SyncSystemRunnable mSyncTaskQueue.isEmpty()");
                        try {
                            mSyncTaskQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                int onChangeQueue = popSyncTaskQueue();
                Logger.d(TAG, "SyncSystemRunnable onChangeQueue :　" + onChangeQueue);
                if (onChangeQueue != 0) {
                    mHandler.sendEmptyMessageDelayed(FLAG_UPDATE, 500);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startSystemOnChangeThread() {
        Logger.d(TAG, "startOnChangeThread");
        if (mSystemOnChangeThread == null || !mSystemOnChangeThread.isAlive()) {
            mSystemOnChangeThread = new Thread(new SyncSystemRunnable());
            mSystemOnChangeThread.setPriority(Thread.MIN_PRIORITY);
            mSystemOnChangeThread.setName(TAG);
            mSystemOnChangeThread.start();
        }
    }
}
