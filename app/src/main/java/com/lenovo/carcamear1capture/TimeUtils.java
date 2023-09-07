package com.lenovo.carcamear1capture;

import java.util.Calendar;

public class TimeUtils {
    public static String getTimeFromTimestamp() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，所以要加1
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            int MILLISECOND = calendar.get(Calendar.MILLISECOND);
            return String.format("%04d-%02d-%02d %02d:%02d:%02d:%02d", year, month, day, hour, minute, second,MILLISECOND);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}