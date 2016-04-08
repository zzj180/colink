package com.aispeech.aios.adapter.node;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.text.TextUtils;
import android.util.Log;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.util.LocationDBHelper;
import com.aispeech.aios.adapter.util.SendBroadCastUtil;

/**
 * @desc 电话领域对应节点
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class PhoneNode extends BaseNode {
    private static final String TAG = "AIOS-Adapter-PhoneNode";

    /** 查询蓝牙状态广播 **/
    public static final String AIOS_BT_STATUS_REQ = "action.adapter.status.request";
    /** 来电响铃广播 **/
    public static final String AIOS_INCOMING_RINGING = "action.bt.AIOS_INCOMING_RINGING";
    /** 来电已接通 **/
    public static final String AIOS_INCOMING_OFFHOOK = "action.bt.AIOS_INCOMING_OFFHOOK";
    /** 来电已挂断/拒接 **/
    public static final String AIOS_INCOMING_IDLE = "action.bt.AIOS_INCOMING_IDLE";
    /** 接听来电 **/
    public static final String AIOS_INCOMING_ACCEPT = "action.intent.AIOS_ACCEPT";
    /** 拒绝来电 **/
    public static final String AIOS_INCOMING_REJECT = "action.intent.AIOS_REJECT";
    /** 确认拨打电话 **/
    public static final String AIOS_OUTGOING_DIAL = "action.intent.AIOS_DIAL";
    /** 去电等待中响铃 **/
    public static final String AIOS_OUTGOING_RINGING = "action.bt.AIOS_OUTGOING_RINGING";
    /** 去电已接通 **/
    public static final String AIOS_OUTGOING_OFFHOOK = "action.bt.AIOS_OUTGOING_OFFHOOK";
    /** 去电已挂断 **/
    public static final String AIOS_OUTGOING_IDLE = "action.bt.AIOS_OUTGOING_IDLE";
    /** 蓝牙连接 **/
    public static final String AIOS_BTSTATE_CONNECTED = "com.android.bt.connected";
    /** 蓝牙断开 **/
    public static final String AIOS_BTSTATE_DISCONNECTED = "com.android.bt.disconnected";
    /** 联系人同步完成 **/
    public static final String AIOS_BT_CONTACTS_SYNC_SUCCESS = "com.aispeech.action.CONTACTS_SYNC_SUCCESS";
    /** 联系人同步失败 **/
    public static final String AIOS_BT_CONTACTS_SYNC_FAILED = "com.aispeech.action.CONTACTS_SYNC_FAILED";
    public static final String BLUETOOTH_PHONE = "android.intent.action.BLUETOOTH_PHONE_STATE";
    
    private static final String ACTION_HANGUP = "com.colink.service.TelphoneService.TelephoneHandupReceive";
	private static final String ACTION_ANSWER = "com.colink.service.TelphoneService.TelephoneAnswerReceive";
    private static final String ACTION_DIAL = "com.colink.service.TelphoneService.TelephoneReceive"; //add by zzj
    private static final String ACTION_PHONE_STATE = "android.intent.action.BLUETOOTH_PHONE_STATE";
    
    private Context mContext;
    private static PhoneNode mInstance;
    private String phoneListTitle;
    private String phoneNum;
    private String phoneName;
    private boolean isConnect = false;
    //    private ResultInfo result;//识别信息
    private BtPhoneReceiver btReceiver;
    private boolean DOMAIN_FALG = false;//标示是否是附近领域过来的,false为附近领域

    private PhoneNode() {
        this.mContext = AdapterApplication.getContext();
    }

    public static synchronized PhoneNode getInstance() {
        if (mInstance == null) {
            mInstance = new PhoneNode();
        }
        return mInstance;
    }

    /**
     * 获取蓝牙和手机的连接状态
     * @return true：已经连接；false：没有连接
     */
    public boolean getPhoneState() {
        return isConnect;
    }


    public void registerBtReceiver() {
        if (null == btReceiver) btReceiver = new BtPhoneReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AIOS_INCOMING_RINGING);/*来电响铃等待对方接听*/
        intentFilter.addAction(ACTION_PHONE_STATE);/*来电响铃等待对方接听*/
        intentFilter.addAction(AIOS_INCOMING_OFFHOOK);/*来电通话中*/
        intentFilter.addAction(AIOS_INCOMING_IDLE);/*来电空闲状态*/
        intentFilter.addAction(AIOS_OUTGOING_RINGING);/*打电话出去等待对方接听*/
        intentFilter.addAction(AIOS_OUTGOING_OFFHOOK);/*打电话出去通话中*/
        intentFilter.addAction(AIOS_OUTGOING_IDLE);/*打电话空闲状态*/
        intentFilter.addAction(AIOS_BTSTATE_CONNECTED);/*蓝牙已连接状态广播*/
        intentFilter.addAction(AIOS_BTSTATE_DISCONNECTED);/*蓝牙断开连接状态广播*/
        mContext.registerReceiver(btReceiver, intentFilter);
        
        //pin wang
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
						isConnect = false;
		                //收到蓝牙已断开消息，通知aios
						//changed by ydj on 2016.3.24
