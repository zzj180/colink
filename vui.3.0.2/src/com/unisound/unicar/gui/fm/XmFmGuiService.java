/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : XmFmGuiService.java
 * @ProjectName : uniCarSolution_xd_dev_0929
 * @PakageName : com.unisound.unicar.gui.fm
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-9-30
 */
package com.unisound.unicar.gui.fm;

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.dao.FmCategoryDao;
import com.unisound.unicar.gui.preference.CommandPreference;
import com.unisound.unicar.gui.ui.MessageReceiver;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;
import com.ximalaya.speechcontrol.ICategoryDataListener;
import com.ximalaya.speechcontrol.SpeechControler;
import com.ximalaya.ting.android.opensdk.model.category.CategoryModel;

/**
 * 
 * @author xiaodong
 * @date 2015-9-30
 */
public class XmFmGuiService extends Service {

    private static final String TAG = XmFmGuiService.class.getSimpleName();

    private Context mContext;
    private SpeechControler mControler;

    private static final int TIME_DELAY_GET_CATAGORY_DATA = 300;
    private static final int TIME_SET_STATE_DELAY = 2000;

    private static final int MSG_INIT_CATEGORY_DATA = 1000;
    private static final int MSG_PAUSE = 1001;
    private static final int MSG_PLAY = 1002;
    private static final int MSG_PREVOIUS = 1003;
    private static final int MSG_NEXT = 1004;
    private static final int MSG_STOP = 005;

