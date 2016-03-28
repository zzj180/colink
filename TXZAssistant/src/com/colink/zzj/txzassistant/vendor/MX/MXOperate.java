package com.colink.zzj.txzassistant.vendor.MX;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.oem.RomSystemSetting;
import com.colink.zzj.txzassistant.util.APPUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MXOperate {

	private final String ACTION_MESSAGE = "VC_TO_NAVI_COMMUNICATE_MESSAGE";
	private final String ACTION_TYPE = "CMD_TYPE_ENUM"; // 为CmdTypeEnum的内容
	private final String ACTION_CONTENT = "COMMUNICATE_INFO_CONTENT";

	private Context mContext;
	private static MXOperate mInstance;

	public MXOperate(Context context) {
		this.mContext = context;
	}

	public static synchronized MXOperate getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new MXOperate(context);
		}
		return mInstance;
	}

	public void startNavi(double lat, double lon, String dest) {
		Intent i = new Intent("com.mxnavi.mxnavi.PTT_DEST_ACTION");
		String dest_string = "(TND,2,0, " + lat + "," + lon + "," + dest + ")";
		i.putExtra("data", dest_string);
		i.setFlags(32);
		mContext.sendBroadcast(i);
	}

	public void closeMap() {
		sendToNaviMessage(1, null);
		AdapterApplication.runOnUiGround(new Runnable() {

			@Override
			public void run() {
				try {
					RomSystemSetting.forceStopPackage(APPUtil.MX_MAP_PKG);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, 1000);
	}

	private void sendToNaviMessage(int actionType, String[] param) {
		Bundle b = new Bundle();
		b.putInt(ACTION_TYPE, actionType);
		if (param != null) {
			b.putStringArray(ACTION_CONTENT, param);
		}
		Intent i = new Intent(ACTION_MESSAGE);
		i.putExtras(b);
		mContext.sendBroadcast(i);
	}
}
