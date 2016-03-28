/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.colink.application;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android_serialport_api.SerialPort;

@SuppressLint("NewApi")
public class Application extends android.app.Application {

	private static Application instance;
	private SerialPort mSerialPort = null;

	public static String device_address;
	public static String blueTooth_name;
	public static String pin_value;
	public static boolean auto_conn = true;
	public static boolean auto_call = false;
	public static int state;

//	public static MainActivity mainActivity;

//	public static SettingActivity settingActivity;

//	public static CallLogActivity callLogActivity;

//	public static ContactActivity contactActivity;

//	public static MusicActivity musicActivity;

	public SerialPort getSerialPort() throws SecurityException, IOException,InvalidParameterException {
		if (mSerialPort == null) {
		//	mSerialPort = new SerialPort("/dev/ttyS1", 9600, 0);
		//	mSerialPort = new SerialPort("/dev/ttyS2", 9600, 0);
			mSerialPort = new SerialPort("/dev/ttyMT1", 9600, 0);
		}
		return mSerialPort;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		changeMetrics(getBaseContext());
		instance = this;
	//	CrashHandler crashHandler = CrashHandler.getInstance();
	//	crashHandler.init(getApplicationContext());
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}

	/*// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void removeActivity(Activity activity) {
		activityList.remove(activity);
	}
*/
	public static Application getInstance() {
		if (null == instance) {
			instance = new Application();
		}
		return instance;
	}

	// 遍历所有Activity并finish
/*	public void exit() {

		if (dialActivity != null)
			dialActivity.finish();
		if (settingActivity != null)
			settingActivity.finish();
		if (callLogActivity != null)
			callLogActivity.finish();
		if (contactActivity != null)
			contactActivity.finish();
		if (mainActivity != null)
			mainActivity.finish();
	}*/

	/*public boolean isExist(Activity activity) {

		return activityList.indexOf(activity) != -1;

	}*/


	//
	private static final boolean DebugFlag = false;

	// 修改屏幕Density
	public static void changeMetrics(Context context) {

		DisplayMetrics curMetrics = context.getResources().getDisplayMetrics();

		if (!DebugFlag) {

			if (curMetrics.densityDpi == DisplayMetrics.DENSITY_HIGH) {

				DisplayMetrics metrics = new DisplayMetrics();

				metrics.scaledDensity = 1.0f;

				metrics.density = 1.0f;

				metrics.densityDpi = DisplayMetrics.DENSITY_MEDIUM;

				metrics.xdpi = DisplayMetrics.DENSITY_MEDIUM;

				metrics.ydpi = DisplayMetrics.DENSITY_MEDIUM;

				metrics.heightPixels = curMetrics.heightPixels;

				metrics.widthPixels = curMetrics.widthPixels;

				context.getResources().getDisplayMetrics().setTo(metrics);

			}

		} else {
			DisplayMetrics metrics = new DisplayMetrics();
			metrics.scaledDensity = (float) (130 / 160.0);
			metrics.density = (float) (130 / 160.0);
			metrics.densityDpi = 130;
			metrics.xdpi = 130;
			metrics.ydpi = 130;
			metrics.heightPixels = curMetrics.heightPixels;
			metrics.widthPixels = curMetrics.widthPixels;
			context.getResources().getDisplayMetrics().setTo(metrics);

		}

	}

}
