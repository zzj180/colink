/**
 * Copyright (c) 2012-2015 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : AMapUriData.java
 * @ProjectName : uniCarSolution
 * @PakageName : com.unisound.unicar.gui.route.operation
 * @Author : Alieen
 * @CreateDate : 2015-07-08
 */
package com.unisound.unicar.gui.route.operation;

import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Alieen
 * @CreateDate : 2015-07-08
 * @ModifiedBy : Alieen
 * @ModifiedDate: 2015-07-08
 * @Modified: 2015-07-08: 实现基本功能
 */
public class AMapUri {
    public static final String TAG = AMapUri.class.getSimpleName();

    private static final String TEMPLATE = "androidamap://%1$s";

    private String mService;
    private HashMap<String, Object> mParams;

    public AMapUri(String service) {
        mService = service;
        mParams = new HashMap<String, Object>();
    }

    public void addParam(String key, Object value) {
        Logger.d(TAG, "addParam:key " + key + ",value " + value);
        if (mParams.containsKey(key)) {
            Logger.w(TAG, "mParams have exist (key='" + key + "',value='" + mParams.get(key) + "')"
                    + ",will be overrided!");
        }
        mParams.put(key, value);
    }

    public String getDatString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format(TEMPLATE, mService));

        String split = "?";
        Set<String> keys = mParams.keySet();
        for (String key : keys) {
            builder.append(split);
            builder.append(key);
            builder.append("=");
            builder.append(mParams.get(key));
            split = "&";
        }

        return builder.toString();
    }
    
    public static void openBaiduNavi(Context context,String action){
		Intent intent = new Intent();
		intent.setData(Uri.parse(action));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}
}
