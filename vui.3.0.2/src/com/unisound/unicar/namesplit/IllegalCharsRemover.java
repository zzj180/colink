package com.unisound.unicar.namesplit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 含有中文的联系人基础数据条目，保留中文、全半角英文a-z大小写、全半角数字0-9、基本符号集 不含有中文的联系人基础数据条目，保留全半角英文a-z大小写、全半角数字0-9、基本符号集、以及空格。
 * 只去掉不在基本符号集定义中的符号（如其它全、半角标点符号、西文、葡文、制表符、数学符号等）， 得到由上述保留内容单一或混合而成的字符串
 */
public class IllegalCharsRemover {
    public List<String> removeIllegalChars(List<String> inputNameList) {
        List<String> resultList = new ArrayList<String>();
        for (String inputtedName : inputNameList) {
            resultList.addAll(removeIllegalChars(inputtedName));
        }
        return resultList;
    }

    public List<String> removeIllegalChars(String name) {
        if (name == null) return new ArrayList<String>();

        if (this.containChineseChar(name)) {
            return formatChinseName(name);
        } else {
            return formatEnlishName(name);
        }
    }

    /*
     * 检查一个字符串中是否包含中文
     */
    private boolean containChineseChar(String input) {
        Matcher chineseMatcher = this.chinesePattern.matcher(input);
        if (!chineseMatcher.find()) return false;
        return true;
    }

    /*
     * 对包含中文的词进行处理, 结果存入outputArrayList 中文、全半角英文a-z大小写、全半角数字0-9、基本符号集
     */
    private List<String> formatChinseName(String uncleanedName) {
        String cleanedName = new String();

        List<String> cleanedNameList = new ArrayList<String>();
        Matcher nameMatcher = this.extractPatternForChinese.matcher(uncleanedName);
        while (nameMatcher.find()) {
            cleanedName += nameMatcher.group();
        }
        cleanedNameList.add(cleanedName);
        return cleanedNameList;
    }

    /*
     * 对不包含中文的词进行处理 全半角英文a-z大小写、全半角数字0-9、基本符号集、以及空格
     */
    private List<String> formatEnlishName(String uncleanedName) {
        List<String> cleanedNameList = new ArrayList<String>();
        String cleanedName = new String();
        Matcher nameMatcher = this.extractPatternForEnglish.matcher(uncleanedName);
        while (nameMatcher.find()) {
            cleanedName += nameMatcher.group();
        }

        // 排除纯空格的情况
        Matcher spaceMatcher = Pattern.compile("\\s+").matcher(cleanedName);
        if (!spaceMatcher.matches()) {
            cleanedNameList.add(cleanedName);
        }
        return cleanedNameList;
    }


    private Pattern extractPatternForChinese = Pattern
            .compile("([a-zａ-ｚA-ZＡ-Ｚ０-９0-9\u4E00-\u9FA5\\.．@＠]+)");
    private Pattern extractPatternForEnglish = Pattern.compile("([a-zａ-ｚA-ZＡ-Ｚ０-９0-9\\.．@＠\\s]+)");
    private Pattern chinesePattern = Pattern.compile(".*[\u4E00-\u9FA5]+.*");


    public static void main(String[] args) {
        List<String> inputNameList = new ArrayList<String>();
        inputNameList.add("王*!！（大林");
        inputNameList.add("Tom*)(*)(*) Herry");

        IllegalCharsRemover illegalCharsRemover = new IllegalCharsRemover();

        List<String> cleanWords = illegalCharsRemover.removeIllegalChars(inputNameList);
        for (String cleanWord : cleanWords) {
            System.out.println(cleanWord);
        }
    }
}
