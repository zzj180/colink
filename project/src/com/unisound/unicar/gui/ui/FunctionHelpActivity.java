package com.unisound.unicar.gui.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.adapter.FunctionHelpAdapter;
import com.unisound.unicar.gui.model.FunctionHelpTextInfo;
import com.unisound.unicar.gui.preference.PrivatePreference;
import com.unisound.unicar.gui.preference.UserPreference;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @author zzj
 * @date 20150914
 */
public class FunctionHelpActivity extends Activity{

	private static final String TAG = FunctionHelpActivity.class.getSimpleName();
	
	private Context mContext;
	
	private TextView tvTitle;
	private ListView mListView;
	private FunctionHelpAdapter mFunctionHelpAdapter;
	int mHelpType;
	
	 private OnClickListener mReturnListerner = new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            onBackPressed();
	        }
	 };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = getIntent();
		String action = intent.getAction();
		mHelpType = intent.getIntExtra(GUIConfig.KEY_FUNCTION_HELP_TYPE, 2);
		
		if (!GUIConfig.ACTION_FUNCTION_HELP.equals(action)) {
			Logger.w(TAG, "!--->wrong action, finish FunctionHelpActivity!");
			finish();
		}
		
		if (GUIConfig.VALUE_FUNCTION_HELP_TYPE_BLUETOOTH == mHelpType) {
			setContentView(R.layout.activity_function_help_bluetooth);
		} else {
			setContentView(R.layout.activity_function_help_list);
		}
		
		mContext = getApplicationContext();
		
		String titleText = intent.getStringExtra(GUIConfig.KEY_FUNCTION_HELP_TITLE);
		tvTitle = (TextView) findViewById(R.id.tv_title_function_name);
		tvTitle.setText(titleText);
		
		if (GUIConfig.VALUE_FUNCTION_HELP_TYPE_BLUETOOTH == mHelpType) {
			initBluetoothHelpView();
		} else {
			initListView();
		}
		
