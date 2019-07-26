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
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE,5);
        if(calendar.before(now)){
            calendar.add(Calendar.DATE,1);
        }
        return calendar;
    }
}
