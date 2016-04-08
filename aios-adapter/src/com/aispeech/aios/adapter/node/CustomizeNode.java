package com.aispeech.aios.adapter.node;

import android.content.IntentFilter;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.model.CommonTipData;
import com.aispeech.aios.adapter.model.CustomTTSData;
import com.aispeech.aios.adapter.model.CustomVoiceData;
import com.aispeech.aios.adapter.model.LangGenData;
import com.aispeech.aios.adapter.receiver.CustomizeNodeReceiver;
import com.aispeech.aios.adapter.util.APPUtil;
import com.aispeech.aios.adapter.util.SystemOperateUtil;

/**
 * @desc 自定义命令节点
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class CustomizeNode extends BaseNode {

    private static final String TAG = "AIOS-Adapter-CustomizeNode";
    private static volatile CustomizeNode mInstance;
    private CustomizeNodeReceiver mCustomizeNodeReceiver;

    public static synchronized CustomizeNode getInstance() {
        if (mInstance == null) {
            mInstance = new CustomizeNode();
        }
        return mInstance;
    }

    @Override
    public String getName() {
        return AiosApi.Customize.NAME;
    }


    @Override
    public void onJoin() {
        super.onJoin();
        publishSticky(AiosApi.Customize.COMMAND, new CustomVoiceData().getStatementJson());
        publishSticky(AiosApi.Customize.GENERATE_NATURAL_LANGUAGE, new LangGenData().getGeneration());
        publishSticky(AiosApi.Customize.TIPS_COMMON, new CommonTipData().getCommonTips());
        publishSticky(AiosApi.Customize.TTS, new CustomTTSData().getCustomTTSs());
    }

    @Override
    public BusClient.RPCResult onCall(String url, byte[]... bytes) throws Exception {
        AILog.i(TAG, url, bytes);

        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith(CustomVoiceData.CallBack.APP_OPEN)) {//根据应用名打开应用

                String pkgName = url.replace(CustomVoiceData.CallBack.APP_OPEN, "");
                APPUtil.getInstance().openApplication(pkgName);

            } else if (url.startsWith(CustomVoiceData.CallBack.APP_CLOSE)) {//根据应用名关闭应用

                String pkgName = url.replace(CustomVoiceData.CallBack.APP_CLOSE, "");
                if(SystemOperateUtil.CAMERA2_PKG.equals(pkgName)){
                	SystemOperateUtil.getInstance().closeDrivingRecorder();
                }
            //    APPUtil.getInstance().killProcess(pkgName);

            } else if (url.equals("/customize/go/home/page")) {
                APPUtil.goHomePage();
            } else if ("/customize/turn/on/fm".equals(url)) {
            	SystemOperateUtil.getInstance().openFM();
            } else if ("/customize/turn/off/fm".equals(url)) {
            	SystemOperateUtil.getInstance().closeFM();
            }else if ("/customize/no/disturb".equals(url)) {
            	SystemOperateUtil.getInstance().openNoDisturb();
            }else if ("/customize/one/navi".equals(url)) {
            	SystemOperateUtil.getInstance().openOneNavi();
            }else if ("/customize/open/radar".equals(url)) {
            	SystemOperateUtil.getInstance().openRADAR();
            }else if ("/customize/close/radar".equals(url)) {
            	SystemOperateUtil.getInstance().closeRADAR();
            }
            UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
        }
        return null;
    }

    /**
     * 注册CustomizeNodeReceiver这个广播，广播含义请参考类CustomizeNodeReceiver
     */
    public void register() {
        if (null == mCustomizeNodeReceiver) {
            IntentFilter filter = new IntentFilter();
            mCustomizeNodeReceiver = new CustomizeNodeReceiver(filter);
            filter.addDataScheme("package");
            AdapterApplication.getContext().registerReceiver(mCustomizeNodeReceiver, filter);
        }

    }

    /**
     * 取消注册CustomizeNodeReceiver这个广播，广播含义请参考类CustomizeNodeReceiver
     */
    public void unRegister() {
        if (null != mCustomizeNodeReceiver) {
            AdapterApplication.getContext().unregisterReceiver(mCustomizeNodeReceiver);
            mCustomizeNodeReceiver = null;
        }
    }


}
