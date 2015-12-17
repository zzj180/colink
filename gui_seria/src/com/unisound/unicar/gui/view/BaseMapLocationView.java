/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : BaseMapLocationView.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.view
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-21
 */
package com.unisound.unicar.gui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * BaseMapLocationView
 * 
 * @author xiaodong
 * @date 20150827
 */
public class BaseMapLocationView extends FrameLayout implements ISessionView {


    public BaseMapLocationView(Context context) {
        super(context);
    }

    public BaseMapLocationView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void setMapLocationViewListener(MapLocationViewListener mapLocationViewListener) {};

    /*
     * (non-Javadoc)
     * 
     * @see com.unisound.unicar.gui.view.ISessionView#isTemporary()
     */
    @Override
    public boolean isTemporary() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.unisound.unicar.gui.view.ISessionView#release()
     */
    @Override
    public void release() {
        // TODO Auto-generated method stub
    }


    /**
     * 
     * @author xiaodong.he
     * @date 20151023
     */
    public interface MapLocationViewListener {

        public void onEditLocationFocus();

        public void onConfirmLocationOk(String locationKeyword);

        public void onConfirmLocationCancel();

        public void onMapViewMove();
    }
}
