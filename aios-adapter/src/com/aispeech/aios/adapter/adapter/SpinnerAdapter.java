package com.aispeech.aios.adapter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aispeech.aios.adapter.R;

/**
 * @desc Spinner控件列表适配器
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private String[] mStringArray;

    public SpinnerAdapter(Context context, String[] array) {
        super(context, R.layout.spinner_item, array);
        mContext = context;
        mStringArray = array;
    }

    //展开列表的时候填充视图
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        //此处text1是Spinner默认的用来显示文字的TextView
        TextView tv = (TextView) convertView.findViewById(R.id.spinner_text);
        if(position < mStringArray.length)
        tv.setText(mStringArray[position]);
        return convertView;

    }

    //
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 修改Spinner选择后结果的字体颜色
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        //此处text1是Spinner默认的用来显示文字的TextView
        TextView tv = (TextView) convertView.findViewById(R.id.spinner_text);
        if(position < mStringArray.length)
        tv.setText(mStringArray[position]);
        return convertView;
    }
}
