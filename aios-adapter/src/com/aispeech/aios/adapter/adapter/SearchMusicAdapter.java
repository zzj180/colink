package com.aispeech.aios.adapter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aispeech.aimusic.config.MusicConfig;
import com.aispeech.aimusic.model.MusicBean;
import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.ui.view.MarqueeTextView;
import com.aispeech.aios.adapter.util.StringUtil;

import java.util.List;

/**
 * @desc 音乐搜索列表适配器
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class SearchMusicAdapter extends BaseAdapter {
    private Context mContext;
    private List<Object> list;
    private String mHightWords;

    public SearchMusicAdapter(Context context, List<Object> list, String title) {
        this.mContext = context;
        this.list = list;
        mHightWords = title;
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.music_result_list_item, null);
            holder.mIndexTv = (TextView) convertView.findViewById(R.id.index_text_view);
            holder.mTitle = (MarqueeTextView) convertView.findViewById(R.id.title_text_view);
            holder.mImage = (ImageView) convertView.findViewById(R.id.image);
            holder.mSrcTxt = (TextView) convertView.findViewById(R.id.music_source);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MusicBean musicBean = (MusicBean) list.get(position);
        holder.mIndexTv.setText((position + 1) + ".");

        if (MusicConfig.MUSIC_TYPE_CONFIG == MusicConfig.AIOS_MUSIC) {//如果是AIOS音乐，就显示本地和云端提示，如果是酷我，就不显示
            holder.mImage.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(musicBean.getUrl())) {//云端
                holder.mImage.setImageResource(R.drawable.icon_music_cloud);
                holder.mSrcTxt.setText("在线");
            } else {
                holder.mImage.setImageResource(R.drawable.icon_music_local);
                holder.mSrcTxt.setText("本地");
            }
        } else {
            holder.mImage.setVisibility(View.GONE);
        }

        String str = musicBean.getTitle() + " - " + musicBean.getArtist();
        holder.mTitle.setText(str);

        StringUtil.highNightWords(mContext, holder.mTitle, str, mHightWords.split(" - "));
        return convertView;
    }

    static class ViewHolder {
        TextView mIndexTv;
        ImageView mImage;
        MarqueeTextView mTitle;
        TextView mSrcTxt;
    }

}
