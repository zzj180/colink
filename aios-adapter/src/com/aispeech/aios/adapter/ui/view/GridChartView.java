package com.aispeech.aios.adapter.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.util.MathUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/18.
 */
public class GridChartView extends View implements ITouchEventNotify, ITouchEventResponse {

	/** 默认背景色 */
	public static final int DEFAULT_BACKGROUD_COLOR = Color.BLACK;

	/** 默认X坐标轴颜色 */
	public static final int DEFAULT_AXIS_X_COLOR = Color.RED;

	/** 默认Y坐标轴颜色 */
	public static final int DEFAULT_AXIS_Y_COLOR = Color.RED;

	/** 默认经线颜色 */
	public static final int DEFAULT_LONGITUDE_COLOR = Color.RED;

	/** 默认纬线颜色 */
	public static final int DEFAULT_LAITUDE_COLOR = Color.RED;

	/** 默认轴线左边距 */
	public static final float DEFAULT_AXIS_MARGIN_LEFT = 60f;

	/** 默认轴线底边距 */
	public static final float DEFAULT_AXIS_MARGIN_BOTTOM = 80f;

	/** 默认轴线上边距 */
	public static final float DEFAULT_AXIS_MARGIN_TOP = 5f;

	/** 默认轴线右边距 */
	public static final float DEFAULT_AXIS_MARGIN_RIGHT = 5f;

	/**边框默认上边距*/
	public static final float DEFAULT_AXIS_BOARD_MARGIN_TOP = 1.0f;
	public static final float DEFAULT_AXIS_BOARD_MARGIN_RIGHT = 1.0f;
	public static final float DEFAULT_AXIS_BOARD_MARGIN_BOTTOM = 2.0f;

	/** 默认经线是否显示刻度 */
	public static final boolean DEFAULT_DISPLAY_LONGTITUDE = Boolean.TRUE;

	/** 默认经线是否使用虚线 */
	public static final boolean DEFAULT_DASH_LONGTITUDE = Boolean.TRUE;

	/** 默认纬线是否显示刻度 */
	public static final boolean DEFAULT_DISPLAY_LATITUDE = Boolean.TRUE;

	/** 默认纬线是否使用虚线 */
	public static final boolean DEFAULT_DASH_LATITUDE = Boolean.TRUE;

	/** 默认是否显示X轴刻度 */
	public static final boolean DEFAULT_DISPLAY_AXIS_X_TITLE = Boolean.TRUE;

	/** 默认是否显示X轴刻度 */
	public static final boolean DEFAULT_DISPLAY_AXIS_Y_TITLE = Boolean.TRUE;

	/** 默认是否显示边距 */
	public static final boolean DEFAULT_DISPLAY_BORDER = Boolean.TRUE;

	/** 默认是否显示边距 */
	public static final int DEFAULT_BORDER_COLOR = Color.RED;

	/** 默认经线刻度字体颜色 **/
	private int DEFAULT_LONGTITUDE_FONT_COLOR = Color.WHITE;

	/** 默认经线刻度字体颜色 **/
	private int DEFAULT_LONGTITUDE_FONT_SIZE = 12;

	/** 默认经线刻度字体颜色 **/
	private int DEFAULT_LATITUDE_FONT_COLOR = Color.RED;;

	/** 默认经线刻度字体颜色 **/
	private int DEFAULT_LATITUDE_FONT_SIZE = 12;

	/** 默认Y轴刻度显示长度 */
	private int DEFAULT_AXIS_Y_MAX_TITLE_LENGTH = 5;

	/** 默认虚线效果 */
	public static final PathEffect DEFAULT_DASH_EFFECT = new DashPathEffect(
			new float[] { 3, 3, 3, 3 }, 1);

	/** 在控件被点击时显示十字横线 */
	public static final boolean DEFAULT_DISPLAY_CROSS_X_ON_TOUCH = false;

	/** 在控件被点击时显示十字竖线 */
	public static final boolean DEFAULT_DISPLAY_CROSS_Y_ON_TOUCH = false;

	/**
	 * /** 背景色
	 */
	private int backgroudColor = DEFAULT_BACKGROUD_COLOR;

	/** X轴颜色 */
	private int axisXColor = DEFAULT_AXIS_X_COLOR;

	/** Y轴颜色 */
	private int axisYColor = DEFAULT_AXIS_Y_COLOR;

