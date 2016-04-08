package com.aispeech.aios.adapter.vendor.MX;

import android.content.Context;
import android.content.Intent;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.util.APPUtil;

/**
 * @desc 凯立德接口类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class MXOperate {
    private static final String TAG = "AIOS-Adapter-KLDOperate";

    private Context mContext;
    private static MXOperate mInstance;

    public MXOperate(Context context) {
        this.mContext = context;
    }

    public static synchronized MXOperate getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new MXOperate(context);
       }
        return mInstance;
    }

    /**
     * 开始导航
     * @param bean 导航目的地POI
     */
    public void startNavigation(PoiBean bean) {
    	Intent i = new Intent("com.mxnavi.mxnavi.PTT_DEST_ACTION");
		String dest_string = "(TND,2,0, " + bean.getLatitude()+","+bean.getLongitude()+","+bean.getName() + ")";
		i.putExtra("data", dest_string);
		i.setFlags(32);
		mContext.sendBroadcast(i);
    }

    /**
     * 退出美行
     */
    public void closeMap() {//强制结束掉百度地图APP，需要root权限
        try {
            APPUtil.getInstance().forceStopPackage(Configs.MapConfig.PACKAGE_MXMAP);
        } catch (Exception e) {
            e.printStackTrace();
            AILog.d(TAG, "结束美行地图失败");
        }
    }

    /**
     * 打开地图操作
     */
    public void openMap() {
        APPUtil.getInstance().openApplication(Configs.MapConfig.PACKAGE_MXMAP);
    }


}
