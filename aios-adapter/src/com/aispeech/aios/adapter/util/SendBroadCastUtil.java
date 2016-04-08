package com.aispeech.aios.adapter.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.AdapterApplication;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @desc 广播发送通用类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class SendBroadCastUtil {

    private Context context;
    private static SendBroadCastUtil mUtil;
    private Intent mIntent;
    private static final String TAG = "SendBroadCastUtil";

    private SendBroadCastUtil() {

        this.context = AdapterApplication.getContext();
        this.mIntent = new Intent();

    }

    /**
     * @return SendBroadCastUtil实例
     */
    public static synchronized SendBroadCastUtil getInstance() {

        if (null == mUtil) {
            mUtil = new SendBroadCastUtil();
        }
        return mUtil;
    }

    /**
     * 广播发送方法
     *
     * @param action 广播action
     * @param name   数据 key
     * @param data   携带 String 数据
     */
    public void sendBroadCast(String action, String name, String data) {
        AILog.i(TAG, "action:"+action+",data[key:"+name+",value:"+data+"]");
        mIntent = new Intent();
        mIntent.setAction(action);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(data)) {//如果携带有数据

            mIntent.putExtra(name, data);
        }
        context.sendBroadcast(mIntent);
    }

    /**
     * 延迟发送广播
     *
     * @param action 广播action
     * @param name   数据 key
     * @param data   携带 String 数据
     * @param delay  携带 String 数据
     */
    public void sendBroadCast(final String action, final String name, final String data, long delay) {
        AILog.i(TAG, "延迟"+delay+"毫秒广播");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sendBroadCast(action, name, data);
            }
        }, delay);
    }

}
