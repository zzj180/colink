package com.colink.util;

public interface Constact {
	public static final String CONTENT_CONTACT = "content://cn.yunzhisheng.vui.provider.ContactProvider/contact";
	public static final String CONTENT_PHONE = "content://cn.yunzhisheng.vui.provider.ContactProvider/phone";
	public static final String DEFAULT_NAME = "CARKIT";
	public static final String PLAY_TTS = "com.wanma.action.PLAY_TTS";
	public static final String OVERDOWN = "导入通讯录结束";
	public static final String TTS_KEY = "content";
	public static final String CONTACTS_KEY = "contacts";
	public static final String ISOVER_KEY = "isover";
	public static final String KEY_PRE = "name";
	public static final String BLUETOOTH = "bluetooth";
	public static final int DISCONNECTED = 0;
	public static final int CONNECTED = 1;
	public static final int OUTGOING_CALL = 2;
	public static final int INCOMING_CALL = 3;
	public static final String HFPCONNECT_FEEDBACK = "IB";
	public static final String OUTGOING_FEEDBACK_IR = "IR";
	public static final String OUTGOING_FEEDBACK_IC = "IC";
	public static final String INCOMING_FEEDBACK_ID = "ID";
	public static final String OUTGOING_FEEDBACK_IM = "IM";
	public static final String A2DP_FEEDBACK_IM = "YO";
	public static final String ANSWER_FEEDBACK = "IG";
	public static final String HANDUP_FEEDBACK = "IF";
	public static final String DEVICE_FEEDBACK = "MX";
	public static final String AUTO_FEEDBACK = "MF";
	public static final String DEVICENAME_FEEDBACK = "MM";
	public static final String PIN_FEEDBACK = "MN";
	public static final String A2DP_START = "MB";
	public static final String VERSION = "MW";
	public static final String A2DP_STOP = "MA";
	public static final String HFPUNCONNECT_FEEDBACK = "IA";
	public static final String HFPUNCONNECTTING_FEEDBACK = "IV";
	public static final String PHONEBOOK_PREPARE_FEEDBACK = "PA";
	public static final String PHONEBOOK_DOWNLOAD_FEEDBACK = "PB";
	public static final String PHONEBOOK_COMPLETE_FEEDBACK = "PC";
	public static final String PHONEBOOK_SYN_COMMAND = "AT#PA\r\n";
	public static final String CONNECT_COMMAND = "AT#CC\r\n";
	public static final String DISCONNECT_COMMAND = "AT#CD\r\n";
	public static final String HANDUP_COMMAND = "AT#CG\r\n";
	public static final String CHANGE_COMMAND = "AT#CO\r\n";
	public static final String CALL_ANSWER_COMMAND = "AT#CE\r\n";
	public static final String REDIAL_COMMAND = "AT#CH\r\n";
	public static final String AUTO_CONNECT_COMMAND = "AT#MG\r\n";
	public static final String UNAUTO_CONNECT_COMMAND = "AT#MH\r\n";
	public static final String AT_MM = "AT#MM\r\n";
	public static final String AT_YS= "AT#YS1\r\n";
	public static final String QUERY_VERSION_COMMAND = "AT#MY\r\n";
	public static final String AUTO_CALL_COMMAND = "AT#MP\r\n";
	public static final String UNAUTO_CALL_COMMAND = "AT#MQ\r\n";
	public static final String QUERY_PIN_COMMAND = "AT#MN\r\n";
	public static final String QUERY_AUTO_COMMAND = "AT#MF\r\n";
	public static final String CHANGE_AUDIO_COMMAND = "AT#MK28\r\n";
	public static final String A2DP_PLAY_COMMAND = "AT#MA\r\n";
	public static final String A2DP_STOP_COMMAND = "AT#MC\r\n";
	public static final String A2DP_NEXT_COMMAND = "AT#MD\r\n";
	public static final String A2DP_PREV_COMMAND = "AT#ME\r\n";
	public static final String DTMF_DIAL_PREFIX = "AT#CX";
	public static final String COMMAND_FUFFIX = "\r\n";
	
