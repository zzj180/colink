package com.unisound.unicar.gui.data.operation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import cn.yunzhisheng.common.PinyinConverter;

import com.unisound.unicar.gui.data.interfaces.IBaseListener;
import com.unisound.unicar.gui.model.AppInfo;
import com.unisound.unicar.gui.preference.AppsPreference;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;

public class AppsDataModel {
    private static final String TAG = "AppsDataModel";
    private static final int mDataInit = 0;
    private static final int mDataUpdate = 1;
    private int mDataType = mDataInit;
    private Thread mWorkThread = null;
    private ArrayList<AppInfo> mAppInfoList = new ArrayList<AppInfo>();
    private ArrayList<AppInfo> mAppInfoList_Syc = new ArrayList<AppInfo>();
    private File mAppsSavedFile = null;
    private final String mAppsCOPName = "dataapps.cop";
    private IBaseListener appsModelListener;
    private Context mContext = null;

    public AppsDataModel(Context context) {
        mContext = context;
    }

    private void update() {
        Logger.d(TAG, "update start");
        mDataType = mDataUpdate;
        sycApps();
        Logger.d(TAG, "update end");
    }

    public void init() {
        mAppsSavedFile = new File(Environment.getExternalStorageDirectory(), mAppsCOPName);
        AppsPreference.init();
        mDataType = mDataInit;
        sycApps();
        Logger.d(TAG, "apps init end");

    }

    public void setListener(IBaseListener l) {
        appsModelListener = l;
    }

    private void sycApps() {
        Logger.d(TAG, "sycApps");
        if (mWorkThread == null || !mWorkThread.isAlive()) {
            Runnable runnable = null;
            runnable = new SyncRunnable();
            mWorkThread = new Thread(runnable);
            mWorkThread.setPriority(Thread.MIN_PRIORITY);
            mWorkThread.setName(TAG);
            mWorkThread.start();
        }

    }

    private class SyncRunnable implements Runnable {
        @Override
        public void run() {
            sysApp();
        }
    }

    public void sysApp() {// 读取系统的app
        clearCache();
        long t1 = System.currentTimeMillis();
        PackageManager packageManager = mContext.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        if (packageManager != null) {
            List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
            mAppInfoList_Syc.clear();
            if (apps != null && apps.size() > 0) {
                for (int i = 0; i < apps.size(); i++) {
                    ResolveInfo resolveInfo = apps.get(i);
                    String appLabel = resolveInfo.loadLabel(packageManager).toString();
                    String[] pinyins = PinyinConverter.getWordSpell1(appLabel);
                    AppInfo appInfo =
                            new AppInfo(appLabel, (pinyins == null || pinyins.length == 0)
                                    ? ""
                                    : pinyins[0], resolveInfo.activityInfo.packageName,
                                    resolveInfo.activityInfo.name);
                    mAppInfoList_Syc.add(appInfo);
                }
            }
            long t2 = System.currentTimeMillis();
            Logger.d(TAG, "Get installed app:" + (t2 - t1));
            copyCache();
        }
    }

    private void clearCache() {
        Logger.d(TAG, "clearCache");
        if (mAppInfoList_Syc != null) {
            mAppInfoList_Syc.clear();
        }
    }

    private void copyCache() {
        Logger.d(TAG, "copyCache");
        if (mAppInfoList != null) {
            mAppInfoList.clear();
        }
        mAppInfoList.addAll(mAppInfoList_Syc);
        Logger.d(TAG, "mAppInfoList:size=" + (mAppInfoList == null ? 0 : mAppInfoList.size()));

        saveAppsToFile(mAppInfoList);
    }

    private void saveAppsToFile(ArrayList<AppInfo> mAppInfoList) {
        try {
            Logger.i(TAG, "--saveMediasToFile--" + mAppsSavedFile.getPath().toString() + " "
                    + Environment.getExternalStorageDirectory());

            if (!mAppsSavedFile.exists()) {
                mAppsSavedFile.createNewFile();
            } else {
                mAppsSavedFile.delete();
                mAppsSavedFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(mAppsSavedFile);
            for (int i = 0; i < mAppInfoList.size(); i++) {
                AppInfo info = mAppInfoList.get(i);
                String mPackageName = info.getmPackageName();
                String mAppLabel = info.getmAppLabel();
                String mAppLabelPinyin = info.getmAppLabelPinyin();

                String mClassName = info.getmClassName();

                JSONObject jObject = new JSONObject();
                JsonTool.putJSONObjectData(jObject, "jsonType", "APPS");
                JsonTool.putJSONObjectData(jObject, "mPackageName", mPackageName);
                JsonTool.putJSONObjectData(jObject, "mAppLabel", mAppLabel);
                JsonTool.putJSONObjectData(jObject, "mAppLabelPinyin", mAppLabelPinyin);
                JsonTool.putJSONObjectData(jObject, "mClassName", mClassName);
                Logger.d(TAG, "-apps-" + jObject.toString());
                fos.write((jObject.toString() + "\n").getBytes());
            }
            mAppInfoList.clear();
            fos.close();
            onDataDone();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private void onDataDone() {// app数据写入文件成功的回调告诉vui，app数据写入文件成功
        Logger.d(TAG, "onDataDone");
        if (appsModelListener != null) {
            if (mDataType == mDataInit) {
                appsModelListener.onDataDone(SessionPreference.SAVE_APPS_DATA_DONE);
            } else if (mDataType == mDataUpdate) {
                appsModelListener.onDataDone(SessionPreference.SAVE_UPDATE_APPS_DATA_DONE);
            }
        }

    }

    public void setDataModelListener(IBaseListener l) {
        appsModelListener = l;
    }


    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        mContext.registerReceiver(mAppChangedReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        if (mContext != null && mAppChangedReceiver != null) {
            mContext.unregisterReceiver(mAppChangedReceiver);
        }
    }

    private BroadcastReceiver mAppChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            update();
        }
    };

    public void release() {
        Logger.d(TAG, "release");
        appsModelListener = null;
        unregisterReceiver();
        mContext = null;
        mAppChangedReceiver = null;
        synchronized (mAppInfoList) {
            if (mAppInfoList != null) {
                mAppInfoList.clear();
                mAppInfoList = null;
            }
        }

        if (mAppInfoList_Syc != null) {
            mAppInfoList_Syc.clear();
            mAppInfoList_Syc = null;
        }
    }
}
