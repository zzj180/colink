package com.aispeech.aios.adapter.vendor.BD;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.util.APPUtil;
import com.aispeech.aios.adapter.util.LocationUtil;

import java.net.URISyntaxException;


/**
 * @desc 百度接口类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class BDOperate {
    private static final String TAG = "AIOS-Adapter-BDOperate";
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
    public void startNavigation(PoiBean endBean) {
        //调起百度地图客户端
        try {
            PoiBean p = LocationUtil.getLocation();
            Intent i = Intent.getIntent("intent://map/direction?origin="
                    + "name:" + p.getAddress()
                    + "|latlng:" + String.valueOf(p.getLatitude())
                    + "," + String.valueOf(p.getLongitude())
                    + "&destination="
                    + "name:" + endBean.getName()
                    + "|latlng:" + String.valueOf(endBean.getLatitude())
                    + "," + String.valueOf(endBean.getLongitude())
                    + "&mode=driving"
                    + "&coord_type=bd09"
                    + "&src=AISPEECH|AIOS-Adapter#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (APPUtil.getInstance().isInstalled("com.baidu.BaiduMap")) {
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
    public void locate() {

        try {
            PoiBean p = LocationUtil.getLocation();
            Intent i = Intent.getIntent("intent://map/marker?location=" + String.valueOf(p.getLatitude()) + "," + String.valueOf(p.getLongitude()) +
                    "&title=我的位置" +
                    "&content=" + p.getAddress() +
                    "&src=AISPEECH|AIOS-Adapter#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end ");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (APPUtil.getInstance().isInstalled("com.baidu.BaiduMap")) {
                context.startActivity(i);
            } else {
                Toast.makeText(context, "请先安装百度地图", Toast.LENGTH_LONG).show();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出百度地图
     */
    public void closeMap() {//强制结束掉百度地图APP，需要root权限
        try {
            APPUtil.getInstance().forceStopPackage(Configs.MapConfig.PACKAGE_BDMAP);
        } catch (Exception e) {
            e.printStackTrace();
            AILog.d(TAG, "结束百度地图失败");
        }
    }

    /**
     * 打开地图操作
     */
    public void openMap() {
        APPUtil.getInstance().openApplication(Configs.MapConfig.PACKAGE_BDMAP);
    }

}
