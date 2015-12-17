package com.unisound.unicar.gui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.chat.ChatListAdapter;
import com.unisound.unicar.gui.chat.ChatObject;
import com.unisound.unicar.gui.utils.ListViewUtil;
import com.unisound.unicar.gui.utils.Logger;

/**
 * Chat View
 * 
 * @author xiaodong
 * @date 20150820
 */
public class ChatView extends FrameLayout implements ISessionView {

    private static final String TAG = ChatView.class.getSimpleName();
    private Context mContext;

    private List<ChatObject> mAllChatObjList = new ArrayList<ChatObject>();// all

    private ListView mListView;
    private ChatListAdapter mMessageListAdapter;

    public ChatView(Context context) {
        super(context);
    }

    public ChatView(Context context, List<ChatObject> allChatObjList) {
        super(context);
        mContext = context;
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_chat, this, true);
        mAllChatObjList = allChatObjList;
        findViews();
    }

    private void findViews() {
        Logger.d(TAG, "!--->findViews---");
        mListView = (ListView) findViewById(R.id.chat_list);
        mMessageListAdapter = new ChatListAdapter(mContext);
        mMessageListAdapter.setList(mAllChatObjList);
        mListView.setAdapter(mMessageListAdapter);
        ListViewUtil.setListViewHeightBasedOnScreen(mContext, mListView, 0);
        mListView.setSelection(mListView.getAdapter().getCount() - 1);
    }

    /**
	 * 
	 */
    public void notifyDataChanged() {
        Logger.d(TAG, "!--->notifyDataChanged---");
        if (null != mMessageListAdapter) {
            mMessageListAdapter.notifyDataSetChanged();
        }
        if (null != mListView) {
            mListView.setSelection(mListView.getAdapter().getCount() - 1);
        }
    }

    @Override
    public boolean isTemporary() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void release() {
        // TODO Auto-generated method stub
        Logger.d(TAG, "!--->release---");
        mAllChatObjList.clear();
    }

}
