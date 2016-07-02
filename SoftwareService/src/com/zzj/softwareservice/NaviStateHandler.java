package com.zzj.softwareservice;

import com.zzj.softwareservice.database.DBConstant;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class NaviStateHandler extends Handler{
	
	private Context mContext;
	
	public NaviStateHandler(Looper looper,Context context) {
		super(looper);
		mContext = context;
	}
	
	/**
	 * Handle navi requests. 
	 * what = 0 isnavi = false
	 * what = 1 isnavi = true;
	 */
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case 0:
			ContentValues con = new ContentValues();
			con.put(DBConstant.NaviTable.ISNAVING, 0);
			mContext.getContentResolver().update(DBConstant.NaviTable.CONTENT_URI, con, null, null);
			break;

		case 1:
			ContentValues values = new ContentValues();
			values.put(DBConstant.NaviTable.ISNAVING, 1);
			mContext.getContentResolver().update(DBConstant.NaviTable.CONTENT_URI, values, null, null);
			break;
		default:
			break;
		}
	
	}
}
