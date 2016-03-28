/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : CommandPreference.java
 * @ProjectName : UniDriveGUI
 * @PakageName : cn.yunzhisheng.vui.assistant.preference
 * @Author : Brant
 * @CreateDate : 2014-9-24
 */
package com.android.kwmusic;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author :ZZJ
 * @CreateDate : 2016-3-27
 */
public class CommandPreference {

    /* action control all players */

    public static final String ACTION_CONTROL_KWMUSIC =
            "com.unisound.action.ACTION_CONTROL_KWMUSIC";

    public static final String ACTION_CONTROL_XMFM = "com.unisound.action.ACTION_CONTROL_XMFM";

    public static final String ACTION_CONTROL_UNICARFM =
            "com.unisound.action.ACTION_CONTROL_UNICARFM";

    public static final String SERVICECMD = "com.android.music.musicservicecommand";
	public static final String CMDNAME = "command";
	public static final String CMDTOGGLEPAUSE = "togglepause";
	public static final String CMDSTOP = "stop";
	public static final String CMDPAUSE = "pause";
	public static final String CMDPLAY = "play";
	public static final String CMDPREVIOUS = "previous";
	public static final String CMDNEXT = "next";
	public static final String CMDOPEN_MUSIC="open_music";
	public static final String MUSIC_KEY="key";
	public static final String MUSIC_PATH="path";
	public static final String CMDSHUFFLE_PLAYBACK = "shuffle_playback";//随机播放
	public static final String CMDORDER_PLAYBACK = "order_playback";//顺序播放
	public static final String CMDSINGLE_CYCLE = "single_cycle";//单曲循环
	public static final String CMDFULL_CYCLE = "full_cycle";//列表循环，全部循环
	public static final String CMDOPEN_DESK_LYRIC = "open_desk_lyric";//打开桌面歌词
	public static final String CMDCLOSE_DESK_LYRIC = "close_desk_lyric";//关闭桌面歌词

    /* < xiaodong added 20150930 Begin */
    public static final String CMD_KEY_FROM = "cmd_from";
    public static final String CMD_FROM_ON_RECORDING_START = "cmd_from_on_recording_start";
    public static final String CMD_FROM_ON_SESSION_DONE = "cmd_from_on_session_done";
    /* xiaodong added 20150930 End > */

    public static final String TOGGLEPAUSE_ACTION = "com.android.music.musicservicecommand.togglepause";
	public static final String PAUSE_ACTION = "com.android.music.musicservicecommand.pause";
	public static final String PREVIOUS_ACTION = "com.android.music.musicservicecommand.previous";
	public static final String NEXT_ACTION = "com.android.music.musicservicecommand.next";
	//add by panda 2015-7-26
	public static final String EXIT_ACTION = "com.android.music.musicservicecommand.stop";
	
	public static final String ACTION_MUSIC_DATA = "com.android.music.musicservicecommand.musicdata";
	public static final String ACTION_MUSIC_LOCAL = "com.android.music.musicservicecommand.localdata";


	
}
