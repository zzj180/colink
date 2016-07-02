package com.colink.zzj.txzassistant.node;

import org.json.JSONException;
import org.json.JSONObject;

import com.colink.zzj.txzassistant.util.Logger;
import com.colink.zzj.txzassistant.util.StringUtil;
import com.txznet.sdk.TXZAudioManager;
import com.txznet.sdk.TXZAudioManager.AudioTool;
import com.txznet.sdk.TXZAudioManager.AudioToolStatusListener;
import com.txznet.sdk.TXZAudioManager.IAudioTool;

/**
 * @desc 音乐节点
 * @auth zzj
 * @date 2016-03-19
 */
public class AudioNode {

	private static AudioNode mInstance;

	private AudioNode() {
	}

	public void init() {
		TXZAudioManager.getInstance().setDefaultAudioTool(AudioTool.AUDIO_XMLY);
	//	TXZAudioManager.getInstance().setAudioTool(tool);
	}

	public static synchronized AudioNode getInstance() {
		if (mInstance == null) {
			mInstance = new AudioNode();
		}
		return mInstance;
	}

	private IAudioTool tool = new IAudioTool() {

		@Override
		public void start() {
			Logger.d("xmly start");
		}

		@Override
		public void setAudioStatusListener(AudioToolStatusListener arg0) {
			Logger.d("xmly setAudioStatusListener");
		}

		@Override
		public void playFm(String arg0) {
			// TODO Auto-generated method stub
			Logger.d("xmly playFm=" + arg0);
			/*try {
				JSONObject json = new JSONObject(arg0);
				String artist = StringUtil.clearSpecial(json.getString("artists"));
				String category = StringUtil.clearSpecial(json.getString("category"));
				String keyword = StringUtil.clearSpecial(json.getString("keywords"));
				Intent intent = new Intent(mContext, XmFmGuiService.class);
				intent.setAction(XmFmConfig.ACTION_START_XM_FM);
				intent.putExtra(XmFmConfig.KEY_EXTRA_FM_CATEGORY, category);
				intent.putExtra(XmFmConfig.KEY_EXTRA_FM_ARTIST, artist);
				intent.putExtra(XmFmConfig.KEY_EXTRA_FM_KEYWORD, keyword);

				mContext.startService(intent);
			} catch (JSONException e) {
				e.printStackTrace();
			}*/
			
		}

		@Override
		public void pause() {
			Logger.d("xmly pause");
			TXZAudioManager.getInstance().pause();
		}

		@Override
		public void exit() {
			Logger.d("xmly exit");
			TXZAudioManager.getInstance().exit();
		}
		
		@Override
		public void next() {
			Logger.d("xmly next");
			TXZAudioManager.getInstance().next();
		}
		
		@Override
		public void prev() {
			Logger.d("xmly prev");
		}
	};

}
