package com.aispeech.aios.adapter.vendor.GD;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.config.Configs.MapConfig;
import com.aispeech.aios.adapter.node.HomeNode;
import com.aispeech.aios.adapter.util.APPUtil;
import com.aispeech.aios.adapter.util.SendBroadCastUtil;


/**
 * @desc 高德地图接口类
 * @auth zzj
 * @date 2016-01-13
 */
public class GDOperate {
    private static final String TAG = "AIOS-Adapter-GDOperate";

    private Context context;
    private static GDOperate mInstance;

    public GDOperate(Context context) {
        this.context = context;
    }

    public static synchronized GDOperate getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new GDOperate(context);
        }
        return mInstance;
    }

    /**
     * 开始导航
     *
     * @param bean 导航目的地POI
     */
    public void startNavigation(PoiBean bean) {
//        this.openVioce();//打开路口语音播报
//        this.checkGDReNavigation();//删除poi遗留  
    	//铁站-C口","latitude":22.575425,"longitude":113.876578,"distance":1375,"tel":""},{"name":"芝麻街(坪洲地铁店
    	 if (APPUtil.getInstance().isInstalled(Configs.MapConfig.PACKAGE_GDMAP)) {
            String lat = String.valueOf(bean.getLatitude());
            String lon = String.valueOf(bean.getLongitude());
            String cat = "android.intent.category.DEFAULT";
            String dat = "androidamap://navi?sourceApplication=AIOS-Adapter&lat=" + lat + "&lon=" + lon + "&dev=0&style=2";
            String pkg = "com.autonavi.minimap";
            Intent i = new Intent();
            i.setData(android.net.Uri.parse(dat));
            i.setPackage(pkg);
            i.addCategory(cat);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(i);
        } else {
            Toast.makeText(context, "请先安装高德地图", Toast.LENGTH_LONG).show();
            HomeNode.getInstance().getBusClient().publish(AiosApi.Other.UI_CLICK);//停掉交互
        }
    }


    /**
     * 退出高德
     */
    public void closeGDMap() {//强制结束掉高德APP，需要root权限

        closeVoice();
        context.sendBroadcast(new Intent("com.amap.stopnavi"));
        Intent mIntent = new Intent("com.autonavi.minimap.carmode.command");
		mIntent.putExtra("NAVI", "APP_EXIT");
		context.sendBroadcast(mIntent);
        try {
        	//changed by ydj on 2016.3.24
       //     APPUtil.getInstance(context).kill(MapConfig.PACKAGE_GDMAP);
        	APPUtil.getInstance().forceStopPackage(MapConfig.PACKAGE_GDMAP);
        	//changed by ydj on 2016.3.24
        } catch (Exception e) {
            e.printStackTrace();
            AILog.d(TAG, "结束高德地图失败");
        }

    }
    
	public void closeCarMap() {// 强制结束掉高德APP，需要root权限
		
		if (APPUtil.getInstance().isInstalled(Configs.MapConfig.PACKAGE_GDMAPFORCATJ)) {
    		Intent mIntent = new Intent("AUTONAVI_STANDARD_BROADCAST_RECV");
    		mIntent.putExtra("KEY_TYPE",10010);
    		context.sendBroadcast(mIntent);

    		new Handler().postDelayed(new Runnable() {

    			@Override
    			public void run() {
    				try {
    					 APPUtil.getInstance().forceStopPackage(Configs.MapConfig.PACKAGE_GDMAPFORCATJ);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    		}, 1000);

    	}
    	if (APPUtil.getInstance().isInstalled(Configs.MapConfig.PACKAGE_GDMAPFORCAT)) {
    		Intent intent = new Intent("AUTONAVI_STANDARD_BROADCAST_RECV");
    		intent.putExtra("KEY_TYPE", 10021);
    		context.sendBroadcast(intent);
        	
        	new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                	 try {
                         APPUtil.getInstance().forceStopPackage(Configs.MapConfig.PACKAGE_GDMAPFORCAT);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                }
            }, 1000);
    	}

	}
    
    public void closeMap() {// 强制结束掉高德APP，需要root权限
			closeCarMap();
		if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)) {
			closeGDMap();
		}

	}

    /**
     * 当前位置，并在地图定位
     */
    public void locate() {

        String act = "android.intent.action.VIEW";
        String cat = "android.intent.category.DEFAULT";
        String data = "androidamap://myLocation?sourceApplication=AIOS-Adapter";
        String pkg = "com.autonavi.minimap";
        Intent i = new Intent();
        i.setAction(act);
        i.addCategory(cat);
        i.setData(android.net.Uri.parse(data));
        i.setPackage(pkg);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);

    }

    /**
     * 打开地图操作
     */
    public void openMap() {
        APPUtil.getInstance().openApplication(Configs.MapConfig.PACKAGE_GDMAP);
    }

    /**
     * 关闭路口语音播报
     */
    public void closeVoice() {
        SendBroadCastUtil.getInstance().sendBroadCast("com.amap.closeroadradio", null, null);
    }

    /**
     * 打开语音播报
     */
    public void openVioce() {
        SendBroadCastUtil.getInstance().sendBroadCast("com.amap.openroadradio", null, null);
    }
}
