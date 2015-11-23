package com.unisound.unicar.gui.domain.localsearch;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;


public class DianPingURLBuilder {

    public static final String TAG = "DianPingURLBuilder";

    private String mUrl;
    private String mAppKey;
    private String mSecret;

    public DianPingURLBuilder(String url, String appKey, String secret) {
        mUrl = url;
        mAppKey = appKey;
        mSecret = secret;
    }

    private String sign(Map<String, String> paramMap) {
        // 对参数名进行字典排序
        String[] keyArray = paramMap.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);

        // 拼接有序的参数名-值串
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mAppKey);
        for (String key : keyArray) {
            stringBuilder.append(key).append(paramMap.get(key));
        }

        stringBuilder.append(mSecret);
        byte[] sha1Digest = null;
        try {
            sha1Digest = getSHA1Digest(stringBuilder.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (sha1Digest != null) {
            return byte2hex(sha1Digest);
        }
        return null;
    }

    public String getUrl(Map<String, String> paramMap) {
        String sign = sign(paramMap);
        paramMap.put("appkey", mAppKey);
        paramMap.put("sign", sign);
        return buildGetUrl(mUrl, paramMap);
    }

    private static byte[] getSHA1Digest(String data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            bytes = md.digest(data.getBytes("UTF-8"));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.getMessage());
        }
        return bytes;
    }

    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    private static String buildGetUrl(String urlDomain, Map<String, String> form) {
        StringBuilder requestUrl = new StringBuilder();
        requestUrl.append(urlDomain);
        requestUrl.append("?");
        String joinChar = "";
        for (Map.Entry<String, String> entry : form.entrySet()) {
            requestUrl.append(joinChar);
            requestUrl.append(entry.getKey());
            requestUrl.append("=");
            String value;
            try {
                value = URLEncoder.encode(entry.getValue(), "UTF-8");
            } catch (Exception e) {
                value = "";
            }
            requestUrl.append(value);
            joinChar = "&";
        }
        return requestUrl.toString();
    }


}
