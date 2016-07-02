package com.colink.zzj.txzassistant.node;

import com.colink.zzj.txzassistant.util.Logger;
import com.txznet.sdk.TXZStatusManager.StatusListener;

public class OnStatusListener implements StatusListener{

	@Override
	public void onBeepEnd() {
		Logger.d("onBeepEnd");
	}

	@Override
	public void onBeginAsr() {
		Logger.d("onBeginAsr");
	}

	@Override
	public void onBeginCall() {
		Logger.d("onBeginCall");
	}

	@Override
	public void onBeginTts() {
		Logger.d("onBeginTts");
	}

	@Override
	public void onEndAsr() {
		Logger.d("onEndAsr");
	}

	@Override
	public void onEndCall() {
		Logger.d("onEndCall");
	}

	@Override
	public void onEndTts() {
		Logger.d("onEndTts");
	}

	@Override
	public void onMusicPause() {
		Logger.d("onMusicPause");
	}

	@Override
	public void onMusicPlay() {
		Logger.d("onMusicPlay");
	}

}
