package com.cheyrouse.gael.go4lunch.utils;


import com.cheyrouse.gael.go4lunch.models.ResultDetail;

import java.util.Calendar;
import java.util.Locale;

// Define date for reset lunch choice
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

    // convert string to hours
    public static String convertStringToHours(String hour){
        String hour1 = hour.substring(0, 2);
        String hour2 = hour.substring(2, 4);
        return hour1 + ":" + hour2;
    }

    public static int getOpenHours(ResultDetail result) {
        int i = 0;
        if (result.getOpeningHours() != null && result.getOpeningHours().getOpenNow()) {
            if(result.getOpeningHours().getPeriods() != null && result.getOpeningHours().getPeriods().get(0).getClose() != null){
                i = 1;
            }else{
                i = 2;
            }
        } else {
            if (result.getOpeningHours() == null) {
                i = 2;
            } else {
                if (!result.getOpeningHours().getOpenNow()) {
                    i = 3;
                }
            }
        }
        return i;
    }
}
