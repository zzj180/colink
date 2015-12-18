package com.unisound.unicar.gui.utils;

public class GUIConfig {

	
	public static final int TIME_AUTO_SHOW_HELP_TEXT = 3000;//3s
	
	public static final int TIME_DELAY_AUTO_CONFIRM = 5000;//5s
	
	public static final int TIME_DELAY_DISMISS_VIEW_ON_TTS_END = 5000;//5s

	public static final long TIME_MIC_VOLUME_UPDATE = 90; //
	
	//Function help
	public static final String ACTION_FUNCTION_HELP = "com.unisound.unicar.ACTION_FUNCTION_HELP";
	public static final String KEY_FUNCTION_HELP_TITLE = "FUNCTION_HELP_TITLE";
	public static final String KEY_FUNCTION_HELP_TYPE = "FUNCTION_HELP_TYPE";
	
	public static final int VALUE_FUNCTION_HELP_TYPE_BLUETOOTH = 1;
	public static final int VALUE_FUNCTION_HELP_TYPE_NAVIGATION = 2;
	public static final int VALUE_FUNCTION_HELP_TYPE_MUSIC = 3;
	public static final int VALUE_FUNCTION_HELP_TYPE_SETTING = 4;
	public static final int VALUE_FUNCTION_HELP_TYPE_RADIO = 5;
	public static final int VALUE_FUNCTION_HELP_TYPE_WEATHER = 6;
	public static final int VALUE_FUNCTION_HELP_TYPE_STOCK = 7;
	public static final int VALUE_FUNCTION_HELP_TYPE_LOCAL_SEARCH = 8;
	public static final int VALUE_FUNCTION_HELP_TYPE_TRAFFIC = 9;
	public static final int VALUE_FUNCTION_HELP_TYPE_LIMIT = 10;
	public static final int VALUE_FUNCTION_HELP_TYPE_WAKEUP = 11;
	
	//SMS
	public static final String ACTION_SMS_SEND_SUCCESS = "com.unisound.unicar.ACTION_SMS_SEND_SUCCESS";
	public static final String ACTION_SMS_SEND_FAIL = "com.unisound.unicar.ACTION_SMS_SEND_FAIL";
	
	public static final int SMS_STATUS_SENDING = 0;
	public static final int SMS_STATUS_SEND_SUCCESS = 1;
	public static final int SMS_STATUS_SEND_FAIL = 2;
	
	//Setting activity
	public static final int ACTIVITY_REQUEST_CODE_CHOOSE_MAP = 1001;
	public static final int ACTIVITY_RESULT_CODE_SETTING_MAP_FINISH  = 2001;
	
	//UI display configuration
	/** GUI Configuration: is Show ASR Record Result*/
	public static final boolean isShowASRRecordResult = true;
	
	/** GUI Configuration: is Show SMS send Confirm Timer*/
	public static final boolean isShowSmsConfirmTimer = false;
	
	/** GUI Configuration: is Show incoming SMS Reply button */
	public static final boolean isShowSmsReplyButton = false;
	
	//UI function configuration
	/** GUI Configuration: is allow UniCarGUI request as Android 4.4 default SMS App */
	public static final boolean isAllowGUIRequestAsDefaultSmsApp = false;
	
	/**
	 * XD 20150811 added for bluetooth call
	 * LandSem: "com.ls.bluetooth.bluetoothphone.DialpadActivity"
	 */
	public static final String ACTIVITY_NAME_BLUETOOTH_CALL = "com.ls.bluetooth.bluetoothphone.DialpadActivity";
	
	/** UniDrive VUI package name */ 
	//delete by zzj 20150916
//	public static final String PACKAGE_NAME_UNICAR_VUI = "com.unisound.unicar.framework";
	//add by zzj 20150916
	public static final String PACKAGE_NAME_UNICAR_VUI = "inet.framework.car";
	
}
