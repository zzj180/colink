package com.colink.zzj.txzassistant.node;

import android.content.Intent;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.txznet.sdk.TXZResourceManager.RecordWin;

public class ZZJRecordWin implements RecordWin {
	private static ZZJRecordWin mInstance;
	private static final String ACTION_TOUCHEVENT = "android.intent.action.TOUCHEVENT";
	public static synchronized ZZJRecordWin getInstance() {
		if (mInstance == null) {
			mInstance = new ZZJRecordWin(); 
		}
		return mInstance;
	}

	@Override
	public void close() {
	}

	@Override
	public void onProgressChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChange(RecordStatus arg0) {
		AdapterApplication.getContext().sendBroadcast(new Intent(ACTION_TOUCHEVENT));
	}

	@Override
	public void onVolumeChange(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setOperateListener(RecordWinOperateListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showAddressChoice(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showAudioChoice(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showContactChoice(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showStockInfo(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showSysText(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showUsrText(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showWheatherInfo(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showWxContactChoice(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void snapPager(boolean arg0) {
		// TODO Auto-generated method stub
	}

}
