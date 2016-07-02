package com.aispeech.aios.adapter.bean;


import java.util.List;

import com.google.gson.annotations.SerializedName;


/**
 * Created by zzj on 2016/3/1.
 */
public class Contact {

    /**
     * name : 移动客服
     * phone_info : [{"number":"10086","operator":"移动","location":"苏州","flag":"工作","type":"手机"}]
     */

    private String name;
    /**
     * number : 10086
     * operator : 移动
     * location : 苏州
     * flag : 工作
     * type : 手机
     */

    @SerializedName("phone_info")
    private List<PhoneInfoEntity> phoneInfo;

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneInfo(List<PhoneInfoEntity> phoneInfo) {
        this.phoneInfo = phoneInfo;
    }

    public String getName() {
        return name;
    }

    public List<PhoneInfoEntity> getPhoneInfo() {
        return phoneInfo;
    }

    public static class PhoneInfoEntity {
        private String number;
        private String operator;
        private String location;
        private String flag;
        private String type;

        public void setNumber(String number) {
            this.number = number;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNumber() {
            return number;
        }

        public String getOperator() {
            return operator;
        }

        public String getLocation() {
            return location;
        }

        public String getFlag() {
            return flag;
        }

        public String getType() {
            return type;
        }
    }
}
