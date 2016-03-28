package com.colink.zzj.txzassistant.node;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.colink.zzj.txzassistant.AdapterApplication;
import com.colink.zzj.txzassistant.oem.RomSystemSetting;
import com.colink.zzj.txzassistant.util.Logger;
import com.txznet.sdk.TXZCallManager;
import com.txznet.sdk.TXZCallManager.CallTool;
import com.txznet.sdk.TXZCallManager.CallToolStatusListener;
import com.txznet.sdk.TXZCallManager.Contact;

/**
 * @desc 电话相关节点
 * @auth zzj
 * @date 2016-03-16
 */
public class PhoneNode  {

    public static final String ADAPTER_BT_STATUS_REQ = "action.adapter.status.request";
    public static final String AIOS_INCOMING_RINGING = "action.bt.AIOS_INCOMING_RINGING";
    public static final String AIOS_INCOMING_OFFHOOK = "action.bt.AIOS_INCOMING_OFFHOOK";
    public static final String AIOS_INCOMING_IDLE = "action.bt.AIOS_INCOMING_IDLE";
    public static final String AIOS_OUTGOING_RINGING = "action.bt.AIOS_OUTGOING_RINGING";
    public static final String AIOS_OUTGOING_OFFHOOK = "action.bt.AIOS_OUTGOING_OFFHOOK";
    public static final String AIOS_OUTGOING_IDLE = "action.bt.AIOS_OUTGOING_IDLE";
    public static final String AIOS_BTSTATE_CONNECTED = "com.android.bt.connected";
    public static final String AIOS_BTSTATE_DISCONNECTED = "com.android.bt.disconnected";
    public static final String AIOS_BT_CONTACTS_SYNC = "com.android.bt.contacts_sync";
    public static final String AIOS_BT_CONTACTS_SYNC_SUCCESS = "com.aispeech.action.CONTACTS_SYNC_SUCCESS";
    public static final String AIOS_BT_CONTACTS_SYNC_FAILED = "com.aispeech.action.CONTACTS_SYNC_FAILED";
    public static final String BLUETOOTH_PHONE = "android.intent.action.BLUETOOTH_PHONE_STATE";
    private Context mContext;
    private static PhoneNode mInstance;
    private PhoneNode() {
       this.mContext = AdapterApplication.getContext();
    }

	public void init() {
		TXZCallManager.getInstance().setCallTool(mCallTool);
		   registerBtReceiver();
	}

    public static synchronized PhoneNode getInstance() {
        if (mInstance == null) {
            mInstance = new PhoneNode();
        }
        return mInstance;
    }

    
    private CallToolStatusListener mCallToolStatusListener;
	private CallTool mCallTool = new CallTool() {
		@Override
		public void setStatusListener(CallToolStatusListener listener) {
			// 记录下listener，适当的时机通知sdk状态变
			mCallToolStatusListener = listener;
			if (listener != null) {
				// TODO 通知最后的电话状态
				listener.onEnabled();
				listener.onIdle();
			}
		}

		@Override
		public boolean rejectIncoming() {
			Logger.d("rejectIncoming");
			RomSystemSetting.RomCustomHANGUP(mContext);
			return true;
		}

		@Override
		public boolean makeCall(Contact contact) {
			Logger.d("makeCall");
			RomSystemSetting.RomCustomDialNumber(mContext, contact.getNumber());
			return true;
		}

		@Override
		public boolean hangupCall() {
			Logger.d("makeCall");
			RomSystemSetting.RomCustomHANGUP(mContext);
			return true;
		}

		@Override
		public CallStatus getStatus() {
			Logger.d("getStatus");
			return CallStatus.CALL_STATUS_IDLE;
		}

		@Override
		public boolean acceptIncoming() {
			Logger.d("acceptIncoming");
			RomSystemSetting.RomCustomAnswerCall(mContext);
			return true;
		}
	};

	private BtPhoneReceiver btReceiver;
	
	 public void registerBtReceiver() {
	        if (null == btReceiver) btReceiver = new BtPhoneReceiver();
	        IntentFilter intentFilter = new IntentFilter();
	        intentFilter.addAction(AIOS_INCOMING_RINGING);/*来电响铃等待对方接听*/
	        intentFilter.addAction(AIOS_INCOMING_OFFHOOK);/*来电通话中*/
	        intentFilter.addAction(AIOS_INCOMING_IDLE);/*来电空闲状态*/
	        intentFilter.addAction(AIOS_OUTGOING_RINGING);/*打电话出去等待对方接听*/
	        intentFilter.addAction(AIOS_OUTGOING_OFFHOOK);/*打电话出去通话中*/
	        intentFilter.addAction(AIOS_OUTGOING_IDLE);/*打电话空闲状态*/
	        intentFilter.addAction(AIOS_BT_CONTACTS_SYNC);/*同步联系人*/
	        mContext.registerReceiver(btReceiver, intentFilter);
	        mContext.getContentResolver().registerContentObserver(Uri.parse("content://com.colink.bluetoothe/bluetootheonline"), false,new ContentObserver(new Handler()) {
	        	
	        	private Cursor query;
	        	String columnName = "online";
				@SuppressLint("NewApi")
				@Override
	        	public void onChange(boolean selfChange, Uri uri) {
	        		super.onChange(selfChange, uri);
	        		query = mContext.getContentResolver().query(uri, null, null, null, null);
	        		if (query.moveToNext()) {
						int state = query.getInt(query.getColumnIndex(columnName));
						switch (state) {
						case 0:
			                //收到蓝牙已断开消息，通知aios
							if (mCallToolStatusListener != null) {
								mCallToolStatusListener
										.onDisabled("很抱歉，蓝牙断开了，电话不可用了"/* 这里的提示会直接交互展示给用户 */);
							}
							break;
						case 1:
							if (mCallToolStatusListener != null) {
								mCallToolStatusListener.onEnabled();
							}
							break;
						default:
							break;
						}
	        		}
	        	}
			} );
	    }

	    public class BtPhoneReceiver extends BroadcastReceiver {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
				if (action.equals(BLUETOOTH_PHONE)) {
	            	int state = intent.getIntExtra("state", TelephonyManager.CALL_STATE_RINGING);
	            	String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
	            	switch (state) {
		                case TelephonyManager.CALL_STATE_RINGING:
		                    if (mCallToolStatusListener != null) {
		    					Contact con = new Contact();
		    					con.setNumber(phoneNumber);
		    					mCallToolStatusListener.onIncoming(con,
		    							true/* 是否tts播报来电信息 */, true/* 是否启动声控识别接听拒接 */);
		    				}
		                    break;
		                case TelephonyManager.CALL_STATE_OFFHOOK:
		                	if (mCallToolStatusListener != null) {
		    					mCallToolStatusListener.onOffhook();
		    				}
		                    break;
		                case TelephonyManager.CALL_STATE_IDLE:
		                	if (mCallToolStatusListener != null) {
		    					mCallToolStatusListener.onIdle();
		    				}
		                    break;
		                default:
		                    break;
	            	}
	            } else if (action.equals(AIOS_INCOMING_OFFHOOK)) {
	                //收到蓝牙来电通话中消息，通知aios
	            	if (mCallToolStatusListener != null) {
    					mCallToolStatusListener.onOffhook();
    				}
	            }
	        }
	    }

	    /**
	     * 解绑广播
	     */
	    public void unRegister() {
	        if (btReceiver != null) {
	            mContext.unregisterReceiver(btReceiver);
	            btReceiver = null;
	        }
	    }

}
