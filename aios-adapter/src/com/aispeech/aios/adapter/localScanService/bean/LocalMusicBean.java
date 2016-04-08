package com.aispeech.aios.adapter.localScanService.bean;

import com.aispeech.aios.adapter.bean.MusicInfo;

/**
 * @desc 扫描本地音乐的Bean
 * @auth AISPEECH
 * @date 2016-02-19
 * @copyright aispeech.com
 */
public class LocalMusicBean {
    private long id;
    private String fileName;//文件名  如 我想听歌.map3
    private String artist;//歌手
    private String musicName;//歌曲名
    private String album;//专辑名
    private String path;//歌曲完整路径
    private String data;//
    private String duration;//时长
    private String size;//文件大小
    private String mime;//音频类型
    private boolean isCloud;//是否云端音乐

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isCloud() {
        return isCloud;
    }

    public void setCloud(boolean cloud) {
        isCloud = cloud;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }


    @Override
    public String toString() {
        return "MusicInfo{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", artist='" + artist + '\'' +
                ", musicName='" + musicName + '\'' +
                ", album='" + album + '\'' +
                ", path='" + path + '\'' +
                ", data='" + data + '\'' +
                ", duration='" + duration + '\'' +
                ", size='" + size + '\'' +
                ", mime='" + mime + '\'' +
                ", isCloud=" + isCloud +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof MusicInfo) {

            LocalMusicBean info = (LocalMusicBean) o;

            return (this.fileName.equals(info.getFileName())) && (this.size.equals(info.getSize()));//认为文件名和大小是一样的，就是同一首歌

        }
        return super.equals(o);
    }
}
