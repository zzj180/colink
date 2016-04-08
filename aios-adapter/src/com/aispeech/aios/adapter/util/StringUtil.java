package com.aispeech.aios.adapter.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.R;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

/**
 * @desc 文本工具类
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class StringUtil {

    private static final String TAG = "AIOS-StringUtil";

    /**
     * 把complete中所有words数组中的元素高亮
     *
     * @param context  Context
     * @param textView TextView
     * @param complete 完整String串
     * @param words    String数组
     */
    public static void highNightWords(Context context, TextView textView, String complete, String... words) {
        if (complete == null) {
            complete = "";
        }
        AILog.i(TAG, "完整字符串：" + complete);

        SpannableStringBuilder builder = new SpannableStringBuilder(complete);
        ForegroundColorSpan highNightSpan;
        int index;

        if (words != null) {
            for (String word : words) {
                AILog.i(TAG, "需要高亮的字符串：" + word);
                if (!TextUtils.isEmpty(word) && complete.contains(word)) {//高亮歌曲名
                    highNightSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.text_hightlight));
                    index = complete.indexOf(word);
                    builder.setSpan(highNightSpan, index, index + word.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                }
            }
        }

        textView.setText(builder);

    }

    /**
     * 字节数组转字符串，经过“utf-8”编码
     *
     * @param bytes 源数据
     * @return 经过编码的String数据
     */
    public static String getEncodedString(byte[] bytes) {
        String result = null;
        try {
            result = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            AILog.e(TAG, e.toString());
        }
        return result;
    }

    /**
     * 判断参数是否是十进制数
     * @param match 需要判断的数
     * @return true:是十进制数;false:不是十进制数
     */
    public static boolean isDecimalNumber(String match) {
        String reg = "([+|-])?(([0-9]+.[0-9]*)|([0-9]*.[0-9]+)|([0-9]+))((e|E)([+|-])?[0-9]+)?";

        if (match == null || match.trim().equals("")) {
            return false;
        }

        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(match).matches();
    }
}
