package com.colink.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlueTootheDB extends SQLiteOpenHelper {
	public static final String DBNAME = "bluetoothedb";

	public static final String TNAME = "bluetootheonline";

	public static String TID = "_id";

	public static String ONLINE = "online";

	public static String DEVICENAME = "devicename";
	
	public static final String SUPPORT = "support";

	public BlueTootheDB(Context context) {
		super(context, DBNAME, null, 13);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		 String CREATE_BLUETOOTHE_TABLE = "create table "
					+ TNAME + " (" + TID + " integer primary key autoincrement, "
					+ ONLINE + " integer  not null, " + DEVICENAME + " text, "+ SUPPORT + " integer);";
		db.execSQL(CREATE_BLUETOOTHE_TABLE);
		ContentValues values = new ContentValues();
		values.put(ONLINE, 0);
		values.put(SUPPORT, 0);
		db.insert(TNAME, null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TNAME;
		db.execSQL(sql);
		onCreate(db);
	}

}
