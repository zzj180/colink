package com.unisound.unicar.gui.model;

public class StockInfo {
    private String mChartImgUrl;
    private String mName;// 股票名称
    private String mCode;// 股票代码
    private String mCurrentPrice;// 当前价格
    private double mChangeAmount;// 涨跌额
    private String mChangeRate;// 涨跌幅
    private String mTurnover;// 换手率
    private String mHighestPrice;// 最高价
    private String mLowestPrice;// 最低价
    private String mUpdateTime;// 更新时间
    private String mYesterdayClosingPrice;// 昨收盘
    private String mTodayOpeningPrice;// 今开盘
    private String mExchange;// 交易所

    public StockInfo() {}

    public StockInfo(String mChartImgUrl, String mName, String mCode, String mCurrentPrice,
            double mChangeAmount, String mChangeRate, String mTurnover, String mHighestPrice,
            String mLowestPrice, String mUpdateTime, String mYesterdayClosingPrice,
            String mTodayOpeningPrice, String mExchange) {
        this.mChartImgUrl = mChartImgUrl;
        this.mName = mName;
        this.mCode = mCode;
        this.mCurrentPrice = mCurrentPrice;
        this.mChangeAmount = mChangeAmount;
        this.mChangeRate = mChangeRate;
        this.mTurnover = mTurnover;
        this.mHighestPrice = mHighestPrice;
        this.mLowestPrice = mLowestPrice;
        this.mUpdateTime = mUpdateTime;
        this.mYesterdayClosingPrice = mYesterdayClosingPrice;
        this.mTodayOpeningPrice = mTodayOpeningPrice;
        this.mExchange = mExchange;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getYesterdayClosingPrice() {
        return mYesterdayClosingPrice;
    }

    public void setYesterdayClosingPrice(String mYesterdayClosingPrice) {
        this.mYesterdayClosingPrice = mYesterdayClosingPrice;
    }

    public String getTodayOpeningPrice() {
        return mTodayOpeningPrice;
    }

    public void setTodayOpeningPrice(String mTodayOpeningPrice) {
        this.mTodayOpeningPrice = mTodayOpeningPrice;
    }

    public String getChartImgUrl() {
        return mChartImgUrl;
    }

    public void setChartImgUrl(String mChartImgUrl) {
        this.mChartImgUrl = mChartImgUrl;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String mCode) {
        this.mCode = mCode;
    }

    public String getCurrentPrice() {
        return mCurrentPrice;
    }

    public void setCurrentPrice(String mCurrentPrice) {
        this.mCurrentPrice = mCurrentPrice;
    }

    public double getChangeAmount() {
        return mChangeAmount;
    }

    public void setChangeAmount(double mChangeAmount) {
        this.mChangeAmount = mChangeAmount;
    }

    public String getChangeRate() {
        return mChangeRate;
    }

    public void setChangeRate(String mChangeRate) {
        this.mChangeRate = mChangeRate;
    }

    public String getTurnover() {
        return mTurnover;
    }

    public void setTurnover(String mTurnover) {
        this.mTurnover = mTurnover;
    }

    public String getHighestPrice() {
        return mHighestPrice;
    }

    public void setHighestPrice(String mHighestPrice) {
        this.mHighestPrice = mHighestPrice;
    }

    public String getLowestPrice() {
        return mLowestPrice;
    }

    public void setLowestPrice(String mLowestPrice) {
        this.mLowestPrice = mLowestPrice;
    }

    public String getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(String mUpdateTime) {
        this.mUpdateTime = mUpdateTime;
    }

    public String getExchange() {
        return mExchange;
    }

    public void setExchange(String exchange) {
        this.mExchange = exchange;
    }
}
