/**
 * Copyright (c) 2012-2013 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : HttpRequest.java
 * @ProjectName : iShuoShuo2
 * @PakageName : cn.yunzhisheng.net
 * @Author : Brant
 * @CreateDate : 2013-1-7
 */
package com.unisound.unicar.gui.domain.localsearch;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import android.text.TextUtils;

import com.squareup.okhttp.OkHttpClient;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Brant
 * @CreateDate : 2013-1-7
 * @ModifiedBy : Brant
 * @ModifiedDate: 2013-1-7
 * @Modified: 2013-1-7: 实现基本功能
 */
public class DefaultHttpRequest {
    public static final String TAG = "DefaultHttpRequest";

    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

    private static HashMap<String, String> mDnsServerMap = new HashMap<String, String>();
    private static long mCreateDnsMapTime = -1;

    /**
     * @Description : 获取http的网络数据
     * @Author : Dancindream
     * @CreateDate : 2014-5-11
     * @param url
     * @param method: GET or POST
     * @param body: post data
     * @param connectTimeout: connect server Timeout
     * @param readTimeout: read data Timeout
     * @return
     * @throws Exception
     */
    public static String getHttpResponse(String url, String method, byte[] body,
            int connect_timeout, int read_timeout) throws Exception {
        Logger.d(TAG, "getHttpResponse url:" + url);
        Logger.d(TAG, "connect_timeout:" + connect_timeout + "; read_timeout:" + read_timeout);
        String urlString = url;
        URL u = null;
        if (urlString == null || urlString.equals("")) {
            throw new Exception("Malformed url");
        }
        try {
            u = new URL(urlString);
        } catch (MalformedURLException e1) {
            throw new Exception("Malformed url");
        }
        String domain = u.getHost();
        String ip = "";
        long now = System.currentTimeMillis();
        if (mCreateDnsMapTime > 0 && (now - mCreateDnsMapTime >= 3600000)) {
            mDnsServerMap.clear();
        }

        if (mDnsServerMap.containsKey(domain)) {
            ip = mDnsServerMap.get(domain);
        } else {
            try {
                InetAddress server = InetAddress.getByName(domain);
                if (server != null && domain != null && domain.contains("hivoice.cn")) {
                    ip = server.getHostAddress();
                    if (mCreateDnsMapTime <= 0) {
                        mCreateDnsMapTime = now;
                    }
                    mDnsServerMap.put(domain, ip);
                }
            } catch (UnknownHostException e) {}
        }
        if (ip != null && !ip.equals("")) {
            urlString = urlString.replace(domain, ip);
        }

        try {
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(connect_timeout, TimeUnit.MILLISECONDS);
            client.setReadTimeout(read_timeout, TimeUnit.MILLISECONDS);

            method = method == null ? "" : method.trim();
            if ("GET".equals(method.toUpperCase())) {
                HttpURLConnection connection = client.open(new URL(urlString));
                InputStream in = null;
                try {
                    // Read the response.
                    in = connection.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    for (int count; (count = in.read(buffer)) != -1;) {
                        out.write(buffer, 0, count);
                    }
                    byte[] response = out.toByteArray();
                    return new String(response, "UTF-8");
                } finally {
                    if (in != null) {
                        in.close();
                    }
                    client = null;
                }
            } else if ("POST".equals(method.toUpperCase())) {
                HttpURLConnection connection = client.open(new URL(urlString));
                OutputStream out = null;
                InputStream in = null;
                try {
                    // Write the request.
                    connection.setRequestMethod("POST");
                    if (body != null) {
                        out = connection.getOutputStream();
                        out.write(body);
                        out.close();
                    }

                    // Read the response.
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new Exception("Unexpected HTTP response: "
                                + connection.getResponseCode() + " "
                                + connection.getResponseMessage());
                    }
                    in = connection.getInputStream();

                    ByteArrayOutputStream array = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    for (int count; (count = in.read(buffer)) != -1;) {
                        array.write(buffer, 0, count);
                    }
                    byte[] response = array.toByteArray();
                    return new String(response, "UTF-8");
                } finally {
                    try {
                        if (out != null) out.close();
                    } catch (Exception e) {}
                    try {
                        if (in != null) in.close();
                    } catch (Exception e) {}

                }
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            throw new Exception("Malformed url");
        } catch (SocketTimeoutException e2) {
            e2.printStackTrace();
            throw new Exception(e2.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

        return "";
    }

    /**
     * @Description : 获取http的网络数据
     * @Author : Dancindream
     * @CreateDate : 2014-5-11
     * @param url
     * @param method: GET or POST
     * @param readTimeout: read data Timeout
     * @return
     * @throws Exception
     */
    public static String getHttpResponse(String url, String method, int readTimeout)
            throws Exception {
        return getHttpResponse(url, method, null, DEFAULT_CONNECT_TIMEOUT, readTimeout);
    }

    /**
     * @Description : 获取http的网络数据
     * @Author : Dancindream
     * @CreateDate : 2014-5-11
     * @param url
     * @param method: GET or POST
     * @param connectTimeout: connect server Timeout
     * @param readTimeout: read data Timeout
     * @return
     * @throws Exception
     */
    public static String getHttpResponse(String url, String method, int connectTimeout,
            int readTimeout) throws Exception {
        return getHttpResponse(url, method, null, connectTimeout, readTimeout);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式
     * @param timeout 连接超时时间
     * @return URL所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param, int connectTimeout, int readTimeout) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        PrintWriter out = null;
        BufferedReader in = null;
        HttpURLConnection conn = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            Logger.d(TAG, "发送POST请求过程中出错：" + e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return result.toString();
    }
}