    /**
     * XM FM callback handler
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Logger.d(TAG, "msg.what = " + msg.what + "; msg.obj" + msg.obj);
            switch (msg.what) {
                case 100:
                    // play title
                    // modelName.setText((String)msg.obj);
                    break;

                default:
                    break;
            }
        };
    };

    /**
     * UI control handler
     */
    private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            Logger.d(TAG, "mUIHandler---handleMessage--" + msg.what);
            switch (msg.what) {
                case MSG_INIT_CATEGORY_DATA:
                    getCategoryListFromSDK();
                    break;
                case MSG_PAUSE:
                    pause();
                    break;
                case MSG_STOP:
                    exitXmFmApp();
                    break;
                default:
                    break;
            }

        };
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "!--->onCreate");
        mContext = getApplicationContext();

        initXmFmPlayer();

        registReceiver();

        getCategoryListFromSDK();

    }

    private void initXmFmPlayer() {
        mControler = SpeechControler.getInstance(this);
        mControler.init(XmFmConfig.XMFM_APP_SECRECT, XmFmConfig.XMFM_APP_KEY,
                XmFmConfig.XMFM_PACK_ID, mHandler);

        setListener();
    }

    private void setListener() {
        if (null != mControler) {
            mControler.setICategoryDataListener(mICategoryDataListener);
        }
    }

    private ICategoryDataListener mICategoryDataListener = new ICategoryDataListener() {

        @Override
        public void onSuccess(List<CategoryModel> categoryList) {
            // TODO Auto-generated method stub
            Logger.d(TAG, "onSuccess--:" + categoryList.toString());
            XmFmCategoryDataUtil.saveCategoryDataToDB(mContext, categoryList);
        }

        @Override
        public void onError(String errorMessage) {
            // TODO Auto-generated method stub
            Logger.d(TAG, "onError--:" + errorMessage);
            // initCategoryDataFromLocal();
        }

    };

    /**
     * 
     */
    private void getCategoryListFromSDK() {
        Logger.d(TAG, "!--->getCategoryListFromSDK-----");
        if (null != mControler && !mControler.checkConnectionStatus()) {
            initXmFmPlayer();
            Logger.d(TAG, "!--->getCategoryListFromSDK---service not connected, get category data "
                    + TIME_DELAY_GET_CATAGORY_DATA + "ms delay");
            mUIHandler
                    .sendEmptyMessageDelayed(MSG_INIT_CATEGORY_DATA, TIME_DELAY_GET_CATAGORY_DATA);
        }

        if (null != mControler && mControler.checkConnectionStatus()) {
            Logger.d(TAG, "!--->getCategoryListFromSDK---getCategoryList begin.");
            mControler.getCategoryList();
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "!--->onStartCommand: intent " + intent);
        boolean isUniDriveFmInstalled =
                DeviceTool.checkApkExist(mContext, GUIConfig.PACKAGE_NAME_UNICAR_FM);

        Logger.d(TAG, "!--->onStartCommand---is UniDriveFm Installed:" + isUniDriveFmInstalled);
        if (intent != null) {
            String action = intent.getAction();
            Logger.d(TAG, "!--->onStartCommand: action " + action);
            if (MessageReceiver.ACTION_START_XM_FM.equals(action)) {
                String artist = intent.getStringExtra(MessageReceiver.KEY_EXTRA_FM_ARTIST);
                String categoryName = intent.getStringExtra(MessageReceiver.KEY_EXTRA_FM_CATEGORY);
                String keyword = intent.getStringExtra(MessageReceiver.KEY_EXTRA_FM_KEYWORD);
                int categoryId = 0;
                if (!TextUtils.isEmpty(categoryName)) {
                    categoryId = getCategoryIdByName(mContext, categoryName);
                }
                Logger.d(TAG, "!--->onStartCommand:--keyword=" + keyword + "; category = "
                        + categoryName + "; categoryId = " + categoryId + "; artist = " + artist);
                if (!TextUtils.isEmpty(keyword)) {
                    startXmFmPlayer(keyword, categoryId);
                }
            }
        }

        return START_STICKY;
    }

    /**
     * 
     * @param categoryName
     * @return
     */
    private int getCategoryIdByName(Context context, String categoryName) {
        int cId = FmCategoryDao.getInstance(context).getCagegoryIdByName(categoryName);
        return cId;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 
     * @param keyword
     * @param categoryId
     */
    private void startXmFmPlayer(String keyword, int categoryId) {
        setPlayMode(keyword, categoryId);

        if (null != mControler && !mControler.isPlaying()) {
            Logger.d(TAG, "!--->startXmFmPlayer--is not play, start play");
            play();
        }

        showXmFmPlayer();
    };


    /**
     * 
     */
    private void startXmFmPlayerContinuePlay(boolean isPlayNext) {
        Logger.d(TAG, "!--->startXmFmPlayerContinuePlay--isPlayNext: " + isPlayNext);
        showXmFmPlayer();
        if (isPlayNext) {
            playNext();
        } else {
            if (null != mControler && !mControler.isPlaying()) {
                play();
            }
        }
    }

    /**
     * 
     */
    private void showXmFmPlayer() {
        Logger.d(TAG, "!--->showXmFmPlayer");
        // com.ximalaya.ting.android.car/.activity.WelcomeActivity
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn =
                new ComponentName(GUIConfig.PACKAGE_NAME_XMLY_FM,
                        GUIConfig.ACTIVITY_NAME_XMLY_FM_MAIN);
        intent.setComponent(cn);
        startActivity(intent);
    }

    /**
     * 
     * @param categoryId
     */
    private void setCategory(int categoryId) {
        if (null != mControler) {
            mControler.setCategoryId(categoryId);
        }
    }

    /**
     * 
     * @param keyword 段子来了/ 郭德纲相声
     */
    private void setPlayMode(String keyword, int categoryId) {
        if (null != mControler) {
            Logger.d(TAG, "!--->setPlayMode---keyword = " + keyword + "; categoryId = "
                    + categoryId);
            mControler.setPlayModel(keyword, categoryId);
        }
    }

    private void play() {
        if (null != mControler) {
            mControler.play();
        }
    }

    private void pause() {
        if (null != mControler) {
            int fmPlayerStatus = mControler.getPlayerStatus();
            boolean isXmFmPlaying = XmFmPlayerConstants.isXmFmPlaying(fmPlayerStatus);
            Logger.d(TAG, "pause--isXmFmPlaying = " + isXmFmPlaying + "; mControler.isPlaying()--"
                    + mControler.isPlaying());

            mControler.pause();
        }
    }

    private void playNext() {
        if (null != mControler) {
            if (!mControler.hasNext()) {
                Toast.makeText(mContext, R.string.audio_no_next, Toast.LENGTH_LONG).show();
                return;
            }
            mControler.playNext();
        }
    }

    private void playPrevious() {
        if (null != mControler) {
            if (!mControler.hasPre()) {
                Toast.makeText(mContext, R.string.audio_no_previous, Toast.LENGTH_LONG).show();
                return;
            }
            mControler.playPre();
        }
    }

    /**
     * stop play and exit XmFm App
     */
    private void exitXmFmApp() {
        Logger.d(TAG, "exitXmFmApp.");
        if (null != mControler) {
            mControler.stopAndExitApp();
        }
    }

    /**
     * XmFm Play control receiver
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Logger.d(TAG, "mReceiver--key : " + key + ",value : " + bundle.get(key));
                }
            }
            String action = intent.getAction();
            String cmd = intent.getStringExtra(CommandPreference.CMD_KEY_NAME);
            Logger.d(TAG, "mReceiver--cmd : " + cmd + ",action : " + action);

            // TODO: check
            if (!mControler.checkConnectionStatus()) {
                Logger.w(TAG, "mReceiver--UniDriveFmService is null, begin rebind.");
                initXmFmPlayer();
                return;
            }
            try {
                // TODO getPlayerStatus
                int fmPlayerStatus = mControler.getPlayerStatus();
                boolean isXmFmOnBg = XmFmPlayerConstants.isXmFmOnBackground(fmPlayerStatus);
                Logger.d(TAG, "mReceiver--fmPlayerStatus = " + fmPlayerStatus + "; isXmFmOnBg = "
                        + isXmFmOnBg);
                if (CommandPreference.CMD_NAME_PREVIOUS.equals(cmd)) {
                    if (isXmFmOnBg) {
                        playPrevious();
                    }
                } else if (CommandPreference.CMD_NAME_NEXT.equals(cmd)) {
                    if (isXmFmOnBg) {
                        playNext();
                    }
                } else if (CommandPreference.CMD_NAME_PLAY.equals(cmd)) {
                    play();
                } else if (CommandPreference.CMD_NAME_PAUSE.equals(cmd)) {
                    mUIHandler.sendEmptyMessageDelayed(MSG_PAUSE, TIME_SET_STATE_DELAY);
                } else if (CommandPreference.CMD_NAME_STOP.equals(cmd)) {
                    mUIHandler.sendEmptyMessageDelayed(MSG_STOP, TIME_SET_STATE_DELAY);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void registReceiver() {
        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(CommandPreference.ACTION_SERVICE_CMD);
        commandFilter.addAction(CommandPreference.ACTION_CONTROL_XMFM);
        commandFilter.setPriority(1000);
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
        super.onDestroy();
        Logger.d(TAG, "!--->-----onDestroy-----");
        if (null != mControler) {
            mControler.destroy();
        }
        unregistReceiver();
    }


}