	/** 经线颜色 */
	private int longitudeColor = DEFAULT_LONGITUDE_COLOR;

	/** 纬线颜色 */
	private int latitudeColor = DEFAULT_LAITUDE_COLOR;

	/** 轴线左边距*/
	private float axisMarginLeft = DEFAULT_AXIS_MARGIN_LEFT;

	/** 轴线底边距*/
	private float axisMarginBottom = DEFAULT_AXIS_MARGIN_BOTTOM;

	/** 轴线上边距*/
	private float axisMarginTop = DEFAULT_AXIS_MARGIN_TOP;

	/** 轴线右边距*/
	private float axisMarginRight = DEFAULT_AXIS_MARGIN_RIGHT;

	/** 经线是否显示 */
	private boolean displayAxisXTitle = DEFAULT_DISPLAY_AXIS_X_TITLE;

	/** 经线是否显示 */
	private boolean displayAxisYTitle = DEFAULT_DISPLAY_AXIS_Y_TITLE;

	/** 经线是否显示 */
	private boolean displayLongitude = DEFAULT_DISPLAY_LONGTITUDE;

	/** 经线是否使用虚线 */
	private boolean dashLongitude = DEFAULT_DASH_LONGTITUDE;

	/** 纬线是否显示 */
	private boolean displayLatitude = DEFAULT_DISPLAY_LATITUDE;

	/** 纬线是否使用虚线 */
	private boolean dashLatitude = DEFAULT_DASH_LATITUDE;

	/** 虚线效果 */
	private PathEffect dashEffect = DEFAULT_DASH_EFFECT;

	/** 显示边距*/
	private boolean displayBorder = DEFAULT_DISPLAY_BORDER;

	/** 边框默认色 */
	private int borderColor = DEFAULT_BORDER_COLOR;

	/** 经线刻度字体颜色 **/
	private int longtitudeFontColor = DEFAULT_LONGTITUDE_FONT_COLOR;

	/** 经线刻度字体颜色 **/
	private int longtitudeFontSize = DEFAULT_LONGTITUDE_FONT_SIZE;

	/** 经线刻度字体颜色 **/
	private int latitudeFontColor = DEFAULT_LATITUDE_FONT_COLOR;

	/** 经线刻度字体颜色 **/
	private int latitudeFontSize = DEFAULT_LATITUDE_FONT_SIZE;

	/** 横轴刻度内容*/
	private List<String> axisXTitles;

	public void setAxisXendTitles(List<String> axisXendTitles) {
		this.axisXendTitles = axisXendTitles;
	}

	private List<String> axisXendTitles;

	public boolean getShowEndTitles() {
		return showEndTitles;
	}

	public void setShowEndTitles(boolean isShow) {
		this.showEndTitles = isShow;
	}

	private boolean showEndTitles = false;

	/** 纵轴刻度内容*/
	private List<String> axisYTitles;

	private List<String> axisYStickTitles;

	/** 纵轴刻度最大字符数 */
	private int axisYMaxTitleLength = DEFAULT_AXIS_Y_MAX_TITLE_LENGTH;

	/** 在控件被点击时时候显示十字竖线 */
	private boolean displayCrossXOnTouch = DEFAULT_DISPLAY_CROSS_X_ON_TOUCH;

	/** 在控件被点击时是否显示十字横线线 */
	private boolean displayCrossYOnTouch = DEFAULT_DISPLAY_CROSS_Y_ON_TOUCH;

	/** 选中位置X坐标*/
	private float clickPostX = 0f;

	/** 选中位置Y坐标*/
	private float clickPostY = 0f;

	/** 通知对象列表 */
	private List<ITouchEventResponse> notifyList;

	/** 当前被选中点*/
	private PointF touchPoint;

	private static Context mContext;
	private DecimalFormat df = new DecimalFormat("0.00");


