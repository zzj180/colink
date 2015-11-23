/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : AroundSearchKeywordAdapter.java
 * @ProjectName : uniCarSolution_dev_xd_20151010
 * @PakageName : com.unisound.unicar.gui.adapter
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-27
 */
package com.unisound.unicar.gui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.AMapPoiAroundSearchView;

/**
 * 
 * @author xiaodong.he
 * @date 2015-10-27
 */
public class AroundSearchKeywordAdapter extends BaseAdapter {

    private static final String TAG = AroundSearchKeywordAdapter.class.getSimpleName();

    private ArrayList<String> list = new ArrayList<String>();
    private Context activity = null;
    private Handler handler;

    /**
     * 
     * @param context
     * @param handler
     * @param list
     */
    public AroundSearchKeywordAdapter(Context context, Handler handler, ArrayList<String> list) {
        this.activity = context;
        this.handler = handler;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView =
                    LayoutInflater.from(activity).inflate(
                            R.layout.localsearch_pop_keyword_list_item, null);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_localsearch_keyword);
            holder.imageView =
                    (ImageView) convertView.findViewById(R.id.iv_localsearch_delete_keyword);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Logger.d(TAG, "getView--data size:" + list.size() + "; text: " + list.get(position));

        holder.textView.setText(list.get(position));

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt("index", position);
                msg.setData(data);
                msg.what = AMapPoiAroundSearchView.MSG_ON_HELP_KEYWORD_SELECT;
                handler.sendMessage(msg);
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt("index", position);
                msg.setData(data);
                msg.what = AMapPoiAroundSearchView.MSG_ON_HELP_KEYWORD_DELETE;
                handler.sendMessage(msg);
            }
        });

        return convertView;
    }

}


class ViewHolder {
    TextView textView;
    ImageView imageView;
}
