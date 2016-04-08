package com.aispeech.aios.adapter.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.aispeech.aios.adapter.bean.StockBean;
import com.aispeech.aios.adapter.bean.StockBean.ChartData;
import com.aispeech.aios.adapter.util.MathUtil;

public class StockChartView extends GridChartView {

	private int maxPointNum;
	private float minValue;
	private float maxValue;
	private StockBean mBean;
	Paint mPaint;
	Paint charPaint;
	Context context;

	public StockChartView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public StockChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public StockChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public void init() {
		charPaint = new Paint();
		charPaint.setColor(0xffff0000);
		charPaint.setStrokeWidth(1.0f);
		charPaint.setStyle(Paint.Style.FILL);
		charPaint.setAntiAlias(true);

		mPaint = new Paint();
		mPaint.setColor(0xffffffff);
		mPaint.setStrokeWidth(1.0f);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);

	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (null != this.mBean) {
			drawLines(canvas);
		}
	}

	protected void drawLines(Canvas canvas){
		// 点线距离
		float lineLength = ((super.getWidth() - super.getAxisMarginLeft() - 3 - super.getAxisMarginRight()) / maxPointNum)-1;
		// 起始位置
		float startX;
		float postY = super.getHeight() - getAxisMarginBottom() + MathUtil.getInstance().dip2px(context,8);


		/** 话股票分时线 */
		startX = super.getAxisMarginLeft() + lineLength / 2f + 1;
		PointF ptFirst = null;
		int chartLen = mBean.mStockList.size();

		for(int i = 0; i < chartLen; i++) {
			ChartData data = mBean.mStockList.get(i);
			float value = data.mTransPrice;

			float valueY = (1f - (value - mBean.baseLineDate) / mBean.length)
					* (super.getHeight() - super.getAxisMarginBottom() - 3) + super.DEFAULT_AXIS_BOARD_MARGIN_TOP + 1;
			if (i > 0){
				canvas.drawLine(ptFirst.x,ptFirst.y,startX,valueY, mPaint);
			}
			//重置起始点
			ptFirst = new PointF(startX , valueY);
			//X位移
			startX = startX + 1 + lineLength;
		}



		/**画股票柱状图*/

		startX = super.getAxisMarginLeft() + lineLength / 2f + 1;
		//X位移
		startX = startX + 1 + lineLength;

		for(int i = 1 ; i < chartLen;i++) {
			ChartData data = mBean.mStockList.get(i);
			ChartData preData = mBean.mStockList.get(i - 1);
			float value = data.mTransNum;
			if(data.mTransPrice >= preData.mTransPrice) {
				charPaint.setColor(0xffff3817);
			} else {
				charPaint.setColor(0xff00f806);
			}

			float valueY = (float) ((1f - (value)
					/ (mBean.curMaxTransNum))
					* getStickHeight() + postY);

			canvas.drawLine(startX,super.getHeight() - DEFAULT_AXIS_BOARD_MARGIN_BOTTOM,startX,valueY,charPaint);
			//X位移
			startX = startX + 1 + lineLength;
		}
	}

	public void setStockBean(StockBean bean) {
		this.mBean = bean;
		invalidate();
	}


	public void setMinValue(float minValue) {
		this.minValue = minValue;
		invalidate();
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
		invalidate();
	}

	public void setMaxPointNum(int maxPointNum) {
		this.maxPointNum = maxPointNum;
	}
}
