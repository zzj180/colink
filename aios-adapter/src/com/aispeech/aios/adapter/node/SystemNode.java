package com.aispeech.aios.adapter.node;

import java.io.UnsupportedEncodingException;

import android.provider.Settings;
import android.util.Log;

import com.aispeech.ailog.AILog;
import com.aispeech.aimusic.AIMusic;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.bean.RpcRecall;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.control.UITimer;
import com.aispeech.aios.adapter.control.UITimerTask;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.util.MapOperateUtil;
import com.aispeech.aios.adapter.util.StringUtil;
import com.aispeech.aios.adapter.util.SystemOperateUtil;

/**
 * @desc 系统操作相关节点
 * @auth zzj
 * @date 2016-01-13
 */
public class SystemNode extends BaseNode {

    private static final String TAG = "SystemNode";
    private static SystemNode mInstance;
    private RpcRecall mRpcRecall;

    public static synchronized SystemNode getInstance() {
        if (mInstance == null) {
            mInstance = new SystemNode();
        }
        return mInstance;
    }

    @Override
    public String getName() {
        return "system";
    }

    @Override
    public void onMessage(String topic, byte[]... parts) throws Exception {
        super.onMessage(topic, parts);
        AILog.i(TAG, topic, parts);
        if (topic.equals("wakeup.result")) {//唤醒成功
            AILog.d(TAG, "System捕获到唤醒成功的消息");
            SystemOperateUtil.getInstance().openScreen();
        } else if (topic.equals("aios.mute.state")) {
            String state = StringUtil.getEncodedString(parts[0]);
           /* AudioManager audioManager = (AudioManager) AdapterApplication.getContext()
					.getSystemService(Context.AUDIO_SERVICE);
            try {
            	boolean enable = Boolean.parseBoolean(state);
            	audioManager.setStreamMute(AudioManager.STREAM_MUSIC, enable);
			} catch (Exception e) {
			}*/
            Log.d(TAG,"state=" + state);
        }

    }

    @Override
    public void onJoin() {
        super.onJoin();
        bc.subscribe("wakeup.result");
        bc.subscribe("aios.mute.state");
    }

    @Override
    public BusClient.RPCResult onCall(String url, byte[]... bytes) throws Exception {
        AILog.i(TAG, url, bytes);
        mRpcRecall = new RpcRecall(url, bytes);
        if (bytes.length > 0) {
            return new BusClient.RPCResult(onCallCommand(url, StringUtil.getEncodedString(bytes[0])));
        } else {
            return new BusClient.RPCResult(onCallCommand(url, ""));
        }
    }

