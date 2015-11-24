/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : GaodeGeocoding.java
 * @ProjectName : vui_datamodel
 * @PakageName : cn.yunzhisheng.vui.location.operation
 * @Author : Conquer
 * @CreateDate : 2014-2-20
 */
package com.unisound.unicar.gui.search.operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.Query;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.unisound.unicar.gui.data.operation.PoiDataModel;
import com.unisound.unicar.gui.location.interfaces.ILocationListener;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.model.PoiInfo;
import com.unisound.unicar.gui.search.interfaces.IPoiListener;
import com.unisound.unicar.gui.utils.DataModelErrorUtil;
import com.unisound.unicar.gui.utils.ErrorUtil;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Conquer
 * @CreateDate : 2014-2-20
 * @ModifiedBy : zhuoran
 * @ModifiedDate: 2015-7-22
 * @Modified: 2014-2-20: 实现基本功能
 */
public class AMapSearchClient extends BaseMapSearchClient {
    public static final String TAG = "AMapSearch";

    private static String offsetPath = "http://api.zdoz.net/transgps.aspx";
    private static final String KEY_LAT = "Lat";
    private static final String KEY_LNG = "Lng";

    public AMapSearchClient(Context context) {
        super(context);
        Logger.d(TAG, "AMapSearchClient init ...");
    }

    @Override
    public void startSearch(PoiDataModel poiDataModel, IPoiListener poiListener) {
        super.startSearch(poiDataModel, poiListener);

        Logger.d(TAG,
                "searchAMapPOIAsyn latitude : " + poiDataModel.getLatitude() + ", lontitude : "
                        + poiDataModel.getLontitude() + ", city : " + poiDataModel.getCity()
                        + ", poi : " + poiDataModel.getPoi());

        if (TextUtils.isEmpty(poiDataModel.getCity())) {
            Logger.e(TAG, "param city can't be empty!");
        }
        Query query =
                new Query(poiDataModel.getPoi(), poiDataModel.getCategory(), poiDataModel.getCity());
        searchPOIAsyn(query, poiListener);
    }

    private OnPoiSearchListener mPOISearchListener = new OnPoiSearchListener() {

        @Override
        public void onPoiSearched(PoiResult poiResult, int rCode) {
            if (isTimeout() || isCanceled()) {
                return;
            }
            Logger.d(TAG, "onPoiSearched:poiResult " + poiResult + ",rCode " + rCode);
            onSearchResultReach();
            ErrorUtil errorUtil = null;
            List<PoiInfo> searchResult = null;
            if (rCode == 0) {
                if (poiResult == null || poiResult.getPois() == null
                        || poiResult.getPois().size() == 0) {
                    Logger.d(TAG, "poiResult is null");
                    errorUtil = DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.RETURN_NORESULT);
                } else {
                    ArrayList<PoiItem> poiItems = poiResult.getPois();
                    if (poiItems != null && poiItems.size() > 0) {
                        searchResult = new ArrayList<PoiInfo>();
                        for (PoiItem item : poiItems) {
                            searchResult.add(parse2POIInfo(item));
                        }
                    }
                }
            } else {
                Logger.e(TAG, "Search poi failed! rCode:" + rCode);
                errorUtil = DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.RETURN_NORESULT);
            }

