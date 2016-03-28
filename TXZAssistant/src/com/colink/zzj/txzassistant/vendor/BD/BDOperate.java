package com.colink.zzj.txzassistant.vendor.BD;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.colink.zzj.txzassistant.oem.RomSystemSetting;
import com.colink.zzj.txzassistant.util.APPUtil;

import java.net.URISyntaxException;


/**
 * @desc 百度接口类
 * @auth zzj
 * @date 2016-03-17
 */
public class BDOperate {
    private Context context;
    private static BDOperate mInstance;

    public BDOperate(Context context) {
        this.context = context;
    }

    public static synchronized BDOperate getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new BDOperate(context);
        }
        return mInstance;
    }

    /**
     * 开始导航
     *
     * @param endBean 目的地poi信息
     */
    @SuppressWarnings("deprecation")
	public void startNavigation(String slat,String slon,String address,double lat,double lon,String name) {
    	
        //调起百度地图客户端
        try {
            Intent i = Intent.getIntent("intent://map/direction?origin="
                    + "name:" + address
                    + "|latlng:" + slat
                    + "," + slon
                    + "&destination="
                    + "name:" + name
                    + "|latlng:" + lat
                    + "," + lon
                    + "&mode=driving"
                    + "&coord_type=bd09"
                    + "&src=AISPEECH|AIOS-Adapter#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (RomSystemSetting.isInstalled(context,APPUtil.BD_MAP_PKG)) {
                context.startActivity(i);
            } else {
                Toast.makeText(context, "请先安装百度地图", Toast.LENGTH_LONG).show();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 当前位置
     */
    @SuppressWarnings("deprecation")
	public void locate(String lat,String lon,String name) {
        try {
            Intent i = Intent.getIntent("intent://map/marker?location=" + lat + "," + lon +
                    "&title=我的位置" +
                    "&content=" + name +
                    "&src=AISPEECH|AIOS-Adapter#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end ");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (RomSystemSetting.isInstalled(context,APPUtil.BD_MAP_PKG)) {
                context.startActivity(i);
            } else {
                Toast.makeText(context, "请先安装百度地图", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
	 * 退出百度地图
	 */
	public void closeMap() {
		try {
			RomSystemSetting.forceStopPackage(APPUtil.BD_MAP_PKG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
