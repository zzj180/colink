package com.aispeech.aios.adapter.config;

import android.text.TextUtils;
import android.support.v4.util.ArrayMap;

/**
 * @desc 配置信息类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class Configs {

    public static final class Package {
        public static final String CAR_RECORDER = "com.softwinner.fireeye";        //行车记录仪
        public static final String TIRE_PRESSURE = "com.lva.tpms";                  //胎压
        public static final String FM = "com.ximalaya.ting.android.car"; //喜马拉雅FM
    }

    public static final class AppName {
        public static final String FM = "喜马拉雅FM"; //喜马拉雅FM
    }

    /**
     * 提示语
     */
    public static final class MarkedWords {
        public static final String NO_MUSIC = "请安装指定的音乐应用";
        public static final String DATE_PASSED = "不支持查询过去日期的天气";
        public static final String DATE_TOO_FAR = "只能查询未来七天的天气";
        public static final String NO_WEATHER_RESUALT = "没有查询到相关天气结果";
        public static final String NO_NETWORK = "网络不给力，请联网后重新查询";
        public static final String NET_EXCEPTION = "网络条件不佳，请稍后重试";
        public static final String XMLY_ERROR = "没有找到搜索的节目";
        public static final String CANT_NOT_UNDERSTAND = "你说的我没听懂喔";
    }

    /**
     * Created by hehr on 2015/11/24.
     */
    public static final class MapConfig {
        /**
         * 高德地图
         */
        public static final int GDMAP = 1;
        /**
         * 百度地图
         */
        public static final int BDMAP = 7;
        /**
         * 凯立德地图
         */
        public static final int KLDMAP = 2;
        /**
         * 图吧地图
         */
        public static final int TBMAP = 6;
        /**
         * 美行地图
         */
        public static final int MXMAP = 3;
        /**
         * 谷歌地图
         */
        public static final int GGMAP = 4;
        /**
         * 百度导航地图
         */
        public static final int BDDH = 0;

        /**
         * 高德地图车机版
         */
        public static final int GDMAPFORCAT = 5;

        public static final String PACKAGE_TBMAP = "com.mapbar.android.carnavi";
        public static final String PACKAGE_GDMAP = "com.autonavi.minimap";
        public static final String PACKAGE_BDMAP = "com.baidu.BaiduMap";
        public static final String PACKAGE_KLDMAP = "cld.navi.c2739.mainframe";
        public static final String PACKAGE_MXMAP = "com.mxnavi.mxnavi";
        public static final String PACKAGE_GGMAP = "com.google.android.apps.maps";
        public static final String PACKAGE_BDDH = "com.baidu.navi";//百度导航的包名
        public static final String PACKAGE_GDMAPFORCAT = "com.autonavi.amapauto";//高德地图车机版包名
    }


    /**
     * 获取地图名称
     * type 编号
     */
    public static String getMapName(int type) {

        String result = mapsName.get(type);
        if (TextUtils.isEmpty(result)) {
            result = mapsName.get(MapConfig.GDMAP);
        }
        return result;
    }

    public static String getMapPackage(int type) {

        String result = mapsPackageName.get(type);
        if (TextUtils.isEmpty(result)) {
            result = mapsPackageName.get(MapConfig.GDMAP);
        }
        return result;
    }

    private static ArrayMap<Integer, String> mapsName = new ArrayMap<Integer, String>() {
        {
            put(MapConfig.GDMAP, "高德地图");
            put(MapConfig.BDDH, "百度导航");
            put(MapConfig.KLDMAP, "凯立德地图");
            put(MapConfig.MXMAP, "美行地图");
            put(MapConfig.GGMAP, "谷歌地图");
            put(MapConfig.BDMAP, "百度地图");
            put(MapConfig.TBMAP, "图吧地图");
            put(MapConfig.GDMAPFORCAT, "高德地图车机版");
        }
    };

    private static ArrayMap<Integer, String> mapsPackageName = new ArrayMap<Integer, String>() {
        {
            put(MapConfig.GDMAP, MapConfig.PACKAGE_GDMAP);
            put(MapConfig.BDDH,  MapConfig.PACKAGE_BDDH);
            put(MapConfig.KLDMAP,  MapConfig.PACKAGE_KLDMAP);
            put(MapConfig.MXMAP,  MapConfig.PACKAGE_MXMAP);
            put(MapConfig.GGMAP,  MapConfig.PACKAGE_GGMAP);
            put(MapConfig.BDMAP,  MapConfig.PACKAGE_BDMAP);
            put(MapConfig.TBMAP,  MapConfig.PACKAGE_TBMAP);
            put(MapConfig.GDMAPFORCAT,  MapConfig.PACKAGE_GDMAPFORCAT);
        }
    };
}
