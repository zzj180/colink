package com.aispeech.aios.bridge;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.aispeech.aios.bridge.utils.Logger;
import com.aispeech.aios.sdk.bean.Contact;
import com.aispeech.aios.sdk.manager.AIOSPhoneManager;

import java.util.ArrayList;

public class PhoneBookService extends IntentService {

	public PhoneBookService() {
		this("PhoneBookService");
	}

	public PhoneBookService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		Uri uri = arg0.getParcelableExtra("uri");
		ArrayList<Contact> list = getContact(uri);
		Logger.d(list.toString());

		AIOSPhoneManager.getInstance().syncContacts(list); //将联系人同步给AIOS

	}

	public ArrayList<Contact> getContact(Uri uri) {
		ArrayList<Contact> listMembers = new ArrayList<Contact>();
		Cursor cursor = null;
		try {

			// 这里是获取联系人表的电话里的信息 包括：名字，名字拼音，联系人id,电话号码；
			// 然后在根据"sort-key"排序
			if (ContactsContract.CommonDataKinds.Phone.CONTENT_URI.equals(uri)) {
				cursor = getContentResolver().query(uri,
						new String[] { "display_name", "data1" }, null, null,
						null);
				if (cursor.moveToFirst()) {
					do {
						Contact contact = new Contact();
						String contact_phone = cursor
								.getString(cursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						String name = cursor.getString(0);
						contact.setName(name);

						 Contact.PhoneInfo cp = new Contact.PhoneInfo();
						cp.setNumber(contact_phone);
						contact.addPhoneInfo(cp);
						if (name != null)
							listMembers.add(contact);
					} while (cursor.moveToNext());
				}
			} else {
				cursor = getContentResolver().query(uri,
						new String[] { "name", "number" }, null, null, null);

				if (cursor != null && cursor.moveToFirst()) {
					do {
						Contact contact = new Contact();
						String name = cursor.getString(cursor
								.getColumnIndex("name"));
						String contact_phone = cursor.getString(cursor
								.getColumnIndex("number"));
						contact.setName(name);
						Contact.PhoneInfo cp = new Contact.PhoneInfo();
						cp.setNumber(contact_phone);
						contact.addPhoneInfo(cp);
						if (name != null)
							listMembers.add(contact);
						if (name != null)
							listMembers.add(contact);
					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return listMembers;
	}

}
