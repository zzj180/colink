package com.zzj.softwareservice.util;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

/**
 * @Module : 隶属模块名
 * @Comments : 实现基本功能
 * @Author : zzj
 * @CreateDate : 2016-03-17
 */
public class APPUtil {
	
	public static final String ONE_NAVI = "ONE_NAVI";
	public static final String DDBOX_APP = "com.glsx.autonavi";
	public static final String TIANAN_APP = "com.share.android";
	public static final String ECAR_1 = "com.coagent.app";
	public static final String ECAR_2 = "com.coagent.ecar";
	
	public static final String MAP_INDEX = "MAP_INDEX";
    public static final String BDDH_APP = "com.baidu.navi";//百度导航的包名
    public static final String GDMAPFORCAT_APP = "com.autonavi.amapauto";//高德地图车机版包名
    public static final String GDMAP_APP = "com.autonavi.minimap";
    public static final String MXMAP_APP = "com.mxnavi.mxnavi";
    public static final String KUWO_MUSIC = "cn.kuwo.kwmusiccar";

	public static void openUrl(Context context, String url) {
		try {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri contentUri = Uri.parse(url);
			intent.setData(contentUri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
    
	public static boolean supportBT(Context context) {
		int support = 1;
		try {
			Cursor query = context.getContentResolver().query(Uri.parse("content://com.colink.bluetoothe/bluetootheonline"),null, null, null, null);
			if (query != null) {
				if (query.moveToNext()) {
					support = query.getInt(query.getColumnIndex("support"));
				}
				query.close();
			}
		} catch (Exception e) {
		}
		return support == 1;
	}
 
    /**
     * 通过包名检测APP是否安装
     *
     * @param packageName 包名
     * @return true or false
     */
    public static boolean isInstalled(Context context,String packageName) {

        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }

        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * 听过包名检测APP是否运行
     * @param packName
     * @return
     */
    @SuppressWarnings("deprecation")
	public static boolean isRunning(Context context,String packName) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
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
	 * 通过包名打开应用 返回true:成功 返回false:失败
	 */
	public static boolean openApplication(Context context,String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return false;
		}

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
        if (intent == null) {
            return false;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return true;
    }
    
}
