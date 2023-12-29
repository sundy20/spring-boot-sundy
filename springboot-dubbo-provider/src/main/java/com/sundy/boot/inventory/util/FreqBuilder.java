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

    public JSONObject fetchFreqInfo(Item itemBO, String bizId, String bizKey) {
        JSONObject t = new JSONObject();
        t.put("key", fetchKey(itemBO, bizId, bizKey));
        t.put("amount", itemBO.getAmount());
        t.put("endTime", calcTime(itemBO));
        return t;
    }

    private String fetchKey(Item itemBO, String bizId, String bizKey) {
        StringBuilder sb = new StringBuilder(Constants.FREQ_PREFIX);
        sb.append(itemBO.getType());
        sb.append("_");
        sb.append(itemBO.getUnit());
        sb.append("_");
        sb.append(itemBO.getInterval());
        sb.append("_");
        if ("freq".equals(itemBO.getType())) {
            sb.append(bizId);
            sb.append("_");
            sb.append(bizKey);
        } else {
            sb.append(bizKey);
        }
        return sb.toString();
    }

    private Long calcTime(Item itemBO) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if ("hour".equals(itemBO.getUnit())) {
            cal.add(Calendar.HOUR_OF_DAY, 1);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            if ("day".equals(itemBO.getUnit())) {
                cal.add(Calendar.DAY_OF_YEAR, itemBO.getInterval());
            } else if ("week".equals(itemBO.getUnit())) {
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                cal.add(Calendar.WEEK_OF_YEAR, itemBO.getInterval());
            } else if ("month".equals(itemBO.getUnit())) {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.MONTH, itemBO.getInterval());
            } else if ("action_range".equals(itemBO.getUnit())) {
                cal.setTimeInMillis(itemBO.getEndTime());
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
