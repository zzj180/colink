package com.aispeech.aios.adapter.ui.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * @desc 设置和帮助按钮3D旋转动画
 * @auth AISPEECH
 * @date 2016-02-14
 * @copyright aispeech.com
 */
public class Rotate3dAnimation extends Animation{


    private float mFromDegrees = 0.0f;
    private float mToDegrees = 0.0f;
    private float mCenterX = 0.0f;
    private float mCenterY = 0.0f;
    private float mDepthZ = 0.0f;
    private Camera mCamera = null;
    OnFlipChangeListener listener = null;

    public Rotate3dAnimation(float fromDegress, float toDegrees, float centerX,
                             float centerY, float depthZ) {
        this.mFromDegrees = fromDegress;
        this.mToDegrees = toDegrees;
        this.mCenterX = centerX;
        this.mCenterY = centerY;
        this.mDepthZ = depthZ;
    }

    public Rotate3dAnimation(float centerX, float centerY) {
        this.mFromDegrees = 0.0f;
        this.mToDegrees = 180.0f;
        this.mCenterX = centerX;
        this.mCenterY = centerY;
        this.mDepthZ = 0.0f;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        if(listener != null && interpolatedTime > 0.5f) {
            listener.onFlipChange(true);
        }

        float deltaD = mFromDegrees + (mToDegrees - mFromDegrees) * interpolatedTime;
        final Matrix matrix = t.getMatrix();

        float depth = (0.5f - Math.abs(interpolatedTime - 0.5f)) * mDepthZ;
        boolean halfMore = (interpolatedTime > 0.5f);

        if(halfMore) {
            deltaD = deltaD - 180;
        }

        mCamera.save();

        mCamera.translate(0.0f, 0.0f, depth);
        mCamera.rotateY(deltaD);
        mCamera.getMatrix(matrix);

        mCamera.restore();

        matrix.preTranslate(-mCenterX, -mCenterY);
        matrix.postTranslate(mCenterX, mCenterY);
    }

    public void setOnFlipChangeListener(OnFlipChangeListener listener) {
        this.listener = listener;
    }

    public interface OnFlipChangeListener {
        void onFlipChange(boolean flipStatus);
    }
}
