package com.unisound.unicar.gui.oem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import cn.yunzhisheng.common.PinyinConverter;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.unisound.unicar.gui.model.ContactInfo;
import com.unisound.unicar.gui.model.PhoneNumberInfo;
import com.unisound.unicar.gui.utils.JsonTool;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.utils.NumberFormat;
import com.unisound.unicar.namesplit.ImeNameExtender;

/**
 * 蓝牙联系人发广播
 * 
 * @author Administrator
 * 
 */
public class RomCustomReceiver extends BroadcastReceiver {

    private static final String TAG = "RomCustomRecevier";

    public static final String INTENT_BASE_ACTION = "cn.yunzhisheng.intent.action.";

    // 同步联系人
    public static final String CATION_CUSTOM_UPDATE = INTENT_BASE_ACTION + "custom.order.contact";

    private Context mContext;
    private ArrayList<String> contactsInfo = new ArrayList<String>();
    private Thread mThread = null;

    private ConcurrentHashMap<Long, Integer> mHashContactIDIndex =
            new ConcurrentHashMap<Long, Integer>();
    public static final String JSON_TYPE_OF_CONTENT = "jsonType";
    public static final String JSON_TYPE_OF_CONTENT_PHONE = "PHONE";
    public static final String JSON_TYPE_OF_CONTENT_CONATCT = "CONTACT";
    public static final String JSON_TYPE_OF_CONTENT_ID = "ID";


    private ArrayList<ContactInfo> mContacts = new ArrayList<ContactInfo>();
    private ArrayList<PhoneNumberInfo> phoneList = new ArrayList<PhoneNumberInfo>();

