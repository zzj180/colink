/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : PlayersControlManager.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.utils
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-28
 */
package com.unisound.unicar.gui.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.kwmusic.KWMusicService;
import com.unisound.unicar.gui.preference.CommandPreference;
import com.unisound.unicar.gui.ui.MessageSender;

/**
 * PlayersControlManager
 * 
 * @author xiaodong.he
 * @date 20151028
 */
public class PlayersControlManager {

    public static final String TAG = PlayersControlManager.class.getSimpleName();

    private boolean isKWMusicFg = false;
    private boolean isXmFmFg = false;
    private boolean isUniCarFmFg = false;

    private boolean isKWMusicBg = false;
    private boolean isXmFmBg = false;
    private boolean isUniCarFmBg = false;

    private static PlayersControlManager instance;

    private PlayersControlManager() {}

    public synchronized static PlayersControlManager getInstance() {
        if (null == instance) {
            instance = new PlayersControlManager();
        }
        return instance;
    }

    private void resetPlayersRunningStatus() {
        isKWMusicFg = false;
        isXmFmFg = false;
        isUniCarFmFg = false;
        isKWMusicBg = false;
        isXmFmBg = false;
        isUniCarFmBg = false;
    }

    /**
     * send Players Control Cmd
     * 
     * @author xiaodong.he
     * 
     * @param context
     * @param messageSender
     * @param cmdName
     */
    public void sendPlayersControlCmd(Context context, MessageSender messageSender, String cmdName) {
        checkPlayersRunningStatus(context);
        String action = CommandPreference.ACTION_SERVICE_CMD;
        if (isKWMusicFg) {
            Logger.d(TAG, "sendPlayerControlCmd---isKWMusicFg only------");
            action = CommandPreference.ACTION_CONTROL_KWMUSIC;
        } else if (isXmFmFg) {
            Logger.d(TAG, "sendPlayerControlCmd---isXmFmFg only------");
            action = CommandPreference.ACTION_CONTROL_XMFM;
        } else if (isUniCarFmFg) {
            Logger.d(TAG, "sendPlayerControlCmd---isUniCarFmFg only------");
            action = CommandPreference.ACTION_CONTROL_UNICARFM;
        } else if (isKWMusicBg && !isXmFmBg && !isUniCarFmBg) {
            Logger.d(TAG, "sendPlayerControlCmd---isKWMusicBg only------");
            action = CommandPreference.ACTION_CONTROL_KWMUSIC;
        } else if (!isKWMusicBg && isXmFmBg && !isUniCarFmBg) {
            Logger.d(TAG, "sendPlayerControlCmd---isXmFmBg only------");
            action = CommandPreference.ACTION_CONTROL_XMFM;
        } else if (!isKWMusicBg && !isXmFmBg && isUniCarFmBg) {
            Logger.d(TAG, "sendPlayerControlCmd---isUniCarFmBg only------");
            action = CommandPreference.ACTION_CONTROL_UNICARFM;
        } else if (isKWMusicBg && KWMusicService.isKWMusicPlaying()) {
            Logger.d(TAG, "sendPlayerControlCmd---isKWMusicBg and is playing------");
            action = CommandPreference.ACTION_CONTROL_KWMUSIC;
        } else if (isKWMusicBg && !KWMusicService.isKWMusicPlaying() && isXmFmBg) {
            Logger.d(TAG, "sendPlayerControlCmd---isKWMusicBg but is not playing and XmFmBg------");
            action = CommandPreference.ACTION_CONTROL_XMFM;
        } else if (isKWMusicBg && !KWMusicService.isKWMusicPlaying() && !isXmFmBg && isUniCarFmBg) {
            Logger.d(TAG,
                    "sendPlayerControlCmd---isKWMusicBg but is not playing and XmFm not Bg and UniCarFm Bg-----");
            action = CommandPreference.ACTION_CONTROL_UNICARFM;
        } else {
            Logger.d(TAG, "sendPlayerControlCmd---send control all players cmd.");
        }
        Logger.d(TAG, "sendPlayerControlCmd--action = " + action + "; cmdName = " + cmdName);
        Intent intent = new Intent(action);
        intent.putExtra(CommandPreference.CMD_KEY_NAME, cmdName);
        if (null != messageSender) {
            messageSender = new MessageSender(context);
        }
        messageSender.sendOrderedMessage(intent, null);
        resetPlayersRunningStatus();
    }

    /**
     * check Players Running Status
     * 
     * @author xiaodong.he
     * @param context
     */
    private void checkPlayersRunningStatus(Context context) {
        checkPlayersForgeground(context);
        checkPlayersBackground(context);
        Logger.d(TAG, "checkPlayersRunningStatus--isKWMusicFg= " + isKWMusicFg + "; isXmFmFg = "
                + isXmFmFg + ";isUniCarFmFg = " + isUniCarFmFg + "; isKWMusicBg = " + isKWMusicBg
                + "; isXmFmBg = " + isXmFmBg + "; isUniCarFmBg = " + isUniCarFmBg);
    }

    /**
     * checkPlayersForgeground
     * 
     * @author xiaodong.he
     */
    private void checkPlayersForgeground(Context mContext) {
        String topActivityName = PackageUtil.getTopActivityName(mContext);
        Logger.d(TAG, "checkPlayersForgeground--topActivity:" + topActivityName);
        if (!TextUtils.isEmpty(topActivityName)) {
            if (topActivityName.startsWith(GUIConfig.PACKAGE_NAME_KUWO_MUSIC)) {
                isKWMusicFg = true;
            } else if (topActivityName.startsWith(GUIConfig.PACKAGE_NAME_XMLY_FM)) {
                isXmFmFg = true;
            } else if (topActivityName.startsWith(GUIConfig.PACKAGE_NAME_UNICAR_FM)) {
                isUniCarFmFg = true;
            }
        }
    }

    /**
     * check Players Background
     * 
     * @author xiaodong.he
     * @param context
     */
    private void checkPlayersBackground(Context context) {
        // Logger.d(TAG, "checkPlayersBackground---------Begin");
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(500);
        for (RunningTaskInfo info : list) {
            String topPkgName = info.topActivity.getPackageName();
            String basePkgName = info.baseActivity.getPackageName();

            String packageName = GUIConfig.PACKAGE_NAME_KUWO_MUSIC;
            if (topPkgName.equals(packageName) && basePkgName.equals(packageName)) {
                Logger.d(TAG, "isAppRunning " + packageName + " is running.");
                isKWMusicBg = true;
            }

            packageName = GUIConfig.PACKAGE_NAME_XMLY_FM;
            if (topPkgName.equals(packageName) && basePkgName.equals(packageName)) {
                Logger.d(TAG, "isAppRunning " + packageName + " is running.");
                isXmFmBg = true;
            }

            packageName = GUIConfig.PACKAGE_NAME_UNICAR_FM;
            if (topPkgName.equals(packageName) && basePkgName.equals(packageName)) {
                Logger.d(TAG, "isAppRunning " + packageName + " is running.");
                isUniCarFmBg = true;
            }
        }
        // Logger.d(TAG, "checkPlayersBackground---------End");
    }

}
