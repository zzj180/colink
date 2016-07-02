package com.aispeech.aios.adapter.vendor.KLD;

import android.content.Context;
import android.content.Intent;

import com.aispeech.aios.adapter.bean.PoiBean;

/**
 * @desc 凯立德接口类
 * @auth zzj
 * @date 2016-01-13
 */
public class KLDOperate {
    private static final String TAG = "AIOS-Adapter-KLDOperate";

    private Context mContext;
    private static KLDOperate mInstance;

    public KLDOperate(Context context) {
        this.mContext = context;
    }

    public static synchronized KLDOperate getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new KLDOperate(context);
       }
        return mInstance;
    }

    /**
     * 开始导航
     * @param bean 导航目的地POI
     */
    public void startNavigation(PoiBean bean) {
        Intent i = new Intent("android.NaviOne.CldStdTncReceiver");
        i.putExtra("CLDTNC","(TNC01,D"+bean.getLatitude()+","+bean.getLongitude()+","+bean.getName()+")");
        mContext.sendBroadcast(i);
    }


    /**
     * 退出凯立德
     */
    public void closeMap() {
        Intent i = new Intent("android.NaviOne.AutoExitReceiver");
        mContext.sendBroadcast(i);
    }

    /**
     * 当前位置，并在地图定位
     */
    public void locate() {
        Intent i = new Intent("android.NaviOne.AutoStartReceiver");
        i.putExtra("CMD", "Start");
        mContext.sendBroadcast(i);
    }

    /**
     * 打开地图操作
     */
    public void openMap(){
        Intent i = new Intent("android.NaviOne.AutoStartReceiver");
        i.putExtra("CMD", "Start");
        mContext.sendBroadcast(i);
    }

}
