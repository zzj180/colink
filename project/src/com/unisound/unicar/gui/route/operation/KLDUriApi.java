package com.unisound.unicar.gui.route.operation;


import com.unisound.unicar.gui.utils.Gps;
import com.unisound.unicar.gui.utils.PositionUtil;

import android.content.Context;
import android.content.Intent;

public class KLDUriApi {

	private static final String EXTRAC = "CLDTNC";
	private static final String act = "android.NaviOne.CldStdTncReceiver";

	public static void startNavi(Context context, double toLat, double toLng, String name) {
		Gps gps = PositionUtil.gcj_To_Gps84(toLat, toLng);
		Intent i = new Intent(act);
		i.putExtra(EXTRAC, "(TNC01,D" + gps.getWgLat()+","+gps.getWgLon()+","+name+")");
		context.sendBroadcast(i);
	}
}
