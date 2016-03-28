package com.colink.zzj.txzassistant.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @desc  字符数据转换工具
 * @auth zzj
 * @date 2016-03-19
 */
public class StringUtil {
	/**
	 * 字符数组转换成字符串，并以、隔开
	 */
	public static String convertArrayToString(String[] array) {
		if (array == null || array.length == 0)
			return "";
		StringBuffer sb = new StringBuffer(array[0]);
		int length = array.length;
		for (int i = 1; i < length; i++) {
			sb.append("、" + array[i]);
		}
		return sb.toString();
	}
	
	 /**
     * 
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String clearSpecialCharacter(String str) throws PatternSyntaxException {
        if (null == str || "".equals(str)) {
            return str;
        }
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？-]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

}
