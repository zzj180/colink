package com.zzj.softwareservice;

import android.util.Log;

public class L {
	public static String TAG = "Software";
	public static boolean DEBUG = true;
	public static void v(String msg){
		if(DEBUG && msg!=null)Log.v(TAG, msg);
	}
}
