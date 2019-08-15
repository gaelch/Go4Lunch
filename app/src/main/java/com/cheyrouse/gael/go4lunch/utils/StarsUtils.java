package com.cheyrouse.gael.go4lunch.utils;

import android.util.Log;

import com.cheyrouse.gael.go4lunch.models.User;

import java.util.List;

public class StarsUtils {

    public static int getRate(double rate, List<User> users){
        int newRate = 0;
        double i = rate/users.size()*100;
        if(i >= 25 && i < 50){
            newRate = 1;
        }
        if(i >= 50 && i <75){
            newRate = 2;
        }
        if(i >= 75){
            newRate = 3;
        }
        Log.e("resultOfRateStars", String.valueOf(i));
        return newRate;
    }
}
