package com.aispeech.aios.adapter.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;


import com.aispeech.ailog.AILog;
import com.aispeech.aimusic.AIMusic;
import com.aispeech.aimusic.config.MusicConfig;
import com.aispeech.aios.adapter.AdapterApplication;

import java.lang.reflect.Method;

/**
 * @desc 系统操作工具类
 * @auth zzj
 * @date 2016-01-13
 */
public class SystemOperateUtil {

	public static final String CAMERA2_PKG = "com.android.camera2";
	private Context con;
	private static SystemOperateUtil myUtil;
	private WifiManager mWifiManager;
	private AudioManager mAudioManager;
	private String TAG = "AIOS-SystemOperateUtil";
	private static final String TIPS_MUSIC_MAX = "音量已经调到最大了";
	private static final String TIPS_MUSIC_MIN = "音量已经调到最小了";
	private static final String TIPS_MUSIC_VOLUME = "音量已经调到";
	private static final String TIPS_BRIGHTNESS_MAX = "屏幕亮度已经是最亮了";
	private static final String TIPS_BRIGHTNESS_MIN = "屏幕亮度已经是最暗了";
	private static final String TIPS_BRIGHTNESS_VOLUME = "屏幕亮度已经调到";
	private static final int TIPS_BRIGHTNESS_MAX_VOLUME = 5;
	private static final int TIPS_BRIGHTNESS_MIN_VOLUME = 1;
	private static final int TIPS_MUSIC_LOWER = 1;// 音量降低
	private static final int TIPS_MUSIC_RAISE = 2;// 音量增大
	private static final int TIPS_MUSIC_SETMAX = 3;// 音量最大
	private static final int TIPS_MUSIC_SETMIN = 4;// 音量最小
	private static final int TIPS_BRIGHTNESS_RAISE = 5;
	private static final int TIPS_BRIGHTNESS_LOWER = 6;
	private int cur_volume;;

    public SystemOperateUtil() {
        this.con = AdapterApplication.getContext();
        this.mWifiManager = (WifiManager) con.getSystemService(Context.WIFI_SERVICE);
        this.mAudioManager = (AudioManager) con.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * @return SystemOperateUtil实例
     */
    public static synchronized SystemOperateUtil getInstance() {

        if (myUtil == null) {
            myUtil = new SystemOperateUtil();
        }
        return myUtil;
    }

    /**
     * 设置WIFI开启关闭
     *
     * @param state true开启  false关闭
     */
    public String setWIFIState(boolean state) {
        if (state) {
            String spot = "";
            if (!mWifiManager.isWifiEnabled()) {
                if (getHotPortState()) {
                    spot = setHotSpotState(false);
                }
                mWifiManager.setWifiEnabled(true);
            }
            if ("".equals(spot)) {
                return play("WIFI已打开");
            } else {
                return play(spot + ", WIFI已打开");
            }

        } else {

            if (mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(false);
            }
            return play("WIFI已关闭");
        }

    }

    private boolean getHotPortState() {
        WifiManager wifiManager = (WifiManager) con.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            int i = (Integer) method.invoke(wifiManager);
            AILog.i(TAG, "wifi AP state:  " + i);
            if (i == 13 || i == 14) { //AP state enable or enabling
                return true;
            }
        } catch (Exception e) {
            AILog.e(TAG, "Cannot get WiFi AP state" + e);
        }
        return false;
    }

