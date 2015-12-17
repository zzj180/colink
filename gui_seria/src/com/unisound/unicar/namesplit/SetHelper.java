package com.unisound.unicar.namesplit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetHelper {

    public static void addToSet(List<String> items, Set<String> set) {
        for (String item : items) {
            addToSet(item, set);
        }
    }

    public static void addToSet(String item, Set<String> set) {
        if (set.contains(item)) return;
        set.add(item);
    }

    public static ArrayList<String> stringSetToList(Set<String> set) {
        ArrayList<String> list = new ArrayList<String>();
        for (String item : set)
            list.add(item);
        return list;
    }

}
