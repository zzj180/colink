package com.unisound.unicar.gui.view;

/**
 * 
 * @author Ch
 * @deprecated 限号UI
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.TrafficControlInfo;

public class TrafficControlView extends FrameLayout implements ISessionView {

    private TextView mTvTitle;
    private TextView tv_date;// 当前时间
    // private TextView [] weeks ;
    private TextView tv_today;// 今天
    private TextView tv_rule;// 限行规则

    private Context mContext;
    private TrafficControlInfo mTrafficControlInfo;

    public TrafficControlView(Context context, TrafficControlInfo mTrafficControlInfo) {
        super(context);
        mContext = context;
        this.mTrafficControlInfo = mTrafficControlInfo;
        initView();
    }



    /**
     * 初始化控件
     */
    private void initView() {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.traffic_control_view, this, true);
        mTvTitle = (TextView) view.findViewById(R.id.tv_traffic_control_title);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        // weeks = new TextView[]{
        // (TextView) findViewById(R.id.tv_traffic_control_number_w1),
        // (TextView) findViewById(R.id.tv_traffic_control_number_w2),
        // (TextView) findViewById(R.id.tv_traffic_control_number_w3),
        // (TextView) findViewById(R.id.tv_traffic_control_number_w4),
        // (TextView) findViewById(R.id.tv_traffic_control_number_w5),
        // (TextView) findViewById(R.id.tv_traffic_control_number_w6),
        // (TextView) findViewById(R.id.tv_traffic_control_number_w7)};
        tv_today = (TextView) view.findViewById(R.id.tv_traffic_control_number_today_1);
        tv_rule = (TextView) view.findViewById(R.id.tv_traffic_control_rule_content);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (mTrafficControlInfo == null) {
            return;
        }
        // mTvTitle.setText(mTrafficControlInfo.getTitleText()); //TODO:

        tv_date.setText(mTrafficControlInfo.getDateInfo());
        tv_today.setText(mTrafficControlInfo.getToady());
        // for (int i = 0; i < weeks.length; i++) {
        // weeks[i].setText(mTrafficControlInfo.getWeeks()[i]);
        // }
        tv_rule.setText(mTrafficControlInfo.getTrafficRule());

    }

    public TrafficControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public TrafficControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }

    @Override
    public boolean isTemporary() {
        return false;
    }

    @Override
    public void release() {}

    /**
     * 设置显示内容
     * 
     * @param text
     */
    // public void setShowText(String text){
    // if(mTv != null)
    // mTv.setText(text);
    // }
    //
    // public TextView getmTv() {
    // return mTv;
    // }


}
