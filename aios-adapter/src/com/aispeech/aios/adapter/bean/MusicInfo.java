package com.aispeech.aios.adapter.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @desc 音乐的javaBean
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class MusicInfo extends BaseBean implements Cloneable {

    private long id;
    private String artist;
    private long duration;
    private boolean isCloudMusic = false;
    private String cloudUrl = "";
    private String path;
    private String fileName;

    /**
     * 名字
     */
    private String name;
	
    /**
     *获取歌曲名
     * @return 歌曲名
     */
    public String getDisplayName() {
        return fileName;
    }

    /**
     *设置歌曲名
     * @param fileName
     */
    public void setDisplayName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获得歌曲id
     * @return  歌曲id
     */
    public long getId() {
        return id;
    }

    /**
     * 设置歌曲id
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     *获得歌手名字
     * @return 歌手名字
     */
    public String getArtist() {
        return artist;
    }

    /**
     * 设置歌手名字
     * @param artist 歌手名字
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * 获得歌曲时长
     * @return 歌曲时长
     */
    public long getDuration() {
        return duration;
    }

    /**
     * 设置歌曲时长
     * @param duration 歌曲时长
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     *网络歌曲url地址
     * @return url地址
     */
    public String getCloudUrl() {
        return cloudUrl;
    }

    /**
     * 获得歌曲路径
     * @return 歌曲路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置歌曲路径
     * @param path 歌曲路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        JSONObject item = new JSONObject();
        try {
            item.put("id", getId() + "");
            item.put("title", getName());
            item.put("artist", getArtist());
            item.put("name", getDisplayName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MusicInfo clone = null;
        try {
            clone = (MusicInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }
}
