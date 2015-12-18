package com.unisound.unicar.namesplit;

import java.util.ArrayList;
import java.util.List;

/*
 * 含有中文的联系人基础数据条目，保留中文、全半角英文a-z大小写、全半角数字0-9、基本符号集 不含有中文的联系人基础数据条目，保留全半角英文a-z大小写、全半角数字0-9、基本符号集、以及空格。
 * 只去掉不在基本符号集定义中的符号（如其它全、半角标点符号、西文、葡文、制表符、数学符号等）， 得到由上述保留内容单一或混合而成的字符串
 */
public class LastNameManager {

    public List<String> extractLastNames(List<String> inputtedNames) {
        List<String> lastNames = new ArrayList<String>();

        for (String inputtedName : inputtedNames) {
            lastNames.addAll(this.extractLastNames(inputtedName));
        }
        return lastNames;
    }

    public List<String> extractLastNames(String inputtedName) {
        if ((inputtedName.length() < 2)/* ||(inputtedName.length() > 4) */)
            return new ArrayList<String>();

        List<String> firstNames = FirstNameManager.getInstance().GetFirstNames(inputtedName);

        List<String> lastNames = new ArrayList<String>();
        for (String firstName : firstNames) {
            String lastName = inputtedName.substring(firstName.length());
            if (lastName.length() < 2) continue;
            lastNames.add(lastName);
        }
        return lastNames;
    }

}
