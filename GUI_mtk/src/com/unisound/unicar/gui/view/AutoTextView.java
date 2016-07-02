package com.unisound.unicar.gui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.ResourceUtils;

/**
 * 
 * @author zzj
 * @date 20150722
 */
public class AutoTextView extends TextSwitcher implements ViewSwitcher.ViewFactory {

    private static final String TAG = AutoTextView.class.getSimpleName();

    private float mTextSize;
    private int mTextColor;
    private Context mContext;
    // mInUp,mOutUp分别构成向下翻页的进出动画
    private Animation mInUp;
    private Animation mOutUp;

    // mInDown,mOutDown分别构成向下翻页的进出动画
    // private Rotate3dAnimation mInDown;
    // private Rotate3dAnimation mOutDown;

    public AutoTextView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public AutoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;

        getAttribute(context, attrs);

        init();
    }

    private void getAttribute(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyView);
        mTextColor = a.getColor(R.styleable.MyView_textColor, 0);

        mTextSize = a.getDimension(R.styleable.MyView_textSize, 1);
        Logger.d(TAG, "!--->getDimension mTextSize = " + mTextSize + "pix");
        mTextSize = ResourceUtils.px2dip(context, mTextSize);
        // mTextSize = ResourceUtils.getDimenXmlDefSize(mContext, R.dimen.text_size_f3);
        Logger.d(TAG, "!--->px2dip---mTextSize = " + mTextSize + "dp; mTextColor = " + mTextColor);
        a.recycle();
    }

    private void init() {
        // TODO Auto-generated method stub
        setFactory(this);
        // mInUp = createAnim(-90, 0 , true, true);
        // mOutUp = createAnim(0, 90, false, true);
        mInUp = AnimationUtils.loadAnimation(mContext, R.anim.push_up_in);
        mOutUp = AnimationUtils.loadAnimation(mContext, R.anim.push_up_out);

        // mInDown = createAnim(90, 0 , true , false);
        // mOutDown = createAnim(0, -90, false, false);

        // TextSwitcher主要用于文件切换，比如 从文字A 切换到 文字 B，
        // setInAnimation()后，A将执行inAnimation，
        // setOutAnimation()后，B将执行OutAnimation

        setInAnimation(mInUp);
        setOutAnimation(mOutUp);
    }

    // private Rotate3dAnimation createAnim(float start, float end, boolean turnIn, boolean turnUp){
    // final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end, turnIn, turnUp);
    // rotation.setDuration(800);
    // rotation.setFillAfter(false);
    // rotation.setInterpolator(new AccelerateInterpolator());
    // return rotation;
    // }

    // 这里返回的TextView，就是我们看到的View
    @Override
    public View makeView() {
        // TODO Auto-generated method stub
        TextView t = new TextView(mContext);
        t.setGravity(Gravity.LEFT);
        t.setTextSize(mTextSize);
        // Logger.d(TAG, "!--->makeView TextSize = "+ResourceUtils.getDimenXmlDefSize(mContext,
        // R.dimen.text_size_f3));
        // t.setTextSize(ResourceUtils.getDimenXmlDefSize(mContext, R.dimen.text_size_f3));//XD
        // modify 20150724
        t.setTextColor(mTextColor);
        t.setMaxLines(2);
        return t;
    }

    // 定义动作，向下滚动翻页
    // public void previous(){
    // if(getInAnimation() != mInDown){
    // setInAnimation(mInDown);
    // }
    // if(getOutAnimation() != mOutDown){
    // setOutAnimation(mOutDown);
    // }
    // }

    /** 定义动作，向上滚动翻页 */
    public void next() {
        if (getInAnimation() != mInUp) {
            setInAnimation(mInUp);
        }
        if (getOutAnimation() != mOutUp) {
            setOutAnimation(mOutUp);
        }
    }

    // class Rotate3dAnimation extends Animation {
    // private final float mFromDegrees;
    // private final float mToDegrees;
    // private float mCenterX;
    // private float mCenterY;
    // private final boolean mTurnIn;
    // private final boolean mTurnUp;
    // private Camera mCamera;
    //
    // public Rotate3dAnimation(float fromDegrees, float toDegrees, boolean turnIn, boolean turnUp)
    // {
    // mFromDegrees = fromDegrees;
    // mToDegrees = toDegrees;
    // mTurnIn = turnIn;
    // mTurnUp = turnUp;
    // }
    //
    // @Override
    // public void initialize(int width, int height, int parentWidth, int parentHeight) {
    // super.initialize(width, height, parentWidth, parentHeight);
    // mCamera = new Camera();
    // mCenterY = getHeight() / 2;
    // mCenterX = getWidth() / 2;
    // }
    //
    // @Override
    // protected void applyTransformation(float interpolatedTime, Transformation t) {
    // final float fromDegrees = mFromDegrees;
    // float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);
    //
    // final float centerX = mCenterX ;
    // final float centerY = mCenterY ;
    // final Camera camera = mCamera;
    // final int derection = mTurnUp ? 1: -1;
    //
    // final Matrix matrix = t.getMatrix();
    //
    // camera.save();
    // if (mTurnIn) {
    // camera.translate(0.0f, derection *mCenterY * (interpolatedTime - 1.0f), 0.0f);
    // } else {
    // camera.translate(0.0f, derection *mCenterY * (interpolatedTime), 0.0f);
    // }
    // camera.rotateX(degrees);
    // camera.getMatrix(matrix);
    // camera.restore();
    //
    // matrix.preTranslate(-centerX, -centerY);
    // matrix.postTranslate(centerX, centerY);
    // }
    // }
}