//		mLayoutInflater = getLayoutInflater();
        ImageButton returnBtn = (ImageButton) findViewById(R.id.backBtn);
        returnBtn.setOnClickListener(mReturnListerner);
		
	}

	/**
	 * init BluetoothHelp View
	 */
	private void initBluetoothHelpView(){
		TextView tvCallOffline1 = (TextView) findViewById(R.id.tv_help_call_offline_text_1);
		TextView tvCallOffline2 = (TextView) findViewById(R.id.tv_help_call_offline_text_2);
		TextView tvCallOnline1 = (TextView) findViewById(R.id.tv_help_call_online_text_1);
		TextView tvCallOnline2 = (TextView) findViewById(R.id.tv_help_call_online_text_2);
		
		TextView tvInCallOffline1 = (TextView) findViewById(R.id.tv_help_incall_offline_text_1);
		TextView tvInCallOffline2 = (TextView) findViewById(R.id.tv_help_incall_offline_text_2);
		
	//	TextView tvSendSMSOnline1 = (TextView) findViewById(R.id.tv_help_send_sms_online_text_1);
	//	TextView tvSendSMSOnline2 = (TextView) findViewById(R.id.tv_help_send_sms_online_text_2);
		
		
		String[] callOfflineArray =  mContext.getResources().getStringArray(R.array.help_child_content_outcall_offline);
		tvCallOffline1.setText(callOfflineArray[0]);
		tvCallOffline2.setText(callOfflineArray[1]);
		
		String[] callOnlineArray =  mContext.getResources().getStringArray(R.array.help_child_content_outcall_online);
		tvCallOnline1.setText(callOnlineArray[0]);
		tvCallOnline2.setText(callOnlineArray[1]);
		
		String[] inCallOfflineArray =  mContext.getResources().getStringArray(R.array.help_child_content_incall_offline);
		tvInCallOffline1.setText(inCallOfflineArray[0]);
		tvInCallOffline2.setText(inCallOfflineArray[1]);
		

/*		String[] smsOnlineArray =  mContext.getResources().getStringArray(R.array.help_child_content_sms_online);
		tvSendSMSOnline1.setText(smsOnlineArray[0]);
		tvSendSMSOnline2.setText(smsOnlineArray[1]);*/
	}
	
	
	private void initListView(){
		mListView = (ListView) findViewById(R.id.lv_help_text);
		mFunctionHelpAdapter = new FunctionHelpAdapter(this, getHelpTextInfo());
		mListView.setAdapter(mFunctionHelpAdapter);
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	private ArrayList<FunctionHelpTextInfo> getHelpTextInfo(){
		
		ArrayList<FunctionHelpTextInfo> mFunctionHelpList = new ArrayList<FunctionHelpTextInfo>();
		
		switch (mHelpType) {
		case GUIConfig.VALUE_FUNCTION_HELP_TYPE_BLUETOOTH:
			
			break;
		case GUIConfig.VALUE_FUNCTION_HELP_TYPE_NAVIGATION:
			mFunctionHelpList = getArrayListByArrayRes(
					R.array.help_child_content_navigation_offline,
					R.array.help_child_content_navigation_online);
			break;
		case GUIConfig.VALUE_FUNCTION_HELP_TYPE_MUSIC:
			mFunctionHelpList = getArrayListByArrayRes(
					R.array.help_child_content_music_offline,
					R.array.help_child_content_music_online);
			break;
		case GUIConfig.VALUE_FUNCTION_HELP_TYPE_SETTING:
			mFunctionHelpList = getArrayListByArrayRes(
					R.array.help_child_content_setting_offline,
					0);
			break;
		case GUIConfig.VALUE_FUNCTION_HELP_TYPE_RADIO:
			mFunctionHelpList = getArrayListByArrayRes(R.array.help_child_content_radio_offline,R.array.help_child_content_radio_online);
			break;
		case GUIConfig.VALUE_FUNCTION_HELP_TYPE_WEATHER:
			mFunctionHelpList = getArrayListByArrayRes(0,R.array.help_child_content_weather_online);
		    break;
		case GUIConfig.VALUE_FUNCTION_HELP_TYPE_STOCK:
			mFunctionHelpList = getArrayListByArrayRes(0,R.array.help_child_content_stock_online);
			break;
		case GUIConfig.VALUE_FUNCTION_HELP_TYPE_LOCAL_SEARCH:
			mFunctionHelpList = getArrayListByArrayRes(0,R.array.help_child_content_local_search_online);
			break;
		case GUIConfig.VALUE_FUNCTION_HELP_TYPE_TRAFFIC:
			mFunctionHelpList = getArrayListByArrayRes(0,R.array.help_child_content_traffic_online);
			break;
        case GUIConfig.VALUE_FUNCTION_HELP_TYPE_LIMIT:
        	mFunctionHelpList = getArrayListByArrayRes(0,R.array.help_child_content_limit_online);
			break;
        case GUIConfig.VALUE_FUNCTION_HELP_TYPE_WAKEUP:
        	String wakeup_words = PrivatePreference.getValue(UserPreference.Key_WAKEUP_WORD, PrivatePreference.mDeufaultLocation);
        	if (!TextUtils.isEmpty(wakeup_words)) {
				FunctionHelpTextInfo info = new FunctionHelpTextInfo();
				info.setHelpText(wakeup_words);
				info.setOfflineText(true);
				mFunctionHelpList.add(info);
			}
        	break;
		default:
			break;
			
		}
		return mFunctionHelpList;
	}
	
	/**
	 * 
	 * @param offlineArrayRes
	 * @param onlineArrayRes
	 * @return
	 */
	private ArrayList<FunctionHelpTextInfo> getArrayListByArrayRes(int offlineArrayRes, int onlineArrayRes){
		ArrayList<FunctionHelpTextInfo> mFunctionHelpList = new ArrayList<FunctionHelpTextInfo>();
		if (offlineArrayRes != 0) {
			String[] offlineArray =  mContext.getResources().getStringArray(offlineArrayRes);
			for (String offlineText : offlineArray) {
				if (!TextUtils.isEmpty(offlineText)) {
					FunctionHelpTextInfo info = new FunctionHelpTextInfo();
					info.setHelpText(offlineText);
					info.setOfflineText(true);
					mFunctionHelpList.add(info);
				}
			}
		}
		if (onlineArrayRes != 0) {
			String[] onlineArray =  mContext.getResources().getStringArray(onlineArrayRes);
			for (String onlineText : onlineArray) {
				if (!TextUtils.isEmpty(onlineText)) {
					FunctionHelpTextInfo info = new FunctionHelpTextInfo();
					info.setHelpText(onlineText);
					info.setOfflineText(false);
					mFunctionHelpList.add(info);
				}
			}
		}
		return mFunctionHelpList;
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
}
