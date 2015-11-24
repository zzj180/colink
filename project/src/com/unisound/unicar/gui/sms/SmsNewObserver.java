/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : SMSObserver.java
 * @ProjectName : NativeSMS
 * @PakageName : com.yzs.sms.demo
 * @Author : CavanShi
 * @CreateDate : 2013-3-13
 */
package com.unisound.unicar.gui.sms;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : CavanShi
 * @CreateDate : 2013-3-13
 * @ModifiedBy : CavanShi
 * @ModifiedDate: 2013-3-13
 * @Modified: 2013-3-13: 实现基本功能
 */
@SuppressLint("HandlerLeak")
public class SmsNewObserver extends ContentObserver {

	public static final String TAG = "SmsNewObserver";

	protected static final int CLIUMN_INDEX_ID = 0;
	protected static final int CLIUMN_INDEX_TYPE = 1;
	protected static final int CLIUMN_INDEX_PHONE = 2;
	protected static final int CLIUMN_INDEX_BODY = 3;
	protected static final int CLIUMN_INDEX_TIME = 4;
	protected static final int CLIUMN_INDEX_THREAD_ID = 5;
	protected static final int CLIUMN_INDEX_PROTOCOL = 7;
	protected static final int CLIUMN_INDEX_PERSON = 8;

	protected static final String[] PORJECTION = new String[] { SMS._ID,
			SMS.TYPE, SMS.ADDRESS, SMS.BODY, SMS.DATE, SMS.THREAD_ID, SMS.READ,
			SMS.PROTOCOL, SMS.PERSON_ID };
	private static final String SELECTION = "read = ? and type = ?";
	private Context mContext;
	private ISMSReceiver mMessageReceiverListener;
	//add tyz begin 0817
	private static Queue<SmsItem> mSyncTaskMSMQueue = new LinkedList<SmsItem>(); //收到短信的队列
//	private boolean isSendtovui = true;
	private Thread mSystemOnChangeThread = null;
	private SmsItem handSmsBegin = new SmsItem();
	private SmsItem handSmsEnd = new SmsItem();
	private Handler hand = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1000:
//				isSendtovui = true;
				break;
			case 1001:
				handSmsBegin = (SmsItem)msg.obj;
				Log.d(TAG, "handSmsBegin.getMessage() = "+handSmsBegin.getMessage()+";==handSmsEnd.getMessage()=="+handSmsEnd.getMessage());
				
				if (handSmsBegin.getMessage() != null && handSmsBegin.getMessage().length() > 0) {
						mMessageReceiverListener.onMessageReveived(handSmsBegin);
				}
				break;
				
