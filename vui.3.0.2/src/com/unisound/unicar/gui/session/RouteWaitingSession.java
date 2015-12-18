/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : WaitingSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-2
 */
package com.unisound.unicar.gui.session;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.unisound.unicar.gui.data.operation.PoiDataModel;
import com.unisound.unicar.gui.location.interfaces.ILocationListener;
import com.unisound.unicar.gui.location.operation.LocationModelProxy;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.model.PoiInfo;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.search.interfaces.IPoiListener;
import com.unisound.unicar.gui.search.operation.POISearchModelProxy;
import com.unisound.unicar.gui.utils.ErrorUtil;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.RouteWaitingContentView;
import com.unisound.unicar.gui.view.RouteWaitingContentView.IRouteWaitingContentViewListener;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-2
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-2
 * @Modified: 2013-9-2: 实现基本功能
 */
public class RouteWaitingSession extends BaseSession {
    public static final String TAG = "RouteWaitingSession";
    private RouteWaitingContentView mWaitingContentView = null;
    private LocationInfo mlLocationInfo = null;
    private LocationModelProxy locationModel = null;
    private POISearchModelProxy poiSearchModelProxy = null;
    private String toPoi = "";
    private String toCity = "";
    private Context mContext;

    private final int RADIUS_DEFALUT = 1000;

    private boolean isSessionCanceled = false;
    /** 是不是已经发送过定位结果response */
    private boolean isRespknow = false;

    private String mCondition = "";

    private IRouteWaitingContentViewListener mListener = new IRouteWaitingContentViewListener() {

        @Override
        public void onCancel() {
            isSessionCanceled = true;
            sendPoiLocationFailedResp();

            mSessionManagerHandler
                    .sendEmptyMessage(SessionPreference.MESSAGE_WAITING_SESSION_CANCEL);
            if (locationModel != null) {
                locationModel.stopLocate();
            }
            if (poiSearchModelProxy != null) {
                poiSearchModelProxy.stop();
            }

        }
    };

    /**
	 * 
	 */
    private void sendPoiLocationFailedResp() {
        Logger.d(TAG, "!--->sendPoiLocationFailedResp----isRespknow = " + isRespknow);
        if (!isRespknow) {
            // if poi data do no send to vui when cancel btn clicked
            // or Poi Location Failed
            // send fail response to vui
            onRespParams("", "FAIL");
            isRespknow = true;
        }
    }


