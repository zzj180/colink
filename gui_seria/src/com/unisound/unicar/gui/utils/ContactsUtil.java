package com.unisound.unicar.gui.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Contacts Util
 * 
 * @author xiaodong
 * @date 20150707
 */
public class ContactsUtil {

    private static final String TAG = ContactsUtil.class.getSimpleName();


    /**
     * get Default Contact Name when no contact find
     * 
     * @param context
     * @return
     */
    // private static String getDefaultConatctName(Context context){
    // boolean isNetworkConnect = Network.checkNetworkConnected(context);
    // Logger.d(TAG, "!--->getDefaultConatctName--isNetworkConnect = " + isNetworkConnect);
    // return isNetworkConnect ? context.getString(R.string.default_name_no_contact_online)
    // : context.getString(R.string.default_name_no_contact_offline);
    // }

    /**
     * has Contact With Name in db
     * 
     * @param ctx
     * @return
     */
    // public static boolean hasContactWithName(Context ctx){
    // boolean hasContactWithName = false;
    // Map<Integer, String> contactMap = getContactMap(ctx);
    // if (contactMap.size() > 0) {
    // hasContactWithName = true;
    // }
    // return hasContactWithName;
    // }

    /**
     * 
     * @param context
     * @param domainHelpText
     * @return
     */
    // public static String getHelpTextWithContactName(Context context, String domainHelpText){
    // String textShow = domainHelpText;
    // String contactName = ContactsUtil.getRandomContactName(context);
    //
    // //if no contact find, use default name
    // if ("".equals(contactName) || null == contactName) {
    // contactName = getDefaultConatctName(context);
    // }
    //
    // Object[] nameFormatParam = new Object[1];
    // nameFormatParam[0] = contactName;
    // try {
    // textShow = String.format(domainHelpText, nameFormatParam);
    // } catch (Exception e) {
    // textShow = domainHelpText;
    // Logger.w(TAG, "!--->String.format error! Text = "+domainHelpText);
    // }
    // Logger.d(TAG, "!--->contactName = "+contactName+"; textShow = "+textShow);
    // return textShow;
    // }

    /**
     * get a random Contact Name from Contact DB
     * 
     * @param ctx
     * @return randomContactName or "" if no Contact Name find
     */
    public static String getRandomContactName(Context ctx) {
        String name = "";
        Map<Integer, String> contactMap = getContactMap(ctx);
        Integer[] keys = contactMap.keySet().toArray(new Integer[0]);
        if (keys.length > 0) {
            Random random = new Random();
            Integer randomKey = keys[random.nextInt(keys.length)];
            name = contactMap.get(randomKey);
        }
        Logger.d(TAG, "!--->getRandomContactName----name = " + name);
        contactMap = null;
        return name;
    }


