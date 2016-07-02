/**
 * Copyright (c) 2012-2015 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : DeviceTool.java
 * @ProjectName : UnicarGUI
 * @PakageName : com.unisound.unicar.gui.utils
 * @Author :
 * @CreateDate : 2014-2-25
 */
package com.colink.zzj.txzassistant.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.provider.Telephony;
import android.provider.Telephony.Sms;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author :
 * @CreateDate : 2014-2-25
 * @ModifiedBy : XD
 * @ModifiedDate: 2015-8-4
 * @Modified:
 */
@SuppressLint("NewApi")
public class DeviceTool {
    public static final String TAG = "DeviceTool";

    private static final String INVALID_IMEI = "000000000000000";

    public static String getDeviceId(Context context) {
        String deviceId = getIMEI(context);
        return (deviceId == null || deviceId.equals("")) ? INVALID_IMEI : deviceId;
    }

    public static String getIMEI(Context context) {
        String imei = "";
        imei =
                ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
                        .getDeviceId();
        if (imei != null && !"".equals(imei) && !imei.equals(INVALID_IMEI)) {
            return imei;
        }

        imei = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        if (imei != null && !"".equals(imei) && !imei.equals(INVALID_IMEI)) {
            return imei;
        }
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info != null) {
            return info.getMacAddress();
        }
        return INVALID_IMEI;

    }

    public static String getMac(Context context) {
        if (context == null) {
            return INVALID_IMEI;
        }
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info != null) {
            return info.getMacAddress();
        }
        return INVALID_IMEI;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";

        try {
            PackageInfo packageInfo =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            versionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return versionName;
    }

    public static String getAppPackageName(Context context) {
        String packageName = "";

        try {
            PackageInfo packageInfo =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            packageName = packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return packageName;
    }



    /**
     * check Apk Exist added by zzj 20151106
     * 
     * @param context
     * @param packageName
     * @return
     */
    @SuppressWarnings("unused")
    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) return false;
        try {
            ApplicationInfo info =
                    context.getPackageManager().getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            Logger.w(TAG, e.toString());
            return false;
        }
    }

    /**
     * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡] XD added
     * 
     * @return
     */
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 
     * @return 10 Android 2.3.3-2.3.7; 13 Android 3.2; 14 Android 4.0; 19 Android 4.4 KitKat
     */
    @SuppressWarnings("deprecation")
    public static int getDeviceSDKVersion() {
        int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
        Logger.d(TAG, "!--->sdkVersion = " + sdkVersion);
        return sdkVersion;
    }

    /**
     * 
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources resources = context.getResources();
        return resources.getDisplayMetrics();
    }

    /**
     * getScreenHight pix XD added 20150820
     * 
     * @param context
     * @return
     */
    public static int getScreenHight(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.heightPixels;
    }

    /**
     * get Screen Width pix XD added 20150820
     * 
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.widthPixels;
    }

    /**
     * get Screen Density XD added 20150820
     * 
     * @param context
     * @return
     */
    public static float getScreenDensity(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.density;
    }

    /**
     * get Status Bar Height XD added 20150831
     * 
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * isScreenLandscape
     * 
     * @param context
     * @return
     */
    public static boolean isScreenLandscape(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Logger.i(TAG, "!--->isScreenLandscape---landscape");
            return true;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Logger.i(TAG, "!--->isScreenLandscape---portrait");
        }
        return false;
    }


    /* < XD added 20150826 for default SMS App Begin */
    /**
     * get Default Sms App Name XD added 20150825
     * 
     * @param context
     * @return
     */
    public static String getDefaultSmsAppName(Context context) {
        String smsApp = Telephony.Sms.getDefaultSmsPackage(context);
        Logger.d(TAG, "!--->getDefaultSmsAppName:" + smsApp);
        return smsApp;
    }

    /**
     * change Default SMS App XD added 20150825
     * 
     * @param context
     * @param packageName
     */
    public static void changeDefaultSMSApp(Context context, String packageName) {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
        context.startActivity(intent);
    }

    /**
     * change GUI To Default Sms App for Android 4.4
     * 
     * @param context
     */
    public static void changeGUIToDefaultSmsApp(Context context) {
        Logger.d(TAG, "!--->changeGUIToDefaultSmsApp-----");
        if (getDeviceSDKVersion() < 19) {
            Logger.d(TAG,
                    "!---> DeviceSDKVersion lesstion than 19, no neeed changeGUIToDefaultSmsApp");
            return;
        }

        String pkgName = context.getPackageName();
        if (!pkgName.equals(DeviceTool.getDefaultSmsAppName(context))) {
            Logger.d(TAG, "!--->UnicarGUI is not default SMS app, change to default SMS app.");
            DeviceTool.changeDefaultSMSApp(context, pkgName);
        }
    }

    /**
     * reset default Sms App for Android 4.4
     * 
     * @param context
     */
    public static void resetDefaultSmsApp(Context context) {
        Logger.d(TAG, "!--->resetDefaultSmsApp-----");
        if (getDeviceSDKVersion() < 19) {
            Logger.d(TAG, "!---> DeviceSDKVersion lesstion than 19, no neeed resetDefaultSmsApp.");
            return;
        }

        String defaultSMSPkgName = "com.android.contacts";
        if (!defaultSMSPkgName.equals(DeviceTool.getDefaultSmsAppName(context))) {
            Logger.d(TAG, "!--->---" + defaultSMSPkgName
                    + " is not default SMS app, change to default SMS app.");
            DeviceTool.changeDefaultSMSApp(context, defaultSMSPkgName);
        }
    }

    /* XD added 20150826 for default SMS App End > */


    /**
     * show Keyboard control
     * 
     * @author xiaodong.he
     * @date 2015-10-27
     * @param editText
     * @param isShow
     */
    public static void showEditTextKeyboard(EditText editText, boolean isShow) {
        Logger.d(TAG, "showKeyboard isShow = " + isShow);
        if (null == editText) {
            return;
        }
        InputMethodManager imm =
                (InputMethodManager) editText.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED); // show
        } else {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0); // hide
        }
    }

}
