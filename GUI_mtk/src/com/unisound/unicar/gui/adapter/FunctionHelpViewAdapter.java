/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : FunctionHelpViewAdapter.java
 * @ProjectName : uniCarGUI
 * @PakageName : com.unisound.unicar.gui.adapter
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-07-16
 */
package com.unisound.unicar.gui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.FunctionHelpTextInfo;
import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.ContactsUtil;
import com.unisound.unicar.gui.utils.Logger;

/**
 * Function Help View Adapter
 * 
 * @author xiaodong
 * @date 20150716
 */
public class FunctionHelpViewAdapter extends BaseAdapter {

    private static final String TAG = FunctionHelpViewAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<FunctionHelpTextInfo> mInfoList;
    private LayoutInflater inflater;

    public FunctionHelpViewAdapter(Context context, ArrayList<FunctionHelpTextInfo> lists) {
        super();
        this.mContext = context;
        this.mInfoList = lists;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mInfoList.get(position);
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
            view = inflater.inflate(R.layout.function_help_view_item, null);
            holder.ivHelpIcon = (ImageView) view.findViewById(R.id.iv_function_item_icon);
            holder.tvHelpText = (TextView) view.findViewById(R.id.tv_function_item_text);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final FunctionHelpTextInfo info = mInfoList.get(position);
        String domainType = info.getType();
        String domainHelpText = info.getHelpText();

        Logger.d(TAG, "!--->getView:-Before Check--domainType = " + domainType
                + "; domainHelpText = " + domainHelpText);
        domainHelpText = checkHelpTextByType(domainType, domainHelpText);
        Logger.d(TAG, "!--->getView:-After Check--domainType = " + domainType
                + "; domainHelpText = " + domainHelpText);

        holder.tvHelpText.setText(domainHelpText);
        setHelpItemIcomByType(holder.ivHelpIcon, domainType);

        return view;
    }


    /**
     * 
     * @param domainType
     * @param domainHelpText
     * @return
     */
    private String checkHelpTextByType(String domainType, String domainHelpText) {
        if (SessionPreference.DOMAIN_CALL.equals(domainType)
                || SessionPreference.DOMAIN_SMS.equals(domainType)) {
            String contactName = ContactsUtil.getRandomContactName(mContext);
            if ("".equals(contactName) || null == contactName) {
                Logger.d(TAG, "no contact find, use default domain Help Text for call & sms");
                // if no contact find, use default domain Help Text for call & sms
                if (SessionPreference.DOMAIN_CALL.equals(domainType)) {
                    domainHelpText =
                            mContext.getResources().getStringArray(
                                    R.array.function_example_call_no_contact)[0];
                } else {
                    domainHelpText =
                            mContext.getResources().getStringArray(
                                    R.array.function_example_sms_no_contact)[0];
                }
            } else {
                Object[] nameFormatParam = new Object[1];
                nameFormatParam[0] = contactName;
                domainHelpText = String.format(domainHelpText, nameFormatParam);
            }
        }
        return domainHelpText;
    }


    /**
     * set Help Item Icom by domainType
     * 
     * @param ivFunctionIcon
     * @param domainType
     */
    private void setHelpItemIcomByType(ImageView ivFunctionIcon, String domainType) {
        Logger.d(TAG, "!--->setHelpItemIcom()-----domainType = " + domainType);
        if (SessionPreference.DOMAIN_CALL.equals(domainType)) {
            ivFunctionIcon.setImageResource(R.drawable.icon_help_call);
        } else if (SessionPreference.DOMAIN_SMS.equals(domainType)) {
            ivFunctionIcon.setImageResource(R.drawable.icon_help_message);
        } else if (SessionPreference.DOMAIN_MUSIC.equals(domainType)) {
            ivFunctionIcon.setImageResource(R.drawable.icon_help_music);
        } else if (SessionPreference.DOMAIN_AUDIO.equals(domainType)) {
            ivFunctionIcon.setImageResource(R.drawable.icon_help_fm);
        } else if (SessionPreference.DOMAIN_ROUTE.equals(domainType)) {
            ivFunctionIcon.setImageResource(R.drawable.icon_help_navigation);
        } else if (SessionPreference.DOMAIN_WEATHER.equals(domainType)) {
            ivFunctionIcon.setImageResource(R.drawable.icon_help_weather);
        } else if (SessionPreference.DOMAIN_SETTING.equals(domainType)) {
            ivFunctionIcon.setImageResource(R.drawable.icon_help_setting);
        } else if (SessionPreference.DOMAIN_STOCK.equals(domainType)) {
            ivFunctionIcon.setImageResource(R.drawable.icon_help_stock);
        } else if (SessionPreference.DOMAIN_LOCAL.equals(domainType)) {
            ivFunctionIcon.setImageResource(R.drawable.icon_help_local_search);
        } else if (SessionPreference.DOMAIN_TRAFFIC.equals(domainType)) {
            ivFunctionIcon.setImageResource(R.drawable.icon_help_traffic);
        } else if (SessionPreference.DOMAIN_LIMIT.equals(domainType)) {
            ivFunctionIcon.setImageResource(R.drawable.icon_help_limit);
        } else if (SessionPreference.DOMAIN_BROADCAST.equals(domainType)) {
            ivFunctionIcon.setImageResource(R.drawable.icon_help_radio);
        }
    }


    private final static class ViewHolder {
        ImageView ivHelpIcon;
        TextView tvHelpText;
        // TextView tvIsOffline;
    }

}
