package com.sundy.boot.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author plus.wang
 * @description jdk8日期工具类
 * @date 2018/5/8
 */
public class DateUtil {

    public static Date string2Date(String datestr, String format) {

        ZoneId zone = ZoneId.systemDefault();

        LocalDateTime localDateTime = LocalDateTime.parse(datestr, DateTimeFormatter.ofPattern(format));

        Instant instant = localDateTime.atZone(zone).toInstant();

        return Date.from(instant);
    }

    public static Date string2Date(String datestr, DatePattern datePattern) {

        ZoneId zone = ZoneId.systemDefault();

        LocalDateTime localDateTime = LocalDateTime.parse(datestr, DateTimeFormatter.ofPattern(datePattern.getPattern()));

        Instant instant = localDateTime.atZone(zone).toInstant();

        return Date.from(instant);
    }

    public static String date2string(Date date, String format) {

        ZoneId zone = ZoneId.systemDefault();

        Instant instant = date.toInstant();

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);

        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }

    public static String date2string(Date date, DatePattern datePattern) {

        ZoneId zone = ZoneId.systemDefault();

        Instant instant = date.toInstant();

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);

        return localDateTime.format(DateTimeFormatter.ofPattern(datePattern.getPattern()));
    }

    public static Date now() {

        return Date.from(Instant.now());
    }

    public long currentSecond() {

        return Instant.now().getEpochSecond();
    }

    public long currentMilliSecond() {

        return System.currentTimeMillis();
    }

    public long currentNano() {

        return System.nanoTime();
    }

    public static Date localDate2Date(LocalDate localDate) {

        ZoneId zone = ZoneId.systemDefault();

        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();

        return Date.from(instant);
    }

    public static LocalDate date2LocalDate(Date date) {

        Instant instant = date.toInstant();

        ZoneId zone = ZoneId.systemDefault();

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);

        return localDateTime.toLocalDate();
    }

    public static Date localDateTime2Date(LocalDateTime localDateTime) {

        ZoneId zone = ZoneId.systemDefault();

        Instant instant = localDateTime.atZone(zone).toInstant();

        return Date.from(instant);
    }

    public static LocalDateTime date2LocalDateTime(Date date) {

        Instant instant = date.toInstant();

        ZoneId zone = ZoneId.systemDefault();

        return LocalDateTime.ofInstant(instant, zone);
    }

    public static Date localTime2Date(LocalTime localTime) {

        LocalDate localDate = LocalDate.now();

        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

        ZoneId zone = ZoneId.systemDefault();

        Instant instant = localDateTime.atZone(zone).toInstant();

        return Date.from(instant);
    }

    public static LocalTime date2LocalTime(Date date) {

        Instant instant = date.toInstant();

        ZoneId zone = ZoneId.systemDefault();

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);

        return localDateTime.toLocalTime();
    }

    public static String localDate2string(LocalDate localDate, DatePattern datePattern) {

        return localDate.format(DateTimeFormatter.ofPattern(datePattern.getPattern()));
    }
}
