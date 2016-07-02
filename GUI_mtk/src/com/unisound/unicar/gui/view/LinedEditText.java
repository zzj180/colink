/**
 * Copyright (c) 2012-2012 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : LinedEditText.java
 * @ProjectName : V Plus 1.0
 * @PakageName : cn.yunzhisheng.vui.assistant.view
 * @Author : Brant
 * @CreateDate : 2012-6-15
 */
package com.unisound.unicar.gui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.coogo.inet.vui.assistant.car.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LinedEditText extends EditText {

    public static final String TAG = "LinedEditText";
    private Rect mRect;
    private Paint mPaint;
    private int mDrawLineMode;

    /**
     * @Author : Brant
     * @CreateDate : 2012-6-15
     * @param context
     * @param attrs
     */
    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRect = new Rect();
        mPaint = new Paint();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LinedEditText);
        int color = a.getColor(R.styleable.LinedEditText_line_color, 0XFFFFFFFF);
        float stokeWidth = a.getDimension(R.styleable.LinedEditText_line_stoke_width, 2);
        float dashOnWidth = a.getDimension(R.styleable.LinedEditText_line_dash_on_width, 2);
        float dashOffWidth = a.getDimension(R.styleable.LinedEditText_line_dash_off_width, 2);
        mDrawLineMode = a.getInt(R.styleable.LinedEditText_lines_according, 1);
        a.recycle();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(stokeWidth);
        PathEffect effects = new DashPathEffect(new float[] {dashOnWidth, dashOffWidth}, 1);
        mPaint.setPathEffect(effects);
        mPaint.setColor(color);

        // Disable hardware
        // rendering:https://code.google.com/p/android/issues/detail?id=29944
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int count = getLineCount();
        int lineHeight = getLineHeight();
        Rect r = mRect;
        Paint paint = mPaint;
        int baseline = 0;
        for (int i = 0; i < count; i++) {
            baseline = getLineBounds(i, r) + r.bottom - (i + 1) * lineHeight;
            // canvas.drawLine(r.left, baseline, r.right, baseline, paint);//画底线
        }

        if (mDrawLineMode == 2) {
            int lines = (getHeight() - getPaddingTop() - getPaddingBottom()) / lineHeight;
            for (int i = count; i < lines; i++) {
                baseline += lineHeight;
                // canvas.drawLine(r.left, baseline, r.right, baseline,
                // paint);//画底线
            }
        }
        super.onDraw(canvas);
    }
}
