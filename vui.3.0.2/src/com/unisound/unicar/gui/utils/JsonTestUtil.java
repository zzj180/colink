package com.unisound.unicar.gui.utils;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;

/**
 * 
 * @author xiaodong
 * @date 20150708
 */
public class JsonTestUtil {

    private static final String TAG = JsonTestUtil.class.getSimpleName();

    /**
     * xd added for test 20150708
     * 
     * @param ctx
     * @return
     */
    public static JSONObject getTestJsonObject(Context ctx) {
        String json = JsonTestUtil.readAssetsFile(ctx);
        Logger.d(TAG, "!--->json = " + json);
        JSONObject obj = JsonTool.parseToJSONObject(json);
        return obj;
    }

    /**
     * 
     * @param ctx
     * @return
     */
    public static String readAssetsFile(Context ctx) {
        String text = "";
        try {
            // Return an AssetManager instance for your application's package
            InputStream is = ctx.getAssets().open("json.txt");
            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            text = new String(buffer, "utf-8");// GB2312
            // Finally stick the string into the text view.
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
        return text;
    }


}
