package com.aispeech.aios.adapter.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @desc 内核需要的电台数据
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class FmItem {
    public String title;
    public long id;
    public int type;

    public static final int TRACK = 1;
    public static final int ALBUM = 2;

    public JSONObject getJson() {
        JSONObject object = new JSONObject();
        try {
            if (type == TRACK) {
                object.put("id", id);
                object.put("track", title);
            } else if (type == ALBUM) {
                object.put("id", id);
                object.put("album", title);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    @Override
    public String toString() {
        return getJson().toString();
    }

}
