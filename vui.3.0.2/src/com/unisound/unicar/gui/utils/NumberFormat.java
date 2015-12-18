package com.unisound.unicar.gui.utils;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberFormat {
    private static final int MOBILE_REGEX_GROUP_COUNT = 3;
    private static final int TELEPHONE_REGEX_GROUP_COUNT = 4;

    private static final String LONG_DISTANCE_CALL_PREFIX =
            "(17951|12593|17969|17968|17909|17908|17901|17900|17911|17910|17930|17931)";
    private static final String CHINA_COUNTRY_CODE = "(\\+86|86)";

    private static final String MOBILE_REGEX = LONG_DISTANCE_CALL_PREFIX + "?" + CHINA_COUNTRY_CODE
            + "?" + "(1[3,4,5,8]{1}[0-9]{9})";
    private static final String TELEPHONE_REGEX = LONG_DISTANCE_CALL_PREFIX + "?"
            + CHINA_COUNTRY_CODE + "?" + "(010|02[0-9]{1}|852|853|0[3-9][0-9]{2})?([0-9]{7,8})";

    public static String getCleanPhoneNumber(String phoneNumber) {
        String cleanNumber = "";

        if (phoneNumber != null && !phoneNumber.equals("")) {
            phoneNumber = phoneNumber.replaceAll("[\\s-]", "");

            Pattern mobilePattern = Pattern.compile(MOBILE_REGEX);
            Matcher mobileMatcher = mobilePattern.matcher(phoneNumber);

            if (mobileMatcher.find()) {
                MatchResult mobileMatchResult = mobileMatcher.toMatchResult();

                if (mobileMatchResult.groupCount() == MOBILE_REGEX_GROUP_COUNT) {
                    cleanNumber = mobileMatchResult.group(MOBILE_REGEX_GROUP_COUNT);
                }
            } else {
                Pattern telephonePattern = Pattern.compile(TELEPHONE_REGEX);
                Matcher telephoneMatcher = telephonePattern.matcher(phoneNumber);

                if (telephoneMatcher.find()) {
                    MatchResult telephoneMatchResult = telephoneMatcher.toMatchResult();

                    if (telephoneMatchResult.groupCount() == TELEPHONE_REGEX_GROUP_COUNT) {
                        cleanNumber = telephoneMatchResult.group(TELEPHONE_REGEX_GROUP_COUNT);
                    }
                } else {
                    cleanNumber = phoneNumber;
                }
            }
        }

        return cleanNumber;
    }

    public static String translateNumberToText(String number) {
        int length = number.length();
        StringBuffer numText = new StringBuffer();

        for (int i = 0; i < length; i++) {
            String s = number.substring(i, i + 1);
            String text = numberToChar(s);
            if (text != null) {
                numText.append(text);
            }
        }
        return numText.toString();
    }

    private static String numberToChar(String num) {
        String s = null;

        if ("0".equals(num)) {
            s = "��";
        } else if ("1".equals(num)) {
            s = "��";
        } else if ("2".equals(num)) {
            s = "��";
        } else if ("3".equals(num)) {
            s = "��";
        } else if ("4".equals(num)) {
            s = "��";
        } else if ("5".equals(num)) {
            s = "��";
        } else if ("6".equals(num)) {
            s = "��";
        } else if ("7".equals(num)) {
            s = "��";
        } else if ("8".equals(num)) {
            s = "��";
        } else if ("9".equals(num)) {
            s = "��";
        }
        return s;
    }
    /* end */
}
