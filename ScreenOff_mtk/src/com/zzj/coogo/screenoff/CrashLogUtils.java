package com.zzj.coogo.screenoff;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

public class CrashLogUtils {

	/**
	 * 
	 * @param context
	 * @param ex
	 * @return
	 */
	private static final String LOG_DIR_PATH = Environment.getExternalStorageDirectory().toString() + "/zzj_log/";
	public static void saveLogToFile(Context context, Throwable ex) throws JSONException, UnsupportedEncodingException {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED))
			return;
		File destDir = new File(LOG_DIR_PATH);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		String crashReportStr = getCrashReport(context, ex);
		try {
			//保存名字为 包名 + 时间.
			String path = LOG_DIR_PATH + getAppInfo(context) + timeStr() + ".txt";
			FileWriter fw = new FileWriter(path);
			fw.write(crashReportStr);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getCrashReport(Context context, Throwable ex) {
		StringBuffer exceptionStr = new StringBuffer();
		Writer w = new StringWriter();
		ex.printStackTrace(new PrintWriter(w));
		exceptionStr.append(w.toString());
		return exceptionStr.toString();
	}

	public static String getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private static String getAppInfo(Context context) {
		try {
			String pkName = context.getPackageName();
			String versionName = context.getPackageManager().getPackageInfo(pkName, 0).versionName;
	//		int versionCode = context.getPackageManager().getPackageInfo(pkName, 0).versionCode;
			return pkName + "_"+versionName;
		} catch (Exception e) {
		}
		return null;
	}

	public static String timeStr() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-MM-SS");//设置日期格式
		return df.format(new Date());
	}
}