            if (mPoiListener != null) {
                mPoiListener.onPoiSearchResult(searchResult, errorUtil);
            }
        }

        @Override
        public void onPoiItemDetailSearched(PoiItemDetail result, int rCode) {
            if (isTimeout() || isCanceled()) {
                return;
            }
            Logger.d(TAG, "onPoiItemDetailSearched:result " + result + ",rCode " + rCode);
            onSearchResultReach();
        }
    };

    private OnGeocodeSearchListener mGeocodeSearchListener = new OnGeocodeSearchListener() {

        @Override
        public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
            if (isTimeout() || isCanceled()) {
                return;
            }
            Logger.d(TAG, "onRegeocodeSearched:result " + result + ",rCpde " + rCode);
            onSearchResultReach();
            ErrorUtil error = null;
            LocationInfo locationInfo = null;
            ArrayList<LocationInfo> locationInfos = null;
            if (rCode == 0) {
                if (result == null || result.getRegeocodeAddress() == null) {} else {
                    RegeocodeQuery query = result.getRegeocodeQuery();
                    locationInfo =
                            parse2LocationInfo(result.getRegeocodeAddress(), query == null
                                    ? null
                                    : query.getPoint());
                }
            } else {
                Logger.e(TAG, "Search poi failed! rCode:" + rCode);
                error = DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.RETURN_EXCEPTION_RESULT);
            }
            if (mLocationListener != null) {
                if (locationInfo != null) {
                    locationInfos = new ArrayList<LocationInfo>();
                    locationInfos.add(locationInfo);
                }
                mLocationListener.onLocationResult(locationInfos, error);
            }
        }

        @Override
        public void onGeocodeSearched(GeocodeResult result, int rCode) {
            if (isTimeout() || isCanceled()) {
                return;
            }
            Logger.d(TAG, "onGeocodeSearched: result " + result + ", rCpde " + rCode);
            onSearchResultReach();
            ErrorUtil error = null;
            ArrayList<LocationInfo> locationInfos = null;
            if (rCode == 0) {
                if (result == null || result.getGeocodeAddressList() == null
                        || result.getGeocodeAddressList().size() == 0) {} else {
                    locationInfos = new ArrayList<LocationInfo>();
                    for (GeocodeAddress address : result.getGeocodeAddressList()) {
                        LocationInfo locationInfo = parse2LocationInfo(address);
                        locationInfos.add(locationInfo);
                    }
                }
            } else {
                Logger.e(TAG, "Geocode failed! rCode: " + rCode);
                error = DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.RETURN_EXCEPTION_RESULT);
            }
            if (mLocationListener != null) {
                mLocationListener.onLocationResult(locationInfos, error);
            }
        }
    };


    public void searchPOIAsyn(Query query, IPoiListener listener) {
        Logger.d(TAG, "searchPOIAsyn:query " + query + ",listener " + listener);
        mPoiListener = listener;
        PoiSearch poiSearch = new PoiSearch(mContext, query);
        poiSearch.setOnPoiSearchListener(mPOISearchListener);
        requestSearch(new Runnable() {
            @Override
            public void run() {
                if (mPoiListener != null) {
                    mPoiListener.onPoiSearchResult(null,
                            DataModelErrorUtil.getErrorUtil(DataModelErrorUtil.SEARCH_POI_TIMEOUT));
                }
            }
        });
        poiSearch.searchPOIAsyn();
    }

    public List<PoiInfo> searchPOI(Query query, SearchBound bound) {
        Logger.d(TAG, "searchPOI:query " + query + ",bound " + bound);

        Looper.prepare();
        PoiSearch poiSearch = new PoiSearch(mContext, query);
        Looper.loop();
        poiSearch.setBound(bound);
        try {
            PoiResult poiResult = poiSearch.searchPOI();
            if (poiResult != null) {
                ArrayList<PoiItem> poiItems = poiResult.getPois();
                if (poiItems != null && poiItems.size() > 0) {
                    List<PoiInfo> poiInfos = new ArrayList<PoiInfo>();
                    for (PoiItem item : poiItems) {
                        poiInfos.add(parse2POIInfo(item));
                    }
                    return poiInfos;
                }
            }
        } catch (AMapException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PoiInfo parse2POIInfo(PoiItem item) {
        PoiInfo info = new PoiInfo();
        info.setId(item.getPoiId());
        info.setName(item.getTitle());
        info.setTel(item.getTel());
        info.setDistance(item.getDistance());
        info.setUrl(item.getWebsite());
        info.setEmail(item.getEmail());
        info.setPostCode(item.getPostcode());
        info.setTypeDes(item.getTypeDes());
        info.setAddress(item.getSnippet());

        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setProvince(item.getProvinceName());
        locationInfo.setCityCode(item.getCityCode());
        locationInfo.setCity(item.getCityName());
        locationInfo.setAddress(item.getSnippet());

        LatLonPoint point = item.getLatLonPoint();
        if (point != null) {
            locationInfo.setLatitude(point.getLatitude());
            locationInfo.setLongitude(point.getLongitude());
        }
        info.setLocationInfo(locationInfo);
        return info;
    }

    private static LocationInfo parse2LocationInfo(RegeocodeAddress address, LatLonPoint point) {
        if (address == null) {
            return null;
        }
        LocationInfo info = new LocationInfo();
        info.setType(LocationInfo.GPS_GAODE);
        if (point != null) {
            info.setLatitude(point.getLatitude());
            info.setLongitude(point.getLongitude());
        }

        info.setProvince(address.getProvince());
        info.setCity(address.getCity());
        info.setDistrict(address.getDistrict());
        info.setName(address.getBuilding());
        info.setAddress(address.getFormatAddress());

        return info;
    }

    private static LocationInfo parse2LocationInfo(GeocodeAddress geocodeAddress) {
        if (geocodeAddress == null) {
            return null;
        }
        LocationInfo info = new LocationInfo();
        info.setType(LocationInfo.GPS_GAODE);
        info.setName(geocodeAddress.getBuilding());
        info.setProvince(geocodeAddress.getProvince());
        info.setCity(geocodeAddress.getCity());
        info.setCityCode(geocodeAddress.getAdcode());
        info.setAddress(geocodeAddress.getFormatAddress());
        info.setDistrict(geocodeAddress.getDistrict());

        LatLonPoint point = geocodeAddress.getLatLonPoint();
        if (point != null) {
            info.setLatitude(point.getLatitude());
            info.setLongitude(point.getLongitude());
        } else {
            Logger.w(TAG, "parse2LocationInfo: LatLonPoint null!");
        }
        return info;
    }

    public void getFromLocationAsyn(RegeocodeQuery query, ILocationListener listener) {
        Logger.d(TAG, "getFromLocationAsyn: query " + query + ", listener " + listener);
        mLocationListener = listener;
        GeocodeSearch geocodeSearch = new GeocodeSearch(mContext);
        geocodeSearch.setOnGeocodeSearchListener(mGeocodeSearchListener);
        requestSearch(new Runnable() {

            @Override
            public void run() {
                if (mLocationListener != null) {
                    mLocationListener.onLocationResult(null, DataModelErrorUtil
                            .getErrorUtil(DataModelErrorUtil.SEARCH_LOCATION_TIMEOUT));
                }
            }
        });
        geocodeSearch.getFromLocationAsyn(query);
    }

    public LocationInfo getFromLocation(RegeocodeQuery query) {
        Logger.d(TAG, "getFromLocation:query " + query);
        Looper.prepare();
        GeocodeSearch geocodeSearch = new GeocodeSearch(mContext);
        Looper.loop();
        try {
            RegeocodeAddress result = geocodeSearch.getFromLocation(query);
            return parse2LocationInfo(result, query.getPoint());
        } catch (AMapException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getFromLocationNameAsyn(GeocodeQuery query, ILocationListener listener) {
        Logger.d(TAG, "getFromLocationNameAsyn: query " + query + ", listener " + listener);
        mLocationListener = listener;
        GeocodeSearch geocodeSearch = new GeocodeSearch(mContext);
        geocodeSearch.setOnGeocodeSearchListener(mGeocodeSearchListener);
        requestSearch(new Runnable() {

            @Override
            public void run() {
                if (mLocationListener != null) {
                    mLocationListener.onLocationResult(null, DataModelErrorUtil
                            .getErrorUtil(DataModelErrorUtil.SEARCH_LOCATION_TIMEOUT));
                }
            }
        });
        geocodeSearch.getFromLocationNameAsyn(query);
    }

    public List<LocationInfo> getFromLocationName(GeocodeQuery query) {
        Logger.d(TAG, "getFromLocationName: query " + query);
        Looper.prepare();
        GeocodeSearch geocodeSearch = new GeocodeSearch(mContext);
        Looper.loop();
        ArrayList<LocationInfo> locationInfos = null;
        try {
            List<GeocodeAddress> addresses = geocodeSearch.getFromLocationName(query);
            locationInfos = null;
            if (addresses == null || addresses.size() == 0) {
                return null;
            } else {
                locationInfos = new ArrayList<LocationInfo>();
                for (GeocodeAddress address : addresses) {
                    LocationInfo locationInfo = parse2LocationInfo(address);
                    locationInfos.add(locationInfo);
                }
            }
        } catch (AMapException e) {
            e.printStackTrace();
        }

        return locationInfos;
    }

    // GPS纠偏
    public static LatLonPoint getOffsetGPS(double lat, double lng) {
        LatLonPoint info = null;

        String path = offsetPath + "?lat=" + lat + "&lng=" + lng;
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(path);
        try {
            HttpResponse res = client.execute(get);
            if (res.getStatusLine().getStatusCode() == 200) {
                String str = EntityUtils.toString(res.getEntity(), "UTF-8");
                JSONObject obj = new JSONObject(str);
                info =
                        new LatLonPoint(Double.parseDouble(obj.getString(KEY_LAT)),
                                Double.parseDouble(obj.getString(KEY_LNG)));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }

    @Override
    public void stopSearch() {
        Logger.d(TAG, "stop");
        cancel();
        release();
    }

    public void release() {
        Logger.d(TAG, "release");
        super.release();
        mContext = null;
    }
}
