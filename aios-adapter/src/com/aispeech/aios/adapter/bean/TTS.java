package com.aispeech.aios.adapter.bean;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * @desc
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class TTS {

    /**TTS语音播报的Action*/
    public static final String ACTION_TO_SPEAK="aios.intent.action.TO_SPEAK";
    /**TTS退出的Action*/
    public static final String ACTION_HOME_EXIT="android.intent.aciton.HOME_EXIT";
    /**TTS文本Intent键值*/
    public static final String INTENT_EXTRA_TEXT="aios.intent.extra.TEXT";
    /**TTS优先级Intent键值*/
    public static final String INTENT_EXTRA_PRIORITY="aios.intent.extra.PRIORITY";



    private int priority = 3;

    private String priorityExtra = INTENT_EXTRA_PRIORITY;

    private String text;

    private String textExtra = INTENT_EXTRA_TEXT;

    /**
     * 默认为2
     * @return
     */
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * 默认为"aios.intent.extra.PRIORITY"
     * @return
     */
    public String getPriorityExtra() {
        return priorityExtra;
    }

    public void setPriorityExtra(String priorityExtra) {
        this.priorityExtra = priorityExtra;
    }

    /**
     * 获取TTS文本
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * 设置TTS文本
     * @param text
     */
    public void setText(String text)  {
        try {
            this.text = new String(text.getBytes(Charset.forName("utf-8")),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 默认为"aios.intent.extra.TEXT"
     * @return
     */
    public String getTextExtra() {
        return textExtra;
    }

    public void setTextExtra(String textExtra) {
        this.textExtra = textExtra;
    }

    @Override
    public String toString() {
        return "TTS{" +
                "text='" + text + '\'' +
                ", priority=" + priority +
                ", priorityExtra='" + priorityExtra + '\'' +
                ", textExtra='" + textExtra + '\'' +
                '}';
    }
}
