/**
 * Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : ContactInfo.java
 * @ProjectName : V Plus 1.0
 * @PakageName : cn.yunzhisheng.ishuoshuo.provider
 * @Author : Brant
 * @CreateDate : 2012-5-30
 */
package com.unisound.unicar.gui.model;

import java.util.ArrayList;


/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2012-5-30
 * @ModifiedBy : Brant
 * @ModifiedDate: 2012-5-30
 * @Modified: 2012-5-30: 实现基本功能
 */
public class ContactInfo {
    public static final String TAG = "ContactInfo";

    public static final int CONTACT_TYPE_PHONE = 1;
    public static final int CONTACT_TYPE_SIM = 2;
    public static final int CONTACT_TYPE_USEFUL = 3;

    public static final int CONTACT_HAS_NUMBER = 1;
    public static final int CONTACT_NO_NUMBER = 0;

    private int id;
    private long contactId = -1;
    private int contactType = 1;
    private String displayName = "";
    private int photoId = 0;
    private String quanpin = "";
    private int hasPhoneNumber = 0;

    private ArrayList<PhoneNumberInfo> phones = new ArrayList<PhoneNumberInfo>();

    /* 2014.02.07 added by sc for nick name use */
    private ArrayList<String> nickNameList = new ArrayList<String>();

    public boolean isExistNickName() {
        return (nickNameList != null && nickNameList.size() > 0);
    }

    public ArrayList<String> getNickName() {
        return nickNameList;
    }

    public void setNickNameList(ArrayList<String> l) {
        nickNameList = l;
    }

    /* end */

    /**
     * @return the contactId
     */
    public long getContactId() {
        return contactId;
    }

    public long getRawContactId() {
        return contactId / 10;
    }

    public int getContactType() {
        return contactType;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return the photoId
     */
    public int getPhotoId() {
        return photoId;
    }

    /**
     * @return the quanpin
     */
    public String getQuanpin() {
        return quanpin;
    }

    /**
     * @return the hasPhoneNumber
     */
    public int hasPhoneNumber() {
        return hasPhoneNumber;
    }

    /**
     * @param contactId the contactId to set
     */
    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName == null ? "" : displayName;
    }

    /**
     * @param hasPhoneNumber the hasPhoneNumber to set
     */
    public void setHasPhoneNumber(int hasPhoneNumber) {
        this.hasPhoneNumber = hasPhoneNumber;
    }

    /**
     * @param photoId the photoId to set
     */
    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    /**
     * @param quanpin the quanpin to set
     */
    public void setQuanpin(String quanpin) {
        this.quanpin = quanpin == null ? "" : quanpin;
    }

    @Override
    public String toString() {
        return "ContactId:" + contactId + ",DisplayName:" + displayName + ",Quanpin:" + quanpin
                + ",HasPhoneNumber" + hasPhoneNumber + ",PhotoId:" + photoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addPhone(PhoneNumberInfo info) {
        this.phones.add(info);
    }

    // modify by WLP at 2013-09-12
    public void cleanPhone() {
        this.phones.clear();
    }

    // end

    public void addPhones(ArrayList<PhoneNumberInfo> infos) {
        this.phones.addAll(infos);
    }

    public ArrayList<PhoneNumberInfo> getPhones() {
        return this.phones;
    }
}
