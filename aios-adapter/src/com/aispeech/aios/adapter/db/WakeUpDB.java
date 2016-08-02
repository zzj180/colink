package com.aispeech.aios.adapter.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WakeUpDB extends SQLiteOpenHelper {
	public static final String DBNAME = "wakeup";

	public static final String TNAME = "wakeupswitch";

	public static final int VERSION = 1;

	public static String TID = "_id";

	public static String SWITCH = "switch";

	private static final String CREATE_BLUETOOTHE_TABLE = "create table "
			+ TNAME + " (" + TID + " integer primary key autoincrement, "
			+ SWITCH + " integer  not null);";

	public WakeUpDB(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BLUETOOTHE_TABLE);
		ContentValues values = new ContentValues();
		values.put(SWITCH, 1);
		db.insert(TNAME, null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXIST " + TNAME;
		db.execSQL(sql);
		onCreate(db);
	}

}
