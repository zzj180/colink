/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : StockShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 */
package com.unisound.unicar.gui.session;

import org.json.JSONObject;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import com.unisound.unicar.gui.search.operation.AMapTrafficClient;
import com.unisound.unicar.gui.search.operation.AMapTrafficClient.ITrafficListener;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.view.TrafficQueryWaitingContentView;
import com.unisound.unicar.gui.view.TrafficQueryWaitingContentView.ITrafficQueryContentViewListener;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : ChenHao
 * @CreateDate : 2015-7-23
 * @ModifiedBy : ChenHao
 * @ModifiedDate: 2015-7-23
 * @Modified: 2013-9-3: 实现基本功能
 */
public class TrafficShowSession extends BaseSession {
    public static final String TAG = "TrafficShowSession";
    private TrafficQueryWaitingContentView mTraQContentView = null;// waiting界面
    private Context mContext;
    private AMapTrafficClient mAMapTrafficClient;// 封装的操作类

    public TrafficShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        mContext = context;
        mAMapTrafficClient = new AMapTrafficClient(mContext);
    }

    @SuppressWarnings("deprecation")
	public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);

        JSONObject resultObject = JsonTool.getJSONObject(jsonProtocol, "data");
        mQuestion = JsonTool.getJsonValue(resultObject, "text", "");
        addQuestionViewText(mQuestion);

        String road = JsonTool.getJsonValue(resultObject, "road", "");
        String city = JsonTool.getJsonValue(resultObject, "city", "");
        if (mTraQContentView == null) {
            mTraQContentView = new TrafficQueryWaitingContentView(mContext);
        }
        mTraQContentView.setLisener(mlistener);
        addAnswerView(mTraQContentView);
        addAnswerViewText(mAnswer);
        // set 监听
        mAMapTrafficClient.setMlistener(mITrafficListener);
        mAMapTrafficClient.startTraffic(city, road);// 实时路况流程入口

    }

    private ITrafficQueryContentViewListener mlistener = new ITrafficQueryContentViewListener() {

        @Override
        public void onCancel() {
            Logger.d(TAG, "!--->mlistener----onCancel--MESSAGE_SESSION_DONE");
            // TODO 暂时先取消
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        }
    };

    // 定义监听
    private ITrafficListener mITrafficListener = new ITrafficListener() {

        @Override
        public void onSessionDone() {
            Logger.d(TAG, "!--->mITrafficListener----onSessionDone--MESSAGE_SESSION_DONE");
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
        }

        @Override
        public void onProcess(String process) {
            mTraQContentView.setPoiText(process);
        }

        @Override
        public void onError(String error) {
            showOnResultText(error);
        }
    };

    @Override
    public void onTTSEnd() {
        // mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    };

    /**
     * 显示结果 并toast提示
     * 
     * @param text
     */
    private void showOnResultText(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
        // TODO 暂时先这么结束流程
        Logger.d(TAG, "!--->showOnResultText--MESSAGE_SESSION_DONE");
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
    }
}
