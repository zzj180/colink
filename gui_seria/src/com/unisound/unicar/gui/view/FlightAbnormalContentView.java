package com.unisound.unicar.gui.view;

import java.util.Random;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.Logger;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class FlightAbnormalContentView extends FrameLayout implements ISessionView {
    private static final String TAG = "FlightAbnormalContentView";
    private TextView flight_abnormal_title;
    private TextView flight_abnormal_tips;
    private Context mContext;
    private String[] flight_tips;

    public FlightAbnormalContentView(Context context) {
        super(context);
        Logger.d(TAG, "FlightAbnormalContentView create");
        this.mContext = context;

        initView();
    }

    private void initView() {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.flight_abnormal_content_view, this, true);

        flight_abnormal_title = (TextView) findViewById(R.id.tv_flight_abnormal_title);
        flight_abnormal_tips = (TextView) findViewById(R.id.tv_flight_abnormal_tips);

        flight_tips = mContext.getResources().getStringArray(R.array.flight_tips);
    }

    public void setFlightAbnormalInfo(String titleInfo) {
        flight_abnormal_title.setText(titleInfo);
        Logger.d(TAG, "FlightAbnormal titleInfo : " + titleInfo);

        int flight_tips_length = flight_tips.length;
        Random random = new Random();
        String random_tip = flight_tips[random.nextInt(flight_tips_length)];
        flight_abnormal_tips.setText(random_tip);
        Logger.d(TAG, "FlightAbnormal tips : " + random_tip);
    }

    @Override
    public boolean isTemporary() {
        return false;
    }

    @Override
    public void release() {}

}
