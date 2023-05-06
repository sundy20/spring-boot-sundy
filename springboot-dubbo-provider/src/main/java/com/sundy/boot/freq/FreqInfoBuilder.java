package com.sundy.boot.freq;

import com.alibaba.fastjson.JSONObject;
import com.sundy.boot.freq.BO.ItemBO;

import java.util.Calendar;


public class FreqInfoBuilder {
    private static final String freqPrefix = "sundy_freq_";
    Calendar calendar;

    public FreqInfoBuilder(Long cur) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);//设置星期一为一周开始的第一天
        calendar.setMinimalDaysInFirstWeek(4);//可以不用设置
        calendar.setTimeInMillis(cur);//获得当前的时间戳
        this.calendar = calendar;
    }

    private Long calcTime(ItemBO itemBO) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if ("hour".equals(itemBO.getUnit())) {
//            int hour = this.calendar.get(Calendar.HOUR_OF_DAY);
//            cal.set(Calendar.HOUR_OF_DAY, hour + (itemBO.getInterval() - hour%itemBO.getInterval()));
            cal.add(Calendar.HOUR_OF_DAY, 1);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            if ("day".equals(itemBO.getUnit())) {
//                int d = calendar.get(Calendar.DAY_OF_YEAR);
//                cal.set(Calendar.DAY_OF_YEAR, d + (itemBO.getInterval() - d%itemBO.getInterval()));
                cal.add(Calendar.DAY_OF_YEAR, itemBO.getInterval());
            } else if ("week".equals(itemBO.getUnit())) {
//                int d = calendar.get(Calendar.WEEK_OF_YEAR);
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
//                cal.set(Calendar.WEEK_OF_YEAR, d + (itemBO.getInterval() - d%itemBO.getInterval()));
                cal.add(Calendar.WEEK_OF_YEAR, itemBO.getInterval());
            } else if ("month".equals(itemBO.getUnit())) {
//                int d = calendar.get(Calendar.MONTH);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.MONTH, itemBO.getInterval());
//                d +=  (itemBO.getInterval() - d%itemBO.getInterval());
//                if(d >= Calendar.UNDECIMBER){
//                    cal.add(Calendar.YEAR, 1);
//                    cal.set(Calendar.MONTH, d%Calendar.UNDECIMBER);
//                }else{
//                    cal.set(Calendar.MONTH, d);
//                }
            } else if ("action_range".equals(itemBO.getUnit())) {
                cal.setTimeInMillis(itemBO.getEndTime());
            }
        }
        return cal.getTimeInMillis();
    }


    private String fetchKey(ItemBO itemBO, String bizId, String bizKey) {
        StringBuilder sb = new StringBuilder(freqPrefix);
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
//            sb.append(calcTime(itemBO));
//            sb.append("_");
            sb.append(bizKey);
        }

        return sb.toString();
    }

    public JSONObject fetchFreqInfo(ItemBO itemBO, String bizId, String bizKey) {
        JSONObject t = new JSONObject();
        t.put("key", fetchKey(itemBO, bizId, bizKey));
        t.put("amount", itemBO.getAmount());
        t.put("endTime", calcTime(itemBO));
        return t;
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

        System.out.println();

        cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        System.out.println(cal.get(Calendar.HOUR_OF_DAY));
        System.out.println(cal.get(Calendar.DAY_OF_YEAR));
        cal.add(Calendar.HOUR_OF_DAY, 1);
        System.out.println(cal.get(Calendar.HOUR_OF_DAY));
        System.out.println(cal.get(Calendar.DAY_OF_YEAR));
    }
}