    /**
     * 
     * @param ctx
     * @return
     */
    private static Map<Integer, String> getContactMap(Context ctx) {
        Map<Integer, String> contactMap = new HashMap<Integer, String>();
        Cursor cursor = null;
        try {
            cursor =
                    ctx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
                            null, null, null);
            int contactIdIndex = 0;
            int nameIndex = 0;
            if (cursor == null) {// XD added 20150908
                return contactMap;
            }
            if (cursor.getCount() > 0) {
                contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            }
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(contactIdIndex);
                String name = cursor.getString(nameIndex);
                // Logger.d(TAG, "!--->contactId = " + contactId + "; name = " + name);

                int cid = 0;
                try {
                    cid = Integer.parseInt(contactId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // if name if is phone number, do not save it
                if (!isMobileNumber(name)) {
                    contactMap.put(cid, name);
                } else {
                    // Logger.d(TAG, "name = " + name + " is Mobile Number");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return contactMap;
    }

    /**
     * get Contact Name By CONTACT_ID
     * 
     * @param ctx
     * @param contactId
     * @return
     */
    private String getContactNameById(Context ctx, String contactId) {
        Logger.d(TAG, "!--->getContactNameById ---------");
        String name = "";
        String[] projection =
                {ContactsContract.PhoneLookup.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
        Cursor cursor = null;
        try {
            cursor =
                    ctx.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            projection, // Which columns to return.
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = '" + contactId
                                    + "'", // WHERE clause.
                            null, // WHERE clause value substitution
                            null); // Sort order.

            if (cursor == null) {
                Logger.d(TAG, "getPeople null");
                return name;
            }
            Logger.d(TAG, "getPeople cursor.getCount() = " + cursor.getCount());
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                // 取得联系人名字
                int nameFieldColumnIndex =
                        cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                name = cursor.getString(nameFieldColumnIndex);
                Logger.d(TAG, "!--->name = " + name + ";nameFieldColumnIndex = "
                        + nameFieldColumnIndex);// FC ?
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return name;
    }

    /**
     * get Contact Name By NUMBER
     * 
     * @param ctx
     * @param number
     * @return
     */
    public static String queryContactNameByNumber(Context ctx, String number) {
        Logger.d(TAG, "!--->getContactNameById ---------");
        String name = "";
        String[] projection =
                {ContactsContract.PhoneLookup.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
        Cursor cursor = null;
        try {
            cursor =
                    ctx.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, // Which
                                                                                            // columns
                                                                                            // to
                                                                                            // return.
                            ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + number + "'", // WHERE
                                                                                                   // clause.
                            null, // WHERE clause value substitution
                            null); // Sort order.

            if (cursor == null) {
                Logger.d(TAG, "getPeople null");
                return name;
            }
            Logger.d(TAG, "getPeople cursor.getCount() = " + cursor.getCount());
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                // 取得联系人名字
                int nameFieldColumnIndex =
                        cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                name = cursor.getString(nameFieldColumnIndex);
                Logger.d(TAG, "!--->name = " + name + ";nameFieldColumnIndex = "
                        + nameFieldColumnIndex);// FC ?
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return name;
    }

    /**
     * check a string whether a Mobile Number
     * 
     * @param mobiles
     * @return
     */
    private static boolean isMobileNumber(String mobiles) {
        if (mobiles == null || "".equals(mobiles)) {
            return false;
        }
        mobiles = mobiles.replaceAll(" ", "");
        mobiles = mobiles.replaceAll("-", "");
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 
     * @param ctx
     */
    public static void testReadAllContacts(Context ctx) {
        Logger.d(TAG, "!-->testReadAllContacts()------");
        StringBuffer buffer = new StringBuffer();

        Cursor cursor =
                ctx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null,
                        null, null);
        int contactIdIndex = 0;
        int nameIndex = 0;

        if (cursor.getCount() > 0) {
            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }
        while (cursor.moveToNext()) {
            String contactId = cursor.getString(contactIdIndex);
            String name = cursor.getString(nameIndex);

            // Log.i(TAG, "contactId = "+contactId+"; name = "+name);
            /*
             * 查找该联系人的phone信息
             */
            Cursor phones =
                    ctx.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                            null, null);
            int phoneIndex = 0;
            if (phones.getCount() > 0) {
                phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            }
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phoneIndex).replaceAll("-", "");
                String content = contactId + ";" + name + ";" + phoneNumber;

                buffer.append(content + "\n");
                Log.i(TAG, "!--->contactId = " + contactId + "; name = " + name
                        + "; phoneNumber = " + phoneNumber);

            }

            /*
             * 查找该联系人的email信息
             */
            // Cursor emails =
            // ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            // null,
            // ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactId,
            // null, null);
            // int emailIndex = 0;
            // if(emails.getCount() > 0) {
            // emailIndex = emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            // }
            // while(emails.moveToNext()) {
            // String email = emails.getString(emailIndex);
            // Log.i(TAG, email);
            // }

        }

        boolean result = FileOperation.writeContacts(buffer.toString());
        Log.d(TAG, "!--->result = " + result);
    }

}
