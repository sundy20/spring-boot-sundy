package com.sundy.boot.inventory.util;

import com.alibaba.fastjson.JSONObject;
import com.sundy.boot.inventory.domain.Item;

import java.util.Calendar;


public class FreqBuilder {
    Calendar calendar;

    public FreqBuilder(Long cur) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);//设置星期一为一周开始的第一天
        calendar.setMinimalDaysInFirstWeek(4);//可以不用设置
        calendar.setTimeInMillis(cur);//获得当前的时间戳
        this.calendar = calendar;
    }

    public JSONObject fetchFreqInfo(Item item, String bizId, String bizKey) {
        JSONObject t = new JSONObject();
        t.put("key", fetchKey(item, bizId, bizKey));
        t.put("amount", item.getAmount());
        t.put("endTime", calcTime(item));
        return t;
    }

    private String fetchKey(Item item, String bizId, String bizKey) {
        StringBuilder sb = new StringBuilder(Constants.FREQ_PREFIX);
        sb.append(item.getType());
        sb.append("_");
        sb.append(item.getUnit());
        sb.append("_");
        sb.append(item.getInterval());
        sb.append("_");
        if ("freq".equals(item.getType())) {
            sb.append(bizId);
            sb.append("_");
            sb.append(bizKey);
        } else {
            sb.append(bizKey);
        }
        return sb.toString();
    }

    private Long calcTime(Item item) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if ("hour".equals(item.getUnit())) {
            cal.add(Calendar.HOUR_OF_DAY, 1);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            if ("day".equals(item.getUnit())) {
                cal.add(Calendar.DAY_OF_YEAR, item.getInterval());
            } else if ("week".equals(item.getUnit())) {
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                cal.add(Calendar.WEEK_OF_YEAR, item.getInterval());
            } else if ("month".equals(item.getUnit())) {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.MONTH, item.getInterval());
            } else if ("action_range".equals(item.getUnit())) {
                cal.setTimeInMillis(item.getEndTime());
            }
        }
        return cal.getTimeInMillis();
    }

    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        System.out.println(cal.get(Calendar.HOUR_OF_DAY));
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(2021, 11, 26);
        System.out.println(cal.get(Calendar.WEEK_OF_YEAR));
        System.out.println(cal.get(Calendar.YEAR));
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        System.out.println(cal.get(Calendar.WEEK_OF_YEAR));
        System.out.println(cal.get(Calendar.YEAR));

        cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        System.out.println(cal.get(Calendar.HOUR_OF_DAY));
        System.out.println(cal.get(Calendar.DAY_OF_YEAR));
        cal.add(Calendar.HOUR_OF_DAY, 1);
        System.out.println(cal.get(Calendar.HOUR_OF_DAY));
        System.out.println(cal.get(Calendar.DAY_OF_YEAR));
    }
}
