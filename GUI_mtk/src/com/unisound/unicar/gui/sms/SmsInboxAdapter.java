/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : SmsInboxAdapter.java
 * @ProjectName : NativeSMS
 * @PakageName : aa.ayzs.sms.demo
 * @Author : CavanShi
 * @CreateDate : 2013-3-26
 */
package com.unisound.unicar.gui.sms;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : CavanShi
 * @CreateDate : 2013-3-26
 * @ModifiedBy : CavanShi
 * @ModifiedDate: 2013-3-26
 * @Modified: 2013-3-26: 实现基本功能
 */
public class SmsInboxAdapter extends BaseAdapter {
    public static final String TAG = "SmsInboxAdapter";

    private static final int mPageSize = 15;

    private ArrayList<SmsItem> mItems;
    private LayoutInflater mInflater;
    private Context mContext;

    private int mAllCountSize;
    private int mPages;
    private int mCurrentPage;

    private Cursor mCursor;

    public SmsInboxAdapter(Context context, ArrayList<SmsItem> items) {
        mItems = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }

    public SmsInboxAdapter(Context context) {
        mItems = new ArrayList<SmsItem>();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }

    public void setSmsCursor(Cursor c) {
        if (c == null) {
            return;
        }
        mCursor = c;
        mAllCountSize = c.getCount();
        mCurrentPage = 1;
        if (mAllCountSize % mPageSize != 0) {
            mPages = mAllCountSize / mPageSize + 1;
        } else {
            /* 2013.05.23 added by shichao for sms crash */
            if (mAllCountSize < mPageSize) {
                mPages = 1;
            } else {
                mPages = mAllCountSize / mPageSize;
            }
            /* end */
        }
        if (mCursor.moveToFirst()) {
            /* 2013.05.23 added by shichao for sms crash */
            int size = 0;
            if (mAllCountSize < mPageSize) {
                size = mAllCountSize;
            } else {
                size = mPageSize;
            }
            /* END */
            for (int i = 0; i < size; i++) {
                SmsItem item = new SmsItem();

                int id;
                String phone;
                String body;
                long time;
                String phoneSub = null;

                id = c.getInt(SmsNewObserver.CLIUMN_INDEX_ID);
                phone = c.getString(SmsNewObserver.CLIUMN_INDEX_PHONE);
                body = c.getString(SmsNewObserver.CLIUMN_INDEX_BODY);
                time = c.getLong(SmsNewObserver.CLIUMN_INDEX_TIME);

                item.setMessage(body);
                String uri = "content://sms/" + id;
                item.setSmsUri(uri);
                item.setNumber(phone);

                String name = queryContactName(mContext, phone);
                if (name != null) {
                    item.setName(name);
                } else {
                    // Log.i("Test" , "the name is :" + name.toString());
                    if (phone.startsWith("+86")) {
                        phoneSub = phone.substring(3, phone.length());
                        String nameSub = queryContactName(mContext, phoneSub);
                        if (nameSub != null) {
                            item.setName(nameSub);
                        } else {
                            item.setName(phone);
                        }
                    } else {
                        item.setName(phone);
                    }
                }
                item.setTime(time);
                mItems.add(item);
                mCursor.moveToNext();
            }
            this.notifyDataSetChanged();
        }
    }

    public int loadNextPage() {
        if (mCurrentPage < mPages) {

            int count = mCurrentPage * mPageSize;
            int end =
                    (count + mPageSize) < mCursor.getCount() ? (count + mPageSize) : mAllCountSize;

            Log.i("Test", "mCurrentPage :" + mCurrentPage);
            Log.i("Test", "CurrentCursor :" + count + " end: " + end);
            Log.i("Test", "nextCursorend:" + (count + mPageSize));
            Log.i("Test", "-------------------");

            for (int i = count; i < end; i++) {
                SmsItem item = new SmsItem();

                int id;
                String phone;
                String phoneSub = null;
                String body;
                long time;

                id = mCursor.getInt(SmsNewObserver.CLIUMN_INDEX_ID);
                phone = mCursor.getString(SmsNewObserver.CLIUMN_INDEX_PHONE);
                body = mCursor.getString(SmsNewObserver.CLIUMN_INDEX_BODY);
                time = mCursor.getLong(SmsNewObserver.CLIUMN_INDEX_TIME);

                item.setMessage(body);
                String uri = "content://sms/" + id;
                item.setSmsUri(uri);
                item.setNumber(phone);
                String name = queryContactName(mContext, phone);
                if (name != null) {
                    item.setName(name);
                } else {
                    // Log.i("Test" , "the name is :" + name.toString());
                    // if(phone.startsWith("+86") || phone.startsWith("12520")) {
                    if (phone.length() > 11) {
                        phoneSub = phone.substring(phone.length() - 11, phone.length());

                        String nameSub = queryContactName(mContext, phoneSub);
                        if (nameSub != null) {
                            item.setName(nameSub);
                        } else {
                            item.setName(phone);
                        }
                    } else {
                        item.setName(phone);
                    }
                }
                item.setTime(time);
                mItems.add(item);
                mCursor.moveToNext();
            }
            this.notifyDataSetChanged();
            mCurrentPage++;
        }
        if (mCurrentPage < mPages) {
            return 1;
        } else {
            return 0;
        }
    }

    public void setItems(ArrayList<SmsItem> items) {
        mItems = items;
    }

    @Override
    public int getCount() {
        if (mItems != null) {
            return mItems.size();
        }
        return 0;
    }

    @Override
    public SmsItem getItem(int position) {
        if (mItems != null && position < getCount()) {
            return mItems.get(position);
        }
        return null;
    }

    public void removeItem(int position) {
        if (mItems != null && mItems.size() > 0) {
            if (position >= mItems.size()) {
                mItems.remove(mItems.size() - 1);
            } else {
                mItems.remove(position);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.sms_inbox_item, null);
            holder = new ViewHolder();
            holder.body = (TextView) convertView.findViewById(R.id.sms_item_text);
            holder.time = (TextView) convertView.findViewById(R.id.sms_item_time);
            holder.name = (TextView) convertView.findViewById(R.id.sms_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.body.setText(getItem(position).getMessage());
        long time = getItem(position).getTime();
        String dateTime =
                DateFormat.format(mContext.getString(R.string.sms_date_format), time).toString();
        holder.time.setText(dateTime);
        holder.name.setText(getItem(position).getName());
        return convertView;
    }

    private String queryContactName(Context c, String number) {
        String disName = null;
        ContentResolver resolver = c.getContentResolver();

        Cursor c2 =
                resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME},
                        ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + number + "'",
                        null, // WHERE clause value substitution
                        null);

        if (c2 != null && c2.moveToFirst()) {
            disName = c2.getString(c2.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        // Log.i("Test" ," new :" + disName);
        return disName;
    }

    public static class ViewHolder {
        TextView name;
        TextView time;
        TextView body;
    }

}
