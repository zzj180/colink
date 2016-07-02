package com.colink.zzj.txzassistant.node;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.util.Logger;
import com.colink.zzj.txzassistant.util.UserPerferenceUtil;
import com.txznet.sdk.TXZPowerManager;

import android.database.ContentObserver;
import android.provider.Settings;

public class ACCStateNode {
	private static ACCStateNode mInstance;
	private String ACC_STATE = "acc_state";

	private ACCStateNode() {
		txzACC();
	}

	public static synchronized ACCStateNode getInstance() {
		if (mInstance == null) {
			mInstance = new ACCStateNode();
		}
		return mInstance;
	}

	public void registAccDB() {
		AdapterApplication.getApp().getContentResolver().registerContentObserver(
				Settings.System.getUriFor(ACC_STATE), false,
				new ContentObserver(AdapterApplication.uiHandler) {
					@Override
					public void onChange(boolean selfChange) {
						// 唤醒
						AdapterApplication.runOnUiGround(new Runnable() {
							@Override
							public void run() {
								txzACC();
							}
						}, 1000);
					}
				});

	}

	private void txzACC() {
		AdapterApplication.mAcc = Settings.System.getInt(
				AdapterApplication.getApp().getContentResolver(), ACC_STATE, 1) == 1;
		UserPerferenceUtil.setWakeupEnable(AdapterApplication.getApp(), AdapterApplication.mAcc);
		Logger.d("acc="+AdapterApplication.mAcc);
		if (AdapterApplication.mAcc) {
			TXZPowerManager.getInstance().reinitTXZ(new Runnable() {
				@Override
				public void run() {
					TXZPowerManager.getInstance().notifyPowerAction(
							TXZPowerManager.PowerAction.POWER_ACTION_WAKEUP);
				}
			});
		} else {
			TXZPowerManager.getInstance().notifyPowerAction(
					TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
			TXZPowerManager.getInstance().releaseTXZ();
		}
	}
}
