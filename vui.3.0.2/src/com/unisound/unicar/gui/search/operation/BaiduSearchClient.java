package com.unisound.unicar.gui.search.operation;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.unisound.unicar.gui.data.operation.PoiDataModel;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.model.PoiInfo;
import com.unisound.unicar.gui.search.interfaces.IPoiListener;
import com.unisound.unicar.gui.utils.DataModelErrorUtil;
import com.unisound.unicar.gui.utils.ErrorUtil;
import com.unisound.unicar.gui.utils.Logger;

public class BaiduSearchClient extends BaseMapSearchClient {
    private final static String TAG = "BaiduSearch";
    private Object mSearcherObj;

    public BaiduSearchClient(Context context) {
        super(context);
        Logger.d(TAG, "BaiduSearchClient init ...");
    }

    private String transPoiResulttoString(PoiResult result) {
        if (result == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("error:");
        builder.append(result.error);
        builder.append("\n");

        builder.append("totalPoiNum:");
        builder.append(result.getTotalPoiNum());
        builder.append("\n");

        builder.append("allPoi:");
        builder.append(result.getAllPoi());
        builder.append("\n");

        return builder.toString();
    }

    private OnGetPoiSearchResultListener mBaiduPoiSearchResultListener =
            new OnGetPoiSearchResultListener() {

                @Override
                public void onGetPoiResult(PoiResult result) {
                    if (isTimeout() || isCanceled()) {
                        return;
                    }
                    Logger.d(TAG, "onGetPoiResult: result " + transPoiResulttoString(result));
                    onSearchResultReach();
                    ErrorUtil error = null;
                    List<PoiInfo> searchResult = null;
                    if (result == null || result.getAllPoi() == null
                            || result.getAllPoi().size() == 0) {
                        error = DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.RETURN_NORESULT);
                    } else if (result.error != null
                            && ReverseGeoCodeResult.ERRORNO.NO_ERROR != result.error) {
                        Logger.e(TAG, result.error.toString());
                        error =
                                DataModelErrorUtil
                                        .getErrorUtil(DataModelErrorUtil.RETURN_EXCEPTION_RESULT);
                    } else {
                        List<com.baidu.mapapi.search.core.PoiInfo> infos = result.getAllPoi();
                        searchResult = new ArrayList<PoiInfo>();
                        for (com.baidu.mapapi.search.core.PoiInfo poiInfo : infos) {
                            searchResult.add(parse2POIInfo(poiInfo));
                        }
                        error = null;
                    }
                    if (mPoiListener != null) {
                        mPoiListener.onPoiSearchResult(searchResult, error);
                    }
                    destorySearcher();
                }

                @Override
                public void onGetPoiDetailResult(PoiDetailResult result) {
                    if (isTimeout() || isCanceled()) {
                        return;
                    }
                    Logger.d(TAG, "onGetPoiDetailResult: result " + result);
                    onSearchResultReach();
                    destorySearcher();
                }
            };

    private static PoiInfo parse2POIInfo(com.baidu.mapapi.search.core.PoiInfo baiduPoiInfo) {
        PoiInfo info = new PoiInfo();
        info.setId(baiduPoiInfo.uid);
        info.setName(baiduPoiInfo.name);
        info.setTel(baiduPoiInfo.phoneNum);
        info.setPostCode(baiduPoiInfo.postCode);
        info.setTypeDes(baiduPoiInfo.type.name());

        Logger.d(TAG, "info ： " + info.toString());

        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setCity(baiduPoiInfo.city);
        locationInfo.setAddress(baiduPoiInfo.address);

        LatLng point = baiduPoiInfo.location;
        if (point != null) {
            locationInfo.setLatitude(point.latitude);
            locationInfo.setLongitude(point.longitude);
        }
        info.setLocationInfo(locationInfo);
        return info;
    }

    @Override
    public void startSearch(PoiDataModel poiDataModel, IPoiListener poiListener) {
        super.startSearch(poiDataModel, poiListener);

        Logger.d(TAG,
                "searchBaiduPOIAsyn latitude : " + poiDataModel.getLatitude() + ", lontitude : "
                        + poiDataModel.getLontitude() + ", city : " + poiDataModel.getCity()
                        + ", poi : " + poiDataModel.getPoi());

        if (TextUtils.isEmpty(poiDataModel.getCity())) {
            Logger.e(TAG, "param city can't be empty!");
        }

        PoiCitySearchOption option = new PoiCitySearchOption();
        option.city(poiDataModel.getCity());
        option.keyword(poiDataModel.getPoi());
        searchInCity(option, poiListener);
    }

    public boolean searchInCity(PoiCitySearchOption option, IPoiListener poiListener) {
        Logger.d(TAG, "searchInCity: option " + option.toString() + ", poiListener " + poiListener);
        mPoiListener = poiListener;
        PoiSearch poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(mBaiduPoiSearchResultListener);
        requestSearch(new Runnable() {

            @Override
            public void run() {
                if (mPoiListener != null) {
                    mPoiListener.onPoiSearchResult(null,
                            DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.SEARCH_POI_TIMEOUT));
                }
            }
        });
        return poiSearch.searchInCity(option);
    }

    private void destorySearcher() {
        if (mSearcherObj == null) {
            return;
        }
        if (mSearcherObj instanceof PoiSearch) {
            ((PoiSearch) mSearcherObj).setOnGetPoiSearchResultListener(null);
            ((PoiSearch) mSearcherObj).destroy();
        } else if (mSearcherObj instanceof GeoCoder) {
            ((GeoCoder) mSearcherObj).setOnGetGeoCodeResultListener(null);
            ((GeoCoder) mSearcherObj).destroy();
        }
        mSearcherObj = null;
    }

    @Override
    public void stopSearch() {
        super.stopSearch();
    }

    @Override
    public void release() {
        super.release();
    }
}