			default:
				break;
			}
		};
	};
	
	//add tyz end 0817
	/**2014-11-19 yujun*/
	private ContentResolver mResolver;
	/**-------------------*/

	public SmsNewObserver(Context context) {
		super(null);
		Logger.d(TAG, "--SmsNewObserver--");
		mContext = context;
		registReceiver();
		/**2014-11-19 yujun*/
		mResolver = mContext.getContentResolver();
		/**-------------------*/
		startSystemOnChangeThread();//启动线程
		
	}

	public static SmsItem popSyncTaskQueue() {//读取堆栈中的短信
		synchronized (mSyncTaskMSMQueue) {
			if (mSyncTaskMSMQueue != null) {
				Iterator<SmsItem> iterable = mSyncTaskMSMQueue.iterator();
				if (iterable.hasNext()) {
					SmsItem taskQueue= iterable.next();
					Logger.d(TAG, "taskQueue : " + taskQueue);
					mSyncTaskMSMQueue.clear();
					return taskQueue;
				}
			}
		}
		return null;
	}
	
	private class SyncSystemRunnable implements Runnable {
		@Override
		public void run() {
			Logger.d(TAG, "SyncSystemRunnable run");
			while (true) {
				synchronized (mSyncTaskMSMQueue) {
					if (mSyncTaskMSMQueue != null && mSyncTaskMSMQueue.isEmpty()) {
						Logger.d(TAG, "SyncSystemRunnable mSyncTaskQueue.isEmpty()");
						try {
							mSyncTaskMSMQueue.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				SmsItem onChangeQueue = popSyncTaskQueue();
				Logger.d(TAG, "SyncSystemRunnable onChangeQueue :　" + onChangeQueue);
				if (onChangeQueue != null) {
					Message message = new Message();
					message.what = 1001;
					message.obj = onChangeQueue;
					hand.sendMessageDelayed(message,500);
				}
				
				try {
					Thread.sleep(5000);
					synchronized (mSyncTaskMSMQueue) {
						if (mSyncTaskMSMQueue != null) {
								mSyncTaskMSMQueue.clear();
							}
						}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void startSystemOnChangeThread(){
		Logger.d(TAG, "startOnChangeThread");
		if (mSystemOnChangeThread == null || !mSystemOnChangeThread.isAlive()) {
			mSystemOnChangeThread = new Thread(new SyncSystemRunnable());
			mSystemOnChangeThread.setPriority(Thread.MIN_PRIORITY);
			mSystemOnChangeThread.setName(TAG);
			mSystemOnChangeThread.start();
		}
	}
	
	
	public void setMessageReveiverListener(ISMSReceiver l) {
		mMessageReceiverListener = l;
	}
	
	/**
	 * 2014-11-25
	 * @param context
	 * @return
	 */
	public Context getContext(){
		return mContext;
	}

	public void onChange(boolean change) {
		super.onChange(change);
		
		
		Logger.d(TAG, "--onChange--");
		Cursor c = null;
		SmsItem item = new SmsItem();
		int id;
		int protocol;
		String phone;
		String body;
		String person;
		try {
			// 读取收件箱中的短信
			/**2014-11-19 yujun*/
			c = mContext.getContentResolver().query(SMS.CONTENT_URI, PORJECTION, SELECTION, new String[] { "0", "1" },"date desc");//获取条件(read = 0,type =1)的短信指针
			/**-------------------*/
			
			
			
			/**2014-11-18 yujun*/
			int nameColumn = c.getColumnIndex("person");
	         int phoneColumn = c.getColumnIndex("address");
	         int smsColumn = c.getColumnIndex("body");
	         /**-----------------*/

			boolean hasDone = false;
			if (c != null) {
				if (c.moveToFirst()) {
					
					/**2014-11-18 yujun : modify*/
					id = c.getInt(CLIUMN_INDEX_ID);
					protocol = c.getInt(CLIUMN_INDEX_PROTOCOL);
					phone = c.getString(phoneColumn);
					body = c.getString(smsColumn);
					person = c.getString(nameColumn);//person是联系人列表里的序号,陌生人为null
					
					/**2014-12-1 yujun 正确获取短信联系人姓名*/
					Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phone));
					Cursor cur = mContext.getContentResolver().query(personUri, new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
					if (cur.moveToFirst()) {
						int nameIdx = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);
						String name = cur.getString(nameIdx);
						cur.close();
						person = name;
					}
					/**------------------*/
					Logger.d(TAG, "--phone--"+phone+"--body--"+body+"--person--"+person);

					if (protocol == SMS.PROTOCOL_SMS) {
//						item = new SmsItem();
						item.setId(id);
						String uri = "content://sms/" + id;
						item.setSmsUri(uri);
						item.setMessage(body);// 内容
						item.setNumber(phone);
						item.setTime(System.currentTimeMillis());
						item.setName(person);
					}
					if (body != null) {
						hasDone = true;
						if (mMessageReceiverListener != null) {
							/**2014-11-19 yujun*/
							ContentValues value = new ContentValues();
							 value.put("read", 1);
							 mResolver.update(SMS.CONTENT_URI, value, " _id = ?",new String[] { id + "" });
							 
							 /**------------------*/
							 
							 synchronized (mSyncTaskMSMQueue) {
								 mSyncTaskMSMQueue.offer(item);
									Logger.d(TAG, "onChange mSyncTaskQueue size : "
											+ mSyncTaskMSMQueue.size());
									if (mSyncTaskMSMQueue != null) {
										mSyncTaskMSMQueue.notifyAll();
									}
								}
							 
//							 if (isSendtovui) {
//								 mMessageReceiverListener.onMessageReveived(item);
//								 isSendtovui = false;
//								 hand.sendEmptyMessageDelayed(1000, 10000);
//								 
//							}
							
						}
					}
					if (!hasDone) {
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null)
				c.close();
		}
	}

	public void registReceiver() {
		Logger.d(TAG, "registReceiver");
		mContext.getContentResolver().registerContentObserver(SMS.CONTENT_URI,
				true, this);
	}

	public void unregistReceiver() {
		Logger.d(TAG, "unregistReceiver");
		mContext.getContentResolver().unregisterContentObserver(this);
	}

	public void release() {
		Logger.d(TAG, "release");
		mContext = null;
		mMessageReceiverListener = null;
	}

	public static class SMS implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://sms");

		public static final String _ID = "_id";
		public static final String FILTER = "!imichat";
		public static final String TYPE = "type";
		public static final String THREAD_ID = "thread_id";
		public static final String ADDRESS = "address";
		public static final String PERSON_ID = "person";
		public static final String DATE = "date";
		public static final String READ = "read";
		public static final String BODY = "body";
		public static final String PROTOCOL = "protocol";

		public static final int MESSAGE_TYPE_ALL = 0;
		public static final int MESSAGE_TYPE_INBOX = 1;
		public static final int MESSAGE_TYPE_SEND = 2;
		public static final int MESSAGE_TYPE_DRAFT = 3;
		public static final int MESSAGE_TYPE_OUTBOX = 4;
		public static final int MESSAGE_TYPE_FAILED = 5;
		public static final int MESSAGE_TYPE_QUEUED = 6;

		public static final int PROTOCOL_SMS = 0;
		public static final int PROTOCOL_MMS = 1;

	}

	public interface ISMSReceiver {
		void onMessageReveived(SmsItem msg);
	}
}
