package com.aispeech.aios.adapter.localScanService.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 扫描本地音乐时，解析文件夹json的工具类
 * @auth AISPEECH
 * @date 2016-02-19
 * @copyright aispeech.com
 */
public class JsonUtil {

    public static List<String> getFolders(String jsonString) throws JSONException {
        List<String> folders = new ArrayList<String>();
        JSONArray jrr = new JSONArray(jsonString);
        JSONObject job = new JSONObject();
        for (int i = 0; i < jrr.length(); i++) {
            job = jrr.getJSONObject(i);
            if (job.has("folder")) {
                folders.add(job.optString("folder"));
            }
        }
        return folders;
    }

}
