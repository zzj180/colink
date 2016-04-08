package com.aispeech.aios.adapter.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/3/28.
 */
public class Songs {

    /**
     * id : 001
     * title : 情书
     * artist : 张学友
     * name : 一起在线听网-张学友-情书
     */

    private List<SongsBean> songs;

    public List<SongsBean> getSongs() {
        return songs;
    }

    public void setSongs(List<SongsBean> songs) {
        this.songs = songs;
    }

    public static class SongsBean {
        private int id;
        private String title;
        private String artist;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String xcc() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    
}
