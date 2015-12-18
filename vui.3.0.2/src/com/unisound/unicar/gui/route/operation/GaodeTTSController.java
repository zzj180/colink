package com.unisound.unicar.gui.route.operation;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.ui.MessageReceiver;
import com.unisound.unicar.gui.utils.Logger;

/**
 * TTS Controller for Gaode Navigation
 * 
 * @author xiaodong
 * @date 20150914
 */
public class GaodeTTSController implements AMapNaviListener {

    private static final String TAG = "TTSController";

    public static GaodeTTSController ttsManager;
    private Context mContext;

    // private float TTS_SPEED_SLOW = 0;
    // private float TTS_SPEED_STANDARD = 13;
    // private float TTS_SPEED_FAST = 20;
    // 合成对象.
    // private SpeechSynthesizer mSpeechSynthesizer;

    GaodeTTSController(Context context) {
        mContext = context;
    }

    public static GaodeTTSController getInstance(Context context) {
        if (ttsManager == null) {
            ttsManager = new GaodeTTSController(context);
        }
        return ttsManager;
    }

    public void init() {
        // SpeechUser.getUser().login(mContext, null, null,
        // "appid=" + mContext.getString(R.string.app_id), listener);
        // 初始化合成对象.
        // mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext);
        // initSpeechSynthesizer();
        // 初始化语音合成对象sand5g2nsc6a5bizpyc2tlzcuzdl646vdek5eti6

        // getPackageInfo();

    }

    /**
     * 使用SpeechSynthesizer合成语音，不弹出合成Dialog.
     * 
     * @param
     */
    public void playText(String playText) {
        if (!isfinish) {
            return;
        }
        playTTS(playText);

    }

    public void stopSpeaking() {
        // if (mSpeechSynthesizer != null)
        // mSpeechSynthesizer.stopSpeaking();

        /*
         * if (mTTSControl != null){ mTTSControl.cancel(); }
         */

    }

    public void startSpeaking() {
        isfinish = true;
    }

    /**
     * 用户登录回调监听器.
     */
    // private SpeechListener listener = new SpeechListener() {
    //
    // @Override
    // public void onData(byte[] arg0) {
    // }
    //
    // @Override
    // public void onCompleted(SpeechError error) {
    // if (error != null) {
    //
    // }
    // }
    //
    // @Override
    // public void onEvent(int arg0, Bundle arg1) {
    // }
    // };

    // @Override
    // public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
    // // TODO Auto-generated method stub
    //
    // }

    boolean isfinish = true;

    // @Override
    // public void onCompleted(SpeechError arg0) {
    // // TODO Auto-generated method stub
    // isfinish = true;
    // }

    // @Override
    // public void onSpeakBegin() {
    // // TODO Auto-generated method stub
    // isfinish = false;
    //
    // }

    // @Override
    // public void onSpeakPaused() {
    // // TODO Auto-generated method stub
    //
    // }

    // @Override
    // public void onSpeakProgress(int arg0, int arg1, int arg2) {
    // // TODO Auto-generated method stub
    //
    // }

    // @Override
    // public void onSpeakResumed() {
    // // TODO Auto-generated method stub
    //
    // }

    public static void destroy() {}

    @Override
    public void onArriveDestination() {
        // TODO Auto-generated method stub
        this.playText(mContext.getString(R.string.tts_route_arrive_destination));
    }

    @Override
    public void onArrivedWayPoint(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onCalculateRouteFailure(int arg0) {
        this.playText(mContext.getString(R.string.tts_route_calculate_route_failure));
    }

    @Override
    public void onCalculateRouteSuccess() {
        this.playText(mContext.getString(R.string.tts_route_calculate_route_success));
    }

    @Override
    public void onEndEmulatorNavi() {
        this.playText(mContext.getString(R.string.tts_route_end_navi));

    }

    @Override
    public void onGetNavigationText(int arg0, String arg1) {
        this.playText(arg1);
    }

    @Override
    public void onInitNaviFailure() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInitNaviSuccess() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChange(AMapNaviLocation arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        this.playText(mContext.getString(R.string.tts_route_recalculate_for_traffic_jam));
    }

    @Override
    public void onReCalculateRouteForYaw() {

        this.playText(mContext.getString(R.string.tts_route_yaw));
    }

    @Override
    public void onStartNavi(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTrafficStatusUpdate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGpsOpenStatus(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo arg0) {

        // TODO Auto-generated method stub

    }

    private void playTTS(String text) {
        if (TextUtils.isEmpty(text)) {
            Logger.w(TAG, "playTTS:text empty!");
            return;
        }
        sendPlayTTSBroadcast(mContext, text);
    }

    /**
     * 
     * @param context
     * @param text
     */
    private void sendPlayTTSBroadcast(Context context, String text) {
        if (context == null) {
            return;
        }
        Logger.d(TAG, "sendPlayTTSBroadcast: text " + text);
        Intent intent = new Intent(MessageReceiver.ACTION_PLAY_TTS);
        intent.putExtra(MessageReceiver.KEY_EXTRA_TTS_TEXT, text);
        context.sendBroadcast(intent);
    }

    /*
     * private String mPackageName; private void getPackageInfo() { try { PackageInfo info =
     * mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0); mPackageName =
     * info.packageName; } catch (NameNotFoundException e) { e.printStackTrace(); mPackageName =
     * null; } Logger.i(TAG, "-getPackageName is--" + mPackageName); }
     */

}
