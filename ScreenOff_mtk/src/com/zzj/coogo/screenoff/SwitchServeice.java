package com.zzj.coogo.screenoff;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class SwitchServeice extends Service {

	private static final String CAMERA2_APP = "com.android.camera2";
/*	private static final String GOOGLE_MAP_APP = "com.google.android.apps.maps";
	private static final String GOOGLE_MAP_ACTIVITY = "com.google.android.maps.MapsActivity";*/
	private static final String TAG = "handerThread";
	protected static final long delayMillis = 600L;
	Handler handler, readHandler;
	HandlerThread mHandlerThread;
	// 定义浮动窗口布局
	WindowManager.LayoutParams wmParams;
	// 创建浮动窗口设置
	WindowManager mWindowManager;

	private View switchView;

//	private boolean showInput;
	BNRBroadCast mScreenOffReceiver;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return START_STICKY;
	}
	private Runnable r = new Runnable() {

		@Override
		public void run() {
			try {
				isTopActivity();
			} catch (Exception e) {
			}

			readHandler.postDelayed(r, delayMillis);

		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		createFloatView();
		switchView.setVisibility(View.GONE);
		handler = new Handler(getMainLooper());
		mHandlerThread = new HandlerThread(TAG);
		mHandlerThread.start();
		readHandler = new Handler(mHandlerThread.getLooper());
		readHandler.postDelayed(r, delayMillis);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mScreenOffReceiver = new BNRBroadCast();
		registerReceiver(mScreenOffReceiver, filter);

	}

	private void createFloatView() {

		wmParams = new WindowManager.LayoutParams();
		// 获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) getApplication().getSystemService(
				WINDOW_SERVICE);

		DisplayMetrics displaymetrics = new DisplayMetrics();  
		displaymetrics.widthPixels = 57;
		displaymetrics.heightPixels = 57;
		mWindowManager.getDefaultDisplay().getMetrics(displaymetrics);
		// 设置window type
		wmParams.type = LayoutParams.TYPE_PHONE;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		// 调整悬浮窗显示的停靠位置为左侧置�?
		wmParams.gravity = Gravity.TOP | Gravity.RIGHT;
		wmParams.x = 9;
		wmParams.y = 250;

		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		// 设置悬浮窗口长宽数据
		wmParams.width = 68;
		wmParams.height = 68;
		/*
		 * // 设置悬浮窗口长宽数据 wmParams.width = 200; wmParams.height = 80;
		 */
		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图
		switchView = inflater.inflate(R.layout.switch_navi, null);
		// 添加mFloatLayout
		mWindowManager.addView(switchView, wmParams);
		switchView.setOnTouchListener(new OnTouchListener() {

			private int lastY;

			private int paramY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:

					// getRawX是触摸位置相对于屏幕的坐标

					lastY = (int) event.getRawY();
					paramY = wmParams.y;
					return false;

				case MotionEvent.ACTION_MOVE:

					int dy = (int) event.getRawY() - lastY;
					wmParams.y = paramY + dy;
					// 更新悬浮窗位�?
					mWindowManager.updateViewLayout(v, wmParams);

					return true;
				case MotionEvent.ACTION_UP:
					if ((int) event.getRawY() == lastY) {
						Intent intent = new Intent(Intent.ACTION_MAIN);
						intent.setClassName(CAMERA2_APP, Constant.CAMERA_ACTIVITY);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						try {
							startActivity(intent);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					break;
				}
				return false; // 此处必须返回false，否则OnClickListener获取不到监听
			}
		});

	}

	@Override
	public void onDestroy() {

		if (switchView != null) {
			// 移除悬浮窗口
			mWindowManager.removeView(switchView);
		}

		if (readHandler != null && mHandlerThread != null) {
			mHandlerThread.quit();
			readHandler.removeCallbacks(r);
		}
		unregisterReceiver(mScreenOffReceiver);
		startService(new Intent(this, SwitchServeice.class));
		super.onDestroy();
	}

	public void isTopActivity() {

		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		// 应用程序位于堆栈的顶层
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (tasksInfo != null && tasksInfo.size() > 0 && switchView != null) {
					if (Constant.BAIDU_NAVI_ACTIVITY.equals(tasksInfo.get(0).topActivity.getClassName())) {
						SwitchServeice.this.sendBroadcast(new Intent("hiden_systemui"));
							switchView.setVisibility(View.VISIBLE);
					} else if (Constant.GAODE_MAP_ACTIVITY.equals(tasksInfo.get(0).topActivity
							.getClassName())) {
						SwitchServeice.this.sendBroadcast(new Intent("hiden_systemui"));
						switchView.setVisibility(View.VISIBLE);
					} else if (Constant.GAODE_CAR_ACTIVITY.equals(tasksInfo.get(0).topActivity
							.getClassName())) {
						switchView.setVisibility(View.VISIBLE);
					} 
					/*else if (GOOGLE_MAP_ACTIVITY.equals(tasksInfo.get(0).topActivity
							.getClassName())) {
						synchronized (this) {
							if (switchView.getVisibility() == View.GONE && !showInput) {
								showInput = true;
								mWindowManager.removeViewImmediate(switchView);
								wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;
								mWindowManager.addView(switchView, wmParams);
								switchView.setVisibility(View.VISIBLE);
								handler.sendEmptyMessageDelayed(1, 2000);
							}
						}
					}*/ else {
						SwitchServeice.this.sendBroadcast(new Intent("show_systemui"));
						switchView.setVisibility(View.GONE);
					}
				}
			}

		});
	}

	/*// 显示虚拟键盘
	public void ShowKeyboard() {
		InputMethodManager imm = (InputMethodManager) switchView.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(switchView.findViewById(R.id.editText1),
				InputMethodManager.SHOW_IMPLICIT);
	}

	// 隐藏虚拟键盘
	public void HideKeyboard() {
		InputMethodManager imm = (InputMethodManager) switchView.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(switchView.findViewById(R.id.editText1)
					.getApplicationWindowToken(),
					InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
	}*/
}
