/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : MusicShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
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

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-3
 * @Modified:
 * 2013-9-3: 实现基本功能
 */
public class UIHandleShowSession extends BaseSession {
	public static final String TAG = "UIHandleShowSession";
//	private final String FATVORITE_ROUTE_FORMAT;
	
	private JSONObject dataObject = null;

	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-3
	 * @param context
	 * @param sessionManagerHandler
	 */
	public UIHandleShowSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
	//	FATVORITE_ROUTE_FORMAT = context.getString(R.string.onfind_place);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);
		addQuestionViewText(mQuestion);

		if (SessionPreference.DOMAIN_ROUTE.equals(mOriginType) && SessionPreference.DOMAIN_CODE_FAVORITE_ROUTE.equals(mOriginCode)) {
//			String poiVendor = PrivatePreference.getValue("poi_vendor", UserPreference.MAP_VALUE_AMAP);
//			JSONObject resultObject = JsonTool.getJSONObject(mDataObject, "result");
//			String toPoi = JsonTool.getJsonValue(resultObject, "toPoi");
//			if ("AMAP".equals(poiVendor) || "GAODE".equals(poiVendor)) {
//				mAnswer = mTTS = DataTool.formatString(FATVORITE_ROUTE_FORMAT, mContext.getString(R.string.gaodemap_or_navigation), toPoi);
//			} else if ("BAIDU".equals(poiVendor)) {
//				mAnswer = mTTS = DataTool.formatString(FATVORITE_ROUTE_FORMAT, mContext.getString(R.string.baidumap_or_navigation), toPoi);
//			} else {
//				mAnswer = mTTS = DataTool.formatString(FATVORITE_ROUTE_FORMAT, mContext.getString(R.string.map_or_navigation), toPoi);
//			}
			addAnswerViewText(mAnswer);
		} else {
			String originType = JsonTool.getJsonValue(jsonProtocol, SessionPreference.KEY_ORIGIN_TYPE, "");
			if (originType.equals(SessionPreference.DOMAIN_MUSIC)) {
				dataObject = JsonTool.getJSONObject(jsonProtocol, SessionPreference.KEY_DATA);
//				if (dataObject != null) {
//					String title = JsonTool.getJsonValue(dataObject, "song");
//					String artist = JsonTool.getJsonValue(dataObject, "artist");
//					String album = JsonTool.getJsonValue(dataObject, "album");
//
//					TrackInfo track = new TrackInfo();
//					track.setTitle(title);
//					track.setArtist(artist);
//					track.setAlbum(album);
//					mAnswer = mContext.getString(R.string.for_find) + JsonTool.getJsonValue(dataObject, "keyword");
//					Bundle bundle = new Bundle();
//					bundle.putParcelable("track", track);
//					Intent intent = new Intent(CommandPreference.ACTION_MUSIC_DATA);
//					intent.putExtras(bundle);
//					messageSender.sendOrderedMessage(intent, null);
//				} else {
//					mAnswer = mContext.getString(R.string.open_music);
//					Intent intent = new Intent(CommandPreference.SERVICECMD);
//					intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDOPEN_MUSIC);
//					messageSender.sendMessage(intent, null);
//				}
			}
			addAnswerViewText(mAnswer);
		}
	}

	@Override
	public void onTTSEnd() {
		Logger.d(TAG, "onTTSEnd");
		super.onTTSEnd();
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		MessageSender messageSender = new MessageSender(mContext);
		Intent intent=null;
		if (dataObject != null) {
			String title = JsonTool.getJsonValue(dataObject, "song");
			String artist = JsonTool.getJsonValue(dataObject, "artist");
			String album = JsonTool.getJsonValue(dataObject, "album");
			String key = JsonTool.getJsonValue(dataObject, "keyword");

			TrackInfo track = new TrackInfo();
			track.setTitle(title);
			track.setArtist(artist);
			track.setAlbum(album);
			if (TextUtils.isEmpty(title)) {
				mAnswer = mContext.getString(R.string.for_find)+ artist + "的歌";
			} else {
				mAnswer = mContext.getString(R.string.for_find) + title;
			}
			Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,MediaStore.Audio.Media.TITLE + " like ? AND " + MediaStore.Audio.Media.ARTIST + " like ?", new String[] {"%"+title+"%","%"+ artist +"%"}, null);
			String path=null;
			if (cursor != null) {
				// 循环遍历cursor
				if (cursor.moveToNext()) {
					path = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
				}
				cursor.close();
			}
			if(path==null){
				Bundle bundle = new Bundle();
				bundle.putParcelable("track", track);
				intent = new Intent(CommandPreference.ACTION_MUSIC_DATA);
				intent.putExtra("key", key);
				intent.putExtras(bundle);
			}else{
				intent = new Intent(CommandPreference.ACTION_MUSIC_LOCAL);
				intent.putExtra("path", path);
			}
		} else {
			Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null, null);
			if (cursor != null) {
				// 循环遍历cursor
				if (cursor.getCount()>0) {
					mAnswer = mContext.getString(R.string.open_music);
					intent = new Intent(CommandPreference.SERVICECMD);
					intent.putExtra(CommandPreference.CMDNAME,CommandPreference.CMDOPEN_MUSIC);
				}else{
					Bundle bundle = new Bundle();
					intent = new Intent(CommandPreference.ACTION_MUSIC_DATA);
					intent.putExtras(bundle);
					intent.putExtra("key", "刘德华");
				}
				cursor.close();
			}else{
				Bundle bundle = new Bundle();
				intent = new Intent(CommandPreference.ACTION_MUSIC_DATA);
				intent.putExtras(bundle);
				intent.putExtra("key", "刘德华");
			}
		}
		messageSender.sendOrderedMessage(intent, null);
		dataObject = null;
	}
}
