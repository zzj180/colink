package com.colink.zzj.txzassistant.node;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.android.kwmusic.CommandPreference;
import com.android.kwmusic.KWMusicService;
import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.util.Logger;
import com.colink.zzj.txzassistant.util.StringUtil;
import com.txznet.sdk.TXZMusicManager;
import com.txznet.sdk.TXZMusicManager.MusicModel;
import com.txznet.sdk.TXZMusicManager.MusicTool;
import com.txznet.sdk.TXZMusicManager.MusicToolStatusListener;
import com.txznet.sdk.TXZMusicManager.MusicToolType;

/**
 * @desc  音乐节点
 * @auth zzj
 * @date 2016-03-19
 */
public class MusicNode{

    private Context mContext;
    private static MusicNode mInstance;
    private MusicNode() {
        this.mContext = AdapterApplication.getContext();
    }

	public void init() {
		TXZMusicManager.getInstance().setMusicTool(mMusicTool);
	}

    public static synchronized MusicNode getInstance() {
        if (mInstance == null) {
            mInstance = new MusicNode();
        }
        return mInstance;
    }
    
    public void setMusicTool(int musicType){
    	switch (musicType) {
		case 0:
			TXZMusicManager.getInstance().setMusicTool(
					MusicToolType.MUSIC_TOOL_KUWO);
			break;
		case 1:
			TXZMusicManager.getInstance().setMusicTool(
					MusicToolType.MUSIC_TOOL_KAOLA);
			break;
		default:
			break;
		}
    }
    
    private MusicTool mMusicTool = new MusicTool() {

		@Override
		public void unfavourMusic() {
//			DebugUtil.showTips("取消收藏当前音乐");
		}

		@Override
		public void switchSong() {
//			DebugUtil.showTips("切换音乐");
		}

		@Override
		public void switchModeRandom() {
			Intent intent = new Intent(mContext,KWMusicService.class);
			intent.setAction(CommandPreference.SERVICECMD);
			intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDSHUFFLE_PLAYBACK);
			mContext.startService(intent);
		}

		@Override
		public void switchModeLoopOne() {
			Intent intent = new Intent(mContext,KWMusicService.class);
			intent.setAction(CommandPreference.SERVICECMD);
			intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDSINGLE_CYCLE);
			mContext.startService(intent);
		}

		@Override
		public void switchModeLoopAll() {
			Intent intent = new Intent(mContext,KWMusicService.class);
			intent.setAction(CommandPreference.SERVICECMD);
			intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDFULL_CYCLE);
			mContext.startService(intent);
		}

		@Override
		public void setStatusListener(MusicToolStatusListener listener) {
			// TODO 状态监听器，如果实现自己的音乐工具，请将监听器记录下来，再对应状态变化时，使用该监听器来通知同行者
		}

		@Override
		public void prev() {
			Intent intent = new Intent(mContext,KWMusicService.class);
			intent.setAction(CommandPreference.PREVIOUS_ACTION);
			mContext.startService(intent);
		}

		@Override
		public void playRandom() {
			Intent intent = new Intent(mContext,KWMusicService.class);
			intent.setAction(CommandPreference.SERVICECMD);
			intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDOPEN_MUSIC);
			mContext.startService(intent);
		}

		@Override
		public void playMusic(MusicModel musicModel) {
			
			//		String album = musicModel.getAlbum();
			//		String[] keywords = musicModel.getKeywords();
			String title = musicModel.getTitle();
			if(title ==null){
				title = "";
			}
			String artist = StringUtil.convertArrayToString(musicModel.getArtist());
			String keyword =artist + title;
			Logger.i("keyword = " + keyword);
			Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,
					MediaStore.Audio.Media.TITLE + " like ? AND " + MediaStore.Audio.Media.ARTIST + " like ?",
					new String[] { "%" + title + "%", "%" + artist + "%" },null);
			String path = null;
			if (cursor != null) {
				int count = cursor.getCount();
				if(count == 0){
					if(TextUtils.isEmpty(keyword)){
						Intent intent = new Intent(mContext,KWMusicService.class);
						intent.setAction(CommandPreference.ACTION_MUSIC_DATA);
						intent.putExtra(CommandPreference.MUSIC_KEY, "刘德华");
						Logger.i("0 = 刘德华");
						mContext.startService(intent);
					}else{
						Intent intent = new Intent(mContext,KWMusicService.class);
						intent.setAction(CommandPreference.ACTION_MUSIC_DATA);
						intent.putExtra(CommandPreference.MUSIC_KEY, keyword);
						Logger.i("0 = " + keyword);
						mContext.startService(intent);
					}
				}else{
					// 循环遍历cursor
					if(TextUtils.isEmpty(title) && TextUtils.isEmpty(artist)){
						int r=(int) (Math.random()*count);
						if(cursor.moveToPosition(r)){
							path = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
							Intent intent = new Intent(mContext,KWMusicService.class);
							intent.setAction(CommandPreference.ACTION_MUSIC_LOCAL);
							intent.putExtra(CommandPreference.MUSIC_PATH, path);
							Logger.i("random = " + path);
							mContext.startService(intent);
						}
						
					}else{
						if (cursor.moveToNext()) {
							path = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
							Intent intent = new Intent(mContext,KWMusicService.class);
							intent.setAction(CommandPreference.ACTION_MUSIC_LOCAL);
							intent.putExtra(CommandPreference.MUSIC_PATH, path);
							Logger.i("path = " + path);
							mContext.startService(intent);
						}
					}
				}
				cursor.close();
			}else{
				if(TextUtils.isEmpty(keyword.trim())){
					Intent intent = new Intent(mContext,KWMusicService.class);
					intent.setAction(CommandPreference.ACTION_MUSIC_DATA);
					intent.putExtra("key", "刘德华");
					mContext.startService(intent);
				}else{
					Intent intent = new Intent(mContext,KWMusicService.class);
					intent.setAction(CommandPreference.ACTION_MUSIC_DATA);
					intent.putExtra("key", keyword);
					mContext.startService(intent);
				}
			}
		}

		@Override
		public void playFavourMusic() {
//			DebugUtil.showTips("播放收藏歌曲");
		}

		@Override
		public void play() {
			Intent intent = new Intent(mContext,KWMusicService.class);
			intent.setAction(CommandPreference.SERVICECMD);
			intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDPLAY);
			mContext.startService(intent);
		}

		@Override
		public void pause() {
			Intent intent = new Intent(mContext,KWMusicService.class);
			intent.setAction(CommandPreference.PAUSE_ACTION);
			mContext.startService(intent);
		}

		@Override
		public void next() {
			Intent intent = new Intent(mContext,KWMusicService.class);
			intent.setAction(CommandPreference.NEXT_ACTION);
			mContext.startService(intent);
		}

		@Override
		public boolean isPlaying() {
			// TODO 返回真实的播放器状态
			return false;
		}

		@Override
		public MusicModel getCurrentMusicModel() {
			// TODO 返回真实的播放的歌曲信息
			return null;
		}

		@Override
		public void favourMusic() {
//			DebugUtil.showTips("收藏当前音乐");
		}

		@Override
		public void exit() {
			Intent intent = new Intent(mContext,KWMusicService.class);
			intent.setAction(CommandPreference.SERVICECMD);
			intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDSTOP);
			mContext.startService(intent);
		}
	};
}
