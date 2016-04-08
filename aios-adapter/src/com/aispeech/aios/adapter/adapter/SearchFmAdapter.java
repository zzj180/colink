package com.aispeech.aios.adapter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.bean.FmItem;
import com.aispeech.aios.adapter.bean.FmSearchParam;
import com.aispeech.aios.adapter.util.StringUtil;

import java.util.List;


public class SearchFmAdapter extends BaseAdapter {
    private Context mContext;
    private List<FmItem> list;
    private FmSearchParam mParam;

    public SearchFmAdapter(Context context, List<FmItem> list, FmSearchParam param) {
        this.mContext = context;
        this.list = list;
        mParam = param;
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
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.fm_result_list_item, null);
            holder.mIndexTv = (TextView) convertView.findViewById(R.id.index_text_view);
            holder.mTitle = (TextView) convertView.findViewById(R.id.title_text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FmItem fmItem = list.get(position);
        holder.mIndexTv.setText((position + 1) + ".");

        StringBuilder sb = new StringBuilder(fmItem.title);
        if (!artistIsEmpty()) {
            sb.append(" - ").append(mParam.getArtist());
        }

        holder.mTitle.setText(sb.toString());

        if (artistIsEmpty()) {
            StringUtil.highNightWords(mContext,holder.mTitle,sb.toString(),mParam.getKeyWord());
        }else {
            StringUtil.highNightWords(mContext, holder.mTitle, sb.toString(), mParam.getArtist(), mParam.getAlbum());
        }
        return convertView;
    }

    static class ViewHolder {
        TextView mIndexTv;
        TextView mTitle;
    }

    private boolean artistIsEmpty(){
        return TextUtils.isEmpty(mParam.getArtist());
    }
}
