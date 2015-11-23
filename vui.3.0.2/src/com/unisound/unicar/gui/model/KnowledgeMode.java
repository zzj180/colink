/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : KnowledgeMode.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.knowledge
 * @Author : Brant
 * @CreateDate : 2012-11-30
 */
package com.unisound.unicar.gui.model;

import java.util.Random;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.Logger;

import android.content.Context;
import android.text.TextUtils;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2012-11-30
 * @ModifiedBy : Brant
 * @ModifiedDate: 2012-11-30
 * @Modified: 2012-11-30: 实现基本功能
 */
public class KnowledgeMode {
    public static final String TAG = "KnowledgeMode";

    public static final int KNOWLEDGE_STAGE_CONTACT_NOT_FOUND = 10001;
    public static final int KNOWLEDGE_STAGE_CONTACT_AUTO_PICK = 10002;
    public static final int KNOWLEDGE_STAGE_NUMBER_AUTO_PICK = 10003;
    public static final int KNOWLEDGE_STAGE_NUMBER_NOT_FOUND = 10004;
    public static final int KNOWLEDGE_STAGE_NETWORK_EXCEPTION = 10005;
    public static final int KNOWLEDGE_STAGE_NO_NETWORK = 10006;
    public static final int KNOWLEDGE_STAGE_NETWORK_TIMEOUT = 10007;
    public static final int KNOWLEDGE_STAGE_NO_RECOGNISE_RESULT = 10008;
    public static final int KNOWLEDGE_STAGE_NO_INPUT = 10009;
    public static final int KNOWLEDGE_STAGE_HELP = 10010;

    public static final int KNOWLEDGE_STAGE_CALL_CONTACT_NAME = 10901;
    public static final int KNOWLEDGE_STAGE_CALL_CONTACT_NAME_NO_RECOGNISE_RESULT = 10902;
    public static final int KNOWLEDGE_STAGE_CALL_CONTACT_NAME_NO_INPUT = 10903;
    public static final int KNOWLEDGE_STAGE_CALL_DIALING = 10904;

    public static final int KNOWLEDGE_STAGE_SMS_CONTACT_NAME = 11001;
    public static final int KNOWLEDGE_STAGE_SMS_CONTENT = 11002;
    public static final int KNOWLEDGE_STAGE_SMS_CONTACT_NAME_NO_RECOGNISE_RESULT = 11003;


    public static final int KNOWLEDGE_STAGE_MEMO_DATETIME = 11201;
    public static final int KNOWLEDGE_STAGE_MEMO_CREATED = 11202;
    public static final int KNOWLEDGE_STAGE_MEMO_DATETIME_EXPIRED = 11203;
    public static final int KNOWLEDGE_STAGE_MEMO_DATETIME_NO_RECOGNISE_RESULT = 11204;

    public static final int KNOWLEDGE_STAGE_APP_NOT_FOUND = 11301;
    public static final int KNOWLEDGE_STAGE_APP_AUTO_PICK = 11302;
    public static final int KNOWLEDGE_STAGE_APP_OPEN_NAME = 11303;
    public static final int KNOWLEDGE_STAGE_APP_OPENING = 11304;
    public static final int KNOWLEDGE_STAGE_APP_OPEN_NAME_NO_RECOGNISE_RESULT = 11305;
    public static final int KNOWLEDGE_STAGE_APP_UNINSTALL_NAME = 11306;
    public static final int KNOWLEDGE_STAGE_APP_UNINSTALLING = 11307;
    public static final int KNOWLEDGE_STAGE_APP_UNINSTALL_NAME_NO_RECOGNISE_RESULT = 11308;

    public static final int KNOWLEDGE_STAGE_WEATHER_FETCHING = 11401;
    public static final int KNOWLEDGE_STAGE_WEATHER_CITY_NOT_FOUND = 11402;
    public static final int KNOWLEDGE_STAGE_WEATHER_LOCATE_FAILED = 11403;

    public static final int KNOWLEDGE_STAGE_SEARCH_KEYWORD = 11501;

    public static final int KNOWLEDGE_STAGE_MUSIC_NOT_FOUND = 11601;

    public static final int KNOWLEDGE_STAGE_WEB_NOT_FOUND = 11701;

    private static Random mRandom = new Random();
    private static int mKnowledgeModeSign = 0;
    private static int mRepeatCount = 0;

    private static final int KNOWLEDGE_ARRAY_START_INDEX = 1;

