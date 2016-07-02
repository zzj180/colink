package com.colink.service;

import java.util.ArrayList;
import java.util.List;

import com.colink.util.Constact;
import com.colink.util.Contact;

import android.app.IntentService;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts.Data;

public class PhoneBookService extends IntentService {

	public PhoneBookService() {
		this("PhoneBookService");
	}

	public PhoneBookService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			List<Contact> contacts = (List<Contact>) intent.getSerializableExtra(Constact.CONTACTS_KEY);
			boolean isover = intent.getBooleanExtra(Constact.ISOVER_KEY, false);
			testInsertBatch(contacts,isover);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testInsertBatch(List<Contact> cs,boolean isover) {
		ContentProviderClient client = getContentResolver()
				.acquireContentProviderClient(ContactsContract.AUTHORITY);
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		try {
			int rawContactInsertIndex = 0;
			for (Contact contact : cs) {
				rawContactInsertIndex = ops.size();
				ops.add(ContentProviderOperation
						.newInsert(RawContacts.CONTENT_URI)
						.withValue(RawContacts.ACCOUNT_TYPE, null)
						.withValue(RawContacts.ACCOUNT_NAME, null)
						.withYieldAllowed(true).build());

				// 添加姓名
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID,
								rawContactInsertIndex)
						.withValue(Data.MIMETYPE,
								StructuredName.CONTENT_ITEM_TYPE)
						.withValue(StructuredName.DISPLAY_NAME,
								contact.getName()).withYieldAllowed(true)
						.build());
				// 添加号码
				ops.add(ContentProviderOperation
						.newInsert(
								android.provider.ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID,
								rawContactInsertIndex)
						.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
						.withValue(Phone.NUMBER, contact.getPhone())
						.withValue(Phone.TYPE, Phone.TYPE_MOBILE)
						.withYieldAllowed(true).build());
			}
			client.applyBatch(ops);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.release();
			}
			if (isover) {
				TelphoneService.isDownPhone = false;
				Intent contactDone = new Intent(Constact.ACTION_CONTACT_DONE);
				sendBroadcast(contactDone);
				Intent intent = new Intent(Constact.PLAY_TTS);
				intent.putExtra(Constact.TTS_KEY, Constact.OVERDOWN);
				sendBroadcast(intent);
			}
		}

		// 在事务中对多个操作批量执行
		// resolver.applyBatch(ContactsContract.AUTHORITY, ops);
	}
}
