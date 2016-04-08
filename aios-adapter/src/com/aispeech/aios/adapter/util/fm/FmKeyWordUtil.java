package com.aispeech.aios.adapter.util.fm;

import android.text.TextUtils;

import com.aispeech.aios.adapter.bean.FmSearchParam;

/**
 * @desc 根据搜索参数对象计算出搜索关键字
 * @auth AISPEECH
 * @date 2016-01-11
 * @copyright aispeech.com
 */
public class FmKeyWordUtil {

    /**
     * 找出优先级高的字段作为搜索关键字
     * 优先级：keyWord > artist > album
     * @param fmSearchParam FmSearchParam object
     * @return 搜索关键字
     */
    public static String getSerchKeyWord(FmSearchParam fmSearchParam){

        String keyword = null;

        if (!TextUtils.isEmpty(fmSearchParam.getCategory())) {
            keyword = fmSearchParam.getCategory();
        }

        if (!TextUtils.isEmpty(fmSearchParam.getAlbum())) {
            keyword = fmSearchParam.getAlbum();
        }

        if (!TextUtils.isEmpty(fmSearchParam.getArtist())) {
            keyword = fmSearchParam.getArtist();
        }

        if (!TextUtils.isEmpty(fmSearchParam.getKeyWord())) {
            keyword = fmSearchParam.getKeyWord();
        }

        return keyword;
    }
}
