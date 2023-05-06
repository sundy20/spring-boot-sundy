package com.sundy.boot.freq;

import com.sundy.boot.freq.BO.ItemBO;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class FreqKeyBuilder {
    private static final String freqPrefix = "sundy_freq_";
    private static SimpleDateFormat dfForHour = new SimpleDateFormat("yyyy-MM-dd-");
    private static SimpleDateFormat dfOther = new SimpleDateFormat("yyyy-");
    Calendar calendar;

    public FreqKeyBuilder(Long cur) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);//设置星期一为一周开始的第一天
        calendar.setMinimalDaysInFirstWeek(4);//可以不用设置
        calendar.setTimeInMillis(cur);//获得当前的时间戳
        this.calendar = calendar;
    }

    private void appendSuffix(StringBuilder sb, String type, String bizId, String bizKey) {
        if ("freq".equals(type)) {
            sb.append(bizId);
            sb.append("_");
            sb.append(bizKey);

        } else if ("stock".equals(type)) {
            sb.append("stock_");
            sb.append(bizKey);
        }
    }

    public String fetchKey(ItemBO itemBO, String bizId, String bizKey) {
        StringBuilder sb = new StringBuilder(freqPrefix);
        if ("hour".equals(itemBO.getUnit())) {
            sb.append(dfForHour.format(this.calendar.getTime()));
            sb.append("hour_");
            sb.append(calendar.get(Calendar.HOUR_OF_DAY));
            sb.append("_");
        } else {
            sb.append(dfOther.format(this.calendar.getTime()));
            if ("day".equals(itemBO.getUnit())) {
                sb.append("day_");
                sb.append(calendar.get(Calendar.DAY_OF_YEAR) / itemBO.getInterval());
                sb.append("_");
            } else if ("week".equals(itemBO.getUnit())) {
                sb.append("week_");
                sb.append(calendar.get(Calendar.WEEK_OF_YEAR) / itemBO.getInterval());
                sb.append("_");
            } else if ("month".equals(itemBO.getUnit())) {
                sb.append("month_");
                sb.append(calendar.get(Calendar.MONTH) / itemBO.getInterval());
                sb.append("_");
            } else if ("action_range".equals(itemBO.getUnit())) {
                sb.append("action_range_");
            }
        }
        appendSuffix(sb, itemBO.getType(), bizId, bizKey);
        return sb.toString();
    }
}
