/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : SmsItem.java
 * @ProjectName : NativeSMS
 * @PakageName : com.yzs.sms.demo
 * @Author : CavanShi
 * @CreateDate : 2013-3-13
 */
package com.unisound.unicar.gui.sms;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : CavanShi
 * @CreateDate : 2013-3-13
 * @ModifiedBy : CavanShi
 * @ModifiedDate: 2013-3-13
 * @Modified: 2013-3-13: 实现基本功能
 */

public class SmsItem implements Parcelable {

    private int id;
    /* private int threadId; */
    private String smsUri;
    private long time;
    private String name;
    private String message;
    private String number;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /*
     * public void setThreadId(int id) { threadId = id; } public int getThreadid() { return
     * threadId; }
     */

    public void setSmsUri(String uri) {
        smsUri = uri;
    }

    public String getSmsUri() {
        return smsUri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setMessage(String body) {
        message = body;
    }

    public String getMessage() {
        return message;
    }

    public void setNumber(String num) {
        number = num;
    }

    public String getNumber() {
        return number;
    }

    public SmsItem() {

    }

    public static final Parcelable.Creator<SmsItem> CREATOR = new Parcelable.Creator<SmsItem>() {

        @Override
        public SmsItem createFromParcel(Parcel source) {
            return new SmsItem(source);
        }

        @Override
        public SmsItem[] newArray(int size) {
            // TODO Auto-generated method stub
            return new SmsItem[size];
        }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        /*
         * dest.writeInt(id); dest.writeInt(threadId);
         */
        dest.writeString(smsUri);
        dest.writeLong(time);
        dest.writeString(name);
        dest.writeString(message);
        dest.writeString(number);
    }

    private SmsItem(Parcel in) {
        /*
         * id = in.readInt(); threadId = in.readInt();
         */
        smsUri = in.readString();
        time = in.readLong();
        name = in.readString();
        message = in.readString();
        number = in.readString();
    }
}
