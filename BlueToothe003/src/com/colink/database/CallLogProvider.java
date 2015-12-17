package com.colink.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class CallLogProvider extends ContentProvider  {  
  
    // public static final String SCHEME = "test";  
    public static final String SCHEME = "content"; // 源码里面规定这样写，所以这个地方改变不了  
  
    public static final String HOST = "com.colink.calllogprovider";  
    public static final String PATH = "calllog";  
  
    public static final int ALARMS = 1;  
    public static final String SHARE_LIST_TYPE = "com.colink.calllogprovider.dir/";  
    public static final int ALARMS_ID = 2;  
    public static final String SHARE_TYPE = "com.colink.calllogprovider.item/";  
  
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);  
  
    private SQLiteOpenHelper mDB = null;  
  
    // ===content://com.zyj:497393102/simple  
    public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + HOST +"/" + PATH);  
  
    // 添加Uri的匹配方式，返回的就是上面自定义的整数类型，1代表操作的是一个批量，2操作的是单独的一个对象  
    static  
    {  
        sURIMatcher.addURI(HOST , PATH, ALARMS);  
        sURIMatcher.addURI(HOST , PATH + "/#", ALARMS_ID);  
    }  
  
    @Override  
    public boolean onCreate()  
    {  
        mDB = new MyDB(getContext()); // 获取数据库的引用  
        return mDB != null;  
    }  
  
    @Override  
    public String getType(Uri uri)  
    {  
        // 得到我们自定义的Uri的类型，看上面你自己的定义  
        int match = sURIMatcher.match(uri);  
        switch (match)  
        {  
            case ALARMS:  
            {  
                return SHARE_LIST_TYPE;  
            }  
            case ALARMS_ID:  
            {  
                return SHARE_TYPE;  
            }  
            default:  
            {  
                throw new IllegalArgumentException("Unknown URI: " + uri);  
            }  
        }  
    }  
  
    @Override  
    public Uri insert(Uri uri, ContentValues values)  
    {  
        // 首先是看Uri和我们自定义的是否匹配，，匹配则将数据属性插入到数据库中并同志更新  
  
    	 SQLiteDatabase db = mDB.getWritableDatabase();  
    	 long rowID = db.insert(MyDB.DATABASE_TABLE, null, values);
    	 
        if (rowID != -1)  
        {  
            getContext().getContentResolver().notifyChange(uri, null);  
            return ContentUris.withAppendedId(CONTENT_URI, rowID);  
        }  
        return CONTENT_URI;  
    }  
  
    @Override  
    public int delete(Uri uri, String selection, String[] selectionArgs)  
    {  
    	  SQLiteDatabase db = mDB.getWritableDatabase();  
        // 首先是看Uri和我们自定义的是否匹配，，匹配则进行删除  
  
        int count = 0;  
        int match = sURIMatcher.match(uri);  
        switch (match)  
        {  
            case ALARMS:  
            case ALARMS_ID:  
                String where = null;  
                // 这里对selection进行匹配操作，看你传递的是一个批量还是一个单独的文件  
                if (selection != null)  
                {  
                    if (match == ALARMS)  
                    {  
                        where = "( " + selection + " )";  
                    }  
                    else  
                    {  
                        where = "( " + selection + " ) AND ";  
                    }  
                }  
                else  
                {  
                    where = "";  
                }  
                if (match == ALARMS_ID)  
                {  
                    // 如果你传递的是一个单独的文件，也就是Uri后面添加了/item的，那么在这里把该值与数据库中的属性段进行比较，返回sql语句中的where  
                    String segment = uri.getPathSegments().get(1);  
                    long rowId = Long.parseLong(segment);  
                    where += " ( " + CallLogDatabase.CALLLOG_ROWID + " = " + rowId + " ) ";  
                }  
                count = db.delete(MyDB.DATABASE_TABLE, where, selectionArgs);  
                break;  
            default:  
                throw new UnsupportedOperationException("Cannot delete URI: " + uri);  
        }  
        getContext().getContentResolver().notifyChange(uri, null);  
        return count;  
    }  
  
    @Override  
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)  
    {  
        // 基本同上了  
        SQLiteDatabase db = mDB.getWritableDatabase();  
  
        int count;  
        long rowId = 0;  
  
        int match = sURIMatcher.match(uri);  
        switch (match)  
        {  
            case ALARMS:  
            case ALARMS_ID:  
            {  
                String myWhere;  
                if (selection != null)  
                {  
                    if (match == ALARMS)  
                    {  
                        myWhere = "( " + selection + " )";  
                    }  
                    else  
                    {  
                        myWhere = "( " + selection + " ) AND ";  
                    }  
                }  
                else  
                {  
                    myWhere = "";  
                }  
                if (match == ALARMS_ID)  
                {  
                    String segment = uri.getPathSegments().get(1);  
                    rowId = Long.parseLong(segment);  
                    myWhere += " ( " + MyDB.CALLLOG_ROWID + " = " + rowId + " ) ";  
                }  
  
                if (values.size() > 0)  
                {  
                    count = db.update(MyDB.DATABASE_TABLE, values, myWhere, selectionArgs);  
                }  
                else  
                {  
                    count = 0;  
                }  
                break;  
            }  
            default:  
            {  
                throw new UnsupportedOperationException("Cannot update URI: " + uri);  
            }  
        }  
        getContext().getContentResolver().notifyChange(uri, null);  
  
        return count;  
    }  
  
    @Override  
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)  
    {  
        SQLiteDatabase db = mDB.getReadableDatabase();  
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder(); //SQLiteQueryBuilder是一个构造SQL查询语句的辅助类  
  
        int match = sURIMatcher.match(uri);  
        switch (match)  
        {  
            case ALARMS:  
            {  
                qb.setTables(MyDB.DATABASE_TABLE);  
                break;  
            }  
            case ALARMS_ID:  
            {  
                qb.setTables(MyDB.DATABASE_TABLE);  
                qb.appendWhere(MyDB.CALLLOG_ROWID + "=");  
                qb.appendWhere(uri.getPathSegments().get(1));  
                break;  
            }  
            default:  
                throw new IllegalArgumentException("Unknown URI: " + uri);  
        }  
        Cursor ret = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);  
  
        if (ret != null)  
        {  
            ret.setNotificationUri(getContext().getContentResolver(), uri);  
        }  
        return ret;  
    }  
  
    private static class MyDB extends SQLiteOpenHelper  
    {  
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
        
        private SQLiteDatabase mDB = null;  
        /**
         * Database creation sql statement
         */
        private static final String CREATE_STUDENT_TABLE =
            "create table " + DATABASE_TABLE + " (" + CALLLOG_ROWID + " integer primary key autoincrement, "
            + CALLLOG_NAME +" text, " + CALLLOG_NUMBER + " text not null," + CALLLOG_TYPE +" integer  not null, "
            		+CALLLOG_DATE+" integer,"+CALLLOG_DURATION+" text);";
     
        private MyDB(Context context)  
        {  
            super(context, DATABASE_NAME, null, 1);  
        }  
  
        @Override  
        public void onCreate(SQLiteDatabase db)  
        {  
            mDB = db;  
            mDB.execSQL(CREATE_STUDENT_TABLE);  
        }  
  
        @Override  
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)  
        {  
            // 升级，自己可以去实现  
        }  
    }  
}  