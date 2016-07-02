package com.android.fm.radio;

import com.zzj.softwareservice.L;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;

/**
 * Provides "background" FM Radio (that uses the hardware) capabilities,
 * allowing the user to switch between activities without stopping playback.
 */
public class FMRadioService extends Service {

	private String FM_FREG = "fm_freg";
	private String FM_SWITCH = "fm_switch";
	private String ACC_STATE = "acc_state";
	private static final int VALUE_DEFAULT = 92500;
	private FMWriteFile fmWriteFile;
	
	ContentObserver observer = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			L.v("onChange");
			int value = Settings.System.getInt(getContentResolver(), FM_FREG,VALUE_DEFAULT);
			int fm_switch = Settings.System.getInt(getContentResolver(),FM_SWITCH, 1);
			try {
				if(fm_switch == 0){
					fmWriteFile.writeQn802x(true, value / 10);
					fmWriteFile.setAudioHeadSetOn();
				}else{
					fmWriteFile.writeQn802x(false, value / 10);
					fmWriteFile.setAudioHeadSetOff();
				}
			} catch (Exception e) {
				fmWriteFile.setAudioHeadSetOff();
				e.printStackTrace();
			}
		}
	};
	private ContentObserver accObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			L.v("ACConChange");
			boolean acc_state = Settings.System.getInt(getContentResolver(),ACC_STATE, 1) == 1;
			if(acc_state){
				int value = Settings.System.getInt(getContentResolver(), FM_FREG,VALUE_DEFAULT);
				int fm_switch = Settings.System.getInt(getContentResolver(),FM_SWITCH, 1);
				try {
					if(fm_switch == 0){
						fmWriteFile.writeQn802x(true, value / 10);
						fmWriteFile.setAudioHeadSetOn();
					}else{
						fmWriteFile.writeQn802x(false, value / 10);
						fmWriteFile.setAudioHeadSetOff();
					}
				} catch (Exception e) {
					fmWriteFile.setAudioHeadSetOff();
					e.printStackTrace();
				}
			}else{
				fmWriteFile.setAudioHeadSetOff();
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		fmWriteFile = new FMWriteFile();
		int value = Settings.System.getInt(getContentResolver(), FM_FREG,VALUE_DEFAULT);
		int fm_switch = Settings.System.getInt(getContentResolver(), FM_SWITCH,1);
		try {
			if(fm_switch == 0){
				fmWriteFile.writeQn802x(true, value / 10);
				fmWriteFile.setAudioHeadSetOn();
			}else{
				fmWriteFile.writeQn802x(false, value / 10);
				fmWriteFile.setAudioHeadSetOff();
			}
		} catch (Exception e) {
			fmWriteFile.setAudioHeadSetOff();
			e.printStackTrace();
		}
		
		getContentResolver().registerContentObserver(Settings.System.getUriFor(FM_FREG), false, observer);
		getContentResolver().registerContentObserver(Settings.System.getUriFor(FM_SWITCH), false, observer);
		
		getContentResolver().registerContentObserver(Settings.System.getUriFor(ACC_STATE), false, accObserver);
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getContentResolver().unregisterContentObserver(observer);
		Intent intent = new Intent();
		intent.setClass(this, FMRadioService.class);
        startService(intent);
	}
}
