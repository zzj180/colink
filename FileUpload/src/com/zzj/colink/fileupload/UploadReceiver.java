package com.zzj.colink.fileupload;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class UploadReceiver extends BroadcastReceiver {
	
	private final String ACTION_PTT_NAVI = "com.mxnavi.mxnavi.PTT_START_ACTION";
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		String action = arg1.getAction();
		if(Intent.ACTION_BOOT_COMPLETED.equals(action)){
			String customer = SystemPropertiesProxy.get(context,"ro.inet.consumer.code");
			if("010".equals(customer)){
				String str = ((TelephonyManager)context.getSystemService("phone")).getDeviceId();
				if(TextUtils.isEmpty(str)){
					str = ((TelephonyManager)context.getSystemService("phone")).getDeviceId();
				}
				if(TextUtils.isEmpty(str)){
					str = ((TelephonyManager)context.getSystemService("phone")).getDeviceId();
				}
				if(TextUtils.isEmpty(str)){
					str = ((TelephonyManager)context.getSystemService("phone")).getDeviceId();
				}
				if(TextUtils.isEmpty(str)){
					return;
				}
				String id= WmEncrypter.encryptByMD5(str + "PinWang");
				WmEncrypter.write(new File("/mnt/sdcard/WM_Edog/SerialId"), str);
				WmEncrypter.write(new File("/mnt/sdcard/WM_Edog/pefa.bin"), id);
			}
		}else if(ACTION_PTT_NAVI.equals(action)){
			int navi = Settings.System.getInt(context.getContentResolver(), "ONE_NAVI", 0);
			switch (navi) {
			case 1:
				ComponentName componetName = new ComponentName("com.coagent.app","com.coagent.activity.MainActivity");
				Intent ecar = new Intent();
				ecar.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ecar.setComponent(componetName);
				try {
					context.startActivity(ecar);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Intent tmpIntent=new Intent("com.android.ecar.recv");
				tmpIntent.putExtra("ecarSendKey", "MakeCall");
				tmpIntent.putExtra("cmdType", "standCMD");
				tmpIntent.putExtra("keySet", "");
				context.sendBroadcast(tmpIntent);
				break;
			default:
				Intent intent = new Intent("com.glsx.bootup.receive.autonavi"); 
				intent.putExtra("autonaviType", 1);  // autonaviType为1：表示直接发起导航请求， 	  // autonaviType为2：只进入导航主页面（不发起请求）;
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					context.startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
}
