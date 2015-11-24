/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : SettingSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 */
package com.unisound.unicar.gui.session;

import java.lang.reflect.Method;
import java.util.List;

import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.oem.RomControl;
import com.unisound.unicar.gui.oem.RomCustomerProcessing;
import com.unisound.unicar.gui.oem.RomSystemSetting;
import com.unisound.unicar.gui.phone.Telephony;
import com.unisound.unicar.gui.preference.CommandPreference;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.route.operation.AMapUri;
import com.unisound.unicar.gui.route.operation.GaodeUriApi;
import com.unisound.unicar.gui.ui.MessageSender;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.MingPianView;
import com.unisound.unicar.gui.view.NoPerSonContentView;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-6
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-6
 * @Modified: 2013-9-6: 实现基本功能
 */
public class SettingSession extends BaseSession {
	private static final String ACTION_COOGO_FEEDBACK = "action.coogo.feedback";
	public static final String COM_GLSX_BOOTUP_RECEIVE_AUTONAVI = "com.glsx.bootup.receive.autonavi";
	private final static String ONE_NAVI= "ONE_NAVI";
	private static final String ACTION_GET_TRAFFIC = "com.wanma.action.GET_TRAFFIC";

	public static final String BAIDU_NAVI = "com.baidu.navi";

	public static final String AUTONAVI_MINIMAP = "com.autonavi.minimap";
	
	public static final String KLD_MAP = "cld.navi.c2739.mainframe";

	private static final String STOP_FM = "action.colink.stopFM";

	public static final String AMAP_STOPNAVI = "com.amap.stopnavi";

	public static final String BAIDU_NAVI_QUITNAVI = "com.baidu.navi.quitnavi";

	private static final String BDNAVI_LAUNCH = "bdnavi://launch?";

	private static final String BDNAVI_GOCOMPANY = "bdnavi://gocompany?";

	private static final String AMAP_GOTOCOMPANY = "com.amap.gotocompany";

	private static final String BDNAVI_GOHOME = "bdnavi://gohome?";

	private static final String AMAP_GOTOHOME = "com.amap.gotohome";

	private static final String ACTIVITY_MANAGER = "android.app.ActivityManager";

	private static final String COM_ANDROID_CAMERA_CAMERA_LAUNCHER = "com.android.camera.CameraLauncher";

	private static final String COM_ANDROID_CAMERA_CAMERA_ACTIVITY = "com.android.camera.CameraActivity";

	private static final String COM_ANDROID_CAMERA2 = "com.android.camera2";

	private static final String COM_WANMA_ACTION_EDOG_OFF = "com.wanma.action.EDOG_OFF";

	private static final String COM_WANMA_ACTION_EDOG_ON = "com.wanma.action.EDOG_ON";

	public static final String TAG = "SettingSession";

	private MessageSender mMessageSender;
	String operator = null;
	String operands = null;
	/**
	 * @Author : Dancindream
	 * @CreateDate : 2013-9-6
	 * @param context
	 * @param sessionManagerHandler
	 */
	public SettingSession(Context context, Handler sessionManagerHandler) {
		super(context, sessionManagerHandler);
		mMessageSender = new MessageSender(context);
	}

