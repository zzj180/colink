package com.aispeech.aios.adapter.bean;

import com.aispeech.aios.adapter.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class RpcRecall {

    private String url;
    private List<byte[]> byteList;

    public RpcRecall(String url, byte[]... bytes) {
        this.url = url;
        byteList = new ArrayList<byte[]>();
        for (byte[] item : bytes) {
            byteList.add(item);
        }
    }

    public String getUrl() {
        return url;
    }

    public List<byte[]> getByteList() {
        return byteList;
    }

    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer("RpcRecall{ url=\"");
        sb.append(url);
        sb.append("\", bytes=");

        for (byte[] item : byteList) {
            sb.append(StringUtil.getEncodedString(item));
            sb.append("\n");
        }
        sb.append(" }");
        return sb.toString();
    }
}
