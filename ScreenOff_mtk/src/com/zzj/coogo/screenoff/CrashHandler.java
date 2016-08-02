/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName	: CrashHandler.java
 * @ProjectName	: vui_voicetv_mobile
 * @PakageName	: cn.yunzhisheng.voicetv.mobile
 * @Author		: Conquer
 * @CreateDate	: 2014-3-19
 */
package com.zzj.coogo.screenoff;

import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;

import org.json.JSONException;

import android.content.Context;

/**
 * @Module		: 隶属模块名
 * @Comments	: 描述
 * @Modified: 
 * 2014-3-19: 实现基本功能
 */
public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";

	//Log文件
	private static CrashHandler crashHandler;
	
	private Thread.UncaughtExceptionHandler defaultExceptionHandler;
	
	private Context mContext;
	private CrashHandler(){
	}
	
	public static CrashHandler getInstance(){
		if(crashHandler == null){
			crashHandler = new CrashHandler();
		}
		return crashHandler;
	}
	
	public void init(Context context){
		//获取系统默认的异常处理器
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		//将当前实例设为系统默认的异常处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
		mContext = context.getApplicationContext();
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		/*ex.printStackTrace();
		
		//如果系统提供了默认的异常处理器，则交给系统去结束程序，否则自己做
		if(!handleException(ex) &&defaultExceptionHandler != null){
			defaultExceptionHandler.uncaughtException(thread, ex);
		}else{
			Process.killProcess(Process.myPid());
		}
		Intent talkService = new Intent(mContext, BNRService.class);
		mContext.startService(talkService);
		Intent windowService = new Intent(mContext, SwitchServeice.class);
		mContext.startService(windowService);*/
		
		try {
			CrashLogUtils.saveLogToFile(mContext, ex);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		defaultExceptionHandler.uncaughtException(thread, ex);
	}
	/*private boolean handleException(Throwable ex) {

		if (ex == null) {
			return false;
		}
		final String msg = ex.getLocalizedMessage();
		if (msg == null) {
			return false;
		}
		return true;
	}*/
	
}
