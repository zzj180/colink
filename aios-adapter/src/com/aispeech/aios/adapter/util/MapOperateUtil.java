package com.aispeech.aios.adapter.util;

import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;

import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.config.Configs.MapConfig;
import com.aispeech.aios.adapter.vendor.BD.BDOperate;
import com.aispeech.aios.adapter.vendor.BDDH.BDDHOperate;
import com.aispeech.aios.adapter.vendor.GD.GDOperate;
import com.aispeech.aios.adapter.vendor.GDCAR.GDCAROperator;
import com.aispeech.aios.adapter.vendor.GOOGLE.GGOperate;
import com.aispeech.aios.adapter.vendor.KLD.KLDOperate;
import com.aispeech.aios.adapter.vendor.MX.MXOperate;
import com.aispeech.aios.adapter.vendor.TB.TBOperate;

/**
 * @desc 第三方APP操作工具类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class MapOperateUtil {

    private static MapOperateUtil mUtil = null;
    private String MAP_INDEX = "MAP_INDEX";
    private Context mContext;

    public MapOperateUtil() {
        this.mContext = AdapterApplication.getContext();
    }

    /**
     * @return ThirdAPPOperateUtil实例
     */
    public static MapOperateUtil getInstance() {

        if (null == mUtil) {
            mUtil = new MapOperateUtil();
        }
        return mUtil;
    }

    /**
     * 简单的打开地图操作
     */
    public void openMap() {
        int mapType = Settings.System.getInt(mContext.getContentResolver(),MAP_INDEX, MapConfig.BDDH);

            switch (mapType) {
                case Configs.MapConfig.GDMAP:
                	if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAPFORCAT)){
                		GDCAROperator.getInstance(mContext).openMap();
                	}else if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)){
                		 GDOperate.getInstance(mContext).openMap();
                	}
                   
                    break;
                case Configs.MapConfig.BDDH:
                    BDDHOperate.getInstance(mContext).openMap();
                    break;
                case Configs.MapConfig.KLDMAP:
                    KLDOperate.getInstance(mContext).openMap();
                    break;
                case Configs.MapConfig.MXMAP:
                    MXOperate.getInstance(mContext).openMap();
                    break;
                case Configs.MapConfig.GGMAP:
                    GGOperate.getInstance(mContext).openMap();
                    break;
                case Configs.MapConfig.BDMAP:
                	BDOperate.getInstance(mContext).openMap();
                	break;
                case Configs.MapConfig.TBMAP:
                	TBOperate.getInstance(mContext).openMap();
                	break;
                case Configs.MapConfig.GDMAPFORCAT:
                    GDCAROperator.getInstance(mContext).openMap();
                    break;
            }
    }

    /**
     * 关闭导航
     */
    public void closeMap() {
    	 int mapType = Settings.System.getInt(mContext.getContentResolver(),MAP_INDEX, MapConfig.BDDH);

            switch (mapType) {
            case Configs.MapConfig.GDMAP:
            	if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAPFORCAT)){
            		GDCAROperator.getInstance(mContext).closeMap();
            	}
            	if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)){
            		 GDOperate.getInstance(mContext).closeMap();
            	}
                break;
            case Configs.MapConfig.BDDH:
                BDDHOperate.getInstance(mContext).closeMap();
                break;
            case Configs.MapConfig.KLDMAP:
                KLDOperate.getInstance(mContext).closeMap();
                break;
            case Configs.MapConfig.MXMAP:
                MXOperate.getInstance(mContext).closeMap();
                break;
            case Configs.MapConfig.GGMAP:
                GGOperate.getInstance(mContext).closeMap();
                break;
            case Configs.MapConfig.BDMAP:
            	BDOperate.getInstance(mContext).closeMap();
            	break;
            case Configs.MapConfig.TBMAP:
            	TBOperate.getInstance(mContext).closeMap();
            	break;
            case Configs.MapConfig.GDMAPFORCAT:
                GDCAROperator.getInstance(mContext).closeMap();
                break;
            }
        
    }

    /**
     * 当前位置，并跳转到地图显示
     */
    public void locateByMap() {
    	 int mapType = Settings.System.getInt(mContext.getContentResolver(),MAP_INDEX, MapConfig.BDDH);

       
            switch (mapType) {
            case Configs.MapConfig.GDMAP:
            	if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAPFORCAT)){
            		GDCAROperator.getInstance(mContext).locate();
            	}else if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)){
            		 GDOperate.getInstance(mContext).locate();
            	}else{
            		 Toast.makeText(mContext, "请先安装高德地图", Toast.LENGTH_LONG).show();
            	}
                GDOperate.getInstance(mContext).locate();
                break;
            case Configs.MapConfig.BDDH:
                BDDHOperate.getInstance(mContext).locate();
                break;
            case Configs.MapConfig.KLDMAP:
                KLDOperate.getInstance(mContext).locate();
                break;
            case Configs.MapConfig.MXMAP:
            	GDOperate.getInstance(mContext).locate();
                break;
            case Configs.MapConfig.GGMAP:
            	GDOperate.getInstance(mContext).locate();
                break;
            case Configs.MapConfig.BDMAP:
            	BDOperate.getInstance(mContext).locate();
            	break;
            case Configs.MapConfig.TBMAP:
            	TBOperate.getInstance(mContext).locate();
            	break;
            case Configs.MapConfig.GDMAPFORCAT:
                GDCAROperator.getInstance(mContext).locate();
                break;
            }
       
    }

    /**
     * 开始导航
     */
    public void startNavigation(PoiBean bean) {
    	 int mapType = Settings.System.getInt(mContext.getContentResolver(),MAP_INDEX, MapConfig.BDDH);

      
            switch (mapType) {
            case Configs.MapConfig.GDMAP:
            	if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAPFORCAT)){
            		GDCAROperator.getInstance(mContext).startNavigation(bean);
            	}else if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)){
            		GDOperate.getInstance(mContext).startNavigation(bean);
            	}else{
            		 Toast.makeText(mContext, "请先安装高德地图", Toast.LENGTH_LONG).show();
            	}
                break;
            case Configs.MapConfig.BDDH:
                BDDHOperate.getInstance(mContext).startNavigation(bean);
                break;
            case Configs.MapConfig.KLDMAP:
                KLDOperate.getInstance(mContext).startNavigation(bean);
                break;
            case Configs.MapConfig.MXMAP:
                MXOperate.getInstance(mContext).startNavigation(bean);
                break;
            case Configs.MapConfig.GGMAP:
                GGOperate.getInstance(mContext).startNavigation(bean);
                break;
            case Configs.MapConfig.BDMAP:
            	BDOperate.getInstance(mContext).startNavigation(bean);
            	break;
            case Configs.MapConfig.TBMAP:
            	TBOperate.getInstance(mContext).startNavigation(bean);
            	break;
            case Configs.MapConfig.GDMAPFORCAT:
                GDCAROperator.getInstance(mContext).startNavigation(bean);
                break;
            }
       
    }
}
