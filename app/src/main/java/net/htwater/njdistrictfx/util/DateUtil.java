package net.htwater.njdistrictfx.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhutianhong on 2015/12/31.
 * 日期字符串转换
 */
public class DateUtil {
    public final static String DATE_FORMAT1 = "yyyyMMdd";
    public final static String DATE_FORMAT2 = "yyyy-MM-dd";
    public final static String DATE_FORMAT3 = "yyyy年MM月dd日";
    public final static String DATE_FORMAT4 = "MM月dd日";
    public final static String DATE_TIME = "HH:mm:ss";
    public final static String DATETIME_FORMAT1 = "yyyyMMdd hh:mm:ss";
    public final static String DATETIME_FORMAT2 = "yyyy-MM-dd hh:mm:ss";
    public final static String DATETIME_FORMAT3 = "yyyy年MM月dd日 hh:mm:ss";
    public final static String DATETIME_FORMAT4 = "yyyy-MM-dd HH:mm";
    public final static String DATETIME_SHORT_FORMAT1 = "yyyyMMdd hh:mm";
    public final static String DATETIME_SHORT_FORMAT2 = "yyyy-MM-dd hh:mm";
    public final static String DATETIME_SHORT_FORMAT3 = "yyyy年MM月dd日 hh:mm";
    public final static String DATETIME_SHORT_FORMAT4 = "yyyyMMddHHmmss";

    /**
     *  yyyy-MM-dd hh:mm:ss 转  HH:mm:ss
     * @param strDate
     * @return
     */
    public static String parseStringTime(String strDate){
        DateFormat df = new SimpleDateFormat(DATE_TIME);
        Date date = parseString2Date(strDate,DATETIME_FORMAT2);
        return df.format(date);
    }

    /**
     * 将日期字符串转日期
     *
     * @param date           日期字符串
     * @param formatepattern 日期格式
     * @return Date 日期
     */
    public static Date parseString2Date(String date, String formatepattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatepattern);
        try {
            return sdf.parse(date);
        } catch (Exception e) {
            return new Date();
        }
    }

    /**
     * 将日期转日期字符串
     *
     * @param date           日期
     * @param formatepattern 日期格式
     * @return String 日期字符串
     */
    public static String parseDate2String(Date date, String formatepattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatepattern);
        return sdf.format(date);
    }

    /**
     * 将long转日期
     */
    public static Date parseLong2Date(long millis) {
        try {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(millis);
            return date.getTime();
        } catch (Exception e) {
            return new Date();
        }
    }

    /**
     * 获取日期星期几
     *
     * @param date 日期
     * @return String 星期几
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDaysName = {"日", "一", "二", "三", "四", "五", "六"};
        //String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return "星期" + weekDaysName[intWeek];
    }

    /**
     * 获取离指定日期的时间间隔(D[M|Y]+[-]1：D天，M月,Y日)
     */
    public static Date getDiffDate(Date date, String spaceType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String type = "";
        spaceType = spaceType.toUpperCase();
        spaceType = spaceType.replace("+", "");
        if (spaceType.startsWith("D")) {
            calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(spaceType.substring(1)));
        } else if (spaceType.startsWith("M")) {
            calendar.add(Calendar.MONTH, Integer.parseInt(spaceType.substring(1)));
        } else if (spaceType.startsWith("Y")) {
            calendar.add(Calendar.YEAR, Integer.parseInt(spaceType.substring(1)));
        }
        return calendar.getTime();
    }

    /**
     * 获取指定日期的年份
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取指定日期的月份
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    /**
     * 获取当前时间，格式HH%3Amm
     *
     * @return
     */
    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm");
        String time = dateFormat.format(new Date());
        String[] array = time.split("-");
        return array[0] + "%3A" + array[1];
    }
}
