package com.aispeech.aios.adapter.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Spring on 2016/2/16.
 * to do:
 */
public class BaseBean implements Parcelable {
    private String title;
    private String outPut;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOutPut() {
        return outPut;
    }

    public void setOutPut(String outPut) {
        this.outPut = outPut;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
