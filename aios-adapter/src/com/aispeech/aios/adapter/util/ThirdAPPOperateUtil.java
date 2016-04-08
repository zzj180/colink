package com.aispeech.aios.adapter.util;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.util.Config;

import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.config.Configs.MapConfig;
import com.aispeech.aios.adapter.node.TTSNode;
import com.aispeech.aios.adapter.vendor.BD.BDOperate;
import com.aispeech.aios.adapter.vendor.BDDH.BDDHOperate;
import com.aispeech.aios.adapter.vendor.GD.GDOperate;
import com.aispeech.aios.adapter.vendor.KLD.KLDOperate;
import com.aispeech.aios.adapter.vendor.TB.TBOperate;

/**
 * @desc 第三方APP操作工具类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class ThirdAPPOperateUtil {

    private static final String TAG = "AIOS-Adapter-ThirdAPPOperateUtil";
    private static ThirdAPPOperateUtil mUtil = null;
    private String MAP_INDEX = "MAP_INDEX";

    private Context mContext;

    public ThirdAPPOperateUtil() {
        this.mContext = AdapterApplication.getContext();
    }

    public static ThirdAPPOperateUtil getInstance() {

        if (null == mUtil) {
            mUtil = new ThirdAPPOperateUtil();
        }
        return mUtil;
    }


    /**
     * 通过包名打开第三方APP
     *
     * @param packName 包名
     * @param APPName  APP名字
     */
    public void openThirdAPP(String packName, String APPName) {

        if (APPUtil.getInstance().isInstalled(packName)) {

            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setPackage(packName);
            List<ResolveInfo> resolveInfoList = mContext.getPackageManager().queryIntentActivities(i, 0);
            ResolveInfo resolveInfo = resolveInfoList.iterator().next();
            if (resolveInfo != null) {
                String activityPackageName = resolveInfo.activityInfo.packageName;
                String className = resolveInfo.activityInfo.name;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName componentName = new ComponentName(activityPackageName, className);
                intent.setComponent(componentName);
                mContext.startActivity(intent);
            }

        } else {
            TTSNode.getInstance().play("没有找到" + APPName + ",无法打开");
        }

    }

    /**
     * 通过包名和类型打开第三方的APP
     *
     * @param packName
     * @param className
     */
    public void openThirdAPP(String packName, String className, String APPName) {
        if (APPUtil.getInstance().isInstalled(packName)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(packName, className);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cn);
            mContext.startActivity(intent);
        } else {
            TTSNode.getInstance().play("没有找到" + APPName + ",无法打开");
        }

    }

   /* *//**
     * 简单的打开地图操作
     *//*
    public void openMap() {
    	int mapType = Settings.System.getInt(mContext.getContentResolver(), MAP_INDEX, Configs.MapConfig.GDMAP);
        if (mapType == 1) {
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)) {
                GDOperate.getInstance(mContext).openMap();
            } else {
                TTSNode.getInstance().play("没有找到图吧地图,无法打开，请先安装地图");
            }
        }  else if (mapType == MapConfig.KLDMAP) {
        	//AIMap.KLDMAP -> MapConfig.KLDMAP by ydj on 2016 , same after
            if (APPUtil.getInstance().isInstalled(PACKAGE_KLDMAP)) {
                KLDOperate.getInstance(mContext).openMap();
            } else {
                TTSNode.getInstance().play("没有找到高德地图,无法打开，请先安装地图");
            }
        } else if (mapType == 0) {//打开百度导航
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_BDDH)) {
                BDDHOperate.getInstance(mContext).openMap();
            } else {
                TTSNode.getInstance().play("没有找到百度地图,无法打开，请先安装地图");
            }
        }else if (mapType == 5) {
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_BDMAP)) {
                BDOperate.getInstance(mContext).openMap();
            } else {
                TTSNode.getInstance().play("没有找到凯立德地图,无法打开，请先安装地图");
            }
        }else if(mapType == MapConfig.TBMAP) {
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_TBMAP)) {
                TBOperate.getInstance(mContext).openMap();
            } else {
                TTSNode.getInstance().play("没有找到百度导航,无法打开，请先安装地图");
            }
        } 
    }

    *//**
     * 关闭导航
     *//*
    public void closeMap() {

        if(AdapterApplication.mapType == 1) {
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)) {
                GDOperate.getInstance(mContext).closeMap();
            } else {
                TTSNode.getInstance().play("没有找到图吧地图,无法关闭");
            }
        }  else if (AdapterApplication.mapType == MapConfig.KLDMAP) {
            if (APPUtil.getInstance().isInstalled(PACKAGE_KLDMAP)) {
                KLDOperate.getInstance(mContext).closeMap();
            } else {
                TTSNode.getInstance().play("没有找到高德地图,无法关闭");
            }
        } else if (AdapterApplication.mapType == 0) {//关闭百度导航
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_BDDH)) {
                BDDHOperate.getInstance(mContext).closeMap();
            } else {
                TTSNode.getInstance().play("没有找到百度地图,无法关闭");
            }
        } else if(AdapterApplication.mapType == MapConfig.TBMAP) {//读取当前地图配置
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_TBMAP)) {
                TBOperate.getInstance(mContext).closeMap();
            } else {
                TTSNode.getInstance().play("没有找到凯立德地图,无法关闭");
            }
        } else if (AdapterApplication.mapType == MapConfig.BDMAP) {
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_BDMAP)) {
                BDOperate.getInstance(mContext).closeMap();
            } else {
                TTSNode.getInstance().play("没有找到百度导航,无法关闭");
            }
        }
    }

    *//**
     * 当前位置，并跳转到地图显示
     *//*
    public void locateByMap() {

        if  (AdapterApplication.mapType == 1) {//高德
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)) {
                GDOperate.getInstance(mContext).locate();
            } else {
                TTSNode.getInstance().play("没有找到图吧地图,无法显示当前位置");
            }
        }else if (AdapterApplication.mapType == MapConfig.KLDMAP) {//凯立德
            if (APPUtil.getInstance().isInstalled(PACKAGE_KLDMAP)) {
                KLDOperate.getInstance(mContext).locate();
            } else {
                TTSNode.getInstance().play("没有找到高德地图,无法显示当前位置");
            }
        } else if (AdapterApplication.mapType == 0) {//百度导航的当前位置
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_BDDH)) {
                BDDHOperate.getInstance(mContext).locate();
            } else {
                TTSNode.getInstance().play("没有找到百度地图,无法显示当前位置");
            }
        }else if(AdapterApplication.mapType == MapConfig.TBMAP) {//图吧
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_TBMAP)) {
                TBOperate.getInstance(mContext).locate();
            } else {
                TTSNode.getInstance().play("没有找到凯立德地图,无法显示当前位置");
            }
        }  else if (AdapterApplication.mapType == 5) {//百度
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_BDMAP)) {
                BDOperate.getInstance(mContext).locate();
            } else {
                TTSNode.getInstance().play("没有找到百度导航,无法显示当前位置");
            }
        } 
    }

    *//**
     * 开始导航
     *//*
    public void startNavigation(PoiBean bean) {
        if  (AdapterApplication.mapType == 1) {//高德
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)) {
                GDOperate.getInstance(mContext).startNavigation(bean);
            } else {
                TTSNode.getInstance().play("没有找到图吧地图,无法导航");
            }
        } else if (AdapterApplication.mapType == MapConfig.KLDMAP) {
            if (APPUtil.getInstance().isInstalled(PACKAGE_KLDMAP)) {
                KLDOperate.getInstance(mContext).startNavigation(bean);
            } else {
                TTSNode.getInstance().play("没有找到高德地图,无法导航");
            }
        } else if (AdapterApplication.mapType == 0) {//百度导航开始导航
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_BDDH)) {
                BDDHOperate.getInstance(mContext).startNavigation(bean);
            } else {
                TTSNode.getInstance().play("没有找到百度导航,无法导航");
            }
        }else if(AdapterApplication.mapType == MapConfig.TBMAP) {//图吧
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_TBMAP)) {
                TBOperate.getInstance(mContext).startNavigation(bean);
            } else {
                TTSNode.getInstance().play("没有找到凯立德地图,无法导航");
            }
        } else if (AdapterApplication.mapType == 5) {
            if (APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_BDMAP)) {
                BDOperate.getInstance(mContext).startNavigation(bean);
            } else {
                TTSNode.getInstance().play("没有找到百度地图,无法导航");
            }
        } 

    }*/

}
