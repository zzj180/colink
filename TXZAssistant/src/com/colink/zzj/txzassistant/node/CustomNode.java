package com.colink.zzj.txzassistant.node;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.android.kwmusic.CommandPreference;
import com.android.kwmusic.KWMusicService;
import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.R;
import com.colink.zzj.txzassistant.oem.RomSystemSetting;
import com.colink.zzj.txzassistant.util.APPUtil;
import com.colink.zzj.txzassistant.util.CustomCommand;
import com.colink.zzj.txzassistant.util.Logger;
import com.colink.zzj.txzassistant.util.SystemPropertiesProxy;
import com.colink.zzj.txzassistant.vendor.BDDH.BDDHOperate;
import com.colink.zzj.txzassistant.vendor.GD.GDOperate;
import com.colink.zzj.txzassistant.vendor.KLD.KLDOperate;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZAsrManager.CommandListener;
import com.txznet.sdk.TXZResourceManager;

/**
 * @desc 自定义指令节点
 * @auth zzj
 * @date 2016-03-19
 */
public class CustomNode implements CustomCommand {

	private Context mContext;
	private static CustomNode mInstance;

	private CustomNode() {
		this.mContext = AdapterApplication.getContext();
	}

	public void init() {
		TXZAsrManager.getInstance().addCommandListener(mCommandListener);
		TXZAsrManager.getInstance().regCommandForFM(87.5F, 108.0F, "FM");

		TXZAsrManager.getInstance().regCommand(
				mContext.getResources().getStringArray(R.array.open_screenOff),
				OPEN_SCREENOFF);
		TXZAsrManager.getInstance().regCommand(
				mContext.getResources().getStringArray(R.array.close_edog),
				CLOSE_EDOG);
		TXZAsrManager.getInstance().regCommand(
				mContext.getResources().getStringArray(R.array.open_edog),
				OPEN_EDOG);
		TXZAsrManager.getInstance().regCommand(
				mContext.getResources().getStringArray(R.array.close_fm),
				CLOSE_FM);
		TXZAsrManager.getInstance().regCommand(
				mContext.getResources().getStringArray(R.array.open_fm),OPEN_FM);
		TXZAsrManager.getInstance().regCommand(
				mContext.getResources().getStringArray(R.array.open_radar),OPEN_RADAR);
		TXZAsrManager.getInstance().regCommand(
				mContext.getResources().getStringArray(R.array.close_radar),CLOSE_RADAR);
		TXZAsrManager.getInstance().regCommand(
				mContext.getResources().getStringArray(R.array.one_navi),ONE_NAVI);
		TXZAsrManager.getInstance().regCommand(
				mContext.getResources().getStringArray(R.array.home_page),HOME_PAGE);
		TXZAsrManager.getInstance().regCommand(mContext.getResources().getStringArray(R.array.close_carcorder), CLOSE_CARCORDER);
		TXZAsrManager.getInstance().regCommand(
				mContext.getResources().getStringArray(R.array.stop_music),STOP_MUSIC);
		
		TXZAsrManager.getInstance().regCommand(
				mContext.getResources().getStringArray(R.array.open_bluetooth),OPEN_BLUETOOTHE);
		TXZAsrManager.getInstance().regCommand(
				mContext.getResources().getStringArray(R.array.close_bluetooth),CLOSE_BLUETOOTHE);
		TXZAsrManager.getInstance().regCommand(new String[]{"打开爱奇艺"},"OPEN_AIQIYI");
		String custom = SystemPropertiesProxy.get(mContext,"ro.inet.consumer.code");
		if ("149".equals(custom)) {
			TXZAsrManager.getInstance().regCommand(
					mContext.getResources().getStringArray(R.array.in_com),IN_COM);
			TXZAsrManager.getInstance().regCommand(
					mContext.getResources().getStringArray(R.array.go_com),GO_COM);
		}
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
			Logger.d(data);
			if (CLOSE_EDOG.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您关闭电子狗", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.closeEDog(mContext);
							}
						});
				return;
			} else if (OPEN_EDOG.equals(data)) {
				// TODO 关闭空调，先关后提示
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您打开电子狗", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.openEDog(mContext);
							}
						});
				return;
			} else if (OPEN_SCREENOFF.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"即将为您进入免打扰模式", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.openNoDisturb(mContext);
							}
						});
				return;
			} else if (data.startsWith("FM#")) {
				String sfm = data.substring("FM#".length());
				Log.d("MUSIC", "调频到：" + sfm);
				float f = Float.parseFloat(sfm);
				int fm = (int) (f * 1000);
				RomSystemSetting.launchFM(fm, mContext);
				return;
			} else if (CLOSE_RADAR.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您关闭雷达", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.closeRADAR(mContext);
							}
						});

				return;
			} else if (OPEN_RADAR.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您打开雷达", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.openRADAR(mContext);
							}
						});

				return;
			} else if (CLOSE_FM.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您关闭FM", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.closeFM(mContext);
							}
						});

				return;
			} else if (OPEN_FM.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您打开FM", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.openFM(mContext);
							}
						});

				return;
			} else if (ONE_NAVI.equals(data)) {
				RomSystemSetting.openONENavi(mContext);
				return;
			} else if (HOME_PAGE.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您返回主界面", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.homePage(mContext);
							}
						});
				return;
			} else if (CLOSE_CARCORDER.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"记录仪将在后台运行", true, new Runnable() {
							@Override
							public void run() {
								try {
									@SuppressWarnings("deprecation")
									List<RunningTaskInfo> tasksInfo = ((ActivityManager) mContext
											.getSystemService(Context.ACTIVITY_SERVICE))
											.getRunningTasks(1);
									// 应用程序位于堆栈的顶层
									if (tasksInfo != null) {
										String className = tasksInfo.get(0).topActivity.getClassName();
										if (CAMERA_LAUNCHER.equals(className)
												|| CAMERA_ACTIVITY.equals(className)) {
											RomSystemSetting.homePage(mContext);
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
				return;
			}else if (OPEN_BLUETOOTHE.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您打开蓝牙", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.setBluetoothEnabled(mContext,true);
							}
						});

				return;
			} else if (CLOSE_BLUETOOTHE.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您关闭蓝牙", true, new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.setBluetoothEnabled(mContext,false);
							}
						});

				return;
			} else if (IN_COM.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(AdapterApplication.getContext().getResources().getString(R.string.in_com), true, null);
				return;
			} else if (GO_COM.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您导航到广东轩辕电子科技有限公司", true, new Runnable() {
							@Override
							public void run() {
								if (AdapterApplication.mapType == 1) {// 高德
									GDOperate.getInstance(mContext).startNavigation(23.0942500125,113.1495453868);
								} else if (AdapterApplication.mapType == 2) {
									KLDOperate.getInstance(mContext).startNavigation(23.0942500125,113.1495453868,"广东轩辕电子科技有限公司");
								} else if (AdapterApplication.mapType == 0) {// 百度导航开始导航
									BDDHOperate.getInstance(mContext).startNavigation(23.100591,113.155987);
								}
							}
						});
				return;
			} else if (STOP_MUSIC.equals(data)) {

				TXZResourceManager.getInstance().speakTextOnRecordWin("为您退出音乐",
						true, new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
									intent.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MEDIA_STOP));
									mContext.sendBroadcast(intent);
									Intent i = new Intent(mContext,KWMusicService.class);
									i.setAction(CommandPreference.SERVICECMD);
									i.putExtra(CommandPreference.CMDNAME,CommandPreference.CMDSTOP);
									mContext.startService(i);
									RomSystemSetting.forceStopPackage(APPUtil.KW_PKG);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
				return;
			}else if("OPEN_AIQIYI".equals(data)){
				
				try {
					APPUtil.lanchApp(mContext, "com.qiyi.video.pad");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}
	};

}
