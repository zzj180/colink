package com.unisound.unicar.namesplit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/*
 * 将非中文字符（全半角）和空格（全半角），视作分隔符 连续的非中文字符视作1个 a. 忽略空格，从字符串中提取所有长度大于等于2的中文字符串，作为中文名称内容，记为格式2 b.
 * 不忽略空格，从字符串中提取所有长度大于等于2的中文字符串，作为中文名称内容，也记为格式2 对上面两步骤得到的内容进行去重
 * 
 * 丁森（地税） 处理后得到 丁森、地税，2个结果 地税: 丁森 处理后得到 地税：丁森，2个结果
 */
public class SplitWordManager {
    // 匹配超过两个字的中文词汇
    private final String MORE_THAN_TWO_WORD_PATTERN = "([\u4E00-\u9FA5]{2,})";

    public List<String> extendSplitWords(List<String> inputNameList) {
        List<String> extendedNameList = new ArrayList<String>();
        for (String currentLine : inputNameList) {
            extendedNameList.addAll(extendNameBySpliting(currentLine));
        }
        return extendedNameList;
    }

    public List<String> extendNameBySpliting(String name) {
        if (name == null) return new ArrayList<String>();

        List<String> nameList = new ArrayList<String>();

        // 空格当做分隔符
        if (name.contains(" ")) {
            String nameSplitedBySpace = name.replaceAll("(\\s)", ",");
            // System.out.println("---:"+nameSplitedBySpace);
            nameList.addAll(splitSingleName(nameSplitedBySpace));
        }
        // 忽视空格, 将空格两边的字符组合到一起
        String nameIgnoreSpace = name.replaceAll("\\s", "");
        // System.out.println("----:"+nameIgnoreSpace);
        nameList.addAll(splitSingleName(nameIgnoreSpace));
        return nameList;
    }


    private List<String> splitSingleName(String name) {
        Matcher matcher = Pattern.compile(this.MORE_THAN_TWO_WORD_PATTERN).matcher(name);
        List<String> outputArrayList = new ArrayList<String>();
        while (matcher.find()) {
            outputArrayList.add(matcher.group());
        }
        return outputArrayList;
    }

    public static void main(String[] args) {
        SplitWordManager splitWordManager = new SplitWordManager();

        ArrayList<String> inputArrayList = new ArrayList<String>();
        // inputArrayList.add("李仲元 王天一  Rose");
        inputArrayList.add("@@@李仲元 王天一$电机");
        // inputArrayList.add("穆亚敏,梁会彬");

        List<String> outputArrayList = splitWordManager.extendSplitWords(inputArrayList);
        for (String outputStr : outputArrayList) {
            System.out.println(outputStr);
        }
    }
}
