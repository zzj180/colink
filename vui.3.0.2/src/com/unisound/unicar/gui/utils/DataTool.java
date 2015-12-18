/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : DataTool.java
 * @ProjectName : vui_common
 * @PakageName : cn.yunzhisheng.common
 * @Author : Alieen
 * @CreateDate : 2014-2-25
 */
package com.unisound.unicar.gui.utils;

import android.annotation.SuppressLint;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Alieen
 * @CreateDate : 2014-2-25
 * @ModifiedBy : Alieen
 * @ModifiedDate: 2014-2-25
 * @Modified: 2014-2-25: 实现基本功能
 */
public class DataTool {
    public static final int ADDRESS_HTTP = 1;
    public static final int ADDRESS_FTP = 2;

    /**
     * CRC 直接使用hash来代替
     * 
     * @param aString
     * @return
     */
    public static int CRC32(String aString) {
        return aString.hashCode();
    }


    public static String numberWordToNumber(String no) {
        if (no == null || no.equals("")) {
            return "";
        }

        String mNumbersChineseL = "零一二三四五六七八九";
        String mNumbersChineseS = "零壹贰叁肆伍陆柒捌玖";
        String mNumbersChineseB = "　要两　　　　　　　";
        String mNumbers = "0123456789";
        StringBuilder sBuilder = new StringBuilder();

        for (int i = 0; i < no.length(); i++) {
            char c = no.charAt(i);

            int index = -1;

            if (index < 0) {
                index = mNumbersChineseL.indexOf(c);
            }

            if (index < 0) {
                index = mNumbersChineseS.indexOf(c);
            }

            if (index < 0) {
                index = mNumbersChineseB.indexOf(c);
            }

            if (index < 0) {
                index = mNumbers.indexOf(c);
            }

            if (index < 0) {
                return "";
            }

            sBuilder.append(mNumbers.charAt(index));
        }

        return sBuilder.toString();
    }

    public static String wordToNumberString(String no) {
        if (no == null || no.equals("")) {
            return "";
        }

        String mNumbersChineseL = "零一二三四五六七八九";
        String mNumbersChineseS = "零壹贰叁肆伍陆柒捌玖";
        String mNumbers = "0123456789";
        StringBuilder sBuilder = new StringBuilder();

        for (int i = 0; i < no.length(); i++) {
            char c = no.charAt(i);

            int index = -1;

            if (index < 0) {
                index = mNumbersChineseL.indexOf(c);
            }

            if (index < 0) {
                index = mNumbersChineseS.indexOf(c);
            }


            if (index < 0) {
                index = mNumbers.indexOf(c);
            }

            if (index < 0) {
                sBuilder.append(c);
                continue;
            }
            sBuilder.append(mNumbers.charAt(index));
        }
        return sBuilder.toString();
    }

    /**
     * @Description : 格式化字符串 {0} {1} {2}... para0, para1, para2...
     * @Author : Alieen
     * @CreateDate : 2014-7-14
     * @param format
     * @param para
     * @return
     */
    public static String formatString(String format, Object... para) {
        if (format == null || para == null || "".equals(format)) {
            return "";
        }

        String result = format;

        for (int i = 0; i < para.length; i++) {
            String swapStr = "{" + i + "}";
            String paraStr = "";
            if (para[i] != null) {
                paraStr = "" + para[i];
            } else {
                paraStr = "";
            }
            result = result.replace(swapStr, paraStr);
        }

        return result;
    }

    public static String formatString(String format, List<String> para) {
        if (format == null || para == null || "".equals(format)) {
            return "";
        }

        String result = format;

        for (int i = 0; i < para.size(); i++) {
            String swapStr = "{" + i + "}";
            String paraStr = "";
            if (para.get(i) != null) {
                paraStr = "" + para.get(i);
            } else {
                paraStr = "";
            }
            result = result.replace(swapStr, paraStr);
        }

        return result;
    }

