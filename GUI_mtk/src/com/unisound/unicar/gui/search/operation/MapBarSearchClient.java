package com.unisound.unicar.gui.search.operation;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.unisound.unicar.gui.data.operation.PoiDataModel;
import com.unisound.unicar.gui.model.LocationInfo;
import com.unisound.unicar.gui.model.PoiInfo;
import com.unisound.unicar.gui.search.interfaces.IPoiListener;
import com.unisound.unicar.gui.utils.DataModelErrorUtil;
import com.unisound.unicar.gui.utils.ErrorUtil;
import com.unisound.unicar.gui.utils.Logger;

public class MapBarSearchClient extends BaseMapSearchClient {
    private final String TAG = "MapBarSearch";

    private Context mContext;
    private String mapBar_data;
    private IPoiListener mIPoiListener;

    private List<PoiInfo> infos;

    private BroadcastReceiver receiver = null;

    public MapBarSearchClient(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        Logger.d(TAG, "MapBarSearchClient init ...");
    }

    @Override
    public void startSearch(PoiDataModel poiDataModel, IPoiListener poiListener) {
        super.startSearch(poiDataModel, poiListener);
        Logger.d(TAG,
                "searchMapBarPOIAsyn latitude : " + poiDataModel.getLatitude() + ", lontitude : "
                        + poiDataModel.getLontitude() + ", city : " + poiDataModel.getCity()
                        + ", poi : " + poiDataModel.getPoi());

        if (TextUtils.isEmpty(poiDataModel.getCity())) {
            Logger.e(TAG, "param city can't be empty!");
        }

        requestSearch(new Runnable() {
            @Override
            public void run() {
                stopSearch();
                ErrorUtil errorUtil =
                        new ErrorUtil(DataModelErrorUtil.SEARCH_POI_TIMEOUT, "搜索位置信息超时");
                mIPoiListener.onPoiSearchResult(null, errorUtil);
            }
        });

        searchPOIAsyn(poiDataModel.getCity(), poiDataModel.getPoi(), poiListener);
    }

    public void searchPOIAsyn(String city, String poi, IPoiListener poiListener) {
        Logger.d(TAG, "city : " + city + " poi : " + poi);
        mIPoiListener = poiListener;
        registerBroadcast();
        sendMsg2MapBar(city, poi);
    }

    /*
     * 注册广播
     */
    private void registerBroadcast() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mapBar_data = intent.getExtras().getString("mabar_data");
                Logger.d(TAG, "mapBar_data = " + mapBar_data);

                try {
                    infos = parse2Info(mapBar_data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Logger.d(TAG, "infos = " + infos);

                // 正常返回
                if (infos != null) {
                    onSearchResultReach();
                    mIPoiListener.onPoiSearchResult(infos, null);
                }
                // 错误返回
                else {
                    stopSearch();
                    ErrorUtil errorUtil =
                            new ErrorUtil(DataModelErrorUtil.SEARCH_POI_TIMEOUT, "搜索位置信息超时");
                    mIPoiListener.onPoiSearchResult(infos, errorUtil);
                }

            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.aciton.mapbar.search.data");
        mContext.registerReceiver(receiver, filter);
    }


    private void unRegisterBroadcast() {
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }

    }

    /*
     * 给图吧发广播
     */
    private void sendMsg2MapBar(String mCity, String mPoi) {
        Intent mIntent = new Intent();
        mIntent.putExtra("navi_city", mCity);
        mIntent.putExtra("navi_keyword", mPoi);
        mIntent.setAction("android.intent.aciton.send.keyword.to.mapbar");
        mContext.sendBroadcast(mIntent);
        Logger.d(TAG, "Msg : [" + mIntent.toString() + "] has sent to MapBar ");
    }

    private List<PoiInfo> parse2Info(String mapBar_data) throws JSONException {
        JSONObject jsonContent = new JSONObject(mapBar_data);
        List<PoiInfo> resultList = new ArrayList<PoiInfo>();

        JSONArray jsonArray = jsonContent.getJSONArray("locations");
        for (int i = 0; i < jsonArray.length(); i++) {
            PoiInfo poiSearchResult = new PoiInfo();

            JSONObject item = (JSONObject) jsonArray.get(i);
            String adress = item.getString("address");
            String name = item.getString("name");
            double lng = item.getDouble("lng");
            double lat = item.getDouble("lat");

            LocationInfo info = new LocationInfo();
            info.setAddress(adress);
            info.setName(name);
            info.setLongitude(lng);
            info.setLatitude(lat);
            poiSearchResult.setName(name);
            poiSearchResult.setLocationInfo(info);

            resultList.add(poiSearchResult);
        }
        return resultList;
    }

    @Override
    public void stopSearch() {
        super.stopSearch();
        unRegisterBroadcast();
    }

    @Override
    public void release() {
        super.release();
        unRegisterBroadcast();
    }
}
