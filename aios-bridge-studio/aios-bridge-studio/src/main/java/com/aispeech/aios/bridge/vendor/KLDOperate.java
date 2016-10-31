package com.aispeech.aios.bridge.vendor;

import com.aispeech.aios.bridge.oem.RomSystemSetting;
import com.aispeech.aios.bridge.utils.APPUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * @desc 凯立德接口类
 * @auth zzj
 * @date 2016-03-17
 */
public class KLDOperate {

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



    public void startNavigation(double lat,double lon,String name) {
        Intent i = new Intent("android.NaviOne.CldStdTncReceiver");
        i.putExtra("CLDTNC","(TNC01,D"+lat+","+lon+","+name+")");
        mContext.sendBroadcast(i);
    }


    /**
     * 退出凯立德
     */
    public void closeMap() {
        Intent i = new Intent("android.NaviOne.AutoExitReceiver");
        mContext.sendBroadcast(i);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
				try {
					RomSystemSetting.forceStopPackage(APPUtil.KLD_MAP_PKG);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, 1000);
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
