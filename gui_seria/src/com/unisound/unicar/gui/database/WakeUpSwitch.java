package com.unisound.unicar.gui.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class WakeUpSwitch extends ContentProvider {
	private SQLiteOpenHelper mOpenHelper;
	private static final UriMatcher sURLMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	public static final String HOST = "com.coogo.inet.vui.assistant.car";
	public static final Uri CONTENT_URI = Uri
			.parse("content://com.coogo.inet.vui.assistant.car/wakeupswitch");
	private static final int ALARMS = 1;
	private static final int ALARMS_ID = 2;
	static {
		sURLMatcher.addURI(HOST, "wakeupswitch", ALARMS);
		sURLMatcher.addURI(HOST, "wakeupswitch/#", ALARMS_ID);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new WakeUpDB(getContext());
		return true;

	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		int match = sURLMatcher.match(uri);
		switch (match) {
		case ALARMS:
			qb.setTables(WakeUpDB.TNAME);
			break;
		case ALARMS_ID:
			qb.setTables(WakeUpDB.TNAME);
			qb.appendWhere("_id=");
			qb.appendWhere(uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor cur = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);
		if (cur != null) {
			cur.setNotificationUri(getContext().getContentResolver(), uri);

		}
		return cur;

	}

	@Override
	public String getType(Uri uri) {
		int match = sURLMatcher.match(uri);

		switch (match) {
		case ALARMS:
			return "vnd.android.cursor.dir/alarms";
		case ALARMS_ID:
			return "vnd.android.cursor.item/alarms";
		default:
			throw new IllegalArgumentException("Unknown URL");
		}

	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sURLMatcher.match(uri) != ALARMS) {
			throw new IllegalArgumentException("Cannot insert into URL: " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(WakeUpDB.TNAME, null, values);
		if (rowId < 0) {
			throw new SQLException("Failed to insert row into " + uri);
		}
		Uri newUrl = ContentUris.withAppendedId(CONTENT_URI, rowId);
		getContext().getContentResolver().notifyChange(newUrl, null);
		return newUrl;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		long rowId = 0;
		switch (sURLMatcher.match(uri)) {
		case ALARMS:
			count = db.delete(WakeUpDB.TNAME, selection, selectionArgs);
			break;
		case ALARMS_ID:
			String segment = uri.getPathSegments().get(1);
			rowId = Long.parseLong(segment);
			if (TextUtils.isEmpty(selection)) {
				selection = "_id=" + rowId;
			} else {
				selection = "_id=" + rowId + " AND (" + selection + ")";
			}
			count = db.delete(WakeUpDB.TNAME, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Cannot delete from URL: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count;
		long rowId = 0;
		int match = sURLMatcher.match(uri);
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (match) {
		
			case ALARMS_ID: {
				String segment = uri.getPathSegments().get(1);
				rowId = Long.parseLong(segment);
				count = db.update(WakeUpDB.TNAME, values, "_id=" + rowId, null);
				break;
			}
			
			case ALARMS:
				count = db.update(WakeUpDB.TNAME, values, null, null);
				break;
				
			default: {
				throw new UnsupportedOperationException("Cannot update URL: " + uri);
			}
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;

	}

}
