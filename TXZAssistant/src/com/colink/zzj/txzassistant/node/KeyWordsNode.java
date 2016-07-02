package com.colink.zzj.txzassistant.node;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.util.Logger;
import com.colink.zzj.txzassistant.util.StringUtil;
import com.colink.zzj.txzassistant.util.UserPerferenceUtil;
import com.txznet.sdk.TXZTtsManager;
import com.txznet.sdk.TXZConfigManager.UserConfigListener;

public class KeyWordsNode implements UserConfigListener{

	@Override
	public void onChangeCommunicationStyle(String arg0) {
	}

	@Override
	public void onChangeWakeupKeywords(String[] keywords) {
		String keyword = StringUtil.convertArrayToString(keywords);
		Logger.d(keyword);
		TXZTtsManager.getInstance().speakText("修改唤醒词为："+ keyword);
		UserPerferenceUtil.setWakeupWords(AdapterApplication.getApp(), keyword);
	}
}
