package com.unisound.unicar.gui.search.operation;

import android.content.Context;
import com.unisound.unicar.gui.data.operation.PoiDataModel;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.search.interfaces.IPoiListener;
import com.unisound.unicar.gui.search.interfaces.ISearch;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Author : zhuoran
 * @CreateDate : 2015-07-22
 * @Modified: xiaodong
 * @ModifiedDate : 20150727 2015-07-22: 实现基本功能
 */
public class POISearchModelProxy implements ISearch {
    private static final String TAG = "POISearchModelProxy";

    public static POISearchModelProxy mInstance = null;

    private POISearchModelProxy() {};

    protected static Object mObjLock = new Object();
    private static BaseMapSearchClient mMapSearchClient;
    // private static Context mContext; //XD delete 20150727
    private IPoiListener mPoiListener;

    private final static int AMAP_INDEX = 1;
    private final static int BAIDU_INDEX = 2;
    private final static int MAPBAR_INDEX = 3;
    private final static int RITU_INDEX = 4;

    /**
     * This code maybe course
     * 
     * @param context
     * @return
     */
    public static synchronized POISearchModelProxy getInstance(Context context) {
        synchronized (mObjLock) {
            Logger.d(TAG, "!--->getInstance new POISearchModelProxy");
            mInstance = new POISearchModelProxy();

            initMapSearchClient(context);
        }
        return mInstance;
    }

    @Override
    public void startSearch(PoiDataModel poiDataModel, IPoiListener poiListener) {
        this.mPoiListener = poiListener;
        mMapSearchClient.startSearch(poiDataModel, mPoiListener);
    }

    private static void initMapSearchClient(Context mContext) {
        mMapSearchClient = createMapSearchClient(mContext);
    }

    private static BaseMapSearchClient createMapSearchClient(Context mContext) {
     /*   int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
        switch (mapIndex) {
            case AMAP_INDEX:
                Logger.d(TAG, "Create AmapSearch Client !!");
                return new AMapSearchClient(mContext);
            case BAIDU_INDEX:
                Logger.d(TAG, "Create BaiduSearch Client !!");
                return new BaiduSearchClient(mContext);
            case MAPBAR_INDEX:
                Logger.d(TAG, "Create MapBarSearch Client !!");
                return new MapBarSearchClient(mContext);
            case RITU_INDEX:
                Logger.d(TAG, "Create RituSearch Client !!");
                return new RituSearchClient(mContext);
            default:
                return new AMapSearchClient(mContext);
        }*/
    	return new AMapSearchClient(mContext);
    }

    public void stop() {
        Logger.d(TAG, "stop");
        relaseMapSearch();
    }

    private void relaseMapSearch() {
        Logger.d(TAG, "relaseMapSearch");
        if (mMapSearchClient != null) {
            mMapSearchClient.release();
            mMapSearchClient = null;
            mPoiListener = null;
        }
    }

    @Override
    public void init() {}

    @Override
    public void stopSearch() {
        mMapSearchClient.stopSearch();
    }

    @Override
    public void release() {
        mMapSearchClient.release();
        mMapSearchClient = null;
    }
}
