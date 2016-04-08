package com.aispeech.aios.adapter.util;

/**
 * Created by Administrator on 2016/2/22.
 */

import android.content.Context;

import com.aispeech.aios.adapter.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 日期时间操作工具类
 *
 */
public final class DateUtils {

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 新建一个日期
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Date newDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month - 1, day);
        return c.getTime();
    }

    /**
     * 新建一个日期
     * @param year 年
     * @param month 月
     * @param day 日
     * @param hour 时
     * @param minute 分
     * @param second 秒
     * @return Date对象
     */
    public static Date newDate(int year, int month, int day, int hour,
                               int minute, int second) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month - 1, day, hour, minute, second);
        return c.getTime();
    }

    /**
     * 指定时间的小时开始
     *
     * @param date Date对象
     * @return ... HH:00:00.000
     */
    public static Date hourBegin(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 指定时间的小时最后一毫秒
     *
     * @param date Date对象
     * @return ... HH:59:59.999
     */
    public static Date hourEnd(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /**
     * 获取指定时间的那天 00:00:00.000 的时间
     *
     * @param date Date对象
     * @return Date对象
     */
    public static Date dayBegin(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取今天 00:00:00.000 的时间
     * @return Date对象
     */
    public static Date dayBegin() {
        return dayBegin(now());
    }

    /**
     * 获取指定时间的那天 23:59:59.999 的时间
     *
     * @param date Date对象
     * @return Date对象
     */
    public static Date dayEnd(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /**
     * 获取今天 23:59:59.999 的时间
     *
     * @return Date对象
     */
    public static Date dayEnd() {
        return dayEnd(now());
    }

    /**
     * 是否是指定日期
     *
     * @param date Date对象
     * @param day Date对象
     * @return
     */
    public static boolean isTheDay(final Date date, final Date day) {
        return date.getTime() >= DateUtils.dayBegin(day).getTime()
                && date.getTime() <= DateUtils.dayEnd(day).getTime();
    }

    /**
     * 是否是今天
     *
     * @param date Date对象
     * @return true：是今天；false：不是今天
     */
    public static boolean isToday(final Date date) {
        return isTheDay(date, DateUtils.now());
    }

    /** yyyy-MM-dd HH:mm:ss */
    public static final int YMDHMS = 1;
    /** yyyy-MM-dd HH:mm */
    public static final int YMDHM = 2;
    /** yyyy-MM-dd HH */
    public static final int YMDH = 3;
    /** yyyy-MM-dd */
    public static final int YMD = 4;
    /** yyyy-MM */
    public static final int YM = 5;
    /** yyyy */
    public static final int Y = 6;

    private static final SimpleDateFormat sdf1 = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm", Locale.ENGLISH);
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat(
            "yyyy-MM-dd HH", Locale.ENGLISH);
    private static final SimpleDateFormat sdf4 = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);
    private static final SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
    private static final SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy", Locale.ENGLISH);

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date Date对象
     * @return 格式化成字段穿的日期
     */
    public static String toString(final Date date) {
        if (date == null)
//                        return "null";
            return "";
        return sdf1.format(date);
    }

    /**
     * 转换到字符串
     *
     * @param date Date对象
     * @param type
     *            DateUtils.YMDHMS : "yyyy-MM-dd HH:mm:ss", DateUtils.YMD :
     *            "yyyy-MM-dd", DateUtils.YM : "yyyy-MM", DateUtils.Y : "yyyy"
     * @return 格式化成字段穿的日期
     */
    public static String toString(final Date date, int type) {
        if (date == null)
            return "null";
        switch (type) {
            case YMDHMS:
                return sdf1.format(date);
            case YMDHM:
                return sdf2.format(date);
            case YMDH:
                return sdf3.format(date);
            case YMD:
                return sdf4.format(date);
            case YM:
                return sdf5.format(date);
            case Y:
                return sdf6.format(date);
        }
        return "unknow";
    }

    /**
     * 转换到字符串
     *
     * @param date Date对象
     * @param fmt 日期格式化字符串
     * @return 格式化成字段穿的日期
     */
    public static String toString(final Date date, String fmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        return sdf.format(date);
    }


    /**
     * 从字符串解析为日期型
     * @param s 日期
     * @param fmt 日期Format串
     * @return Date对象
     */
    public static Date parse(final String s, final String fmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        try {
            return sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从字符串(yyyy-MM-dd HH:mm:ss)解析为日期型
     * @param s 日期
     * @return Date对象
     */
    public static Date parse(final String s) {
        try {
            return sdf1.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把参数一通过参数二转换成日期后返回日期的毫秒数，如果不能转换
     * 成功，就返回0L
     * @param dateStr 日期字符串
     * @param format 日期格式
     * @return the number of milliseconds since Jan. 1, 1970, midnight GMT. Or 0L
     */
    public static long parseLong(final String dateStr, final String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(dateStr);
            return date == null ? 0L : date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 把参数转换成日期后返回日期的毫秒数，如果不能转换成功，就返回0L
     * @param dateStr 日期字符串
     * @return the number of milliseconds since Jan. 1, 1970, midnight GMT. Or 0L
     */
    public static long parseLong(final String dateStr) {
        try {
            Date t = sdf1.parse(dateStr);
            return t == null ? 0L : t.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }


    /**
     * 根据传入的字段串日期类型判断是否是周末
     * @param dateStr 日期字符串
     * @return true：是周末，false：不是周末
     */
    public static boolean isWeenkend(String dateStr) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = df.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                    || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  false;
    }

    public static boolean isBetweenTime(String strDate, String strDateBegin, String strDateEnd) {

        if(!StringUtil.isDecimalNumber(strDate.substring(11,13)) || !StringUtil.isDecimalNumber(strDate.substring(14,16)) ||
                !StringUtil.isDecimalNumber(strDateBegin.substring(0, 2)) || !StringUtil.isDecimalNumber(strDateBegin.substring(3)) ||
                !StringUtil.isDecimalNumber(strDateEnd.substring(0, 2)) || !StringUtil.isDecimalNumber(strDateEnd.substring(3 ))) {
            return false;
        }

        int strDateH = Integer.parseInt(strDate.substring(11,13));
        int strDateM = Integer.parseInt(strDate.substring(14,16));

        int strDateBeginH = Integer.parseInt(strDateBegin.substring(0,2));
        int strDateBeginM = Integer.parseInt(strDateBegin.substring(3));

        int strDateEndH = Integer.parseInt(strDateEnd.substring(0,2));
        int strDateEndM = Integer.parseInt(strDateEnd.substring(3));

        if((strDateH > strDateBeginH && strDateH < strDateEndH)){
            if(strDateM == 59) {
                return false;
            }
            return true;
        } else if(strDateH == strDateBeginH && strDateM >= strDateBeginM) {
            return true;
        } else if(strDateH == strDateEndH && strDateM < strDateEndM) {
            return true;
        }else {
            return false;
        }
    }


    /**
     * 把日期拆分成键值对形式，可通过返回值的get方法获取
     * @param date 日期字符串
     * @return 日期键值对
     */
    public static Map<String, String> getDateArray(String date) {
        if(date != null && date.length() == 19) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("year", date.substring(0,4));
            params.put("month", date.substring(5,7));
            params.put("day", date.substring(8,10));
            params.put("hour", date.substring(11,13));
            params.put("min", date.substring(14, 16));
            params.put("second", date.substring(17, 19));
            return params;
        }
        return null;
    }

    /**
     * 天气查询中，获取某一天是一周中的周几
     * @param context 上下文
     * @param date 要查的日期
     * @param day 与今天相差的天数，若不显示“今天、明天”，可以传入-1
     * @return 周几。若与今天相差为0：今天；1：明天；2：后天；其他：周几
     */
    public static String getWeekDate(Context context, String date, int day) {
        String[] mWeeks = context.getResources().getStringArray(R.array.week);
        String dateString;

        if (day == 0) {
            dateString = "今天";
        } else if (day == 1) {
            dateString = "明天";
        } else if (day == 2) {
            dateString = "后天";
        } else {
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateString = mWeeks[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        }
        return dateString;
    }
}

