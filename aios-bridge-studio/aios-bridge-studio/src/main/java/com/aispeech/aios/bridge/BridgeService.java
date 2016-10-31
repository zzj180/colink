package com.aispeech.aios.bridge;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.aispeech.aios.bridge.utils.APPUtil;
import com.aispeech.aios.bridge.utils.Gps;
import com.aispeech.aios.bridge.utils.PositionUtil;
import com.aispeech.aios.bridge.utils.PreferenceUtil;
import com.aispeech.aios.bridge.utils.SystemPropertiesProxy;
import com.aispeech.aios.bridge.vendor.BDDHOperate;
import com.aispeech.aios.bridge.vendor.GDOperate;
import com.aispeech.aios.bridge.vendor.KLDOperate;

public class BridgeService extends Service {

    public final static String STRAT_NAVI_ACTION = "com.zzj.software.NAVI_ACTION";
    public static final String ACTION_PRE_NAVI = "action.colink.remotestart";

    AlertDialog mAlertDialog;
    Button button1, button2;
    private static final String PLAY_TTS = "com.wanma.action.PLAY_TTS";
    public BridgeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
            String action = intent.getAction();
           if(STRAT_NAVI_ACTION.equals(action)) {
                double lat = intent.getDoubleExtra("lat", 0);
                double lon = intent.getDoubleExtra("lon", 0);
                String name = intent.getStringExtra("address");
                startNavigation(lat, lon, name);
            }else if(ACTION_PRE_NAVI.equals(action)){
               CreateDialog(this);
           }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startNavigation(double lat, double lon,
                                 String name) {
        int mapType = Settings.System.getInt(getContentResolver(), APPUtil.MAP_INDEX, 1);
        if (mapType == 1) {// 高德
            GDOperate.getInstance(this).startNavigation(lat, lon);
        } else if (mapType == 2) {
            KLDOperate.getInstance(this).startNavigation(lat, lon, name);
        } else if (mapType == 0) {// 百度导航开始导航
            Gps bd09 = PositionUtil.gcj02_To_Bd09(lat, lon);
            BDDHOperate.getInstance(this).startNavigation(bd09.getWgLat(),bd09.getWgLon());
        }
    }

    public void CreateDialog(Context context) {
        if (mAlertDialog != null) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.prepare_navi, null);
        button1 = (Button) v.findViewById(R.id.button1);
        button2 = (Button) v.findViewById(R.id.button2);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        button1.setOnClickListener(l);
        button2.setOnClickListener(l);

        String address = PreferenceUtil.getString(this,"address");
        ((TextView) v.findViewById(R.id.textView1)).setText(getString(R.string.prepnavi_endpoi, address));
        builder.setView(v);
        Intent intent = new Intent(PLAY_TTS);
        String content = getString(R.string.receive_prepnavi_command);

        if (getString(R.string.prepnavi_address).equals(address)) {
        } else {
            String customer = SystemPropertiesProxy.get(this,"ro.inet.consumer.code");
            if ("003".equals(customer)) {
                content = content + ",目的地是" + address;
            } else {
            }
        }

        intent.putExtra("content", content);
        context.sendBroadcast(intent);
        mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);// 显示
        mAlertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        CountDown();
        mAlertDialog.show();

        mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mAlertDialog = null;
                PreferenceUtil.setFloat(BridgeService.this,"lat", 0f);
                PreferenceUtil.setFloat(BridgeService.this,"lng", 0f);
                PreferenceUtil.setString(BridgeService.this,"address", "");
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
            }
        });
    }

    /**
     * by ZZJ add
     */
    View.OnClickListener l = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button1:
                    if (mAlertDialog != null) {
                        startNavi();
                    }
                    break;
                default:
                    if (mAlertDialog != null) {
                        PreferenceUtil.setFloat(BridgeService.this,"lat", 0f);
                        PreferenceUtil.setFloat(BridgeService.this,"lng", 0f);
                        PreferenceUtil.setString(BridgeService.this,"address", "");
                        mAlertDialog.dismiss();
                    }
                    break;
            }

        }
    };

    /**
     * by ZZJ
     */
    CountDownTimer mCountDownTimer;

    private void CountDown() {
        if (mCountDownTimer != null) {
            return;
        }

        mCountDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                button1.setText(getString(R.string.cancel_prepnavi,
                        millisUntilFinished / 1000+""));
            }

            @Override
            public void onFinish() {
                if (mAlertDialog != null) {
                    startNavi();
                }
            }
        };
        mCountDownTimer.start();
    }

    /**
     * by ZZJ
     */
    private void startNavi() {
        Gps gcj02 = PositionUtil.bd09_To_Gcj02(PreferenceUtil.getFloat(this,"lat", 0f),PreferenceUtil.getFloat(this,"lng", 0f));
        startNavigation(gcj02.getWgLat(), gcj02.getWgLon(),PreferenceUtil.getString(this,"address", ""));
        PreferenceUtil.setFloat(BridgeService.this,"lat", 0f);
        PreferenceUtil.setFloat(BridgeService.this,"lng", 0f);
        PreferenceUtil.setString(BridgeService.this,"address", "");
        mAlertDialog.dismiss();
    }

}
