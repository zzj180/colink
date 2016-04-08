package com.aispeech.aios.adapter.localScanService.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDBHelper extends SQLiteOpenHelper {

    //数据库信息
    public static final int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "music_scanner.db";

    //方便SQL合成的常量
    private static final String TEXT = " TEXT";
    private static final String SEP = ",";

    //----本地音乐的数据库
    public static final String SQL_CREATE_MUSIC_LOCAL = "CREATE TABLE "
            + MusicLocal.TABLE_NAME + "("
            + MusicLocal._ID + TEXT + SEP
            + MusicLocal.ARTIST + TEXT + SEP
            + MusicLocal.TITLE + TEXT + SEP
            + MusicLocal.ALBUM + TEXT + SEP
            + MusicLocal.DURATION + TEXT + SEP
            + MusicLocal.SIZE + TEXT + SEP
            + MusicLocal.DATA + TEXT + SEP
            + MusicLocal.MIME_TYPE + TEXT + SEP
            + MusicLocal.FILENAME + TEXT + SEP
            + MusicLocal.PATH + TEXT
                       + ")";
    private static final String SQL_DELETE_MUSIC_LOCAL = "DROP TABLE IF EXISTS " + MusicLocal.TABLE_NAME;

    private static MusicDBHelper instance = null;
    private Context mContext;

    private MusicDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext=context;
    }

    /**
     * 获得MusicDBHelper
     * @param context
     * @return
     */
    public static synchronized MusicDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MusicDBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MUSIC_LOCAL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL(SQL_DELETE_MUSIC_LOCAL);
        onCreate(db);
    }

}
