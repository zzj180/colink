package com.aispeech.aios.adapter.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.AdapterApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @desc 定位数据类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class LocationDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "LocationDBHelper";

    private static final Integer DB_VERSION = 2;
    private static final String DB_NAME = "location.db";

    private SQLiteDatabase db = null;

    private File mDbFile = null;

    private Context mContext;

    private static LocationDBHelper instance;

    /**
     * 单例模式
     *
     * @param context
     */
    private LocationDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        db = openDatabase();
    }

    public SQLiteDatabase openDatabase() {
        mDbFile = mContext.getDatabasePath(DB_NAME);
        AILog.i("databasePath:", mDbFile.getAbsolutePath());

        if (!mDbFile.exists()) {
            try {
                copyDB();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error creating source database", e);
            }
        }

        return SQLiteDatabase.openDatabase(mDbFile.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);
    }

    public static synchronized LocationDBHelper getInstance() {
        if (instance == null) {
            instance = new LocationDBHelper(AdapterApplication.getContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        AILog.d(TAG, "location db onCreate");
        createDB();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        AILog.d(TAG, "location db onUpdate, oldVersion: " + oldVersion + ", newVersion: " + newVersion);
        updateDB();
    }

    private void createDB() {
        boolean dBExists = mDbFile.exists();
        if (!dBExists) {
            try {
                copyDB();
            } catch (IOException e) {
                e.printStackTrace();
                //LauncherApplication.sendException(e);
            }
        }
    }

    private void updateDB() {
        try {
            copyDB();
        } catch (IOException e) {
            e.printStackTrace();
            //LauncherApplication.sendException(e);
        }
    }

    /**
     * 复制数据库文件到软件目录
     */
    public synchronized void copyDB() throws IOException {
        AILog.d(TAG, "DATABASE: COPY START ");
        File directory = mDbFile.getParentFile();
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                AILog.e(TAG, "failed creat files  in location");
            }
        }
        InputStream is = mContext.getAssets().open(DB_NAME);
        OutputStream os = null;
        try {
            os = new FileOutputStream(mDbFile);
            byte[] buffer = new byte[1024 * 100];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        is.close();
        AILog.d(TAG, "DATABASE: COPY END ");
    }

    private static final String REGEX_86 = "+86";
    private static final String REGEX_0_9 = "[^0-9]";
    private static final String empty = "";

    /**
     * 根据号码查询号码归属地和运营商信息
     *
     * @param phoneNumber 电话号码
     * @return 号码归属地和运营商信息
     */
    public String findPhoneAreaByNumber(String phoneNumber) {
        //String phoneNumber = phoneInfo.getNumber();
        if (TextUtils.isEmpty(phoneNumber)) {
            return null;
        }
        if (phoneNumber.startsWith(REGEX_86)) {
            phoneNumber = phoneNumber.substring(3);
        }
        phoneNumber = phoneNumber.replaceAll(REGEX_0_9, empty);
        if (phoneNumber.length() < 7 || phoneNumber.length() > 11) {
            return null;
        }
        phoneNumber = phoneNumber.substring(0, 7);
        Cursor c = null;
        String area = null;
        try {
            c = db.rawQuery("select * from phone_location where rowid = ?", new String[]{phoneNumber});
            if (c.getCount() == 1) {
                c.moveToNext();
                area = c.getString(c.getColumnIndex("area"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return area;

    }

}