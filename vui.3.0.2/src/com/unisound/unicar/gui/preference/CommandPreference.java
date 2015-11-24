/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : CommandPreference.java
 * @ProjectName : UniDriveGUI
 * @PakageName : cn.yunzhisheng.vui.assistant.preference
 * @Author : Brant
 * @CreateDate : 2014-9-24
 */
package com.unisound.unicar.gui.preference;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author :
 * @CreateDate : 2014-9-24
 * @ModifiedBy : Xiaodong.He
 * @ModifiedDate: 2015-9-30
 * @Modified: update variable name
 */
public class CommandPreference {

    /* action control all players */
    public static final String ACTION_SERVICE_CMD = "com.android.music.musicservicecommand";

    public static final String ACTION_CONTROL_KWMUSIC =
            "com.unisound.action.ACTION_CONTROL_KWMUSIC";

    public static final String ACTION_CONTROL_XMFM = "com.unisound.action.ACTION_CONTROL_XMFM";

    public static final String ACTION_CONTROL_UNICARFM =
            "com.unisound.action.ACTION_CONTROL_UNICARFM";

    public static final String CMD_KEY_NAME = "command";

    public static final String CMD_NAME_TOGGLEPAUSE = "togglepause";
    public static final String CMD_NAME_STOP = "stop";
    public static final String CMD_NAME_PAUSE = "pause";
    public static final String CMD_NAME_PLAY = "play";
    public static final String CMD_NAME_PREVIOUS = "previous";
    public static final String CMD_NAME_NEXT = "next";
    public static final String CMD_NAME_OPEN_MUSIC = "open_music";
	public static final String MUSIC_KEY="key";
	public static final String MUSIC_PATH="path";

    /** 随机播放 */
    public static final String CMD_NAME_SHUFFLE_PLAYBACK = "shuffle_playback";
    /** 顺序播放 */
    public static final String CMD_NAME_ORDER_PLAYBACK = "order_playback";
    /** 单曲循环 */
    public static final String CMD_NAME_SINGLE_CYCLE = "single_cycle";
    /** 列表循环，全部循环 */
    public static final String CMD_NAME_FULL_CYCLE = "full_cycle";
    /** 打开桌面歌词 */
    public static final String CMD_NAME_OPEN_DESK_LYRIC = "open_desk_lyric";
    /** 关闭桌面歌词 */
    public static final String CMD_NAME_CLOSE_DESK_LYRIC = "close_desk_lyric";

    /* < xiaodong added 20150930 Begin */
    public static final String CMD_KEY_FROM = "cmd_from";
    public static final String CMD_FROM_ON_RECORDING_START = "cmd_from_on_recording_start";
    public static final String CMD_FROM_ON_SESSION_DONE = "cmd_from_on_session_done";
    /* xiaodong added 20150930 End > */

    public static final String ACTION_TOGGLEPAUSE =
            "com.android.music.musicservicecommand.togglepause";
    public static final String ACTION_PAUSE = "com.android.music.musicservicecommand.pause";
    public static final String ACTION_PREVIOUS = "com.android.music.musicservicecommand.previous";
    public static final String ACTION_NEXT = "com.android.music.musicservicecommand.next";
    // add by panda 2015-7-26
    public static final String ACTION_EXIT = "com.android.music.musicservicecommand.stop";

    public static final String ACTION_MUSIC_DATA =
            "com.android.music.musicservicecommand.musicdata";
	public static final String ACTION_MUSIC_LOCAL = "com.android.music.musicservicecommand.localdata";

     public static final String MUSCIC_DATA = "musicData"; //XD delete 20150930
     public static final boolean MUSIC_CUSTOM = true; //XD delete 20150930

}
