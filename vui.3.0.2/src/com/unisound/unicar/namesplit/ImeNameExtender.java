package com.unisound.unicar.namesplit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ImeNameExtender {
    private final int MAX_USER_DATAITEM_LEN = 100;

    public ArrayList<String> extendImeNames(String[] inputNames) {

        HashSet<String> extendedNameSet = new HashSet<String>();

        for (String inputtedName : inputNames) {
            if (inputtedName.length() > MAX_USER_DATAITEM_LEN) continue;

            IllegalCharsRemover illegalCharsRemover = new IllegalCharsRemover();
            SplitWordManager splitWordManager = new SplitWordManager();
            LastNameManager lastNameManager = new LastNameManager();

            List<String> regularNameList = illegalCharsRemover.removeIllegalChars(inputtedName);
            SetHelper.addToSet(regularNameList, extendedNameSet);

            List<String> splittedWordList = splitWordManager.extendNameBySpliting(inputtedName);
            SetHelper.addToSet(splittedWordList, extendedNameSet);

            List<String> lastNameList = lastNameManager.extractLastNames(splittedWordList);
            SetHelper.addToSet(lastNameList, extendedNameSet);
        }

        return SetHelper.stringSetToList(extendedNameSet);
    }

    public static void main(String[] args) {
        ImeNameExtender imeWordsExtender = new ImeNameExtender();

        ArrayList<String> inputNameList = new ArrayList<String>();
        inputNameList.add("李仲元 王天一,)abc");
        inputNameList.add("梁家盟");
        inputNameList.add("董照焜");
        inputNameList.add("王*！（大林");
        inputNameList.add("Tom*)(*)(*) Herry");

        List<String> extendedNameList =
                imeWordsExtender.extendImeNames(inputNameList.toArray(new String[inputNameList
                        .size()]));
        for (String extendedName : extendedNameList) {
            System.out.println(extendedName);
        }
    }

}
