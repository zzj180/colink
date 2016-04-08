package com.aispeech.aios.adapter.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.node.CommonPoiNode;
import com.aispeech.aios.adapter.node.HomeNode;
import com.aispeech.aios.adapter.node.MusicNode;
import com.aispeech.aios.adapter.node.PhoneNode;
import com.aispeech.aios.adapter.node.TTSNode;
import com.aispeech.aios.adapter.service.FloatWindowService;
import com.aispeech.aios.adapter.ui.MyWindowManager;
import com.aispeech.aios.adapter.util.APPUtil;
import com.aispeech.aios.adapter.util.AssetsXmlUtil;
import com.aispeech.aios.adapter.util.NotificationUtil;
import com.aispeech.aios.adapter.util.PreferenceHelper;
import com.aispeech.aios.adapter.util.SendBroadCastUtil;

/**
 * @desc adapter的接收器
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "AIOS-MyReceiver";
    private Service service;
    private final String keyAttrConfigFile = "configs/cfgs.xml";

    @Override
    public void onReceive(final Context context, Intent intent) {
        AILog.i(TAG, "onReceive - " + intent.getAction());

        String action = intent.getAction();
        service = FloatWindowService.getRunningService();

        if (action.startsWith("aios.intent.action.STATE")) {
            String state = intent.getStringExtra("aios.intent.extra.STATE");
            if (state.equals("awake")) {
                if (FloatWindowService.getRunningService() == null) {
                    startFloatWindowsService(context);
                }
                if (APPUtil.getInstance().isSystemUid()) {
                    MyWindowManager.getInstance().setBackgroundBlur();
                }
            } else if (state.equals("ready")) {
                AILog.i(TAG, "aios is ready");

                //查询蓝牙连接状态
                AdapterApplication.getContext().sendBroadcast(new Intent(PhoneNode.AIOS_BT_STATUS_REQ));
                MusicNode.getInstance().onAiosStarted();

                if (TTSNode.getInstance().getBusClient() == null) {
                    TTSNode.getInstance().start();
                }

                String jsonStr = AssetsXmlUtil.readXmlFile(AdapterApplication.getContext(), keyAttrConfigFile);
                String basicAttr = AssetsXmlUtil.getBasicAttrs(jsonStr);
                String cluStatus = AssetsXmlUtil.getClusterStatus(jsonStr);
                int volume = AssetsXmlUtil.getTtsVolume(jsonStr);

                TTSNode.getInstance().call("/keys/modules/include", "set", basicAttr);
                TTSNode.getInstance().call("/keys/sds/cluster", "set", cluStatus);
                TTSNode.getInstance().call("/keys/tts/volume", "set", String.valueOf(volume));

                BusClient.RPCResult result = TTSNode.getInstance().call("/keys/modules/include", "get");
                if (result != null && result.retval != null) {
                    try {
                        AILog.json(TAG, new String((result.retval == null) ? "unknown".getBytes() : result.retval, "utf-8"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        AILog.i(TAG, "have no get modules status");
                    }
                }

                String jsonstr = "[" +
                        "{\"folder\":\"/mnt/sdcard/Music\"},\n" +
                        "{\"folder\":\"/mnt/sdcard/kwmusiccar/Song\"}\n" +
                        "]";

                AILog.i(TAG, "The paths below will be scan for music  " + jsonstr);
                Intent jsonIntent = new Intent("aios.intent.action.LOCAL_MUSIC_SCAN");
                jsonIntent.putExtra("aios.intent.extra.TEXT", jsonstr);
                AdapterApplication.getContext().sendBroadcast(jsonIntent);
            }


        } else if (action.equals("com.android.action_acc_off")) {
            //休眠
            AILog.e(TAG, "acc_off__________________________");
            SendBroadCastUtil.getInstance().sendBroadCast("com.aispeech.acc.status", "status", "off");
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    AILog.e(TAG, "acc_off__stopservice________________________");
//                    context.stopService(new Intent(context, FloatWindowService.class));
//                }
//            }, 5000);

        } else if (action.equals("com.android.action_acc_on")) {
            //唤醒
            AILog.e(TAG, "acc_on__________________________");
//            context.startService(new Intent(context, FloatWindowService.class));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AILog.e(TAG, "acc_on__startservice________________________");
                    SendBroadCastUtil.getInstance().sendBroadCast("com.aispeech.acc.status", "status", "on");
                }
            }, 5000);
        }
        //回家
        else if (action.equals(AiosApi.Other.ACTION_SLIDE_LEFT)) {
            CommonPoiNode.getInstance().go("我要回家");
        }
        //去公司
        else if (action.equals(AiosApi.Other.ACTION_SLIDE_RIGHT)) {
            CommonPoiNode.getInstance().go("导航去公司");
        } else if (action.equals("aios.intent.action.NOTIFY_WECHAT_RESUME")) {//微信APP栈顶，悬浮窗不显示
            MyWindowManager.getInstance().removeSmallWindow();
        } else if (action.equals("aios.intent.action.NOTIFY_WECHAT_PAUSE")) {//微信APP不在栈顶，悬浮窗显示
        }//微信接人
        else if (action.equals("aios.intent.action.WECHAT_PICK")) {
            BusClient bc = HomeNode.getInstance().getBusClient();
            if (bc != null) {
                BusClient.RPCResult rpc = bc.call("/keys/wakeup/allow", "get");
                try {
                    String status = new String((rpc.retval == null) ? "unknown".getBytes() : rpc.retval, "utf-8");
                    AILog.d(TAG, "status=" + status);
                    if ("enable".equals(status) || "nil".equals(status)) {
                        String msg = intent.getStringExtra("latlng");//消息格式：经度#纬度
                        if (null != msg) {
                            String res[] = msg.split("#");
                            if (res.length == 2) {
                                NotificationUtil mNotificationUtil = NotificationUtil.getInstance();
                                mNotificationUtil.setPoi(res[0], res[1]);
                                mNotificationUtil.publishPickUp("收到公众平台的一条接人消息,请问是导航还是取消?");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (action.equals("android.media.VOLUME_CHANGED_ACTION")) {
            AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);// 当前的媒体音量
            AILog.d("VOLUME_CHANGED_ACTION" + currVolume);
            if (currVolume != 0) {
                PreferenceHelper.getInstance().setVolume(currVolume);
            }
        }
    }

    private void startFloatWindowsService(Context context) {
        Intent i = new Intent("android.intent.action.START_FLOATWINDOW");
        context.startService(i);
        AILog.i(TAG, "start float window service");
    }
}