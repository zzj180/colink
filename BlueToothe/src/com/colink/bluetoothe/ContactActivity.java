package com.colink.bluetoothe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.colink.bluetoolthe.R;
import com.colink.service.TelphoneService;
import com.colink.service.TelphoneService.localBinder;
import com.colink.util.Constact;
import com.colink.util.CustomDialog;

public class ContactActivity extends Activity implements OnClickListener,
		Constact, LoaderCallbacks<Cursor> {

	private ListView listView;
	private TextView textView;
	Cursor cursor;
	private TelphoneService mService;
	private SimpleCursorAdapter mAdapter;
	private ProgressBar bar;
	CustomDialog dialog;
	private static final String dialover = "com.colink.zzj.contact.donedial";

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

	private static int LOADER_ID = 1;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (dialover.equals(action))
				bar.setVisibility(View.GONE);
			else if (ACTION_CONTACT_START.equals(action))
				bar.setVisibility(View.VISIBLE);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/*
		 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */
		// No Titlebar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact);

		listView = (ListView) findViewById(R.id.listView);
		textView = (TextView) findViewById(R.id.textView1);
		bar = (ProgressBar) findViewById(R.id.progressBar1);
		if (TelphoneService.isDownPhone) {
			bar.setVisibility(View.VISIBLE);
		} else {
			bar.setVisibility(View.GONE);
		}
		findViewById(R.id.download).setOnClickListener(this);
		findViewById(R.id.contact_del).setOnClickListener(this);
		findViewById(R.id.contact_dial).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		// 从数据库获取联系人姓名和电话号码
		// cursor = managedQuery(
		// ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
		// null,null,Phone.SORT_KEY_PRIMARY);
		mAdapter = new SimpleCursorAdapter(this, R.layout.contact_item, cursor,new String[] { Phone.DISPLAY_NAME, Phone.NUMBER }, new int[] {R.id.textView1, R.id.textView2 },CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		listView.setAdapter(mAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getLoaderManager().initLoader(LOADER_ID, null, this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(dialover);
		filter.addAction(ACTION_CONTACT_START);
		registerReceiver(receiver, filter);

		Intent service = new Intent(this, TelphoneService.class);
		bindService(service, conn, BIND_AUTO_CREATE);
	}

	// 弹窗
	private void dialog1(String text) {
		
		dialog = new CustomDialog(this);
		
		TextView textView = (TextView) dialog.getTextView();// 方法在CustomDialog中实现
		
		textView.setText(text);
		
		dialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (mService != null)
					
					mService.sendCommand(PHONEBOOK_SYN_COMMAND);
				
				dialog.dismiss();
			}
			
		});

		dialog.setOnNegativeListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				dialog.dismiss();
				
			}
		});

		dialog.show();

	}

	// 弹窗
	private void dialog2(String text) {
		dialog = new CustomDialog(this);
		TextView textView = (TextView) dialog.getTextView();// 方法在CustomDialog中实现
		textView.setText(text);
		dialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyAsyncQueryHandler asyncQueryHandler = new MyAsyncQueryHandler(getContentResolver());
				asyncQueryHandler.startDelete(LOADER_ID, null,TelphoneService.RAW_CONTENT_URI, null, null);
				dialog.dismiss();
			}
		});
		dialog.setOnNegativeListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.download:

			dialog1("重新下载电话会清空当前电话本");

			break;

		case R.id.contact_del:

			dialog2("是否清空当前电话本");

			// getContentResolver().delete(EcarConatactsProvider.CONTENT_URI, null, null);

			break;

		case R.id.contact_dial:

			int position = listView.getCheckedItemPosition();
			if (position != -1) {
				listView.setSelection(position);
				if (mService != null)
					mService.sendCommand("AT#CW"+ ((TextView) listView.getAdapter().getView(position, null, null).findViewById(R.id.textView2)).getText() + "\r\n");
				// Application.blueMic=true;
			}

			break;

		case R.id.back:

			finish();

			break;

		default:

			break;

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(conn);
		// overridePendingTransition(R.anim.fade, R.anim.hold);
		unregisterReceiver(receiver);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this,
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, Phone.SORT_KEY_PRIMARY);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		this.cursor = cursor;
		// The asynchronous load is complete and the data
		// is now available for use. Only now can we associate
		// the queried Cursor with the SimpleCursorAdapter.
		if (cursor != null && textView != null)
			textView.setText(Html.fromHtml(String.format(getResources().getString(R.string.count), cursor.getCount())));
		if (mAdapter != null)
			mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (mAdapter != null)
			mAdapter.swapCursor(null);
	}

	@SuppressLint("HandlerLeak")
	class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
			// TODO Auto-generated method stub
			if (mAdapter != null) {
				mAdapter.swapCursor(null);
				if (textView != null)
					textView.setText(Html.fromHtml(String.format(getResources().getString(R.string.count), 0)));
			}
			super.onDeleteComplete(token, cookie, result);
		}
	}
}
