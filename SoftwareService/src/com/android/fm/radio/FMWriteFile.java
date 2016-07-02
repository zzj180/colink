package com.android.fm.radio;

import com.zzj.softwareservice.L;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FMWriteFile implements IAccessFile {

	public static final String DEVICE_FILE = "/sys/class/qn802x_cls/qn802x/qn802x";

	@Override
	public void writeQn802x(boolean mSwitcher, int mCurrentFreq) throws Exception {
		int retry = 5;
			do {
				FileOutputStream fos = new FileOutputStream(DEVICE_FILE);
				byte[] wBuf = new byte[5];
				wBuf[0] = (byte) 0xFA;
				wBuf[1] = (byte) (mSwitcher ? 1 : 0);
				wBuf[2] = (byte) 0xFC;
				wBuf[3] = (byte) ((mCurrentFreq >> 8) & 0xff);
				wBuf[4] = (byte) (mCurrentFreq & 0xff);

				
					L.v("wbuf = " + mCurrentFreq);

				fos.write(wBuf, 0, 5);
				fos.flush();
				fos.close();

				FileInputStream fis = new FileInputStream(DEVICE_FILE);
				int mFreqTmp;
				byte[] rBuf = new byte[3];
				fis.read(rBuf);
				mFreqTmp = (int) ((rBuf[1] & 0xff) << 8);
				mFreqTmp += (int) (rBuf[2] & 0xff);
				fis.close();

				if (mFreqTmp == mCurrentFreq)
					break;

				retry--;
			} while (retry != 0);
	}
	
	public void setAudioHeadSetOn(){
	//	AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADSET,AudioSystem.DEVICE_STATE_AVAILABLE,"");//switch audio to headset
	}
	
	public void setAudioHeadSetOff(){
	//	AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADSET,AudioSystem.DEVICE_STATE_UNAVAILABLE,"");//switch audio to headset
	}

}