    public static String getKnowledgeAnswer(Context context, int stage) {
        if (mKnowledgeModeSign == stage) {
            mRepeatCount++;
        } else {
            mKnowledgeModeSign = stage;
            mRepeatCount = 0;
        }

        int[] repeatArr = null;
        switch (stage) {
            case KNOWLEDGE_STAGE_NETWORK_EXCEPTION:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_NETWORK_EXCEPTION;
                break;
            case KNOWLEDGE_STAGE_NO_NETWORK:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_NO_NETWORK;
                break;
            case KNOWLEDGE_STAGE_NETWORK_TIMEOUT:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_NETWORK_TIMEOUT;
                break;
            case KNOWLEDGE_STAGE_NO_RECOGNISE_RESULT:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_NO_RECOGNISE_RESULT;
                break;
            case KNOWLEDGE_STAGE_NO_INPUT:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_NO_INPUT;
                break;
            case KNOWLEDGE_STAGE_HELP:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_HELP;
                break;
            case KNOWLEDGE_STAGE_CALL_CONTACT_NAME:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_CALL_CONTACT_NAME;
                break;
            case KNOWLEDGE_STAGE_CONTACT_NOT_FOUND:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_CONTACT_NOT_FOUND;
                break;
            case KNOWLEDGE_STAGE_CONTACT_AUTO_PICK:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_CONTACT_AUTO_PICK;
                break;
            case KNOWLEDGE_STAGE_NUMBER_NOT_FOUND:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_NUMBER_NOT_FOUND;
                break;
            case KNOWLEDGE_STAGE_NUMBER_AUTO_PICK:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_NUMBER_AUTO_PICK;
                break;
            case KNOWLEDGE_STAGE_CALL_CONTACT_NAME_NO_RECOGNISE_RESULT:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_NUMBER_AUTO_PICK;
                break;
            case KNOWLEDGE_STAGE_CALL_CONTACT_NAME_NO_INPUT:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_CALL_CONTACT_NAME_NO_INPUT;
                break;
            case KNOWLEDGE_STAGE_CALL_DIALING:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_CALL_DIALING;
                break;
            case KNOWLEDGE_STAGE_SMS_CONTACT_NAME:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_SMS_CONTACT_NAME;
                break;
            case KNOWLEDGE_STAGE_SMS_CONTENT:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_SMS_CONTENT;
                break;
            case KNOWLEDGE_STAGE_SMS_CONTACT_NAME_NO_RECOGNISE_RESULT:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_SMS_CONTACT_NAME_NO_RECOGNISE_RESULT;
                break;
            case KNOWLEDGE_STAGE_MEMO_DATETIME:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_MEMO_DATETIME;
                break;
            case KNOWLEDGE_STAGE_MEMO_CREATED:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_MEMO_CREATED;
                break;
            case KNOWLEDGE_STAGE_MEMO_DATETIME_EXPIRED:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_MEMO_DATETIME_EXPIRED;
                break;
            case KNOWLEDGE_STAGE_MEMO_DATETIME_NO_RECOGNISE_RESULT:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_MEMO_DATETIME_NO_RECOGNISE_RESULT;
                break;
            case KNOWLEDGE_STAGE_APP_NOT_FOUND:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_APP_NOT_FOUND;
                break;
            case KNOWLEDGE_STAGE_WEB_NOT_FOUND:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_WEB_NOT_FOUND;
                break;
            case KNOWLEDGE_STAGE_APP_AUTO_PICK:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_APP_AUTO_PICK;
                break;
            case KNOWLEDGE_STAGE_APP_OPEN_NAME:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_APP_OPEN_NAME;
                break;
            case KNOWLEDGE_STAGE_APP_OPENING:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_APP_OPENING;
                break;
            case KNOWLEDGE_STAGE_APP_OPEN_NAME_NO_RECOGNISE_RESULT:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_APP_OPEN_NAME_NO_RECOGNISE_RESULT;
                break;
            case KNOWLEDGE_STAGE_APP_UNINSTALL_NAME:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_APP_UNINSTALL_NAME;
                break;
            case KNOWLEDGE_STAGE_APP_UNINSTALLING:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_APP_UNINSTALLING;
                break;
            case KNOWLEDGE_STAGE_APP_UNINSTALL_NAME_NO_RECOGNISE_RESULT:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_APP_UNINSTALL_NAME_NO_RECOGNISE_RESULT;
                break;
            case KNOWLEDGE_STAGE_WEATHER_FETCHING:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_WEATHER_FETCHING;
                break;
            case KNOWLEDGE_STAGE_WEATHER_CITY_NOT_FOUND:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_WEATHER_CITY_NOT_FOUND;
                break;
            case KNOWLEDGE_STAGE_WEATHER_LOCATE_FAILED:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_WEATHER_LOCATE_FAILED;
                break;
            case KNOWLEDGE_STAGE_SEARCH_KEYWORD:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_SEARCH_KEYWORD;
                break;
            case KNOWLEDGE_STAGE_MUSIC_NOT_FOUND:
                repeatArr = KnowledgeBase.KNOWLEDGE_STAGE_MUSIC_NOT_FOUND;
                break;
        }

        if (repeatArr == null || repeatArr.length == 0) {
            return "";
        }

        if (mRepeatCount >= repeatArr.length) {
            mRepeatCount = repeatArr.length - 1;
        }

        String[] answerArr = KnowledgeBase.getStringArray(context, repeatArr[mRepeatCount]);
        return getRandomItem(answerArr);
    }