    /**
     * 根据onCall回调的参数处理交互逻辑
     * @param url     参考onCall回调参数1
     * @param APPName APP名称
     * @return
     * @throws UnsupportedEncodingException
     */
    private String onCallCommand(String url, String APPName) throws UnsupportedEncodingException {
    	 Log.i(TAG, url+APPName);
        UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 1000);
        if (url.equals(AiosApi.System.OPEN_APP)) {//打开第三方APP
            if (APPName.equals("行车记录仪")) {//打开行车记录仪
            	SystemOperateUtil.getInstance().openDrivingRecorder();
                return "行车记录仪打开";
            } else if (APPName.equals("音乐") || APPName.equals("酷我音乐")) {//打开音乐
                UITimer.getInstance().executeAppTask(new UITimerTask() , UITimer.DELAY_MIDDLE);
                AIMusic.play();
            } else if (APPName.equals("地图") || APPName.equals("导航")) {
                MapOperateUtil.getInstance().openMap();
            } else if (APPName.equals("胎压")) {//打开胎压
                return SystemOperateUtil.getInstance().openTirePressure();
            }
        } else if (url.equals(AiosApi.System.CLOSE_APP)) {//关闭第三方APP
            if (APPName.equals("行车记录仪")) {//关闭行车记录仪
               SystemOperateUtil.getInstance().closeDrivingRecorder();
               return "行车记录仪关闭";
            } else if (APPName.equals("导航")) {//关闭导航
            	MapOperateUtil.getInstance().closeMap();
            } else if (APPName.equals("音乐") || APPName.equals("酷我音乐")) {//关闭音乐
                AIMusic.exit();
            } else if (APPName.equals("地图")) {//关闭地图
            	MapOperateUtil.getInstance().closeMap();
            } else if (APPName.equals("胎压")) {//关闭胎压
                return SystemOperateUtil.getInstance().closeTirePressure();
            }
        } else if (url.equals(AiosApi.System.VOLUME_UP)) {//音量调大
            return SystemOperateUtil.getInstance().setVolumeUp();
        } else if (url.equals(AiosApi.System.VOLUME_DOWN)) {//音量调小
            return SystemOperateUtil.getInstance().setVolumeDown();
        } else if (url.equals(AiosApi.System.VOLUME_MUTE)) {//静音
            return SystemOperateUtil.getInstance().setMuteVolume();
        } else if (url.equals(AiosApi.System.VOLUME_UNMUTE)) {//取消静音
            return SystemOperateUtil.getInstance().setUnMuteVolume();
        } else if (url.equals(AiosApi.System.VOLUME_MIN)) {//音量最小
            return SystemOperateUtil.getInstance().setMinVolume();
        } else if (url.equals(AiosApi.System.VOLUME_MAX)) {//音量最大
            return SystemOperateUtil.getInstance().setMaxVolume();
        } else if (url.equals(AiosApi.System.BRIGHTNESS_UP)) {//亮度调大
            try {
                return SystemOperateUtil.getInstance().setScreenBrightnessUp();
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return "调节亮度失败";
            }
        } else if (url.equals(AiosApi.System.BRIGHTNESS_DOWN)) {//亮度调小
            try {
                return SystemOperateUtil.getInstance().setScreenBrightnessDown();
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return "调节亮度失败";
            }
        } else if (url.equals("/system/brightness/min")) {
            try {
                return SystemOperateUtil.getInstance().setScreenBrightnessMin();
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return "调节亮度失败";
            }
        } else if (url.equals("/system/brightness/max")) {
            try {
                return SystemOperateUtil.getInstance().setScreenBrightnessMax();
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return "调节亮度失败";
            }
        } else if (url.equals(AiosApi.System.SCREEN_ON)) {//打开屏幕
            SystemOperateUtil.getInstance().openScreen();
        } else if (url.equals(AiosApi.System.SCREEN_OFF)) {//关闭屏幕
        	SystemOperateUtil.getInstance().openNoDisturb();
        } else if (url.equals(AiosApi.System.MEDIA_PAUSE)) {//暂停播放
            AIMusic.playPause();
        } else if (url.equals(AiosApi.System.MEDIA_PLAY)) {//播放音乐
            //当关键词匹配“播放音乐”时，才会进入该分支
            if (mRpcRecall.getByteList().size() > 0) {//取得指定的播放模式
                String mode = StringUtil.getEncodedString(mRpcRecall.getByteList().get(0));
                AIMusic.setPlayMode(mode);
            }

            UITimer.getInstance().executeAppTask(new UITimerTask() , UITimer.DELAY_MIDDLE);
            AIMusic.play();
        } else if (url.equals(AiosApi.System.MEDIA_PREV)) {//播放上一首
            AIMusic.playPre();
        } else if (url.equals(AiosApi.System.MEDIA_NEXT)) {//播放下一首
            AIMusic.playNext();
        } else if (url.equals(AiosApi.System.MEDIA_STOP)) {//停止播放
            AIMusic.stop();
        } else if (url.equals(AiosApi.System.BLUETOOTH_ON)) {//蓝牙打开
            return SystemOperateUtil.getInstance().openBlueTooth();
        } else if (url.equals(AiosApi.System.BLUETOOTH_OFF)) {//蓝牙关闭
            return SystemOperateUtil.getInstance().closeBlueTooth();
        } else if (url.equals(AiosApi.System.WIFI_ON)) {//WIFI打开
            return SystemOperateUtil.getInstance().setWIFIState(true);
        } else if (url.equals(AiosApi.System.WIFI_OFF)) {//WIFI关闭
            return SystemOperateUtil.getInstance().setWIFIState(false);
        } else if (url.equals(AiosApi.System.HOTSPOT_ON)) {//热点打开
            return SystemOperateUtil.getInstance().setHotSpotState(true);
        } else if (url.equals(AiosApi.System.HOTSPOT_OFF)) {//热点关闭
            return SystemOperateUtil.getInstance().setHotSpotState(false);
        } else if (url.equals(AiosApi.System.DVR_CAPTURE)) {//拍照
            return SystemOperateUtil.getInstance().takePhoto();
        } else if (url.equals(AiosApi.System.FM_ON)) {//FM发射打开
            return SystemOperateUtil.getInstance().openFM();
        } else if (url.equals(AiosApi.System.FM_OFF)) {//FM发射关闭
            return SystemOperateUtil.getInstance().closeFM();
        } else if (url.equals(AiosApi.System.EDOG_ON)) {//电子狗打开
            return SystemOperateUtil.getInstance().openEDog();
        } else if (url.equals(AiosApi.System.EDOG_OFF)) {//电子狗关闭
            return SystemOperateUtil.getInstance().closeEDog();
        } else {//不支持
            return "对不起，暂不支持此功能";
        }
        return "";
    }
}
