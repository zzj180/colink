package com.aispeech.aios.adapter.bean;


/**
 * @desc AIOS FM搜索接口，返回数据结构
 * @auth AISPEECH
 * @date 2016-01-11
 * @copyright aispeech.com
 */
public class FmSearchParam {

    /**
     * category : 相声
     * album : 小沈阳专辑
     * track : 不差钱
     * artist : 小沈阳
     * keyword : 小沈阳&不差钱
     */

    private String category;
    private String album;
    private String track;
    private String artist;
    private String keyWord;

    /**
     * 获取搜索的FM类别
     * @return FM电台类别
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置搜索的FM类别
     * @param category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 获得搜索的FM专辑名
     * @return 搜索的FM专辑名
     */
    public String getAlbum() {
        return album;
    }

    /**
     * 设置搜索的FM专辑名
     * @param album 搜索的FM专辑名
     */
    public void setAlbum(String album) {
        this.album = album;
    }

    /**
     * 获取搜索的FM引用源
     * @return 索的FM引用源
     */
    public String getTrack() {
        return track;
    }

    /**
     * 设置搜索的FM引用源
     * @param track 搜索的FM引用源
     */
    public void setTrack(String track) {
        this.track = track;
    }

    /**
     * 获取收听的FM艺术家名字
     * @return 收听的FM艺术家名字
     */
    public String getArtist() {
        return artist;
    }

    /**
     *设置收听的FM艺术家名字
     * @param artist 收听的FM艺术家名字
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     *获取收听的FM搜索关键词
     * @return 收听的FM搜索关键词
     */
    public String getKeyWord() {
        return keyWord;
    }

    /**
     *获取收听的FM搜索关键词
     * @param keyWord 收听的FM搜索关键词
     */
    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    @Override
    public String toString() {
        return "FmSearchParam{" +
                "category='" + category + '\'' +
                ", album='" + album + '\'' +
                ", track='" + track + '\'' +
                ", artist='" + artist + '\'' +
                ", keyWord='" + keyWord + '\'' +
                '}';
    }
}
