/*
 * Copyright (C) 2007-2008 Esmertec AG. Copyright (C) 2007-2008 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.unisound.unicar.gui.sms;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.database.SqliteWrapper;
import com.unisound.unicar.gui.model.Telephony.Sms;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;

/**
 * This service essentially plays the role of a "worker thread", allowing us to store incoming
 * messages to the database, update notifications, etc. without blocking the main thread that
 * SmsReceiver runs on.
 */
public class SmsSenderService extends Service {
    public static final String TAG = "SmsSenderService";
    public static final String ACTION_SERVICE_STATE_CHANGED =
            "com.unisound.intent.action.SERVICE_STATE";
    public static final long NO_TOKEN = -1L;
    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;
    private boolean mSending;

    public static final String MESSAGE_SENT_ACTION = "com.unisound.intent.action.MESSAGE_SENT";

    // Indicates next message can be picked up and sent out.
    public static final String EXTRA_MESSAGE_SENT_SEND_NEXT = "SendNextMsg";

    public static final String ACTION_SEND_MESSAGE = "com.unisound.intent.action.SEND_MESSAGE";

    // This must match the column IDs below.
    private static final String[] SEND_PROJECTION = new String[] {Sms._ID, // 0
            Sms.THREAD_ID, // 1
            Sms.ADDRESS, // 2
            Sms.BODY, // 3
            Sms.STATUS, // 4

    };

    public Handler mToastHandler = new Handler();

    // This must match SEND_PROJECTION.
    private static final int SEND_COLUMN_ID = 0;
    private static final int SEND_COLUMN_THREAD_ID = 1;
    private static final int SEND_COLUMN_ADDRESS = 2;
    private static final int SEND_COLUMN_BODY = 3;
    private static final int SEND_COLUMN_STATUS = 4;

    private int mResultCode;

    @Override
    public void onCreate() {
        Logger.d(TAG, "!--->onCreate------");
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.
        HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "!--->onStartCommand------");
        // Temporarily removed for this duplicate message track down.

        mResultCode = intent != null ? intent.getIntExtra("result", 0) : 0;

