package com.colink.zzj.txzassistant.node;

import android.content.Context;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.oem.RomSystemSetting;
import com.txznet.sdk.TXZSysManager;
import com.txznet.sdk.TXZSysManager.VolumeMgrTool;
/**
 * @desc  系统节点
 * @auth zzj
 * @date 2016-03-19
 */
public class SystemNode {

	private Context mContext;
	private static SystemNode mInstance;

	private SystemNode() {
		this.mContext = AdapterApplication.getContext();
	}

	public void init() {
		TXZSysManager.getInstance().setVolumeMgrTool(mVolumeMgrTool);
	}

	public static synchronized SystemNode getInstance() {
		if (mInstance == null) {
			mInstance = new SystemNode();
		}
		return mInstance;
	}
	
	private VolumeMgrTool mVolumeMgrTool = new VolumeMgrTool() {
		@Override
		public void mute(boolean enable) {
			if (enable)
				RomSystemSetting.setMute(mContext);
			else
				RomSystemSetting.setUnMute(mContext);
			// TODO 静音控制
		}

		@Override
		public void minVolume() {
			// TODO 最小音量
			RomSystemSetting.setMinVolume(mContext);
		}

		@Override
		public void maxVolume() {
			// TODO 最大音量
			RomSystemSetting.setMaxVolume(mContext);
		}

		@Override
		public void incVolume() {
			// TODO 增加音量
			RomSystemSetting.RaiseOrLowerVolume(mContext, true, 2);
		}

		@Override
		public void decVolume() {
			// TODO 减小音量
			RomSystemSetting.RaiseOrLowerVolume(mContext, false, 2);
		}
	};

}
