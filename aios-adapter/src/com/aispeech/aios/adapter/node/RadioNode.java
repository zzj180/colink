package com.aispeech.aios.adapter.node;

import android.provider.Settings;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.bean.RpcRecall;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.util.SystemOperateUtil;

/**
 * @desc AIOS Radio Node
 * 调频/调幅收音机领域
 * @auth AISPEECH
 * @date 2016-01-18
 * @copyright aispeech.com
 */
public class RadioNode extends BaseNode {

    private static final String TAG = "AIOS-Adapter-RadioNode";

    private static RadioNode mInstance;
    private RpcRecall mRpcRecall;

    public static synchronized RadioNode getInstance() {
        if (mInstance == null) {
            mInstance = new RadioNode();
        }

        return mInstance;
    }

    @Override
    public String getName() {
        return AiosApi.Radio.NAME;
    }

    @Override
    public void onMessage(String topic, byte[]... parts) throws Exception {
        super.onMessage(topic, parts);
        AILog.i(TAG, topic, parts);
    }

    @Override
    public BusClient.RPCResult onCall(String url, byte[]... args) throws Exception {
        mRpcRecall = new RpcRecall(url, args);
        AILog.i(TAG, url, args);

        UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
        String fmStr = new String(args[0],"utf-8");//added by ydj 2016.3.24
		if (AiosApi.Radio.FM_PLAY.equals(url)) {
        	try {
        		int fm = (int) (Float.parseFloat(fmStr)*1000);
        		if(fm < 87500){
        			TTSNode.getInstance().play("请在FM87.5到108兆赫中搜索");
        		}else if(fm <= 108000){
        			SystemOperateUtil.getInstance().launchFM(fm);
            		TTSNode.getInstance().play("已为您调频到FM" + fmStr);
        		}else{
        			TTSNode.getInstance().play("请在FM87.5到108兆赫中搜索");
        		}
        		
			} catch (NumberFormatException e) {
				
			}
        } else if (AiosApi.Radio.AM_PLAY.equals(url)) {
            TTSNode.getInstance().play("已为您调幅到AM" + fmStr);
        }
        
        return null;
    }

    private void exitSubspendWindow() {
    	//upgraded by ydj on 2016.3.24
//        mHandler.sendEmptyMessage(REMOVE_SMALL_WINDOW);
        UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
        //upgraded by ydj on 2016.3.24
    }
}
