package com.xiang.Util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 祥祥 on 2016/3/21.
 */
public class SportTimeUtil {

    private static final String TAG = "SportTimeUtil";
    /**
     * 一秒的毫秒数
     */
    public static final long SECOND = 1000;
    /**
     * 一分钟的毫秒数
     */
    public static final long MINITE = 60 * 1000;
    /**
     * 一小时的毫秒数
     */
    public static final long HOUR = 60 * 60 * 1000;
    /**
     * 半小时
     */
    public static final long HALF_HOUR = HOUR / 2;
    /**
     * 一天的毫秒数
     */
    public static final long DAY = 24 * HOUR;
    /**
     * 一周的毫秒数
     */
    public static final long WEEK = 7 * DAY;
    /**
     * 两周的毫秒数
     */
    public static final long TWO_WEEK = 2 * WEEK;
    /**
     * 10天
     */
    public static final long TEN_DAYS = 10 * DAY;
    /**
     * 一个月的毫秒数
     */
    public static final long MONTH = 30 * DAY;
    /**
     * 半个月的毫秒数
     */
    public static final long HALF_MONTH = MONTH / 2;

    public static final long YEAR = 12 * MONTH;

    /**
     * 时间格式
     */
    private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat FORMAT_DATA = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat TIME_DATABASE_NAMEDateFormat = new SimpleDateFormat("yyyyMMdd");

    public static String format(Date date){
        return TIME_FORMAT.format(date);
    }

    public static String formatTime(Long a){
        return TIME_FORMAT.format(a);
    }

    public static String formatTime(Date a) {
        return TIME_FORMAT.format(a);
    }

    /**
     * 几分钟前 等
     * @return
     */
    public static String getDateFromNow(Date date){
        Date nowDate = new Date();
        return getDateDescription(date.getTime(), nowDate.getTime());
    }

    /**
     * 几分钟前 等
     * @return
     */
    public static String getDateFromNow(long timestamp){
        Date nowDate = new Date();
        return getDateDescription(timestamp, nowDate.getTime());
    }

    /**
     * 返回字符串 yyyyMMddHHmmss
     * @return
     */
    public static String getCurrentFileName(){
        Date nowDate = new Date();
        return new SimpleDateFormat("yyyyMMddHHmmss").format(nowDate);
    }

    /**
     *
     * @param dateString "1971-11-10 10:20:10"
     * @return
     */
    public static String getDateFromNow(String dateString){
        Date nowDate = new Date();
        try {
            int year = (Integer.parseInt(dateString.substring(0, 4)) - 1970);
            int month = Integer.parseInt(dateString.substring(5, 7));
            int day = Integer.parseInt(dateString.substring(8, 10));
            int hour = Integer.parseInt(dateString.substring(11, 13));
            int min = Integer.parseInt(dateString.substring(14, 16));
            int second = Integer.parseInt(dateString.substring(17, 19));
            Log.d(TAG, "时间：" + dateString);
            Log.d(TAG, "解析后 " + year + month + day + hour + min + second);

            Date d = TIME_FORMAT.parse(dateString);

            Log.d(TAG, d.toString());

            return getDateDescription(d.getTime(), nowDate.getTime());
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "时间：" + dateString);
            return "";
        }
    }

    private static String getDateDescription(long date, long now){

        long l_date = date;
        long l_now = now;

        long max = l_date > l_now ? l_date : l_now;
        long small = l_date > l_now ? l_now : l_date;

        String des = l_date > l_now ? "后" : "前";

        long minus = (max - small);

        if (minus > YEAR) {
            return FORMAT_DATA.format(date);
        } else if (minus > MONTH) {
            return (minus / MONTH) + "个月" + des;
        } else if (minus > DAY) {
            return (minus / DAY) + "天" + des;
        } else if (minus > HOUR) {
            return (minus / HOUR) + "小时" + des;
        } else if (minus > MINITE) {
            return (minus / MINITE) + "分钟" + des;
        } else if (minus > 10 * SECOND) {
            return minus / SECOND + "秒" + des;
        } else {
            return "刚刚";
        }

    }
}
