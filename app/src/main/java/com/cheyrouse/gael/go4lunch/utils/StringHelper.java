package com.cheyrouse.gael.go4lunch.utils;

import android.content.Context;
import android.util.Log;

import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.Location;
import com.cheyrouse.gael.go4lunch.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {

    // To convert double in string
    public static String convertInString(double latitude, double longitude) {
        return latitude + "," + longitude;
    }

    // Get joining CoWorkers
    public static String getCoWorkers(List<String> users, User user, Context context) {
        List<String> userList = new ArrayList<>();
        for(String u : users){
            if(!user.getUsername().equals(u)){
                userList.add(u);
            }
        }
        String match = "";
        if(context != null){
            match = String.valueOf(userList).replace("[", "").replace("]", "")
                    + context.getResources().getString(R.string.with_you);
        }
        if (Objects.requireNonNull(userList).size() != 0) {
            return match;
        } else {
            assert context != null;
            return context.getResources().getString(R.string.no_coworker);
        }
    }

    // Get number of CoWorkers
    public static String getNumberOfCoworkers(List<User> usersAreJoining){
        if(usersAreJoining.size() != 0){
            return "(" + usersAreJoining.size() + ")";
        }else {
            return "(0)";
        }

    }

}
