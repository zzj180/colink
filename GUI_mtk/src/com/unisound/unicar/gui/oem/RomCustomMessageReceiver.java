package com.unisound.unicar.gui.oem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.unisound.unicar.gui.utils.Logger;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class RomCustomMessageReceiver extends BroadcastReceiver {
	private static final String TAG = "RomCustomMessageReceiver";
	public static final String INTENT_BASE_ACTION = "cn.yunzhisheng.intent.action.";

	// 同步联系人
	public static final String CATION_CUSTOM_UPDATE = INTENT_BASE_ACTION + "custom.order.contact";

	private Context mContext;

	private Thread mThread = null;

	private ArrayList<String> contactsInfo = new ArrayList<String>();
	private static final int CONTACT_SYNC = 1; // 同步所有联系人
	private static final int CONTACT_DELETE_ONE = 2; // 删除一个联系人
	private static final int CONTACT_DELETE_ALL = 3; // 删除全部联系人

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		String action = intent.getAction();
		if (action.equals(CATION_CUSTOM_UPDATE)) {// 添加蓝牙电话传输过来的电话到数据库
			final ArrayList<String> contactList = intent
					.getStringArrayListExtra("contactList");

			int flag = intent.getIntExtra("flag", 1);
			Logger.d(TAG, "CATION_CUSTOM_UPDATE flag:" + flag);
			switch (flag) {
			case CONTACT_SYNC:
				if (contactList != null && contactList.size() > 1) {
					contactsInfo.clear();
					contactsInfo.addAll(contactList);
					if (contactsInfo != null && contactsInfo.size() > 0) {
						insertContactInfosInNewThread(contactsInfo);
					}
				}
				break;
			case CONTACT_DELETE_ONE:
				if (contactList != null && contactList.size() == 1) {
					String[] names = contactList.get(0).split("#");
					if (names != null && names.length == 2) {
						String name = names[0];
						String number = names[1];
						deleteContact(name, number);
					}

					String name = names[0];
					String number = names[1];
					deleteContact(name, number);
				}
				break;
			case CONTACT_DELETE_ALL:
				resetContact();
				resetPhone();
				break;
			}
		}
	}

	/**
	 * 清空数据库中的联系人表
	 */
	private void resetContact() {
		Logger.d(TAG, "resetContact");
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri
				.parse("content://cn.yunzhisheng.vui.provider.ContactProvider/contact");
		resolver.delete(uri, null, null);
	}

	/**
	 * 清空数据库中的电话表
	 */
	private void resetPhone() {
		Logger.d(TAG, "resetPhone");
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri
				.parse("content://cn.yunzhisheng.vui.provider.ContactProvider/phone");
		resolver.delete(uri, null, null);
	}

	/**
	 * 添加联系人到数据库
	 */
	private void insertContact(String name, String number) {
//		Logger.d(TAG, "name: " + name + " number: " + number);
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri
				.parse("content://cn.yunzhisheng.vui.provider.ContactProvider/contact");
		ContentValues values = new ContentValues();
		values.put("display_name", name);
		values.put("has_phone_number", 1);
		Uri resultUri = resolver.insert(uri, values);
		if (resultUri != null) {
			List<String> segments = resultUri.getPathSegments();
			if (segments != null && segments.size() > 0) {
				String contactId = segments.get(1);
				insertPhone(Long.parseLong(contactId), number);
			}
		}
	}

	// 添加电话到数据库的电话表
	private void insertPhone(long contactId, String number) {
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri
				.parse("content://cn.yunzhisheng.vui.provider.ContactProvider/phone");
		ContentValues values = new ContentValues();
		values.put("contact_id", contactId);
		values.put("raw_number", number);
		values.put("number", number);
		resolver.insert(uri, values);
	}

	private void deleteContact(String name, String number) {

		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri
				.parse("content://cn.yunzhisheng.vui.provider.ContactProvider/contact");
		String[] contactProjection = { "_id" };
		Cursor cursor = resolver.query(uri, contactProjection,
				"display_name=?", new String[] { name }, null);
		if (cursor != null) {
			List<Long> contactdIds = new ArrayList<Long>();
			if (cursor.moveToFirst()) {
				do {
					long contact_id = cursor.getLong(0);
					contactdIds.add(contact_id);
//					Logger.d(TAG, " contact_id:" + contact_id);
				} while (cursor.moveToNext());
			}
			cursor.close();
			cursor = null;
			if (contactdIds.size() > 0) {
				for (long id : contactdIds) {
					if (deletePhone(id, number)) {
						resolver.delete(uri, "_id=?", new String[] { id + "" });
						Logger.d(TAG, CATION_CUSTOM_UPDATE + " deleted");
					}
				}
			}
		}
	}

	private boolean deletePhone(long id, String number) {
		ContentResolver resolver = mContext.getContentResolver();
		Uri phoneUri = Uri
				.parse("content://cn.yunzhisheng.vui.provider.ContactProvider/phone");
		String[] contactProjection = { "contact_id", "number" };
		Cursor cursor = resolver.query(phoneUri, contactProjection,
				"contact_id=?", new String[] { id + "" }, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					String phoneNumber = cursor.getString(1);
					if (phoneNumber != null && phoneNumber.equals(number)) {
						resolver.delete(phoneUri, "contact_id=?",
								new String[] { id + "" });
						cursor.close();
						cursor = null;
						return true;
					}
				} while (cursor.moveToNext());
			}
			cursor.close();
			cursor = null;
		}
		return false;
	}

	private void insertContactInfosInNewThread(final ArrayList<String> contacts) {
		Logger.d(TAG, "insertContactInfosInNewThread");
		if (mThread == null) {
			mThread = new Thread(new Runnable() {
				@Override
				public void run() {
					if (contacts != null && contacts.size() > 1) {
						resetContact();
						resetPhone();
						for (int i = 0; i < contacts.size(); i++) {
							String[] info = contacts.get(i).split("#");
							Logger.d(TAG, "info : " + Arrays.asList(info));
							if (info != null && info.length == 2) {
								insertContact(info[0], info[1]);
							}
						}
					}
					Logger.d(TAG, CATION_CUSTOM_UPDATE + " sync end");
					mThread = null;
				}
			});
		}
		if (!mThread.isAlive()) {
			Logger.d(TAG, CATION_CUSTOM_UPDATE + " start sync");
			mThread.start();
		}
	}
}
