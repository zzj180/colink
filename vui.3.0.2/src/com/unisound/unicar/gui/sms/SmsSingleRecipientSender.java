package com.unisound.unicar.gui.sms;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import com.unisound.unicar.gui.model.Telephony.Sms;
import com.unisound.unicar.gui.utils.Logger;

public class SmsSingleRecipientSender extends SmsMessageSender {

    private static final String TAG = "SmsSingleRecipientSender";
    private final boolean mRequestDeliveryReport;
    private String mDest;
    private Uri mUri;

    public SmsSingleRecipientSender(Context context, String dest, String msgText, long threadId,
            boolean requestDeliveryReport, Uri uri) {
        super(context, null, msgText, threadId);
        mDest = dest;
        mRequestDeliveryReport = requestDeliveryReport;
        mUri = uri;
    }

    public void sendMessage(long token) throws SmsException {

        Log.v(TAG, "sendMessage token: " + token);
        if (mDest == null) {
            throw new SmsException("Null destination.");
        }
        if (mMessageText == null) {
            // Don't try to send an empty message, and destination should be
            // just
            // one.
            throw new SmsException("Null message body or have multiple destinations.");
        }

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> messages = null;

        messages = smsManager.divideMessage(mMessageText);
        // remove spaces from destination number (e.g. "801 555 1212" ->
        // "8015551212")
        mDest = mDest.replaceAll(" ", "");
        mDest = mDest.replaceAll("-", "");
        int messageCount = messages.size();

        if (messageCount == 0) {
            // Don't try to send an empty message.
            throw new SmsException("SmsMessageSender.sendMessage: divideMessage returned "
                    + "empty messages. Original message is \"" + mMessageText + "\"");
        }

        boolean moved = Sms.moveMessageToFolder(mContext, mUri, Sms.MESSAGE_TYPE_OUTBOX);
        if (!moved) {
            throw new SmsException("SmsMessageSender.sendMessage: couldn't move message "
                    + "to outbox: " + mUri);
        }

        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>(messageCount);
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>(messageCount);
        for (int i = 0; i < messageCount; i++) {
            if (mRequestDeliveryReport) {
                Logger.d(TAG, "!--->MESSAGE_STATUS_RECEIVED_ACTION");
                // TODO: Fix: It should not be necessary to
                // specify the class in this intent. Doing that
                // unnecessarily limits customizability.
                deliveryIntents.add(PendingIntent.getBroadcast(mContext, 0, new Intent(
                        MessageStatusReceiver.MESSAGE_STATUS_RECEIVED_ACTION, mUri, mContext,
                        MessageStatusReceiver.class), 0));
            }
            Intent intent =
                    new Intent(SmsSenderService.MESSAGE_SENT_ACTION, mUri, mContext,
                            SmsSendReceiver.class);

            int requestCode = 0;
            if (i == messageCount - 1) {
                // Changing the requestCode so that a different pending intent
                // is created for the last fragment with
                // EXTRA_MESSAGE_SENT_SEND_NEXT set to true.
                requestCode = 1;
                intent.putExtra(SmsSenderService.EXTRA_MESSAGE_SENT_SEND_NEXT, true);
            }

            Logger.v(TAG, "!--->sendMessage sendIntent: " + intent);

            sentIntents.add(PendingIntent.getBroadcast(mContext, requestCode, intent, 0));
        }
        try {
            smsManager.sendMultipartTextMessage(mDest, mServiceCenter, messages, sentIntents,
                    deliveryIntents);

        } catch (Exception ex) {
            Log.e(TAG, "SmsMessageSender.sendMessage: caught", ex);
            throw new SmsException("SmsMessageSender.sendMessage: caught " + ex
                    + " from SmsManager.sendTextMessage()");
        }

        Log.d(TAG, "[SmsSingleRecipientSender]  sendMessage: address=" + mDest + ", threadId="
                + mThreadId + ", uri=" + mUri + ", msgs.count=" + messageCount);

    }
}
