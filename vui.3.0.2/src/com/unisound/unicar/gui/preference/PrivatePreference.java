/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : PrivatePreference.java
 * @ProjectName : vui_common
 * @PakageName : cn.yunzhisheng.preference
 * @Author : Dancindream
 * @CreateDate : 2013-8-15
 */
package com.unisound.unicar.gui.preference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.os.Environment;

import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-8-15
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-8-15
 * @Modified: 2013-8-15: 实现基本功能
 */
public class PrivatePreference {
    public static final String TAG = "PrivatePreference";
    public static String IMEI = "";
    public static String VERSION = "";
    public static String PACKAGE = "";
    private static boolean isReaded = false;
    private static HashMap<String, String> mDataMap = new HashMap<String, String>();
    private static String mSDCardPath = "";
    private static String mFilesPath = "";
    private static String mCachePath = "";

    public static String contact_type = "SYSTEM";

    public static String MAC = "";

    public static String mDeufaultLocation = "";

    private static ArrayList<IUpdatePreferenceListener> mUpdatePreferenceListeners =
            new ArrayList<IUpdatePreferenceListener>();

    public static void init(Context context) {
        if (context == null || isReaded) {
            return;
        }

        mFilesPath = context.getFilesDir().getAbsolutePath();
        mCachePath = context.getCacheDir().getAbsolutePath();
        int uid = android.os.Process.myUid();
        if (uid == android.os.Process.SYSTEM_UID) {
            Logger.e(TAG,
                    "Process is SYSTEM Process, Can't use getExternalStorageDirectory, will use %files% folder");
            mSDCardPath = mFilesPath;
        } else {
            mSDCardPath = Environment.getExternalStorageDirectory().getPath();
        }

        try {
            InputStream in = context.getAssets().open("config.mg");
            readMg(in, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream in = context.getAssets().open("debug.mg");
            readMg(in, context);
        } catch (IOException e) {}

        String custom_debug_path =
                getValue("custom_debug_path", "%sdcard%/YunZhiSheng/vui/debug/debug.mg");
        if (custom_debug_path != null && !custom_debug_path.equals("")) {
            custom_debug_path = transPath(custom_debug_path);
            File f = new File(custom_debug_path);
            if (f != null && f.exists()) {
                try {
                    InputStream in = new FileInputStream(f);
                    readMg(in, context);
                } catch (FileNotFoundException e) {}
            }
        }

        String debug = getValue("debug", "true");
        Logger.d(TAG, "!--->Config debug = " + debug);
        Logger.DEBUG = Boolean.parseBoolean(debug);

        String write_log = getValue("write_log", "false");
        // Logger.WRITE_LOG = Boolean.parseBoolean(write_log);

        String version = getValue("version", "");
        IMEI = DeviceTool.getDeviceId(context);
        VERSION = DeviceTool.getAppVersionName(context);
        PACKAGE = DeviceTool.getAppPackageName(context);
        MAC = DeviceTool.getMac(context);

        // mDeufaultLocation = getValue("default_location", mDeufaultLocation);
        contact_type = getValue("contact_type", contact_type);

        Logger.d(TAG, version);
        Logger.d(TAG, "IMEI:" + IMEI + ";" + "APP version:" + VERSION + ";" + "package:" + PACKAGE
                + ";" + "MAC:" + MAC);
        printConfig();
        // USCLogPush.init(context);
    }

    private static void printConfig() {
        Logger.d(TAG, "printConfig");
        if (mDataMap != null) {
            Set<String> keys = mDataMap.keySet();
            for (String key : keys) {
                Logger.d(TAG, "    [" + key + "=" + mDataMap.get(key) + "]");
            }
        }
    }

    private static void readMg(InputStream in, Context context) {
        if (in == null) {
            return;
        }
        try {
            if (in.available() <= 0) {
                return;
            }

            byte[] filecontent = new byte[in.available()];
            in.read(filecontent);
            in.close();

            String content = new String(filecontent, "utf-8");

            if (content == null || content.equals("")) {
                return;
            }

            content.replaceAll("\r\n", "\n");
            String[] dataItem = content.split("\n");

            if (dataItem == null || dataItem.length <= 0) {
                return;
            }

            for (int i = 0; i < dataItem.length; i++) {
                String item = dataItem[i];
                if (item == null || item.equals("")) {
                    continue;
                }
                item = item.trim();
                if (item.indexOf("#") == 0) {
                    continue;
                }
                String[] data = item.split("=");

                if (data == null || data.length <= 1) {
                    continue;
                }

                String key = "";
                String value = "";
                if (data.length >= 1) {
                    key = data[0];
                }
                if (data.length >= 2) {
                    value = data[1];
                }
                key = key == null ? "" : key.trim();
                value = transPath(value);

                mDataMap.put(key, value);
            }
        } catch (Exception e) {}
    }

    public static String getValue(String key, String defaultValue) {
        if (mDataMap != null && mDataMap.containsKey(key)) {
            return mDataMap.get(key);
        } else {
            return defaultValue;
        }
    }

    public static boolean getBooleanValue(String key, boolean defaultValue) {
        String value = getValue(key, String.valueOf(defaultValue));
        return Boolean.valueOf(value);
    }

    public static int getIntegerValue(String key, int defaultValue) throws NumberFormatException {
        String value = getValue(key, String.valueOf(defaultValue));
        return Integer.valueOf(value);
    }

    public static float getFloatValue(String key, float defaultValue) throws NumberFormatException {
        String value = getValue(key, String.valueOf(defaultValue));
        return Float.valueOf(value);
    }

    public static double getDoubleValue(String key, double defaultValue)
            throws NumberFormatException {
        String value = getValue(key, String.valueOf(defaultValue));
        return Double.valueOf(value);
    }

    public static void setValue(String key, String value) {
        if (mDataMap != null) {
            Logger.d(TAG, "setValue: key " + key + ", value " + value);
            mDataMap.put(key, value);
            for (int i = 0; i < mUpdatePreferenceListeners.size(); i++) {
                IUpdatePreferenceListener l = mUpdatePreferenceListeners.get(i);
                if (l != null) {
                    l.onUpdate();
                }
            }
        }
    }

    public static String transPath(String path) {
        path = path == null ? "" : path.trim();
        if (path.contains("%sdcard%")) {
            path = path.replaceAll("%sdcard%", mSDCardPath);
        } else if (path.contains("%files%")) {
            path = path.replaceAll("%files%", mFilesPath);
        } else if (path.contains("%cache%")) {
            path = path.replaceAll("%cache%", mCachePath);
        }
        return path;
    }

    public static void addUpdateListener(IUpdatePreferenceListener l) {
        mUpdatePreferenceListeners.add(l);
    }

    public static void removeUpdateListener(IUpdatePreferenceListener l) {
        mUpdatePreferenceListeners.remove(l);
    }

    public static int[] str2IntArray(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        String[] strArray = str.split(",");
        if (strArray == null || strArray.length <= 0) {
            return null;
        }
        int[] result = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            String origin = strArray[i];
            origin = origin == null ? "" : origin.trim();
            try {
                result[i] = Integer.valueOf(origin);
            } catch (Exception e) {
                result[i] = 0;
            }
        }
        return result;
    }

    public static String decrypt(int[] strInput) {
        char[] strArrary = new char[strInput.length];
        for (int i = 0; i < strInput.length; i++) {
            strArrary[i] = (char) ((~strInput[i]) - i - i);
        }
        return new String(strArrary);
    }
}
