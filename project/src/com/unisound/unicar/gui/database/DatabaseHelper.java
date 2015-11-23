package com.unisound.unicar.gui.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

	// db name
	private static final String DATABASE_NAME = "unidrive.db";
	private static final int DATABASE_VERSION = 100;
	private static DatabaseHelper mInstance;
	
	DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static synchronized DatabaseHelper getInstance(Context context) {
		if(mInstance == null) {
			mInstance = new DatabaseHelper(context);
		}
		return mInstance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE " + DBConstant.ChatHistoryTable.TABLE_NAME + "("
				+ DBConstant.ChatHistoryTable._ID + " INTEGER PRIMARY KEY, "
				+ DBConstant.ChatHistoryTable.CHAT_ASK + " TEXT, "
				+ DBConstant.ChatHistoryTable.CHAT_ANSWER + " TEXT, "
				+ DBConstant.ChatHistoryTable.CHAT_TIME + " TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
