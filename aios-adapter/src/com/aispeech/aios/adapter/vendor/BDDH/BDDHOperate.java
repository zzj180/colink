package com.aispeech.aios.adapter.vendor.BDDH;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.util.APPUtil;

/**
 * @desc 这是百度导航的Operate
 * @auth AISPEECH
 * @date 2016-01-12
 * @copyright aispeech.com
 */

public class BDDHOperate {
    private static final String TAG = "AIOS-Adapter-BDDHOperate";

    private Context context;
    private static BDDHOperate mInstance;


    public BDDHOperate(Context context) {
        this.context = context;
    }

    public static synchronized BDDHOperate getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new BDDHOperate(context);
        }
        return mInstance;
    }

    /**
     * 开始导航
     */
    public void startNavigation(PoiBean bean) {

        Uri uri = Uri.parse("bdnavi://plan?&dest=" +
                bean.getLatitude() +
                "," +
                bean.getLongitude());
        Intent intent = new Intent("com.baidu.navi.action.START", uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (APPUtil.getInstance().isInstalled("com.baidu.navi")) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "请先安装百度导航", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 当前位置
     */
    public void locate() {

        Uri uri = Uri.parse("bdnavi://where");
        Intent i = new Intent("com.baidu.navi.action.START", uri);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (APPUtil.getInstance().isInstalled("com.baidu.navi")) {
            context.startActivity(i);

        } else {
            Toast.makeText(context, "请先安装百度导航", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 退出百度导航
     */
    public void closeMap() {
    	context.sendBroadcast(new Intent("com.baidu.navi.quitnavi"));
        try {
     //       APPUtil.getInstance().kill(MapConfig.PACKAGE_BDDH);
        	APPUtil.getInstance().forceStopPackage(Configs.MapConfig.PACKAGE_BDDH);
        } catch (Exception e) {
            e.printStackTrace();
            AILog.d(TAG, "结束百度导航失败");
        }

    }

    /**
     * 打开百度导航
     */
    public void openMap() {
        Uri uri = Uri.parse("bdnavi://launch");
        Intent i = new Intent("com.baidu.navi.action.START", uri);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
