/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : MediaInfo.java
 * @ProjectName : vui_datamodel
 * @PakageName : cn.yunzhisheng.vui.modes
 * @Author : Brant
 * @CreateDate : 2014-9-22
 */
package com.unisound.unicar.gui.model;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-9-22
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-9-22
 * @Modified: 2014-9-22: 实现基本功能
 */
public class MediaInfo {
    public static final String TAG = "MediaInfo";
    private long mId;
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private String mDuration;
    private String mPath;

    public MediaInfo(String title, String artist, String album, String duration, String path) {
        mTitle = title;
        mArtist = artist;
        mAlbum = album;
        mDuration = duration;
        mPath = path;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        this.mAlbum = album;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        this.mDuration = duration;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    @Override
    public String toString() {
        return "(mId=" + mId + ",mTitle=" + mTitle + ",mArtist=" + mArtist + ",mAlbum=" + mAlbum
                + ",mDuration=" + mDuration + ",mPath=" + mPath + ")";
    }
}
