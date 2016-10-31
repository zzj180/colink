package com.colink.zzj.txzassistant.util;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import com.colink.zzj.txzassistant.AdapterApplication;

/**
 * @desc APP通用工具类
 * @auth ZZJ
 * @date 2016-03-20
 */
public class APPUtil {

    private static APPUtil mUtil;
    private Context mContext;
    private Process process;
    
    public static final String BD_NAVI_PKG = "com.baidu.navi";
    public static final String BD_MAP_PKG = "com.baidu.BaiduMap";
    public static final String GD_MAP_PKG = "com.autonavi.minimap";
    public static final String GD_CAR_PKG = "com.autonavi.amapauto";
    public static final String GD_CARJ_PKG = "com.autonavi.amapautolite";
    public static final String KLD_MAP_PKG = "cld.navi.c2739.mainframe";
    public static final String MX_MAP_PKG = "com.mxnavi.mxnavi";
    public static final String GG_MAP_PKG = "com.google.android.apps.maps";
    public static final String CAMERA_PKG = "com.android.camera2";
    public static final String KW_PKG = "cn.kuwo.kwmusiccar";
    public static final String IMUSIC_PKG = "cn.imusic.car.app";
    public static synchronized APPUtil getInstance() {

        if (mUtil == null) {

            mUtil = new APPUtil();
        }
        return mUtil;
    }

    public APPUtil() {
        this.mContext = AdapterApplication.getContext();
    }

    public boolean isSystemUid() {
        String sharedUserId = "";
        try {
            PackageInfo pi = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
            sharedUserId = pi.sharedUserId;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null != sharedUserId && sharedUserId.equals("android.uid.system");
    }

    /**
     * 通过包名检测APP是否安装
     *
     * @param packageName 包名
     * @return true or false
     */
    public boolean isInstalled(String packageName) {

        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        PackageInfo packageInfo;
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }

        if (packageInfo == null) {
            Logger.e(packageName + " 没有安装");
            return false;
        } else {
        	Logger.i(packageName + " 已经安装");
            return true;
        }

    }

    /**
     * 听过包名检测APP是否运行
     *
     * @param packName
     * @return
     */
    @SuppressWarnings("deprecation")
	public boolean isRunning(String packName) {

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packName) || info.baseActivity.getPackageName().equals(packName)) {
                isAppRunning = true;
                break;
            }
        }
        return isAppRunning;
    }

    /**
     * 结束掉某个APP，需传入包名
     *
     * @param packageName
     */
    public void kill(String packageName) {
        initProcess();
        killProcess(packageName);
        closeOutputStream();
    }

    /**
     * 结束进程
     */
    public void killProcess(String packageName) {
        try {
            process = Runtime.getRuntime().exec("su \n");
            process = Runtime.getRuntime().exec("am force-stop " + packageName + " \n");
            process = Runtime.getRuntime().exec("exit \n");
            Logger.e("kill process by aios-adapter!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化进程
     */
    private void initProcess() {
        if (process == null) {
            try {
                process = Runtime.getRuntime().exec("su \n" );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     */
    private void closeOutputStream() {
        if (process != null)
            try {
                process.getOutputStream().close();
                process = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    
    
    
    /**
	 * 强制停止应用程序
	 * 
	 * @param pkgName
	 */
	public static  void forceStopPackage(String pkgName, Context context){
		try {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
			method.invoke(am, pkgName);
		} catch (Exception e) {
		}
	}


    /**
     * 判断指定包名的进程是否运行
     * @param context
     * @param packageName 指定包名
     * @return 是否运行
     */
    public static boolean isRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo rapi : infos) {
            if (rapi.processName.equals(packageName)) {
            	Logger.i(packageName+" 已经打开");
                return true;
            }
        }
        Logger.i(packageName+" 没有打开");
        return false;
    }
    
    /**
     * 启动指定包名的应用
     * @param context
     * @param packageName 指定包名
     */
    public static void lanchApp (Context context,String pk_name) throws Exception{  
        PackageManager packageManager = context.getPackageManager();   
        Intent intent= packageManager.getLaunchIntentForPackage(pk_name);  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(intent);
    }  

}
