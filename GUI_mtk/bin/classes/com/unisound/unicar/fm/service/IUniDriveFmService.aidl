package com.unisound.unicar.fm.service;
import com.unisound.unicar.fm.service.IUniDriveFmCallback;  

interface IUniDriveFmService {

    void playVoiceByKeyword(String keyword);
    
    void playVoice(String artist, String category, String keyword);
    
    /** 1-up; 2-next; 3-play; 4-pause; 5-stop; 6-exit;*/
    void playControl(int status);
    
    /**1-continue play last voice; 2-play next voice of last play list */
    void playHistory(int status);
    
    void playSeek(long milliseconds);
    
    /** 
     STATE_IDLE = 0;
     STATE_INITIALIZED = 1;
     STATE_PREPARED = 2;
     STATE_STARTED = 3;
     STATE_STOPPED = 4;
     STATE_PAUSED = 5;
     STATE_COMPLETED = 6;
     STATE_ERROR = 7;
     STATE_END = 8;
     STATE_PREPARING = 9;
     STATE_ADS_BUFFERING = 10;
     STATE_PLAYING_ADS = 11;
     **/
    int getPlayerStatus();
    
	void registerCallback(IUniDriveFmCallback callback);
	
}