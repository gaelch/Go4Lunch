package com.cheyrouse.gael.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;
import static com.cheyrouse.gael.go4lunch.utils.Constants.MY_PREFS;
import static com.cheyrouse.gael.go4lunch.utils.Constants.RESTAURANTS;
import static com.cheyrouse.gael.go4lunch.utils.Constants.USER_PREFS;

public class Prefs {

    //This class using SharedPreferences and the Gson library

    private static Prefs instance;
    private static SharedPreferences prefs;


    //Class Prefs constructor
    Prefs(Context context) {
        prefs = context.getSharedPreferences(MY_PREFS, MODE_PRIVATE);
    }
    //Prefs.get is called to create a new instance of Prefs
    public static Prefs get(Context context) {
        if (instance == null)
            instance = new Prefs(context);
        return instance;
    }

    //storeCategories change ArrayList into json strings and save it
    public void storeUserPrefs(User user) {
        //start writing (open the file)
        SharedPreferences.Editor editor = prefs.edit();
        //put the data
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(USER_PREFS, json);
        //close the file
        editor.apply();
    }
    public void storeChoicePrefs(ResultDetail restaurant) {
        //start writing (open the file)
        SharedPreferences.Editor editor = prefs.edit();
        //put the data
        Gson gson = new Gson();
        String json = gson.toJson(restaurant);
        editor.putString(RESTAURANTS, json);
        //close the file
        editor.apply();
    }

    //getCategories recovers json strings and return there in ArrayList
    public User getPrefsUser() {
        Gson gson = new Gson();
        String json = prefs.getString(USER_PREFS, "");
        return gson.fromJson(json, User.class);
    }

    public ResultDetail getChoice(){
        Gson gson = new Gson();
        String json = prefs.getString(RESTAURANTS, "");
        return gson.fromJson(json, ResultDetail.class);
    }

    //store boolean to notification
    public void storeBoolean(Boolean aSwitch){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("switch", aSwitch);
        editor.apply();
    }

    public Boolean getBoolean(){
        return prefs.getBoolean("switch", false);
    }
}
