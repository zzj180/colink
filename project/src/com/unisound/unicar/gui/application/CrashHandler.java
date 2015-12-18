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

import cn.yunzhisheng.vui.assistant.WindowService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

/**
 * 自定义系统的Crash捕捉类
 * 将软件版本信息，设备信息，出错信息保存在SD卡中 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "CrashHandler";
	private Context mContext;
	private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().toString();
	private static CrashHandler mInstance = new CrashHandler();
	private static UncaughtExceptionHandler mDefaultHandler;
	
	
	private CrashHandler(){}
	/**
	 * 单例模式，保证只有一个CrashHandler实例存在
	 * @return
	 */
	public static CrashHandler getInstance(){
		 mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();   
		return mInstance;
	}

	/**
	 * 异常发生时，系统回调的函数，我们在这里处理一些操作
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		//将一些信息保存到SDcard中
		if (!handleException(ex) && mDefaultHandler != null) {   
            //如果用户没有处理则让系统默认的异常处理器来处理   
            mDefaultHandler.uncaughtException(thread, ex);   
        } else {   
            //Sleep一会后结束程序   
            try {   
                Thread.sleep(3000);   
            } catch (InterruptedException e) {   
            }   
            Process.killProcess(Process.myPid());
            new Thread(new Runnable() {
				
				@Override
				public void run() {
					Intent windowService = new Intent(mContext, WindowService.class);
					windowService.setAction(WindowService.ACTION_START_REQUEST_MAKE_FINISHED);
					mContext.startService(windowService);
				}
			}).start();
        }   
	}
	
	   /**  
	    * 自定义错误处理,收集错误信息  
	    * 发送错误报告等操作均在此完成.  
	    * 开发者可以根据自己的情况来自定义异常处理逻辑  
	    * @param ex  
	    * @return true:如果处理了该异常信息;否则返回false  
	    */   
	    private boolean handleException(Throwable ex) {   
	        if (ex == null) {   
	            return false;   
	        }   
	        final String msg = ex.getLocalizedMessage();   
	        if(msg == null) {  
	            return false;  
	        }  
	        //保存错误报告文件   
	//        savaInfoToSD(mContext, ex);
	        //sendCrashReportsToServer(mContext);   
	        return true;   
	    }   
	
	/**
	 * 为我们的应用程序设置自定义Crash处理
	 */
	public void setCrashHanler(Context context){
		mContext = context;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	/**
	 * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
	 * @param context
	 * @return
	 */
	private HashMap<String, String> obtainSimpleInfo(Context context){
		HashMap<String, String> map = new HashMap<String, String>();
		PackageManager mPackageManager = context.getPackageManager();
		PackageInfo mPackageInfo = null;
		try {
			mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		map.put("versionName", mPackageInfo.versionName);
		map.put("versionCode", "" + mPackageInfo.versionCode);
		
		map.put("MODEL", "" + Build.MODEL);
		map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
		map.put("PRODUCT", "" +  Build.PRODUCT);
		
		return map;
	}
	
	
	/**
	 * 获取系统未捕捉的错误信息
	 * @param throwable
	 * @return
	 */
	private String obtainExceptionInfo(Throwable throwable) {
		StringWriter mStringWriter = new StringWriter();
		PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
		throwable.printStackTrace(mPrintWriter);
		mPrintWriter.close();
		
		Log.e(TAG, mStringWriter.toString());
		return mStringWriter.toString();
	}
	
	/**
	 * 保存获取的 软件信息，设备信息和出错信息保存在SDcard中
	 * @param context
	 * @param ex
	 * @return
	 */
	public String savaInfoToSD(Context context, Throwable ex){
		String fileName = null;
		StringBuffer sb = new StringBuffer();
		
		for (Map.Entry<String, String> entry : obtainSimpleInfo(context).entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key).append(" = ").append(value).append("\n");
		}  
		
		sb.append(obtainExceptionInfo(ex));
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File dir = new File(SDCARD_ROOT + File.separator + "YunZhiSheng" + File.separator + "crash" + File.separator + "uniCarSolution" + File.separator);
			if(! dir.exists()){
				dir.mkdirs();
			}
			
			try{
				fileName = dir.toString() + File.separator + paserTime(System.currentTimeMillis()) + ".log";
				FileOutputStream fos = new FileOutputStream(fileName);
				fos.write(sb.toString().getBytes());
				fos.flush();
				fos.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		return fileName;
		
	}
	
	
	/**
	 * 将毫秒数转换成yyyy-MM-dd-HH-mm-ss的格式
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