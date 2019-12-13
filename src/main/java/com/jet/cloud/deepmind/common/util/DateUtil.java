package com.jet.cloud.deepmind.common.util;

import com.jet.cloud.deepmind.rtdb.model.TimeUnit;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author yhy
 * @create 2019-10-24 11:08
 */
public class DateUtil {
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 时间戳转换为localtime
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime longToLocalTime(long timestamp) {
        if (timestamp <= 0) {
            return null;
        }
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * LocalDateTime转成时间戳
     *
     * @param time
     * @return
     */
    public static Long localDateTimeToLong(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 日期字符串转成LocalDateTime
     *
     * @param time
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String time) {
        return LocalDateTime.parse(time, dateTimeFormatter);
    }

    /**
     * 日期字符串转成时间戳
     *
     * @param time
     * @return
     */
    public static Long stringToLong(String time) {
        LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);
        return localDateTimeToLong(localDateTime);
    }

    /**
     * 获取输入日期的0点时刻日期
     */
    public static Date getDate(Date date) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        cd.set(Calendar.HOUR_OF_DAY, 0);
        cd.set(Calendar.MINUTE, 0);
        cd.set(Calendar.SECOND, 0);
        cd.set(Calendar.MILLISECOND, 0);
        Date d = cd.getTime();
        return d;
    }

    //明天0点
    public static Date initDateByDay24() {
        return DateUtil.localTodate(LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

    //获取当前月第一天：
    public static Date firstDayMonth() {
        //获取当前月第一天：
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    //上月一号0点
    public static Date initDateByUpperMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) - 1, 1, 0, 0, 0);
        return calendar.getTime();
    }

    //上月当前时间
    public static Date initDateByYesMonthNowTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    /**
     *  获取指定日期下个月的第一天
     *
     * @return
     */
    public static Date getFirstDayOfNextMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取当年的第一天
     *
     * @param
     * @return
     */
    public static Date getCurrYearFirst() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearFirst(currentYear);
    }

    /**
     * 获取某年第一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getYearFirst(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }

    /**
     * 获取明年的第一天
     *
     * @param
     * @return
     */
    public static Date getNextYearFirst() {
        Date date = getCurrYearFirst();
        LocalDateTime dateTime = DateUtil.dateToLocalDateTime(date).plusYears(1);
        return DateUtil.LocalDateTime2Date(dateTime);
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        return zonedDateTime.toLocalDateTime();
    }

    /**
     * @param localDateTime
     * @return LocalDateTime转换成Date类型
     */
    public static Date LocalDateTime2Date(LocalDateTime localDateTime) {
        ZonedDateTime zoneDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Instant instant = zoneDateTime.toInstant();
        Date date = new Date();
        date.setTime(instant.toEpochMilli());
        return date;
    }

    public static LocalDate getDateOfTimestamp(long timestamp) {
        if (timestamp <= 0) {
            return null;
        }
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone).toLocalDate();
    }

    public static Date stringToDate2(String str) {
        if (str == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    public static Date stringToDate(String str) {
        if (str == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    public static Date toDate(String str) {
        if (str == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    /**
     * @param date
     * @return 根据默认时区返回date对象
     */
    public static LocalDate dateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        return zonedDateTime.toLocalDate();
    }

    public static String localDateToString(LocalDate date) {
        return dateToString(localDateToDate(date));
    }

    public static String localDateTimeToString(LocalDateTime dateTime) {
        return dateTimeFormatter.format(dateTime);
    }

    public static String longToString(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return localDateTimeToString(longToLocalTime(timestamp));
    }


    public static String dateToString(Date date) {
        SimpleDateFormat st = new SimpleDateFormat("yyyy-MM-dd");
        return st.format(date);
    }

    public static Date localDateToDate(LocalDate beginDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = beginDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    //当天0点
    public static Date initDateByDay() {
        return DateUtil.localTodate(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

    /**
     * 计算两个日期之间的日期
     *
     * @param begin
     * @param end
     * @return
     */
    public static List<Long> calcDate(@NotNull LocalDate begin, @NotNull LocalDate end) {
        List<Long> list = new ArrayList<>();
        final Long day = 24 * 60 * 60 * 1000L;
        LocalDateTime start = LocalDateTime.of(begin.getYear(), begin.getMonth(), begin.getDayOfMonth(), 0, 0, 0);
        LocalDateTime over = LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 0, 0, 0);
        long startDate = DateUtil.localTodate(start).getTime();
        long overDate = DateUtil.localTodate(over).getTime();
        if (startDate > overDate) {
            Long temp = startDate;
            startDate = overDate;
            overDate = temp;
        }
        while (startDate <= overDate) {
            list.add(startDate);
            startDate += day;
        }
        return list;
    }

    public static List<LocalDateTime> calcDate(@NotNull Date begin, @NotNull Date end) {
        LocalDate b = DateUtil.dateToLocalDate(begin);
        LocalDate e = DateUtil.dateToLocalDate(end);
        List<LocalDateTime> list = new ArrayList<>();
        final Long day = 24 * 60 * 60 * 1000L;
        LocalDateTime over = LocalDateTime.of(e.getYear(), e.getMonth(), e.getDayOfMonth(), 0, 0, 0);
        LocalDateTime start = LocalDateTime.of(b.getYear(), b.getMonth(), b.getDayOfMonth(), 0, 0, 0);
        long overDate = DateUtil.localTodate(over).getTime();
        long startDate = DateUtil.localTodate(start).getTime();
        if (startDate > overDate) {
            Long temp = startDate;
            startDate = overDate;
            overDate = temp;
        }
        while (startDate <= overDate) {
            list.add(DateUtil.timestampToLocalDateTime(startDate));
            startDate += day;
        }
        return list.subList(0, list.size() - 1);
    }

    public static Date localTodate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static LocalDateTime timestampToLocalDateTime(@NotNull long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, zone);
        return dateTime;
    }

    public static LocalDate timestampToLocalDate(@NotNull long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, zone);
        String dateStr = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return LocalDate.parse(dateStr);
    }

    /**
     * 计算两个日期之间的日期
     *
     * @param begin
     * @param end
     * @return
     */
    public static List<Long> calcDate(@NotNull LocalDate begin, @NotNull LocalDate end, TimeUnit unit) {
        List<Long> list = new ArrayList<>();
        LocalDateTime start = LocalDateTime.of(begin.getYear(), begin.getMonth(), begin.getDayOfMonth(), 0, 0, 0);
        LocalDateTime over = LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 0, 0, 0);
        long startDate = DateUtil.localTodate(start).getTime();
        long overDate = DateUtil.localTodate(over).getTime();
        while (startDate < overDate) {
            list.add(startDate);
            if (unit.equals(TimeUnit.MONTHS)) {
                startDate = DateUtil.localDateTimeToLong(DateUtil.longToLocalTime(startDate).plusMonths(1));
            } else if (unit.equals(TimeUnit.DAYS)) {
                startDate = DateUtil.localDateTimeToLong(DateUtil.longToLocalTime(startDate).plusDays(1));
            } else {
                startDate = DateUtil.localDateTimeToLong(DateUtil.longToLocalTime(startDate).plusHours(1));
            }
        }
        return list;
    }

    /**
     * 计算两个日期之间的日期
     *
     * @param begin
     * @param end
     * @return
     */
    public static List<Long> calcDate(@NotNull LocalDateTime begin, @NotNull LocalDateTime end, TimeUnit unit) {
        List<Long> list = new ArrayList<>();
        long startDate = DateUtil.localTodate(begin).getTime();
        long overDate = DateUtil.localTodate(end).getTime();
        while (startDate < overDate) {
            list.add(startDate);
            if (unit.equals(TimeUnit.MONTHS)) {
                startDate = DateUtil.localDateTimeToLong(DateUtil.longToLocalTime(startDate).plusMonths(1));
            } else if (unit.equals(TimeUnit.DAYS)) {
                startDate = DateUtil.localDateTimeToLong(DateUtil.longToLocalTime(startDate).plusDays(1));
            } else {
                startDate = DateUtil.localDateTimeToLong(DateUtil.longToLocalTime(startDate).plusHours(1));
            }
        }
        return list;
    }


    public static void main(String[] args) {
        System.out.println(initDateByDay24());
    }
}
