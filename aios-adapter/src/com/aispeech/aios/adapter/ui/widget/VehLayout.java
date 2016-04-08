package com.aispeech.aios.adapter.ui.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.bean.VehicleRestrictionBean;
import com.aispeech.aios.adapter.util.VolleyHelper;
import com.android.volley.toolbox.NetworkImageView;

import java.util.Hashtable;
import java.util.Map;

/**
 * @desc 限行页面Layout
 * @auth AISPEECH
 * @date 2016-02-11
 * @copyright aispeech.com
 */
public class VehLayout extends LinearLayout implements View.OnClickListener {
    private TextView txt_veh_content, txt_local_title, txt_out_title;
    private NetworkImageView img_veh_out, img_veh_local;
    private LinearLayout li_veh_out, li_veh_local;
    private Context mContext;
    private final static int MSG_REMOVELARGEIMAGE = 0x01;
    private Map<String, String> urlMap = new Hashtable<String, String>();
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private View mFloatLayout;
    private boolean isCreated = false;//标志悬浮窗是否创建，默认flase

    public VehLayout(Context context) {
        this(context, null);
    }

    public VehLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VehLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }

    private void initView() {
        View view = View.inflate(mContext, R.layout.vehiclerestriction_layout, this);
        txt_local_title = (TextView) view.findViewById(R.id.txt_local_title);
        txt_out_title = (TextView) view.findViewById(R.id.txt_out_title);
        txt_veh_content = (TextView) view.findViewById(R.id.txt_veh_content);
        img_veh_local = (NetworkImageView) view.findViewById(R.id.img_veh_local);
        img_veh_out = (NetworkImageView) view.findViewById(R.id.img_veh_out);
        li_veh_out = (LinearLayout) view.findViewById(R.id.li_veh_out);
        li_veh_local = (LinearLayout) view.findViewById(R.id.li_veh_local);
        img_veh_local.setOnClickListener(this);
        img_veh_out.setOnClickListener(this);

    }

    /**
     * 显示限行UI
     * @param bean 车辆限行的实体bean
     */
    public void showUI(VehicleRestrictionBean bean) {
        try {
            AILog.d("****show vehUI******" + bean.getTitle() + "," + bean.getOutPut());
            if (bean != null) {
                urlMap.clear();
                li_veh_local.setVisibility(VISIBLE);
                li_veh_out.setVisibility(VISIBLE);
//                if (bean.getTitle() != null) {
//                    StringUtil.highNightKeyword(mContext, txt_title, " /为您查到“" + bean.getTitle() + "的限行”", bean.getTitle() + "的限行");
//                }
                if (bean.getOutPut() != null) {
                    txt_veh_content.setText(bean.getOutPut());
                }
                if (bean.getRules() != null) {
                    if (bean.getRules().size() == 1) {
                        img_veh_out.setImageUrl(bean.getRules().get(0).getUrl(), VolleyHelper.getInstance().getImageLoader());
                        li_veh_local.setVisibility(GONE);
                        urlMap.put("2", bean.getRules().get(0).getUrl());
                        txt_out_title.setText(bean.getRules().get(0).getName());
                        urlMap.put("out_title", bean.getRules().get(0).getName());
                    } else if (bean.getRules().size() == 2) {
                        img_veh_local.setImageUrl(bean.getRules().get(0).getUrl(), VolleyHelper.getInstance().getImageLoader());
                        img_veh_out.setImageUrl(bean.getRules().get(1).getUrl(), VolleyHelper.getInstance().getImageLoader());
                        urlMap.put("1", bean.getRules().get(0).getUrl());
                        urlMap.put("2", bean.getRules().get(1).getUrl());
                        urlMap.put("local_title", bean.getRules().get(0).getName());
                        urlMap.put("out_title", bean.getRules().get(1).getName());
                        txt_local_title.setText(bean.getRules().get(0).getName());
                        txt_out_title.setText(bean.getRules().get(1).getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_veh_local:
                showLargeImg(1);
                break;
            case R.id.img_veh_out:
                showLargeImg(2);
                break;
        }
    }

    private void showLargeImg(int type) {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) AdapterApplication.getContext().getSystemService(
                Context.WINDOW_SERVICE);
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.gravity = Gravity.TOP;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mFloatLayout = LayoutInflater.from(AdapterApplication.getContext()).inflate(R.layout.veh_largeimg_layout,
                null);
        NetworkImageView imageView = (NetworkImageView) mFloatLayout.findViewById(R.id.img_veh_largeImg);
        imageView.setImageUrl(urlMap.get("" + type), VolleyHelper.getInstance().getImageLoader());
        TextView txt_veh_largeImgTitle = (TextView) mFloatLayout.findViewById(R.id.txt_veh_largeImgTitle);
        if (type == 1) {
            txt_veh_largeImgTitle.setText(urlMap.get("local_title"));
        } else {
            txt_veh_largeImgTitle.setText(urlMap.get("out_title"));
        }
        mWindowManager.addView(mFloatLayout, wmParams);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVehLargeImage();
            }
        });
        isCreated = true;//更改状态值
    }

    /**
     * 移除掉限行大的详细图片
     */
    public void removeVehLargeImage() {
        mHandler.sendEmptyMessage(MSG_REMOVELARGEIMAGE);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REMOVELARGEIMAGE:
                    if (mWindowManager != null && isCreated  ){
                        mWindowManager.removeView(mFloatLayout);
                        isCreated = false;

                    }
                    break;
                default:
                    break;
            }
        }
    };
}
