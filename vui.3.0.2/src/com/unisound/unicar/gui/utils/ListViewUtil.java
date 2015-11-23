package com.unisound.unicar.gui.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 
 * @author xiaodong
 * @date 20150723
 */
public class ListViewUtil {

    private static final String TAG = ListViewUtil.class.getSimpleName();


    /**
     * set ListView Height Based On Screen
     * 
     * @param context
     * @param listView
     * @param borderHight
     */
    public static void setListViewHeightBasedOnScreen(Context context, ListView listView,
            int borderHight) {
        Logger.d(TAG, "!--->setListViewHeightBasedOnScreen-----borderHight = " + borderHight);
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int screenHight = DeviceTool.getScreenHight(context);
        params.height = screenHight - borderHight;
        listView.setLayoutParams(params);
    }

    /**
     * set ListView Height Based On Children
     * 
     * @param context
     * @param listView
     * @param borderHight
     */
    public static void setListViewHeightBasedOnChildren(Context context, ListView listView,
            int borderHight) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        // listView.getDividerHeight()获取子项间分隔符占用的高度
        int listViewHight =
                totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        int screenHight = DeviceTool.getScreenHight(context);
        if (listViewHight > screenHight) {
            params.height = screenHight - borderHight;
        } else {
            params.height = listViewHight;
        }
        Logger.d(TAG, "!--->Set List View hight:" + params.height);
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }


}