	public static final String NUMBER_0="0";
	public static final String NUMBER_1="1";
	public static final String NUMBER_2="2";
	public static final String NUMBER_3="3";
	public static final String NUMBER_4="4";
	public static final String NUMBER_5="5";
	public static final String NUMBER_6="6";
	public static final String NUMBER_7="7";
	public static final String NUMBER_8="8";
	public static final String NUMBER_9="9";

	public final String DEVICE_FILE = "/sys/class/btaudio_cls/btaudio/btaudio";
	
	//广播给方案商处理业务
	public static final int HAND_UP_CMD = 1; //挂断电话
	public static final int MAKE_CALL_CMD = 2; //请求拨号，如果蓝牙未连接，弹窗提示
	public static final int SEND_NAVI_CMD = 4; //往地图发消息，翼卡已经处理
	public static final int SPEAKER_DISABLE_CMD = 8;//关闭SPK,
	public static final int SPEAKER_ENABLE_CMD = 16;//开喇叭
	public static final int MAKE_CALL_OTHER_CMD = 32;//拨分机号码
	public static final int GOC_SEND_DTMF = 0xa; //发送dtmf 指令
	public static final int GOC_SEND_RESULT = 0xb; //发送已经解析的数据
	public static final int ECAR_CALL_OTHER_CMD = 0xc;//翼卡拨打分机号码
	//接受蓝牙广播状态
	public static final int BLUE_NOT_CONNECTED = 1;//断开
	public static final int CALL_STATUS_INCOMING = 2;//来电
	public static final int CALL_STATUS_OUTCALL = 3;//拨打电话;
	public static final int CALL_STATUS_HAND_UP = 4;//挂断
	public static final int CALL_STATUS_OUTCALLING = 5;//接通
	public static final int RESUME_VOICE = 6;
	public static final String ECAR_REC_DTMF = "dtmf";//dtmf
	public static final String ECAR_RECEICVER_CMD = "BT_RECEIVE_CMD";//翼卡接收到的广播关键字
	public static final String ECAR_SENDCMD = "BT_SEND_CMD";//翼卡发送广播的命令参数
	public static final String ECAR_CALL_OTHER = "CALL_OTHER";//翼卡拨打分机号码
	public static final String ECAR_CALL_NUMBER = "CALL_PHONE_NUMBER";//翼卡拨打电话号码
	public static final String ECAR_CALL_NAME = "CALL_PHONE_NAME";//翼卡拨打名字
	public static final String ECAR_SEND_ACTION = "com.android.ecar.tnc.send";
	
	public static final String A2DP_SWITCH = "a2dpswitch";
	//翼卡发广播
	public static final String CALL_RECEIVE_ACTION = "com.android.ecar.tnc.rec";
	
	public static final String VERSION_KEY = "version";
	
	public static final String ACTION_BLUE_STATE="com.colink.zzj.action.bluetooth.state";
	public static final String ACTION_BLUE_NAME="com.colink.zzj.action.bluetooth.name";
	public static final String ACTION_DEVICE_ADDRESS="com.colink.zzj.device.address";
	public static final String ACTION_DEVICE_NAME="com.colink.zzj.device.name";
	public static final String ACTION_DEVICE_PIN="com.colink.zzj.device.pin";
	public static final String ACTION_DEVICE_STATE="com.colink.zzj.device.state";
	public static final String ACTION_DEVICE_AUTO="com.colink.zzj.device.auto";
	public static final String ACTION_CONTACT_DONE="com.colink.zzj.contact.done";
	public static final String ACTION_CONTACT_START="com.colink.zzj.contact.start";
	public static final String ECAR_NUM1="075787807155";
	public static final String ECAR_NUM2="4008005005";
	public static final String ECAR_NUM3="4008015170";
	public static final String ECAR_NUM4="075787807160";
	public static final String ECAR_APP="com.coagent.app";
	public static final String ECAR_NEW_APP="com.coagent.ecar";
	public static final String ECAR_SERVICE="com.coagent.server.EcarAppService";
	public static final String ECAR_NEW_SERVICE="com.coagent.ecarnet.car.service.BootService";
	
	public static final String SP_UNICAR_KEY_IS_FIRST_START = "is_first_start";
	public static final String SP_UNICAR_KEY_IS_FIRST_VOICE = "is_first_voice";
	
}
