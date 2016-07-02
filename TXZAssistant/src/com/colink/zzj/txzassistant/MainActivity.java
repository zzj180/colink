package com.colink.zzj.txzassistant;


import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.colink.zzj.txzassistant.setting.SettingsViewPagerActivity;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZPowerManager;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
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
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button1:
			Settings.System.putInt(getContentResolver(), "asr", 0);
			initParams();
			
			break;
		case R.id.button2:
			Settings.System.putInt(getContentResolver(), "asr", 1);
			initParams();
			break;

		case R.id.button3:
			startActivity(new Intent(this, SettingsViewPagerActivity.class));
			break;
		default:
			break;
		}
		
	}

	private void initParams() {
		TXZPowerManager.getInstance().notifyPowerAction(TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
		TXZPowerManager.getInstance().releaseTXZ();
		
		Process.killProcess(Process.myPid());
		System.exit(0);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
			}
		}).start();
	}
}
