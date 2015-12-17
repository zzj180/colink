/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : DBOpenHelper.java
 * @ProjectName : uniCarGUI
 * @PakageName : com.unisound.unicar.gui.database
 * @Author : Xiaodong.He
 * @CreateDate : 2015-09-22
 */
package com.unisound.unicar.gui.database;

import com.unisound.unicar.gui.utils.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/***
 * 
 * @author xiaodong
 * 
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = DBOpenHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 101;
    private static DBOpenHelper mInstance;

    private DBOpenHelper(Context context) {
        super(context, DBConstant.DB_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBOpenHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBOpenHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, "onCreate---Create DB...");

        db.execSQL(DBConstant.ChatHistoryTable.SQL_CMD_CREATE_TABLE);
        db.execSQL(DBConstant.FmCategoryTB.SQL_CMD_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.w(TAG, "onUpgrade---upgrading database from version " + oldVersion + " to"
                + newVersion + ", which will " + "destroy all old data");

        db.execSQL(DBConstant.ChatHistoryTable.SQL_CMD_DROP_TABLE);
        db.execSQL(DBConstant.FmCategoryTB.SQL_CMD_DROP_TABLE);

        onCreate(db);
    }

}
