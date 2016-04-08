package com.aispeech.aios.adapter.localScanService.db;

import android.provider.BaseColumns;

/**
 * Created by Jervis on 2015/12/3.
 */
public class MusicLocal implements BaseColumns{

    /**
     *数据库表名称
     */
    public static final String TABLE_NAME = "music_local";
    /**
     * 歌手列
     */
    public static final String ARTIST = "artist";
    /**
     *歌曲名称列
     */
    public static final String TITLE = "title";
    /**
     * 专辑列
     */
    public static final String ALBUM = "album";
    /**
     * 时长列
     */
    public static final String DURATION = "duration";
    /**
     * 文件大小列
     */
    public static final String SIZE = "_size";
    /**
     * 时间列
     */
    public static final String DATA = "_data";
    /**
     * 歌曲类型列
     */
    public static final String MIME_TYPE = "mime_type";
    /**
     * 文件路径列
     */
    public static final String PATH = "_path";
    /**
     * 文件名列
     */
    public static final String FILENAME= "file_name";


}
