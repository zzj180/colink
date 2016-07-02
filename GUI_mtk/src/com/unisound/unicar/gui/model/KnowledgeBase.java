/**
 * Copyright (c) 2012-2012 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : KnowledgeBase.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.vui.assistant.knowledge
 * @Author : Brant
 * @CreateDate : 2012-11-30
 */
package com.unisound.unicar.gui.model;

import com.coogo.inet.vui.assistant.car.R;

import android.content.Context;
import android.util.SparseArray;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2012-11-30
 * @ModifiedBy : Brant
 * @ModifiedDate: 2012-11-30
 * @Modified: 2012-11-30: 实现基本功能
 */
public class KnowledgeBase {
    public static final String TAG = "KnowledgeBase";

    public static int[] KNOWLEDGE_STAGE_CONTACT_NOT_FOUND =
            new int[] {R.array.knowledge_stage_contact_not_found};
    public static int[] KNOWLEDGE_STAGE_CONTACT_AUTO_PICK =
            new int[] {R.array.knowledge_stage_contact_auto_pick};

    public static int[] KNOWLEDGE_STAGE_NUMBER_NOT_FOUND =
            new int[] {R.array.knowledge_stage_number_not_found};
    public static int[] KNOWLEDGE_STAGE_NUMBER_AUTO_PICK =
            new int[] {R.array.knowledge_stage_number_auto_pick};

    public static int[] KNOWLEDGE_STAGE_NETWORK_EXCEPTION =
            new int[] {R.array.knowledge_stage_network_exception};
    public static int[] KNOWLEDGE_STAGE_NO_NETWORK = new int[] {R.array.knowledge_stage_no_network};
    public static int[] KNOWLEDGE_STAGE_NETWORK_TIMEOUT =
            new int[] {R.array.knowledge_stage_network_timeout};

    public static int[] KNOWLEDGE_STAGE_NO_RECOGNISE_RESULT =
            new int[] {R.array.knowledge_stage_no_recognise_result};
    public static int[] KNOWLEDGE_STAGE_NO_INPUT = new int[] {R.array.knowledge_stage_no_input};

    public static int[] KNOWLEDGE_STAGE_HELP = new int[] {R.array.knowledge_stage_help};

    public static int[] KNOWLEDGE_STAGE_CALL_CONTACT_NAME =
            new int[] {R.array.knowledge_stage_call_contact_name};
    public static int[] KNOWLEDGE_STAGE_CALL_CONTACT_NAME_NO_RECOGNISE_RESULT =
            new int[] {R.array.knowledge_stage_call_contact_name_no_recognise_result_1};
    public static int[] KNOWLEDGE_STAGE_CALL_CONTACT_NAME_NO_INPUT =
            new int[] {R.array.knowledge_stage_call_contact_name_no_input_1};
    public static int[] KNOWLEDGE_STAGE_CALL_DIALING =
            new int[] {R.array.knowledge_stage_call_dialing};

    public static int[] KNOWLEDGE_STAGE_SMS_CONTACT_NAME =
            new int[] {R.array.knowledge_stage_sms_contact_name};
    public static int[] KNOWLEDGE_STAGE_SMS_CONTENT = new int[] {
            R.array.knowledge_stage_sms_content, R.array.knowledge_stage_sms_content_continue};
    public static int[] KNOWLEDGE_STAGE_SMS_CONTACT_NAME_NO_RECOGNISE_RESULT =
            new int[] {R.array.knowledge_stage_sms_contact_name_no_recognise_result_1};

    public static int[] KNOWLEDGE_STAGE_MEMO_DATETIME =
            new int[] {R.array.knowledge_stage_memo_datetime};
    public static int[] KNOWLEDGE_STAGE_MEMO_CREATED =
            new int[] {R.array.knowledge_stage_memo_created};
    public static int[] KNOWLEDGE_STAGE_MEMO_DATETIME_EXPIRED =
            new int[] {R.array.knowledge_stage_memo_datetime_expired_1};
    public static int[] KNOWLEDGE_STAGE_MEMO_DATETIME_NO_RECOGNISE_RESULT =
            new int[] {R.array.knowledge_stage_memo_datetime_no_recognise_result_1};

    public static int[] KNOWLEDGE_STAGE_APP_OPEN_NAME =
            new int[] {R.array.knowledge_stage_app_open_name};
    public static int[] KNOWLEDGE_STAGE_APP_NOT_FOUND =
            new int[] {R.array.knowledge_stage_app_not_found};
    public static int[] KNOWLEDGE_STAGE_APP_AUTO_PICK =
            new int[] {R.array.knowledge_stage_app_auto_pick};
    public static int[] KNOWLEDGE_STAGE_APP_OPENING =
            new int[] {R.array.knowledge_stage_app_opening};
    public static int[] KNOWLEDGE_STAGE_APP_OPEN_NAME_NO_RECOGNISE_RESULT =
            new int[] {R.array.knowledge_stage_app_open_name_no_recognise_result_1};
    public static int[] KNOWLEDGE_STAGE_APP_UNINSTALL_NAME =
            new int[] {R.array.knowledge_stage_app_uninstall_name};
    public static int[] KNOWLEDGE_STAGE_APP_UNINSTALLING =
            new int[] {R.array.knowledge_stage_app_uninstalling};
    public static int[] KNOWLEDGE_STAGE_APP_UNINSTALL_NAME_NO_RECOGNISE_RESULT =
            new int[] {R.array.knowledge_stage_app_uninstall_name_no_recognise_result_1};

    public static int[] KNOWLEDGE_STAGE_WEB_NOT_FOUND =
            new int[] {R.array.knowledge_stage_web_not_found};

    public static int[] KNOWLEDGE_STAGE_WEATHER_FETCHING =
            new int[] {R.array.knowledge_stage_weather_fetching};
    public static int[] KNOWLEDGE_STAGE_WEATHER_CITY_NOT_FOUND =
            new int[] {R.array.knowledge_stage_weather_city_not_found};
    public static int[] KNOWLEDGE_STAGE_WEATHER_LOCATE_FAILED =
            new int[] {R.array.knowledge_stage_weather_locate_failed};

    public static int[] KNOWLEDGE_STAGE_SEARCH_KEYWORD =
            new int[] {R.array.knowledge_stage_search_keyword};

    public static int[] KNOWLEDGE_STAGE_MUSIC_NOT_FOUND =
            new int[] {R.array.knowledge_stage_music_not_found};
    private static SparseArray<String[]> mStrArrCache = new SparseArray<String[]>();

    public static String[] getStringArray(Context context, int res) {
        String[] arr = mStrArrCache.get(res);
        if (arr == null) {
            arr = context.getResources().getStringArray(res);
            mStrArrCache.put(res, arr);
        }
        return arr;
    }
}
