package com.aispeech.aios.adapter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aispeech.aios.adapter.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/2/12.
 */
public class StockAdapter extends BaseAdapter {

    Context context;
    List<StockItem> list = null;
    boolean status;
    DecimalFormat df = new DecimalFormat("0.00");

    public StockAdapter(Context context, List<StockItem> list, boolean status) {
        this.context = context;
        this.list = list;
        this.status = status;
    }

    @Override
    public int getCount() {
        if(list != null && list.size() > 0)
        {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(list != null && list.size() > 0)
        {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;

        if(view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.stock_detail_item, null);
            holder._key = (TextView)view.findViewById(R.id.i_stock_key);
            holder._value = (TextView)view.findViewById(R.id.i_stock_value);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        String option = list.get(position).option;
        String key = list.get(position).key;
        String value = list.get(position).value;

        if(TextUtils.isEmpty(option) ||  TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return null;
        }

        holder._key.setText(key);

        if(option.equals("dealNum")) {
            float count = Float.valueOf(value);
            if(count / 100000000 > 1) {
                holder._value.setText(df.format(count / 100000000) + "亿");
            }else if(count > 0){
                holder._value.setText(df.format(count / 10000) + "万");
            } else {
                holder._value.setText("——");
            }

        } else if (option.equals("dealPri")) {
            float price = Float.valueOf(value);
            if(price / 100000000 > 1) {
                holder._value.setText(df.format(price / 100000000) + "亿");
            } else if(price > 0){
                holder._value.setText(df.format(price / 10000) + "万");
            } else {
                holder._value.setText("——");
            }
        } else {
            holder._value.setText(df.format(Double.valueOf(value)));
        }

        if(status) {
            if(option.equals("openPri") || option.equals("highPri") || option.equals("lowPri"))
            {
                holder._value.setTextColor(context.getResources().getColor(R.color.text_stock_red));
            } else {
                holder._value.setTextColor(context.getResources().getColor(R.color.text_white));
            }
        } else {
            if(option.equals("openPri") || option.equals("highPri") || option.equals("lowPri"))
            {
                holder._value.setTextColor(context.getResources().getColor(R.color.text_stock_green));
            } else {
                holder._value.setTextColor(context.getResources().getColor(R.color.text_white));
            }
        }

        return view;
    }

    public static class StockItem {
        public String option;
        public String value;
        public String key;
        public StockItem(String option,String key, String value) {
            this.option = option;
            this.key = key;
            this.value = value;
        }
    }

    public static class ViewHolder {
        TextView _key;
        TextView _value;
    }
}