    /**
     * @Author : Dancindream
     * @CreateDate : 2013-9-2
     * @param context
     * @param sessionManagerHandler
     * @param sessionViewContainer
     */
    public RouteWaitingSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        mContext = context;
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);

        mAnswer = JsonTool.getJsonValue(jsonProtocol, "ttsAnswer");
        addQuestionViewText(mQuestion);

        if (mWaitingContentView == null) {
            mWaitingContentView = new RouteWaitingContentView(mContext);
        }
        mWaitingContentView.setLisener(mListener);
        toPoi = JsonTool.getJsonValue(mDataObject, "toPOI");
        mCondition = JsonTool.getJsonValue(mDataObject, "condition");
        mWaitingContentView.setEndPOI(toPoi);
        addAnswerView(mWaitingContentView);
        addAnswerViewText(mAnswer);

        if (WindowService.mLocationInfo != null) {
            this.mlLocationInfo = WindowService.mLocationInfo;
            toCity = getToCityString(mlLocationInfo.getCity());
            mWaitingContentView.setStartPOI(mlLocationInfo.getAddress());
            poiSearchModelProxy = POISearchModelProxy.getInstance(mContext);
            Logger.d(TAG, "Location info : " + mlLocationInfo + " toPoi : " + toPoi + " toCity "
                    + toCity);
            PoiDataModel poiDataModel = convert2PoiDataModel(mlLocationInfo, toPoi, toCity);
            poiSearchModelProxy.startSearch(poiDataModel, mPoiListener);
        } else {
            locationModel = LocationModelProxy.getInstance(mContext);

            locationModel.setLocationListener(new ILocationListener() {

                @Override
                public void onLocationResult(List<LocationInfo> infos, ErrorUtil code) {

                }

                @Override
                public void onLocationChanged(LocationInfo info, ErrorUtil errorUtil) {
                    Logger.d(TAG, "onLocationChanged :　info : " + info);
                    mlLocationInfo = info;
                    if (errorUtil != null) {
                        if (!isRespknow) {
                            onRespParams(getJsonRespFromErrorUtil(errorUtil));
                            isRespknow = true;
                        }
                    }
                    // 定位成功
                    else {
                        if (mlLocationInfo != null) {
                            toCity = getToCityString(info.getCity());
                            mWaitingContentView.setStartPOI(mlLocationInfo.getAddress());
                            poiSearchModelProxy = POISearchModelProxy.getInstance(mContext);
                            // 开始poi搜索
                            PoiDataModel poiDataModel =
                                    convert2PoiDataModel(mlLocationInfo, toPoi, toCity);
                            poiSearchModelProxy.startSearch(poiDataModel, mPoiListener);
                        }
                    }
                }

            });

            locationModel.startLocate();
        }

        Logger.d(TAG, "--WaitingSession mAnswer : " + mAnswer + "--");
    }

    private String getToCityString(String locationCity) {
        String toCityTemp = JsonTool.getJsonValue(mDataObject, "toCity");

        // 当获取为Current_city时，取定位城市。否则选择语义给出的城市。
        if ("CURRENT_CITY".equals(toCityTemp)) {
            return locationCity;
        } else {
            return toCityTemp;
        }
    }

    private PoiDataModel convert2PoiDataModel(LocationInfo mlLocationInfo, String toPoi,
            String toCity) {
        PoiDataModel poiDataModelTemp = new PoiDataModel();

        poiDataModelTemp.setLatitude(mlLocationInfo.getLatitude());
        poiDataModelTemp.setLontitude(mlLocationInfo.getLongitude());
        poiDataModelTemp.setCity(toCity);
        poiDataModelTemp.setPoi(toPoi);
        poiDataModelTemp.setCategory("");
        poiDataModelTemp.setRadius(RADIUS_DEFALUT);

        return poiDataModelTemp;
    }


    private String getJsonRespFromErrorUtil(ErrorUtil errorUtil) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("locationErrcode", errorUtil.code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private IPoiListener mPoiListener = new IPoiListener() {
        @Override
        public void onPoiSearchResult(List<PoiInfo> infos, ErrorUtil errorUtil) {
            List<LocationInfo> locationInfos = null;
            if (errorUtil != null) {

                if (isSessionCanceled) {
                    if (!isRespknow) {
                        onRespParams(getJsonRespFromErrorUtil(errorUtil), "FAIL");
                        isRespknow = true;
                    }
                } else {

                    if (!isRespknow) {
                        onRespParams(getJsonRespFromErrorUtil(errorUtil));
                        isRespknow = true;
                    }
                }
            }
            // 定位成功
            else {
                if (infos != null) {
                    locationInfos = new ArrayList<LocationInfo>();
                    for (PoiInfo poiInfo : infos) {
                        LocationInfo locationInfo = poiInfo.getLocationInfo();
                        if (locationInfo == null) {
                            locationInfo = new LocationInfo();
                        }
                        locationInfo.setName(poiInfo.getName());
                        locationInfo.setmCondition(mCondition);
                        locationInfos.add(locationInfo);
                    }
                    Logger.d(TAG, "locationInfos : " + JsonTool.toJson(locationInfos));

                    Logger.d(TAG, "!--->sendRespParamsDelay----isSessionCanceled = "
                            + isSessionCanceled);
                    if (isSessionCanceled) {
                        if (!isRespknow) {
                            onRespParams(JsonTool.toJson(locationInfos), "FAIL");
                            isRespknow = true;
                        }

                    } else {
                        if (!isRespknow) {
                            onRespParams(JsonTool.toJson(locationInfos));
                            isRespknow = true;
                        }

                    }

                }
            }
        }
    };


    /**
     * @param respJson
     */
    private void sendRespParamsDelay(String respJson) {
        Logger.d(TAG, "!--->sendRespParamsDelay----isSessionCanceled = " + isSessionCanceled
                + "; respJson = " + respJson);
        if (!isSessionCanceled) {
            onRespParamsDelay(respJson);
        } else {
            sendPoiLocationFailedResp();
        }
    }

    @Override
    public void release() {
        Logger.d(TAG, "!-----tyz--release--->");
        mWaitingContentView = null;
        locationModel = null;
        mPoiListener = null;
        super.release();
    }

    @Override
    public void onTTSEnd() {
        super.onTTSEnd();
        Logger.d(TAG, "!-----ttsend--route--->");
    }

}
