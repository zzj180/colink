package com.aispeech.aios.adapter.bean;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/16.
 */
public class StockBean extends BaseBean{

    public BaseData mBaseData = null;
    public List<ChartData> mStockList = null;
    public List<String> mTimeList = null;
    public int curPosition = 0;
    public float curMaxTransPrice = 0.0f;
    public float curMaxTransNum = 0.0f;
    public String res = "";

    public float baseLineDate = 0.0f;
    public float length = 0.0f;

    public StockBean() {
        mStockList = new ArrayList<ChartData>();
        curPosition = 0;
        curMaxTransPrice = 0.0f;
        curMaxTransNum = 0.0f;
        mBaseData = new BaseData();
        mTimeList = new ArrayList<String>();
    }

    public void clearStockData() {
        if(mStockList == null) {
            mStockList = new ArrayList<ChartData>();
        } else {
            mStockList.clear();
        }

        if(mTimeList == null) {
            mTimeList  = new ArrayList<String>();
        } else {
            mTimeList.clear();
        }
        curPosition = 0;
        curMaxTransPrice = 0.0f;
        curMaxTransNum = 0.0f;
    }

    public void clearChartData() {
        if(mStockList != null && mStockList.size() > 0) {
            mStockList.clear();
        }

        if(mTimeList != null && mTimeList.size() > 0) {
            mTimeList.clear();
        }
    }

    public void insertChartData(ChartData item) {
        if(mStockList != null) {
            mStockList.add(item);
        }
    }

    public void notifyMaxDataRetrive() {
        int length = mStockList.size();
        for(int i = curPosition; i < length; i++) {
            ChartData item = mStockList.get(i);
            if(item.mTransPrice > curMaxTransPrice) {
                curMaxTransPrice = item.mTransPrice;
            }

            if(item.mTransNum > curMaxTransNum) {
                curMaxTransNum = item.mTransNum;
            }
        }
        curPosition = length - 1;
    }


    public void parseJson(JSONObject jsonObject) {
        if(jsonObject != null) {
            JSONObject result = jsonObject.optJSONObject("result");
            if(result == null) {
                res = "fail";
                return;
            }

            res = "success";

            if(mBaseData == null) {
                mBaseData = new BaseData();
            }

            mBaseData.symbol = result.optString("symbol");
            mBaseData.code = result.optString("code");
            mBaseData.name = result.optString("name");
            mBaseData.exchange = result.optString("exchange");

            mBaseData.low = StringUtil.isDecimalNumber(result.optString("low")) ? Float.valueOf(result.optString("low")) : 0.0f ;
            mBaseData.current = StringUtil.isDecimalNumber(result.optString("currrent")) ? Float.valueOf(result.optString("currrent")) : 0.0f;
            mBaseData.peLYR = StringUtil.isDecimalNumber(result.optString("peLYR")) ? Float.valueOf(result.optString("peLYR")) : 0.0f;
            mBaseData.peTTM = StringUtil.isDecimalNumber(result.optString("peTTM")) ? Float.valueOf(result.optString("peTTM")) : 0.0f;
            mBaseData.high = StringUtil.isDecimalNumber(result.optString("high")) ? Float.valueOf(result.optString("high")) : 0.0f;
            mBaseData.lastClose = StringUtil.isDecimalNumber(result.optString("lastClose")) ? Float.valueOf(result.optString("lastClose")) : 0.0f;
            mBaseData.percentage = StringUtil.isDecimalNumber(result.optString("percentage")) ? Float.valueOf(result.optString("percentage")) : 0.0f;
            mBaseData.change = StringUtil.isDecimalNumber(result.optString("change")) ? Float.valueOf(result.optString("change")) : 0.0f;
            mBaseData.amount = StringUtil.isDecimalNumber(result.optString("amount")) ? Float.valueOf(result.optString("amount")) : 0.0f;
            mBaseData.open = StringUtil.isDecimalNumber(result.optString("open")) ? Float.valueOf(result.optString("open")) : 0.0f;
            mBaseData.adate = dateStrConvert(result.optString("date") == null? "" : result.optString("date"));
            mBaseData.avolume = StringUtil.isDecimalNumber(result.optString("volume")) ? Float.valueOf(result.optString("volume")) : 0.0f;
            mBaseData.turnoverRate = StringUtil.isDecimalNumber(result.optString("turnoverRate")) ? Float.valueOf(result.optString("turnoverRate")) : 0.0f;
            mBaseData.state = result.optString("state");

            if(Math.abs(mBaseData.high - mBaseData.lastClose) > Math.abs(mBaseData.lastClose - Math.abs(mBaseData.low))) {
                baseLineDate = Math.abs(mBaseData.lastClose - Math.abs(mBaseData.high - mBaseData.lastClose));
                length = Math.abs(mBaseData.high - mBaseData.lastClose) * 2;
            } else {
                baseLineDate = Math.abs(mBaseData.lastClose - Math.abs(mBaseData.low - mBaseData.lastClose));
                length = Math.abs(mBaseData.low - mBaseData.lastClose) * 2;
            }

            JSONArray time = result.optJSONArray("tradeTime");
            if(time != null) {
                int length = time.length();
                for(int i = 0; i < length; i++) {
                    String item = time.optString(i);
                    if(mTimeList != null) {
                        mTimeList.add(item);
                    }
                }
            }


            JSONArray list = result.optJSONObject("chart") != null ? result.optJSONObject("chart").optJSONArray("list") : null;
            if (list != null) {
                int length = list.length();
                for (int i = 0; i < length; i++) {
                    try {
                        JSONObject item = list.getJSONObject(i);
                        if (item != null) {
                            String date = item.optString("date");
                            String volume = item.optString("volume");
                            String price = item.optString("price");
                            insertChartData(new ChartData(Float.valueOf(price), Float.valueOf(volume), date));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        } else {
            res = "fail";
        }

    }

    private String dateStrConvert(String date) {
        String convertDate = null;
        String sets[] = date.split("\\+");
        if(sets != null && sets.length == 2) {
            convertDate = sets[0].replace("T", " ");
        } else {
            sets = date.split("T");
            if(sets != null && sets.length == 2) {
                int index = sets[1].lastIndexOf("-");
                if(index != -1 && index < sets[1].length()) {
                    convertDate = sets[0] + " " + sets[1].substring(0,  index);
                }

            }
        }
        AILog.e("tag", "-------  " +convertDate + " ");
        return convertDate;
    }

    public static class ChartData {
        public float mTransPrice = 0.0f;
        public float mTransNum = 0.0f;
        public String mTime;

        public ChartData(float transPrice, float transNum, String mTime) {
            this.mTransPrice = transPrice;
            this.mTransNum = transNum;
            this.mTime = mTime;
        }
    }

    public static class BaseData {
        public String symbol = "";
        public float low;
        public float current;
        public float peLYR;
        public float peTTM;
        public float high;
        public float lastClose;
        public float percentage;
        public float change;
        public float turnoverRate;
        public String code = "";
        public float amount;
        public float open;
        public String adate = "";
        public float avolume;
        public String name = "";
        public String exchange = "";
        public String state = "";
    }
}