    /**
     * @Description : 笛卡尔积
     * @Author : Alieen
     * @CreateDate : 2014-7-18
     * @param inputList
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ArrayList Dikaerji(ArrayList inputList) {

        ArrayList MainList = (ArrayList) inputList.get(0);
        for (int i = 1; i < inputList.size(); i++) {
            ArrayList addList = (ArrayList) inputList.get(i);
            ArrayList temp = new ArrayList();
            for (int j = 0; j < MainList.size(); j++) // 每次先计算两个集合的笛卡尔积，然后用其结果再与下一个计算
            {
                for (int k = 0; k < addList.size(); k++) {
                    ArrayList cut = new ArrayList();
                    if (MainList.get(j) instanceof ArrayList) {
                        cut.addAll((ArrayList) MainList.get(j));
                    } else {
                        cut.add(MainList.get(j));
                    }
                    if (addList.get(k) instanceof ArrayList) {
                        cut.addAll((ArrayList) addList.get(k));
                    } else {
                        cut.add(addList.get(k));
                    }
                    temp.add(cut);
                }
            }
            MainList = temp;
        }
        return MainList;
    }


    /**
     * @Description : byteToString
     * @Author : Alieen
     * @CreateDate : 2012-6-12
     * @param b 字符串数组
     * @param len 长度，如果不知道长度，则给数组b的长度
     * @param code 编码格式，默认UTF8
     * @return
     */
    public static String byteToString(byte[] b, int len, String code) {
        String result = "";
        if (b != null && b.length >= len) {
            int index = 0;
            while (index < len) {
                if (b[index] != '\0') {
                    index++;
                } else {
                    break;
                }
            }

            if (index > 0) {
                byte[] br = new byte[index];
                System.arraycopy(b, 0, br, 0, index);
                try {
                    if (code == null || code.equals("")) {
                        code = "utf8";
                    }

                    result = new String(br, code);
                } catch (UnsupportedEncodingException e) {
                    result = "";
                }

                br = null;
            }
        }

        return result;
    }


    /**
     * 测试目录是否存在，如果不存在，则创建一系列目录
     * 
     * @param path 需要测试的目录（如果路径中拥有文件名将被忽略）
     * @return 是否成功创建目录
     */
    public static boolean validPath(String path) {
        boolean result = false;
        int len = 0;
        if (path != null && (len = path.indexOf('/') + 1) >= 0) {
            File file = null;
            while (len < path.length() && (len = path.indexOf('/', len)) >= 0) {
                String root = path.substring(0, ++len);
                root = root == null ? "" : root;
                file = new File(root);
                if (!file.exists() && !file.mkdir()) return false;
                result = true;
            }
        }
        return result;
    }

    /**
     * Description : 将时间（秒）按照 时:分:秒 格式输出 Author : Alieen Create Date : 2014-6-30
     * 
     * @param duration
     * @return
     */
    public static String getDuration(int duration) {
        Integer hour = duration / 3600;
        int tmp = duration % 3600;
        Integer minute = tmp / 60;
        Integer second = tmp % 60;

        String time = "";
        if (hour > 0) {
            time = hour + ":";

            // 歌曲时间 分前面显示0
            if (minute.toString().length() == 1) {
                time += DataTool.getZero(1) + minute.toString() + ":";
            } else {
                time += minute.toString() + ":";
            }
        } else {
            time += minute.toString() + ":";
        }

        if (second.toString().length() == 1) {
            time += DataTool.getZero(1) + second.toString();
        } else {
            time += second.toString();
        }

        return time;
    }

    /**
     * Description : 将字符串转换成数值 Author : Alieen Create Date : 2014-6-29
     * 
     * @param str
     * @return
     */
    public static int string2int(String str) {
        int values = 0;
        if (str == null || str.equals("")) {
            return values;
        } else {
            try {
                values = Integer.valueOf(str);
            } catch (NumberFormatException e) {
                values = 0;
            }
            return values;
        }
    }

