package com.unisound.unicar.gui.chat;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.unisound.unicar.gui.database.DBOpenHelper;
import com.unisound.unicar.gui.database.DBConstant.ChatHistoryTable;
import com.unisound.unicar.gui.utils.Logger;

/**
 * 
 * @author xiaodong
 * @date 20150820
 */
public class ChatDataBaseUtil {

    private static final String TAG = ChatDataBaseUtil.class.getSimpleName();

    private static final String[] cloums = new String[] {"_id", "text", "tts", "time"};
    private static final int CLOUMS_INDEX_ID = 0;
    private static final int CLOUMS_INDEX_ASK = 1;
    private static final int CLOUMS_INDEX_ANSWER = 2;
    private static final int CLOUMS_INDEX_TIME = 3;


    /**
     * get exist chat data from database
     * 
     * @param context
     * @return
     */
    public static List<ChatObject> getExistChatDataFromDB(Context context) {
        Logger.d(TAG, "!--->getExistChatDataFromDB----");
        List<ChatObject> allChatObjList = new ArrayList<ChatObject>();
        try {
            Cursor cursor =
                    context.getContentResolver().query(ChatHistoryTable.CONTENT_URI, cloums, null,
                            null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    String ask_info = cursor.getString(CLOUMS_INDEX_ASK);
                    String answer_info = cursor.getString(CLOUMS_INDEX_ANSWER);
                    ChatObject co = new ChatObject(ask_info, answer_info);
                    allChatObjList.add(co);
                } while (cursor.moveToNext());
                if (allChatObjList.size() != cursor.getCount()) {
                    Logger.d(TAG, "Unknow count error!!!!");
                }
            }
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        } catch (Exception e) {
            DBOpenHelper.getInstance(context);
            e.printStackTrace();
        }
        return allChatObjList;
    }


    /**
     * save data to database
     */
    public static void saveNewChatDataIntoDB(Context context, List<ChatObject> chatObjList) {
        Logger.d(TAG, "!--->saveNewChatDataIntoDB----");
        try {
            for (ChatObject object : chatObjList) {
                ContentValues values = new ContentValues(2);
                values.put(ChatHistoryTable.CHAT_ASK, object.getAskText());
                values.put(ChatHistoryTable.CHAT_ANSWER, object.getAnswerText());
                context.getContentResolver().insert(ChatHistoryTable.CONTENT_URI, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DBOpenHelper.getInstance(context);
        }
    }
}
