/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : RomApp.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.oem
 * @Author : zzj
 * @CreateDate : 2015-9-9
 */
package com.unisound.unicar.gui.oem;

import com.unisound.unicar.gui.utils.Logger;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @ModifiedBy : zzj
 * @Modified: 2015-9-9: 实现基本功能
 */
public class RomApp {
    public static final String TAG = "RomApp";

    public static void lanchApp(Context context, String packageName, String className) {
        Logger.d(TAG, "lanchApp packageName=" + packageName + "; className:" + className);
        if (context != null) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClassName(packageName, className);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
            	context.startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }

    public static void uninstallApp(Context context, String packageName) {
        Logger.d(TAG, "uninstallApp packageName=" + packageName);
        if (context != null) {
            Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", packageName, null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
    
    public static void lanchApp(Context context,String pk_name){  
        PackageManager packageManager = context.getPackageManager();   
        Intent intent= packageManager.getLaunchIntentForPackage(pk_name);  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
        	context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }  
}