    /**
     * Description : 拆分字符串 Author : Alieen Create Date : 2014-6-29
     * 
     * @param a2 被拆分字符串
     * @param ch 拆分自符
     * @return
     */
    public static ArrayList<String> stringSplit(String a2, String ch) {
        if (a2 == null || a2.length() <= 0) {
            return null;
        }
        String a[] = a2.split(ch);
        ArrayList<String> array = new ArrayList<String>();
        for (int i = 0; i < a.length; i++) {
            String str = a[i];
            if (str == null || str.trim().equals("")) {
                continue;
            }
            array.add(str.trim());
        }
        return array;
    }

    /**
     * Description : 拼合字符串 Author : Alieen Create Date : 2014-6-29
     * 
     * @param array 数据组
     * @param ch 拼合字符
     * @return
     */
    public static String stringSpell(ArrayList<String> array, String ch) {
        if (array == null || array.size() <= 0) {
            return "";
        }

        String values = (String) array.get(0);
        for (int i = 1; i < array.size(); i++) {
            values += ch;
            values += (String) array.get(i);
        }

        return values;
    }

    /**
     * Description : 得到 序号 Author : Alieen Create Date : 2014-6-29
     * 
     * @param num 当前数值
     * @param max 最大长度
     * @return 序号，填充0
     */
    public static String getNumber(int num, int max) {
        if (num < 0) {
            return getZero(max);
        }
        String sNum = String.valueOf(num);
        if (sNum.length() <= max) {
            sNum = getZero(max - sNum.length()) + sNum;
        } else {
            sNum = sNum.substring(sNum.length() - max, sNum.length());
        }
        return sNum;
    }

    /**
     * Description : 得到填充的0的字符串 Author : Alieen Create Date : 2014-6-29
     * 
     * @param n 需要多少个0
     * @return
     */
    public static String getZero(int n) {
        String s = "";
        for (int i = 0; i < n; i++) {
            s += "0";
        }
        return s;
    }

    /**
     * Description : 去掉首尾空格 Author : Alieen Create Date : 2014-6-29
     * 
     * @param str
     * @return
     */
    public static String trim(String str) {
        if (str == null || str.equals("")) {
            return "";
        } else {
            return str.trim();
        }
    }

    /**
     * 获取当前秒数
     * 
     * @param path
     * @return
     */
    public static long getNowTime() {
        return System.currentTimeMillis();
    }

    /**
     * 返回文件类型的大写
     * 
     * @param displayName
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String getFileTypeCapital(String displayName) {
        int index = displayName.lastIndexOf(".");
        if (index < 0 || index >= displayName.length()) {
            return "";
        }
        String type = displayName.substring(displayName.lastIndexOf(".") + 1, displayName.length());
        return type.toUpperCase();
    }

    /**
     * 路径解析 得到的数组中 String[0] 是dir 且开头是带 “/”，结尾不带“/” 比如 “/sdcard” String[1] 是displayname
     */
    public static String[] PathAnalysis(String path) {
        if (path == null || path.equals("")) {
            return null;
        }
        String[] values = new String[2];
        int length = path.length();
        int dirIndex = path.indexOf("/");
        int nameIndex = path.lastIndexOf("/");

        String dir = "";
        String displayName = "";

        if (dirIndex > 0) {
            // 第一位不是"/"
            dir = "/";
        } else if (dirIndex == 0) {
            // 第一位是 "/"
            dir = "";
        }

        if (nameIndex > 0 && nameIndex < length) {
            dir += path.substring(0, nameIndex);
            displayName = path.substring(nameIndex + 1, length);
        } else if (nameIndex == 0) {
            dir = "/";
            displayName = path.substring(1, length);
        }

        if (nameIndex < 0 || dirIndex < 0) {
            dir = "/";
            displayName = path;
        }
        values[0] = dir;
        values[1] = displayName;
        return values;
    }

