/**
 * Copyright (c) 2012-2015 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : UniDriveFmGuiService.java
 * @ProjectName : uniCarGui
 * @PakageName : com.unisound.unicar.gui.fm
 * @Author : Xiaodong.He
 * @CreateDate : 2015-09-23
 */
package com.unisound.unicar.gui.fm;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.unisound.unicar.fm.service.IUniDriveFmCallback;
import com.unisound.unicar.fm.service.IUniDriveFmService;
import com.unisound.unicar.gui.preference.CommandPreference;
import com.unisound.unicar.gui.ui.MessageReceiver;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.PlayersControlManager;

/**
 * UniDriveFmGuiService
 * 
 * @author Xiaodong.He
 * @date 2015-09-23
 */
public class UniDriveFmGuiService extends Service {

    private static final String TAG = UniDriveFmGuiService.class.getSimpleName();

    private Context mContext;

    private IUniDriveFmService mUniDriveFmService;

    private static final int PLAYER_CONTROL_STATUS_PREVIOUS = 1;
    private static final int PLAYER_CONTROL_STATUS_NEXT = 2;
    private static final int PLAYER_CONTROL_STATUS_PLAY = 3;
    private static final int PLAYER_CONTROL_STATUS_PAUSE = 4;

    private static final int PLAYER_CONTROL_STATUS_EXIT = 6;

    private static final int MSG_PAUSE = 1001;
    private static final int MSG_PLAY = 1002;
    private static final int MSG_PREVOIUS = 1003;
    private static final int MSG_NEXT = 1004;
    private static final int MSG_STOP = 1005;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Logger.d(TAG, "!--->onCreate");
        mContext = getApplicationContext();

        bindUniDriveFmService();
        registReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Logger.d(TAG, "!--->onStartCommand: intent " + intent);
        boolean isUniDriveFmInstalled =
                DeviceTool.checkApkExist(mContext, GUIConfig.PACKAGE_NAME_UNICAR_FM);

        boolean isXmFmInstalled =
                DeviceTool.checkApkExist(mContext, GUIConfig.PACKAGE_NAME_XMLY_FM);
        if (isXmFmInstalled) {
            Logger.w(TAG, "!--->onStartCommand---XmFm Installed return;");
            return START_NOT_STICKY;
        }

        Logger.d(TAG, "!--->onStartCommand---is UniDriveFm Installed:" + isUniDriveFmInstalled);
        if (intent != null) {
            String action = intent.getAction();
            Logger.d(TAG, "!--->onStartCommand: action " + action);
            if (MessageReceiver.ACTION_START_UNIDRIVE_FM.equals(action)) {
                String artist = intent.getStringExtra(MessageReceiver.KEY_EXTRA_FM_ARTIST);
                String category = intent.getStringExtra(MessageReceiver.KEY_EXTRA_FM_CATEGORY);
                String keyword = intent.getStringExtra(MessageReceiver.KEY_EXTRA_FM_KEYWORD);
                Logger.d(TAG, "!--->onStartCommand:--keyword=" + keyword + "; category = "
                        + category + "; artist = " + artist);
                if (!TextUtils.isEmpty(keyword)) {
                    playVoice(artist, category, keyword);
                }
            }
        }

