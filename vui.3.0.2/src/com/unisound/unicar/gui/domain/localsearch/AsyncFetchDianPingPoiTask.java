package com.unisound.unicar.gui.domain.localsearch;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.model.PoiInfo;
import com.unisound.unicar.gui.search.interfaces.IPoiListener;
import com.unisound.unicar.gui.utils.DataModelErrorUtil;
import com.unisound.unicar.gui.utils.JsonTool;

public class AsyncFetchDianPingPoiTask extends AsyncTask<String, Integer, List<PoiInfo>> {
    public static final String TAG = "AsyncFetchDianPingPoiTask";
    private IPoiListener mListener;

    public AsyncFetchDianPingPoiTask() {}

    @Override
    protected List<PoiInfo> doInBackground(String... params) {
        if (params != null && params.length > 0) {
            String url = params[0];
            try {
                return parseJSONResult(DefaultHttpRequest.getHttpResponse(url, "GET", 30000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private List<PoiInfo> parseJSONResult(String result) {
        try {
            JSONObject obj = JsonTool.parseToJSONObject(result);
            if ("OK".equals(obj.getString(DianPing.STATUS))) {
                JSONArray businessArr = JsonTool.getJsonArray(obj, DianPing.BUSINESSES);
                List<PoiInfo> resultList = new ArrayList<PoiInfo>();
                for (int i = 0; i < businessArr.length(); i++) {
                    PoiInfo poiSearchResult = new PoiInfo();

                    JSONObject poiObj = JsonTool.getJSONObject(businessArr, i);
                    poiSearchResult.setId(JsonTool.getJsonValue(poiObj, DianPing.BUSINESS_ID));
                    poiSearchResult.setName(JsonTool.getJsonValue(poiObj, DianPing.NAME));
                    poiSearchResult.setBranchName(JsonTool.getJsonValue(poiObj,
                            DianPing.BRANCH_NAME));
                    poiSearchResult.setDistance(JsonTool
                            .getJsonValue(poiObj, DianPing.DISTANCE, -1));

                    JSONArray categoriesArr = JsonTool.getJsonArray(poiObj, DianPing.CATEGORIES);
                    if (categoriesArr != null) {
                        String[] categories = new String[categoriesArr.length()];
                        for (int j = 0; j < categoriesArr.length(); j++) {
                            categories[j] = categoriesArr.getString(j);
                        }
                        poiSearchResult.setCategories(categories);
                    }

                    JSONArray regionsArr = JsonTool.getJsonArray(poiObj, DianPing.REGIONS);
                    if (regionsArr != null) {
                        String[] regions = new String[regionsArr.length()];
                        for (int j = 0; j < regionsArr.length(); j++) {
                            regions[j] = regionsArr.getString(j);
                        }
                        poiSearchResult.setRegions(regions);
                    }
                    poiSearchResult.setRating(Float.parseFloat(JsonTool.getJsonValue(poiObj,
                            DianPing.AVG_RATING, "0.0")));
                    poiSearchResult
                            .setHas_coupon(JsonTool.getJsonValue(poiObj, "has_coupon") == "0"
                                    ? false
                                    : true);
                    poiSearchResult.setHas_deal(JsonTool.getJsonValue(poiObj, "has_deal") == "0"
                            ? false
                            : true);
                    poiSearchResult.setHas_online_reservation(JsonTool.getJsonValue(poiObj,
                            "has_online_reservation") == "0" ? false : true);
                    poiSearchResult.setAvg_price(Float.parseFloat(JsonTool.getJsonValue(poiObj,
                            "avg_price", "0.0")));

                    poiSearchResult.setAddress(JsonTool.getJsonValue(poiObj, DianPing.ADDRESS));
                    poiSearchResult.setTel(JsonTool.getJsonValue(poiObj, DianPing.TELEPHONE));
                    poiSearchResult.setUrl(JsonTool.getJsonValue(poiObj, DianPing.BUSINESS_URL));

                    if (poiObj.has(DianPing.LATITUDE) && poiObj.has(DianPing.LONGITUDE)) {
                        double lat = poiObj.getDouble(DianPing.LATITUDE);
                        double lng = poiObj.getDouble(DianPing.LONGITUDE);
                        LocationInfo info = new LocationInfo();
                        info.setLatitude(lat);
                        info.setLongitude(lng);
                        info.setCity(JsonTool.getJsonValue(poiObj, DianPing.CITY));
                        info.setType(LocationInfo.GPS_ORIGIN);
                        poiSearchResult.setLocationInfo(info);
                    }
                    resultList.add(poiSearchResult);
                }
                if (resultList.size() > 0) {
                    return resultList;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<PoiInfo> result) {
        super.onPostExecute(result);
        if (mListener != null && !isCancelled()) {
            if (result != null) {
                mListener.onPoiSearchResult(result, null);
            } else {
                mListener.onPoiSearchResult(result,
                        DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.RETURN_NORESULT));
            }
        }
    }

    public void setListener(IPoiListener listener) {
        mListener = listener;
    }
}
