/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : CommandPreference.java
 * @ProjectName : CarPlay
 * @PakageName : cn.yunzhisheng.vui.assistant.preference
 * @Author : Brant
 * @CreateDate : 2014-9-24
 */
package com.unisound.unicar.gui.preference;


/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2014-9-24
 * @ModifiedBy : Brant
 * @ModifiedDate: 2014-9-24
 * @Modified:
 * 2014-9-24: 实现基本功能
 */
public class CommandPreference {
	public static final String TAG = "CommandPreference";

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

	public static final String TOGGLEPAUSE_ACTION = "com.android.music.musicservicecommand.togglepause";
	public static final String PAUSE_ACTION = "com.android.music.musicservicecommand.pause";
	public static final String PREVIOUS_ACTION = "com.android.music.musicservicecommand.previous";
	public static final String NEXT_ACTION = "com.android.music.musicservicecommand.next";
	//add by panda 2015-7-26
	public static final String EXIT_ACTION = "com.android.music.musicservicecommand.stop";
	
	public static final String ACTION_MUSIC_DATA = "com.android.music.musicservicecommand.musicdata";
	public static final String ACTION_MUSIC_LOCAL = "com.android.music.musicservicecommand.localdata";
	public static final String MUSCIC_DATA = "musicData";
	public static final boolean MUSIC_CUSTOM = true;
}
