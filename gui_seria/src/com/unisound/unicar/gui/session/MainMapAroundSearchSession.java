/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : MainMapAroundSearchSession.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.session
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-20
 */
package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.ui.AMapPoiAroundSearchActivity;
import com.unisound.unicar.gui.utils.GuiProtocolUtil;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.AMapPoiAroundSearchView;
import com.unisound.unicar.gui.view.AMapPoiAroundSearchView.PoiAroundSearchViewListener;

/**
 * @author xiaodong.he
 * @date 20151021
 */
public class MainMapAroundSearchSession extends BaseSession {

    public static final String TAG = MainMapAroundSearchSession.class.getSimpleName();

    private Context mContext;

    private boolean isRecordingControlStoped = false;

    private AMapPoiAroundSearchView mAMapAroundSearchView;

    private static final boolean isShowView = true;

    private PoiAroundSearchViewListener mPoiAroundSearchViewListener =
            new PoiAroundSearchViewListener() {

                @Override
                public void onEditFucus() {
                    Logger.d(TAG, "onEditFucus-------");
                    if (!isRecordingControlStoped) {
                        sendRecordingControlEvent(false);
                    }
                    updateMicEnableStatus(false);
                }

                @Override
                public void onEditCancel() {
                    Logger.d(TAG, "onEditCancel-------");
                    sendRecordingControlEvent(true);
                    updateMicEnableStatus(true);
                }

                @Override
                public void onStartSelectPoiSearchType() {
                    Logger.d(TAG, "onStartSelectPoiSearchType----isRecordingControlStoped = "
                            + isRecordingControlStoped);
                    if (!isRecordingControlStoped) {
                        sendRecordingControlEvent(false);
                    }
                    updateMicEnableStatus(false);
                }

                @Override
                public void onPoiSearchTypeSelect(String poiSearchType) {
                    Logger.d(TAG, "onPoiSearchTypeSelect---poiSearchType = " + poiSearchType);
                    onUiProtocal(SessionPreference.EVENT_NAME_UPDATE_LOCALSEARCH_KEYWORD,
                            GuiProtocolUtil.getUpdateAroundSearchParamProtocol(poiSearchType));
                    updateMicEnableStatus(true);
                }

                @Override
                public void onMapViewMove() {
                    if (!isRecordingControlStoped) {
                        Logger.d(TAG, "onMapMove--is recording--stop it.");
                        sendRecordingControlEvent(false);
                    }
                }

            };

    /**
     * @param context
     * @param sessionManagerHandler
     */
    public MainMapAroundSearchSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        mContext = context;
    }

    @Override
    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);

        if (isShowView) {
            showAMapAroundSearchView();
        } else {
            startAMapPoiAroundSearchActivity();
        }
    }

    /**
     * 
     * @param isStart
     */
    private void sendRecordingControlEvent(boolean isStart) {
        Logger.d(TAG, "sendRecordingControlEvent---isStart = " + isStart);
        sendRecordingControlEvent(isStart, SessionPreference.DOMAIN_NEARBY_SEARCH);
        isRecordingControlStoped = !isStart;
    }

    private void showAMapAroundSearchView() {
        if (mAMapAroundSearchView == null) {
            mAMapAroundSearchView = new AMapPoiAroundSearchView(mContext);
            mAMapAroundSearchView.setPoiAroundSearchViewListener(mPoiAroundSearchViewListener);
        }

        addAnswerView(mAMapAroundSearchView);
    }

    private void startAMapPoiAroundSearchActivity() {
        Logger.d(TAG, "start AMapPoiAroundSearchActivity");
        Intent intent = new Intent(mContext, AMapPoiAroundSearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void onTTSEnd() {
        // TODO Auto-generated method stub
        super.onTTSEnd();
        Logger.d(TAG, "onTTSEnd---");

    }

    @Override
    public void release() {
        // TODO Auto-generated method stub
        super.release();
        Logger.d(TAG, "release---");
        if (null != mAMapAroundSearchView) {
            mAMapAroundSearchView.release();
        }
    }

}
