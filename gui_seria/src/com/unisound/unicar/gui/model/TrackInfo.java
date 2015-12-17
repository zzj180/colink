/*
 * Copyright (C) 2009 Teleca Poland Sp. z o.o. <android@teleca.com> Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.unisound.unicar.gui.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TrackInfo implements Parcelable {
    private String id;
    private String title;
    private String artist;
    private String album;
    private int duration;
    private String url;
    private String imgUrl;

    public static final Parcelable.Creator<TrackInfo> CREATOR =
            new Parcelable.Creator<TrackInfo>() {

                @Override
                public TrackInfo createFromParcel(Parcel source) {
                    return new TrackInfo(source);
                }

                @Override
                public TrackInfo[] newArray(int size) {
                    return new TrackInfo[size];
                }
            };

    public TrackInfo() {}

    private TrackInfo(Parcel source) {
        readFromParcel(source);
    }

    // 注意写入变量和读取变量的顺序应该一致 不然得不到正确的结果
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeInt(duration);
        dest.writeString(url);
        dest.writeString(imgUrl);
    }

    // 注意读取变量和写入变量的顺序应该一致 不然得不到正确的结果
    public void readFromParcel(Parcel source) {
        id = source.readString();
        title = source.readString();
        artist = source.readString();
        album = source.readString();
        duration = source.readInt();
        url = source.readString();
        imgUrl = source.readString();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "id: " + id + ", title " + title + ", artist " + artist + ", album " + album
                + ", duration " + duration + ", url " + url + ", imgUrl " + imgUrl;
    }
}
