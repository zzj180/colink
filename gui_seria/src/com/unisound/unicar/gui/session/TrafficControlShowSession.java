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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.model.TrafficControlInfo;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.view.TrafficControlView;

/**
 * @Module : 隶属模块名 限号
 * @Comments : 描述
 * @Author : ChenHao
 * @CreateDate : 2015-7-29
 * @ModifiedBy : ChenHao
 * @ModifiedDate: 2015-7-29
 * @Modified: 2013-9-3: 实现基本功能
 */
public class TrafficControlShowSession extends BaseSession {
    public static final String TAG = "TrafficControlShowSession";
    private Context mContext;
    private String result = "";// 返回结果
    private TrafficControlView mTraControlView;
    private TrafficControlInfo mTrafficControlInfo;

    public TrafficControlShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
        mContext = context;
        mTrafficControlInfo = new TrafficControlInfo();
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);

        JSONObject resultObject = JsonTool.getJSONObject(jsonProtocol, "data");
        if (resultObject == null) return;
        mQuestion = JsonTool.getJsonValue(resultObject, "text", "") /*
                                                                     * mContext.getString(R.string.
                                                                     * to_stock)
                                                                     */;
        addQuestionViewText(mQuestion);
        result = JsonTool.getJsonValue(resultObject, "header", "");
        JSONObject results = JsonTool.getJSONObject(resultObject, "result");
        // 无结果直接结束session
        if (results == null) {
            mSessionManagerHandler.sendEmptyMessageDelayed(SessionPreference.MESSAGE_SESSION_DONE,
                    500);
            return;
        }
        // 限行规则
        String trafficRule = JsonTool.getJsonValue(results, "nonlocal");
        String jsonWeeks = JsonTool.getJsonValue(results, "trafficControlInfos");
        JSONArray jsonArr = JsonTool.parseToJSONOArray(jsonWeeks);
        String[] weeks = new String[jsonArr.length()];
        for (int i = 0; i < jsonArr.length(); i++) {
            try {
                weeks[i] = jsonArr.getJSONObject(i).getString("forbiddenTailNumber");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        /**
         * 一周限行消息,由于之后返回今天 至往后的数据 所有按周一至周日显示不行
         */
        mTrafficControlInfo.setWeeks(weeks);
        String today = mTrafficControlInfo.getStringData();
        // int t_week = mTrafficControlInfo.getWeek();
        /**
         * by chenhao 注： 这里直接与播报的TTs保持,周一返回的是7天的数据 周二只返回6天依次类推...
         * 所有此处获取有问题,并且有可能今天去查询明天或者后天的信息，所有通过weekId 获取信息没法实现
         */
        mTrafficControlInfo.setToady(result/* weeks[t_week-1] */);// 今日限行
        mTrafficControlInfo.setDateInfo(today);// 头部日期消息
        mTrafficControlInfo.setTrafficRule(trafficRule);// 限行规则
        mTraControlView = new TrafficControlView(mContext, mTrafficControlInfo);
        // mTraControlView.setShowText(result);
        addAnswerView(mTraControlView);
        addAnswerViewText(mAnswer);
    }

    @Override
    public void onTTSEnd() {
        // XD 20151123 modify
        if (UserPerferenceUtil.VALUE_VERSION_MODE_EXP == UserPerferenceUtil
                .getVersionMode(mContext)) {
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_RELEASE_ONLY);
        } else {
            mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE_DELAY);
        }
    };

}
