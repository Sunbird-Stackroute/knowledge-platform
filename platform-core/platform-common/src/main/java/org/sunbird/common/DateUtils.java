package org.sunbird.common;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public final static String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static String format(Date date) {
        SimpleDateFormat sdf = getDateFormat();
        if (null != date) {
            try {
                return sdf.format(date);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static Date parse(String dateStr) {
        SimpleDateFormat sdf = getDateFormat();
        if (StringUtils.isNotBlank(dateStr)) {
            try {
                return sdf.parse(dateStr);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static Date parseISOFormattedDate(String date) throws ParseException {
        return parseFormattedDate(date, ISO_FORMAT);
    }

    public static Date parseFormattedDate(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.parse(date);
    }

    public static SimpleDateFormat getDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return sdf;
    }
    public static String formatCurrentDate() {
        return format(new Date());
    }


    public static String formatCurrentDate(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }
}
