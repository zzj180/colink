package com.aispeech.aios.adapter.receiver;


import com.aispeech.aios.adapter.service.PhoneBookService;
import com.aispeech.aios.adapter.util.TTSController;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PhoneBookReceive extends BroadcastReceiver {

	private final static String PHONEBOOK_ACTION = "com.colink.zzj.contact.done";
	private static final String PLAY_TTS = "com.wanma.action.PLAY_TTS";
	private static final String GL_PLAY_TTS = "com.glsx.tts.speaktext";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (PHONEBOOK_ACTION.equals(action)) {
			try {
				context.startService(new Intent(context, PhoneBookService.class));
			} catch (Exception e) {
			}
		} else if (PLAY_TTS.equals(action)) {
			String playText = intent.getStringExtra("content");
			int id = intent.getIntExtra("id", -1);
			TTSController.getInstance(context).playText(playText, id);
		} else if (GL_PLAY_TTS.equals(action)) {
			String playText = intent.getStringExtra("ttsText");
			TTSController.getInstance(context).playText(playText);
		}
	}

}
