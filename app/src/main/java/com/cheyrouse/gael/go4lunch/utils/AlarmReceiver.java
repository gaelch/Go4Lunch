package com.cheyrouse.gael.go4lunch.utils;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;


public class AlarmReceiver extends BroadcastReceiver {

    static final String CHANEL_ID = "chanel_id";
    static final int NOTIFICATION_ID = 0;
    private Context mContext;

    //Receive notification and execute request to search user articles
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        //request sharedPreferences to reset restaurant
        Prefs prefs = new Prefs(context);
        ResultDetail resultDetail = prefs.getChoice();
        User user = prefs.getPrefsUser();
        user.setChoice("");
        prefs.storeChoicePrefs(null);
        prefs.storeUserPrefs(user);
        mContext = context;
        executeRequestToFireStoreToResetChoice(user, resultDetail);
    }

    private void executeRequestToFireStoreToResetChoice(User user, ResultDetail resultDetail) {
        UserHelper.updateChoice(null, user.getUid());
        RestaurantHelper.deleteUserChoice(user.getUsername(), resultDetail.getName());
    }
}



