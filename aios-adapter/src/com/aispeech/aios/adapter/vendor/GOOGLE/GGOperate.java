package com.aispeech.aios.adapter.vendor.GOOGLE;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.config.Configs.MapConfig;
import com.aispeech.aios.adapter.util.APPUtil;

/**
 * @desc 凯立德接口类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class GGOperate {
    private static final String TAG = "AIOS-Adapter-KLDOperate";

    private Context mContext;
    private static GGOperate mInstance;

    public GGOperate(Context context) {
        this.mContext = context;
    }

    public static synchronized GGOperate getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new GGOperate(context);
       }
        return mInstance;
    }

    
    /**
     * 开始导航
     * @param bean 导航目的地POI
     */
    public void startNavigation(PoiBean bean) {
    	Uri gmmIntentUri = Uri.parse("google.navigation:q=" + bean.getLatitude() + "," + bean.getLongitude()
				+ "&mode=d");
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
		mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mapIntent.setPackage(MapConfig.PACKAGE_GGMAP);
		try {
			mContext.startActivity(mapIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 退出google
     */
    public void closeMap() {//强制结束掉百度地图APP，需要root权限
        try {
            APPUtil.getInstance().forceStopPackage(Configs.MapConfig.PACKAGE_GGMAP);
        } catch (Exception e) {
            e.printStackTrace();
            AILog.d(TAG, "结束谷歌地图失败");
        }
    }

    
    /**
     * 打开地图操作
     */
    public void openMap() {
        APPUtil.getInstance().openApplication(Configs.MapConfig.PACKAGE_GGMAP);
    }


}
