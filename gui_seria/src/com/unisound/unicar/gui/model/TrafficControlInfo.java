package com.unisound.unicar.gui.model;

import java.util.Calendar;


/**
 * 限行bean对象类
 * 
 * @author Ch
 * 
 */
public class TrafficControlInfo {

    private String titleText; // XD 20150824 added
    private String weeks[];// 周一至周日的限行信息
    private String toady;// 今天的限行信息
    private String dateInfo;// 时间头信息
    private String trafficRule;// 限行规则

    public TrafficControlInfo() {}

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getTrafficRule() {
        return trafficRule;
    }

    public void setTrafficRule(String trafficRule) {
        this.trafficRule = trafficRule;
    }

    public String[] getWeeks() {
        return weeks;
    }

    public void setWeeks(String[] weeks) {
        this.weeks = weeks;
    }

    public String getDateInfo() {
        return dateInfo;
    }

    public void setDateInfo(String dateInfo) {
        this.dateInfo = dateInfo;
    }

    public String getToady() {
        return toady;
    }

    public void setToady(String toady) {
        this.toady = toady;
    }

    /**
     * 获取当前年月日星期几
     * 
     * @author chenhao
     * @return
     */
    public String getStringData() {
        Calendar c = Calendar.getInstance();
        // c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        String results = mMonth + "月" + mDay + "日 " + "\t星期" + mWay;
        return results;
    }

    /**
     * 获取星期几 (周日为一周的第一天 此处为了跟UI对应做个转换)
     * 
     * @author chenhao
     * @return
     */
    public int getWeek() {
        Calendar c = Calendar.getInstance();
        int mWay = c.get(Calendar.DAY_OF_WEEK);
        if (mWay == 1)
            mWay = 7;
        else
            mWay = mWay - 1;
        return mWay;
    }
}
