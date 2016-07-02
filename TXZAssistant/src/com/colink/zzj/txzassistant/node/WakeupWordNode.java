package com.colink.zzj.txzassistant.node;

import com.colink.zzj.txzassistant.util.Logger;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZConfigManager.UIConfigListener;
/**
 * @desc  自定义指令节点
 * @auth zzj
 * @date 2016-03-19
 */
public class WakeupWordNode {
	
	private static WakeupWordNode mInstance;
	private WakeupWordNode() {
	}

	public void init() {
		TXZConfigManager.getInstance().setUIConfigListener(new UIConfigListener() {
			@Override
			public void onConfigChanged(String arg0) {
				Logger.d(arg0);
			}
		});
		
	}

	public static synchronized WakeupWordNode getInstance() {
		if (mInstance == null) {
			mInstance = new WakeupWordNode();
		}
		return mInstance;
	}
	

}
