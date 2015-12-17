package com.unisound.unicar.gui.domain.localsearch;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.Query;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.model.PoiInfo;
import com.unisound.unicar.gui.utils.Logger;

public class AmapLocalSearchClient {
    private static final String TAG = "AmapLocalSearchClient";

    private static AmapLocalSearchClient amapLocalSearchClient;
    private static Context mContext;
    private Query query;
    private static final int FRIST_PAGE = 0;
    private static final int PAGE_ITEM_NUM = 10;
    private static final int SERACH_TIMEOUT = 10000;
    public static final int WAHT_TIMEOUT = 10001;
    private OnPoiSearchListener mOnPoiSearchListener;
    private Message msg;

    private AmapLocalSearchClient() {}

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case WAHT_TIMEOUT:
                    mOnPoiSearchListener.onPoiSearched(null, WAHT_TIMEOUT);
                    break;
                default:
                    break;
            }
        };
    };

    public static AmapLocalSearchClient getInstance(Context mContext) {
        AmapLocalSearchClient.mContext = mContext;

        if (amapLocalSearchClient == null) {
            synchronized (AmapLocalSearchClient.class) {
                if (amapLocalSearchClient == null) {
                    amapLocalSearchClient = new AmapLocalSearchClient();
                }
            }
        }
        return amapLocalSearchClient;
    }


    public void searchAMAPPoiAsyn(double lat, double lng, String keyWord, String category,
            String cityCode, OnPoiSearchListener mOnPoiSearchListener) {
        Logger.d(TAG, "searchAMAPPoiAsyn : [" + "lat-" + lat + ", lng-" + lng + ", keyWord-"
                + keyWord + ", category-" + category + ", cityCode-" + cityCode);
        this.mOnPoiSearchListener = mOnPoiSearchListener;

        query = new PoiSearch.Query(keyWord, category, cityCode);
        query.setPageSize(PAGE_ITEM_NUM);
        query.setPageNum(FRIST_PAGE);

        PoiSearch poiSearch = new PoiSearch(mContext, query);
        poiSearch.setBound(new SearchBound(new LatLonPoint(lat, lng), 1000));
        poiSearch.setOnPoiSearchListener(mOnPoiSearchListener);
        sendDelayMsg();
        poiSearch.searchPOIAsyn();
    }


    private void sendDelayMsg() {
        msg = new Message();
        msg.what = WAHT_TIMEOUT;
        handler.sendMessageDelayed(msg, SERACH_TIMEOUT);
        Logger.d(TAG, "timeout msg has send");
    }

    public void onSearchDone() {
        Logger.d(TAG, "onSearchDone");
        if (handler.hasMessages(WAHT_TIMEOUT)) {
            handler.removeMessages(WAHT_TIMEOUT);
        }
    }

    public List<PoiInfo> getPoiInfo(ArrayList<PoiItem> poiItems) {
        List<PoiInfo> poiInfos = new ArrayList<PoiInfo>();
        if (poiItems != null && poiItems.size() > 0) {
            for (PoiItem item : poiItems) {
                PoiInfo poiInfo = new PoiInfo();
                poiInfo.setName(item.toString());
                String[] regions = {item.getAdName()};
                poiInfo.setRegions(regions);
                String[] types = item.getTypeDes().split(";");
                String[] mCategories = {types[types.length - 1]};
                poiInfo.setCategories(mCategories);
                poiInfo.setRating(3.5f);
                poiInfo.setTel(item.getTel());
                poiInfo.setDistance(item.getDistance());

                LatLonPoint latLonPoint = item.getLatLonPoint();
                double latitude = latLonPoint.getLatitude();
                double longitude = latLonPoint.getLongitude();
                LocationInfo mLocationInfo = new LocationInfo();
                mLocationInfo.setLatitude(latitude);
                mLocationInfo.setLongitude(longitude);
                poiInfo.setLocationInfo(mLocationInfo);

                poiInfos.add(poiInfo);
            }
            Logger.d(TAG, "poiInfos : " + poiInfos.toString());
        }
        return poiInfos;
    }
}
