/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : MainMapLocationSession.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.session
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-20
 */
package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.utils.GuiProtocolUtil;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.AMapLocationView;
import com.unisound.unicar.gui.view.BaseMapLocationView;

/**
 * @author xiaodong.he
 * @date 20151020
 */
public class MainMapLocationSession extends BaseSession {

    public static final String TAG = MainMapLocationSession.class.getSimpleName();

    private Context mContext;

    private boolean isRecordingControlStoped = false;

    private BaseMapLocationView mMapLocationView;

    // private AMapLocationView mAMapLocationView;
    // private BaiduMapLocationView mBaiduMapLocationView;

    /**
     * @param context
     * @param sessionManagerHandler
     */
    public MainMapLocationSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        mContext = context;
    }

    @Override
    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);

        int mapType = UserPerferenceUtil.getMapChoose(mContext);
        Logger.d(TAG, "mapType = " + mapType);
        switch (mapType) {
            case UserPerferenceUtil.VALUE_MAP_AMAP:
                if (mMapLocationView == null) {
                    mMapLocationView = new AMapLocationView(mContext);
                }
                break;
            // case UserPerferenceUtil.VALUE_MAP_BAIDU:
            // if (mMapLocationView == null) {
            // mMapLocationView = new BaiduMapLocationView(mContext);
            // }
            // break;

            default:
                Logger.d(TAG, "default--AMapLocationView");
                if (mMapLocationView == null) {
                    mMapLocationView = new AMapLocationView(mContext);
                }
                break;
        }

        mMapLocationView.setMapLocationViewListener(mMapLocationViewListener);
        addAnswerView(mMapLocationView);
    }

    private BaseMapLocationView.MapLocationViewListener mMapLocationViewListener =
            new BaseMapLocationView.MapLocationViewListener() {

                @Override
                public void onEditLocationFocus() {
                    Logger.d(TAG,
                            "mMapLocationViewListener--onEditLocationFocus---isRecordingControlStoped = "
                                    + isRecordingControlStoped);
                    if (!isRecordingControlStoped) {
                        sendRecordingControlEvent(false);
                    }

                    updateMicEnableStatus(false);
                }

                @Override
                public void onConfirmLocationOk(String locationKeyword) {
                    Logger.d(TAG, "mMapLocationViewListener---onConfirmLocationOk---Keyword = "
                            + locationKeyword);
                    isRecordingControlStoped = false;
                    onUiProtocal(SessionPreference.EVENT_NAME_UPDATE_POI_KEYWORD,
                            GuiProtocolUtil.getChangeLocationParamProtocol(locationKeyword));
                    updateMicEnableStatus(true);
                }

                @Override
                public void onConfirmLocationCancel() {
                    Logger.d(TAG, "mMapLocationViewListener---onConfirmLocationCancel-----");
                    sendRecordingControlEvent(true);
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
     * 
     * @param isStart
     */
    private void sendRecordingControlEvent(boolean isStart) {
        Logger.d(TAG, "sendRecordingControlEvent---isStart = " + isStart);
        sendRecordingControlEvent(isStart, SessionPreference.DOMAIN_ROUTE);
        isRecordingControlStoped = !isStart;
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
        if (null != mMapLocationView) {
            mMapLocationView.release();
        }
    }

}
