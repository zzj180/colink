package com.colink.bluetoothe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.colink.bluetoolthe.R;
import com.colink.database.CallLogDatabase;
import com.colink.database.CallLogProvider;
import com.colink.service.TelphoneService;
import com.colink.service.TelphoneService.localBinder;

public class CallLogActivity extends Activity implements OnClickListener {
	
	ListView listView;

	private TelphoneService mService;
	public static final String ACTION_INSERT_LOG="com.colink.zzj.calllog.insertlog";

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((localBinder) service).getService();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// No Titlebar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.calllog);
		
		init();
		
		Intent service = new Intent(this, TelphoneService.class);
		bindService(service, conn, BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		
		unbindService(conn);
	}

	public ArrayList<CallLogBean> list;

	private void init() {
		
		listView = (ListView) findViewById(R.id.listView);
		findViewById(R.id.contact_dial).setOnClickListener(this);
		findViewById(R.id.contact).setOnClickListener(this);
		findViewById(R.id.contact_del).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		
		list = new ArrayList<CallLogBean>();
		
		adapter=new CallLogAdapter();
		
		listView.setAdapter(adapter);
		
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		String[] projection = { CallLogDatabase.CALLLOG_DATE, CallLogDatabase.CALLLOG_NUMBER,
				CallLogDatabase.CALLLOG_TYPE, CallLogDatabase.CALLLOG_NAME,CallLogDatabase.CALLLOG_DURATION,
				CallLogDatabase.CALLLOG_ROWID }; // 查询的列
		// 所有来电记录的查询

		asyncQuery = new MyAsyncQueryHandler(
				getContentResolver());
		
		asyncQuery.startQuery(0, null, CallLogProvider.CONTENT_URI, projection, null, null,
				"date desc");
	}
	public MyAsyncQueryHandler asyncQuery;
	CallLogAdapter adapter;
	public class MyAsyncQueryHandler extends AsyncQueryHandler {

		@SuppressLint("HandlerLeak")
		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}
		
		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
			// TODO Auto-generated method stub
			super.onDeleteComplete(token, cookie, result);
		}
		
		@Override
		protected void onInsertComplete(int token, Object cookie, Uri uri) {
			
			int id=(int) ContentUris.parseId(uri);
			
			CallLogBean callLogBean=(CallLogBean) cookie;
			
			callLogBean.setId(id);
			
			list.add(0, callLogBean);
			
			adapter.notifyDataSetChanged();
			
			listView.requestFocusFromTouch();
			
			listView.setSelection(0);
			
		}

		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			
			if (cursor != null && cursor.getCount() > 0) {
				
				// HH以24小时制，hh以12小时制
				cursor.moveToFirst();
				
				for (int i = 0, size = cursor.getCount();i<size; i++) {
					
					cursor.moveToPosition(i);
					
					 long date = cursor.getLong(cursor
							.getColumnIndex(CallLog.Calls.DATE));
					 
					 long duration=cursor.getLong(cursor
								.getColumnIndex(CallLog.Calls.DURATION));
					
					String number = cursor.getString(cursor
							.getColumnIndex(CallLog.Calls.NUMBER));
					
					int type = cursor.getInt(cursor
							.getColumnIndex(CallLog.Calls.TYPE));
					
					String cachedName = cursor.getString(cursor
							.getColumnIndex(CallLog.Calls.CACHED_NAME));// 缓存的名称与电话号码，如果它的存在
					
					int id = cursor.getInt(cursor
							.getColumnIndex(CallLog.Calls._ID));
					
					CallLogBean clb = new CallLogBean();
					
					clb.setId(id);
					
					clb.setNumber(number);
					
					clb.setName(cachedName);
					clb.setDate(date);
					clb.setDuration(duration);
					
					if (null == cachedName || "".equals(cachedName)) {
						
						clb.setName(number);
						
					}
					
					clb.setType(type);
					
					list.add(clb);
				}
				cursor.close();
				
				adapter.notifyDataSetChanged();
			}
		}
	}
	public class CallLogAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public CallLogBean getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return list.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(CallLogActivity.this).inflate(R.layout.calllog_item, parent, false);
				holder=new ViewHolder();
				holder.callName_textView = (TextView) convertView.findViewById(R.id.textView1);
				holder.callNumber_textView = (TextView) convertView.findViewById(R.id.textView2);
				holder.callDate_textView = (TextView) convertView.findViewById(R.id.textView3);
				holder.callDuration_textView = (TextView) convertView.findViewById(R.id.textView4);
				holder.callType_imageView=(ImageView) convertView.findViewById(R.id.imageView1);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			holder.callName_textView.setText(getItem(position).getName());
			holder.callNumber_textView.setText(getItem(position).getNumber());
			holder.callDate_textView.setText(getDateStr(getItem(position).getDate()));
			
			switch (getItem(position).getType()) {
			case 1:
				holder.callType_imageView.setImageResource(R.drawable.yijie);
				holder.callDuration_textView.setText(getTimeStr(getItem(position).getDuration()));
				break;
			case 2:
				holder.callType_imageView.setImageResource(R.drawable.yibo);
				holder.callDuration_textView.setText(getTimeStr(getItem(position).getDuration()));
				break;
			case 3:
				holder.callType_imageView.setImageResource(R.drawable.weijie);
				holder.callDuration_textView.setText(null);
				break;
			default:
				break;
			}
			return convertView;
		}
		
	}
	private class ViewHolder {
		ImageView callType_imageView;
		TextView callName_textView;
		TextView callNumber_textView;
		TextView callDate_textView;
		TextView callDuration_textView;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.contact_dial:
			int position = listView.getCheckedItemPosition();
			if(position!=-1){
				listView.setSelection(position);
				if(mService!=null){
					mService.sendCommand("AT#CW" + ((TextView)listView.getAdapter().getView(position, null, null).findViewById(R.id.textView2)).getText() + "\r\n");
		//			Application.blueMic=true;
				}
			}
			break;
		case R.id.contact:
			startActivity(new Intent(this, ContactActivity.class));
			overridePendingTransition(R.anim.fade, R.anim.hold);
			break;
		case R.id.contact_del:
			int index = listView.getCheckedItemPosition();
			if(index!=-1){
				asyncQuery.startDelete(0, null,CallLogProvider.CONTENT_URI, CallLog.Calls._ID+"=?", new String[] {list.get(index).getId()+""});
				list.remove(index);
			}else{
				asyncQuery.startDelete(0, null, CallLogProvider.CONTENT_URI, null, null);
				list.clear();
			}
			adapter.notifyDataSetChanged();
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
		
	}
	private String getDateStr(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA);
        return formatter.format(new Date(millis));
	}
	
	private String getTimeStr(long time) {
		time = time / 1000;
		int s = (int) (time % 60);
		int m = (int) (time / 60 % 60);
		int h = (int) (time / 3600);
		if(h>0){
			return String.format("%d:%02d:%02d", h,m,s);
		}else{
			return String.format("%02d:%02d", m,s);
		}
	}
		
}
