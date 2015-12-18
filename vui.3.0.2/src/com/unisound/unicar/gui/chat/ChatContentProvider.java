package com.unisound.unicar.gui.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.unisound.unicar.gui.database.DBConstant;
import com.unisound.unicar.gui.database.DBConstant.ChatHistoryTable;
import com.unisound.unicar.gui.database.DBOpenHelper;
import com.unisound.unicar.gui.utils.Logger;


public class ChatContentProvider extends ContentProvider {

    private static final String TAG = "ChatContentProvider";
    // database helper
    private DBOpenHelper mOpenHelper;
    private SQLiteDatabase mDb;
    //
    private Context mContext;
    private ContentResolver mContentResolver;

    //
    private static HashMap<String, String> sHistoryProjectionMap;
    static {
        sHistoryProjectionMap = new HashMap<String, String>();
        sHistoryProjectionMap.put(ChatHistoryTable._ID, ChatHistoryTable._ID);
        sHistoryProjectionMap.put(ChatHistoryTable.CHAT_ASK, ChatHistoryTable.CHAT_ASK);
        sHistoryProjectionMap.put(ChatHistoryTable.CHAT_ANSWER, ChatHistoryTable.CHAT_ANSWER);
        sHistoryProjectionMap.put(ChatHistoryTable.CHAT_TIME, ChatHistoryTable.CHAT_TIME);
    }
    //
    private static final UriMatcher sUriMatch;
    // 定义匹配时返回的number，此处只有1个table，故有collection和item两种
    private static final int HISTORY_COLLECTION_URI_INDICATOR = 1;
    private static final int HISTORY_ITEM_URI_INDICATION = 2;
    static {
        sUriMatch = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatch.addURI(DBConstant.AUTHORITY, "history", HISTORY_COLLECTION_URI_INDICATOR);
        sUriMatch.addURI(DBConstant.AUTHORITY, "history/#", HISTORY_ITEM_URI_INDICATION);
    }


    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        int match = sUriMatch.match(uri);
        switch (match) {
            case HISTORY_COLLECTION_URI_INDICATOR:
                return "vnd.android.cursor.dir/history";
            case HISTORY_ITEM_URI_INDICATION:
                return "vnd.android.cursor.item/history";
            default:
                throw new IllegalArgumentException("Unknow URI:" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        // 新增,故uri只能为collection
        if (sUriMatch.match(uri) != HISTORY_COLLECTION_URI_INDICATOR) {
            throw new IllegalArgumentException("URI =" + uri);
        }
        // 允许插入空行
        ContentValues cv;
        if (values == null) {
            cv = new ContentValues();
        } else {
            cv = new ContentValues(values);
        }
        //
        if (cv.containsKey(ChatHistoryTable.CHAT_TIME)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd-HH:mm:ss");
            String now = sdf.format(new Date(System.currentTimeMillis()));
            cv.put(ChatHistoryTable.CHAT_TIME, now);
        }

        mDb = mOpenHelper.getWritableDatabase();
        long rowId = mDb.insert(ChatHistoryTable.TABLE_NAME, null, cv);
        if (rowId > 0) {
            Uri insertUri = ContentUris.withAppendedId(ChatHistoryTable.CONTENT_URI, rowId);
            mContentResolver.notifyChange(insertUri, null);
            Logger.d(TAG, "insertUri =" + insertUri);
            return insertUri;
        }
        throw new SQLiteException("Failed to insert!!!");
    }


    private boolean initialize() {
        mContext = getContext();
        mContentResolver = mContext.getContentResolver();
        mOpenHelper = DBOpenHelper.getInstance(mContext);
        try {
            mDb = mOpenHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            mDb = mOpenHelper.getReadableDatabase();
        }
        return true;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        initialize();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        // TODO Auto-generated method stub
        Logger.d(TAG, "query uri=" + uri);
        // check uri
        // first 通过SQLiteQueryBuilder,设置数据库查询的信息.Uri有两种情况,一种是collect,
        // 一种已经指定某个item,两者需要区别对待,item将获取_ID,并在where中增加一个匹配条件.
        SQLiteQueryBuilder dbQuery = new SQLiteQueryBuilder();
        final int match = sUriMatch.match(uri);
        switch (match) {
            case HISTORY_COLLECTION_URI_INDICATOR:
                dbQuery.setTables(ChatHistoryTable.TABLE_NAME);
                dbQuery.setProjectionMap(sHistoryProjectionMap);
                break;
            case HISTORY_ITEM_URI_INDICATION:
                dbQuery.setTables(ChatHistoryTable.TABLE_NAME);
                dbQuery.setProjectionMap(sHistoryProjectionMap);
                dbQuery.appendWhere(ChatHistoryTable._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknow uri=" + uri);
        }
        // second v 对排序进行缺省设置
        // String order = TextUtils.isEmpty(sortOrder) ? HistoryTable.DEFAULE_SORT_ORDER :
        // sortOrder;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        // third start query
        final Cursor c =
                dbQuery.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // fourth 向系统注册通知:观察所要查询的数据,即Uri对应的数据是否发生变化
        // 开发者通过provider接口获取数据,可通过通知获知数据已经发生变更
        if (c != null) {
            c.setNotificationUri(mContentResolver, ChatHistoryTable.CONTENT_URI);
        }
        return c;
    }

    @Override
    public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
        // TODO Auto-generated method stub
        return 0;
    }

}
