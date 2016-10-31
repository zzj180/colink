package com.colink.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;
import java.util.TreeSet;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.Build;

public class CrashHandler implements UncaughtExceptionHandler {

	/** Debug Log tag */
	/**
	 * 是否开启日志输出,在Debug状态下开启, 在Release状态下关闭以提示程序性能
	 * */
	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/** CrashHandler实例 */
	private static CrashHandler INSTANCE;
	/** 程序的Context对象 */
	private Context mContext;
	/** 使用Properties来保存设备的信息和错误堆栈信息 */
	private Properties mDeviceCrashInfo = new Properties();
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
	private static final String STACK_TRACE = "STACK_TRACE";
	/** 错误报告文件的扩展名 */
	private static final String CRASH_REPORTER_EXTENSION = ".cr";

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CrashHandler();
		}
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Application.getInstance().closeSerialPort();
		setUnMute();
		try {
			CrashLogUtils.saveLogToFile(mContext, ex);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		}
		/*if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			// Sleep一会后结束程序
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			Process.killProcess(Process.myPid());
			SharedPreferences sp = mContext.getSharedPreferences(
					Constact.BLUETOOTH, Context.MODE_PRIVATE);
			final int errorCount = sp.getInt("errorCount", 0);
			if (errorCount < 5) {
				sp.edit().putInt(Constact.BLUETOOTH, errorCount + 1).apply();
			} else {
				sp.edit().putInt(Constact.BLUETOOTH, 0).apply();
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					if (errorCount < 5)
						mContext.startService(new Intent(
								"cn.colink.service.Telphone_Service"));
				}
			}).start();
		}*/
	}

	private void setUnMute() {
		AudioManager audioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamMute(1, false);
		audioManager.setStreamMute(2, false);
		audioManager.setStreamMute(3, false);
		audioManager.setStreamMute(4, false);
		audioManager.setStreamMute(5, false);
		audioManager.setStreamMute(6, false);
		audioManager.setStreamMute(7, false);
		audioManager.setStreamMute(8, false);
		audioManager.setStreamMute(9, false);
		Intent intent = new Intent("com.coogo.BLUETOOTH_PHONE_STATE");
		intent.putExtra("heedset", true);
		mContext.sendBroadcast(intent);
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		final String msg = ex.getLocalizedMessage();
		if (msg == null) {
			return false;
		}
		// 收集设备信息
		collectCrashDeviceInfo(mContext);
		// 保存错误报告文件
		saveCrashInfoToFile(ex);
		// 发送错误报告到服务器
		// sendCrashReportsToServer(mContext);
		return true;
	}

	/**
	 * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
	 */
	public void sendPreviousReportsToServer() {
		sendCrashReportsToServer(mContext);
	}

	/**
	 * 把错误报告发送给服务器,包含新产生的和以前没发送的.
	 * 
	 * @param ctx
	 */
	private void sendCrashReportsToServer(Context ctx) {
		String[] crFiles = getCrashReportFiles(ctx);
		if (crFiles != null && crFiles.length > 0) {
			TreeSet<String> sortedFiles = new TreeSet<String>();
			sortedFiles.addAll(Arrays.asList(crFiles));
			for (String fileName : sortedFiles) {
				File cr = new File(ctx.getFilesDir(), fileName);
				postReport(cr);
				cr.delete();// 删除已发送的报告
			}
		}
	}

	private void postReport(File file) {
		// TODO 发送错误报告到服务器
	}

	/**
	 * 获取错误报告文件名
	 * 
	 * @param ctx
	 * @return
	 */
	private String[] getCrashReportFiles(Context ctx) {
		File filesDir = ctx.getFilesDir();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(CRASH_REPORTER_EXTENSION);
			}
		};
		return filesDir.list(filter);
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return
	 */
	private String saveCrashInfoToFile(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		String result = info.toString();
		printWriter.close();
		mDeviceCrashInfo.put("EXEPTION", ex.getLocalizedMessage());
		mDeviceCrashInfo.put(STACK_TRACE, result);
		try {
			FileOutputStream trace = mContext.openFileOutput(
					mContext.getPackageName(), Context.MODE_PRIVATE);
			mDeviceCrashInfo.store(trace, "");
			trace.flush();
			trace.close();
			return mContext.getPackageName();
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 收集程序崩溃的设备信息
	 * 
	 * @param ctx
	 */
	public void collectCrashDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				mDeviceCrashInfo.put(VERSION_NAME,
						pi.versionName == null ? "not set" : pi.versionName);
				mDeviceCrashInfo.put(VERSION_CODE, "" + pi.versionCode);
			}
		} catch (NameNotFoundException e) {
		}
		// 使用反射来收集设备信息.在Build类中包含各种设备信息,
		// 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
		// 具体信息请参考后面的截图
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mDeviceCrashInfo.put(field.getName(), "" + field.get(null));
			} catch (Exception e) {
			}
		}
	}

}