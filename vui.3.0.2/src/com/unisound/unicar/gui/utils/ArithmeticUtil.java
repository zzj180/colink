package com.unisound.unicar.gui.utils;

import java.util.Random;

/**
 * 
 * @author xiaodong
 * 
 */
public class ArithmeticUtil {

    /**
     * 
     * @param min : for array index is 0
     * @param max : for array index is array.lenth -1
     * @param n , 0 < n =< array.lenth -1
     * @return
     */
    public static int[] getRandomArray(int min, int max, int n) {
        int len = max - min + 1;

        if (max < min || n > len) {
            return null;
        }

        // 初始化给定范围的待选数组
        int[] source = new int[len];
        for (int i = min; i < min + len; i++) {
            source[i - min] = i;
        }

        int[] result = new int[n];
        Random rd = new Random();
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            // 待选数组0到(len-2)随机一个下标
            index = Math.abs(rd.nextInt() % len--);
            // 将随机到的数放入结果集
            result[i] = source[index];
            // 将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换
            source[index] = source[len];
        }
        return result;
    }


    /**
     * XD added 20150721 getMaxNumber
     * 
     * @param a
     * @param b
     * @param c
     * @return MaxNumber
     */
    public static int getMaxNumber(int a, int b, int c) {
        int max = a;
        if (b > max) max = b;
        if (c > max) max = c;
        return max;
    }
}
