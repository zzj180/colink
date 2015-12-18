package com.unisound.unicar.gui.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class DBConstant {

    public static final String DB_NAME = "unidrive.db";

    public static final String AUTHORITY = "com.unisound.unicar.gui.chat.ChatProvider";

    public static final class ChatHistoryTable implements BaseColumns {
        //
        public ChatHistoryTable() {};

        //
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/history");
        //
        public static final String TABLE_NAME = "history";
        public static final String CHAT_ASK = "text";
        public static final String CHAT_ANSWER = "tts";
        public static final String CHAT_TIME = "time";
        // public static final String DEFAULE_SORT_ORDER = "modified DESC";

        public static final String SQL_CMD_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY, " + CHAT_ASK + " TEXT, "
                + CHAT_ANSWER + " TEXT, " + CHAT_TIME + " TEXT" + ")";

        public static final String SQL_CMD_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /********************************** xiaodong 20151009 add Begin *************************************/
    public abstract interface FmCategoryTB {

        public static final String TABLE_NAME_CATEGORY = "TB_CATEGORY_XMFM";
        public static final String TABLE_ITEM_ID = "_ID";
        public static final String TABLE_ITEM_CATEGORY_ID = "CATEGORY_ID";
        public static final String TABLE_ITEM_CATEGORY_NAME = "CATEGORY_NAME";

        public static final String SQL_CMD_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_CATEGORY + "("
                // + TABLE_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TABLE_ITEM_CATEGORY_ID + " INTEGER," + TABLE_ITEM_CATEGORY_NAME + " TEXT);";

        public static final String SQL_CMD_DROP_TABLE = "DROP TABLE IF EXISTS "
                + TABLE_NAME_CATEGORY;
    }
    /********************************** xiaodong 20151009 add End ****************************************/
}