        return START_STICKY;
    }

    private void playVoice(String artist, String category, String keyword) {
        Logger.d(TAG, "playVoice---artist = " + artist + "; category = " + category
                + "; keyword = " + keyword + "; mUniDriveFmService=" + mUniDriveFmService);
        if (mUniDriveFmService == null) {
            return;
        }
        try {
            mUniDriveFmService.playVoice(artist, category, keyword);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mUniDriveFmService = IUniDriveFmService.Stub.asInterface(service);
            try {
                Logger.d(TAG, "!--->onServiceConnected()-----------");
                mUniDriveFmService.registerCallback(mCallback);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Logger.d(TAG, "onServiceDisconnected");
            mUniDriveFmService = null;
            rebindUniDriveFmService();
        }
    };

    private IUniDriveFmCallback mCallback = new IUniDriveFmCallback.Stub() {

        @Override
        public void onCallBack(String callBackJson) throws RemoteException {
            // TODO Auto-generated method stub
            Logger.d(TAG, "onCallBack---:" + callBackJson);
        }
    };

    public void bindUniDriveFmService() {
        try {
            Logger.d(TAG, "!--->bindUniDriveFmService");
            Intent intent = new Intent("com.unisound.unicar.fm.start");
            // intent.setPackage(getPackageName());
            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mContext.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rebindUniDriveFmService() {
        if (mUniDriveFmService == null) {
            Logger.d(TAG, "rebindUniDriveFmService");
            bindUniDriveFmService();
        }
    }

    /**
     * XmFm Play control receiver
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            boolean isXmFmInstalled =
                    DeviceTool.checkApkExist(mContext, GUIConfig.PACKAGE_NAME_XMLY_FM);
            if (isXmFmInstalled) {
                Logger.w(TAG, "!--->onReceive---XmFm Installed return;");
                return;
            }

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Logger.d(TAG, "mReceiver--key : " + key + ",value : " + bundle.get(key));
                }
            }
            String action = intent.getAction();
            String cmd = intent.getStringExtra(CommandPreference.CMDNAME);
            Logger.d(TAG, "mReceiver--cmd : " + cmd + ",action : " + action);

            if (mUniDriveFmService == null) {
                Logger.w(TAG, "mReceiver--UniDriveFmService is null, begin rebind.");
                bindUniDriveFmService();
                return;
            }
            try {
                int fmPlayerStatus = mUniDriveFmService.getPlayerStatus();
                boolean isXmFmOnBg = XmFmPlayerConstants.isXmFmOnBackground(fmPlayerStatus);
                Logger.d(TAG, "mReceiver--fmPlayerStatus = " + fmPlayerStatus + "; isXmFmOnBg = "
                        + isXmFmOnBg);
                if (CommandPreference.CMDPREVIOUS.equals(cmd)) {
                    if (isXmFmOnBg) {
                        mUniDriveFmService.playControl(PLAYER_CONTROL_STATUS_PREVIOUS);
                    }
                } else if (CommandPreference.CMDNEXT.equals(cmd)) {
                    if (isXmFmOnBg) {
                        mUniDriveFmService.playControl(PLAYER_CONTROL_STATUS_NEXT);
                    }
                } else if (CommandPreference.CMDPLAY.equals(cmd)) {
                    mUniDriveFmService.playControl(PLAYER_CONTROL_STATUS_PLAY);
                } else if (CommandPreference.CMDPAUSE.equals(cmd)) {
                    mUIHandler.sendEmptyMessageDelayed(MSG_PAUSE,
                            PlayersControlManager.TIME_SET_STATE_DELAY);
                    // if (XmFmPlayerConstants.isXmFmPlaying(fmPlayerStatus)) {
                    // Logger.d(TAG, "UniDriveFmPlayer is playing, pause it");
                    // mUniDriveFmService.playControl(PLAYER_CONTROL_STATUS_PAUSE);
                    // }
                } else if (CommandPreference.CMDSTOP.equals(cmd)) {
                    // mUniDriveFmService.playControl(PLAYER_CONTROL_STATUS_EXIT);
                    mUIHandler.sendEmptyMessageDelayed(MSG_STOP,
                            PlayersControlManager.TIME_SET_STATE_DELAY);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * UI control handler
     */
    private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            Logger.d(TAG, "mUIHandler---handleMessage--" + msg.what);
            switch (msg.what) {
                case MSG_PAUSE:
                    pause();
                    break;
                case MSG_STOP:
                    try {
                        mUniDriveFmService.playControl(PLAYER_CONTROL_STATUS_EXIT);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }

        };
    };

    private void pause() {
        try {
            int fmPlayerStatus = mUniDriveFmService.getPlayerStatus();
            if (XmFmPlayerConstants.isXmFmPlaying(fmPlayerStatus)) {
                Logger.d(TAG, "UniDriveFmPlayer is playing, pause it");
                mUniDriveFmService.playControl(PLAYER_CONTROL_STATUS_PAUSE);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void registReceiver() {
        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(CommandPreference.SERVICECMD);
        commandFilter.addAction(CommandPreference.ACTION_CONTROL_UNICARFM);
        mContext.registerReceiver(mReceiver, commandFilter);
    }

    private void unregistReceiver() {
        try {
            mContext.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            Logger.printStackTrace(e);
        }
    }



    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Logger.d(TAG, "!--->onDestroy");
        unregistReceiver();
    }

}
