package com.aispeech.aios.adapter.localScanService.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.localScanService.util.JsonUtil;
import com.aispeech.aios.client.AIOSMusicDataNode;

import org.json.JSONException;


/**
 * @desc 本地音乐扫描服务
 * @auth AISPEECH
 * @date 2016-02-19
 * @copyright aispeech.com
 */
public class LocalMusicScanService extends Service implements AIOSMusicDataNode.IMuiscSyncListener {

    private static final String TAG = "LocalMusicScanService";
    private LMSReceiver mMusicReceiver = null;
    private static final int MESSAGE_START_SCAN = 0x001;//开启扫描

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_START_SCAN) {

                try {

                    ThreadPoolManager.getLongPool().execute(new LocalMusicScanner(LocalMusicScanService.this, JsonUtil.getFolders(msg.obj.toString())));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        AILog.i(TAG, "LocalMusicScanService onCreate() !!! ");
        AIOSMusicDataNode.getInstance().setMuiscSyncListener(this);//注册本地音乐是否同步成功监听器
        registerMusicReceiver();//广播接收器
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterMusicReceiver();//解绑广播接收器
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 动态注册音乐广播
     */
    private void registerMusicReceiver() {

        if (mMusicReceiver == null) {
            mMusicReceiver = new LMSReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("aios.intent.action.LOCAL_MUSIC_SCAN");
            this.registerReceiver(mMusicReceiver, filter);

        }
    }

    /**
     * 解绑音乐广播
     */
    private void unregisterMusicReceiver() {
        if (mMusicReceiver != null) {
            this.unregisterReceiver(mMusicReceiver);
            mMusicReceiver = null;
        }
    }

    @Override
    public void syncSuccess() {
        AILog.i(TAG, " Local Music Sync Success");
        Intent i = new Intent();
        i.setAction("aios.intent.action.DATA_SYNC_STATE");//监听AIOS data.sync.state 同步完成后发出广播 action
        i.putExtra("type", "songs");
        i.putExtra("state", "true");
        this.sendBroadcast(i);
    }

    @Override
    public void syncFailed() {
        AILog.i(TAG, " Local Music Sync Failed");
        Intent i = new Intent();
        i.setAction("aios.intent.action.DATA_SYNC_STATE");//监听AIOS data.sync.state 同步完成后发出广播 action
        i.putExtra("type", "songs");
        i.putExtra("state", "false");
        this.sendBroadcast(i);
    }

    private class LMSReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("aios.intent.action.LOCAL_MUSIC_SCAN")) {//开启本地扫描广播
                AILog.d(TAG, "收到 aios.intent.action.LOCAL_MUSIC_SCAN");
                if (intent.getExtras().containsKey("aios.intent.extra.TEXT")) {
                    Message msg = Message.obtain();
                    msg.what = MESSAGE_START_SCAN;
                    msg.obj = intent.getStringExtra("aios.intent.extra.TEXT");
                    mHandler.sendMessage(msg);
                }
            }
        }

    }


}
