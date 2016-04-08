package com.aispeech.aios.adapter.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aispeech.aios.adapter.R;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-02-15
 * @copyright aispeech.com
 */
public class HelpInfoAdapter extends BaseAdapter {
    public final static int HELP_TYPE_HOME = 0;
    public final static int HELP_TYPE_MUSIC = 1;
    public final static int HELP_TYPE_PHONE = 2;
    public final static int HELP_TYPE_NAVIGATION = 3;
    public final static int HELP_TYPE_FM = 4;
    public final static int HELP_TYPE_WEATHER = 5;
    public final static int HELP_TYPE_VEH = 6;
    public final static int HELP_TYPE_STOCK = 7;

    private Context mContext;
    private int mTipsType = HELP_TYPE_HOME;
    private int[] mHomeTipsImages = {
            R.drawable.icon_tips_phone,
            R.drawable.icon_tips_music,
            R.drawable.icon_tips_nav_arr,
            R.drawable.icon_tips_near};
    private String[] mTipsTypeString;
    private String[] mTipsHomeString;
    private String[] mTipsMusicString;
    private String[] mTipsPhoneString;
    private String[] mTipsNavigationString;
    private String[] mTipsWeatherString;
    private String[] mTipsStockString;
    private String[] mTipsVehString;

    public HelpInfoAdapter(Context context) {
        this.mContext = context;
        mTipsTypeString = mContext.getResources().getStringArray(R.array.help_tips_type);
        mTipsHomeString = mContext.getResources().getStringArray(R.array.help_tips_home);
        mTipsMusicString = mContext.getResources().getStringArray(R.array.help_tips_music);
        mTipsPhoneString = mContext.getResources().getStringArray(R.array.help_tips_phone);
        mTipsNavigationString = mContext.getResources().getStringArray(R.array.help_tips_navigation);
        mTipsWeatherString = mContext.getResources().getStringArray(R.array.help_tips_weather);
        mTipsStockString = mContext.getResources().getStringArray(R.array.help_tips_stock);
        mTipsVehString = mContext.getResources().getStringArray(R.array.help_tips_veh);
    }

    public void setTipsType(int tipsType) {
        this.mTipsType = tipsType;
    }

    @Override
    public int getCount() {
        int iCount = 0;
        switch (mTipsType) {
            case HelpInfoAdapter.HELP_TYPE_PHONE:
                iCount = mTipsPhoneString.length;
                break;
            case HelpInfoAdapter.HELP_TYPE_MUSIC:
                iCount = mTipsMusicString.length;
                break;
            case HelpInfoAdapter.HELP_TYPE_NAVIGATION:
                iCount = mTipsNavigationString.length;
                break;
            case HelpInfoAdapter.HELP_TYPE_WEATHER:
                iCount = mTipsWeatherString.length;
                break;
            case HelpInfoAdapter.HELP_TYPE_VEH:
                iCount = mTipsVehString.length;
                break;
            case HelpInfoAdapter.HELP_TYPE_STOCK:
                iCount = mTipsStockString.length;
                break;
            case HelpInfoAdapter.HELP_TYPE_HOME:
            case HelpInfoAdapter.HELP_TYPE_FM:
            default:
                iCount = mTipsHomeString.length;
                break;
        }
        return iCount;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler viewHodler;
        if (null == convertView) {
            viewHodler = new ViewHodler();
            convertView = View.inflate(mContext, R.layout.help_item_layout, null);
            viewHodler.typeImage = (ImageView) convertView.findViewById(R.id.typeImage);
            viewHodler.typeText = (TextView) convertView.findViewById(R.id.typeText);
            viewHodler.tipsText = (TextView) convertView.findViewById(R.id.tipsText);
            convertView.setTag(viewHodler);
        } else {
            viewHodler = (ViewHodler) convertView.getTag();
        }

        viewHodler.typeImage.setVisibility(View.GONE);
        viewHodler.typeText.setVisibility(View.GONE);

        switch (mTipsType) {
            case HelpInfoAdapter.HELP_TYPE_PHONE:
                viewHodler.tipsText.setText(mTipsPhoneString[position]);
                break;
            case HelpInfoAdapter.HELP_TYPE_MUSIC:
                viewHodler.tipsText.setText(mTipsMusicString[position]);
                break;
            case HelpInfoAdapter.HELP_TYPE_NAVIGATION:
                viewHodler.tipsText.setText(mTipsNavigationString[position]);
                break;
            case HelpInfoAdapter.HELP_TYPE_WEATHER:
                viewHodler.tipsText.setText(mTipsWeatherString[position]);
                break;
            case HelpInfoAdapter.HELP_TYPE_VEH:
                viewHodler.tipsText.setText(mTipsVehString[position]);
                break;
            case HelpInfoAdapter.HELP_TYPE_STOCK:
                viewHodler.tipsText.setText(mTipsStockString[position]);
                break;
            case HelpInfoAdapter.HELP_TYPE_HOME:
            case HelpInfoAdapter.HELP_TYPE_FM:
            default:
                viewHodler.typeImage.setImageResource(mHomeTipsImages[position]);
                viewHodler.typeText.setText(mTipsTypeString[position]);
                viewHodler.tipsText.setText(mTipsHomeString[position]);

                viewHodler.typeImage.setVisibility(View.VISIBLE);
                viewHodler.typeText.setVisibility(View.VISIBLE);
                break;
        }

        return convertView;
    }

    class ViewHodler {
        ImageView typeImage;
        TextView typeText;
        TextView tipsText;
    }
}
