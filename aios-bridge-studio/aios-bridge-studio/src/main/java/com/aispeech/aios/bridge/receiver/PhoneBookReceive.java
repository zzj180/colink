package com.aispeech.aios.bridge.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.aispeech.aios.bridge.BridgeApplication;
import com.aispeech.aios.bridge.PhoneBookService;
import com.aispeech.aios.bridge.utils.Logger;
import com.aispeech.aios.sdk.manager.AIOSPhoneManager;
import com.aispeech.aios.sdk.manager.AIOSTTSManager;

public class PhoneBookReceive extends BroadcastReceiver {

	private final static String PHONEBOOK_ACTION = "com.colink.zzj.contact.done";
	private static final String PLAY_TTS = "com.wanma.action.PLAY_TTS";
	private static final String GL_PLAY_TTS = "com.glsx.tts.speaktext";

	private static final String ACTION_ACC_ON = "android.intent.action.ACC_ON_KEYEVENT";
	private static final String ACTION_ACC_OFF = "android.intent.action.ACC_OFF_KEYEVENT";

	private static final String ACTION_ECAR_NAVI = "com.android.ecar.send";
	private static final String ACTION_ECAR_CALL_END = "com.ecar.call.idle";

	private static final String ACTION_PHONE_STATE = "android.intent.action.BLUETOOTH_PHONE_STATE";

	public static String _CMD_ = "ecarSendKey";

	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		Logger.e(action);
		if (PHONEBOOK_ACTION.equals(action)) {
			try {
				Intent service = new Intent(context,PhoneBookService.class);
				service.putExtra("uri", ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
				context.startService(service);
			} catch (Exception e) {
			}
		} else if (PLAY_TTS.equals(action)) {
			if(!BridgeApplication.isMute) {
				String playText = intent.getStringExtra("content");
				AIOSTTSManager.speak(playText);
			}
		} else if (GL_PLAY_TTS.equals(action)) {
			if(!BridgeApplication.isMute) {
				String playText = intent.getStringExtra("ttsText");
				AIOSTTSManager.speak(playText);
			}
		} else if (ACTION_ACC_ON.equals(action)) {

		} else if (ACTION_ACC_OFF.equals(action)) {

		} else if (ACTION_ECAR_NAVI.equals(action)) {
			String cmd = intent.getStringExtra(_CMD_);
			Log.d("TTSReceive", "cmd = " + cmd);
			 if ("UpdateContacts".equals(cmd)) {
				try {
					Intent service = new Intent(context,PhoneBookService.class);
					service.putExtra("uri", Uri.parse("content://com.android.ecar.provider.contacts/Contacts"));
					context.startService(service);
				} catch (Exception e) {
				}
			}
		} else if (intent.getAction().equals(ACTION_PHONE_STATE)) {// total

			// 收到蓝牙来电铃声响起消息，通知aios

			int state = intent.getIntExtra("state",TelephonyManager.CALL_STATE_RINGING);
			// added by ydj on 2016.4.23
			// String name = intent.getExtras().getString("name");
			String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			String name = getContactNameByNumber(context,number);
			// added by ydj on 2016.4.23

			switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					if (TextUtils.isEmpty(name)) {
						if (TextUtils.isEmpty(number)) {
							name = "未知号码";
						} else {
							name = number;
						}
					}
					AIOSPhoneManager.getInstance().incomingCallRing(name, number);
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					AIOSPhoneManager.getInstance().callOffhook();
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					AIOSPhoneManager.getInstance().callEnd();
					break;
				default:
					break;
			}// total else if
		}else if(ACTION_ECAR_CALL_END.equals(action)){
			AIOSPhoneManager.getInstance().callEnd();
		}
	}

	private String getContactNameByNumber(Context context,String number) {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri,
				new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME }, ContactsContract.CommonDataKinds.Phone.NUMBER + "="
						+ number, null, null);
		String name = null;
		if (cursor != null && cursor.moveToFirst()) {
			name = cursor.getString(0);
		}
		if (cursor != null)
			cursor.close();
		return name;
	}

}
