package com.aispeech.aios.adapter.node;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.bean.StockBean;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.util.DateUtils;
import com.aispeech.aios.adapter.util.StringUtil;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @desc 股票领域对应节点
 * @auth AISPEECH
 * @date 2016-01-18
 * @copyright aispeech.com
 */
public class StockNode extends BaseNode {

    private static StockNode mInstance;
    private static final String TAG = "AIOS-Adapter-StockNode";

    private StockBean mStockBean;

    public static synchronized StockNode getInstance() {
        if (mInstance == null) {
            mInstance = new StockNode();
        }
        return mInstance;
    }

    private StockNode() {
    }

    @Override
    public String getName() {
        return "stock";
    }

    @Override
    public void onJoin() {
        super.onJoin();
        bc.subscribe("data.stock.query.result");
    }

    @Override
    public void onMessage(String topic, byte[]... parts) throws Exception {
        super.onMessage(topic, parts);
        AILog.i(TAG, topic, parts);

        if (AiosApi.Player.STATE.equals(topic) && "wait".equals(StringUtil.getEncodedString(parts[0]))) {
            bc.unsubscribe(AiosApi.Player.STATE);
        }

        UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI, null, null);

        if(topic.equals("data.stock.query.result")) {
            int retCode = Integer.valueOf(new String(parts[1], "utf-8"));
            if(retCode == 0) {
                getSuccessStockInfo(new JSONObject(new String(parts[0], "utf-8")));
            } else if(retCode == -1) {
                TTSNode.getInstance().playRpc("stock.query.stock.result", "网络条件不佳，请重试!");
                UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
            } else if(retCode == -2) {
                TTSNode.getInstance().playRpc("stock.query.stock.result", "网络条件不佳，请重试!");
                UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
            }
        }

    }

    @Override
    public BusClient.RPCResult onCall(String url, byte[]... args) throws Exception {
        AILog.i(TAG, url, args);

        bc.call("/data" + url, args);
        UiEventDispatcher.notifyUpdateUI(UIType.LoadingUI);
        return null;
    }


    private void getSuccessStockInfo(JSONObject response) {
        mStockBean = new StockBean();

        mStockBean.parseJson(response);
        if("success".equals(mStockBean.res)) {
            mStockBean.notifyMaxDataRetrive();

            mStockBean.setTitle(mStockBean.mBaseData.name);

            String percent = mStockBean.mBaseData.percentage >= 0 ? "上涨 " + mStockBean.mBaseData.change +"， 涨幅 " + mStockBean.mBaseData.percentage + "%" : "下跌 "+ -mStockBean.mBaseData.change +"， 跌幅 "+ -mStockBean.mBaseData.percentage + "%";

            float price = mStockBean.mBaseData.current;
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            if("suspend".equals(mStockBean.mBaseData.state)) {
                String speack = mStockBean.mBaseData.name + "目前已停牌, 停牌前价格 " + mStockBean.mBaseData.lastClose;
                TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                UiEventDispatcher.notifyUpdateUI(UIType.StockNodeUI, mStockBean);
                AILog.i(TAG, "current stock is suspend");
                return;
            }

            try {
                Date  date = df.parse(mStockBean.mBaseData.adate);
                if(DateUtils.isToday(date)) {
                    if(mStockBean.mTimeList.size() == 1) {
                        String tranTime[] = mStockBean.mTimeList.get(0).split("-");
                        if(tranTime != null && tranTime.length == 2) {
                            if (DateUtils.isBetweenTime(mStockBean.mBaseData.adate, tranTime[0], tranTime[1])) {
                                String speack = mStockBean.mBaseData.name + "现在" + percent + "，报 " + price;
                                TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                            } else if(DateUtils.isBetweenTime(mStockBean.mBaseData.adate, "00:00", tranTime[0])){
                                String speack = mStockBean.mBaseData.name + "现在" + percent + "，报 " + price;
                                TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                            } else {
                                String speack = mStockBean.mBaseData.name + "今天" + percent + "，报 " + price;
                                TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                            }
                        } else {
                            String speack = mStockBean.mBaseData.name + percent + "，报 " + price;
                            TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                        }
                    } else if(mStockBean.mTimeList.size() == 2) {
                        String tranTime0[] = mStockBean.mTimeList.get(0).split("-");
                        String tranTime1[] = mStockBean.mTimeList.get(1).split("-");
                        if(tranTime0 != null && tranTime1 != null && tranTime0.length == 2 && tranTime1.length == 2) {
                            if (DateUtils.isBetweenTime(mStockBean.mBaseData.adate, tranTime0[0], tranTime1[1])) {
                                String speack = mStockBean.mBaseData.name + "现在" + percent + "，报 " + price;
                                TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                            } else if(DateUtils.isBetweenTime(mStockBean.mBaseData.adate, "00:00", tranTime0[0])) {
                                String speack = mStockBean.mBaseData.name + "现在" + percent + "，报 " + price;
                                TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                            } else {
                                String speack = mStockBean.mBaseData.name + "今天" + percent + "，报 " + price;
                                TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                            }
                        } else {
                            String speack = mStockBean.mBaseData.name + percent + "，报 " + price;
                            TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                        }
                    } else {
                        String speack = mStockBean.mBaseData.name + percent + "，报 " + price;
                        TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                    }
                } else {
                    if(DateUtils.isWeenkend(mStockBean.mBaseData.adate)){
                        Map<String, String> params = DateUtils.getDateArray(mStockBean.mBaseData.adate);
                        if(params != null) {
                            String speack = params.get("month") + "月" + params.get("day") +"日" + mStockBean.mBaseData.name + percent +"，报 " + price;
                            TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                        } else {
                            String speack = mStockBean.mBaseData.name + percent +"，报 " + price;
                            TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                        }
                    } else {
                        Map<String, String> params = DateUtils.getDateArray(mStockBean.mBaseData.adate);
                        if(params != null) {
                            String speack = params.get("month") + "月" + params.get("day") +"日" + mStockBean.mBaseData.name + percent +"，报 " + price;
                            TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                        } else {
                            String speack = mStockBean.mBaseData.name + percent +"，报 " + price;
                            TTSNode.getInstance().playRpc("stock.query.stock.result", speack);
                        }
                    }
                }
                UiEventDispatcher.notifyUpdateUI(UIType.StockNodeUI, mStockBean);
            } catch (Exception e) {
                e.printStackTrace();
                TTSNode.getInstance().playRpc("stock.query.stock.result", "无法获取该股票信息!");
                UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
            }
        } else if("fail".equals(mStockBean.res)) {
            TTSNode.getInstance().playRpc("stock.query.stock.result", "未查到相关的股票!");
            UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
        } else {
            TTSNode.getInstance().playRpc("stock.query.stock.result", "未查到相关的股票!");
            UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
        }
    }


}
