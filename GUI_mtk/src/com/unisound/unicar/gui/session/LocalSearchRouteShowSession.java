/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : RouteShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package com.unisound.unicar.gui.session;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.route.baidu.BaiduMap;
import com.unisound.unicar.gui.route.operation.GaodeMap;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified: 2013-9-6: 实现基本功能
 */
public class LocalSearchRouteShowSession extends BaseSession {
    public static final String TAG = "LocalSearchRouteConfireShowSession";

    private Context mContext;

    private final int AMAP_INDEX = 1;
    private final int BAIDU_INDEX = 2;
    private final int MAPBAR_INDEX = 3;
    private final int RITU_INDEX = 4;

    private double toLat = 0.0;
    private double toLng = 0.0;
    private String toCity = "";
    private String toPoi = "";

    private LocationInfo mLocationInfo; // XD added 20150911
    private double mFromLat = 0.0;
    private double mFromLng = 0.0;
    private String mFromPoi = "";
    private String mFromeCity = "";
    private String mCondition = "";
    private int mStyle = 2;



    /**
     * @Author : Dancindream
     * @CreateDate : 2013-9-6
     * @param context
     * @param sessionManagerHandler
     */
    public LocalSearchRouteShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        this.mContext = context;
    }

    @SuppressWarnings("deprecation")
	public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        addQuestionViewText(mQuestion);
        getCurrentLocation();
        // mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        Log.d(TAG, "jsonProtocol = " + jsonProtocol.toString());
        try {
            JSONObject data = jsonProtocol.getJSONObject("data");
            JSONObject poiinfo = new JSONObject(data.getString("location"));
            toLat = poiinfo.getDouble("lat");
            toLng = poiinfo.getDouble("lng");
            toCity = poiinfo.getString("city");
            toPoi = poiinfo.getString("name");
            mCondition = poiinfo.getString("condition");
            mFromPoi = "出发地";
            changeConditionToStyle(mCondition);
            // showRoute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addAnswerViewText(mAnswer);

    }

    private void changeConditionToStyle(String condition) {
        Logger.d(TAG, "condition = " + condition);
        if ("TIME_FIRST".equals(condition)) {
            mStyle = 0;
        } else if ("ECAR_FEE_FIRST".equals(condition)) {
            mStyle = 1;
        } else if ("ECAR_DIS_FIRST".equals(condition)) {
            mStyle = 2;
        } else if ("ECAR_AVOID_TRAFFIC_JAM".equals(condition)) {
            mStyle = 4;
        }
    }

    private void getCurrentLocation() {
        mLocationInfo = WindowService.mLocationInfo;
        if (mLocationInfo != null) {
            mFromLat = mLocationInfo.getLatitude();
            mFromLng = mLocationInfo.getLongitude();
            mFromeCity = mLocationInfo.getCity();
            mFromPoi = mLocationInfo.getProvince();
        }
    }

    private void showRoute(int style) {
        int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
        switch (mapIndex) {
            case AMAP_INDEX:
                Logger.d(TAG, "mapIndex : " + mapIndex + " use amap route ...");
                // XD modify 20150914
                GaodeMap.showRoute(mContext, "driving", mFromLat, mFromLng, mFromeCity, mFromPoi,
                        toLat, toLng, toCity, toPoi, style);

                // GaodeMap.showRoute(mContext, "driving", toLat, toLng,toCity, toPoi);
                break;
            case BAIDU_INDEX:
                Logger.d(TAG, "mapIndex : " + mapIndex + " use baidu route ...");
                // XD modify 20150914
                // convertAmap2Baidu();
                BaiduMap.showRoute(mContext, BaiduMap.ROUTE_MODE_DRIVING, mFromLat, mFromLng,
                        mFromeCity, mFromPoi, toLat, toLng, toCity, toPoi);

                // startNavi(toLat, toLng, mFromPoi, toPoi);
                break;
            case MAPBAR_INDEX:
                Logger.d(TAG, "mapIndex : " + mapIndex + " use mapbar route ...");
                JSONObject mJson = new JSONObject();
                try {
                    mJson.put("toLatitude", toLat);
                    mJson.put("toLongtitude", toLng);
                    mJson.put("toPoi", toPoi);
                    Log.d(TAG, "msg = " + mJson.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent mIntent = new Intent();
                String action = "android.intent.action.SEND_POIINFO";
                mIntent.setAction(action);
                mIntent.putExtra("poi_info", mJson.toString());
                Log.d(TAG, "msg = " + action.toString());
                mContext.sendBroadcast(mIntent);
                break;
            case RITU_INDEX:
                Logger.d(TAG, "mapIndex : " + mapIndex + " use ritu route ...");
                Intent intent = new Intent("android.intent.action.ritu.keyword.name");
                int lat = (int) toLat;
                int lng = (int) toLng;
                Logger.d(TAG, "navi_keyword_name ：【" + lng + "," + lat + "," + toPoi + "】");
                intent.putExtra("navi_keyword_name", lng + "," + lat + "," + toPoi);
                mContext.sendBroadcast(intent);
                break;
            default:
                break;
        }
    }

    /*
     * public void startNavi(double toLat, double toLng, String fromPoi, String toPoi) {
     * //先将高德坐标转换为百度坐标 convertAmap2Baidu(); LatLng pt1 = new LatLng(mFromLat, mFromLng); LatLng pt2
     * = new LatLng(toLat, toLng);
     * 
     * Logger.d(TAG, "from lng : [ " + mFromLat + " -- " + mFromLng + " ]"); Logger.d(TAG,
     * "to lng : [ " + toLat + " -- " + toLng + " ]"); Logger.d(TAG, "from poi : " + fromPoi +
     * " to poi : " + toPoi);
     * 
     * // 构建 导航参数 NaviPara para = new NaviPara(); para.startPoint = pt1; para.startName = fromPoi;
     * para.endPoint = pt2; para.endName = toPoi;
     * 
     * try { BaiduMapNavigation.openBaiduMapNavi(para, mContext); } catch
     * (BaiduMapAppNotSupportNaviException e) { e.printStackTrace(); } }
     */

/*    private void convertAmap2Baidu() {
        mFromLat = LocationModelProxy.getInstance(mContext).getLastLocation().getLatitude();
        mFromLng = LocationModelProxy.getInstance(mContext).getLastLocation().getLongitude();

        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordType.COMMON);
        // sourceLatLng待转换坐标
        Logger.d(TAG, "convertAmap2Baidu--Before convert : lat : " + mFromLat + " lng : "
                + mFromLng);
        LatLng tempLng = new LatLng(mFromLat, mFromLng);
        converter.coord(tempLng);
        LatLng desLatLng = converter.convert();
        mFromLat = desLatLng.latitude;
        mFromLng = desLatLng.longitude;
        Logger.d(TAG, "convertAmap2Baidu--After convert : lat : " + mFromLat + " lng : " + mFromLng);
    }*/

    @Override
    public void onTTSEnd() {
        super.onTTSEnd();
        Logger.d(TAG, "!---LocalSearchRouteConfireShowSession------------>onTTSEnd");
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        showRoute(mStyle);
    }
}