//		                mHandler.sendEmptyMessage(PHONE_BLUETOOTH_DISCONNECTED);
						publishSticky("phone.bluetooth.state", "disconnected");
						//changed by ydj on 2016.3.24
						break;
					case 1:
						 isConnect = true;
			                //收到蓝牙已连接消息，通知aios
						 //changed by ydj on 2016.3.24
//			            mHandler.sendEmptyMessage(PHONE_BLUETOOTH_CONNECTED);
			            publishSticky("phone.bluetooth.state", "connected");
			          //changed by ydj on 2016.3.24
						break;
					default:
						break;
					}
        		}
        	}
		} );
      //pin wang
    }

    public class BtPhoneReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AILog.i(TAG, "action" + intent.getAction());
            if (intent.getAction().equals(AIOS_BTSTATE_CONNECTED)) {
                isConnect = true;
                //收到蓝牙已连接消息，通知aios
                publishSticky("phone.bluetooth.state", "connected");
            } else if (intent.getAction().equals(AIOS_BTSTATE_DISCONNECTED)) {
                isConnect = false;
                //收到蓝牙已断开消息，通知aios
                publishSticky("phone.bluetooth.state", "disconnected");
            } else if (intent.getAction().equals(ACTION_PHONE_STATE)) {//total else if
                //收到蓝牙来电铃声响起消息，通知aios
                
            	int state = intent.getIntExtra("state", TelephonyManager.CALL_STATE_RINGING);
            	String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            	
            	//added by ydj on 2016.4.23
            	String name = intent.getExtras().getString("name");
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                //added by ydj on 2016.4.23
                
            	switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                	//deleted by ydj on 2016.4.23
                	/*Message msg=new Message();
                    msg.getData().putString("number",phoneNumber);
                    msg.what=PHONE_INCOMING_CALL;
                    mHandler.sendMessage(msg);*/
                	//deleted by ydj on 2016.4.23
                	
                	//added by ydj on 2016.4.23
                    if (TextUtils.isEmpty(name)) {
                        if (TextUtils.isEmpty(number)) {
                            name = "未知号码";
                        } else {
                            name = number;
                        }
                    }

                    UiEventDispatcher.notifyUpdateUI(UIType.Phone);
                    String addressIn = LocationDBHelper.getInstance().findPhoneAreaByNumber(number);

                    List<Object> list = new ArrayList<Object>();
                    list.add(name);
                    list.add(number);
                    list.add(addressIn);

                    UiEventDispatcher.notifyUpdateUI(UIType.PhoneAnswerStatus, list, "");
                    if (null != bc) {
                        bc.publish("phone.incomingcall.state", "ringing", number, name);
                    }
                    AILog.i(TAG, "NAME：" + name + "-----" + "NUMBER:" + number);
                  //added by ydj on 2016.4.23
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                	 if (bc != null) {
                         bc.publish("phone.incomingcall.state", "offhook");
                       //changed by ydj on 2016.3.24
//                         mHandler.sendEmptyMessage(PHONE_OFFHOOK_REMOVEWINDOW);
                         UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
                       //changed by ydj on 2016.3.24
                     }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (null != bc) {
                    	bc.publish("phone.incomingcall.state", "idle");
                        UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
                    }
                    break;
                default:
                    break;
        	}//total else if
            } else if (intent.getAction().equals(AIOS_INCOMING_OFFHOOK)) {
                //收到蓝牙来电通话中消息，通知aios
                if (bc != null) {
                    bc.publish("phone.incomingcall.state", "offhook");
                    UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
                }
            } else if (intent.getAction().equals(AIOS_INCOMING_IDLE)) {
                //收到蓝牙来电空闲状态消息，通知aios
                if (bc != null) {
                    bc.publish("phone.incomingcall.state", "idle");

                    UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
                }
            } else if (intent.getAction().equals(AIOS_OUTGOING_RINGING)) {
                //收到蓝牙打电话出去铃声状态消息，通知aios
                if (bc != null) {
                    bc.publish("phone.outgoingcall.state", "ringing");
                }
            } else if (intent.getAction().equals(AIOS_OUTGOING_OFFHOOK)) {
                //收到蓝牙打电话出去正在通话中状态消息，通知aios
                if (bc != null) {
                    bc.publish("phone.outgoingcall.state", "offhook");
                    UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
                }
            } else if (intent.getAction().equals(AIOS_OUTGOING_IDLE)) {
                //收到蓝牙去电空闲状态消息，通知aios
                if (bc != null) {
                    bc.publish("phone.outgoingcall.state", "idle");
                }
            }
        }
    }

    /**
     * 取消注册BtPhoneReceiver广播
     */
    public void unRegister() {
        if (btReceiver != null) {
            mContext.unregisterReceiver(btReceiver);
            btReceiver = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public String getName() {
        return "phone";
    }

    @Override
    public void onJoin() {
        super.onJoin();
        bc.subscribe("data.sync.state");
    }

    @Override
    public void onMessage(String topic, byte[]... parts) throws Exception {
        super.onMessage(topic, parts);
        AILog.i(TAG, topic, parts);
        Log.d(TAG, topic+"  :parts="+ new String(parts[0]));
        if (topic.equals("data.sync.state") && "contacts".equals(new String(parts[0]))) {
            if ("true".equals(new String(parts[1]))) {
                TTSNode.getInstance().play("联系人同步完成");
                mContext.sendBroadcast(new Intent(AIOS_BT_CONTACTS_SYNC_SUCCESS));
            } else {
                TTSNode.getInstance().play("联系人同步失败");
                mContext.sendBroadcast(new Intent(AIOS_BT_CONTACTS_SYNC_FAILED));
            }
        }
    }

    @Override
    public BusClient.RPCResult onCall(final String url, final byte[]... args) throws Exception {
        AILog.i(TAG, url, args);

        if (url.equals("/phone/outgoingcall")) {
            DOMAIN_FALG = true;
            UiEventDispatcher.notifyUpdateUI(UIType.Awake);
        } else if (url.equals(AiosApi.Phone.CONTACTS_LIST)) {
            //列出联系人供选择
            showList(args);
        } else if (url.equals(AiosApi.Phone.CONTACTS_SELECT_PREV_PAGE)) {
            if(UiEventDispatcher.isListViewFirstPage()) {
                return new BusClient.RPCResult(null, "current is the first page");
            } else {
                UiEventDispatcher.notifyUpdateUI(UIType.ListViewPrePage);
            }
        } else if (url.equals(AiosApi.Phone.CONTACTS_SELECT_NEXT_PAGE)) {
            if(UiEventDispatcher.isListViewLastPage()) {
                return new BusClient.RPCResult(null, "current is the last page");
            } else {
                UiEventDispatcher.notifyUpdateUI(UIType.ListViewNextPage);
            }
        } else if (url.equals(AiosApi.Phone.OUT_GOING_CALL_DIAL)) {
            callPhone(args[0]);
        } else if (url.equals(AiosApi.Phone.OUT_GOING_CALL_DIAL_WAIT)) {
            //打断倒计时
            UiEventDispatcher.notifyUpdateUI(UIType.PhoneStopWait, 0);

        } else if (url.equals(AiosApi.Phone.OUT_GOING_CALL_DIAL_OK)) {
            //确定拨打
            AILog.i(TAG, "confirm to call phone(num.) : " + phoneNum);

       //modify by zzj     SendBroadCastUtil.getInstance().sendBroadCast(AIOS_OUTGOING_DIAL, "number", phoneNum);
            SendBroadCastUtil.getInstance().sendBroadCast(ACTION_DIAL, "number", phoneNum);
            //end by zzj
            if(bc != null) {
                bc.publish(AiosApi.Other.UI_CLICK);
            }

            UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);

        } else if (url.equals(AiosApi.Phone.OUT_GOING_CALL_DIAL_CANCLE)) {
            //取消拨打
            UiEventDispatcher.notifyUpdateUI(UIType.PhoneStopWait, 1);

        } else if (url.equals(AiosApi.Phone.IN_COMING_ACCEPT)) {
            //接听当前来电
            Intent acceptIntent = new Intent(ACTION_ANSWER);
            mContext.sendBroadcast(acceptIntent);
            UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);

        } else if (url.equals(AiosApi.Phone.IN_COMING_REJECT)) {
            //拒绝当前来电
            if(bc != null) {
                bc.publish("phone.incomingcall.state", "idle");
            }

            Intent rejectIntent = new Intent(ACTION_HANGUP);
            mContext.sendBroadcast(rejectIntent);
            UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow);
        }
        return null;
    }

    /**
     * 悬浮窗显示搜索到的联系人列表
     * @param args 联系人列表信息
     * @throws JSONException
     * @throws UnsupportedEncodingException
     */
    private void showList(byte[]... args) throws JSONException, UnsupportedEncodingException {

        List<Object> list = new ArrayList<Object>();
        if (null != args && null != args[0]) {
            JSONArray listJson = new JSONArray(new String(args[0], "utf-8"));
            AILog.i(TAG, "items' count ：" + listJson.length());
            for (int j = 0; j < listJson.length(); j++) {
                JSONObject itemJson = listJson.getJSONObject(j);

                AILog.json(itemJson.toString());
                list.add(new PhoneItem(itemJson.getString("name"), itemJson.getString("number")));
            }
        }

        phoneListTitle = "联系人";
        UiEventDispatcher.notifyUpdateUI(UIType.PhoneList, list, phoneListTitle);
    }

    /**
     * 给某个人打电话
     * @param arg 联系人信息
     * @throws JSONException
     * @throws UnsupportedEncodingException
     */
    private void callPhone(byte[] arg) throws JSONException, UnsupportedEncodingException {
        JSONObject dataJson = new JSONObject(new String(arg, "utf-8"));
        JSONArray arrayJson = dataJson.getJSONArray("phone_info");

        try {
            JSONObject tempJson = arrayJson.optJSONObject(0);
            phoneName = dataJson.getString("name");
            phoneNum = tempJson.getString("number");
        } catch (JSONException exception) {//附近领域跳过来
            JSONObject temp1Json = arrayJson.optJSONObject(0);
            phoneName = dataJson.getString("name");
            phoneNum = temp1Json.getString("number");
        }

        String address = LocationDBHelper.getInstance().findPhoneAreaByNumber(phoneNum);
        if (DOMAIN_FALG) {
            List<Object> list = new ArrayList<Object>();
            list.add(phoneName);
            list.add(phoneNum);
            list.add(address);
            UiEventDispatcher.notifyUpdateUI(UIType.PhoneUpdateWait, list, "");
        }

        AILog.i(TAG, "phoneNum===" + phoneNum + ",,,phoneName===" + phoneName);
    }


    public static class PhoneItem{
        public String pName;
        public String pNumber;

        public PhoneItem(String name, String number) {
            this.pName = name;
            this.pNumber = number;
        }
    }
}
