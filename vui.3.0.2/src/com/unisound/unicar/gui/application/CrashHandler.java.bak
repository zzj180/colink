package com.unisound.unicar.gui.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

import com.unisound.unicar.gui.utils.Logger;

/**
 * 自定义系统的Crash捕捉类 将软件版本信息，设备信息，出错信息保存在SD卡中
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private Context mContext;
    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().toString();
    private static CrashHandler mInstance = new CrashHandler();


    private CrashHandler() {}

    /**
     * 单例模式，保证只有一个CrashHandler实例存在
     * 
     * @return
     */
    public static CrashHandler getInstance() {
        return mInstance;
    }

    /**
     * 异常发生时，系统回调的函数，我们在这里处理一些操作
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 将一些信息保存到SDcard中
        savaInfoToSD(mContext, ex);
    }

    /**
     * 为我们的应用程序设置自定义Crash处理
     */
    public void setCrashHanler(Context context) {
        mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
     * 
     * @param context
     * @return
     */
    private HashMap<String, String> obtainSimpleInfo(Context context) {
        HashMap<String, String> map = new HashMap<String, String>();
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo =
                    mPackageManager.getPackageInfo(context.getPackageName(),
                            PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        map.put("versionName", mPackageInfo.versionName);
        map.put("versionCode", "" + mPackageInfo.versionCode);

        map.put("MODEL", "" + Build.MODEL);
        map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
        map.put("PRODUCT", "" + Build.PRODUCT);

        return map;
    }


    /**
     * 获取系统未捕捉的错误信息
     * 
     * @param throwable
     * @return
     */
    private String obtainExceptionInfo(Throwable throwable) {
        StringWriter mStringWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
        throwable.printStackTrace(mPrintWriter);
        mPrintWriter.close();

        Logger.e(TAG, mStringWriter.toString());
        return mStringWriter.toString();
    }

    /**
     * 保存获取的 软件信息，设备信息和出错信息保存在SDcard中
     * 
     * @param context
     * @param ex
     * @return
     */
    private String savaInfoToSD(Context context, Throwable ex) {
        String fileName = null;
        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, String> entry : obtainSimpleInfo(context).entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append(" = ").append(value).append("\n");
        }

        sb.append(obtainExceptionInfo(ex));

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir =
                    new File(SDCARD_ROOT + File.separator + "YunZhiSheng" + File.separator
                            + "crash" + File.separator + "uniCarSolution" + File.separator);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try {
                fileName =
                        dir.toString() + File.separator + paserTime(System.currentTimeMillis())
                                + ".log";
                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write(sb.toString().getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return fileName;

    }


    /**
     * 将毫秒数转换成yyyy-MM-dd-HH-mm-ss的格式
     * 
     * @param milliseconds
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    private String paserTime(long milliseconds) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String times = format.format(new Date(milliseconds));
        return times;
    }
}
