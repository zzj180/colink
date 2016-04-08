package com.aispeech.aios.adapter.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.aispeech.ailog.AILog;
import com.aispeech.aimusic.AIMusic;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.node.ChatNode;
import com.aispeech.aios.adapter.node.CommonPoiNode;
import com.aispeech.aios.adapter.node.CustomizeNode;
import com.aispeech.aios.adapter.node.HomeNode;
import com.aispeech.aios.adapter.node.LocationNode;
import com.aispeech.aios.adapter.node.MusicNode;
import com.aispeech.aios.adapter.node.NavigationNode;
import com.aispeech.aios.adapter.node.NearbyNode;
import com.aispeech.aios.adapter.node.PhoneNode;
import com.aispeech.aios.adapter.node.RadioNode;
import com.aispeech.aios.adapter.node.StockNode;
import com.aispeech.aios.adapter.node.SystemNode;
import com.aispeech.aios.adapter.node.TTSNode;
import com.aispeech.aios.adapter.node.VehicleRestrictionNode;
import com.aispeech.aios.adapter.node.WeatherNode;
import com.aispeech.aios.adapter.ui.MyWindowManager;
import com.aispeech.aios.adapter.util.PreferenceHelper;


/**
 * @desc 悬浮窗相关服务
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class FloatWindowService extends Service {

    private String TAG = "AIOS-FloatWindowService";
    Handler mWorkerHandler;
    private HandlerThread mWorker;

    private static FloatWindowService service;

    private HandlerThread mHandlerThread = new HandlerThread("worker-thread");

    @Override
    public void onCreate() {
        super.onCreate();
        service = this;
        AILog.i(TAG, "FloatWindowService on create");
        PendingIntent pendingintent = PendingIntent.getService(this, 0, new Intent(this, com.aispeech.aios.adapter.service.FloatWindowService.class), 0);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon_app_launcher)
                .setContentTitle("AIOS-Adapter")
                .setContentText("AIOS-Adapter 正在运行")
                .setContentIntent(pendingintent)
                .build();
        startForeground(R.string.app_name, notification);


        initNodes();
        mHandlerThread.start();
    }

    public static FloatWindowService getRunningService() {
        return service;
    }

    /**
     * 初始化Node
     */
    private void initNodes() {
        AILog.d(TAG, "initNodes - " + this);
        if (mWorker != null) {
            return;
        }

        mWorker = new HandlerThread("worker-thread");
        mWorker.start();
        mWorkerHandler = new Handler(mWorker.getLooper());
        mWorkerHandler.post(new Runnable() {

            @Override
            public void run() {
                PhoneNode.getInstance().start();
                PhoneNode.getInstance().registerBtReceiver();
                
                HomeNode.getInstance().start();
                HomeNode.getInstance().register();

                CustomizeNode.getInstance().start();
                CustomizeNode.getInstance().register();

                TTSNode.getInstance().start();

                NavigationNode.getInstance().start();

                NearbyNode.getInstance().start();

                WeatherNode.getInstance().start();

                MusicNode.getInstance().start();

                LocationNode.getInstance().start();

                SystemNode.getInstance().start();

                VehicleRestrictionNode.getInstance().start();

                StockNode.getInstance().start();

                RadioNode.getInstance().start();

//                FmNode.getInstance().start();

                ChatNode.getInstance().start();

                CommonPoiNode.getInstance().start();
            }
        });
        Intent intent = new Intent();
        intent.setAction("com.aispeech.launcher.BRING_TO_FRONT");
        sendBroadcast(intent);

        AILog.e(TAG, "Service Context ------> " + getRunningService());
        AILog.e(TAG, "AdapterApplication Context ------> " + AdapterApplication.getContext());
        AIMusic.initMusic(AdapterApplication.getContext(), PreferenceHelper.getInstance().getDefaultMusicType());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AILog.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        AILog.i(TAG, "AIOS FloatWindowService on destroy!!!");
        MyWindowManager.getInstance().removeSmallWindow();

        stopForeground(true);
        HomeNode.getInstance().unRegister();
        PhoneNode.getInstance().unRegister();
        CustomizeNode.getInstance().unRegister();
        service = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 重新启动CustomizeNode
     */
    public void restartCustomizeNode() {

        AILog.e(TAG, "-----------restart customize node---------------");
        if (CustomizeNode.getInstance() != null && CustomizeNode.getInstance().isRunning()) {
            CustomizeNode.getInstance().stop();
        }
        if (mWorker == null) {
            mWorker = new HandlerThread("worker-thread");
            mWorker.start();
        }
        if (mWorkerHandler == null) {
            mWorkerHandler = new Handler(mWorker.getLooper());
        }

        mWorkerHandler.post(new Runnable() {
            @Override
            public void run() {
                CustomizeNode.getInstance().start();
                CustomizeNode.getInstance().register();
            }
        });
    }
}