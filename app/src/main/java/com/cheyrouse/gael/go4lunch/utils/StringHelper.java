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

    public static String extractValueFromHtml(String s) {
        String urlStr ="";
        String regex = "\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);

        while(m.find())
        {
            urlStr = m.group();

            if (urlStr.startsWith("(") && urlStr.endsWith(")"))
            {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
        }
        Log.e("url ", urlStr);

        return urlStr;
    }

    public static String convertInString(double latitude, double longitude) {
        return latitude + "," + longitude;
    }


    public static String getCoWorkers(List<String> users, User user, Context context) {
        List<String> userList = new ArrayList<>();
        for(String u : users){
            if(!user.getUsername().equals(u)){
                userList.add(u);
            }
        }
        String match = String.valueOf(userList).replace("[", "").replace("]", "") + " will be there with you";
        if (Objects.requireNonNull(userList).size() != 0) {
            return match;
        } else {
            return context.getResources().getString(R.string.no_coworker);
        }
    }

}
