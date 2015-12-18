package com.unisound.unicar.gui.model;

public class AppInfo {
    public String getmPackageName() {
        return mPackageName;
    }

    public void setmPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public String getmAppLabel() {
        return mAppLabel;
    }

    public void setmAppLabel(String mAppLabel) {
        this.mAppLabel = mAppLabel;
    }

    public String getmAppLabelPinyin() {
        return mAppLabelPinyin;
    }

    public void setmAppLabelPinyin(String mAppLabelPinyin) {
        this.mAppLabelPinyin = mAppLabelPinyin;
    }

    public String getmClassName() {
        return mClassName;
    }

    public void setmClassName(String mClassName) {
        this.mClassName = mClassName;
    }

    public String mPackageName;
    public String mAppLabel;
    public String mAppLabelPinyin;
    public String mClassName;

    public AppInfo(String appLabel, String pinyin, String packageName, String className) {
        mAppLabel = appLabel;
        mAppLabelPinyin = pinyin;
        mPackageName = packageName;
        mClassName = className;
    }

    @Override
    public String toString() {
        return "[mPackageName: " + mPackageName + ", mAppLabel: " + mAppLabel
                + ", mAppLabelPinyin: " + mAppLabelPinyin + ", mClassName:" + mClassName + "]";
    }
}
