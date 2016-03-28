package com.colink.zzj.txzassistant.node;

import android.content.Context;
import android.util.Log;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.R;
import com.colink.zzj.txzassistant.oem.RomSystemSetting;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZAsrManager.CommandListener;
/**
 * @desc  自定义指令节点
 * @auth zzj
 * @date 2016-03-19
 */
public class CustomNode {
	
	private Context mContext;
	private static CustomNode mInstance;
	private CustomNode() {
		this.mContext = AdapterApplication.getContext();
	}

	public void init() {
		TXZAsrManager.getInstance().addCommandListener(mCommandListener);
		TXZAsrManager.getInstance().regCommandForFM(87.5F, 108.0F, "FM");
		
		TXZAsrManager.getInstance().regCommand(mContext.getResources().getStringArray(R.array.open_screenOff), "OPEN_SCREENOFF");
		TXZAsrManager.getInstance().regCommand(mContext.getResources().getStringArray(R.array.close_edog), "CLOSE_EDOG");
		TXZAsrManager.getInstance().regCommand(mContext.getResources().getStringArray(R.array.open_edog), "OPEN_EDOG");
		TXZAsrManager.getInstance().regCommand(mContext.getResources().getStringArray(R.array.close_fm), "CLOSE_FM");
		TXZAsrManager.getInstance().regCommand(mContext.getResources().getStringArray(R.array.open_fm), "OPEN_FM");
		TXZAsrManager.getInstance().regCommand(mContext.getResources().getStringArray(R.array.open_radar), "OPEN_RADAR");
		TXZAsrManager.getInstance().regCommand(mContext.getResources().getStringArray(R.array.close_radar), "CLOSE_RADAR");
		TXZAsrManager.getInstance().regCommand(mContext.getResources().getStringArray(R.array.one_navi), "ONE_NAVI");
		TXZAsrManager.getInstance().regCommand(mContext.getResources().getStringArray(R.array.home_page), "HOME_PAGE");
	}

	public static synchronized CustomNode getInstance() {
		if (mInstance == null) {
			mInstance = new CustomNode();
		}
		return mInstance;
	}

	
	CommandListener mCommandListener = new CommandListener() {
		@Override
		public void onCommand(String cmd, String data) {
			if ("CLOSE_EDOG".equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您关闭电子狗", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.closeEDog(mContext);
							}
						});
				return;
			}else if ("OPEN_EDOG".equals(data)) {
				// TODO 关闭空调，先关后提示
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您打开电子狗", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.openEDog(mContext);
							}
						});
				return;
			}else if("OPEN_SCREENOFF".equals(data)){
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"即将为您进入免打扰模式", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.openNoDisturb(mContext);
							}
						});
				return;
			}else if (data.startsWith("FM#")) {
				String sfm = data.substring("FM#".length());
				Log.d("MUSIC","调频到：" + sfm);
				float f = Float.parseFloat(sfm);
				int fm = (int) (f*1000);
				RomSystemSetting.launchFM(fm, mContext);
				return;
			}else if("CLOSE_RADAR".equals(data)){
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您关闭雷达", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.closeRADAR(mContext);
							}
						});
				
				return;
			}else if("OPEN_RADAR".equals(data)){
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您打开雷达", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.openRADAR(mContext);
							}
						});
				
				return;
			}else if("CLOSE_FM".equals(data)){
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您关闭FM", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.closeFM(mContext);
							}
						});
				
				return;
			}else if("OPEN_FM".equals(data)){
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您打开FM", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.openFM(mContext);
							}
						});
				
				return;
			}else if("ONE_NAVI".equals(data)){
				RomSystemSetting.openONENavi(mContext);
				return;
			}else if("HOME_PAGE".equals(data)){
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您返回主界面", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.homePage(mContext);
							}
						});
				
				return;
			}
		}
	};
}
