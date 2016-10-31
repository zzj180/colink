
package cn.coogo.hardware.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import it.sauronsoftware.cron4j.Scheduler;

import cn.coogo.hardware.service.activity.ModelSelectActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class HardwareService extends Service {

    private static final int CHECK_SENSOR = 0x01;
    private static final int RECEIVE_ACC_OFF = 0x02;
    private static final int RECEIVE_SCREEN_ON = 0x03;
    private static final int RECEIVE_ACC_ON = 0x04;
    private static final int RECEIVE_BACK_CAMERA = 0x05;
    private static final int RECERVE_FORE_CAMERA = 0x06;
    private static final int MSG_EVERY_DAT_THREE_CLOCK_REBOOT = 0x07;
    private static final int MSG_EDOG_VOICE_STATUS = 0x08;
    private static final int MSG_RECEIVE_ACC_ON = 0x09;

    private static final String TAG = "HardwareService";
    public final static String ACC_DEVICE = "/sys/class/accdriver_cls/accdriver/accdriver";
    public static final String ACTION_ACC_ON_KEYEVENT = "android.intent.action.ACC_ON_KEYEVENT";
    public static final String ACTION_ACC_OFF_KEYEVENT = "android.intent.action.ACC_OFF_KEYEVENT";
    public static final String ACTION_SEC_ON_KEYEVENT = "android.intent.action.SEC_ON_KEYEVENT";
    public static final String ACTION_ALARM = "android.intent.action.ALARM_RECEIVER";
    public static final String POWER_ON_START = "powerOnStart";
    public static final String IS_RUN_MONITOR = "isRunMonitor";
    public static final String ACTION_BACK_CAMERA = "action_back_camera";
    public static final String ACTION_FORE_CAMERA = "action_fore_camera";
    public static final String ACTION_BACK_CAR_ON_KEYEVENT = "android.intent.action.BACK_CAR_ON_KEYEVENT";
    public static final String ACTION_BACK_CAR_OFF_KEYEVENT = "android.intent.action.BACK_CAR_OFF_KEYEVENT";
    public static final String ACTION_TEMP_NORMAL_KEYEVENT = "android.intent.action.TEMP_NORMAL_KEYEVENT";
    public static final String ACTION_TEMP_WARN_KEYEVENT = "android.intent.action.TEMP_WARN_KEYEVENT";
    public static final String ACTION_TEMP_HIGH_KEYEVENT = "android.intent.action.TEMP_HIGH_KEYEVENT";
    public static final String ACTION_CARKE_WAKEUP = "com.szcx.system.wakeup";
    public static final String ACTION_CARKE_SLEEP = "com.szcx.system.sleep";
    public static final String ACTION_TEMP_HIGHT = "action.temp.hight";
    public static final String ACTION_TEMP_NORMAL = "action.temp.normal";
    public static final String ACTION_TEMP_WARN = "action.temp.warn";
    public static final String ACTION_GET_EDOG_FLOTING = "com.szcx.edog.get.floating.status";
    public static final String ACTION_RETURN_EDOG_FLOTING = "com.szcx.edog.return.floating.status";
    public static final String ACTION_EDOG_VOICE_OFF = "com.szcx.edog.voice.off";
    public static final String ACTION_EDOG_VOICE_ON = "com.szcx.edog.voice.on";
    public static final String ACTION_SET_EDOG_FLOATING = "com.szcx.edog.floating.window";
    public static final String ACTION_THREE_CLOCK_REBOOT = "action_three_clock_reboot";
    public static final String ACTION_FIFTEEN_CLOCK_REBOOT = "action_fifteen_clock_reboot";
    public static final String KEY_EDOG_FLOTING = "window";
    public static final String ACTION_FINISH_ACT = "action.finish.activity";
    public static final String ACTION_EDOG_VOICE_STATUS = "com.cxsz.edog.voice.status";
    public static final String KEY_EDOG_VOICE_STATUS = "checked";
    public static final String ACTION_TIME_SET = "android.intent.action.TIME_SET";
    public static final String ACTION_ACC_ON_START_RECORD = "action_acc_on_start_record";
    public static final String ACTION_ACC_ON_AFTER_THREE_MIN = "acc_on_after_three_min";

    private AlarmManager mAlarmManager;
    private Intent mThreeIntent;
    private Intent mFifteenIntent;
    private PendingIntent mThree;
    private PendingIntent mFifteen;
    private BroadcastReceiver mReceiver;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private Timer mTimer;
    private TimerTask mThreeTimeTask;
    private TimerTask mFifteenTimerTask;
    private Scheduler mScheduler;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_SENSOR:
                    updateSensorCheck();
                    break;
                case RECEIVE_ACC_OFF:
                    Toast.makeText(HardwareService.this, "RECEIVE ACC OFF", 1).show();
                    startHomeAct();
                    startModelSelectAct();
                    break;
                case RECEIVE_SCREEN_ON:
                    break;
                case RECEIVE_ACC_ON:
                    startCameraAct(true);
                    break;
                case MSG_EVERY_DAT_THREE_CLOCK_REBOOT:
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int min = calendar.get(Calendar.MINUTE);
                    Log.i(TAG, "hour : " + hour + "  min : " + min);
                    if ((hour == 3 || hour == 15) && min < 10) {
                        if (!readAccFile()) {
                            reboot();
                        }
                    }
                    break;
                case MSG_EDOG_VOICE_STATUS:
                    boolean status = (Boolean) msg.obj;
                    if (status) {
                        writeRadarFile((byte) (1 & 0xff));
                    } else {
                        writeRadarFile((byte) (0 & 0xff));
                    }
                    break;
                case MSG_RECEIVE_ACC_ON:
                    Log.i(TAG, "send broadcase  :  " + ACTION_ACC_ON_AFTER_THREE_MIN);
                    sendBroadcast(new Intent(ACTION_ACC_ON_AFTER_THREE_MIN));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        initReceiver();
        initWakeLock();
        aquireWakeLock();
        setEdogVoiceStatus();
        startAlarm(HardwareService.this);
        mHandler.sendEmptyMessageDelayed(MSG_RECEIVE_ACC_ON, 3 * 60 * 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Log onStartCommand ...");
        updateSensorCheck();
        if (!readAccFile()) {
            releaseWakeLock();
            mHandler.sendEmptyMessageDelayed(RECEIVE_ACC_OFF, 5000);
            sendBroadcast(new Intent(ACTION_CARKE_SLEEP));
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy...");
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        mScheduler.stop();
        releaseWakeLock();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    private Binder mBinder = new LocalBinder();

    class LocalBinder extends Binder {
        public HardwareService getService() {
            return HardwareService.this;
        }
    }

    public void updateSensorCheck() {
        int ret = 0;
        if (ret == 0) {
            sendBroadcast(new Intent(ACTION_BACK_CAMERA));
        } else {
            sendBroadcast(new Intent(ACTION_FORE_CAMERA));
        }
        // mHandler.sendEmptyMessageDelayed(CHECK_SENSOR, 3 * 1000);
        Log.i("gpio", "sensor check " + ret);
    }

    private void initReceiver() {
        mReceiver = new AccBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ACC_OFF_KEYEVENT);
        filter.addAction(ACTION_ACC_ON_KEYEVENT);
        filter.addAction(ACTION_ALARM);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(ACTION_BACK_CAR_OFF_KEYEVENT);
        filter.addAction(ACTION_BACK_CAR_ON_KEYEVENT);
        filter.addAction(ACTION_TEMP_HIGH_KEYEVENT);
        filter.addAction(ACTION_TEMP_NORMAL_KEYEVENT);
        filter.addAction(ACTION_TEMP_WARN_KEYEVENT);
        filter.addAction(ACTION_RETURN_EDOG_FLOTING);
        filter.addAction(ACTION_EDOG_VOICE_STATUS);
        filter.addAction(ACTION_ACC_ON_START_RECORD);
        filter.addAction(ACTION_THREE_CLOCK_REBOOT);
        filter.addAction(ACTION_FIFTEEN_CLOCK_REBOOT);
        filter.addAction(ACTION_TIME_SET);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        registerReceiver(mReceiver, filter);
    }

    private void initWakeLock() {
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            try {
                mWakeLock.release();
                mWakeLock = null;
                Log.i(TAG, "WakeLock Release");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void aquireWakeLock() {
        if (mWakeLock == null) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
            mWakeLock.acquire();
            Log.i(TAG, "WakeLock Aquire");
        }
    }

    private void screenOff() {
        if (mPowerManager != null) {
            mPowerManager.goToSleep(SystemClock.uptimeMillis());
        }
    }

    private boolean mIsNormalTemp = false;
    private boolean mIsEdog = false;

    class AccBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "Hardware Service Receive Action : " + action);
            if (TextUtils.equals(action, ACTION_ACC_OFF_KEYEVENT)) {
                releaseWakeLock();
                sendBroadcast(new Intent(ModelSelectActivity.ACTION_FINISH_ACT));
                context.sendBroadcast(new Intent(ACTION_CARKE_SLEEP));
                mHandler.sendEmptyMessageDelayed(RECEIVE_ACC_OFF, 1000);
            } else if (TextUtils.equals(action, ACTION_ACC_ON_KEYEVENT)) {
                aquireWakeLock();
                context.sendBroadcast(new Intent(ACTION_CARKE_WAKEUP));
                // if (mIsNormalTemp)
                // return;
                if (!ModelSelectActivity.isServiceRunning(context,
                        ModelSelectActivity.RECORD_SERVICE_CLASS_NAME) && readAccFile()) {
                    startCameraAct(true);
                } else {
                    sendBroadcast(new Intent(ACTION_ACC_ON_START_RECORD));
                }
                mHandler.sendEmptyMessageDelayed(MSG_RECEIVE_ACC_ON, 3 * 60 * 1000);
            } else if (TextUtils.equals(action, ACTION_ALARM)) {
            } else if (TextUtils.equals(action, Intent.ACTION_SCREEN_ON)) {
            } else if (TextUtils.equals(action, Intent.ACTION_SCREEN_OFF)) {
            } else if (TextUtils.equals(action, ACTION_BACK_CAR_OFF_KEYEVENT)) {
                sendBroadcast(new Intent(ACTION_FORE_CAMERA));
            } else if (TextUtils.equals(action, ACTION_BACK_CAR_ON_KEYEVENT)) {
                if (mIsNormalTemp)
                    return;
                if (!ModelSelectActivity.isServiceRunning(context,
                        ModelSelectActivity.RECORD_SERVICE_CLASS_NAME) && readAccFile()) {
                    startCameraAct(true);
                } else {
                    sendBroadcast(new Intent(ACTION_BACK_CAMERA));
                }
            } else if (TextUtils.equals(action, ACTION_TEMP_HIGH_KEYEVENT)) {
                mIsNormalTemp = true;
                sendBroadcast(new Intent(ACTION_TEMP_HIGHT));
            } else if (TextUtils.equals(action, ACTION_TEMP_WARN_KEYEVENT)) {
                sendBroadcast(new Intent(ACTION_TEMP_WARN));
            } else if (TextUtils.equals(action, ACTION_TEMP_NORMAL_KEYEVENT)) {
                mIsNormalTemp = false;
                if (!ModelSelectActivity.isServiceRunning(context,
                        ModelSelectActivity.RECORD_SERVICE_CLASS_NAME)) {
                    if (readAccFile())
                        startCameraAct(true);
                } else {
                    sendBroadcast(new Intent(ACTION_TEMP_NORMAL));
                }
            } else if (TextUtils.equals(action, ACTION_CARKE_SLEEP)) {

            } else if (TextUtils.equals(action, ACTION_RETURN_EDOG_FLOTING)) {
                if (intent.getExtras() != null) {
                }
            } else if (TextUtils.equals(action, ACTION_THREE_CLOCK_REBOOT)) {
                mHandler.sendEmptyMessage(MSG_EVERY_DAT_THREE_CLOCK_REBOOT);
            } else if (TextUtils.equals(action, ACTION_FIFTEEN_CLOCK_REBOOT)) {
                mHandler.sendEmptyMessage(MSG_EVERY_DAT_THREE_CLOCK_REBOOT);
            } else if (TextUtils.equals(action, ACTION_EDOG_VOICE_STATUS)) {
                sendEdogVoiceStatus(intent);
            } else if (TextUtils.equals(action, ACTION_TIME_SET)) {
                startAlarm(HardwareService.this);
            }else if(TextUtils.equals(action, Intent.ACTION_SHUTDOWN)){
                alarmCancel(HardwareService.this);
            }
        }
    }

    public static final String IS_REBOOTED = "is_rebooted";

    public static void setSharePrs(Context context, boolean isFirst) {
        String name = context.getPackageName() + "_cdr_preference";
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,
                Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_REBOOTED, isFirst);
        editor.apply();
    }

    public static boolean getSharePrs(Context context) {
        String name = context.getPackageName() + "_cdr_preference";
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,
                Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_REBOOTED,
                false);
    }

    public static void initEdog(Context context) {
        Log.i(TAG, "first init EDOG");
        context.sendBroadcast(new Intent(ACTION_EDOG_VOICE_OFF));
        Intent intent = new Intent(ACTION_SET_EDOG_FLOATING);
        intent.putExtra(KEY_EDOG_FLOTING, false);
        context.sendBroadcast(intent);
    }

    public static boolean readAccFile() {

        FileInputStream fis = null;
        byte[] rBuf = new byte[10];
        boolean accOn = false;
        try
        {
            fis = new FileInputStream(ACC_DEVICE);
            fis.read(rBuf);
            fis.close();
            if (rBuf[0] == (byte) 0) {
                accOn = false;
            } else if (rBuf[0] == (byte) 1) {
                accOn = true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.i(TAG, "accOn : " + accOn);
            return accOn;
        }
    }

    private void startModelSelectAct() {
        Log.i(TAG, "startModelSelectAct...");
        Intent intent = new Intent(this, ModelSelectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void startCameraAct(boolean isPowerOn) {
        Log.i(TAG, "CameraActivity...");
        Intent intent = new Intent();
        ComponentName cn = new ComponentName("com.android.camera2",
                "com.android.camera.CameraActivity");
        intent.setComponent(cn);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(POWER_ON_START, isPowerOn);
        startActivity(intent);
    }

    public void startHomeAct() {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(mHomeIntent);
    }

    public static void reboot() {
        // Intent i = new Intent(Intent.ACTION_REBOOT);
        // i.putExtra("nowait", 1);
        // i.putExtra("interval", 1);
        // i.putExtra("window", 0);
        // sendBroadcast(i);
        try {
            Log.i(TAG, "reboot");
            Runtime.getRuntime().exec("am start -a android.intent.action.REBOOT");
        } catch (Exception e) {
        }
    }

    public static final String RADAR_FILE = "/sys/class/radar_pow_cls/radar_pow/radar_pow";

    public void writeRadarFile(byte b) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(RADAR_FILE));
            fos.write(b);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendEdogVoiceStatus(Intent intent) {
        if (intent.getExtras() != null) {
            Message msg = new Message();
            msg.what = MSG_EDOG_VOICE_STATUS;
            msg.obj = intent.getExtras().getBoolean(KEY_EDOG_VOICE_STATUS, false);
            mHandler.sendMessage(msg);
            Log.i(TAG, "KEY_EDOG_VOICE_STATUS : " + (Boolean) msg.obj);
        }
    }

    private void setEdogVoiceStatus() {
        sendBroadcast(new Intent(ACTION_EDOG_VOICE_OFF));
    }

    /**
     * 3 clock
     */
    public void start3Clock(Context context) {
        AlarmManager mg = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mFifteenIntent = new Intent(ACTION_THREE_CLOCK_REBOOT);
        PendingIntent p = PendingIntent.getBroadcast(context,
                0, mFifteenIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        long systemTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long selectTime = calendar.getTimeInMillis();
        if (systemTime > selectTime) {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String selectStr = sdf.format(new Date(calendar.getTimeInMillis()));
        Log.i(TAG, "selectStr 3 clock : " + selectStr);
        long clockTime = SystemClock.elapsedRealtime();
        Log.i(TAG, "selectStr 3 clock : " + (clockTime + calendar.getTimeInMillis() - systemTime) / 1000);
        mg.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,clockTime + calendar.getTimeInMillis() - systemTime,AlarmManager.INTERVAL_DAY,p);
    }

    /**
     * 15 clock
     */
    public void start15Clock(Context context) {
        AlarmManager mg = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mFifteenIntent = new Intent(ACTION_FIFTEEN_CLOCK_REBOOT);
        PendingIntent p = PendingIntent.getBroadcast(context,
                0, mFifteenIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        long systemTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long selectTime = calendar.getTimeInMillis();
        if (systemTime > selectTime) {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String selectStr = sdf.format(new Date(calendar.getTimeInMillis()));
        Log.i(TAG, "selectStr 15 clock : " + selectStr);
        long clockTime = SystemClock.elapsedRealtime();
        Log.i(TAG, "selectStr 15 clock : " + (clockTime + calendar.getTimeInMillis() - systemTime) / 1000);
        mg.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, clockTime + calendar.getTimeInMillis() - systemTime,AlarmManager.INTERVAL_DAY,p);
    }
    
    public void alarmCancel(Context context){
        AlarmManager mg = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent fifteen = new Intent(ACTION_FIFTEEN_CLOCK_REBOOT);
        PendingIntent fiftIntent = PendingIntent.getBroadcast(context,
                0, mFifteenIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent three = new Intent(ACTION_THREE_CLOCK_REBOOT);
        PendingIntent threIntent = PendingIntent.getBroadcast(context,
                0, mFifteenIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mg.cancel(threIntent);
        mg.cancel(fiftIntent);
    }

    public void startAlarm(Context context) {
        Log.i(TAG, "startAlarm....");
        start3Clock(context);
        start15Clock(context);
    }

}