	public void putProtocol(JSONObject jsonProtocol) {
		super.putProtocol(jsonProtocol);

		operator = JsonTool.getJsonValue(mDataObject, "operator", "");
		operands = JsonTool.getJsonValue(mDataObject, "operands", "");
		if (SessionPreference.VALUE_SETTING_OBJ_QUESTION.equals(operands)) { 
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.question_answer1);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
				mAnswer = mContext.getString(R.string.question_answer2);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_TOTAL.equals(operator)){
				String[] array = mContext.getResources().getStringArray(R.array.holf);
				int len=array.length;
				int r=(int) (Math.random()*len);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse( mContext.getString(R.string.question_total), R.anim.mingpian);
				addAnswerView(mingPianView);
				mAnswer = "     野狼战队有成员17名，分别是"+array[r%len]+","+array[(r+1)%len]+","+array[(r+2)%len]+"等等";
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_SENLIN.equals(operator)){
				mAnswer = mContext.getString(R.string.question_sll);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.wjh);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_YE.equals(operator)){
				mAnswer = mContext.getString(R.string.question_yl);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.qsc);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_ZHAN.equals(operator)){
				mAnswer = mContext.getString(R.string.question_zl);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.zl);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_CANG.equals(operator)){
				mAnswer = mContext.getString(R.string.question_cl);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.cl);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_TU.equals(operator)){
				mAnswer = mContext.getString(R.string.question_tl);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.tul);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_TOU.equals(operator)){
				mAnswer = mContext.getString(R.string.question_toul);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.tl);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_HEI.equals(operator)){
				mAnswer = mContext.getString(R.string.question_hl);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.hl);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_XUE.equals(operator)){
				mAnswer = mContext.getString(R.string.question_xl);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.xl);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_FENGHUO.equals(operator)){
				mAnswer = mContext.getString(R.string.question_fhl);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.bb);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_JI.equals(operator)){
				mAnswer = mContext.getString(R.string.question_jl);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.jl);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_SHUAI.equals(operator)){
				mAnswer = mContext.getString(R.string.question_sl);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.sl);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_ER.equals(operator)){
				mAnswer = mContext.getString(R.string.question_el);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.el);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_YIN.equals(operator)){
				mAnswer = mContext.getString(R.string.question_yinl);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.yinl);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_CAI.equals(operator)){
				mAnswer = mContext.getString(R.string.question_cail);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.cail);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_CHI.equals(operator)){
				mAnswer = mContext.getString(R.string.question_chil);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.chil);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_QINHAI.equals(operator)){
				mAnswer = mContext.getString(R.string.question_qhl);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.qhl);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}else if(SessionPreference.VALUE_SETTING_ACT_XIAO.equals(operator)){
				mAnswer = mContext.getString(R.string.question_xiaol);
				MingPianView mingPianView=new MingPianView(mContext);
				mingPianView.setResouse(mAnswer, R.drawable.xl);
				addAnswerView(mingPianView);
				RomCustomerProcessing.playtts((WindowService) mContext, mAnswer, RomCustomerProcessing.TTS_END_RECOGNIER_START);
			}
				
		}
		Logger.d(TAG, "!--->putProtocol:---operator = "+operator+"; operands = "+operands);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onTTSEnd() {
		Logger.d(TAG, "onTTSEnd");
		super.onTTSEnd();
//		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
		
		if (TextUtils.isEmpty(mAnswer)) {
			mAnswer = mQuestion;
		}
		if (SessionPreference.VALUE_SETTING_OBJ_QUESTION.equals(operands)){
			return;
		}
		/*< XD 20150717 delete begin */
		if (SessionPreference.VALUE_SETTING_OBJ_CALL.equals(operands)) {		//电话
			Logger.d(TAG, "!--->VALUE_SETTING_OBJ_CALL---operator = "+operator);
			if (SessionPreference.VALUE_SETTING_ACT_ANSWER.equals(operator)) {
				mAnswer = mContext.getString(R.string.answer_call);
				RomSystemSetting.RomCustomAnswerCall(mContext);
				Telephony.answerRingingCall(mContext);
			} else if (SessionPreference.VALUE_SETTING_ACT_HANG_UP.equals(operator)) {
				mAnswer = mContext.getString(R.string.end_call);
				RomSystemSetting.RomCustomHANGUP(mContext);
				Telephony.endCall(mContext);
			}else if (SessionPreference.VALUE_SETTING_ACT_REDIAL.equals(operator)) {
				RomSystemSetting.RomCustomReDial(mContext);
			}    
		} 
		/* XD 20150717 delete End >*/
		else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_SHUFFLE_PLAYBACK.equals(operands)) {   //随机播放
			if (SessionPreference.VALUE_SETTING_ACT_CANCEL.equals(operator)) {
				mAnswer = mContext.getString(R.string.cancel_random_play);		
			} else if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.random_play);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDSHUFFLE_PLAYBACK);
				mMessageSender.sendMessage(intent, null);

			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_ORDER_PLAYBACK.equals(operands)) {		//顺序播放
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.sequence_play);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDORDER_PLAYBACK);
				mMessageSender.sendMessage(intent, null);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_SINGLE_CYCLE.equals(operands)) {		//单曲循环
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.single_cycle);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDSINGLE_CYCLE);
				mMessageSender.sendMessage(intent, null);

			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_LIST_CYCLE.equals(operands)) {		//列表循环
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.list_cycle);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDFULL_CYCLE);
				mMessageSender.sendMessage(intent, null);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_FULL_CYCLE.equals(operands)) {		//全部循环
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				mAnswer = mContext.getString(R.string.full_cycle);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDFULL_CYCLE);
				mMessageSender.sendMessage(intent, null);
			}
		} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE_DESK_LYRIC.equals(operator)) {		//关闭桌面歌词
			mAnswer = mContext.getString(R.string.close_desk_lyric);
			Intent intent = new Intent(CommandPreference.SERVICECMD);
			intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDCLOSE_DESK_LYRIC);
			mMessageSender.sendMessage(intent, null);
		} else if (SessionPreference.VALUE_SETTING_ACT_OPEN_DESK_LYRIC.equals(operator)) {		//打开桌面歌词
			mAnswer = mContext.getString(R.string.open_desk_lyric);
			Intent intent = new Intent(CommandPreference.SERVICECMD);
			intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDOPEN_DESK_LYRIC);
			mMessageSender.sendMessage(intent, null);
		} else if (SessionPreference.VALUE_SETTING_OBJ_EDOG.equals(operands)) {			//电子狗开关
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				Intent intent = new Intent();
				intent.setAction(COM_WANMA_ACTION_EDOG_ON);
				mContext.sendBroadcast(intent);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				Intent intent2 = new Intent();
				intent2.setAction(COM_WANMA_ACTION_EDOG_OFF);
				mContext.sendBroadcast(intent2);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_FM.equals(operands)) {		//关闭FM
			if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mContext.sendBroadcast(new Intent(STOP_FM));
				mAnswer = mContext.getString(R.string.close_fm);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_CARCORDER.equals(operands)) {		//行车记录仪	
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_carcoder);
				RomControl.enterControl(mContext, RomControl.ROM_APP_LAUNCH,COM_ANDROID_CAMERA2,COM_ANDROID_CAMERA_CAMERA_ACTIVITY);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				List<RunningTaskInfo> tasksInfo = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
				// 应用程序位于堆栈的顶层
				if (tasksInfo.size() > 0) {
					if (COM_ANDROID_CAMERA_CAMERA_LAUNCHER.equals(tasksInfo.get(0).topActivity.getClassName())
							|| COM_ANDROID_CAMERA_CAMERA_ACTIVITY.equals(tasksInfo.get(0).topActivity.getClassName())) {
						Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
						mHomeIntent.addCategory(Intent.CATEGORY_HOME);
						mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
								| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						mContext.startActivity(mHomeIntent);
					}
				}
				mAnswer = mContext.getString(R.string.close_carcoder);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_HOME.equals(operands)||SessionPreference.VALUE_SETTING_OBJ_HOMEPAGE.equals(operands)) {		//返回主界面
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)||SessionPreference.VALUE_SETTING_ACT_SET.equals(operator)) {
				mAnswer = mContext.getString(R.string.return_home);
				Intent mHomeIntent = new Intent(Intent.ACTION_MAIN , null);                        
				mHomeIntent.addCategory(Intent.CATEGORY_HOME);
				mHomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				mContext.startActivity(mHomeIntent);
				Intent intent=new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
				intent.putExtra("reason", "homekey");
				mContext.sendBroadcast(intent);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_CALL_LOG.equals(operands)) {		//打开通话记录
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_calllog);
				RomSystemSetting.openCallLog(mContext);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_NAVI.equals(operands)) {			//导航
			if (SessionPreference.VALUE_SETTING_ACT_GO_BACK_HOME.equals(operator)) {
				mAnswer = mContext.getString(R.string.go_home);
				// Toast.makeText(mContext, jsonProtocol.toString(),
				// Toast.LENGTH_SHORT).show();
				int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
				switch (mapIndex) {
				case 1:
					mContext.sendStickyBroadcast(new Intent(AMAP_GOTOHOME));
					break;
				case 2:
					AMapUri.openBaiduNavi(mContext, BDNAVI_GOHOME);
					break;
				default:
					break;
				}

			} else if (SessionPreference.VALUE_SETTING_ACT_GO_BACK_COMPANY.equals(operator)) {
				mAnswer = mContext.getString(R.string.go_company);
				int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
				switch (mapIndex) {
				case 1:
					mContext.sendStickyBroadcast(new Intent(AMAP_GOTOCOMPANY));
					break;
				case 2:
					AMapUri.openBaiduNavi(mContext, BDNAVI_GOCOMPANY);
					break;
				default:
					break;
				}

			} else if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = "为您打开导航";
				// GaodeMap.openAMapClient(mContext);
				int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
				switch (mapIndex) {
				case 1:
					GaodeUriApi.openAMap(mContext);
					break;
				case 3:
					try {
						PackageManager packageManager = mContext.getPackageManager();
						Intent intent = packageManager.getLaunchIntentForPackage(KLD_MAP);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.startActivity(intent);
					} catch (Exception e) {
					}
					break;
				default:
					AMapUri.openBaiduNavi(mContext, BDNAVI_LAUNCH);
					break;
				}

			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = "为您关闭导航";
				try {
					mContext.sendBroadcast(new Intent(BAIDU_NAVI_QUITNAVI));
					mContext.sendBroadcast(new Intent(AMAP_STOPNAVI));
					final Context context=mContext.getApplicationContext();
					mSessionManagerHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							try {
								forceStopPackage(AUTONAVI_MINIMAP, context);
							} catch (Exception e) {
     							e.printStackTrace();
							}
							try {
								forceStopPackage(BAIDU_NAVI, context);
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								forceStopPackage(KLD_MAP, context);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, 1000);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(SessionPreference.VALUE_SETTING_ACT_PHONE_NAVI.equals(operator)){			//一键导航/翼卡在线
				int navi = Settings.System.getInt(mContext.getContentResolver(), ONE_NAVI, 0);
				switch (navi) {
				case 1:
					ComponentName componetName = new
					ComponentName("com.coagent.app","com.coagent.activity.MainActivity");
					Intent ecar = new Intent();
					ecar.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ecar.setComponent(componetName);
					try {
						mContext.startActivity(ecar);
					} catch (Exception e) {
					}
					break;
				default:
					Intent intent = new Intent(COM_GLSX_BOOTUP_RECEIVE_AUTONAVI); 
					intent.putExtra("autonaviType", 1);  // autonaviType为1：表示直接发起导航请求， 	  // autonaviType为2：只进入导航主页面（不发起请求）;
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try {
						mContext.startActivity(intent);
					} catch (Exception e) {
					}
					break;
				}
			} else if (SessionPreference.VALUE_SETTING_ACT_OPEN_PLAY.equals(operator)) {
				int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
				switch (mapIndex) {
				case 1:
					mContext.sendStickyBroadcast(new Intent("com.amap.openroadradio"));
					break;
				default:
					break;
				}

			}else if (SessionPreference.VALUE_SETTING_ACT_CLOSE_PLAY.equals(operator)) {
				int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
				switch (mapIndex) {
				case 1:
					mContext.sendStickyBroadcast(new Intent("com.amap.closeroadradio"));
					break;
				default:
					break;
				}
			}else if (SessionPreference.VALUE_SETTING_ACT_ZOOMIN.equals(operator)) {		//放大地图	
				int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
				switch (mapIndex) {
				case 1:
					mContext.sendStickyBroadcast(new Intent("com.amap.zoomoutmap"));
					break;
				default:
					break;
				}
			}else if (SessionPreference.VALUE_SETTING_ACT_ZOOMOUT.equals(operator)) {		//缩小地图
				int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
				switch (mapIndex) {
				case 1:
					mContext.sendStickyBroadcast(new Intent("com.amap.zoominmap"));
					break;
				default:
					break;
				}
			}else if (SessionPreference.VALUE_SETTING_ACT_CARUP.equals(operator)) {
				int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
				switch (mapIndex) {
				case 1:
					mContext.sendStickyBroadcast(new Intent("com.amap.carup"));
					break;
				default:
					break;
				}
			}else if (SessionPreference.VALUE_SETTING_ACT_NORTHUP.equals(operator)) {
				int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
				switch (mapIndex) {
				case 1:
					mContext.sendStickyBroadcast(new Intent("com.amap.northup"));
					break;
				default:
					break;
				}
			}else if (SessionPreference.VALUE_SETTING_ACT_2D.equals(operator)) {
				int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
				switch (mapIndex) {
				case 1:
					mContext.sendStickyBroadcast(new Intent("com.amap.2d"));
					break;
				default:
					break;
				}
			}else if (SessionPreference.VALUE_SETTING_ACT_3D.equals(operator)) {
				int mapIndex = UserPerferenceUtil.getMapChoose(mContext);
				switch (mapIndex) {
				case 1:
					mContext.sendStickyBroadcast(new Intent("com.amap.3d"));
					break;
				default:
					break;
				}
			}
		}else if (SessionPreference.VALUE_SETTING_OBJ_MEMORY.equals(operands)) {	//清除内存
			if (SessionPreference.VALUE_SETTING_ACT_CLEAR.equals(operator)) {
				Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
				mHomeIntent.addCategory(Intent.CATEGORY_HOME);
				mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				mContext.startActivity(mHomeIntent);
				final ApplicationInfo info = mContext.getApplicationInfo();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						ActivityManager activityManger = (ActivityManager) mContext
								.getSystemService(Context.ACTIVITY_SERVICE);
						List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();
						if (list != null)
							for (int i = 0; i < list.size(); i++) {
								ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
								String[] pkgList = apinfo.pkgList;
								if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
									for (int j = 0; j < pkgList.length; j++) {
										if (!pkgList[j].equals(info.packageName))
											activityManger.killBackgroundProcesses(pkgList[j]);
									}
								}
							}
					}
				}).start();
				mAnswer = "已清除内存";
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_SCREEN.equals(operands)) {		//关屏
			if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setClassName("com.zzj.coogo.screenoff","com.zzj.coogo.screenoff.ScrrenoffActivity");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					mContext.startActivity(intent);
				} catch (Exception e) {
				}
				mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
				return;
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_RADAR.equals(operands)) {			//雷达开关
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				Intent intent = new Intent();
				intent.setAction("com.wanma.action.RADAR_ON");
				mContext.sendBroadcast(intent);
				mAnswer = "为您打开雷达";

			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE
					.equals(operator)) {
				Intent intent = new Intent();
				intent.setAction("com.wanma.action.RADAR_OFF");
				mContext.sendBroadcast(intent);
				mAnswer = "为您关闭雷达";

			}

		} else if (SessionPreference.VALUE_SETTING_OBJ_TRAFFIC.equals(operands)) {			//实时路况
			if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {
				Intent intent = new Intent();
				intent.setAction(ACTION_GET_TRAFFIC);
				mContext.sendBroadcast(intent);
			}
		}
		
		else if (SessionPreference.VALUE_SETTING_OBJ_3G.equals(operands)) {			//移动网络开关
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_3g);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_3G);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_3g);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_3G);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_AUTOLIGHT.equals(operands)) {  //自动亮度开关
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
			}
			mAnswer = mContext.getString(R.string.open_display_settings);
			RomControl.enterControl(mContext, RomControl.ROM_OPEN_DISPLAY_SETTINGS);
		} else if (SessionPreference.VALUE_SETTING_OBJ_BLUETOOTH.equals(operands)) {   //蓝牙开关
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_bluetooth);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_BLUETOOTH);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_bluetooth);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_BLUETOOTH);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_TIME.equals(operands)) {   //时间设置
			if (SessionPreference.VALUE_SETTING_ACT_SET.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_time_settings);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_TIME_SETTINGS);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_LIGHT.equals(operands)) {   //亮度调节
			int light = 80;
			try {
				light = Settings.System.getInt(mContext.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
			} catch (SettingNotFoundException e) {
				e.printStackTrace();
			}
			if (SessionPreference.VALUE_SETTING_ACT_INCREASE.equals(operator)) {
				light += 35;
				if (light > 255) {
					light = 255;
				} else if (light < 0) {
					light = 0;
				}
				Settings.System.putInt(mContext.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, light);
			} else if (SessionPreference.VALUE_SETTING_ACT_DECREASE.equals(operator)) {
				light -=35;
				if (light > 255) {
					light = 255;
				} else if (light < 0) {
					light = 0;
				}
				Settings.System.putInt(mContext.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, light);
			} else if (SessionPreference.VALUE_SETTING_ACT_MAX.equals(operator)) {
				Settings.System.putInt(mContext.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, 255);
			} else if (SessionPreference.VALUE_SETTING_ACT_MIN.equals(operator)) {
				Settings.System.putInt(mContext.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, 0);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_VOLUMN.equals(operands)) {  //音量调节
			if (SessionPreference.VALUE_SETTING_ACT_INCREASE.equals(operator)) {
				mAnswer = mContext.getString(R.string.increase_volumne);
				RomControl.enterControl(mContext, RomControl.ROM_INCREASE_VOLUMNE, 2);
			} else if (SessionPreference.VALUE_SETTING_ACT_DECREASE.equals(operator)) {
				mAnswer = mContext.getString(R.string.decrease_volumne);
				RomControl.enterControl(mContext, RomControl.ROM_DECREASE_VOLUMNE, 2);
			} else if (SessionPreference.VALUE_SETTING_ACT_MAX.equals(operator)) {
				mAnswer = mContext.getString(R.string.max_volumne);
				RomControl.enterControl(mContext, RomControl.ROM_VOLUMNE_MAX);
			} else if (SessionPreference.VALUE_SETTING_ACT_MIN.equals(operator)) {
				mAnswer = mContext.getString(R.string.min_volumne);
				RomControl.enterControl(mContext, RomControl.ROM_VOLUMNE_MIN);
			} else if(SessionPreference.VALUE_SETTING_ACT_SET.equals(operator)){
				String value = JsonTool.getJsonValue(mDataObject, "value", "6");
				RomControl.enterControl(mContext, RomControl.ROM_VOLUMNE_SET,value);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MODEL_INAIR.equals(operands)) { //飞行模式
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.wait_open_setting_inair);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_MODEL_INAIR);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.wait_close_setting_inair);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_MODEL_INAIR);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MODEL_MUTE.equals(operands)) {  //静音模式
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_model_mute);
		//		RomControl.enterControl(mContext, RomControl.ROM_OPEN_MODEL_MUTE);
				RomControl.enterControl(mContext, RomControl.ROM_VOLUMNE_MIN);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_model_mute);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_MODEL_MUTE);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_MODEL_VIBRA.equals(operands)) {  //震动模式
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_model_vibra);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_MODEL_VIBRA);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_model_vibra);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_MODEL_VIBRA);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_RINGTONE.equals(operands)) {   //声音设置
			if (SessionPreference.VALUE_SETTING_ACT_SET.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_sound_setting);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_SOUND_SETTINGS);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_ROTATION.equals(operands)) {  //屏幕自动旋转开关
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_rotation);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_ROTATION);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_rotation);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_ROTATION);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_WALLPAPER.equals(operands)) {   //壁纸设置
			if (SessionPreference.VALUE_SETTING_ACT_SET.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_wallpaper_setting);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_WALLPAPER_SETTINGS);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_WIFI.equals(operands)) {  //WIFE开关
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_wifi);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_WIFI);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_wifi);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_WIFI);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_WIFI_SPOT.equals(operands)) {  //WIFE热点开关
			if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
				mAnswer = mContext.getString(R.string.open_wifi_spot);
				RomControl.enterControl(mContext, RomControl.ROM_OPEN_WIFI_SPOT);
			} else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = mContext.getString(R.string.close_wifi_spot);
				RomControl.enterControl(mContext, RomControl.ROM_CLOSE_WIFI_SPOT);
			}
		} else if (SessionPreference.VALUE_SETTING_OBJ_ROAD_CAMERA_COMPLAIN.equals(operands)) {  //电子眼投诉
			mContext.sendOrderedBroadcast(new Intent(ACTION_COOGO_FEEDBACK), null);
		}
		// 如果协议中包含OBJ_XXX,请在这里添加
		else {
			/*if (operator.equals(SessionPreference.VALUE_SETTING_ACT_OPEN_CHANNEL)) {
			} else */if (SessionPreference.VALUE_SETTING_ACT_PREV.equals(operator)) {        //上一首
				mAnswer = mContext.getString(R.string.previous);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDPREVIOUS);
				mMessageSender.sendMessage(intent, null);
			} else if (SessionPreference.VALUE_SETTING_ACT_NEXT.equals(operator)) {			//下一首
				mAnswer = mContext.getString(R.string.next);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDNEXT);
				mMessageSender.sendMessage(intent, null);
			} else if (SessionPreference.VALUE_SETTING_ACT_PLAY.equals(operator)) {			//播放
				mAnswer = mContext.getString(R.string.play);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDPLAY);
				mMessageSender.sendMessage(intent, null);
			} else if (SessionPreference.VALUE_SETTING_ACT_PAUSE.equals(operator)) {		//暂停
				mAnswer = mContext.getString(R.string.pause);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDPAUSE);
				mMessageSender.sendMessage(intent, null);
			} else if (SessionPreference.VALUE_SETTING_ACT_RESUME.equals(operator)) {		//恢复播放
				mAnswer = mContext.getString(R.string.recovery);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDPLAY);
				mMessageSender.sendMessage(intent, null);
			} else if (SessionPreference.VALUE_SETTING_ACT_STOP.equals(operator)) {			//停止
				mAnswer = mContext.getString(R.string.stop);
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDSTOP);
				mMessageSender.sendMessage(intent, null);
			} else if(SessionPreference.VALUE_SETTING_ACT_SCREEN_DISPLAY.equals(operator)){
				int light = 80;
				try {
					light = Settings.System.getInt(mContext.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
					if (light > 255) {
						light = 255;
					} else if (light < 0) {
						light = 0;
					}
				} catch (SettingNotFoundException e) {
					e.printStackTrace();
				}
				Settings.System.putInt(mContext.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, light + 35);
			}
			//云端的关闭既然不处理就删掉判断
/*			else if (SessionPreference.VALUE_SETTING_ACT_CLOSE.equals(operator)) {
				mAnswer = "已执行关闭";
				Intent intent = new Intent(CommandPreference.SERVICECMD);
				intent.putExtra(CommandPreference.CMDNAME, CommandPreference.CMDSTOP);
				mMessageSender.sendMessage(intent, null);
			}*/
			else if (SessionPreference.VALUE_SETTING_OBJ_MUSIC_PREVIOUS_ITEM.equals(operands)) {
				if (SessionPreference.VALUE_SETTING_ACT_OPEN.equals(operator)) {
					
					Intent intent = new Intent(CommandPreference.SERVICECMD);
					intent.putExtra(CommandPreference.CMDNAME,CommandPreference.CMDOPEN_MUSIC);
					mContext.sendOrderedBroadcast(intent, null);
					
				}else if (SessionPreference.VALUE_SETTING_ACT_PREV.equals(operator)) {
					mAnswer = mContext.getString(R.string.play_previous);
					// Toast.makeText(mContext, jsonProtocol.toString(),
					// Toast.LENGTH_SHORT).show();
				}
			} else if (SessionPreference.VALUE_SETTING_ACT_PLAY_MESSAGE.equals(operator)) {
				Logger.d(TAG, "--VALUE_SETTING_ACT_PLAY_MESSAGE Execution Broadcast--");
				mAnswer = mContext.getString(R.string.sms_content);
				//mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_PLAY_REVEIVED_MESSAGE);
			} else if (SessionPreference.VALUE_SETTING_ACT_CANCEL.equals(operator)) {
				mAnswer = mContext.getString(R.string.cancal);
			} else{
				addQuestionViewText(mQuestion);
				addAnswerViewText(mQuestion);
				NoPerSonContentView view = new NoPerSonContentView(mContext);
				view.setShowTitle(mContext.getString(R.string.no_support_cmd, mQuestion)); 
				addAnswerView(view, true);
				mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
				return;
			}
			// 如果协议中仅包含ACT_XXX，请在此添加
		}
		addQuestionViewText(mQuestion);
		addAnswerViewText(mAnswer);
        NoPerSonContentView view = new NoPerSonContentView(mContext);
        view.setShowTitle(mContext.getString(R.string.executed)+ mQuestion); 
        addAnswerView(view, true);
		mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);
	}
	
	public static void forceStopPackage(String pkgName, Context context)
			throws Exception {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		Method method = Class.forName(ACTIVITY_MANAGER).getMethod("forceStopPackage", String.class);
		method.invoke(am, pkgName);
	}
}
