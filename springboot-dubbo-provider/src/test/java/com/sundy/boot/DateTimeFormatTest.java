package com.sundy.boot;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * @author plus.wang
 * @description
 * @date 2018/5/8
 */
public class DateTimeFormatTest {

    public static void main(String[] args) {
        int a=0;
        long b=1L;
        a= (int) b;
        System.out.println(a);
        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
        String formattedDate = formatter.format(LocalDate.now());
        String formattedZonedDate = formatter.format(ZonedDateTime.now());

        System.out.println("LocalDate          : " + formattedDate);
        System.out.println("formattedZonedDate : " + formattedZonedDate);

        LocalDateTime dateTime = LocalDateTime.now();
        String strDate1 = dateTime.format(DateTimeFormatter.BASIC_ISO_DATE);    // 20180303
        String strDate2 = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);    // 2013-03-03
        String strDate3 = dateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);    // 当前时间
        String strDate4 = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));   // 2018-03-03
        // 今天是：2018年 三月 03日 星期六
        String strDate5 = dateTime.format(DateTimeFormatter.ofPattern("今天是：YYYY年 MMMM dd日 E", Locale.CHINESE));

        System.out.println(strDate1);
        System.out.println(strDate2);
        System.out.println(strDate3);
        System.out.println(strDate4);
        System.out.println(strDate5);

        // 将一个字符串解析成一个日期对象
        String strDate6 = "2018-03-03";
        String strDate7 = "2017-03-03 15:30:05";

        LocalDate date = LocalDate.parse(strDate6, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime dateTime1 = LocalDateTime.parse(strDate7, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println(date);
        System.out.println(dateTime1);
        System.out.println(new Date());


        Instant instant = Instant.now();

        System.out.println("当前秒：" + instant.getEpochSecond());
        System.out.println("当前毫秒：" + instant.toEpochMilli());
        System.out.println("当前纳秒：" + instant.getNano());

        System.out.println("------------------------------------------");

        System.out.println("当前毫秒：" + System.currentTimeMillis());
        System.out.println("当前纳秒：" + System.nanoTime());

        System.out.println("------------------------------------------");

        StackTraceElement[] stackTraceElements = new RuntimeException().getStackTrace();

        for (StackTraceElement stackTraceElement : stackTraceElements) {

            System.out.println(stackTraceElement.getMethodName());

            System.out.println(stackTraceElement.getClassName());

        }
    }
}
