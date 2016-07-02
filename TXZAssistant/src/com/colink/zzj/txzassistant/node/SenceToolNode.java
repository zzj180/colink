package com.colink.zzj.txzassistant.node;

import android.content.Context;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.txznet.sdk.TXZSenceManager;
import com.txznet.sdk.TXZSenceManager.SenceTool;
import com.txznet.sdk.TXZSenceManager.SenceType;
import com.txznet.sdk.TXZSysManager;

public class SenceToolNode implements SenceTool {
	private Context mContext;
	private static SenceToolNode mInstance;

	private SenceToolNode() {
		this.mContext = AdapterApplication.getContext();
	}

	public void init() {
		TXZSenceManager.getInstance().setSenceTool(SenceType.SENCE_TYPE_WAKEUP, this);
	}

	public static synchronized SenceToolNode getInstance() {
		if (mInstance == null) {
			mInstance = new SenceToolNode();
		}
		return mInstance;
	}
	
	@Override
	public boolean process(SenceType arg0, String arg1) {
		if("取消静音".equals(arg1)){
			
			
			return true;
		}else if("静音静音".equals(arg1)){
			
			
			return true;
		}else if("免打扰模式".equals(arg1)){
			
			
			return true;
		}
		return false;
	}
}
