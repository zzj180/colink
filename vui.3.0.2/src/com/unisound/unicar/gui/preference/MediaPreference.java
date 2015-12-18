/**
 * Copyright (c) 2012-2014 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : MediaPreference.java
 * @ProjectName : vui_datamodel
 * @PakageName : cn.yunzhisheng.preference
 * @Author : Alieen
 * @CreateDate : 2014-11-10
 */
package com.unisound.unicar.gui.preference;


/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Alieen
 * @CreateDate : 2014-11-10
 * @ModifiedBy : Alieen
 * @ModifiedDate: 2014-11-10
 * @Modified: 2014-11-10: 实现基本功能
 */
public class MediaPreference {
    public static final String TAG = "MediaPreference";

    // "SYSTEM"|"CUSTOM"
    public static String MEDIA_TYPE = "SYSTEM";

    public static void initPreference() {
        MEDIA_TYPE = PrivatePreference.getValue("media_type", MEDIA_TYPE);
    }

    public static void init() {
        initPreference();
        PrivatePreference.addUpdateListener(new IUpdatePreferenceListener() {
            @Override
            public void onUpdate() {
                initPreference();
            }
        });
    }
}
