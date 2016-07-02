package com.unisound.unicar.gui.route.operation;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class KLDUriApi {

	private static final String EXTRAC = "CLDTNC";
	private static final String act = "android.NaviOne.CldStdTncReceiver";
	public static void startNavi(Context context, double toLat, double toLng, String name) {
		Intent i = new Intent(act);
		if(TextUtils.isEmpty(name)){
			name = "启动凯立德";
		}
		i.putExtra(EXTRAC, "(TNC01,D" + toLat+","+toLng+","+name+")");
		context.sendBroadcast(i);
		
	}
}
