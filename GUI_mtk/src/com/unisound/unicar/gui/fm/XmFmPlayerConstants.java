/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : XmFmPlayerConstants.java
 * @ProjectName : uniCarSolution_xd_dev_0929
 * @PakageName : com.unisound.unicar.gui.fm
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-9-29
 */
package com.unisound.unicar.gui.fm;

/**
 * 
 * @author Xiaodong.He
 * @date 2015-09-29
 */
public class XmFmPlayerConstants {

    public static final int STATE_IDLE = 0;
    public static final int STATE_INITIALIZED = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_STARTED = 3;
    public static final int STATE_STOPPED = 4;
    public static final int STATE_PAUSED = 5;
    public static final int STATE_COMPLETED = 6;
    public static final int STATE_ERROR = 7;
    public static final int STATE_END = 8;
    public static final int STATE_PREPARING = 9;
    public static final int STATE_ADS_BUFFERING = 10;
    public static final int STATE_PLAYING_ADS = 11;

    /**
     * is XmFm on Background
     * 
     * @param state
     * @return
     */
    public static boolean isXmFmOnBackground(int state) {
        if (STATE_INITIALIZED == state || STATE_PREPARED == state || STATE_STARTED == state
                || STATE_STOPPED == state || STATE_PAUSED == state || STATE_COMPLETED == state
                || STATE_END == state || STATE_PREPARING == state || STATE_ADS_BUFFERING == state
                || STATE_PLAYING_ADS == state) {
            return true;
        } else {
            // STATE_IDLE STATE_ERROR
            return false;
        }
    }

    /**
     * is XmFm Playing
     * 
     * @param state
     * @return
     */
    public static boolean isXmFmPlaying(int state) {
        if (STATE_STARTED == state || STATE_COMPLETED == state || STATE_PREPARING == state
                || STATE_ADS_BUFFERING == state || STATE_PLAYING_ADS == state) {
            return true;
        } else {
            return false;
        }
    }
}
