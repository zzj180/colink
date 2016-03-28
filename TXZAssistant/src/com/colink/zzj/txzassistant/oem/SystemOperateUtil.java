package com.colink.zzj.txzassistant.oem;

import java.lang.reflect.Method;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import com.colink.zzj.txzassistant.AdapterApplication;

/**
 * @desc 系统操作工具类
 * @auth ZZJ
 * @date 2016-03-17
 */
public class SystemOperateUtil {

    private Context con;
    private static SystemOperateUtil myUtil;
    private WifiManager mWifiManager;

    public SystemOperateUtil() {
        this.con = AdapterApplication.getContext();
        this.mWifiManager = (WifiManager) con.getSystemService(Context.WIFI_SERVICE);
    }

    public static synchronized SystemOperateUtil getInstance() {

        if (myUtil == null) {
            myUtil = new SystemOperateUtil();
        }
        return myUtil;
    }


    /**
     * 设置热点开启关闭
     *
     * @param state
     * @return 开启或者关闭   成功与否
     */
    public void setHotSpotState(boolean state) {

        if (state) {//打开
//            this.setWIFIState(false);//先关闭WIFI
            boolean wifiState = mWifiManager.isWifiEnabled();
            PreferenceHelper.getInstance().setWifiState(wifiState);
            if (wifiState) {
                mWifiManager.setWifiEnabled(false);
            }
        }

        try {
            // 热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            // 配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = "AndroidAP";
            // 配置热点的密码
            apConfig.preSharedKey = "12345678";
            // 安全：WPA2_PSK
            apConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            // 通过反射调用设置热点
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            // 返回热点打开状态
            boolean hotPortState = (Boolean) method.invoke(mWifiManager, apConfig, state);

            if (state) {
                if (hotPortState) {
                } else {
                }
            } else {
                if (hotPortState) {
                    mWifiManager.setWifiEnabled(PreferenceHelper.getInstance().getWifiState());
                } else {
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 截屏
     */
    public String shotScreen() {
        return "对不起，暂不支持此功能";
    }

    /**
     * 点亮屏幕
     */
    public void openScreen() {
        Settings.System.putInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                PreferenceHelper.getInstance().getBrightness());
    }

    /**
     * 拍一张照片
     */
    public String takePhoto() {
        return "对不起，暂不支持此功能";
    }

    /**
     * 关闭屏幕
     */
    public void closeScreen() {
        try {
            int brightness = Settings.System.getInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            PreferenceHelper.getInstance().setBrightness(brightness);
            Settings.System.putInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开蓝牙
     */
    public String openBlueTooth() {
    	Intent intent = new Intent();
		intent.setAction("android.intent.action.MATCH_BLUETOOTH");
		con.sendBroadcast(intent);
		return "打开蓝牙";
    }

    /**
     * 关闭蓝牙
     */
    public String closeBlueTooth() {
    	Intent intent = new Intent();
		intent.setAction("android.intent.action.CLOSE_BLUETOOTH");
		con.sendBroadcast(intent);
		return "关闭蓝牙";
    }

    /**
     * 打开FM
     */
    public String openFM() {
    	Settings.System.putInt(con.getContentResolver(), "fm_switch", 0);
    	return "打开FM";
    }

    /**
     * 关闭FM
     */
    public String closeFM() {
    	Settings.System.putInt(con.getContentResolver(), "fm_switch", 1);
    	con.sendBroadcast(new Intent("action.colink.stopFM"));
    	return "关闭FM";
    }
    
    /**
     * 发射FM
     */
    public void launchFM(int fm) {
    	Settings.System.putInt(con.getContentResolver(), "fm_freg", fm);
    	Settings.System.putInt(con.getContentResolver(), "fm_switch", 0);
    	
    	Intent intent=new Intent("action.colink.startFM");
		intent.putExtra("fm_fq",  (fm*1.0f/1000));
		con.sendBroadcast(intent);
    }

    /**
     * 打开电子狗
     */
    public String openEDog() {
    	Intent intent = new Intent();
		intent.setAction("com.wanma.action.EDOG_ON");
		con.sendBroadcast(intent);
		return "打开电子狗";
    }

    /**
     * 关闭电子狗
     */
    public String closeEDog() {
    	Intent intent = new Intent();
		intent.setAction("com.wanma.action.EDOG_OFF");
		con.sendBroadcast(intent);
		return "关闭电子狗";
    }
    
    /**
     * 免打扰模式
     */
    public void openNoDisturb() {
    	Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClassName("com.zzj.coogo.screenoff", "com.zzj.coogo.screenoff.ScrrenoffActivity");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			con.startActivity(intent);
		} catch (Exception e) {
		}
    }
    
    
    /**
     * 打开一键导航
     */
    public void openOneNavi() {
    	int navi = Settings.System.getInt(con.getContentResolver(), "ONE_NAVI", 0);
		switch (navi) {
		case 1:
			ComponentName componetName = new ComponentName("com.coagent.app","com.coagent.activity.MainActivity");
			Intent ecar = new Intent();
			ecar.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ecar.setComponent(componetName);
			try {
				con.startActivity(ecar);
			} catch (Exception e) {
			}

			Intent tmpIntent = new Intent("com.android.ecar.recv");
			tmpIntent.putExtra("ecarSendKey", "MakeCall");
			tmpIntent.putExtra("cmdType", "standCMD");
			tmpIntent.putExtra("keySet", "");
			con.sendBroadcast(tmpIntent);
			break;
		default:
			Intent intent = new Intent("com.glsx.bootup.receive.autonavi");
			intent.putExtra("autonaviType", 1); // autonaviType为1：表示直接发起导航请求，
												// autonaviType为2：只进入导航主页面（不发起请求）;
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				con.startActivity(intent);
			} catch (Exception e) {
			}
			break;
		}
    }

    /**
     * 打开胎压
     */
    public String openTirePressure() {
        return "对不起，暂不支持此功能";
    }

    /**
     * 关闭胎压
     */
    public String closeTirePressure() {
        return "对不起，暂不支持此功能";

    }

    /**
     * 打开行车记录仪
     */
    public void openDrivingRecorder() {
    	openApplicationByLauncher("com.android.camera2");
    }

    /**
     * 关闭行车记录仪
     */
    public void closeDrivingRecorder() {
    	Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
        intent.addCategory(Intent.CATEGORY_HOME);
        con.startActivity(intent);
    }

    public boolean openApplicationByLauncher(String pkg) {
            try {
                Intent intent = con.getPackageManager().getLaunchIntentForPackage(pkg);
                con.startActivity(intent);
            } catch (Exception e) {
            }
        return true;
    }



}
