package com.unisound.unicar.gui.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class DBConstant {
	
	public static final String AUTHORITY = "com.unisound.unicar.gui.chat.ChatProvider";
	
	public static final class ChatHistoryTable implements BaseColumns{
		//
		public ChatHistoryTable() {};
		//
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/history");
		//
		public static final String TABLE_NAME = "history";
		public static final String CHAT_ASK = "text";
		public static final String CHAT_ANSWER = "tts";
		public static final String CHAT_TIME = "time";
		//public static final String DEFAULE_SORT_ORDER = "modified DESC";
		
	}

}
