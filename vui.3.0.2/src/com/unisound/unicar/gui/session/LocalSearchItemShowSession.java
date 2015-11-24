/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : LocalSearchItemShowSession.java
 * @ProjectName : uniCarSolution_dev_xd_1010
 * @PakageName : com.unisound.unicar.gui.view
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-28
 */
package com.unisound.unicar.gui.session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.domain.localsearch.DianPing;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.model.PoiInfo;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.LocalSearchItemShowView;
import com.unisound.unicar.gui.view.LocalSearchItemShowView.LocalSearchItemViewListener;

public class LocalSearchItemShowSession extends BaseSession {
    private static final String TAG = LocalSearchItemShowSession.class.getSimpleName();

    private LocalSearchItemShowView mlocalSearchItemView;
    private Context mContext;

    public LocalSearchItemShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        this.mContext = context;
    }

    private LocalSearchItemViewListener mViewListener = new LocalSearchItemViewListener() {

        @Override
        public void onButtonClick(int type) {
            switch (type) {
                case LocalSearchItemShowView.TYPE_BUTTON_CALL:
                    onUiProtocal(SessionPreference.EVENT_NAME_SELECT_LOCALSEARCH_ITEM_ACTION,
                            "{\"param\":\"call\"}");
                    break;
                case LocalSearchItemShowView.TYPE_BUTTON_NAVI:
                    onUiProtocal(SessionPreference.EVENT_NAME_SELECT_LOCALSEARCH_ITEM_ACTION,
                            "{\"param\":\"route\"}");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void putProtocol(JSONObject jsonProtocol) {
        // TODO Auto-generated method stub
        super.putProtocol(jsonProtocol);
        Logger.d(TAG, "putProtocol-jsonProtocal = " + jsonProtocol);
        String phoneNum = "";
        if (mDataObject != null) {
            phoneNum = JsonTool.getJsonValue(mDataObject, "phoneNum");
        }

        mlocalSearchItemView = new LocalSearchItemShowView(mContext);
        mlocalSearchItemView.initData(getDatas());
        // mlocalSearchItemView.setText(jsonProtocol.toString());

        mlocalSearchItemView.setViewListener(mViewListener);
        // if (TextUtils.isEmpty(phoneNum)) {
        // mlocalSearchItemView.setBtnCallVisiable(true);
        // }
        addAnswerView(mlocalSearchItemView);
    }

    private PoiInfo getDatas() {
        PoiInfo poiSearchResult = new PoiInfo();
        if (mDataObject == null) {
            return null;
        }
        try {
            String poiObjStr = JsonTool.getJsonValue(mDataObject, "locationsearch");
            Logger.d(TAG, "getDatas---poiObjStr = " + poiObjStr);
            JSONObject poiObj = JsonTool.parseToJSONObject(poiObjStr);
            JSONArray regionsArr = JsonTool.getJsonArray(poiObj, "region");
            if (regionsArr != null) {
                String[] regions = new String[regionsArr.length()];
                for (int j = 0; j < regionsArr.length(); j++) {
                    regions[j] = regionsArr.getString(j);
                }
                poiSearchResult.setRegions(regions);
            }

            JSONObject local = poiObj.getJSONObject("location");
            double lat = local.getDouble("lat");
            double lng = local.getDouble("lng");
            LocationInfo info = new LocationInfo();
            info.setLatitude(lat);
            info.setLongitude(lng);
            info.setCity(JsonTool.getJsonValue(local, DianPing.CITY));
            info.setType(LocationInfo.GPS_ORIGIN);
            poiSearchResult.setLocationInfo(info);

            poiSearchResult.setTel(JsonTool.getJsonValue(poiObj, "tel"));
            poiSearchResult.setHas_online_reservation(JsonTool.getJsonValue(poiObj,
                    "has_online_reservation", false));
            poiSearchResult.setUrl(JsonTool.getJsonValue(poiObj, "url"));
            poiSearchResult.setId(JsonTool.getJsonValue(poiObj, "id"));
            poiSearchResult.setName(JsonTool.getJsonValue(poiObj, "name"));

            poiSearchResult.setHas_coupon(JsonTool.getJsonValue(poiObj, "has_coupon", false));
            poiSearchResult.setDistance(JsonTool.getJsonValue(poiObj, "distance", 0));
            poiSearchResult.setRating((float) JsonTool.getJsonValue(poiObj, "rate", 0.0));

            JSONArray categoriesArr = JsonTool.getJsonArray(poiObj, "category");
            String[] category = {categoriesArr.get(0).toString()};
            poiSearchResult.setCategories(category);

            poiSearchResult.setSeletItem(JsonTool.getJsonValue(poiObj,
                    SessionPreference.KEY_TO_SELECT, ""));
            poiSearchResult.setCallSelectItem(JsonTool.getJsonValue(poiObj,
                    SessionPreference.KEY_CALL_TO_SELECT, ""));

            if (regionsArr != null && regionsArr.length() > 1) {
                String[] categoriesArrs = new String[categoriesArr.length()];
                for (int j = 0; j < categoriesArr.length(); j++) {
                    categoriesArrs[j] = categoriesArr.getString(j);
                }
                poiSearchResult.setCategories(categoriesArrs);
            }

            poiSearchResult.setHas_deal(JsonTool.getJsonValue(poiObj, "has_deal", false));
            poiSearchResult.setAvg_price(JsonTool.getJsonValue(poiObj, "avg_price", 0));
            poiSearchResult.setBranchName(JsonTool.getJsonValue(poiObj, "branchName"));
        } catch (JSONException e) {
            Logger.d(TAG, e);
        }
        return poiSearchResult;
    }

}
