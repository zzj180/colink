package com.colink.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CallLogDatabase {
	  /**
     * Database Name
     */
    private static final String DATABASE_NAME = "calllog_database";
 
    /**
     * Table Name
     */
    private static final String DATABASE_TABLE = "tb_calllog";
    
    /**
     * Table columns
     */
    public static final String CALLLOG_NAME = "name";
    public static final String CALLLOG_NUMBER = "number";
    public static final String CALLLOG_ROWID = "_id";
    public static final String CALLLOG_TYPE = "type";
    public static final String CALLLOG_DATE = "date";
    public static final String CALLLOG_DURATION = "duration";
    
    /**
     * Database creation sql statement
     */
    private static final String CREATE_STUDENT_TABLE =
        "create table " + DATABASE_TABLE + " (" + CALLLOG_ROWID + " integer primary key autoincrement, "
        + CALLLOG_NAME +" text, " + CALLLOG_NUMBER + " text not null," + CALLLOG_TYPE +" integer  not null, "
        		+CALLLOG_DATE+" integer,"+CALLLOG_DURATION+" integer);";
 
    /**
     * Context
     */
    private Context mCtx;
 
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }
        /**
         * onCreate method is called for the 1st time when database doesn't exists.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_STUDENT_TABLE);
        }
        /**
         * onUpgrade method is called when database version changes.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
    
    public CallLogDatabase(Context context) {
		this.mCtx=context;
	}
    public CallLogDatabase open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    /**
     * This method is used for closing the connection.
     */
    public void close() {
        mDbHelper.close();
    }
 
    /**
     * This method is used to create/insert new record Student record.
     * @param name
     * @param grade
     * @return long
     */
    public long createCalllog(ContentValues values) {
        return mDb.insert(DATABASE_TABLE, null, values);
    }
    /**
     * This method will delete Student record.
     * @param rowId
     * @return boolean
     */
    public boolean deleteCalllog(long rowId) {
        return mDb.delete(DATABASE_TABLE, CALLLOG_ROWID + "=" + rowId, null) > 0;
    }
 
    /**
     * This method will return Cursor holding all the Student records.
     * @return Cursor
     */
    public Cursor fetchCallLogs() {
        return mDb.query(DATABASE_TABLE, new String[] {CALLLOG_ROWID, CALLLOG_NAME,
        		CALLLOG_NUMBER ,CALLLOG_TYPE,CALLLOG_DATE,CALLLOG_DURATION}, null, null, null, null, null);
    }
 
    /**
     * This method will return Cursor holding the specific Student record.
     * @param id
     * @return Cursor
     * @throws SQLException
     */
    public Cursor fetchCallLog(long id) throws SQLException {
        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {CALLLOG_ROWID, CALLLOG_NAME,
            		CALLLOG_NUMBER ,CALLLOG_TYPE,CALLLOG_DATE,CALLLOG_DURATION}, CALLLOG_ROWID + "=" + id, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
 
}
