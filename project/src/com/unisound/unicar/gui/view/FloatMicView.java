package com.unisound.unicar.gui.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.preference.UserPreference;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * 
 * @author xiaodong
 * @date 20150805
 */
@SuppressLint({ "HandlerLeak" })
@SuppressWarnings("unused")
public class FloatMicView extends FloatView {

	private static final String TAG = FloatMicView.class.getSimpleName();

	private static final long AUTO_HIDE_DELAY = 3000;
	private static final String TAG_BTN_FOLAT_MIC = "mic";

	private Context mContext;
	
	private float mRawX, mRawY;
	private int mLastPostionX = 0;
	private int mLastPostionY = 0;
	private boolean mHasMoved;
	private double mDef = 0;

	private ImageView mBtnFloatMic;
	private OnClickListener mListener;
	private Handler mHandler = new Handler();
	private Runnable mRunnableHide = new Runnable() {

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void run() {
			if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				Logger.d(TAG, "start alpha animataion.");
				AlphaAnimation animation = new AlphaAnimation(1.0f, 0.5f);
				animation.setDuration(500);
				animation.setFillAfter(true);
				animation.setRepeatCount(0);
				mBtnFloatMic.startAnimation(animation);
			} 
//			else {
//				Logger.d(TAG, "setAlpha.");
//				mBtnFloatMic.setAlpha(0.5f);
//			}
		}
	};

	@SuppressLint({ "NewApi", "ClickableViewAccessibility" })
	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		/**the location relative to the view, (the view top left corner is the origin)*/
		float mTouchStartX, mTouchStartY;
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// Logger.d(TAG, "onTouch: " + event);
			mRawX = event.getRawX();
			mRawY = event.getRawY() - mStatusBarHeight;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mHasMoved = false;
				mTouchStartX = event.getX();
				mTouchStartY = event.getY();
				mLastPostionX = (int) mRawX;
				mLastPostionY = (int) mRawY;
				getScreenSize(mContext);

				mBtnFloatMic.setImageResource(R.drawable.float_mic_pressed);
				mHandler.removeCallbacks(mRunnableHide);
				if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
					Logger.d(TAG, "onTouch: start animation");
					AlphaAnimation animation = new AlphaAnimation(0.5f, 1.0f);
					animation.setDuration(200);
					animation.setFillAfter(true);
					animation.setRepeatCount(0);
					mBtnFloatMic.startAnimation(animation);
				} else {
					Logger.d(TAG, "onTouch: setAlpha");
					mBtnFloatMic.setAlpha(1.0f);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (Math.abs(mRawX - mLastPostionX) > mDef && Math.abs(mRawY - mLastPostionY) > mDef) {
					mHasMoved = true;
					// update float view position param
					int newX = (int) (mRawX - mTouchStartX);
					int newY = (int) (mRawY - mTouchStartY);
					//Logger.d(TAG, "!--->ACTION_MOVE--x = "+mRawX+"; y = "+mRawY+"; mTouchStartX = "+mTouchStartX+"; mTouchStartY = "+mTouchStartY);
					updateViewPosition(newX, newY);
				}
				break;
			case MotionEvent.ACTION_UP:
				Logger.d(TAG, "!--->ACTION_UP---x = "+mRawX+"; y = "+mRawY+"; mTouchStartX = "+mTouchStartX+"; mTouchStartY = "+mTouchStartY+"; mHasMoved = "+mHasMoved);
				mBtnFloatMic.setImageResource(R.drawable.float_mic_normal);
				
				mWindowParams.x = (int) (mRawX - mTouchStartX);
				mWindowParams.y = (int) (mRawY - mTouchStartX);
				if (mHasMoved) {
					requestAutoDock();
					saveViewPostion();
				}
				mTouchStartX = mTouchStartY = 0;
				break;
			}
			return false;
		}
	};

	// private int isMoveTalkCancle(float startMove, float endMove) {
	// float mMoveY1 = startMove - endMove;
	// float mMoveY2 = endMove - startMove;
	// if (mMoveY1 >= 130) {
	// mMoveTalkCancel = 1;
	// startY = endMove;
	// } else if (mMoveY2 >= 120) {
	// mMoveTalkCancel = 2;
	// startY = endMove;
	// }
	// Logger.d(TAG, "mMoveTalkCancel = " + mMoveTalkCancel);
	// return mMoveTalkCancel;
	// }

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Logger.d(TAG, "--float mic button clicked!--");
			if (mListener != null && !mHasMoved) {
				mListener.onClick(v);
			}
		}
	};

	public FloatMicView(Context context) {
		super(context);
		mContext = context;

		mDef = mContext.getResources().getDimensionPixelSize(R.dimen.float_window_def);
		initViewStyle();
		initViewCtrls();

		// mGestureDetector = new GestureDetector(context, onGestureListener);
		// mGestureDetector.setIsLongpressEnabled(true);
	}

	private void requestAutoDock() {
//		int x = mLastPostionX, y = mLastPostionY;
		int x = mWindowParams.x, y = mWindowParams.y;
		
		Logger.d(TAG, "requestAutoDock---mWindowParams.x = "+x+"; mScreenSize.x/2 = " + mScreenSize.x / 2);
		if (x <= mScreenSize.x / 2) {
			x = 0;
		} else {
			x = mScreenSize.x - getWidth();
		}
		updateViewPosition(x, y);

//		resetHideTimer();
	}

	private void resetHideTimer() {
		mHandler.removeCallbacks(mRunnableHide);
		mHandler.postDelayed(mRunnableHide, AUTO_HIDE_DELAY);
	}

	@SuppressLint("InlinedApi")
	private void initViewStyle() {
		mWindowParams.type = android.view.WindowManager.LayoutParams.TYPE_PHONE
								| android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
								| android.view.WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		mWindowParams.format = PixelFormat.RGBA_8888;
		mWindowParams.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
								| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
								| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
								| WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

		mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
		// mWindowParams.windowAnimations = R.style.PauseDialogAnimation;
		mWindowParams.width = LayoutParams.WRAP_CONTENT;
		mWindowParams.height = LayoutParams.WRAP_CONTENT;
	}

	private void initViewCtrls() {
		Context context = getContext();
		mBtnFloatMic = new ImageView(context);
		// mBtnFloatMic.setText("按住说话");
		mBtnFloatMic.setTag(TAG_BTN_FOLAT_MIC);
		mBtnFloatMic.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mBtnFloatMic.setImageResource(R.drawable.float_mic_normal);
		mBtnFloatMic.setClickable(true);
		mBtnFloatMic.setOnClickListener(mOnClickListener);
		mBtnFloatMic.setOnTouchListener(mOnTouchListener);
		addView(mBtnFloatMic);

		// startTalk();
	}

	private void saveViewPostion() {
		Logger.d(TAG, "!--->saveViewPostion---mWindowParams.x = "+mWindowParams.x+"; mWindowParams.y = "+mWindowParams.y);
		UserPerferenceUtil.setInputViewX(mContext, mWindowParams.x);
		UserPerferenceUtil.setInputViewY(mContext, mWindowParams.y);
	}

	private void getViewPostion() {
		int savedX = UserPerferenceUtil.getInputViewX(mContext, 0); 
		int savedY = UserPerferenceUtil.getInputViewY(mContext, getFloatViewDefaultLocationY()); 
		Logger.d(TAG, "!--->getViewPostion------savedX = "+savedX+"; savedY = "+savedY);
		mWindowParams.x = mLastPostionX = savedX;
		mWindowParams.y = mLastPostionY = savedY;
	}

	/**
	 * get FloatView default LocationY
	 * XD 20150901 added
	 * @return
	 */
	private int getFloatViewDefaultLocationY(){
		int floatViewHight = mContext.getResources().getDrawable(R.drawable.float_mic_normal).getIntrinsicHeight();
		int defaultY = (DeviceTool.getScreenHight(mContext) + mStatusBarHeight - floatViewHight) / 2;  //mWindowSize.y / 2 - getHeight()
		Logger.d(TAG, "!--->getFloatViewDefaultLoactionY--defaultY = "+defaultY +"; floatViewHight = " + floatViewHight);
		return defaultY;
	}
	
	private void updateViewPosition(int x, int y) {
		Logger.d(TAG, "!--->updateViewPosition--x = "+x+"; y = "+y);
		mWindowParams.x = mLastPostionX = x;
		mWindowParams.y = mLastPostionY = y;
		mWindowManager.updateViewLayout(this, mWindowParams);
	}

	private void showSpeakView() {
		mBtnFloatMic.setVisibility(View.GONE);
	}

	private void hideSpeakView() {
		mBtnFloatMic.setVisibility(View.VISIBLE);
	}

	public void setOnClickListener(OnClickListener l) {
		mListener = l;
	}

	public ImageView getFloatMicInstance(){
		return mBtnFloatMic;
	}
	@Override
	public void show() {
		Logger.d(TAG, "show");
		getViewPostion();
		super.show();
//		resetHideTimer();
	}

	@Override
	public void hide() {
		Logger.d(TAG, "hide");
		super.hide();
	}
}
