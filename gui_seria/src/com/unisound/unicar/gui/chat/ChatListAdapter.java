package com.unisound.unicar.gui.chat;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;

public class ChatListAdapter extends BaseAdapter {

    private static final String TAG = ChatListAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    private Context mContext;
    private List<ChatObject> mChatObjectList = new ArrayList<ChatObject>();

    private class ViewHolder {
        private TextView tv_ask;
        private TextView tv_answer;
    }

    public ChatListAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) LayoutInflater.from(mContext);
    }

    public void setList(List<ChatObject> list) {
        mChatObjectList = list;
    }

    @Override
    public int getCount() {
        return mChatObjectList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChatObjectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.list_item_chat, null);
            holder.tv_ask = (TextView) view.findViewById(R.id.text_ask);
            holder.tv_answer = (TextView) view.findViewById(R.id.text_answer);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ChatObject chatObj = mChatObjectList.get(position);
        holder.tv_ask.setText(chatObj.getAskText());
        holder.tv_answer.setText(chatObj.getAnswerText());

        return view;
    }

}
