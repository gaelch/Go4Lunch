package com.cheyrouse.gael.go4lunch.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String TEXT_DATE = "dd-MM-yyyy";
    public static final String API_DATE = "yyyyMMdd";

    // To format date
    public static String getDate(int year, int dayOfMonth, int monthOfYear, String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, monthOfYear, dayOfMonth);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(date);
    }
    public static int getYear() {
        final Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    public static int getMonth() {
        final Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH);
    }

    public static int getDay() {
        final Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH);
    }

    // get calendar to Alarm Helper
    public static Calendar getCalendarPresets(){
        //in a current date at 1 pm, this property get an instance to calendar
        Calendar now = Calendar.getInstance(Locale.FRANCE);
        Calendar calendar = Calendar.getInstance(Locale.FRANCE);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE,25);
        if(calendar.before(now)){
            calendar.add(Calendar.DATE,1);
        }
        return calendar;
    }
}
