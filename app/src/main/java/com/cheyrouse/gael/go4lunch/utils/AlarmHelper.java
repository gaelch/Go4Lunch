package com.cheyrouse.gael.go4lunch.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmHelper {

    //Configuration of Alarm Helper too reset restaurant choice
   public void configureAlarmToResetChoice(Context context) {
        AlarmManager alarmManager;
        PendingIntent pendingIntent;
        //getCalendarPresets
        Calendar calendar = DateUtils.getCalendarPresetsToResetChoice();
        //call AlarmReceiver class
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent;
        intent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}
