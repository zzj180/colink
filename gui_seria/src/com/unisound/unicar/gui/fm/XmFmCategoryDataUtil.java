/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : XmFmCategoryDataUtil.java
 * @ProjectName : uniCarSolution_xd_dev_0929
 * @PakageName : com.unisound.unicar.gui.fm
 * @version : 1.0
 * @Author : Xiaodong.He
 * @CreateDate : 2015-10-9
 */
package com.unisound.unicar.gui.fm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.unisound.unicar.gui.dao.FmCategoryDao;
import com.unisound.unicar.gui.utils.Logger;
import com.ximalaya.ting.android.opensdk.model.category.CategoryModel;


/**
 * @author xiaodong
 * 
 */
public class XmFmCategoryDataUtil {

    private static final String TAG = XmFmCategoryDataUtil.class.getSimpleName();

    /**
     * is Category Updated
     * 
     * @param newList
     * @param dbList
     * @return
     */
    public static boolean isCategoryUpdated(List<CategoryModel> newList, List<CategoryModel> dbList) {
        boolean isUpdated = false;
        if (null == dbList || null == newList) {
            return false;
        }
        if (dbList.size() != newList.size()) {
            Logger.d(TAG, "isCategoryUpdated---category data size changed.");
            return true;
        }

        Map<Integer, String> newCategoryMap = new HashMap<Integer, String>();
        for (CategoryModel newCategory : newList) {
            newCategoryMap.put(newCategory.getCategoryId(), newCategory.getCategoryName());
        }

        for (CategoryModel dbCategory : dbList) {
            String newValue = newCategoryMap.get(dbCategory.getCategoryId());
            if (TextUtils.isEmpty(newValue) || !newValue.equals(dbCategory.getCategoryName())) {
                Logger.d(
                        TAG,
                        "isCategoryUpdated---category data changed." + " oldId: "
                                + dbCategory.getCategoryId() + "; oldValue = "
                                + dbCategory.getCategoryName() + "; newValue: " + newValue);
                return true;
            }
        }
        return isUpdated;
    }


    /**
     * save Category Data to DB if XmFmCategoryData updated from SDK
     * 
     * @param newCategoryList
     */
    public static void saveCategoryDataToDB(Context context, List<CategoryModel> newCategoryList) {
        /*
         * for (XmFmCategory category : categoryList) { Logger.d(TAG,
         * "initCategoryData---:"+category.toString()); }
         */
        ArrayList<CategoryModel> dbList = FmCategoryDao.getInstance(context).getAllEntries();
        if (null != dbList && dbList.size() == 0) {
            Logger.d(TAG, "!--->saveCategoryDataToDB---db Category data is null, begin added");
            FmCategoryDao.getInstance(context).add(newCategoryList, false);
        } else if (XmFmCategoryDataUtil.isCategoryUpdated(newCategoryList, dbList)) {
            Logger.d(TAG, "!--->saveCategoryDataToDB---update db Category data...");
            FmCategoryDao.getInstance(context).add(newCategoryList, true);
        }
    }



    /**
     * write Category Data to DB if XmFmCategoryData update from local
     */
    private void initCategoryDataFromLocal(Context context) {
        List<CategoryModel> categoryList = XmFmCategoryDataUtil.getDefaultCategoryList();
        saveCategoryDataToDB(context, categoryList);
    }

    public static Map<Integer, String> getDefaultCategoryMap() {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(3, "有声书");
        map.put(2, "音乐");
        map.put(4, " 综艺娱乐");
        map.put(2, "相声评书");
        map.put(6, "儿童");
        map.put(1, "新闻");
        map.put(10, "情感生活");
        map.put(9, "历史人文");
        map.put(5, "外语");
        map.put(13, "培训讲座");
        map.put(14, "百家讲坛");
        map.put(15, "广播剧");
        map.put(16, "戏曲");
        map.put(17, "电台");
        map.put(8, "商业财经");
        map.put(18, "IT科技");
        map.put(7, "健康养生");
        map.put(20, "校园");
        map.put(21, "汽车");
        map.put(22, "旅游");
        map.put(23, "电影");
        map.put(24, "游戏");
        map.put(11, "其他");
        return map;
    }

    /***
     * get default category List 1 新闻 2 音乐 3 有声书 4 综艺娱乐 5 外语 6 儿童 7 健康养生 8 商业财经 9 历史人文 10 情感生活 11 其他
     * 12 相声评书 13 培训讲座 14 百家讲坛 15 广播剧 16 戏曲 17 电台 18 IT科技 20 校园 21 汽车 22 旅游 23 电影 24 游戏
     * 
     * @return
     */
    private static List<CategoryModel> getDefaultCategoryList() {
        List<CategoryModel> list = new ArrayList<CategoryModel>();

        CategoryModel category = new CategoryModel();
        category.setCategoryId(1);
        category.setCategoryName("新闻");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(2);
        category.setCategoryName("音乐");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(3);
        category.setCategoryName("有声书");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(4);
        category.setCategoryName("综艺娱乐");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(5);
        category.setCategoryName("外语");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(6);
        category.setCategoryName("儿童");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(7);
        category.setCategoryName("健康养生");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(8);
        category.setCategoryName("商业财经");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(9);
        category.setCategoryName("历史人文");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(10);
        category.setCategoryName("情感生活");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(11);
        category.setCategoryName("其他");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(12);
        category.setCategoryName("相声评书");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(13);
        category.setCategoryName("培训讲座");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(14);
        category.setCategoryName("百家讲坛");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(15);
        category.setCategoryName("广播剧");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(16);
        category.setCategoryName("戏曲");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(17);
        category.setCategoryName("电台");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(18);
        category.setCategoryName("IT科技");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(20);
        category.setCategoryName("校园");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(21);
        category.setCategoryName("汽车");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(22);
        category.setCategoryName("旅游");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(23);
        category.setCategoryName("电影");
        list.add(category);

        category = new CategoryModel();
        category.setCategoryId(24);
        category.setCategoryName("游戏");
        list.add(category);

        return list;
    }

}
