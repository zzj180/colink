package com.zzj.softwareservice;

import com.android.fm.radio.FMRadioService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent arg1) {
		try {
			context.startService(new Intent(context, FMRadioService.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
