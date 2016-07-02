package com.unisound.unicar.gui.route.operation;

import com.unisound.unicar.gui.utils.GUIConfig;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class GoogleUriApi {

	public static void showRoute(Context context, double lat, double lon) {

		/*
		 * Intent it = new Intent(Intent.ACTION_VIEW,
		 * Uri.parse("http://maps.google.com/maps?f=d&daddr="
		 * +lat+","+lon+"&hl=zh-TW"));
		 * it.setClassName(GUIConfig.PACKAGE_NAME_GOOGLE_MAP,
		 * GUIConfig.ACTIVITY_NAME_GOOGLE_MAP);
		 * it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * context.startActivity(it);
		 */
		Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon
				+ "&mode=d");
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
		mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mapIntent.setPackage(GUIConfig.PACKAGE_NAME_GOOGLE_MAP);
		try {
			context.startActivity(mapIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
