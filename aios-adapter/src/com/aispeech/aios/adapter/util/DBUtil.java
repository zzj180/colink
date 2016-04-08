package com.aispeech.aios.adapter.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.AdapterApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @desc 数据库工具类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class DBUtil {
    private String filePath = "";
    private String fileName = "restrict.db";
    private SQLiteDatabase db;
    public static DBUtil mInstance;

    public DBUtil() {
        openDatabase(AdapterApplication.getContext());
    }

    /**
     * 获取本类实例，打开了数据了
     * @return 本类实例
     */
    public static DBUtil getInstance() {
        if (mInstance == null) {
            mInstance = new DBUtil();
        }
        return mInstance;
    }

    private void openDatabase(Context context) {
        db = openDb(context);
    }

    public String getCity(String area) {
        String city = selectCity(area);
        return city;
    }

    private SQLiteDatabase openDb(Context context) {
        filePath = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File dbFile = new File(filePath);
        if (dbFile.exists()) {
            return SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        } else {
            try {
                File path = new File(context.getFilesDir().getAbsolutePath());
                if (path.mkdir()) {
                } else {
                }
                AssetManager assetManager = context.getAssets();
                InputStream is = assetManager.open(fileName);
                FileOutputStream fos = new FileOutputStream(dbFile);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
                fos.close();
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            return openDb(context);
        }
    }

    private String selectCity(String area) {
        Cursor c = null;
        try {
            c = db.rawQuery("select * from mapn where area='" + area + "'",
                    null);
            if (c != null) {
                if (c.getCount() == 1) {
                    if (c.moveToFirst()) {
                        String city = c.getString(c.getColumnIndex("city"));
                        AILog.d("city1=" + city);
                        return city == null ? "" : city;
                    }
                } else if (c.getCount() > 1) {
                    return getCityMap1(area);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return "";
    }

    private String getCityMap1(String area) {
        Cursor c = null;
        String city = "";
        try {
            c = db.rawQuery("select * from map1 where area='" + area + "'",
                    null);
            if (c != null) {
                if (c.moveToFirst()) {
                    city = c.getString(c.getColumnIndex("city"));
                    AILog.d("citylast=" + city);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return city == null ? "" : city;
    }
}
