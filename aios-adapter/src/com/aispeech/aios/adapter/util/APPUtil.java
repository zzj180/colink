package com.aispeech.aios.adapter.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.node.TTSNode;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @desc APP通用工具类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class APPUtil {

    private static final String TAG = "AIOS-Adapter-AppUtil";
    private static APPUtil mUtil;
    private Context mContext;
    private Process process;

    public static synchronized APPUtil getInstance() {

        if (mUtil == null) {

            mUtil = new APPUtil();
        }
        return mUtil;
    }

    public APPUtil() {
        this.mContext = AdapterApplication.getContext();
    }

    /**
     * 根据ShareID判断
     * @return 是否是系统级应用
     */
    public boolean isSystemUid() {
        String sharedUserId = "";
        try {
            PackageInfo pi = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
            sharedUserId = pi.sharedUserId;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null != sharedUserId && sharedUserId.equals("android.uid.system");
    }

    /**
     * 通过包名检测APP是否安装
     *
     * @param packageName 包名
     * @return true or false
     */
    public boolean isInstalled(String packageName) {
        boolean isInstalled = false;

        if (!TextUtils.isEmpty(packageName)) {
            PackageInfo packageInfo;
            try {
                packageInfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                packageInfo = null;
                e.printStackTrace();
            }

            if (packageInfo == null) {
                isInstalled = false;
            } else {
                isInstalled = true;
            }
        }
        AILog.i(TAG, packageName + "is installed ? " + isInstalled);
        return isInstalled;
    }

    /**
     * 听过包名检测APP是否运行
     *
     * @param packName
     * @return
     */
    public boolean isRunning(String packName) {

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packName) || info.baseActivity.getPackageName().equals(packName)) {
                isAppRunning = true;
                break;
            }
        }
        AILog.i(TAG, packName + "is running ? " + isAppRunning);
        return isAppRunning;
    }

    /**
     * 根据包名强行停止一个应用，需要系统签名，并且push到/system/app目录下
     * @param pkgName 包名
     */
    public void forceStopPackage(String pkgName) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(am, pkgName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束进程
     */
    public void killProcess(String packageName) {
        try {
            process = Runtime.getRuntime().exec("su \n");
            process = Runtime.getRuntime().exec("am force-stop " + packageName + " \n");
            process = Runtime.getRuntime().exec("exit \n");
            AILog.e(TAG, "closeApplication "+ packageName + " by aios-adapter!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化进程
     */
    private void initProcess() {
        if (process == null) {
            try {
                process = Runtime.getRuntime().exec("su \n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     */
    private void closeOutputStream() {
        if (process != null)
            try {
                process.getOutputStream().close();
                process = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * 回到主页
     */
    public static void goHomePage() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
        intent.addCategory(Intent.CATEGORY_HOME);
        AdapterApplication.getContext().startActivity(intent);
        AILog.i(TAG, "go to home page");
    }

    /**
     * 结束掉某个APP，需传入包名
     *
     * @param pkg 应用包名
     */
    public boolean closeApplication(String pkg) {
        if (!isInstalled(pkg)) {
            TTSNode.getInstance().play("本地找不到此应用...");
            AILog.d(TAG, "在本地找不到此应用！");
            return false;
        }
        if (isRunning(pkg)) {
            AILog.d(TAG, "closeApplication application!!!");
            initProcess();
            killProcess(pkg);
            closeOutputStream();
            return true;
        } else {
            TTSNode.getInstance().play("此应用没有运行...");
            AILog.d(TAG, "无运行中的此应用");
            return false;
        }
    }

    /**
     * 通过包名打开应用 返回true:成功 返回false:失败
     */
    public boolean openApplication(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }

        AILog.i(TAG, "The package will be open : " + pkgName);
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(pkgName);
        if (intent == null) {
            return false;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        return true;
    }
}
