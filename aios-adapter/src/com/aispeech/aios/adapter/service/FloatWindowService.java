package com.aispeech.aios.adapter.service;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.database.ContentObserver;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.aispeech.ailog.AILog;
import com.aispeech.aimusic.AIMusic;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.node.ChatNode;
import com.aispeech.aios.adapter.node.CommonPoiNode;
import com.aispeech.aios.adapter.node.CustomizeNode;
import com.aispeech.aios.adapter.node.HomeNode;
import com.aispeech.aios.adapter.node.LocationNode;
import com.aispeech.aios.adapter.node.MusicNode;
import com.aispeech.aios.adapter.node.NavigationNode;
import com.aispeech.aios.adapter.node.NearbyNode;
import com.aispeech.aios.adapter.node.PhoneNode;
import com.aispeech.aios.adapter.node.RadioNode;
import com.aispeech.aios.adapter.node.StockNode;
import com.aispeech.aios.adapter.node.SystemNode;
import com.aispeech.aios.adapter.node.TTSNode;
import com.aispeech.aios.adapter.node.VehicleRestrictionNode;
import com.aispeech.aios.adapter.node.WeatherNode;
import com.aispeech.aios.adapter.receiver.PhoneBookReceive;
import com.aispeech.aios.adapter.ui.MyWindowManager;
import com.aispeech.aios.adapter.util.Gps;
import com.aispeech.aios.adapter.util.MapOperateUtil;
import com.aispeech.aios.adapter.util.PositionUtil;
import com.aispeech.aios.adapter.util.PreferenceHelper;
import com.aispeech.aios.adapter.util.SystemPropertiesProxy;
import com.aispeech.aios.adapter.util.UserPreference;
import com.aispeech.aios.adapter.vendor.BDDH.BDDHOperate;
import com.aispeech.aios.adapter.vendor.GD.GDOperate;

