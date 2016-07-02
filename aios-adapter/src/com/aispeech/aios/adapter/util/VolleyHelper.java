package com.aispeech.aios.adapter.util;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.AdapterApplication;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

/**
 * @desc Volley帮助类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class VolleyHelper {
    private static VolleyHelper mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VolleyHelper() {
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }


    /**
     * @return VolleyHelper实例
     */
    public static synchronized VolleyHelper getInstance() {
        if (mInstance == null) {
            mInstance = new VolleyHelper();
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(AdapterApplication.getContext());
        }
        return mRequestQueue;
    }

    /**
     * 添加一个请求到队列
     * @param req Request object
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * @return ImageLoader对象
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**url含中文 要转码
     * URLEncoder.encode(XXX,"utf-8");
     * @param method POST or GET
     * @param url 网络地址
     * @param successListener 请求成功的监听器
     * @param errorListener 请求失败的监听器
     */
    public void JsonObjectRequest(int method, String url, Response.Listener successListener, Response.ErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method,url, successListener, errorListener);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(6000, 1, 1f));
        addToRequestQueue(jsonObjectRequest);
        AILog.d(VolleyHelper.class.getSimpleName(),"request");
    }
}