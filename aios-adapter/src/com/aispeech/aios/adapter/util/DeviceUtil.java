
package com.aispeech.aios.adapter.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.aispeech.aios.adapter.AdapterApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @desc 获取设备的信息
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public final class DeviceUtil {

    private static String TAG = "DeviceUtil";
    /**
     * <p>
     * <b>IMEI.</b>
     * </p>
     * Returns the unique device ID, for example, the IMEI for GSM and the MEID
     * or ESN for CDMA phones. Return null if device ID is not available.
     * <p/>
     * Requires Permission: READ_PHONE_STATE
     *
     * @param context 上下文
     * @return 设备ID
     */
    public synchronized static String getDeviceId(Context context) {
        if (context == null) {
            return "";
        }

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null || TextUtils.isEmpty(tm.getDeviceId())) {
            // 双卡双待需要通过phone1和phone2获取imei，默认取phone1的imei。
            tm = (TelephonyManager) context.getSystemService("phone1");
        }

        String imei = null;
        if (tm != null) {
            imei = tm.getDeviceId();
        }

        if(imei != null) {
            return imei.trim();
        }

        return "";
    }

    /**
     * Returns the serial number of the SIM, if applicable. Return null if it is
     * unavailable.
     * <p/>
     * Requires Permission: READ_PHONE_STATE
     *
     * @param context 上下文
     * @return the serial number of the SIM, if applicable. Return null if it is unavailable.
     */
    public synchronized static String getSimSerialNumber(Context context) {
        if (context == null) {
            return "";
        }
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();
    }

    /**
     * A 64-bit number (as a hex string) that is randomly generated on the
     * device's first boot and should remain constant for the lifetime of the
     * device. (The value may change if a factory reset is performed on the
     * device.)
     *
     * @param context 上下文
     * @return
     */
    public synchronized static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 操作系统版本
     *
     * @return
     */
    public static String getOSversion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 设备商
     *
     * @return
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 设备型号
     *
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 序列号
     *
     * @return
     */
    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return serial;
    }

    /**
     * SD CARD ID
     *
     * @return
     */
    public static synchronized String getSDcardID() {
        try {
            String sdCid = null;
            String[] memBlkArray = new String[] {
                    "/sys/block/mmcblk0", "/sys/block/mmcblk1", "/sys/block/mmcblk2"
            };
            for (String memBlk : memBlkArray) {
                File file = new File(memBlk);
                if (file.exists() && file.isDirectory()) {
                    Process cmd = Runtime.getRuntime().exec("cat " + memBlk + "/device/cid");
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(cmd.getInputStream()));
                    sdCid = br.readLine();
                    if (!TextUtils.isEmpty(sdCid)) {
                        return sdCid;
                    }
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String syncWithFileCache(Context context, String key, String value) {
        File file = new File(context.getFilesDir(), "." + key);
        if (value == null) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                value = br.readLine().trim();
                if (value.equals("")) {
                    value = null;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        // ignore
                        e.printStackTrace();
                    }
                }
            }
        } else {
            FileOutputStream fo = null;
            try {
                fo = new FileOutputStream(file);
                fo.write(value.getBytes());
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                if (fo != null) {
                    try {
                        fo.close();
                    } catch (IOException e) {
                        // ignore
                        e.printStackTrace();
                    }
                }
            }
        }

        return value;
    }

    private static String memcachedMac = null;
    /**
     * get mac address
     * try to get mac address from WifiManager firstly,
     * if wifi is disabled, enable it firstly
     * then try to get mac address in a loop with timeout 5s
     * @param context 上下文
     * @return MAC地址
     */
    public static String getMac(Context context) {

        String mac = null;
        String filecachedMac= null;

        if (memcachedMac != null) {
            return memcachedMac;
        }

        if (context == null) {
            return null;
        }

        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            if (info != null) {
                mac = info.getMacAddress();
                filecachedMac = syncWithFileCache(context, "mac", mac);
                if (filecachedMac != null) {
                    return filecachedMac;
                }
            }

            // in case of anr
            if (context.getMainLooper().getThread().equals(Thread.currentThread())) {
                return null;
            }

            boolean isWifiEnabled = wifi.isWifiEnabled();
            if (!isWifiEnabled) {
                wifi.setWifiEnabled(true);
                Thread.sleep(5000);
            }

            info = wifi.getConnectionInfo();
            mac = info.getMacAddress();
            if (!isWifiEnabled) {
                wifi.setWifiEnabled(false);
            }

            filecachedMac = syncWithFileCache(context, "mac", mac);

            return filecachedMac;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取屏幕的分辨率
     *
     * @param context
     * @return int array with 2 items. The first item is width, and the second
     *         is height.
     */
    public static int[] getScreenResolution(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        int[] resolution = new int[2];
        resolution[0] = dm.widthPixels;
        resolution[1] = dm.heightPixels;

        return resolution;
    }

    public static float getDeviceDensity(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        return dm.density;
    }

    /**
     * 获取WIFI的Mac地址
     *
     * @param context
     * @return Wifi的BSSID即mac地址
     */
    public static String getWifiBSSID(Context context) {
        if (context == null) {
            return null;
        }

        String mac = null;
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wm.getConnectionInfo();
        if (info != null) {
            mac = info.getBSSID();// 获得本机的MAC地址
        }

        return mac;
    }

    public static String getPackageVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取系统休眠时间。
     *
     * @return
     */
    public static int getScreenOffTimeOut(Context context) {
        int sleepTime;
        try {
            sleepTime = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
            sleepTime = 15 * 1000;
        }
        return sleepTime;
    }

    public static boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     *
     * @return The number of cores, or 1 if failed to get result
     */
    public static int getCPUNumCores() {
        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    // Check if filename is "cpu", followed by a single digit
                    // number
                    return Pattern.matches("cpu[0-9]", pathname.getName());
                }
            });
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * Get the external app cache directory.
     *
     * @param context
     *            The context to use
     * @return The external cache dir
     */
    @TargetApi(8)
    public static File getExternalCacheDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                return context.getExternalCacheDir();
            }
        }

        return null;
    }

    /**
     * Get the external app cache directory.
     *
     * @param context
     *            The context to use
     * @param name
     *            the dir name
     * @return The external cache dir
     */
    @TargetApi(8)
    public static File getExternalCacheDir(Context context, String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                File cacheDir = context.getExternalCacheDir();
                if (cacheDir != null) {
                    if (!cacheDir.exists()) {
                        cacheDir.mkdirs();
                    }
                    if (TextUtils.isEmpty(name)) {
                        return context.getExternalCacheDir();
                    } else {
                        File dir = new File(cacheDir, name);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        return dir;
                    }
                }
            }
        }

        return null;
    }

    /**
     *
     * @param context context
     * @param timeout the max time to wait, senconds
     * @return the absolute path of cache root dir, "" for fail
     */
    public static String getExternalCacheBlocking(Context context, int timeout){
        String cacheroot = "";
        for (int i = 0; i < timeout; i++) {
            File cacheDir = DeviceUtil.getExternalCacheDir(AdapterApplication.getContext());
            if (cacheDir != null) {
                cacheroot = cacheDir.getAbsolutePath();
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        return cacheroot;
    }

    public static int dip2px(Context ctx, int dpValue)
    {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        float scale = metrics.density;



        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context ctx, int pxValue)
    {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        float scale = metrics.density;
        return (int) (pxValue / scale + 0.5f);
    }
}


