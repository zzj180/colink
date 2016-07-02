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

import android.annotation.SuppressLint;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unisound.unicar.gui.dao.FmCategoryDao;
import com.unisound.unicar.gui.preference.CommandPreference;
import com.unisound.unicar.gui.ui.MessageReceiver;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.PlayersControlManager;
import com.ximalaya.speechcontrol.Constants;
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

    private static final int TIME_DELAY_SATRT_SEARCH = 1500;
    private static final int TIME_DELAY_GET_CATAGORY_DATA = 300;
    private static final int TIME_CHECK_AFTER_STOP_DELAY = 500;

    private static final int MSG_INIT_CATEGORY_DATA = 1000;
    private static final int MSG_PAUSE = 1001;
    private static final int MSG_STOP = 1005;
    private static final int MSG_CHECK_AFTER_STOP = 1006;

    private static final int MSG_START_XMFM_PLAYER = 1007;

    private static final int MAX_REBIND_COUNT = 5;
    private int mRebindCount = 0;

    private String mKeyword = "";
    private int mCategoryId = 0;
    private int mEpisode = -1;

    /**
     * XM FM callback handler
     */
    @SuppressLint("HandlerLeak")
	private Handler mXmFmCallBackHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Logger.d(TAG, "msg.what = " + msg.what + "; msg.obj" + msg.obj);
            switch (msg.what) {
                case Constants.CodeGetCategoryModel:
                    Logger.d(TAG, "CallBackHandler---CodeGetCategoryModel----");
                    List<CategoryModel> categoryList =
                            new Gson().fromJson(msg.obj.toString(),
                                    new TypeToken<List<CategoryModel>>() {}.getType());
                    XmFmCategoryDataUtil.saveCategoryDataToDB(mContext, categoryList);
                    break;
                case Constants.CodeModelName:
                    Logger.d(TAG, "Playing:" + msg.obj.toString());
                    break;

                case Constants.CodeSearchAlbumList:
                    Logger.d(TAG, "CallBackHandler---CodeSearchAlbumList----");

                    // mControler.browseAlbums(album.getId(), 1, 20);
                    break;
                case Constants.CodeCommonTrackList:
                    Logger.d(TAG, "CallBackHandler---CodeCommonTrackList----");
                    Logger.d(TAG, "CallBackHandler---CodeCommonTrackList--mEpisode = " + mEpisode);

                    /*
                     * if (mEpisode != -1) { setPlayByAlbumTracks(commonTrackList, mEpisode); }
                     */
                    break;

                case Constants.CodeErr:
                    Logger.w(TAG, "Error:" + msg.obj.toString());
                    break;
                default:
                    break;
            }
        };
    };

    /**
     * UI control handler
     */
    @SuppressLint("HandlerLeak")
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
                case MSG_CHECK_AFTER_STOP:
                    doPauseIfPlaying();
                    break;
                case MSG_START_XMFM_PLAYER:
                    Logger.d(TAG, "MSG_SET_PLAY_MODE--keyword = " + mKeyword + "; categoryId = "
                            + mCategoryId);
                    startXmFmPlayer(mKeyword, mCategoryId, mEpisode);
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
        mRebindCount = 0;

        initXmFmPlayer();

        registReceiver();

        getCategoryListFromSDK();

    }

    private void initXmFmPlayer() {
        Logger.d(TAG, "initXmFmPlayer-----");
        mControler = SpeechControler.getInstance(this);
        mControler.init(XmFmConfig.XMFM_APP_SECRECT_ENCODE, XmFmConfig.XMFM_APP_KEY_ENCODE, "",
                mXmFmCallBackHandler);
    }

    private void getCategoryListFromSDK() {
        Logger.d(TAG, "!--->getCategoryListFromSDK-----mRebindCount = " + mRebindCount);
        if (checkConnection()) {
            Logger.d(TAG, "!--->getCategoryListFromSDK---getCategoryList begin.");
            mControler.getCategoryList();
        } else {
            if (mRebindCount < MAX_REBIND_COUNT) {
                Logger.d(TAG,
                        "!--->getCategoryListFromSDK---service not connected, get category data "
                                + TIME_DELAY_GET_CATAGORY_DATA + "ms delay");
                mUIHandler.sendEmptyMessageDelayed(MSG_INIT_CATEGORY_DATA,
                        TIME_DELAY_GET_CATAGORY_DATA);
            }
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
                mKeyword = intent.getStringExtra(MessageReceiver.KEY_EXTRA_FM_KEYWORD);
                mEpisode = intent.getIntExtra(MessageReceiver.KEY_EXTRA_FM_EPISODE, -1);
                // if (mEpisode != -1) {
                // mKeyword = mKeyword + " " + mEpisode;
                // }
                mCategoryId = 0;
                if (!TextUtils.isEmpty(categoryName)) {
                    mCategoryId = getCategoryIdByName(mContext, categoryName);
                }
                Logger.d(TAG, "!--->onStartCommand:--keyword=" + mKeyword + "; category = "
                        + categoryName + "; categoryId = " + mCategoryId + "; artist = " + artist);
                // if (!checkConnection()) {
                // Logger.w(TAG, "onStartCommand--service not Connect");
                //
                // }
                if (!TextUtils.isEmpty(mKeyword)) {
                    startXmFmPlayer(mKeyword, mCategoryId, mEpisode);
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
    private void startXmFmPlayer(String keyword, int categoryId, int episode) {
        if (checkConnection()) {
            Logger.d(TAG, "startXmFmPlayer---service is conntected---mRebindCount = "
                    + mRebindCount);
            setPlayMode(keyword, categoryId);
            if (null != mControler && !mControler.isPlaying()) {
                Logger.d(TAG, "!--->startXmFmPlayer--is not play, start play");
                play();
            }
            showXmFmPlayer();
        } else {
            if (mRebindCount < MAX_REBIND_COUNT) {
                mUIHandler.sendEmptyMessageDelayed(MSG_START_XMFM_PLAYER, TIME_DELAY_SATRT_SEARCH);
            }
        }
    };


    /**
     * 
     * @return false if service not Connect
     */
    private boolean checkConnection() {
        boolean isXmFmInstalled =
                DeviceTool.checkApkExist(mContext, GUIConfig.PACKAGE_NAME_XMLY_FM);
        Logger.d(TAG, "checkConnection----isXmFmInstalled = " + isXmFmInstalled);
        if (!isXmFmInstalled) {
            mRebindCount = MAX_REBIND_COUNT;
            Logger.w(TAG, "checkConnection--app:" + GUIConfig.PACKAGE_NAME_XMLY_FM
                    + ".apk is not installed.");
            return false;
        }
        if (null == mControler || (null != mControler && !mControler.checkConnectionStatus())) {
            Logger.w(TAG, "checkConnection--service not Connect---begin rebind.");
            initXmFmPlayer();
            mRebindCount++;
            return false;
        } else {
            return true;
        }
    }

    /**
     * 
     */
  /*  private void startXmFmPlayerContinuePlay(boolean isPlayNext) {
        Logger.d(TAG, "!--->startXmFmPlayerContinuePlay--isPlayNext: " + isPlayNext);
        showXmFmPlayer();
        if (isPlayNext) {
            playNext();
        } else {
            if (null != mControler && !mControler.isPlaying()) {
                play();
            }
        }
    }*/

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
  /*  private void setCategory(int categoryId) {
        if (null != mControler) {
            mControler.setCategoryId(categoryId);
        }
    }*/

    /**
     * 
     * @param keyword
     */
    private void setPlayMode(String keyword, int categoryId) {
        if (null != mControler) {
            Logger.d(TAG, "!--->setPlayMode---keyword = " + keyword + "; categoryId = "
                    + categoryId);
            mControler.setPlayModel(keyword, categoryId);
        }
    }


    /* < XD 20151116 added for search album begin */
    /**
     * get Album List
     * 
     * @date 2015-11-16
     * @param keyword
     * @param categoryId
     */
   /* private void getAlbumList(String keyword, int categoryId) {
        if (null != mControler) {
            // type: 1 search Album list; 2 search Sound list
            mControler.getSourseLists(keyword, categoryId, 1, 20, 1);
        }
    }*/

    /**
     * set Play By Album Tracks
     * 
     * @date 2015-11-16
     * @param commonTrackList
     * @param position
     */
  /*  private void setPlayByAlbumTracks(CommonTrackList commonTrackList, int position) {
        Logger.d(TAG, "position = " + position + "; commonTrackList = " + commonTrackList);
        if (null != mControler) {
            mControler.setPlayByAlbumTracks(commonTrackList, position);
        }
    }*/

    /* XD 20151116 added for search album End > */

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
            int fmPlayerStatus = mControler.getPlayerStatus();
            Logger.d(TAG, "exitXmFmApp--before exit---fmPlayerStatus = " + fmPlayerStatus);
            mControler.stopAndExitApp();
            mUIHandler.sendEmptyMessageDelayed(MSG_CHECK_AFTER_STOP, TIME_CHECK_AFTER_STOP_DELAY);
        }
    }

    /**
     * do Pause If is Playing
     * 
     * @author xiaodong.he
     * @date 201511-3
     */
    private void doPauseIfPlaying() {
        int fmPlayerStatus = mControler.getPlayerStatus();
        boolean isPlaying = XmFmPlayerConstants.isXmFmPlaying(fmPlayerStatus);
        Logger.d(TAG, "doPauseIfPlaying-----fmPlayerStatus = " + fmPlayerStatus + "; isPlaying = "
                + isPlaying);
        if (isPlaying) {
            mControler.pause();
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
            String cmd = intent.getStringExtra(CommandPreference.CMDNAME);
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
                if (CommandPreference.CMDPREVIOUS.equals(cmd)) {
                    if (isXmFmOnBg) {
                        playPrevious();
                    }
                } else if (CommandPreference.CMDNEXT.equals(cmd)) {
                    if (isXmFmOnBg) {
                        playNext();
                    }
                } else if (CommandPreference.CMDPLAY.equals(cmd)) {
                    play();
                } else if (CommandPreference.CMDPAUSE.equals(cmd)) {
                    mUIHandler.sendEmptyMessageDelayed(MSG_PAUSE,
                            PlayersControlManager.TIME_SET_STATE_DELAY);
                } else if (CommandPreference.CMDSTOP.equals(cmd)) {
                    mUIHandler.sendEmptyMessageDelayed(MSG_STOP,
                            PlayersControlManager.TIME_SET_STATE_DELAY);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void registReceiver() {
        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(CommandPreference.SERVICECMD);
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