        if (mResultCode != 0) {
            Logger.v(TAG, "onStart: #" + startId + " mResultCode: " + mResultCode + " = "
                    + translateResultCode(mResultCode));
        }

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        return Service.START_NOT_STICKY;
    }

    private static String translateResultCode(int resultCode) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                return "Activity.RESULT_OK";
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                return "SmsManager.RESULT_ERROR_GENERIC_FAILURE";
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                return "SmsManager.RESULT_ERROR_RADIO_OFF";
            case SmsManager.RESULT_ERROR_NULL_PDU:
                return "SmsManager.RESULT_ERROR_NULL_PDU";
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                return "SmsManager.RESULT_ERROR_NO_SERVICE";
            default:
                return "Unknown error code";
        }
    }

    @Override
    public void onDestroy() {
        // Temporarily removed for this duplicate message track down.
        // if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE) ||
        // LogTag.DEBUG_SEND) {
        // Log.v(TAG, "onDestroy");
        // }
        mServiceLooper.quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        /**
         * Handle incoming transaction requests. The incoming requests are initiated by the MMSC
         * Server or by the MMS Client itself.
         */
        @Override
        public void handleMessage(Message msg) {
            int serviceId = msg.arg1;
            Intent intent = (Intent) msg.obj;

            Logger.d(TAG, "!--->handleMessage serviceId: " + serviceId + " intent: " + intent);

            if (intent != null) {
                String action = intent.getAction();

                int error = intent.getIntExtra("errorCode", 0);

                Logger.d(TAG, "!--->handleMessage action: " + action + " error: " + error);

                if (MESSAGE_SENT_ACTION.equals(intent.getAction())) {
                    handleSmsSent(intent);
                } else if (ACTION_SERVICE_STATE_CHANGED.equals(action)) {
                    handleServiceStateChanged(intent);
                } else if (ACTION_SEND_MESSAGE.endsWith(action)) {
                    handleSendMessage();
                }
            }

            stopSelfResult(serviceId);
        }
    }

    private void handleServiceStateChanged(Intent intent) {
        // If service just returned, start sending out the queued messages
        ServiceState serviceState = ServiceState.newFromBundle(intent.getExtras());
        if (serviceState.getState() == ServiceState.STATE_IN_SERVICE) {
            sendFirstQueuedMessage();
        }
    }

    private void handleSendMessage() {
        Logger.d(TAG, "!--->handleSendMessage----mSending = " + mSending);
        if (!mSending) {
            boolean isSuccess = sendFirstQueuedMessage();
            Logger.d(TAG, "!--->handleSendMessage--isSuccess" + isSuccess);
        }
    }

    public synchronized boolean sendFirstQueuedMessage() {
        Logger.d(TAG, "!--->sendFirstQueuedMessage-------");
        boolean success = false; // XD 20150826 modify
        // get all the queued messages from the database
        final Uri uri = Uri.parse("content://sms/queued");
        ContentResolver resolver = getContentResolver();
        Cursor c =
                SqliteWrapper.query(this, resolver, uri, SEND_PROJECTION, null, null, "date ASC");
        // date ASC so we send out in same order the user tried to send messages.

        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    String msgText = c.getString(SEND_COLUMN_BODY);
                    String address = c.getString(SEND_COLUMN_ADDRESS);
                    int threadId = c.getInt(SEND_COLUMN_THREAD_ID);
                    int status = c.getInt(SEND_COLUMN_STATUS);

                    int msgId = c.getInt(SEND_COLUMN_ID);
                    Uri msgUri = ContentUris.withAppendedId(Sms.CONTENT_URI, msgId);

                    SmsMessageSender sender =
                            new SmsSingleRecipientSender(this, address, msgText, threadId,
                                    status == Sms.STATUS_PENDING, msgUri);

                    Logger.v(TAG, "sendFirstQueuedMessage " + msgUri + ", address: " + address
                            + ", threadId: " + threadId);

                    try {
                        sender.sendMessage(NO_TOKEN);
                        mSending = true;
                        success = true; // XD 20150826 added
                    } catch (SmsException e) {
                        Logger.e(TAG, "!--->sendFirstQueuedMessage: failed to send message "
                                + msgUri + ", catch :" + e.getMessage());
                        mSending = false;
                        messageFailedToSend(msgUri);
                        success = false;
                    }
                }
            } catch (Exception e) {
                Logger.d(TAG, "!--->sendFirstQueuedMessage--catch :" + e.getMessage());
            } finally {
                c.close();
            }
        }
        if (success) {
            // We successfully sent all the messages in the queue. We don't need
            // to be notified of any service changes any longer.
            unRegisterForServiceStateChanges();

            /* < XD 20150826 delete Begin */
            /*
             * mToastHandler.post(new Runnable() { public void run() { Logger.d(TAG,
             * "!--->sms_sent_success"); sendBroadcast(new
             * Intent(GUIConfig.ACTION_SMS_SEND_SUCCESS)); Toast.makeText(SmsSenderService.this,
             * getString(R.string.sms_sent_success), Toast.LENGTH_SHORT).show(); } });
             */
            /* XD 20150826 delete End > */
        }
        Logger.d(TAG, "!--->sendFirstQueuedMessage----success = " + success);
        return success;
    }

    private void handleSmsSent(Intent intent) {
        Uri uri = intent.getData();
        mSending = false;
        boolean sendNextMsg = intent.getBooleanExtra(EXTRA_MESSAGE_SENT_SEND_NEXT, false);

        Logger.d(TAG, "handleSmsSent uri: " + uri + " sendNextMsg: " + sendNextMsg
                + " mResultCode: " + mResultCode + " = " + translateResultCode(mResultCode));

        if (mResultCode == Activity.RESULT_OK) {

            Logger.v(TAG, "handleSmsSent move message to sent folder uri: " + uri);

            if (!Sms.moveMessageToFolder(this, uri, Sms.MESSAGE_TYPE_SENT)) {
                Logger.e(TAG, "handleSmsSent: failed to move message " + uri + " to sent folder");
            }

            if (sendNextMsg) {
                sendFirstQueuedMessage();
            }

            showSmsSendStatus(true); // XD 20150826 added
            // Update the notification for failed messages since they may be
            // deleted.
            // MessagingNotification.updateSendFailedNotification(this);
        } else if ((mResultCode == SmsManager.RESULT_ERROR_RADIO_OFF)
                || (mResultCode == SmsManager.RESULT_ERROR_NO_SERVICE)) {

            Logger.v(TAG, "handleSmsSent: no service, queuing message w/ uri: " + uri);

            // We got an error with no service or no radio. Register for state
            // changes so
            // when the status of the connection/radio changes, we can try to
            // send the
            // queued up messages.
            registerForServiceStateChanges();
            // We couldn't send the message, put in the queue to retry later.
            Sms.moveMessageToFolder(this, uri, Sms.MESSAGE_TYPE_QUEUED);

            showSmsSendStatus(false); // XD 20150826 modify
        } else {
            messageFailedToSend(uri);
            if (sendNextMsg) {
                sendFirstQueuedMessage();
            }
        }
    }

    /* < XD 20150826 added Begin */
    private void showSmsSendStatus(final boolean isSuccess) {
        Logger.d(TAG, "!--->showSmsSendStatus--isSuccess = " + isSuccess);
        mToastHandler.post(new Runnable() {
            public void run() {
                if (isSuccess) {
                    sendBroadcast(new Intent(GUIConfig.ACTION_SMS_SEND_SUCCESS));
                    Toast.makeText(SmsSenderService.this, getString(R.string.sms_sent_success),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SmsSenderService.this, getString(R.string.smsMessageQueued),
                            Toast.LENGTH_SHORT).show();
                    sendBroadcast(new Intent(GUIConfig.ACTION_SMS_SEND_FAIL));
                }
            }
        });
    }

    /* XD 20150826 added End > */

    private void messageFailedToSend(Uri uri) {

        Logger.v(TAG, "!--->messageFailedToSend msg failed uri: " + uri);
        Sms.moveMessageToFolder(this, uri, Sms.MESSAGE_TYPE_FAILED);
        // MessagingNotification.notifySendFailed(getApplicationContext(),
        // true);
    }

    /**
     * Move all messages that are in the outbox to the failed state and set them to unread.
     * 
     * @return The number of messages that were actually moved
     */
    // private int moveOutboxMessagesToFailedBox() {
    // ContentValues values = new ContentValues(3);
    //
    // values.put(Sms.TYPE, Sms.MESSAGE_TYPE_FAILED);
    // values.put(Sms.ERROR_CODE, SmsManager.RESULT_ERROR_GENERIC_FAILURE);
    // values.put(Sms.READ, Integer.valueOf(0));
    //
    // int messageCount = SqliteWrapper.update(
    // getApplicationContext(),
    // getContentResolver(),
    // Outbox.CONTENT_URI,
    // values,
    // "type = " + Sms.MESSAGE_TYPE_OUTBOX,
    // null);
    // if (Log.isLoggable(MmsLogTag.TRANSACTION, Log.VERBOSE)
    // || MmsLogTag.DEBUG_SEND) {
    // Log.v(TAG, "moveOutboxMessagesToFailedBox messageCount: "
    // + messageCount);
    // }
    // return messageCount;
    // }

    public static final String CLASS_ZERO_BODY_KEY = "CLASS_ZERO_BODY";

    /**
     * If the message is a class-zero message, display it immediately and return null. Otherwise,
     * store it using the <code>ContentResolver</code> and return the <code>Uri</code> of the thread
     * containing this message so that we can use it for notification.
     */
    // private Uri insertMessage(Context context, SmsMessage[] msgs, int error,
    // String format) {
    // // Build the helper classes to parse the messages.
    // SmsMessage sms = msgs[0];
    //
    // if (sms.getMessageClass() == SmsMessage.MessageClass.CLASS_0) {
    // displayClassZeroMessage(context, sms, format);
    // return null;
    // } else if (sms.isReplace()) {
    // return replaceMessage(context, msgs, error);
    // } else {
    // return storeMessage(context, msgs, error);
    // }
    // }

    /**
     * This method is used if this is a "replace short message" SMS. We find any existing message
     * that matches the incoming message's originating address and protocol identifier. If there is
     * one, we replace its fields with those of the new message. Otherwise, we store the new message
     * as usual.
     * 
     * See TS 23.040 9.2.3.9.
     */
    // private Uri replaceMessage(Context context, SmsMessage[] msgs, int error)
    // {
    // SmsMessage sms = msgs[0];
    // ContentValues values = extractContentValues(sms);
    // values.put(Sms.ERROR_CODE, error);
    // int pduCount = msgs.length;
    //
    // if (pduCount == 1) {
    // // There is only one part, so grab the body directly.
    // values.put(
    // Inbox.BODY,
    // replaceFormFeeds(sms.getDisplayMessageBody()));
    // } else {
    // // Build up the body from the parts.
    // StringBuilder body = new StringBuilder();
    // for (int i = 0; i < pduCount; i++) {
    // sms = msgs[i];
    // if (sms.mWrappedSmsMessage != null) {
    // body.append(sms.getDisplayMessageBody());
    // }
    // }
    // values.put(Inbox.BODY, replaceFormFeeds(body.toString()));
    // }
    //
    // ContentResolver resolver = context.getContentResolver();
    // String originatingAddress = sms.getOriginatingAddress();
    // int protocolIdentifier = sms.getProtocolIdentifier();
    // String selection = Sms.ADDRESS + " = ? AND " + Sms.PROTOCOL + " = ?";
    // String[] selectionArgs = new String[] { originatingAddress,
    // Integer.toString(protocolIdentifier) };
    //
    // Cursor cursor = SqliteWrapper.query(
    // context,
    // resolver,
    // Inbox.CONTENT_URI,
    // REPLACE_PROJECTION,
    // selection,
    // selectionArgs,
    // null);
    //
    // if (cursor != null) {
    // try {
    // if (cursor.moveToFirst()) {
    // long messageId = cursor.getLong(REPLACE_COLUMN_ID);
    // Uri messageUri = ContentUris.withAppendedId(
    // Sms.CONTENT_URI,
    // messageId);
    //
    // SqliteWrapper.update(
    // context,
    // resolver,
    // messageUri,
    // values,
    // null,
    // null);
    // return messageUri;
    // }
    // } finally {
    // cursor.close();
    // }
    // }
    // return storeMessage(context, msgs, error);
    // }

    // public static String replaceFormFeeds(String s) {
    // // Some providers send formfeeds in their messages. Convert those
    // // formfeeds to newlines.
    // return s.replace('\f', '\n');
    // }

    // private static int count = 0;

    // private Uri storeMessage(Context context, SmsMessage[] msgs, int error) {
    // SmsMessage sms = msgs[0];
    //
    // // Store the message in the content provider.
    // ContentValues values = extractContentValues(sms);
    // values.put(Sms.ERROR_CODE, error);
    // int pduCount = msgs.length;
    //
    // if (pduCount == 1) {
    // // There is only one part, so grab the body directly.
    // values.put(
    // Inbox.BODY,
    // replaceFormFeeds(sms.getDisplayMessageBody()));
    // } else {
    // // Build up the body from the parts.
    // StringBuilder body = new StringBuilder();
    // for (int i = 0; i < pduCount; i++) {
    // sms = msgs[i];
    // if (sms.mWrappedSmsMessage != null) {
    // body.append(sms.getDisplayMessageBody());
    // }
    // }
    // values.put(Inbox.BODY, replaceFormFeeds(body.toString()));
    // }
    //
    // // Make sure we've got a thread id so after the insert we'll be able to
    // // delete
    // // excess messages.
    // Long threadId = values.getAsLong(Sms.THREAD_ID);
    // String address = values.getAsString(Sms.ADDRESS);
    //
    // // Code for debugging and easy injection of short codes, non email
    // // addresses, etc.
    // // See Contact.isAlphaNumber() for further comments and results.
    // // switch (count++ % 8) {
    // // case 0: address = "AB12"; break;
    // // case 1: address = "12"; break;
    // // case 2: address = "Jello123"; break;
    // // case 3: address = "T-Mobile"; break;
    // // case 4: address = "Mobile1"; break;
    // // case 5: address = "Dogs77"; break;
    // // case 6: address = "****1"; break;
    // // case 7: address = "#4#5#6#"; break;
    // // }
    //
    // if (!TextUtils.isEmpty(address)) {
    // Contact cacheContact = Contact.get(address, true);
    // if (cacheContact != null) {
    // address = cacheContact.getNumber();
    // }
    // } else {
    // address = getString(Res.string.unknown_sender);
    // values.put(Sms.ADDRESS, address);
    // }
    //
    // if (((threadId == null) || (threadId == 0)) && (address != null)) {
    // threadId = Threads.getOrCreateThreadId(context, address);
    // values.put(Sms.THREAD_ID, threadId);
    // }
    //
    // ContentResolver resolver = context.getContentResolver();
    //
    // Uri insertedUri = SqliteWrapper.insert(
    // context,
    // resolver,
    // Inbox.CONTENT_URI,
    // values);
    //
    // // Now make sure we're not over the limit in stored messages
    // Recycler.getSmsRecycler().deleteOldMessagesByThreadId(
    // getApplicationContext(),
    // threadId);
    //
    // return insertedUri;
    // }

    /**
     * Extract all the content values except the body from an SMS message.
     */
    // private ContentValues extractContentValues(SmsMessage sms) {
    // // Store the message in the content provider.
    // ContentValues values = new ContentValues();
    //
    // values.put(Inbox.ADDRESS, sms.getDisplayOriginatingAddress());
    //
    // // Use now for the timestamp to avoid confusion with clock
    // // drift between the handset and the SMSC.
    // // Check to make sure the system is giving us a non-bogus time.
    // Calendar buildDate = new GregorianCalendar(2011, 8, 18); // 18 Sep 2011
    // Calendar nowDate = new GregorianCalendar();
    // long now = System.currentTimeMillis();
    // nowDate.setTimeInMillis(now);
    //
    // if (nowDate.before(buildDate)) {
    // // It looks like our system clock isn't set yet because the current
    // // time right now
    // // is before an arbitrary time we made this build. Instead of
    // // inserting a bogus
    // // receive time in this case, use the timestamp of when the message
    // // was sent.
    // now = sms.getTimestampMillis();
    // }
    //
    // values.put(Inbox.DATE, new Long(now));
    // values.put(Inbox.DATE_SENT, Long.valueOf(sms.getTimestampMillis()));
    // values.put(Inbox.PROTOCOL, sms.getProtocolIdentifier());
    // values.put(Inbox.READ, 0);
    // values.put(Inbox.SEEN, 0);
    // if (sms.getPseudoSubject().length() > 0) {
    // values.put(Inbox.SUBJECT, sms.getPseudoSubject());
    // }
    // values.put(Inbox.REPLY_PATH_PRESENT, sms.isReplyPathPresent() ? 1 : 0);
    // values.put(Inbox.SERVICE_CENTER, sms.getServiceCenterAddress());
    // return values;
    // }

    /**
     * Displays a class-zero message immediately in a pop-up window with the number from where it
     * received the Notification with the body of the message
     * 
     */
    // private void displayClassZeroMessage(Context context, SmsMessage sms,
    // String format) {
    // // Using NEW_TASK here is necessary because we're calling
    // // startActivity from outside an activity.
    // Intent smsDialogIntent = new Intent(context, ClassZeroActivity.class)
    // .putExtra("pdu", sms.getPdu())
    // .putExtra("format", format)
    // .setFlags(
    // Intent.FLAG_ACTIVITY_NEW_TASK
    // | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    //
    // context.startActivity(smsDialogIntent);
    // }

    private void registerForServiceStateChanges() {
        Context context = getApplicationContext();
        unRegisterForServiceStateChanges();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SERVICE_STATE_CHANGED);

        Logger.v(TAG, "registerForServiceStateChanges");
        context.registerReceiver(SmsSendReceiver.getInstance(), intentFilter);
    }

    private void unRegisterForServiceStateChanges() {

        Logger.v(TAG, "unRegisterForServiceStateChanges");

        try {
            Context context = getApplicationContext();
            context.unregisterReceiver(SmsSendReceiver.getInstance());
        } catch (IllegalArgumentException e) {
            // Allow un-matched register-unregister calls
        }
    }

}
