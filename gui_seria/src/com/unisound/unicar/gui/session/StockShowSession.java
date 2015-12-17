/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : StockShowSession.java
 * @ProjectName : UniCarGUI
 * @PakageName : com.unisound.unicar.gui.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 */
package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.StockInfo;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.StockContentView;

/**
 * @Module :
 * @Comments :
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 * @ModifiedBy : xiaodong.he
 * @ModifiedDate: 2015-11-23
 * @Modified:
 */
public class StockShowSession extends BaseSession {
    public static final String TAG = "StockShowSession";
    private StockContentView mContentView = null;

    /**
     * @Author : Dancindream
     * @CreateDate : 2013-9-3
     * @param context
     * @param sessionManagerHandler
     */
    public StockShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
    }

    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);

        JSONObject resultObject = JsonTool.getJSONObject(mDataObject, "result");
        mQuestion =
                JsonTool.getJsonValue(resultObject, "name", "")
                        + mContext.getString(R.string.to_stock);
        addQuestionViewText(mQuestion);

        StockInfo stockInfo = new StockInfo();

        stockInfo.setChartImgUrl(JsonTool.getJsonValue(resultObject, "imageUrl", ""));
        stockInfo.setName(JsonTool.getJsonValue(resultObject, "name", ""));
        stockInfo.setCode(JsonTool.getJsonValue(resultObject, "code", ""));
        stockInfo.setCurrentPrice(JsonTool.getJsonValue(resultObject, "currentPrice", ""));
        stockInfo.setChangeAmount(Double.parseDouble(JsonTool.getJsonValue(resultObject,
                "changeAmount", "0.0")));
        stockInfo.setChangeRate(JsonTool.getJsonValue(resultObject, "changeRate", ""));
        stockInfo.setTurnover(JsonTool.getJsonValue(resultObject, "turnover", ""));
        stockInfo.setHighestPrice(JsonTool.getJsonValue(resultObject, "highestPrice", ""));
        stockInfo.setLowestPrice(JsonTool.getJsonValue(resultObject, "lowestPrice", ""));
        stockInfo.setYesterdayClosingPrice(JsonTool.getJsonValue(resultObject,
                "yesterdayClosePrice", ""));
        stockInfo.setTodayOpeningPrice(JsonTool.getJsonValue(resultObject, "todayOpenPrice", ""));
        stockInfo.setUpdateTime(JsonTool.getJsonValue(resultObject, "updateTime", ""));

        mContentView = new StockContentView(mContext);
        mContentView.updateUI(stockInfo);
        addAnswerView(mContentView, true);
        Logger.d(TAG, "--StockShowSession mAnswer : " + mAnswer + "--");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.unisound.unicar.gui.session.BaseSession#release()
     */
    @Override
    public void release() {
        // TODO Auto-generated method stub
        super.release();
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
