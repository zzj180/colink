package com.unisound.unicar.gui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unisound.unicar.gui.R;
import com.unisound.unicar.gui.model.FunctionHelpTextInfo;
import com.unisound.unicar.gui.utils.Logger;

/**
 * 
 * @author xiaodong
 * @date 20150713
 */
public class FunctionHelpAdapter extends BaseAdapter {

    private static final String TAG = FunctionHelpAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<FunctionHelpTextInfo> lists;
    private LayoutInflater inflater;

    public FunctionHelpAdapter(Context context, ArrayList<FunctionHelpTextInfo> lists) {
        super();
        this.mContext = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.function_help_activity_list_item, null);
            holder.tvHelpText = (TextView) view.findViewById(R.id.tv_function_item_text);
            holder.tvIsOffline = (TextView) view.findViewById(R.id.tv_function_item_status_offline);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final FunctionHelpTextInfo info = lists.get(position);
        Logger.d(TAG, "!--->getView:---HelpText = " + info.getHelpText());
        holder.tvHelpText.setText(info.getHelpText());
        if (info.isOfflineText()) {
            holder.tvIsOffline.setVisibility(View.VISIBLE);
        } else {
            holder.tvIsOffline.setVisibility(View.GONE);
        }

        return view;
    }


    private final static class ViewHolder {
        TextView tvHelpText;
        TextView tvIsOffline;
    }

}