    /**
     * 文件名解析 得到的数组中 String[0] 是文件名 String[1] 是文件类型
     */
    public static String[] NameAnalysis(String displayName) {
        if (displayName == null || displayName.equals("")) {
            return null;
        }
        String[] values = new String[2];
        int index = displayName.lastIndexOf(".");
        if (index > 0 && index < displayName.length()) {
            values[0] = displayName.substring(0, index);
            values[1] = displayName.substring(index + 1, displayName.length());
        } else if (index == 0) {
            values[0] = "";
            values[1] = displayName;
        } else {
            values[0] = displayName;
            values[1] = "";
        }
        return values;
    }

    /**
     * 路径合成
     * 
     * @param dir
     * @param displayName
     * @return
     */
    public static String PathSpell(String dir, String displayName) {
        if (dir == null || dir.equals("")) {
            if (displayName == null || displayName.equals("")) {
                return "";
            } else {
                return displayName;
            }
        }
        return dir + "/" + displayName;
    }

    /**
     * @Description : delChar
     * @param str
     * @return
     */
    public static String delChar(String str) {
        if (str == null || str.equals("")) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = tranChar(str.charAt(i), true);
            if (c != 0) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String tranChar(String str) {
        if (str == null || str.equals("")) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = tranChar(str.charAt(i), false);
            if (c != 0) {
                sb.append(c);
            }
        }

        return sb.toString().trim();
    }

    private static char tranChar(char c, boolean del) {

        String str1 = "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ";
        String str2 = "ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ";
        String str3 = "abcdefghijklmnopqrstuvwxyz";
        String str4 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String str5 = ",.? ";
        String str6 = "，。？　";

        int index = -1;
        if ((index = str1.indexOf(c)) >= 0) {
            c = str3.charAt(index);
        } else if ((index = str2.indexOf(c)) >= 0) {
            c = str4.charAt(index);
        } else if ((index = str5.indexOf(c)) >= 0) {
            c = del ? 0 : str6.charAt(index);
        } else if ((index = str6.indexOf(c)) >= 0) {
            c = del ? 0 : str6.charAt(index);
        }

        return c;
    }

    /**
     * @Description : trimChar
     * @Author : Alieen
     * @CreateDate : 2012-6-19
     * @param str
     * @return
     */
    public static String trimChar(String str) {
        if (str == null || str.equals("")) {
            return "";
        }

        int start = 0;
        int end = 0;

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ',' || str.charAt(i) == '，' || str.charAt(i) == '.'
                    || str.charAt(i) == '。' || str.charAt(i) == '?' || str.charAt(i) == '？'
                    || str.charAt(i) == ' ' || str.charAt(i) == '　') {
                continue;
            } else {
                start = i;
                break;
            }
        }
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == ',' || str.charAt(i) == '，' || str.charAt(i) == '.'
                    || str.charAt(i) == '。' || str.charAt(i) == '?' || str.charAt(i) == '？'
                    || str.charAt(i) == ' ' || str.charAt(i) == '　') {
                continue;
            } else {
                end = i + 1;
                break;
            }
        }

        if (end > start && start >= 0) {
            return str.substring(start, end).trim();
        }

        return "";
    }

    static public String escapePartial(String str) {
        final Pattern pt1 = Pattern.compile("<s>|<unk>|\\s+");
        final Pattern pt2 = Pattern.compile("(</s>)+|<SIL>");

        String ret;

        ret = pt1.matcher(str).replaceAll("");
        ret = pt2.matcher(ret).replaceAll("，");

        return ret;
    }

    static public String escapeSentence(String str) {
        final Pattern pt3 = Pattern.compile("(^，)|(，$)");
        final Pattern pt4 = Pattern.compile("([吗呢]$)|(^(请问)|(为(什么|啥))|(怎么))");

        String ret = escapePartial(str);

        ret = pt3.matcher(ret).replaceAll("");

        if (pt4.matcher(ret).find()) {
            ret += "？";
        } else {
            ret += "。";
        }

        return ret;
    }

}
