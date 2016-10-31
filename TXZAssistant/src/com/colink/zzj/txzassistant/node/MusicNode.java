package com.colink.zzj.txzassistant.node;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.android.kwmusic.CommandPreference;
import com.android.kwmusic.KWMusicService;
import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.oem.RomSystemSetting;
import com.colink.zzj.txzassistant.util.APPUtil;
import com.colink.zzj.txzassistant.util.Logger;
import com.colink.zzj.txzassistant.util.StringUtil;
import com.colink.zzj.txzassistant.util.SystemPropertiesProxy;
import com.txznet.sdk.TXZAudioManager;
import com.txznet.sdk.TXZMusicManager;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZMusicManager.MusicModel;
import com.txznet.sdk.TXZMusicManager.MusicTool;
import com.txznet.sdk.TXZMusicManager.MusicToolStatusListener;

/**
 * @desc 音乐节点
 * @auth zzj
 * @date 2016-03-19
 */
public class MusicNode {

	private Context mContext;
	private static MusicNode mInstance;
	
	private static final String AITING_ACTION = "cn.imusic.car.CAR_MUSIC_CONTROL";
	private static final String TYPE_KEY = "command_type";
	private static final String TEXT_KEY = "command_text";
	private static int SEARCH_TYPE = 0X0001;
	private static int PLAY_RANDOM_TYPE = 0X0002;
	private static int PLAY_LOOP_TYPE = 0X0003;
	private static int PREV_TYPE = 0X0004;
	private static int NEXT_TYPE = 0X0005;
	private static int PLAY_TYPE = 0X0006;
	private static int PAUSE_TYPE = 0X0007;

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

	/*
	 * public void setMusicTool(int musicType) { switch (musicType) { case 0:
	 * TXZMusicManager
	 * .getInstance().setMusicTool(MusicToolType.MUSIC_TOOL_KUWO); break; case
	 * 1:
	 * TXZMusicManager.getInstance().setMusicTool(MusicToolType.MUSIC_TOOL_KAOLA
	 * ); break; default: break; } }
	 */

