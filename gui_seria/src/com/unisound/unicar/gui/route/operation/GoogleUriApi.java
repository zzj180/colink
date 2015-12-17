package com.unisound.unicar.gui.route.operation;

import java.lang.reflect.Method;

import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.GUIConfig;

import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class GoogleUriApi {

	public static void showRoute(Context context, double lat, double lon){
		
		if(DeviceTool.checkApkExist(context,GUIConfig.PACKAGE_NAME_GOOGLE_MAP)){
			/*try {
				disableStatusBar(context);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
        	Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?f=d&daddr="+lat+","+lon+"&hl=zh-TW"));
			it.setClassName(GUIConfig.PACKAGE_NAME_GOOGLE_MAP, GUIConfig.ACTIVITY_NAME_GOOGLE_MAP);
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(it);
    	}else{
    		Toast.makeText(context, "未安装google map", Toast.LENGTH_SHORT).show();
    	}
	}
	
	private static void disableStatusBar(Context context){
        try {
            Object service = context.getSystemService("statusbar");
            Class<?> claz = Class.forName("android.app.StatusBarManager");
            Method expand = claz.getMethod("disable",new Class[] { int.class });;
            expand.invoke(service,0x00000001);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static final void collapseStatusBar(Context ctx) {
        Object sbservice = ctx.getSystemService("statusbar");
        try {
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method collapse;
                collapse = statusBarManager.getMethod("collapsePanels");
            collapse.invoke(sbservice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
