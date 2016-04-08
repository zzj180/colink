package com.aispeech.aios.adapter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.node.HomeNode;
import com.aispeech.aios.adapter.node.NearbyNode;
import com.aispeech.aios.adapter.node.PhoneNode;
import com.aispeech.aios.adapter.node.TTSNode;
import com.aispeech.aios.adapter.util.SendBroadCastUtil;
import com.aispeech.aios.adapter.util.MapOperateUtil;

import java.util.List;

/**
 * @desc 附近搜索列表适配器
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class NearbyAdapter extends BaseAdapter {
    private List<Object> l;//数据
    private Context mContext;//上下文

    public NearbyAdapter(Context context, List<Object> list) {
        this.l = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return l.size();
    }

    @Override
    public Object getItem(int position) {
        return l.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder = null;
        if (null == convertView) {
            mHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.nearby_item, null);
//            mHolder.address = (TextView) convertView.findViewById(R.id.nearby_area);
            mHolder.distance = (TextView) convertView.findViewById(R.id.nearby_distance);
            mHolder.name = (TextView) convertView.findViewById(R.id.nearby_address);
            mHolder.image_nav = (ImageView) convertView.findViewById(R.id.nearby_nav);
            mHolder.image_phone = (ImageView) convertView.findViewById(R.id.nearby_phone);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.name.setText(String.valueOf(position + 1) + ". " + ((PoiBean) l.get(position)).getName());//设置poi名称
        mHolder.distance.setText(((PoiBean) l.get(position)).getDisplayDistance());//设置距离
//        mHolder.address.setText(l.get(position).getAddress());//设置地址
        if (!TextUtils.isEmpty(((PoiBean) l.get(position)).getTelephone())) {//如果电话号码不为空
            mHolder.image_phone.setImageResource(R.drawable.nearby_phone_selected);//将电话图片设置可选状态

            mHolder.image_phone.setOnClickListener(new View.OnClickListener() {//电话点击事件
                @Override
                public void onClick(View v) {
                    if (PhoneNode.getInstance().getPhoneState()) {
                        SendBroadCastUtil.getInstance().sendBroadCast("action.intent.AIOS_DIAL", "number", ((PoiBean) l.get(position)).getTelephone());//调用蓝牙APP打电话
                    } else {
                        TTSNode.getInstance().play("连接蓝牙后我才能帮你打电话哦");
                    }
                    NearbyNode.getInstance().getBusClient().publish(AiosApi.Other.UI_CLICK);////语音交互停止
                    UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, null, null);
                }
            });

        } else {
            mHolder.image_phone.setImageResource(R.drawable.nearby_phone_nselected);//将电话图片设置可选状态
        }

        mHolder.image_nav.setOnClickListener(new View.OnClickListener() {//导航点击事件
            @Override
            public void onClick(View v) {
                HomeNode.getInstance().getBusClient().publish(AiosApi.Other.UI_CLICK);//语音交互停止
                MapOperateUtil.getInstance().startNavigation(((PoiBean) l.get(position)));//导航
                UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, null, null);
            }
        });
        //设置点击事件，暂先空缺
        return convertView;
    }

    public static final class ViewHolder {
        public TextView distance;//距离  1000Km
        public TextView name;// poi名称  北京天安门
        public ImageView image_phone;//电话图标
        public ImageView image_nav;//导航图标
    }
}
