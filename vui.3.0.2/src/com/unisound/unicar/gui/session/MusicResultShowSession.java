/**
 * Copyright (c) 2012-2015 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MusicResultShowSession.java
 * @ProjectName : unicar
 * @PakageName : com.unisound.unicar.gui.session
 * @Author : Hexiaodong
 * @CreateDate : 20150828
 */
package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.TrackInfo;
import com.unisound.unicar.gui.preference.CommandPreference;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.ui.MessageSender;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.MusicResultView;

/**
 * 
 * @author xiaodong
 * @date 20150828
 */
public class MusicResultShowSession extends CommBaseSession {

	public static final String TAG = MusicResultShowSession.class
			.getSimpleName();

	private MusicResultView mMusicResultView = null;
	private String musicName = "";
	private String artist = "";
	/** doreso, xiami */
	private String mType = "";

	public MusicResultShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	}

	@Override
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		Logger.d(TAG, "!--->MusicResultShowSession-putProtocol--mDataObject "
				+ mDataObject);

		if (mDataObject != null) {
			mType = JsonTool.getJsonValue(mDataObject,
					SessionPreference.KEY_TYPE);
			musicName = JsonTool.getJsonValue(mDataObject,
					SessionPreference.KEY_MUSIC_RESULT_SONG);
			artist = JsonTool.getJsonValue(mDataObject,
					SessionPreference.KEY_MUSIC_RESULT_ARTIST);
		}

		Logger.d(TAG, "!--->MusicResultShowSession-putProtocol--mType " + mType
				+ "; musicName = " + musicName + "; artist = " + artist);
		if (SessionPreference.VALUE_MUSIC_RESULT_TYPE_DORESO.equals(mType)) {
			if (mMusicResultView == null) {
				mMusicResultView = new MusicResultView(mContext);
				mMusicResultView.initView(musicName, artist);
			}
			addSessionView(mMusicResultView);
		}
	}

	private void startMusic() {
		MessageSender messageSender = new MessageSender(mContext);
		/*
		 * if (mDataObject != null) { String title =
		 * JsonTool.getJsonValue(mDataObject, "song"); String artist =
		 * JsonTool.getJsonValue(mDataObject, "artist"); String album =
		 * JsonTool.getJsonValue(mDataObject, "album");
		 * 
		 * TrackInfo track = new TrackInfo(); track.setTitle(title);
		 * track.setArtist(artist); track.setAlbum(album); mAnswer =
		 * mContext.getString(R.string.for_find) +
		 * JsonTool.getJsonValue(mDataObject, "keyword"); Bundle bundle = new
		 * Bundle(); bundle.putParcelable("track", track); Intent intent = new
		 * Intent(CommandPreference.ACTION_MUSIC_DATA);
		 * intent.putExtras(bundle); messageSender.sendOrderedMessage(intent,
		 * null); } else { mAnswer = mContext.getString(R.string.open_music);
		 * Intent intent = new Intent(CommandPreference.SERVICECMD);
		 * intent.putExtra(CommandPreference.CMDNAME,
		 * CommandPreference.CMDOPEN_MUSIC); messageSender.sendMessage(intent,
		 * null); }
		 */
		Intent intent = null;
		if (mDataObject != null) {
			String key = JsonTool.getJsonValue(mDataObject, "keyword");

			TrackInfo track = new TrackInfo();
			track.setTitle(musicName);
			track.setArtist(artist);
			mAnswer = mContext.getString(R.string.for_find) + key;
			Cursor cursor = mContext.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,
					MediaStore.Audio.Media.TITLE + " like ? AND " + MediaStore.Audio.Media.ARTIST + " like ?",
					new String[] { "%" + musicName + "%", "%" + artist + "%" },null);
			String path = null;
			if (cursor != null) {
				// 循环遍历cursor
				if(TextUtils.isEmpty(musicName) && TextUtils.isEmpty(artist)){
					int count = cursor.getCount();
					int r=(int) (Math.random()*count);
					if(cursor.moveToPosition(r)){
						path = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
					}
					
				}else{
					if (cursor.moveToNext()) {
						path = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
					}
				}
				cursor.close();
			}
			if (path==null) {
				if(TextUtils.isEmpty(musicName)&&TextUtils.isEmpty(artist)){
					Bundle bundle = new Bundle();
					intent = new Intent(CommandPreference.ACTION_MUSIC_DATA);
					intent.putExtras(bundle);
					intent.putExtra("key", "刘德华");
				}else{
					Bundle bundle = new Bundle();
					bundle.putParcelable("track", track);
					intent = new Intent(CommandPreference.ACTION_MUSIC_DATA);
					intent.putExtra("key", key);
					intent.putExtras(bundle);
				}
			} else {
				/*if(TextUtils.isEmpty(musicName) && TextUtils.isEmpty(artist)){
					intent = new Intent(CommandPreference.ACTION_SERVICE_CMD);
					intent.putExtra(CommandPreference.CMD_KEY_NAME,
							CommandPreference.CMD_NAME_OPEN_MUSIC);
				}else{
					intent = new Intent(CommandPreference.ACTION_MUSIC_LOCAL);
					intent.putExtra("path", path);
				}*/
				intent = new Intent(CommandPreference.ACTION_MUSIC_LOCAL);
				intent.putExtra("path", path);
			}
		} else {
			Cursor cursor = mContext.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
					null, null);
			if (cursor != null) {
				// 循环遍历cursor
				if (cursor.getCount() > 0) {
					mAnswer = mContext.getString(R.string.open_music);
					intent = new Intent(CommandPreference.ACTION_SERVICE_CMD);
					intent.putExtra(CommandPreference.CMD_KEY_NAME,
							CommandPreference.CMD_NAME_OPEN_MUSIC);
				} else {
					Bundle bundle = new Bundle();
					intent = new Intent(CommandPreference.ACTION_MUSIC_DATA);
					intent.putExtras(bundle);
					intent.putExtra("key", "刘德华");
				}
				cursor.close();
			} else {
				Bundle bundle = new Bundle();
				intent = new Intent(CommandPreference.ACTION_MUSIC_DATA);
				intent.putExtras(bundle);
				intent.putExtra("key", "刘德华");
			}
		}
		messageSender.sendOrderedMessage(intent, null);
	}

	@Override
	public void onTTSEnd() {
		Logger.d(TAG, "onTTSEnd");
		super.onTTSEnd();
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);

		startMusic();

		mDataObject = null;
	}
}