    /**
     * 设置热点开启关闭
     *
     * @param state
     * @return 开启或者关闭   成功与否
     */
    public String setHotSpotState(boolean state) {

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
                    return play("热点已打开");
                } else {
                    return play("热点打开失败，wifi已关闭");
                }
            } else {
                if (hotPortState) {
                    mWifiManager.setWifiEnabled(PreferenceHelper.getInstance().getWifiState());
                    return play("热点已关闭");
                } else {
                    return play("热点关闭失败");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 降低屏幕亮度 51
     */
    public String setScreenBrightnessDown() throws Settings.SettingNotFoundException {

        int currentBrightness = PreferenceHelper.getInstance().getBrightness() - 51;
        Settings.System.putInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);//设置屏幕亮度调节方式为手动模式
//        currentBrightness = Settings.System.getInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) - 51;//获取当前屏幕亮度-20

        if (currentBrightness < 51) {
            currentBrightness = 51;
        }
        return playBrightnessTips(TIPS_BRIGHTNESS_LOWER, currentBrightness);
    }

    /**
     * 提高屏幕亮度 51
     */
    public String setScreenBrightnessUp() throws Settings.SettingNotFoundException {
        int currentBrightness = PreferenceHelper.getInstance().getBrightness() + 51;
        Settings.System.putInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);//设置屏幕亮度调节方式为手动模式
//        currentBrightness = Settings.System.getInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) + 51;//获取当前屏幕亮度-20

        if (currentBrightness > 255) {
            currentBrightness = 255;
        }
        return playBrightnessTips(TIPS_BRIGHTNESS_RAISE, currentBrightness);
    }


    /**
     * 屏幕亮度调到最暗
     */
    public String setScreenBrightnessMin() throws Settings.SettingNotFoundException {
        return playBrightnessTips(TIPS_BRIGHTNESS_LOWER, 51);
    }

    /**
     * 屏幕亮度调到最亮
     */
    public String setScreenBrightnessMax() throws Settings.SettingNotFoundException {
        return playBrightnessTips(TIPS_BRIGHTNESS_RAISE, 255);
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
		Settings.System.putInt(con.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS, PreferenceHelper
						.getInstance().getBrightness());
	}

	/**
	 * 拍一张照片
	 */
	public String takePhoto() {
		con.sendBroadcast(new Intent("KEY_CAMARA_GET_PICTURE"));
		return "拍照";
	}

	/**
	 * 关闭屏幕
	 */
	public void closeScreen() {
		try {
			int brightness = Settings.System.getInt(con.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
			PreferenceHelper.getInstance().setBrightness(brightness);
			Settings.System.putInt(con.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS, 0);
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

		Intent intent = new Intent("action.colink.startFM");
		intent.putExtra("fm_fq", (fm * 1.0f / 1000));
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
		intent.setClassName("com.zzj.coogo.screenoff",
				"com.zzj.coogo.screenoff.ScrrenoffActivity");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			con.startActivity(intent);
		} catch (Exception e) {
		}
	}

	/**
	 * 打开雷达
	 */
	public void openRADAR() {
		Intent intent = new Intent();
		intent.setAction("com.wanma.action.RADAR_ON");
		con.sendBroadcast(intent);
	}

	/**
	 * 关闭雷达
	 */
	public void closeRADAR() {
		Intent intent = new Intent();
		intent.setAction("com.wanma.action.RADAR_OFF");
		con.sendBroadcast(intent);
	}

	/**
	 * 打开一键导航
	 */
	public void openOneNavi() {
		int navi = Settings.System.getInt(con.getContentResolver(), "ONE_NAVI",
				0);
		switch (navi) {
		case 1:
			ComponentName componetName = new ComponentName("com.coagent.app",
					"com.coagent.activity.MainActivity");
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
			if (APPUtil.getInstance().isInstalled("com.share.android")) {
				Intent intent = new Intent("tianan.cloudcall.action.CALL");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					con.startActivity(intent);
				} catch (Exception e) {
				}
			}else{
				Intent intent = new Intent("com.glsx.bootup.receive.autonavi");
				intent.putExtra("autonaviType", 1); // autonaviType为1：表示直接发起导航请求，
													// autonaviType为2：只进入导航主页面（不发起请求）;
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					con.startActivity(intent);
				} catch (Exception e) {
				}
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
		openApplicationByLauncher(CAMERA2_PKG);
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

	/**
	 * 增大多媒体音量
	 */
	public String setVolumeUp() {
		return playSoundTips(TIPS_MUSIC_RAISE);
	}

	/**
	 * 降低多媒体音量
	 */
	public String setVolumeDown() {
		return playSoundTips(TIPS_MUSIC_LOWER);
	}

	/**
	 * 设置静音
	 */
	public String setMuteVolume() {
		setMute(true);
		return "媒体音已静音";
	}

	/**
	 * 取消静音
	 */
	public String setUnMuteVolume() {
		setMute(false);
		return "媒体音已取消静音";
	}

	private void setMute(boolean mute) {
		/*if (mute) {
			if (!isMute) {
				isMute = mute;
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
			}
		} else {
			isMute = mute;
			mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
		}
		
		AudioManager audioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
*/		if(mute){
			int pre_volune = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
			if(pre_volune!=0){
				cur_volume = pre_volune;
			}
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0 , 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0 , 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,0 , 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_RING,0 , 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,0 , 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,0 , 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,0 , 0);
		}else{
			mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
			int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if(currentVolume==0){
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,cur_volume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, cur_volume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,cur_volume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_RING,cur_volume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,cur_volume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,cur_volume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,cur_volume/2, 0);
			}
		}
	}

	/**
	 * 音量最大
	 */

	public String setMaxVolume() {
		return playSoundTips(TIPS_MUSIC_SETMAX);
	}

	/***
	 * 音量最小
	 */
	public String setMinVolume() {
		return playSoundTips(TIPS_MUSIC_SETMIN);
	}

	// 音量调节播报
	private String playSoundTips(final int type) {
		setMute(false);
		int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM); // 系统当前音量/3
		AILog.d("volume=" + currentVolume);
		String platform = SystemPropertiesProxy.get(con,AdapterApplication.KEY_PLATFORM);
		switch (type) {
		case TIPS_MUSIC_RAISE:// 增大音量
			if (TextUtils.isEmpty(platform)) {
				if (currentVolume >= 7) {
					currentVolume = 7;
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume * 2, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
					return play(TIPS_MUSIC_MAX);
				} else {
					currentVolume++;
					// PreferenceHelper.getInstance().setVolume(currentVolume *
					// 3);
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume * 2, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
					return play(TIPS_MUSIC_VOLUME + currentVolume);
				}
			} else {
				if (currentVolume > 11) {
					currentVolume = 12;
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,6, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume, 0);
					return play(TIPS_MUSIC_MAX);
				} else {
					currentVolume++;
					// PreferenceHelper.getInstance().setVolume(currentVolume *
					// 3);
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currentVolume/2, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume, 0);
					return play(TIPS_MUSIC_VOLUME + currentVolume);
				}
			}
		case TIPS_MUSIC_LOWER:// 降低音量
			if (TextUtils.isEmpty(platform)) {
				if (currentVolume <= 0) {
					currentVolume = 0;
					// 静音了播报无作用
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume * 2, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
					return play(TIPS_MUSIC_MIN);
				} else {
					--currentVolume;
					// PreferenceHelper.getInstance().setVolume(currentVolume *
					// 3);
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume * 2, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
					return play(TIPS_MUSIC_VOLUME + currentVolume);

				}
			} else {
				if (currentVolume <= 0) {
					currentVolume = 0;
					// 静音了播报无作用
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume, 0);
					return play(TIPS_MUSIC_MIN);
				} else {
					--currentVolume;
					// PreferenceHelper.getInstance().setVolume(currentVolume *
					// 3);
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currentVolume/2, 0);
					mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume, 0);
					return play(TIPS_MUSIC_VOLUME + currentVolume);

				}
			}
		case TIPS_MUSIC_SETMAX:// 音量最大
			if (TextUtils.isEmpty(platform)) {
				currentVolume = 7;
				// PreferenceHelper.getInstance().setVolume(currentVolume * 3);
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume * 2, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,currentVolume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
			} else {
				currentVolume = 12;
				// PreferenceHelper.getInstance().setVolume(currentVolume * 3);
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,currentVolume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,currentVolume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,6, 0);
				mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume, 0);
			}
			return play(TIPS_MUSIC_MAX);
		case TIPS_MUSIC_SETMIN:// 音量最小
			currentVolume = 0;
			// PreferenceHelper.getInstance().setVolume(currentVolume * 3);
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					currentVolume * 2, 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
					currentVolume, 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
					currentVolume, 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentVolume, 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currentVolume, 0);
			mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,currentVolume, 0);
			return play(TIPS_MUSIC_MIN);
		default:
			break;
		}
		return null;

	}

	// 屏幕亮度调节播报
	private String playBrightnessTips(int type, int brightness) {
		brightness = brightness / 51;
		if (brightness < TIPS_BRIGHTNESS_MIN_VOLUME) {
			brightness = TIPS_BRIGHTNESS_MIN_VOLUME;
		}
		AILog.i(TAG, "currentBrightness：" + (brightness * 51));
		Settings.System.putInt(con.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS, brightness * 51);// 设置亮度值
		PreferenceHelper.getInstance().setBrightness(brightness * 51);
		switch (type) {
		case TIPS_BRIGHTNESS_RAISE:
			if (brightness == TIPS_BRIGHTNESS_MAX_VOLUME) {
				return play(TIPS_BRIGHTNESS_MAX);
			} else {
				return play(TIPS_BRIGHTNESS_VOLUME + brightness);
			}
		case TIPS_BRIGHTNESS_LOWER:
			if (brightness == TIPS_BRIGHTNESS_MIN_VOLUME) {
				return play(TIPS_BRIGHTNESS_MIN);
			} else {
				return play(TIPS_BRIGHTNESS_VOLUME + brightness);
			}
		default:
			break;
		}
		return null;
	}

	private String play(String text) {
		// 等待播报语音可以播报的接口
		AILog.d("", "text=" + text);
		return text;
	}

	/**
	 * 打开软件
	 */

	public boolean openApplication(String pkg, String cls) {
		boolean isOperate = true;
		if (APPUtil.getInstance().isInstalled(pkg)) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			ComponentName cn = new ComponentName(pkg, cls);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setComponent(cn);
			con.startActivity(intent);

			if (pkg.equals(MusicConfig.Package.MUSIC_KW)) {
				AIMusic.play();
			}
		} else {
			Toast.makeText(con, "在本地找不到此软件！", Toast.LENGTH_LONG).show();
			isOperate = false;
		}
		return isOperate;
	}

	public boolean openApplicationByLauncher(String pkg) {
		if (APPUtil.getInstance().isInstalled(pkg)) {
			try {
				Intent intent = con.getPackageManager()
						.getLaunchIntentForPackage(pkg);
				con.startActivity(intent);
			} catch (Exception e) {
				AILog.e(TAG, e.toString());
			}
		}
		return true;
	}

	/**
	 * 关闭软件
	 */
	public boolean closeApplication(String pkg) {
		if (!APPUtil.getInstance().isInstalled(pkg)) {
			Toast.makeText(con, "本地找不到此应用...", Toast.LENGTH_LONG).show();
			AILog.d(TAG, "在本地找不到此应用！");
			return false;
		}
		if (APPUtil.getInstance().isRunning(pkg)) {
			AILog.d(TAG, "kill application!!!");
			APPUtil.getInstance().killProcess(pkg);
			return true;
		} else {
			Toast.makeText(con, "此应用没有运行...", Toast.LENGTH_LONG).show();
			AILog.d(TAG, "无运行中的此应用");
			return false;
		}
	}

}
