package com.zzj.softwareservice;

import com.android.fm.radio.FMRadioService;
import com.zzj.softwareservice.bd.BNRService;
import com.zzj.softwareservice.database.DBConstant;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private ContentObserver observer = new ContentObserver(new Handler()) {

		private Cursor query;
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.e("navi",selfChange+"");
			query = getContentResolver().query(
					DBConstant.NaviTable.CONTENT_URI, null, null, null, null);
			if (query.moveToNext()) {
				Log.e("navi",
						"CURRENT_ROADNAME = "
								+ query.getString(query.getColumnIndex(DBConstant.NaviTable.NEXT_ROAD)));
				Log.e("navi",
						"IMAGERES = "+ query.getString(query
										.getColumnIndex(DBConstant.NaviTable.MANEUVER_IMAGE)));
				Log.e("navi",
						"MANEUVER_IMAGE = "
								+ query.getString(query
										.getColumnIndex(DBConstant.NaviTable.NEXT_ROAD_DISTANCE)));
			}
			if(query!=null){
				query.close();
			}
			
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startService(new Intent(this, BNRService.class));
		try {
			startService(new Intent(this, FMRadioService.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	/*	getContentResolver().registerContentObserver(
				DBConstant.NaviTable.CONTENT_URI, false, observer);*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		getContentResolver().unregisterContentObserver(observer);
	}
}
