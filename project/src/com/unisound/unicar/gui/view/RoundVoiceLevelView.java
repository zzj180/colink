package com.unisound.unicar.gui.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.Logger;

/**
 * 利用View的相交模式，实现两种圆形图片的进度条
 * @author xiaodong
 * @date 20150612
 */
public class RoundVoiceLevelView extends View {
	
	private static final String TAG = RoundVoiceLevelView.class.getSimpleName();
	
	private static final String ATTR_SRC_PROGRESS_ZERO= "src_progress_zero";
	private static final String ATTR_SRC_PROGRESS_FULL= "src_progress_full";
	private boolean isShowPercent = false;
	
	private Bitmap bmpBg;
	private Bitmap bmpForbg;
	
	private PorterDuffXfermode mMode ;
	private Paint mXferPaint ;
	private RectF mOval ;
	private int mPercent;
	
	private Paint paint ;
	private Rect targetRect ;
	private FontMetricsInt fontMetrics ;
	
	private float left = 0;
	private float top = 0;
	/**
	 * 重写父类构造函数
	 * 
	 * @param context
	 */
	public RoundVoiceLevelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		// setFocusableInTouchMode(true);
		getCustomAttributes(context, attrs);
		
		mMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
//        mXferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mXferPaint = new Paint();
//        mXferPaint.setStyle(Paint.Style.FILL);
        mXferPaint.setColor(Color.RED);
        mXferPaint.setXfermode(mMode);
        mOval = new RectF();
        mOval.left = 0;
        mOval.top = 0;
        
        mPercent = 0 ;
        
        this.left = this.getLeft();
		this.top = this.getTop();
        
        if(isShowPercent){
        	paint = new Paint() ;
        	paint.setColor(Color.CYAN) ;
        	paint.setTextSize(24) ;
        	targetRect = new Rect(100, 100, 100 + bmpBg.getWidth(), 100 + bmpBg.getHeight());  
        	fontMetrics = paint.getFontMetricsInt(); 
        }
	}
	
	private void getCustomAttributes(Context context, AttributeSet attrs){
		int zeroId = -1;
        int fullId = -1;
        zeroId = attrs.getAttributeResourceValue(null, ATTR_SRC_PROGRESS_ZERO, 0);
        fullId = attrs.getAttributeResourceValue(null, ATTR_SRC_PROGRESS_FULL, 0);
        if (zeroId > 0) {
        	bmpBg = (Bitmap) BitmapFactory.decodeResource(getResources(),
        			zeroId);
        	Log.d(TAG, "!--->zeroId = "+zeroId);
        } else {
        	bmpBg = (Bitmap) BitmapFactory.decodeResource(getResources(),
    				R.drawable.mic_recording_bg); //ic_set_download_speed1
        }
        if (fullId > 0) {
        	bmpForbg = (Bitmap) BitmapFactory.decodeResource(getResources(),
        			fullId);
        	Log.d(TAG, "!--->fullId = "+zeroId);
        }else{
        	bmpForbg = (Bitmap) BitmapFactory.decodeResource(getResources(),
    				R.drawable.mic_recording_voice); //ic_set_download_speed1
        }
	}
	
	/**
	 * 重写父类绘图函数
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onDraw(Canvas canvas) {
//	    this.left = this.getLeft();
//	    this.top = this.getTop();
		//Logger.d(TAG, "!--->onDraw()----left = "+left+"; top = "+top);
		
		mXferPaint.setXfermode(null);
		canvas.drawBitmap(bmpBg, left, top, mXferPaint) ;
		int saveCount = canvas.saveLayer(left, top, left + bmpBg.getWidth(), top + bmpBg.getHeight(), null, 
				Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG 
				| Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);

		mOval.left = left ;
		mOval.top = top ;
        mOval.right = left + bmpBg.getWidth();
        mOval.bottom = top + bmpBg.getHeight();
        mXferPaint.setXfermode(null);
        canvas.drawArc(mOval, -90, 360 * mPercent / 100, true, mXferPaint);

        mXferPaint.setXfermode(mMode);
        canvas.drawBitmap(bmpForbg, left, top, mXferPaint);

        canvas.restoreToCount(saveCount);
        
        //在中间显示percent
        if(isShowPercent){
        	int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;  
        	// 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()  
        	paint.setTextAlign(Paint.Align.CENTER);
        	
        	canvas.drawText(mPercent + "%", targetRect.centerX(), baseline, paint);
        }
	}

	
	public void setLocation(float left, float top){
		Logger.d(TAG, "!--->setLocation---left = "+left+"; top = "+top);
		this.left = left;
		this.top = top;
	}
	
	public void setPercent(int percent) {
		//Log.d(TAG, "!--->setPercent :"+percent);
		this.mPercent = percent;
		// 重绘画布
		invalidate();
		// postInvalidate();
	}
	
	
	
}