	public GridChartView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public GridChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public GridChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}


	private void init() {
		axisMarginLeft = MathUtil.getInstance().dip2px(mContext, 40);
		axisMarginBottom = MathUtil.getInstance().dip2px(mContext, 50);
		axisMarginRight = 5;
		showEndTitles = false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			result = Math.min(result, specSize);
		}
		return result;
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			result = Math.min(result, specSize);
		}
		return result;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getY() > 0
				&& event.getY() < super.getBottom() - getAxisMarginBottom()
				&& event.getX() > super.getLeft() + getAxisMarginLeft()
				&& event.getX() < super.getRight()) {
			if (event.getPointerCount() == 1) {
				clickPostX = event.getX();
				clickPostY = event.getY();

				PointF point = new PointF(clickPostX,clickPostY);
				touchPoint = point;
//				super.invalidate();

//				notifyEventAll(this);

			} else if (event.getPointerCount() == 2) {
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 绘制半透明文本
	 *
	 * @param ptStart
	 * @param ptEnd
	 * @param content
	 * @param fontSize
	 * @param canvas
	 */

	private void drawAlphaTextBox(PointF ptStart, PointF ptEnd, String content,
			int fontSize, Canvas canvas) {

		Paint mPaintBox = new Paint();
		mPaintBox.setColor(Color.BLACK);
		mPaintBox.setAlpha(80);


		Paint mPaintBoxLine = new Paint();
		mPaintBoxLine.setColor(Color.CYAN);
		mPaintBoxLine.setAntiAlias(true);

		canvas.drawRoundRect(new RectF(ptStart.x, ptStart.y, ptEnd.x, ptEnd.y),
				20.0f, 20.0f, mPaintBox);

		canvas.drawLine(ptStart.x, ptStart.y, ptStart.x, ptEnd.y,mPaintBoxLine);
		canvas.drawLine(ptStart.x, ptEnd.y, ptEnd.x, ptEnd.y, mPaintBoxLine);
		canvas.drawLine(ptEnd.x, ptEnd.y, ptEnd.x, ptStart.y, mPaintBoxLine);
		canvas.drawLine(ptEnd.x, ptStart.y, ptStart.x, ptStart.y,mPaintBoxLine);

		canvas.drawText(content, ptStart.x, ptEnd.y, mPaintBoxLine);
	}

	public String getAxisXGraduate(Object value) {

		float length = super.getWidth() - axisMarginLeft - 2 * axisMarginRight;
		float valueLength = ((Float) value).floatValue() - axisMarginLeft
				- axisMarginRight;

		return String.valueOf(valueLength / length);
	}

	public String getAxisYGraduate(Object value) {

		float length = super.getHeight() - axisMarginBottom - 2 * axisMarginTop;
		float valueLength = length
				- (((Float) value).floatValue() - axisMarginTop);

		return String.valueOf(valueLength / length);
	}

	protected void drawWithFingerClick(Canvas canvas) {
		Paint mPaint = new Paint();
		mPaint.setColor(Color.CYAN);

		// 水平线长度
		float lineHLength = getWidth() - 2f;
		// 垂直线高度
		float lineVLength = getHeight() - 2f;

		// 绘制横纵线
		if (isDisplayAxisXTitle()) {
			lineVLength = lineVLength - axisMarginBottom;

			if (clickPostX > 0 && clickPostY > 0) {
				if (displayCrossXOnTouch) {
					PointF BoxVS = new PointF(clickPostX - longtitudeFontSize
							* 5f / 2f, lineVLength + 2f);
					PointF BoxVE = new PointF(clickPostX + longtitudeFontSize
							* 5f / 2f, lineVLength + axisMarginBottom - 1f);

					drawAlphaTextBox(BoxVS, BoxVE,
							getAxisXGraduate(clickPostX), longtitudeFontSize,
							canvas);
				}
			}
		}

		if (isDisplayAxisYTitle()) {
			lineHLength = lineHLength - getAxisMarginLeft();

			if (clickPostX > 0 && clickPostY > 0) {
				if (displayCrossYOnTouch) {
					PointF BoxHS = new PointF(1f, clickPostY - latitudeFontSize
							/ 2f);
					PointF BoxHE = new PointF(axisMarginLeft, clickPostY
							+ latitudeFontSize / 2f);

					drawAlphaTextBox(BoxHS, BoxHE,
							getAxisYGraduate(clickPostY), latitudeFontSize,
							canvas);
				}
			}
		}

		if (clickPostX > 0 && clickPostY > 0) {
			// 显示纵线
			if (displayCrossXOnTouch) {
				canvas
						.drawLine(clickPostX, 1f, clickPostX, lineVLength,
								mPaint);
			}

			// 显示横线
			if (displayCrossYOnTouch) {
				canvas.drawLine(axisMarginLeft, clickPostY, axisMarginLeft
						+ lineHLength, clickPostY, mPaint);
			}
		}
	}

	/**
	 * 绘制股票chart
	 *
	 * @param canvas
	 */
	protected void drawBorder(Canvas canvas) {
		float width = super.getWidth() - DEFAULT_AXIS_BOARD_MARGIN_RIGHT - axisMarginRight;
		float height = super.getHeight() - 2;

		Paint mPaint = new Paint();
		mPaint.setColor(borderColor);
		float postY = super.getHeight() - axisMarginBottom - 1;
		float postX = axisMarginLeft + 1;


		// 绘制上、右边距
		canvas.drawLine(postX, DEFAULT_AXIS_BOARD_MARGIN_TOP, width, DEFAULT_AXIS_BOARD_MARGIN_TOP, mPaint);
		canvas.drawLine(width, postY, width, DEFAULT_AXIS_BOARD_MARGIN_TOP, mPaint);
	}

	/**
	 * 绘制柱状图
	 * @param canvas
	 */
	protected void drawStickBorder(Canvas canvas) {
		float width = super.getWidth() - DEFAULT_AXIS_BOARD_MARGIN_RIGHT - axisMarginRight;
		float height = super.getHeight() - DEFAULT_AXIS_BOARD_MARGIN_BOTTOM;

		Paint mPaint = new Paint();
		mPaint.setColor(borderColor);
		float postY = super.getHeight() - axisMarginBottom - 1 + MathUtil.getInstance().dip2px(mContext, 8);
		float postX = axisMarginLeft + 1;

		canvas.drawLine(postX, height, width, height, mPaint); //底部横线
		canvas.drawLine(postX, postY, width, postY, mPaint);   //顶部横线
		canvas.drawLine(postX, height, postX, postY, mPaint);  //左侧竖线
		canvas.drawLine(width, height, width, postY, mPaint);  //右侧竖线
	}




	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 设置背景色
		super.setBackgroundColor(backgroudColor);
		canvas.drawColor(0xff000000);

		// 绘制经线纬线
		if (displayLongitude || displayAxisXTitle) {
			drawAxisGridX(canvas);
			drawStickAxisGridX(canvas);
		}
		if (displayLatitude || displayAxisYTitle) {
			drawAxisGridY(canvas);
			drawStickAxisGridY(canvas);
		}

		// 绘制XY轴
		drawXAxis(canvas);
		drawYAxis(canvas);

		// 绘制边距
		if (this.displayBorder) {
			drawBorder(canvas);
			drawStickBorder(canvas);
		}

		// 绘制十字坐标
		if (displayCrossXOnTouch || displayCrossYOnTouch) {
			drawWithFingerClick(canvas);
		}
	}

	/**
	 * 获取柱状图的最大高度
	 * @return
	 */
	public float getStickHeight() {
		return axisMarginBottom + 1 - MathUtil.getInstance().dip2px(mContext, 8) - DEFAULT_AXIS_BOARD_MARGIN_BOTTOM;
	}

	/**
	 * 绘制纬线
	 *
	 * @param canvas
	 */
	protected void drawStickAxisGridY(Canvas canvas) {
		float postX = axisMarginLeft + 1;
		float width = super.getWidth() - DEFAULT_AXIS_BOARD_MARGIN_RIGHT - axisMarginRight;
		float postY = super.getHeight() - axisMarginBottom - 1 + MathUtil.getInstance().dip2px(mContext, 8);
		float height = axisMarginBottom + 1 - MathUtil.getInstance().dip2px(mContext, 8) - DEFAULT_AXIS_BOARD_MARGIN_BOTTOM ;
		if (null != axisYStickTitles) {
			int counts = axisYStickTitles.size();
			// 线条Paint
			Paint mPaintLine = new Paint();
			mPaintLine.setColor(latitudeColor);
			if (dashLatitude) {
				mPaintLine.setPathEffect(dashEffect);
			}

			Paint mPaintFont = new Paint();
			mPaintFont.setColor(latitudeFontColor);
			mPaintFont.setTextSize(latitudeFontSize);
			mPaintFont.setAntiAlias(true);

			Paint.FontMetrics fontMetrics = mPaintFont.getFontMetrics();
			float fontHeight =  fontMetrics.leading + fontMetrics.ascent + fontMetrics.descent;
			if (counts > 1) {
				float postOffset = height / (counts - 1);
				for (int i = 0; i <= counts; i++) {
					// 绘制线条
					if (displayLatitude) {
						canvas.drawLine(postX,
								postY + i * postOffset , width, postY + i * postOffset,
								mPaintLine);
					}
					// 绘制刻度
					if (displayAxisYTitle) {

						if (i < counts - 1 && i > 0) {
							float fontWidth = mPaintFont.measureText(df.format(Float.valueOf(axisYStickTitles.get(i))));
							canvas.drawText(df.format(Float.valueOf(axisYStickTitles.get(i))), axisMarginLeft - fontWidth - 2, postY + i
											* postOffset + latitudeFontSize / 2f,
									mPaintFont);
						} else if (0 == i) {
							float fontWidth = mPaintFont.measureText(df.format(Float.valueOf(axisYStickTitles.get(i))));
							canvas.drawText(df.format(Float.valueOf(axisYStickTitles.get(i))), axisMarginLeft - fontWidth - 2, super
									.getHeight() - DEFAULT_AXIS_BOARD_MARGIN_BOTTOM , mPaintFont);
						} else if(i == counts - 1) {
							float fontWidth = mPaintFont.measureText(df.format(Float.valueOf(axisYStickTitles.get(i))));
							canvas.drawText(df.format(Float.valueOf(axisYStickTitles.get(i))), axisMarginLeft - fontWidth - 2 ,
									super.getHeight() - axisMarginBottom - 1 + MathUtil.getInstance().dip2px(mContext, 8) - fontHeight,
									mPaintFont);
						}
					}
				}
			}
		}
	}

	/**
	 * 绘制经线
	 *
	 * @param canvas
	 */
	protected void drawStickAxisGridX(Canvas canvas) {
		float height = super.getHeight() - DEFAULT_AXIS_BOARD_MARGIN_BOTTOM;
		float postY = super.getHeight() - axisMarginBottom - 1 + MathUtil.getInstance().dip2px(mContext, 8);
		if (null != axisXTitles) {

			int counts = axisXTitles.size();

			Paint mPaintLine = new Paint();
			mPaintLine.setColor(longitudeColor);
			if (dashLongitude) {
				mPaintLine.setPathEffect(dashEffect);
			}

			Paint mPaintFont = new Paint();
			mPaintFont.setColor(longtitudeFontColor);
			mPaintFont.setTextSize(longtitudeFontSize);
			mPaintFont.setAntiAlias(true);
			if (counts > 1) {
				float postOffset = (super.getWidth() - axisMarginLeft - 2 - axisMarginRight)
						/ (counts - 1);
				float offset = axisMarginLeft  + 1;

				for (int i = 0; i <= counts; i++) {
					// 绘制线条
					if (displayLongitude && i != 0) {
						canvas.drawLine(offset + i * postOffset, height, offset + i
								* postOffset, postY, mPaintLine);
					}

				}
			}
		}
	}


	/**
	 * 绘制经线
	 *
	 * @param canvas
	 */
	protected void drawAxisGridX(Canvas canvas) {

		if (null != axisXTitles) {

			int counts = axisXTitles.size();
			float length = super.getHeight() - axisMarginBottom ;
			Paint mPaintLine = new Paint();
			mPaintLine.setColor(longitudeColor);
			if (dashLongitude) {
				mPaintLine.setPathEffect(dashEffect);
			}

			Paint mPaintFont = new Paint();
			mPaintFont.setColor(longtitudeFontColor);
			mPaintFont.setTextSize(longtitudeFontSize);
			mPaintFont.setAntiAlias(true);
			if (counts > 1) {
				float postOffset = (super.getWidth() - axisMarginLeft - 2 - axisMarginRight)
						/ (counts - 1);
				float offset = axisMarginLeft  + 1;

				for (int i = 0; i <= counts; i++) {
					// 绘制线条
					if (displayLongitude && i != 0) {
						canvas.drawLine(offset + i * postOffset, DEFAULT_AXIS_BOARD_MARGIN_TOP, offset + i
								* postOffset, length, mPaintLine);
					}
					// 绘制刻度
					if (displayAxisXTitle) {
						if (i < counts - 1 && i > 0) {
							canvas.drawText(axisXTitles.get(i), offset + i
									* postOffset
									- (axisXTitles.get(i).length())
									* longtitudeFontSize / 4, super
									.getHeight()
									- axisMarginBottom + longtitudeFontSize,
									mPaintFont);
						} else if (0 == i) {
							canvas.drawText(axisXTitles.get(i),
									this.axisMarginLeft + 2f, super.getHeight()
											- axisMarginBottom
											+ longtitudeFontSize, mPaintFont);
						} else if(i == counts - 1) {
							canvas.drawText(axisXTitles.get(i), offset + i
											* postOffset
											- (axisXTitles.get(i).length())
											* longtitudeFontSize / 2, super
											.getHeight()
											- axisMarginBottom + longtitudeFontSize,
									mPaintFont);
						}
					}
				}
			}
		}
	}

	/**
	 * 绘制Y轴
	 *
	 * @param canvas
	 */
	protected void drawYAxis(Canvas canvas) {

		float length = super.getHeight() - axisMarginBottom;
		float postX = axisMarginLeft + 1;

		Paint mPaint = new Paint();
		mPaint.setColor(axisXColor);

		canvas.drawLine(postX, DEFAULT_AXIS_BOARD_MARGIN_TOP, postX, length, mPaint);
	}
	/**
	 * 绘制X轴
	 *
	 * @param canvas
	 */
	protected void drawXAxis(Canvas canvas) {

		float length = super.getWidth() - DEFAULT_AXIS_BOARD_MARGIN_RIGHT - axisMarginRight;
		float postY = super.getHeight() - axisMarginBottom - 1;
		float postX = axisMarginLeft + 1;

		Paint mPaint = new Paint();
		mPaint.setColor(axisXColor);

		canvas.drawLine(postX, postY, length, postY, mPaint);

	}


	/**
	 * 绘制纬线
	 *
	 * @param canvas
	 */
	protected void drawAxisGridY(Canvas canvas) {
		if (null != axisYTitles) {
			int counts = axisYTitles.size();
			float length = super.getWidth() - axisMarginLeft - axisMarginRight;
			// 线条Paint
			Paint mPaintLine = new Paint();
			mPaintLine.setColor(latitudeColor);
			if (dashLatitude) {
				mPaintLine.setPathEffect(dashEffect);
			}

			Paint mPaintFont = new Paint();
			mPaintFont.setColor(latitudeFontColor);
			mPaintFont.setTextSize(latitudeFontSize);
			mPaintFont.setAntiAlias(true);

			Paint.FontMetrics fontMetrics = mPaintFont.getFontMetrics();
			float fontHeight =  fontMetrics.leading + fontMetrics.ascent + fontMetrics.descent;

			if (counts > 1) {
				float postOffset = (super.getHeight() - axisMarginBottom)
						/ (counts - 1);
				float offset = super.getHeight() - axisMarginBottom - 1;
				for (int i = 0; i <= counts; i++) {
					// 绘制线条
					if (displayLatitude) {
						canvas.drawLine(axisMarginLeft + 1,
								offset - i * postOffset , axisMarginLeft
										+ length, offset - i * postOffset,
								mPaintLine);
					}
					// 绘制刻度
					if (displayAxisYTitle) {

						if (i < counts - 1 && i > 0) {
							float fontWidth = mPaintFont.measureText(df.format(Float.valueOf(axisYTitles.get(i))));
							if(i > 2) {
								mPaintFont.setColor(getResources().getColor(R.color.text_stock_red));
							} else if(i < 2) {
								mPaintFont.setColor(getResources().getColor(R.color.text_stock_green));
							} else if(i == 2) {
								mPaintFont.setColor(getResources().getColor(R.color.white));
							}
							canvas.drawText(df.format(Float.valueOf(axisYTitles.get(i))), axisMarginLeft - fontWidth - 2,
									offset - i * postOffset + latitudeFontSize / 2f,
									mPaintFont);

							if(showEndTitles && axisXendTitles != null ) {
								canvas.drawText(axisXendTitles.get(i), axisMarginLeft + length + 2,
										offset - i * postOffset + latitudeFontSize / 2f, mPaintFont);
							}
						} else if (0 == i) {
							float fontWidth = mPaintFont.measureText(df.format(Float.valueOf(axisYTitles.get(i))));
							mPaintFont.setColor(getResources().getColor(R.color.text_stock_green));
							canvas.drawText(df.format(Float.valueOf(axisYTitles.get(i))), axisMarginLeft - fontWidth - 2,
									super.getHeight() - this.axisMarginBottom - 2f, mPaintFont);

							if(showEndTitles && axisXendTitles != null) {
								canvas.drawText(axisXendTitles.get(i), axisMarginLeft + length + 2,
										super.getHeight() - this.axisMarginBottom - 2f, mPaintFont);
							}

						} else if(i == counts - 1) {
							float fontWidth = mPaintFont.measureText(df.format(Float.valueOf(axisYTitles.get(i))));
							mPaintFont.setColor(getResources().getColor(R.color.text_stock_red));
							canvas.drawText(df.format(Float.valueOf(axisYTitles.get(i))), axisMarginLeft - fontWidth - 2 ,
									DEFAULT_AXIS_BOARD_MARGIN_TOP - fontHeight + 1,
									mPaintFont);

							if(showEndTitles && axisXendTitles != null) {
								canvas.drawText(axisXendTitles.get(i), axisMarginLeft +length + 2,
										DEFAULT_AXIS_BOARD_MARGIN_TOP - fontHeight + 1, mPaintFont);
							}
						}
					}
				}
			}
		}
	}

	public void notifyEvent(GridChartView chart) {
		PointF point = chart.getTouchPoint();
		if(null != point){
			clickPostX = point.x;
			clickPostY = point.y;
		}
		touchPoint = new PointF(clickPostX , clickPostY);
		super.invalidate();
	}

	public void addNotify(ITouchEventResponse notify) {
		if (null == notifyList) {
			notifyList = new ArrayList<ITouchEventResponse>();
		}
		notifyList.add(notify);
	}

	public void removeNotify(int i) {
		if (null != notifyList && notifyList.size() > i) {
			notifyList.remove(i);
		}
	}

	public void removeAllNotify() {
		if (null != notifyList) {
			notifyList.clear();
		}
	}

	public void notifyEventAll(GridChartView chart) {
		if (null != notifyList) {
			for (int i = 0; i < notifyList.size(); i++) {
				ITouchEventResponse ichart = notifyList.get(i);
				ichart.notifyEvent(chart);
			}
		}
	}


	public int getBackgroudColor() {
		return backgroudColor;
	}

	public void setBackgroudColor(int backgroudColor) {
		this.backgroudColor = backgroudColor;
	}

	public int getAxisXColor() {
		return axisXColor;
	}

	public void setAxisXColor(int axisXColor) {
		this.axisXColor = axisXColor;
	}

	public int getAxisYColor() {
		return axisYColor;
	}

	public void setAxisYColor(int axisYColor) {
		this.axisYColor = axisYColor;
	}

	public int getLongitudeColor() {
		return longitudeColor;
	}

	public void setLongitudeColor(int longitudeColor) {
		this.longitudeColor = longitudeColor;
	}

	public int getLatitudeColor() {
		return latitudeColor;
	}

	public void setLatitudeColor(int latitudeColor) {
		this.latitudeColor = latitudeColor;
	}

	public float getAxisMarginLeft() {
		return axisMarginLeft;
	}

	public void setAxisMarginLeft(float axisMarginLeft) {
		this.axisMarginLeft = axisMarginLeft;
		if (0f == axisMarginLeft) {
			this.displayAxisYTitle = Boolean.FALSE;
		}
	}

	public float getAxisMarginBottom() {
		return axisMarginBottom;
	}

	public void setAxisMarginBottom(float axisMarginBottom) {
		this.axisMarginBottom = axisMarginBottom;

		if (0f == axisMarginBottom) {
			this.displayAxisXTitle = Boolean.FALSE;
		}
	}

	public float getAxisMarginTop() {
		return axisMarginTop;
	}

	public void setAxisMarginTop(float axisMarginTop) {
		this.axisMarginTop = axisMarginTop;
	}

	public float getAxisMarginRight() {
		return axisMarginRight;
	}

	public void setAxisMarginRight(float axisMarginRight) {
		this.axisMarginRight = axisMarginRight;
	}

	public List<String> getAxisXTitles() {
		return axisXTitles;
	}

	public void setAxisXTitles(List<String> axisXTitles) {
		this.axisXTitles = axisXTitles;
	}

	public List<String> getAxisYTitles() {
		return axisYTitles;
	}

	public void setAxisYTitles(List<String> axisYTitles) {
		this.axisYTitles = axisYTitles;
	}

	public boolean isDisplayLongitude() {
		return displayLongitude;
	}

	public void setDisplayLongitude(boolean displayLongitude) {
		this.displayLongitude = displayLongitude;
	}

	public boolean isDashLongitude() {
		return dashLongitude;
	}

	public void setDashLongitude(boolean dashLongitude) {
		this.dashLongitude = dashLongitude;
	}

	public boolean isDisplayLatitude() {
		return displayLatitude;
	}

	public void setDisplayLatitude(boolean displayLatitude) {
		this.displayLatitude = displayLatitude;
	}

	public boolean isDashLatitude() {
		return dashLatitude;
	}

	public void setDashLatitude(boolean dashLatitude) {
		this.dashLatitude = dashLatitude;
	}

	public PathEffect getDashEffect() {
		return dashEffect;
	}

	public void setDashEffect(PathEffect dashEffect) {
		this.dashEffect = dashEffect;
	}

	public boolean isDisplayAxisXTitle() {
		return displayAxisXTitle;
	}

	public void setDisplayAxisXTitle(boolean displayAxisXTitle) {
		this.displayAxisXTitle = displayAxisXTitle;

		// 如果不显示X轴刻度,则底边边距为0
		if (false == displayAxisXTitle) {
			this.axisMarginBottom = 0;
		}
	}

	public boolean isDisplayAxisYTitle() {
		return displayAxisYTitle;
	}

	public void setDisplayAxisYTitle(boolean displayAxisYTitle) {
		this.displayAxisYTitle = displayAxisYTitle;

		// 如果不显示Y轴刻度,则左边边距为0
		if (false == displayAxisYTitle) {
			this.axisMarginLeft = 0;
		}
	}

	public void setDefaultAxisMarginLeft(float left) {
		axisMarginLeft = left;
	}

	public void setDefaultAxisMarginBottom(float bottom) {
		axisMarginBottom = bottom;
	}

	public boolean isDisplayBorder() {
		return displayBorder;
	}

	public void setDisplayBorder(boolean displayBorder) {
		this.displayBorder = displayBorder;
	}

	public int getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
	}

	public int getLongtitudeFontColor() {
		return longtitudeFontColor;
	}

	public void setLongtitudeFontColor(int longtitudeFontColor) {
		this.longtitudeFontColor = longtitudeFontColor;
	}

	public int getLongtitudeFontSize() {
		return longtitudeFontSize;
	}

	public void setLongtitudeFontSize(int longtitudeFontSize) {
		this.longtitudeFontSize = longtitudeFontSize;
	}

	public int getLatitudeFontColor() {
		return latitudeFontColor;
	}

	public void setLatitudeFontColor(int latitudeFontColor) {
		this.latitudeFontColor = latitudeFontColor;
	}

	public int getLatitudeFontSize() {
		return latitudeFontSize;
	}

	public void setLatitudeFontSize(int latitudeFontSize) {
		this.latitudeFontSize = latitudeFontSize;
	}

	public int getAxisYMaxTitleLength() {
		return axisYMaxTitleLength;
	}

	public void setAxisYMaxTitleLength(int axisYMaxTitleLength) {
		this.axisYMaxTitleLength = axisYMaxTitleLength;
	}

	public boolean isDisplayCrossXOnTouch() {
		return displayCrossXOnTouch;
	}

	public void setDisplayCrossXOnTouch(boolean displayCrossXOnTouch) {
		this.displayCrossXOnTouch = displayCrossXOnTouch;
	}

	public boolean isDisplayCrossYOnTouch() {
		return displayCrossYOnTouch;
	}

	public void setDisplayCrossYOnTouch(boolean displayCrossYOnTouch) {
		this.displayCrossYOnTouch = displayCrossYOnTouch;
	}

	public PointF getTouchPoint() {
		return touchPoint;
	}

	public void setTouchPoint(PointF touchPoint) {
		this.touchPoint = touchPoint;
	}

	public void setAxisYStickTitles(List<String> stick) {
		axisYStickTitles = stick;
	}


}
