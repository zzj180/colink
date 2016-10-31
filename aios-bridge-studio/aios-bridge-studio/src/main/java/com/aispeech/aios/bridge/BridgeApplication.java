package com.aispeech.aios.bridge;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.bridge.constant.Constants;
import com.aispeech.aios.bridge.oem.RomSystemSetting;
import com.aispeech.aios.bridge.utils.APPUtil;
import com.aispeech.aios.bridge.utils.FloatView;
import com.aispeech.aios.bridge.utils.Logger;
import com.aispeech.aios.bridge.utils.PreferenceUtil;
import com.aispeech.aios.bridge.utils.SystemDefaultUtil;
import com.aispeech.aios.bridge.utils.SystemPropertiesProxy;
import com.aispeech.aios.common.bean.MajorWakeup;
import com.aispeech.aios.common.config.PkgName;
import com.aispeech.aios.common.property.StatusProperty;
import com.aispeech.aios.common.property.SystemProperty;
import com.aispeech.aios.sdk.AIOSForCarSDK;
import com.aispeech.aios.sdk.bean.Command;
import com.aispeech.aios.sdk.bean.ShortcutWakeup;
import com.aispeech.aios.sdk.listener.AIOSCustomizeListener;
import com.aispeech.aios.sdk.listener.AIOSPhoneListener;
import com.aispeech.aios.sdk.listener.AIOSRadioListener;
import com.aispeech.aios.sdk.listener.AIOSReadyListener;
import com.aispeech.aios.sdk.listener.AIOSSettingListener;
import com.aispeech.aios.sdk.listener.AIOSStatusListener;
import com.aispeech.aios.sdk.listener.AIOSSystemListener;
import com.aispeech.aios.sdk.manager.AIOSCustomizeManager;
import com.aispeech.aios.sdk.manager.AIOSPhoneManager;
import com.aispeech.aios.sdk.manager.AIOSRadioManager;
import com.aispeech.aios.sdk.manager.AIOSSettingManager;
import com.aispeech.aios.sdk.manager.AIOSStatusManager;
import com.aispeech.aios.sdk.manager.AIOSSystemManager;
import com.aispeech.aios.sdk.manager.AIOSTTSManager;
import com.aispeech.aios.sdk.manager.AIOSUIManager;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义Application类
 */
