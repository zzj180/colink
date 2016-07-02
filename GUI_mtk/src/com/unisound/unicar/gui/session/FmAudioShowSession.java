package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.unisound.unicar.gui.fm.UniDriveFmGuiService;
import com.unisound.unicar.gui.fm.XmFmGuiService;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.ui.MessageReceiver;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * FmAudioShowSession
 * 
 * @author xiaodong
 * @date 20150922
 */
public class FmAudioShowSession extends BaseSession {
    private static final String TAG = FmAudioShowSession.class.getSimpleName();
    private Context mContext;

    private String mCategory;
    private String mArtist;
    private String mKeyword;
    private int mEpisode = -1;
    private String mSearchType;

    public FmAudioShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        Logger.d(TAG, "FmAudioShowSession create");
        this.mContext = context;
        mSessionManagerHandler = sessionManagerHandler;
    }

    @Override
    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "putProtocal -- jsonProtocol : " + jsonProtocol.toString());

        mSearchType = mOriginCode;
        mCategory = JsonTool.getJsonValue(mDataObject, "category");
        mArtist = JsonTool.getJsonValue(mDataObject, "artist");
        mKeyword = JsonTool.getJsonValue(mDataObject, "keyword");

        if (mDataObject.has(SessionPreference.KEY_AUDIO_EPISODE)) {
            mEpisode = JsonTool.getJsonValue(mDataObject, SessionPreference.KEY_AUDIO_EPISODE, -1);
            Logger.d(TAG, "putProtocol----episode = " + mEpisode);
        }
        // String answer = mAnswer;

    }

    /**
     * start UniDriveFm Player
     * 
     * @param mCategory
     * @param mArtist
     * @param mKeyword
     */
    private void startUniDriveFmPlayer() {
        Logger.d(TAG, "startUniDriveFmPlayer----");
        Intent intent = new Intent(mContext, UniDriveFmGuiService.class);
        intent.setAction(MessageReceiver.ACTION_START_UNIDRIVE_FM);
        intent.putExtra(MessageReceiver.KEY_EXTRA_FM_CATEGORY, mCategory);
        intent.putExtra(MessageReceiver.KEY_EXTRA_FM_ARTIST, mArtist);
        intent.putExtra(MessageReceiver.KEY_EXTRA_FM_KEYWORD, mKeyword);

        intent.putExtra(MessageReceiver.KEY_EXTRA_FM_SEARCH_TYPE, mSearchType);
        intent.putExtra(MessageReceiver.KEY_EXTRA_FM_EPISODE, mEpisode);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startService(intent);
    }


    /**
     * start XM FM Player
     * 
     * @param mCategory
     * @param mArtist
     * @param mKeyword
     */
    private void startXmFmPlayer() {
        Logger.d(TAG, "startXmFmPlayer----");
        Intent intent = new Intent(mContext, XmFmGuiService.class);
        intent.setAction(MessageReceiver.ACTION_START_XM_FM);
        intent.putExtra(MessageReceiver.KEY_EXTRA_FM_CATEGORY, mCategory);
        intent.putExtra(MessageReceiver.KEY_EXTRA_FM_ARTIST, mArtist);
        intent.putExtra(MessageReceiver.KEY_EXTRA_FM_KEYWORD, mKeyword);

        intent.putExtra(MessageReceiver.KEY_EXTRA_FM_SEARCH_TYPE, mSearchType);
        intent.putExtra(MessageReceiver.KEY_EXTRA_FM_EPISODE, mEpisode);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startService(intent);
    }

    @Override
    public void onTTSEnd() {
        super.onTTSEnd();
        Logger.d(TAG, "onTTSEnd");
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);

        boolean isXmFmInstalled =
                DeviceTool.checkApkExist(mContext, GUIConfig.PACKAGE_NAME_XMLY_FM);
        Logger.d(TAG, "!--->onTTSEnd---isXmFmInstalled = " + isXmFmInstalled);
        if (isXmFmInstalled) {
            startXmFmPlayer();
        } else {
            startUniDriveFmPlayer();
        }

    }

}