/**
 * @desc 悬浮窗相关服务
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
@SuppressLint("NewApi")
public class FloatWindowService extends Service {

	private String TAG = "AIOS-FloatWindowService";
	Handler mWorkerHandler;
	private HandlerThread mWorker;

	private static FloatWindowService service;
	private String MAP_INDEX = "MAP_INDEX";
	private HandlerThread mHandlerThread = new HandlerThread("worker-thread");
	public static final String BACK_CAR_STATE = "back_car_state";
	@Override
	public void onCreate() {
		super.onCreate();
		service = this;
		mUserPreference = new UserPreference(this);
		AILog.i(TAG, "FloatWindowService on create");
		/*
		 * PendingIntent pendingintent = PendingIntent.getService(this, 0, new
		 * Intent(this,
		 * com.aispeech.aios.adapter.service.FloatWindowService.class), 0);
		 * 
		 * Notification notification = new Notification.Builder(this)
		 * .setSmallIcon(R.drawable.icon_app_launcher)
		 * .setContentTitle("AIOS-Adapter") .setContentText("AIOS-Adapter 正在运行")
		 * .setContentIntent(pendingintent) .build();
		 * startForeground(R.string.app_name, notification);
		 */

		initNodes();
		mHandlerThread.start();
	}

	public static FloatWindowService getRunningService() {
		return service;
	}

	/**
	 * 初始化Node
	 */
	private void initNodes() {
		AILog.d(TAG, "initNodes - " + this);
		if (mWorker != null) {
			return;
		}
		AIMusic.initMusic(AdapterApplication.getContext(), PreferenceHelper
				.getInstance().getDefaultMusicType());
		mWorker = new HandlerThread("worker-thread");
		mWorker.start();
		mWorkerHandler = new Handler(mWorker.getLooper());
		mWorkerHandler.post(new Runnable() {

			@Override
			public void run() {
				PhoneNode.getInstance().start();
				PhoneNode.getInstance().registerBtReceiver();

				HomeNode.getInstance().start();
				HomeNode.getInstance().register();

				CustomizeNode.getInstance().start();
				CustomizeNode.getInstance().register();

				TTSNode.getInstance().start();

				NavigationNode.getInstance().start();

				NearbyNode.getInstance().start();

				WeatherNode.getInstance().start();

				MusicNode.getInstance().start();

				LocationNode.getInstance().start();

				SystemNode.getInstance().start();

				VehicleRestrictionNode.getInstance().start();

				StockNode.getInstance().start();

				RadioNode.getInstance().start();

				// FmNode.getInstance().start();

				ChatNode.getInstance().start();

				CommonPoiNode.getInstance().start();
			}
		});
		Intent intent = new Intent();
		intent.setAction("com.aispeech.launcher.BRING_TO_FRONT");
		sendBroadcast(intent);

		AILog.e(TAG, "Service Context ------> " + getRunningService());
		AILog.e(TAG,"AdapterApplication Context ------> " + AdapterApplication.getContext());
		
		AIMusic.closeDeskLyric();
		
		isBackCar();
		Handler handler = new Handler(getMainLooper());
		getContentResolver().registerContentObserver(Settings.System.getUriFor(BACK_CAR_STATE), true,
				new ContentObserver(handler) {
					@Override
					public void onChange(boolean selfChange) {
						isBackCar();
					}

				});
		
		getContentResolver().registerContentObserver(
				Settings.System.getUriFor(MAP_INDEX), false,
				new ContentObserver(handler) {
					@Override
					public void onChange(boolean selfChange) {
						int mapType = getMapType();
						if(mapType==0){
							GDOperate.getInstance(getApplicationContext()).closeMap();
						}else if(mapType == 1){
							BDDHOperate.getInstance(getApplicationContext()).closeMap();
						}
					}
				});
	}
	
	private void isBackCar() {
		boolean isBackCar = Settings.System.getInt(getContentResolver(),BACK_CAR_STATE,0) == 1;
		if(isBackCar){
			 cancelAios();
		}
	}
	
	private int getMapType() {
		return Settings.System.getInt(getContentResolver(), MAP_INDEX, 0);
	}

	private void cancelAios() {
		BusClient bc = HomeNode.getInstance().getBusClient();
		 UiEventDispatcher.notifyUpdateUI(UIType.Awake);
		 UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
		 if (null != bc) {
		     bc.publish(AiosApi.Other.UI_PAUSE);
		 }
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		AILog.i(TAG, "onStartCommand");
		if (intent != null) {
			String action = intent.getAction();
			if (PhoneBookReceive.ACTION_START_NAVI.equals(action)) {
				CreateDialog(this);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		AILog.i(TAG, "AIOS FloatWindowService on destroy!!!");
		MyWindowManager.getInstance().removeSmallWindow();

		stopForeground(true);
		HomeNode.getInstance().unRegister();
		PhoneNode.getInstance().unRegister();
		CustomizeNode.getInstance().unRegister();
		service = null;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * 重新启动CustomizeNode
	 */
	public void restartCustomizeNode() throws Exception {

		AILog.e(TAG, "-----------restart customize node---------------");
		if (CustomizeNode.getInstance() != null
				&& CustomizeNode.getInstance().isRunning()) {
			CustomizeNode.getInstance().stop();
		}
		if (mWorker == null) {
			mWorker = new HandlerThread("worker-thread");
			mWorker.start();
		}
		if (mWorkerHandler == null) {
			mWorkerHandler = new Handler(mWorker.getLooper());
		}

		mWorkerHandler.post(new Runnable() {
			@Override
			public void run() {
				CustomizeNode.getInstance().start();
				CustomizeNode.getInstance().register();
			}
		});
	}

	private static final String PLAY_TTS = "com.wanma.action.PLAY_TTS";
	AlertDialog mAlertDialog;
	Button button1, button2;
	private UserPreference mUserPreference;

	/**
	 * by ZZJ
	 */
	@SuppressLint("InflateParams")
	public void CreateDialog(Context context) {
		if (mAlertDialog != null) {
			return;
		}
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.prepare_navi, null);
		button1 = (Button) v.findViewById(R.id.button1);
		button2 = (Button) v.findViewById(R.id.button2);
		Builder builder = new AlertDialog.Builder(context);
		button1.setOnClickListener(l);
		button2.setOnClickListener(l);
		String address = mUserPreference.getString("address",getString(R.string.prepnavi_address));
		((TextView) v.findViewById(R.id.textView1)).setText(getString(R.string.prepnavi_endpoi, address));
		builder.setView(v);
		Intent intent = new Intent(PLAY_TTS);
		String content = getString(R.string.receive_prepnavi_command);

		if (getString(R.string.prepnavi_address).equals(address)) {
		} else {
			String customer = SystemPropertiesProxy.get(this,
					"ro.inet.consumer.code");
			if ("003".equals(customer)) {
				content = content + ",目的地是" + address;
			}
		}

		intent.putExtra("content", content);
		context.sendBroadcast(intent);
		mAlertDialog = builder.create();
		mAlertDialog.setCanceledOnTouchOutside(false);
		mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);// 显示
		mAlertDialog.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		CountDown();
		mAlertDialog.show();

		mAlertDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				mAlertDialog = null;
				mUserPreference.putFloat("lat", 0f);
				mUserPreference.putFloat("lng", 0f);
				mUserPreference.putString("address", "");
				if (mCountDownTimer != null) {
					mCountDownTimer.cancel();
					mCountDownTimer = null;
				}
			}
		});
	}

	/**
	 * by ZZJ add
	 */

	OnClickListener l = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				if (mAlertDialog != null) {
					startNavi();
				}
				break;
			default:
				if (mAlertDialog != null) {
					mUserPreference.putFloat("lat", 0f);
					mUserPreference.putFloat("lng", 0f);
					mUserPreference.putString("address", "");
					mAlertDialog.dismiss();
				}
				break;
			}

		}
	};

	/**
	 * by ZZJ
	 */
	CountDownTimer mCountDownTimer;

	private void CountDown() {
		if (mCountDownTimer != null) {
			return;
		}

		mCountDownTimer = new CountDownTimer(10000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {

				button1.setText(getString(R.string.cancel_prepnavi,
						millisUntilFinished / 1000));
			}

			@Override
			public void onFinish() {
				if (mAlertDialog != null) {
					startNavi();
				}
			}

		};
		mCountDownTimer.start();
	}

	/**
	 * by ZZJ
	 */
	private void startNavi() {
		Gps gcj02 = PositionUtil.bd09_To_Gcj02(
				mUserPreference.getFloat("lat", 0f),
				mUserPreference.getFloat("lng", 0f));
		PoiBean bean = new PoiBean();
		bean.setLatitude(gcj02.getWgLat());
		bean.setLongitude(gcj02.getWgLon());
		MapOperateUtil.getInstance().startNavigation(bean);
		mUserPreference.putFloat("lat", 0f);
		mUserPreference.putFloat("lng", 0f);
		mUserPreference.putString("address", "");
		mAlertDialog.dismiss();
	}
}