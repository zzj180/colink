package com.colink.zzj.txzassistant;

import java.util.ArrayList;

import com.colink.zzj.txzassistant.util.Logger;
import com.txznet.sdk.TXZCallManager;
import com.txznet.sdk.TXZCallManager.Contact;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class PhoneBookService extends IntentService{
	
	public PhoneBookService() {
		this("PhoneBookService");
	}
	
	public PhoneBookService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		
		ArrayList<Contact> list = getContact();
		if(list.size()>0)
		TXZCallManager.getInstance().syncContacts(list);
	}
	
	 public ArrayList<Contact> getContact() {
	        ArrayList<Contact> listMembers = new ArrayList<Contact>();
	        Cursor cursor = null;
	        try {
	          
	            Uri uri = Uri.parse("content://com.android.ecar.provider.contacts/Contacts");
	            // 这里是获取联系人表的电话里的信息  包括：名字，名字拼音，联系人id,电话号码；
	            // 然后在根据"sort-key"排序 
	            cursor = getContentResolver().query(
	                    uri,new String[] {"name", "number"}, null, null, null);

	            if (cursor!= null && cursor.moveToFirst()) {
	                do {
	                	Contact contact = new Contact();
	                    String name = cursor.getString(cursor.getColumnIndex("name"));
	                    String contact_phone = cursor.getString(cursor.getColumnIndex("number"));
	                    contact.setName(name);
	                    contact.setNumber(contact_phone);
	                    if (name != null)
	                        listMembers.add(contact);
	                } while (cursor.moveToNext());
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if(cursor!=null) cursor.close();
	        }
	        return listMembers;
	    }

}