    public static String getRecognitionNoResultAnswer(Context context, String message) {
        if (!TextUtils.isEmpty(message)) {
            String[] grammerKeyWordCall =
                    KnowledgeBase.getStringArray(context, R.array.knowledge_grammer_keyword_call);
            for (String str : grammerKeyWordCall) {
                if (message.contains(str)) {
                    return getRandomContentString(context, R.array.knowledge_grammer_example_call);
                }
            }

            String[] grammerKeyWordSms =
                    KnowledgeBase.getStringArray(context, R.array.knowledge_grammer_keyword_sms);
            for (String str : grammerKeyWordSms) {
                if (message.contains(str)) {
                    return getRandomContentString(context, R.array.knowledge_grammer_example_sms);
                }
            }

            String[] grammerKeyWordWeather =
                    KnowledgeBase
                            .getStringArray(context, R.array.knowledge_grammer_keyword_weather);
            for (String str : grammerKeyWordWeather) {
                if (message.contains(str)) {
                    return getRandomContentString(context,
                            R.array.knowledge_grammer_example_weather);
                }
            }

            String[] grammerKeyWordApp =
                    KnowledgeBase.getStringArray(context, R.array.knowledge_grammer_keyword_app);
            for (String str : grammerKeyWordApp) {
                if (message.contains(str)) {
                    return getRandomContentString(context, R.array.knowledge_grammer_example_app);
                }
            }

            String[] grammerKeyWordMemo =
                    KnowledgeBase.getStringArray(context, R.array.knowledge_grammer_keyword_memo);
            for (String str : grammerKeyWordMemo) {
                if (message.contains(str)) {
                    return getRandomContentString(context, R.array.knowledge_grammer_example_memo);
                }
            }

            String[] grammerKeyWordSearch =
                    KnowledgeBase.getStringArray(context, R.array.knowledge_grammer_keyword_search);
            for (String str : grammerKeyWordSearch) {
                if (message.contains(str)) {
                    return getRandomContentString(context, R.array.knowledge_grammer_example_search);
                }
            }
        }
        return getRandomString(context, R.array.knowledge_stage_no_recognise_result);
    }

    public static String getRandomString(Context context, int res) {
        String[] answerArr = KnowledgeBase.getStringArray(context, res);
        return getRandomItem(answerArr);
    }

    public static String getHeadString(Context context, int res) {
        String[] answerArr = KnowledgeBase.getStringArray(context, res);
        if (answerArr.length >= KNOWLEDGE_ARRAY_START_INDEX) {
            return answerArr[0];
        }
        return "";
    }

    public static String getRandomContentString(Context context, int res) {
        String[] answerArr = KnowledgeBase.getStringArray(context, res);
        return getRandomItem(answerArr, KNOWLEDGE_ARRAY_START_INDEX);
    }

    public static String getAllString(Context context, int res) {
        String[] answerArr = KnowledgeBase.getStringArray(context, res);
        StringBuilder sb = new StringBuilder();

        if (answerArr != null && answerArr.length > 0) {
            for (int i = 0; i < answerArr.length - KNOWLEDGE_ARRAY_START_INDEX; i++) {
                if (sb.length() > 0) {
                    sb.append("\n\n");
                }
                sb.append((i + 1) + "、" + answerArr[i + KNOWLEDGE_ARRAY_START_INDEX]);
            }
        }
        return sb.toString();
    }

    private static String getRandomItem(String[] arr) {
        if (arr == null || arr.length == 0) {
            return "";
        } else {
            return arr[mRandom.nextInt(arr.length)];
        }
    }

    private static String getRandomItem(String[] arr, int startIndex) {
        if (arr == null || arr.length == 0) {
            return "";
        }
        /* <xd added begin */
        else if (arr.length == 1) {
            Logger.d(TAG, "!--->arr.length == 1, arr[0] = " + arr[0]);
            return arr[0];
        }
        /* xd added End > */
        else {
            return arr[mRandom.nextInt(arr.length - startIndex) + startIndex];
        }
    }
}