public class BridgeApplication extends Application{
    private static final String TAG = "Bridge - BridgeApplication";
    private static Context mContext;
    private static final String MAP_INDEX = "MAP_INDEX";
    private static final String BACK_CAR_STATE = "back_car_state";
    private static final String TTS_SHOW = "tts_show";
    public static final String ACC_STATE = "acc_state";
    public static Handler uiHandler;
    public static boolean isMute;
    private FloatView mFloatView;
    private boolean isInit;
    private PowerManager.WakeLock wl;
  //  private String curActivity;
 //   private boolean canResume;
    public static Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("Unknown Error");
        }
        return mContext;
    }

    public static BridgeApplication getApplication() {
        if (mContext == null) {
            throw new RuntimeException("Unknown Error");
        }
        return (BridgeApplication) mContext;
    }

    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent localIntent = new Intent("aios.intent.action.UI_MIC_CLICK");
            localIntent.setFlags(32);
            mContext.sendBroadcast(localIntent);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        isInit = false;
        mContext = this;

        AIOSForCarSDK.initialize(this, new AIOSReadyListener() {
            @Override
            public void onAIOSReady() {
                Logger.d("On aios ready...");
                if(!isInit) {
                    isInit = true;
                    initAios();
                }

            }
        });

        uiHandler = new Handler(Looper.getMainLooper());
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(MAP_INDEX), false,
                new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        setMap();
                    }

                });

        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(ACC_STATE), true,
                new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        setACC();
                    }
                });

      //  isBackCar();
        getContentResolver().registerContentObserver(Settings.System.getUriFor(BACK_CAR_STATE), true,
                new ContentObserver(new Handler(getMainLooper())) {
                    @Override
                    public void onChange(boolean selfChange) {
                        isBackCar();
                    }

                });

        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isInit) {
                    isInit = true;
                    initAios();
                }
            }
        },8000);

        //悬浮麦克风
        mFloatView = FloatView.getInstance(this);
        mFloatView.setOnClickListener(mOnClick);
        String processName = getCurProcessName(this);
        if (!TextUtils.isEmpty(processName)) {
            boolean defaultProcess = processName.equals("com.aispeech.aios.bridge");
            if (defaultProcess) {
                if (!mFloatView.isShown()) {
                    mFloatView.show();
                }
            }
        }
    }

    private void initAios() {
        Logger.d("initAios...");
        //使原生快捷唤AIOSCustomizeManager.getInstance().setLaunchTips(getString(R.string.launch_tips,wakeupZh));醒词生效
        AIOSCustomizeManager.getInstance().setNativeShortcutWakeupsEnabled(true);
        //定制录音机
        boolean aec = SystemPropertiesProxy.getBoolean(BridgeApplication.this,"ro.voice.aec.switch",false);
        boolean interrupt = SystemPropertiesProxy.getBoolean(BridgeApplication.this,"ro.voice.interrupt.switch",false);
        AIOSCustomizeManager.getInstance().customizeRecorder(aec, interrupt, false);
        //语音启动语

        //定制主唤醒词
        String defWakeupZh = "你好狼仔";
        String defWakeupPy = "ni hao lang zai";
        String wakeupZh = SystemPropertiesProxy.getString(BridgeApplication.this,"ro.colink.voice.name",defWakeupZh);
        String wakeupPy = SystemPropertiesProxy.getString(BridgeApplication.this,"ro.colink.voice.pinyin",defWakeupPy);

        String wakeupZh_secondary= SystemPropertiesProxy.get(BridgeApplication.this,"ro.voice.name.secondary");
        String wakeupPy_secondary = SystemPropertiesProxy.get(BridgeApplication.this,"ro.voice.pinyin.secondary");
        List<MajorWakeup> majorWakeups= new ArrayList<MajorWakeup>();
        float confidence = 0.10f;
        majorWakeups.add(new MajorWakeup( wakeupZh, wakeupPy, confidence));
        if(!TextUtils.isEmpty(wakeupZh_secondary) && !TextUtils.isEmpty(wakeupPy_secondary)){
            majorWakeups.add(new MajorWakeup( wakeupZh_secondary, wakeupPy_secondary, confidence));
        }
        Logger.d("wakeup =" + majorWakeups);
        try{
            AIOSCustomizeManager.getInstance().setMajorWakeup(majorWakeups);
        } catch (InvalidParameterException e) {
            e.printStackTrace();
            majorWakeups.clear();
            majorWakeups.add(new MajorWakeup( defWakeupZh, defWakeupPy, confidence));
            AIOSCustomizeManager.getInstance().setMajorWakeup(majorWakeups);
        }

        //定制悬浮窗为全屏
           /*     WindowManager.LayoutParams layoutParams = AIOSUIManager.getInstance().obtainLayoutParams();
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                AIOSUIManager.getInstance().setLayoutParams(layoutParams);*/

        //设置音频管理监听器，请实现或者使用以下监听器
    //    AIOSAudioManager.getInstance().registerAudioListener(new BridgeAudioListener());
        //如果静态注册了自定义命令，请注册以下监听器
        AIOSCustomizeManager.getInstance().disableNativeShortcutWakeup(new String[]{
                "tui chu dao hang",
                "guan bi dao hang",
                "ting zhi dao hang"});

        AIOSCustomizeManager.getInstance().registerCustomizeListener(customizeListener);
        registCommands();

        AIOSRadioManager.getInstance().registerRadioListener(aiosListener);
        AIOSPhoneManager.getInstance().registerPhoneListener(phoneListener);
        setMap();
        AIOSPhoneManager.getInstance().setBTStatus(true);
        AIOSSystemManager.getInstance().registerSystemListener(aiosSystemListener);
        AIOSStatusManager.getInstance().registerStatusListener(aiosStatusListener);
        AIOSSettingManager.getInstance().registerSettingListener(aiosSettingListener);
        AIOSUIManager.getInstance().setShowWechatPublicQrcode(false);
    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    private void registCommands(){

        List<Command> commands = new ArrayList<Command>();
      /*  Command closeDog = new Command(Constants.CLOSE_EDOG,getResources().getStringArray(R.array.close_edog));
        Command openeDog = new Command(Constants.OPEN_EDOG,getResources().getStringArray(R.array.open_edog));*/
        Command closeRadar = new Command(Constants.CLOSE_RADAR,getResources().getStringArray(R.array.close_radar));
        Command openRadar= new Command(Constants.OPEN_RADAR,getResources().getStringArray(R.array.open_radar));
        Command closeFM = new Command(Constants.CLOSE_FM, getResources().getStringArray(R.array.close_fm));
        Command openFM= new Command(Constants.OPEN_FM,getResources().getStringArray(R.array.open_fm));
        Command noDisturb = new Command(Constants.OPEN_SCREENOFF,getResources().getStringArray(R.array.open_screenOff));
        Command oneNavi = new Command(Constants.ONE_NAVI,getResources().getStringArray(R.array.one_navi));
        Command closeRcorder = new Command(Constants.CLOSE_CARCORDER, getResources().getStringArray(R.array.close_carcorder));
        Command closeBluttooth = new Command(Constants.CLOSE_BLUETOOTH, getResources().getStringArray(R.array.close_bluetooth));
        Command openBluetooth = new Command(Constants.OPEN_BLUETOOTH, getResources().getStringArray(R.array.open_bluetooth));
        Command goHome = new Command(Constants.HOME_PAGE, getResources().getStringArray(R.array.home_page));
        Command closeNavi = new Command(Constants.CLOSE_NAVI, getResources().getStringArray(R.array.close_navi));
        //注册单个命令，注册多个消息请见regCommands(List<Command>)
       /* commands.add(closeDog);
        commands.add(openeDog);*/
        commands.add(noDisturb);
        commands.add(closeRadar);
        commands.add(openRadar);
        commands.add(closeFM);
        commands.add(openFM);
        commands.add(oneNavi);
        commands.add(closeRcorder);
        commands.add(closeBluttooth);
        commands.add(openBluetooth);
        commands.add(goHome);
        commands.add(closeNavi);
        AIOSCustomizeManager.getInstance().regCommands(commands);

        List<ShortcutWakeup> shortcutWakeupList = new ArrayList<ShortcutWakeup>();
        shortcutWakeupList.add(new ShortcutWakeup(Constants.CAPTURE_PICTURE_ASR, 0.12f));
        AIOSCustomizeManager.getInstance().registerShortcutWakeups(shortcutWakeupList);

    }


    AIOSCustomizeListener customizeListener = new AIOSCustomizeListener() {
        @Override
        public void onCommandEffect(@NonNull String data) {
            Logger.d(data);
           /* if (Constants.CLOSE_EDOG.equals(data)) {
                RomSystemSetting.closeEDog(BridgeApplication.getContext());
            } else if (Constants.OPEN_EDOG.equals(data)) {
                RomSystemSetting.openEDog(BridgeApplication.getContext());

            } else */if (Constants.OPEN_SCREENOFF.equals(data)) {
      //          canResume = false;
                RomSystemSetting.openNoDisturb(BridgeApplication.getContext());
                AIOSTTSManager.speak("进入免打扰");
            }else if (Constants.CLOSE_RADAR.equals(data)) {
                RomSystemSetting.closeRADAR(BridgeApplication.getContext());
                AIOSTTSManager.speak("雷达已关闭");
            } else if (Constants.OPEN_RADAR.equals(data)) {
                RomSystemSetting.openRADAR(BridgeApplication.getContext());
                AIOSTTSManager.speak("雷达已打开");
            } else if (Constants.CLOSE_FM.equals(data)) {
                RomSystemSetting.closeFM(BridgeApplication.getContext());
            } else if (Constants.OPEN_FM.equals(data)) {
                RomSystemSetting.openFM(BridgeApplication.getContext());
            } else if (Constants.ONE_NAVI.equals(data)) {
     //           canResume = false;
                RomSystemSetting.openONENavi(BridgeApplication.getContext());
            } else if (Constants.CLOSE_CARCORDER.equals(data)) {
                try {
                    @SuppressWarnings("deprecation")
                    List<ActivityManager.RunningTaskInfo> tasksInfo = ((ActivityManager) BridgeApplication.getContext()
                            .getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
                    // 应用程序位于堆栈的顶层
                    if (tasksInfo != null) {
                        String className = tasksInfo.get(0).topActivity.getClassName();
                        if (Constants.CAMERA_LAUNCHER.equals(className)
                                || Constants.CAMERA_ACTIVITY.equals(className)) {
          //                  canResume = false;
                            RomSystemSetting.homePage(BridgeApplication.getContext());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (Constants.OPEN_BLUETOOTH.equals(data)) {
                RomSystemSetting.setBluetoothEnabled(mContext,true);
                AIOSTTSManager.speak("蓝牙已打开");
            } else if (Constants.CLOSE_BLUETOOTH.equals(data)) {
                RomSystemSetting.setBluetoothEnabled(mContext,false);
                AIOSTTSManager.speak("蓝牙已关闭");
            } else if (Constants.HOME_PAGE.equals(data)) {
    //            canResume = false;
                RomSystemSetting.homePage(mContext);
                AIOSTTSManager.speak("已为你返回桌面");
            }  else if (Constants.CLOSE_NAVI.equals(data)) {
    //            canResume = false;
                RomSystemSetting.closeNavi(mContext);
            }   /*else if("OPEN_AIQIYI".equals(data)){

            try {
                APPUtil.lanchApp(BridgeApplication.getContext(), "com.qiyi.video.pad");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        }
        @Override
        public void onShortcutWakeUp(@NonNull String pinyin) {
            Logger.d(pinyin);
            if (Constants.CAPTURE_PICTURE_ASR.equals(pinyin)) {
                if (APPUtil.getInstance().isInstalled(APPUtil.CAMERA_PKG)) {
                    BridgeApplication.getContext().sendBroadcast(new Intent("action.colink.take.picture"));
                } else {
                    BridgeApplication.getContext().sendBroadcast(new Intent("android.intent.action.TAKE_PICTURE"));
                }
            }
        }
    };


    AIOSRadioListener aiosListener = new AIOSRadioListener() {
        /**
         * 执行调频操作
         *
         * @param frequency FM频段
         */
        @Override
        public void onFMPlay(double frequency) {
            AILog.i(TAG, "调频到："+frequency);
            int fm = (int) (frequency * 1000);
            RomSystemSetting.launchFM(fm, BridgeApplication.getContext());
        }

        /**
         * 执行调幅操作
         *
         * @param frequency AM频段
         */
        @Override
        public void onAMPlay(double frequency) {
            AILog.i(TAG, "调幅到："+frequency);
        }
    };

    AIOSPhoneListener phoneListener = new AIOSPhoneListener() {
        @Override
        public boolean getBTStatus() {
            return true;
        }

        @Override
        public void rejectIncoming() {
            AIOSPhoneManager.getInstance().callEnd();
            Logger.d("已拒接来电");
            RomSystemSetting.RomCustomHANGUP(mContext);
        }

        @Override
        public void acceptIncoming() {
            AIOSPhoneManager.getInstance().callOffhook();
            Logger.d("已接听来电");
            RomSystemSetting.RomCustomAnswerCall(mContext);
        }

        @Override
        public void makeCall(String name, String number) {

            AIOSPhoneManager.getInstance().setBTStatus(true);
            int enable = 1;
            try {
                Cursor query = mContext.getContentResolver().query(Uri.parse("content://com.colink.bluetoothe/bluetootheonline"),null, null, null, null);
                if (query != null) {
                    if (query.moveToNext()) {
                        enable = query.getInt(query.getColumnIndex("support"));
                    }
                    query.close();
                }
            } catch (Exception e) {
            }

            // int enable =
            // SystemPropertiesProxy.getInt(mContext,"ro.product.btmodule",1);
            if (enable == 1) {
                RomSystemSetting.RomCustomDialNumber(mContext,number);
            } else {
        //        canResume = false;
                Intent tmpIntent = new Intent("com.android.ecar.recv");
                tmpIntent.putExtra("ecarSendKey", "VoipMakeCall");
                tmpIntent.putExtra("cmdType", "standCMD");
                tmpIntent.putExtra("keySet", "name,number");
                tmpIntent.putExtra("name", name);
                tmpIntent.putExtra("number", number);
                mContext.sendBroadcast(tmpIntent);
            }
        }

        @Override
        public String onSyncContactsResult(boolean isSuccess) {
            if (isSuccess) {
                //do something 联系人同步成功
                return "联系人同步成功";
            } else {
                //do something 联系人同步失败
                return "联系人同步失败";
            }
        }
    };

   AIOSSystemListener aiosSystemListener = new AIOSSystemListener() {
       @Override
       public void onOpenApp(@NonNull String packageName) {
           try {
               APPUtil.lanchApp(BridgeApplication.this,packageName);
           } catch (Exception e) {
               e.printStackTrace();
           }
       }

       @Override
       public void onCloseApp(@NonNull String packageName) {

       }

       @Nullable
        @Override
        public String onSetVolume(String changeType) {
            String text;
            if (changeType.equals(SystemProperty.VolumeProperty.VOLUME_RAISE)) {
                text =SystemDefaultUtil.getInstance().setVolumeUp();

            } else if (changeType.equals(SystemProperty.VolumeProperty.VOLUME_LOWER)) {
                text = SystemDefaultUtil.getInstance().setVolumeDown();

            } else if (changeType.equals(SystemProperty.VolumeProperty.VOLUME_MAX)) {
                text = SystemDefaultUtil.getInstance().setMaxVolume();

            } else if (changeType.equals(SystemProperty.VolumeProperty.VOLUME_MIN)) {
                text = SystemDefaultUtil.getInstance().setMinVolume();

            } else if (changeType.equals(SystemProperty.VolumeProperty.VOLUME_MUTE)) {
                text =  SystemDefaultUtil.getInstance().setMuteVolume();

            } else if (changeType.equals(SystemProperty.VolumeProperty.VOLUME_UNMUTE)) {
                text = SystemDefaultUtil.getInstance().setUnMuteVolume();

            } else {
                text = "暂不支持此功能";

            }
            return text;
        }

        @Nullable
        @Override
        public String onSetBrightness(String changeType) {
            String text;
            try{
                if (changeType.equals(SystemProperty.BrightnessProperty.BRIGHTNESS_RAISE)) {
                    text = SystemDefaultUtil.getInstance().setScreenBrightnessUp();

                } else if (changeType.equals(SystemProperty.BrightnessProperty.BRIGHTNESS_LOWER)) {
                    text =  SystemDefaultUtil.getInstance().setScreenBrightnessDown();

                } else if (changeType.equals(SystemProperty.BrightnessProperty.BRIGHTNESS_MAX)) {
                    text = SystemDefaultUtil.getInstance().setScreenBrightnessMax();

                } else if (changeType.equals(SystemProperty.BrightnessProperty.BRIGHTNESS_MIN)) {
                    text = SystemDefaultUtil.getInstance().setScreenBrightnessMin();

                } else {
                    text = "暂不支持此功能";

                }
                return text;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "暂不支持此功能";
        }

        @Nullable
        @Override
        public String onOpenState(String openName) {
            String text;
            if (openName.equals(SystemProperty.CommonStateProperty.STATE_BLUETOOTH)) {
                RomSystemSetting.setBluetoothEnabled(mContext,true);
                text = "蓝牙已打开";

            } else if (openName.equals(SystemProperty.CommonStateProperty.STATE_WIFI)) {
                SystemDefaultUtil.getInstance().setWIFIState(true);
                text = "WIFI已打开";

            } else if (openName.equals(SystemProperty.CommonStateProperty.STATE_HOT)) {
                SystemDefaultUtil.getInstance().setHotSpotState(true);
                text = "热点已打开";

            } else if (openName.equals(SystemProperty.CommonStateProperty.STATE_SCREEN)) {
                text =  SystemDefaultUtil.getInstance().openScreen();

            } else if (openName.equals(SystemProperty.CommonStateProperty.STATE_EDOG)) {
                RomSystemSetting.openEDog(BridgeApplication.getContext());
                text = "电子狗已打开";

            } else if (openName.equals(SystemProperty.CommonStateProperty.STATE_DRIVING_RECORDER)) {
                text = RomSystemSetting.openDriveRecorder(getApplicationContext(),"行车记录仪");

            }/* else if (openName.equals(SystemProperty.CommonStateProperty.STATE_TIRE_PRESSURE)) {
                text = "胎压已打开";

            }*/ else {
                text = "暂不支持此功能";

            }
            return text;
        }

        @Nullable
        @Override
        public String onCloseState(String closeName) {
            String text;
            if (closeName.equals(SystemProperty.CommonStateProperty.STATE_BLUETOOTH)) {
                RomSystemSetting.setBluetoothEnabled(mContext,false);
                text = "蓝牙已关闭";

            } else if (closeName.equals(SystemProperty.CommonStateProperty.STATE_WIFI)) {
                SystemDefaultUtil.getInstance().setWIFIState(false);
                text = "WIFI已关闭";

            } else if (closeName.equals(SystemProperty.CommonStateProperty.STATE_HOT)) {
                SystemDefaultUtil.getInstance().setHotSpotState(false);
                text = "热点已关闭";

            } else if (closeName.equals(SystemProperty.CommonStateProperty.STATE_SCREEN)) {
               text = SystemDefaultUtil.getInstance().closeScreen();

            } else if (closeName.equals(SystemProperty.CommonStateProperty.STATE_EDOG)) {
                RomSystemSetting.closeEDog(BridgeApplication.getContext());
                text = "电子狗已关闭";

            }/* else if (closeName.equals(SystemProperty.CommonStateProperty.STATE_DRIVING_RECORDER)) {
                text = "行车记录仪已关闭";

            } else if (closeName.equals(SystemProperty.CommonStateProperty.STATE_TIRE_PRESSURE)) {
                text = "胎压已关闭";

            }*/ else {
                text = "暂不支持此功能";

            }
            return text;
        }

        @Nullable
        @Override
        public String onSnapShot() {
            if(APPUtil.getInstance().isInstalled(APPUtil.CAMERA_PKG)){
                BridgeApplication.getContext().sendBroadcast(new Intent("action.colink.take.picture"));
            }else {
                BridgeApplication.getContext().sendBroadcast(new Intent("android.intent.action.TAKE_PICTURE"));
            }
            return "茄子";
        }
    };

    AIOSStatusListener aiosStatusListener = new AIOSStatusListener() {
        @Override
        public void onVadVolumeChange(float volumeValue) {

        }

        @Override
        public void onListeningChange(String status) {

        }

        @Override
        public void onRecognitionChange(String status) {

        }

        @Override
        public void onAIOSStatusChange(String status) {
            switch (status){
                case StatusProperty.AIOSStatusProperty.ASLEEP :
                    isMute = false;
                    Settings.System.putInt(getContentResolver(), TTS_SHOW, 0);
                  /*  if(!isBackCar && canResume){
                        if (Constants.BAIDU_NAVI_ACTIVITY.equals(curActivity)) {
                            try {
                                APPUtil.lanchApp(getApplicationContext(),APPUtil.BD_NAVI_PKG);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (Constants.GAODE_MAP_ACTIVITY.equals(curActivity)) {
                            try {
                                APPUtil.lanchApp(getApplicationContext(),APPUtil.GD_MAP_PKG);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else if (Constants.GAODE_CAR_ACTIVITY.equals(curActivity)) {
                            if (APPUtil.getInstance().isInstalled(APPUtil.GD_CARJ_PKG)) {
                                try {
                                    APPUtil.lanchApp(getApplicationContext(), APPUtil.GD_CARJ_PKG);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else if (APPUtil.getInstance().isInstalled(APPUtil.GD_CAR_PKG)) {
                                try {
                                    APPUtil.lanchApp(getApplicationContext(), APPUtil.GD_CAR_PKG);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }*/
                    break;
                case StatusProperty.AIOSStatusProperty.AWAKE :
                    Settings.System.putInt(getContentResolver(), TTS_SHOW, 1);
                    isMute = true;

                    BridgeApplication.getContext().sendBroadcast(new Intent("action.coogo.QUITE_SCREENOFF"));
                    try {
                        getWakeLock().acquire();
                        getWakeLock().release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                //    getCurActivity();
                /*    canResume = true;
                    boolean isCamera = isConsumeActivity();
                    if (isCamera) {
                        RomSystemSetting.homePage(mContext);
                    }*/
                    break;
                default:

            }
        }

        @Override
        public void onPlayerStatusChange(String status) {
        }

        @Override
        public void onRecorderStatusChange(String status) {
            Logger.d("onRecorderStatusChange="+status);
                switch (status){
                    case StatusProperty.BUSY :
                        AudioManager audioManager = (AudioManager) BridgeApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
                        if (audioManager != null && isMute) {
                            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                        }
                        break;
                    case StatusProperty.IDLE :
                        AudioManager audio = (AudioManager) BridgeApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
                        if (audio != null) {
                            audio.setStreamMute(AudioManager.STREAM_MUSIC, false);
                        }
                        break;
                }
        }

        @Override
        public void onContextInput(String inputContext) {

        }

        @Override
        public void onContextOutput(String outputContext) {

        }
    };

    AIOSSettingListener aiosSettingListener = new AIOSSettingListener() {
        @Override
        public void onDialogueChange(int oldStyle, int newStyle) {

        }

        @Override
        public void onTtsResChange(@NonNull String oldRes, @NonNull String newRes) {

        }

        @Override
        public void onDefaultMapChange(@NonNull String oldPkgName, @NonNull String newPkgName) {
            if(TextUtils.equals(APPUtil.GD_CAR_PKG,newPkgName) || TextUtils.equals(APPUtil.GD_CARJ_PKG,newPkgName) ||TextUtils.equals(APPUtil.GD_MAP_PKG,newPkgName)){
                Settings.System.putInt(getContentResolver(), MAP_INDEX, 1);
            }else if(TextUtils.equals(APPUtil.BD_NAVI_PKG,newPkgName)){
                Settings.System.putInt(getContentResolver(), MAP_INDEX, 0);
            }else if(TextUtils.equals(PkgName.MapPkgName.CARE_LAND,newPkgName)){
                Settings.System.putInt(getContentResolver(), MAP_INDEX, 2);
            }
        }
    };

    private void setMap(){
        int type =  Settings.System.getInt(getContentResolver(), MAP_INDEX, 0);
        if(type==0){
            AIOSSettingManager.getInstance().setDefaultMap(PkgName.MapPkgName.BAIDU_NAVI);
        }else if(type == 1){
            if(APPUtil.getInstance().isInstalled(PkgName.MapPkgName.AUTO_AMAP_LITE)) {
                AIOSSettingManager.getInstance().setDefaultMap(PkgName.MapPkgName.AUTO_AMAP_LITE);
            }else if(APPUtil.getInstance().isInstalled(PkgName.MapPkgName.AUTO_AMAP)) {
                AIOSSettingManager.getInstance().setDefaultMap(PkgName.MapPkgName.AUTO_AMAP);
            }else{
                AIOSSettingManager.getInstance().setDefaultMap(PkgName.MapPkgName.AMAP);
            }
        }else if(type == 2){
            AIOSSettingManager.getInstance().setDefaultMap(PkgName.MapPkgName.CARE_LAND);
        }
    }

    private void setACC() {
      //  canResume = false;
        boolean acc = Settings.System.getInt(getContentResolver(),ACC_STATE, 1) == 1;
        if(acc){
            AIOSSystemManager.getInstance().setACCOn();
            if ( PreferenceUtil.getFloat(this,"lat", 0f) != 0f &&  PreferenceUtil.getFloat(this,"lng", 0f) != 0f) {
                Intent windowService = new Intent(this,
                        BridgeService.class);
                windowService.setAction(BridgeService.ACTION_PRE_NAVI);
                startService(windowService);
            }
        }else{
            AIOSSystemManager.getInstance().setACCOff();
        }
    }

   /* private void getCurActivity() {
        curActivity = null;
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        // 应用程序位于堆栈的顶层
        if (tasksInfo != null) {
            ComponentName name = tasksInfo.get(0).topActivity;
            if (name != null) {
                curActivity = name.getClassName();
                Logger.d(curActivity);
            }
        }
    }

    public boolean isConsumeActivity() {
        if (Constants.GAODE_MAP_ACTIVITY.equals(curActivity) || Constants.GAODE_CAR_ACTIVITY.equals(curActivity)
                || Constants.BAIDU_NAVI_ACTIVITY.equals(curActivity)) {
            return true;
        }
        return false;
    }
*/
    boolean isBackCar;
    private void isBackCar() {
        isBackCar = Settings.System.getInt(getContentResolver(),BACK_CAR_STATE,0) == 1;
        Logger.d("后录="+isBackCar);
        if(isBackCar){
    //        canResume = false;
            AIOSSystemManager.getInstance().stopInteraction();
        }
    }

    private PowerManager.WakeLock getWakeLock() {
        if (wl == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP,getPackageName());
        }
        return wl;
    }

}
