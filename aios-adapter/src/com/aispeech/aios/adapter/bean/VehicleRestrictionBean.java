package com.aispeech.aios.adapter.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spring on 2016/2/16.
 * to do:
 */
public class VehicleRestrictionBean extends BaseBean {

    private String date;
    private String city;
    private List<ImageInfo> rules = new ArrayList<ImageInfo>();
    private String errId;
    private String error;

    /**
     * 获取查询的日期
     * @return 日期
     */
    public String getDate() {
        return date;
    }

    /**
     * 写入查询的日期
     * @param date 日期
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 获取查询的城市
     * @return 城市名称
     */
    public String getCity() {
        return city;
    }

    /**
     * 写入查询的城市
     * @param city 城市名称
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取限行规则列表
     * @return 规则列表
     */
    public List<ImageInfo> getRules() {
        return rules;
    }

    /**
     * 写入限行规则列表
     * @param rules 规则列表
     */
    public void setRules(List<ImageInfo> rules) {
        this.rules = rules;
    }

    /**
     * 获取查询错误码
     * @return 错误码
     */
    public String getErrId() {
        return errId;
    }

    /**
     * 写入错误码
     * @param errId 错误码
     */
    public void setErrId(String errId) {
        this.errId = errId;
    }

    /**
     * 获取错误内容
     * @return 错误内容
     */
    public String getError() {
        return error;
    }

    /**
     * 写入错误内容
     * @param error 错误内容
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * 限行规则子类
     */
    public static class ImageInfo {
        private String url;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
