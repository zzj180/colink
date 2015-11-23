/**
 * Copyright (c) 2012-2012 Mango(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : PhoneNumberInfo.java
 * @ProjectName : iShuoShuo
 * @PakageName : cn.yunzhisheng.ishuoshuo.provider
 * @Author : Brant
 * @CreateDate : 2012-10-9
 */
package com.unisound.unicar.gui.model;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2012-10-9
 * @ModifiedBy : Brant
 * @ModifiedDate: 2012-10-9
 * @Modified: 2012-10-9: 实现基本功能
 */
public class PhoneNumberInfo {
    public static final String TAG = "PhoneNumberInfo";
    private int contactType;
    private int id;
    private int contactId;
    private String number;
    private String rawNumber;
    private int type;
    private String label;
    private int primaryValue;
    private int superPrimaryValue;

    private String attribution;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getRawNumber() {
        return rawNumber;
    }

    public void setRawNumber(String rawNumber) {
        this.rawNumber = rawNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPrimaryValue() {
        return primaryValue;
    }

    public void setPrimaryValue(int primaryValue) {
        this.primaryValue = primaryValue;
    }

    public int getSuperPrimaryValue() {
        return superPrimaryValue;
    }

    public void setSuperPrimaryValue(int superPrimaryValue) {
        this.superPrimaryValue = superPrimaryValue;
    }

    public int getContactType() {
        return contactType;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }

    @Override
    public String toString() {
        return "ContactType:" + contactType + ",Id:" + id + ",ContactId:" + contactId + ",Number:"
                + number + ",Attribution:" + attribution + ",RawNumber:" + rawNumber + ",type:"
                + type + ",label:" + label + ",PrimaryValue:" + primaryValue
                + ",SuperPrimaryValue:" + superPrimaryValue;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public String getAttribution() {
        return this.attribution;
    }
}
