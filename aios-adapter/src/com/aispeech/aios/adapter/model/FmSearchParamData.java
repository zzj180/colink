package com.aispeech.aios.adapter.model;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.bean.FmSearchParam;
import com.aispeech.aios.adapter.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;


/**
 * @desc AIOS电台搜索接口处理
 * @auth AISPEECH
 * @date 2016-01-11
 * @copyright aispeech.com
 */
public class FmSearchParamData {

    private static final String TAG = "AIOS-Adapter-FmSearchParamData";

    private FmSearchParam mParam;
    private byte[] mBytes;

    public FmSearchParamData(List<byte[]> byteList) {
        if (byteList != null && byteList.size() > 0) {
            mBytes = byteList.get(0);
        }
    }

    public FmSearchParam getParam() {
        if (mBytes == null) {
            return null;
        }
        mParam = new Gson().fromJson(StringUtil.getEncodedString(mBytes), new TypeToken<FmSearchParam>() {
        }.getType());
        AILog.i(TAG, mParam);

       return mParam;
    }


}