    private File mContactSavedFile;
    private final String contactCOPName = "datacontatct.cop";

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mContactSavedFile = new File(Environment.getExternalStorageDirectory(), contactCOPName);
        String action = intent.getAction();
        if (action.equals(CATION_CUSTOM_UPDATE)) {
            final ArrayList<String> contactList = intent.getStringArrayListExtra("contactList");
            Logger.d(TAG, "contactList = " + contactList.toString());
            if (contactList != null && contactList.size() > 1) {
                contactsInfo.clear();
                contactsInfo.addAll(contactList);
                if (contactsInfo != null && contactsInfo.size() > 0) {
                    insertContactInfosInNewThread(contactsInfo);
                }
            }

        }
    }

    private void insertContactInfosInNewThread(final ArrayList<String> contacts) {
        Logger.d(TAG, "insertContactInfosInNewThread");
        if (mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (contacts != null && contacts.size() > 1) {
                        cleanAllData();// 清空list值
                        for (int i = 0; i < contacts.size(); i++) {
                            String[] info = contacts.get(i).split("#");
                            Logger.d(TAG, "info : " + Arrays.asList(info));
                            if (info != null && info.length == 2) {
                                insertContactToList(contacts.size() - i + "", info[0], info[1]);
                            }
                        }
                        saveContactToFile(mContacts, phoneList);
                    }
                    Logger.d(TAG, CATION_CUSTOM_UPDATE + " sync end");
                    mThread = null;
                }
            });
        }
        if (!mThread.isAlive()) {
            Logger.d(TAG, CATION_CUSTOM_UPDATE + " start sync");
            mThread.start();
        }
    }

    private void cleanAllData() {

        if (mContacts != null) {
            mContacts.clear();
        }
        if (phoneList != null) {
            phoneList.clear();
        }
        if (mHashContactIDIndex != null) {
            mHashContactIDIndex.clear();
        }

    }

    /**
     * 添加蓝牙联系人到list
     * 
     * @param string
     * @param string2
     */
    protected void insertContactToList(String id, String name, String number) {
        if (name != null && number != null) {
            ContactInfo contact = new ContactInfo();
            contact.setContactId(Long.valueOf(id));
            contact.setContactType(ContactInfo.CONTACT_TYPE_PHONE);

            if (number != null && "".equals(number)) {
                contact.setHasPhoneNumber(1);
            }
            ImeNameExtender imeWordsExtender = new ImeNameExtender();
            ArrayList<String> extendedNameList =
                    imeWordsExtender.extendImeNames(new String[] {name});
            contact.setNickNameList(extendedNameList);

            String[] pinyins = PinyinConverter.getNameSpell1(name);
            if (pinyins != null) {
                contact.setQuanpin(pinyins[0]);
            }
            contact.setDisplayName(name);

            StringBuilder sb = new StringBuilder();
            for (int j = 0; extendedNameList != null && j < extendedNameList.size(); j++) {
                sb.append(extendedNameList.get(j) + " ");
            }
            contact.setId((int) getContactId(contact.getContactId(), contact.getContactType()));
            mContacts.add(contact);

            int index = mContacts.size() - 1;
            mHashContactIDIndex.put(getContactId(contact.getContactId(), contact.getContactType()),
                    index);

            PhoneNumberInfo numberInfo = new PhoneNumberInfo();
            numberInfo.setContactId(Integer.valueOf(id));
            numberInfo.setContactType(ContactInfo.CONTACT_TYPE_PHONE);

            numberInfo.setNumber(number);
            numberInfo.setRawNumber(NumberFormat.getCleanPhoneNumber(numberInfo.getNumber()));
            phoneList.add(numberInfo);

        }

    }

    private long getContactId(long contactId, int contactType) {
        return contactId * 10 + contactType;
    }

    /**
     * 将联系人数据写入到文件中
     * 
     * @param l 联系人的姓名类表
     * @param phones 联系人的电话列表
     */

    public void saveContactToFile(ArrayList<ContactInfo> l, ArrayList<PhoneNumberInfo> phones) {
        FileOutputStream fos = null;
        try {
            Logger.i(TAG, "--saveContactToFile--" + mContactSavedFile.getPath().toString() + " "
                    + Environment.getExternalStorageDirectory());
            Logger.i(TAG, "--saveContactToFile--" + l.size() + " " + phones.size());
            if (mContactSavedFile.exists()) {
                mContactSavedFile.delete();
            }

            mContactSavedFile.createNewFile();

            fos = new FileOutputStream(mContactSavedFile);
            for (int i = 0; i < l.size(); i++) {
                ContactInfo info = l.get(i);

                int id = info.getId();
                long contactId = info.getContactId();
                int contactType = info.getContactType();
                String displayName = info.getDisplayName();
                int photoId = info.getPhotoId();
                String quanpin = info.getQuanpin();
                int hasPhoneNumber = info.hasPhoneNumber();
                ArrayList<String> nickNameList = info.getNickName();

                JSONObject jObject = new JSONObject();
                JsonTool.putJSONObjectData(jObject, "jsonType", "CONTACT");
                JsonTool.putJSONObjectData(jObject, "contactId", String.valueOf(contactId));
                JsonTool.putJSONObjectData(jObject, "id", String.valueOf(id));
                JsonTool.putJSONObjectData(jObject, "contactType", String.valueOf(contactType));
                JsonTool.putJSONObjectData(jObject, "displayName", displayName);
                JsonTool.putJSONObjectData(jObject, "photoId", String.valueOf(photoId));
                JsonTool.putJSONObjectData(jObject, "quanpin", quanpin);
                JsonTool.putJSONObjectData(jObject, "hasPhoneNumber",
                        String.valueOf(hasPhoneNumber));

                JSONArray nickNameArray = new JSONArray();
                for (int k = 0; nickNameList != null && k < nickNameList.size(); k++) {
                    nickNameArray.put(k, nickNameList.get(k));
                }
                JsonTool.putJSONObjectData(jObject, "nickNameList", nickNameArray);

                Logger.i(TAG, "-contact-" + jObject.toString());

                fos.write((jObject.toString() + "\n").getBytes());
            }

            for (int k = 0; phones != null && k < phones.size(); k++) {
                PhoneNumberInfo phone = phones.get(k);
                JSONObject obj = new JSONObject();
                JsonTool.putJSONObjectData(obj, JSON_TYPE_OF_CONTENT, JSON_TYPE_OF_CONTENT_PHONE);
                JsonTool.putJSONObjectData(obj, "contactType",
                        String.valueOf(phone.getContactType()));
                JsonTool.putJSONObjectData(obj, "id", String.valueOf(phone.getId()));
                JsonTool.putJSONObjectData(obj, "contactId", String.valueOf(phone.getContactId()));
                JsonTool.putJSONObjectData(obj, "number", phone.getNumber());
                JsonTool.putJSONObjectData(obj, "rawNumber", phone.getRawNumber());
                JsonTool.putJSONObjectData(obj, "type", String.valueOf(phone.getType()));
                JsonTool.putJSONObjectData(obj, "label", phone.getLabel());
                JsonTool.putJSONObjectData(obj, "primaryValue",
                        String.valueOf(phone.getPrimaryValue()));
                JsonTool.putJSONObjectData(obj, "superPrimaryValue",
                        String.valueOf(phone.getSuperPrimaryValue()));
                JsonTool.putJSONObjectData(obj, "attribution", phone.getAttribution());

                Logger.i(TAG, "-phnoe-" + obj.toString());

                fos.write((obj.toString() + "\n").getBytes());
            }

            // mHashContactIDIndex
            if (mHashContactIDIndex != null && mHashContactIDIndex.size() > 0) {
                JSONObject jObjc = new JSONObject();
                JsonTool.putJSONObjectData(jObjc, JSON_TYPE_OF_CONTENT, JSON_TYPE_OF_CONTENT_ID);
                Iterator<Entry<Long, Integer>> i = mHashContactIDIndex.entrySet().iterator();
                JSONArray jArray = new JSONArray();
                while (i.hasNext()) {
                    Entry<Long, Integer> e = (Entry<Long, Integer>) i.next();
                    Long key = (Long) e.getKey();
                    Integer value = (Integer) e.getValue();

                    JSONObject jObject = new JSONObject();
                    JsonTool.putJSONObjectData(jObject, "key", key);
                    JsonTool.putJSONObjectData(jObject, "value", value);

                    jArray.put(jObject);
                }
                JsonTool.putJSONObjectData(jObjc, "data", jArray);

                Logger.i(TAG, "-ContactID-" + jObjc.toString());

                fos.write((jObjc.toString() + "\n").getBytes());
            }
            phoneList.clear();
            // fos.close();
            /**
             * SessionPreference.SAVE_CONTACT_DATA_DONE 这块需要通知vui联系人文件写完成，vui可以读取数据进行编译
             * 
             */
            Intent intent = new Intent();
            intent.setAction(WindowService.ACTION_CUSTOMFROMBT);
            mContext.sendBroadcast(intent);
            // if(contactModelListener != null){
            // contactModelListener.onDataDone(SessionPreference.SAVE_CONTACT_DATA_DONE);
            // }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
