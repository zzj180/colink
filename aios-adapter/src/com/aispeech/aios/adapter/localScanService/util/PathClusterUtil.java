package com.aispeech.aios.adapter.localScanService.util;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.localScanService.bean.Folder;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 对音乐目录进行聚类操作
 * @auth AISPEECH
 * @date 2016-03-18
 * @copyright aispeech.com
 */
public class PathClusterUtil {

    private static final String TAG = "PathClusterUtil";

    /**
     * 对路径进行聚类，提高解析速度，节约资源，
     * 防止对接者传多个相同路径，或传了根目录还传子目录
     */
    public static List<String> getClusteredList(List<String> folderList) {

        AILog.i(TAG, "聚类之前所有目录:");
        print(folderList);

        if (folderList == null){
            return new ArrayList<String>();
        }

        List<String> firstFilteredList = new ArrayList<String>();//第一次过滤后的结果，去掉重复的目录
        for (String folder : folderList) {
            if (!firstFilteredList.contains(folder)) {
                firstFilteredList.add(folder);
            }
        }

        List<Folder> tmpFolderList = new ArrayList<Folder>();//把所有路径转换成Folder对象，每个对象附带默认一个false标志
        for (String folder : firstFilteredList) {
            tmpFolderList.add(new Folder(folder));
        }

        for (int i = 0, len = tmpFolderList.size(); i < len; i++) {//在带标志位的列表里面去标记那些重复的目录，标志为true
            for (int j = i + 1; j < len; j++) {
                String left = tmpFolderList.get(i).getPath();
                String right = tmpFolderList.get(j).getPath();
                if (left.startsWith(right)) {
                    tmpFolderList.get(i).setFlag(true);
                } else if (right.startsWith(left)) {
                    tmpFolderList.get(j).setFlag(true);
                }
            }
        }

        List<String> finalList = new ArrayList<String>();
        for (Folder folder : tmpFolderList) {

            if (!folder.isFlag()) {//没有标志为true的目录，说明是聚类的结果
                AILog.i(TAG, folder.getPath());
                finalList.add(folder.getPath());
            }
        }

        AILog.i(TAG, "聚类之后所有目录:");
        print(finalList);
        return finalList;
    }

    private static void print(List<String> list) {
        if (list != null) {
            for (String path : list) {
                AILog.i(TAG, path);
            }
        }
    }
}
