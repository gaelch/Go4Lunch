package com.cheyrouse.gael.go4lunch.utils;

import java.util.Calendar;
import java.util.Locale;

public class DateUtils {

    // get calendar to Alarm Helper reset choice
    public static Calendar getCalendarPresetsToResetChoice(){
        //in a current date at 1 pm, this property get an instance to calendar
        Calendar now = Calendar.getInstance(Locale.FRANCE);
        Calendar calendar = Calendar.getInstance(Locale.FRANCE);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE,30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if(calendar.before(now)){
            calendar.add(Calendar.DATE,1);
        }
        return calendar;
    }
}