	private MusicTool mMusicTool = new MusicTool() {

		@Override
		public void pause() {
			Logger.d("pause");
			if (TXZAudioManager.getInstance().isPlaying()) {
				Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
				intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
						KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE));
				mContext.sendBroadcast(intent);

			}
			if(APPUtil.getInstance().isInstalled(APPUtil.KW_PKG)){
				Intent intent = new Intent(mContext, KWMusicService.class);
				intent.setAction(CommandPreference.PAUSE_ACTION);
				mContext.startService(intent);
			}else{
				Intent intent = new Intent(AITING_ACTION);
				intent.putExtra(TYPE_KEY, PAUSE_TYPE);
				mContext.sendBroadcast(intent);
				
			}
		}

		@Override
		public void unfavourMusic() {
			// DebugUtil.showTips("取消收藏当前音乐");
			TXZResourceManager.getInstance().speakTextOnRecordWin(
					"不支持取消收藏当前音乐", false, null);
		}

		@Override
		public void switchSong() {
			// DebugUtil.showTips("切换音乐");
		}

		@Override
		public void switchModeRandom() {
			if(APPUtil.getInstance().isInstalled(APPUtil.KW_PKG)){
				Intent intent = new Intent(mContext, KWMusicService.class);
				intent.setAction(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME,
						CommandPreference.CMDSHUFFLE_PLAYBACK);
				mContext.startService(intent);
			}else{
				Intent intent = new Intent(AITING_ACTION);
				intent.putExtra(TYPE_KEY, PLAY_RANDOM_TYPE);
				mContext.sendBroadcast(intent);
			}
		}

		@Override
		public void switchModeLoopOne() {
			if(APPUtil.getInstance().isInstalled(APPUtil.KW_PKG)){
				Intent intent = new Intent(mContext, KWMusicService.class);
				intent.setAction(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME,
						CommandPreference.CMDSINGLE_CYCLE);
				mContext.startService(intent);
			}else{
				Intent intent = new Intent(AITING_ACTION);
				intent.putExtra(TYPE_KEY, PLAY_LOOP_TYPE);
				mContext.sendBroadcast(intent);
			}
		}

		@Override
		public void switchModeLoopAll() {
			if(APPUtil.getInstance().isInstalled(APPUtil.KW_PKG)){
				Intent intent = new Intent(mContext, KWMusicService.class);
				intent.setAction(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME,CommandPreference.CMDFULL_CYCLE);
				mContext.startService(intent);
			}else{
				Intent intent = new Intent(AITING_ACTION);
				intent.putExtra(TYPE_KEY, PLAY_LOOP_TYPE);
				mContext.sendBroadcast(intent);
			}
		}

		@Override
		public void setStatusListener(MusicToolStatusListener listener) {
			// TODO 状态监听器，如果实现自己的音乐工具，请将监听器记录下来，再对应状态变化时，使用该监听器来通知同行者
		}

		@Override
		public void prev() {
			Logger.d("prev");
			if(APPUtil.getInstance().isInstalled(APPUtil.KW_PKG)){
				Intent intent = new Intent(mContext, KWMusicService.class);
				intent.setAction(CommandPreference.PREVIOUS_ACTION);
				mContext.startService(intent);
			}else{
				Intent intent = new Intent(AITING_ACTION);
				intent.putExtra(TYPE_KEY, PREV_TYPE);
				mContext.sendBroadcast(intent);
			}
		}

		@Override
		public void playRandom() {
			if(APPUtil.getInstance().isInstalled(APPUtil.KW_PKG)){
				Intent intent = new Intent(mContext, KWMusicService.class);
				intent.setAction(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME,
						CommandPreference.CMDOPEN_MUSIC);
				mContext.startService(intent);
			}else{
				Intent intent = new Intent(AITING_ACTION);
				intent.putExtra(TYPE_KEY, PLAY_RANDOM_TYPE);
				mContext.sendBroadcast(intent);
			}
		}

		@Override
		public void playMusic(MusicModel musicModel) {

			// String album = musicModel.getAlbum();
			// String[] keywords = musicModel.getKeywords();

			String title = musicModel.getTitle();
			String artist = StringUtil.convertArrayToString(musicModel.getArtist());
			if(APPUtil.getInstance().isInstalled(APPUtil.KW_PKG)){
				String customer = SystemPropertiesProxy.get(mContext,"ro.inet.consumer.code");
				if (CommandPreference.XUANYUAN_CUSTOMER.equals(customer)) {
					Intent intent = new Intent(mContext, KWMusicService.class);
					intent.setAction(CommandPreference.ACTION_MUSIC_DATA_RAW);
					intent.putExtra(CommandPreference.TITLE_KEY, title);
					intent.putExtra(CommandPreference.ARTIST_KEY, artist);
					mContext.startService(intent);
				} else {
					if (title == null) {
						title = "";
					}
					String keyword = artist + title;
					Logger.i("keyword = " + keyword);
					Cursor cursor = mContext.getContentResolver().query(
							MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,
							MediaStore.Audio.Media.TITLE + " like ? AND " + MediaStore.Audio.Media.ARTIST + " like ?",
							new String[] { "%" + title + "%", "%" + artist + "%" },null);
					String path = null;
					if (cursor != null) {
						int count = cursor.getCount();
						if (count == 0) {
							if (TextUtils.isEmpty(keyword)) {
								Intent intent = new Intent(mContext,KWMusicService.class);
								intent.setAction(CommandPreference.ACTION_MUSIC_DATA);
								intent.putExtra(CommandPreference.MUSIC_KEY, "刘德华");
								mContext.startService(intent);
							} else {
								Intent intent = new Intent(mContext,KWMusicService.class);
								intent.setAction(CommandPreference.ACTION_MUSIC_DATA);
								intent.putExtra(CommandPreference.MUSIC_KEY,
										keyword);
								mContext.startService(intent);
							}
						} else {
							// 循环遍历cursor
							if (TextUtils.isEmpty(title)
									&& TextUtils.isEmpty(artist)) {
								int r = (int) (Math.random() * count);
								if (cursor.moveToPosition(r)) {
									path = cursor
											.getString((cursor
													.getColumnIndex(MediaStore.Audio.Media.DATA)));
									Intent intent = new Intent(mContext,
											KWMusicService.class);
									intent.setAction(CommandPreference.ACTION_MUSIC_LOCAL);
									intent.putExtra(CommandPreference.MUSIC_PATH,
											path);
									mContext.startService(intent);
								}
	
							} else {
								if (cursor.moveToNext()) {
									path = cursor
											.getString((cursor
													.getColumnIndex(MediaStore.Audio.Media.DATA)));
									Intent intent = new Intent(mContext,
											KWMusicService.class);
									intent.setAction(CommandPreference.ACTION_MUSIC_LOCAL);
									intent.putExtra(CommandPreference.MUSIC_PATH,
											path);
									mContext.startService(intent);
								}
							}
						}
						cursor.close();
					} else {
						if (TextUtils.isEmpty(keyword.trim())) {
							Intent intent = new Intent(mContext,KWMusicService.class);
							intent.setAction(CommandPreference.ACTION_MUSIC_DATA);
							mContext.startService(intent);
						} else {
							Intent intent = new Intent(mContext,KWMusicService.class);
							intent.setAction(CommandPreference.ACTION_MUSIC_DATA);
							intent.putExtra("key", keyword);
							mContext.startService(intent);
						}
					}
				}
			}else{
				if (title == null) {
					title = "";
				}
				String keyword = artist + title;
				Intent intent = new Intent(AITING_ACTION);
				intent.putExtra(TYPE_KEY, SEARCH_TYPE);
				intent.putExtra(TEXT_KEY, keyword);
				mContext.sendBroadcast(intent);
			}

		}

		@Override
		public void playFavourMusic() {
			Logger.d("playFavourMusic");
			TXZResourceManager.getInstance().speakTextOnRecordWin("不支持播放收藏歌曲",false, null);

		}

		@Override
		public void play() {
			if(APPUtil.getInstance().isInstalled(APPUtil.KW_PKG)){
				Intent intent = new Intent(mContext, KWMusicService.class);
				intent.setAction(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME,CommandPreference.CMDOPEN_MUSIC);
				mContext.startService(intent);
			}else{
				Intent intent = new Intent(AITING_ACTION);
				intent.putExtra(TYPE_KEY, PLAY_TYPE);
				mContext.sendBroadcast(intent);
			}
		}

		@Override
		public void next() {
			if(APPUtil.getInstance().isInstalled(APPUtil.KW_PKG)){
				Intent intent = new Intent(mContext, KWMusicService.class);
				intent.setAction(CommandPreference.NEXT_ACTION);
				mContext.startService(intent);
			}else{
				Intent intent = new Intent(AITING_ACTION);
				intent.putExtra(TYPE_KEY, NEXT_TYPE);
				mContext.sendBroadcast(intent);
			}
		}

		@Override
		public boolean isPlaying() {
			// TODO 返回真实的播放器状态
			return false;
		}

		@Override
		public MusicModel getCurrentMusicModel() {
			// TODO 返回真实的播放的歌曲信息
			MusicModel currentMusicModel = TXZMusicManager.getInstance()
					.getCurrentMusicModel();
			Logger.d(currentMusicModel.getTitle() + ","
					+ currentMusicModel.getArtist());
			return currentMusicModel;
		}

		@Override
		public void favourMusic() {
			Logger.d("favourMusic");
			// DebugUtil.showTips("收藏当前音乐");
			TXZResourceManager.getInstance().speakTextOnRecordWin("不支持收藏当前音乐",false, null);

		}

		@Override
		public void exit() {
			Logger.d("exit");
			Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
			intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_STOP));
			mContext.sendBroadcast(intent);
			
			 Intent i = new Intent(mContext,KWMusicService.class);
			 i.setAction(CommandPreference.SERVICECMD);
			 i.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDSTOP);
			 mContext.startService(i);
			

			try {
				RomSystemSetting.forceStopPackage(APPUtil.KW_PKG);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				RomSystemSetting.forceStopPackage(APPUtil.IMUSIC_PKG);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
