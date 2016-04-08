package com.aispeech.aios.adapter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.vendor.GD.GDOperate;
import com.aispeech.aios.client.AIOSNotificationNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Spring on 2016/3/10.
 * to do:
 */
public class PickerView extends LinearLayout {
    private JSONObject mPoi = new JSONObject();
    private String mTitle;
    private TextView t_positive_text, t_negative_text;

    public PickerView(Context context, List<Object> list, String title) {
        super(context);
        this.mPoi = (JSONObject) list.get(0);
        this.mTitle = title;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(AdapterApplication.getContext());
        // 获取浮动窗口视图所在布局
        View mView = inflater.inflate(R.layout.pick_person_alert_dialog,
                this);
        TextView mTips = (TextView) mView.findViewById(R.id.t_dialog_content);
        mTips.setText(mTitle);
        t_positive_text = (TextView) mView.findViewById(R.id.t_positive_text);
        t_positive_text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UiEventDispatcher.notifyUpdateUI(UIType.DismissPicker);
                BusClient bc = AIOSNotificationNode.getInstance().getBusClient();
                if (null != bc) {
                    bc.publish(AiosApi.Other.UI_PAUSE);
                }
                PoiBean poiBean = new PoiBean();
                try {
                    poiBean.setLatitude(Double.parseDouble(mPoi.getString("latitude")));
                    poiBean.setLongitude(Double.parseDouble(mPoi.getString("longitude")));
                    GDOperate.getInstance(AdapterApplication.getContext()).startNavigation(poiBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        t_negative_text = (TextView) mView.findViewById(R.id.t_negative_text);
        t_negative_text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                BusClient bc = AIOSNotificationNode.getInstance().getBusClient();
                if (null != bc) {
                    bc.publish(AiosApi.Other.UI_PAUSE);
                }
                UiEventDispatcher.notifyUpdateUI(UIType.DismissPicker);
            }
        });
    }
}
