package com.cheyrouse.gael.go4lunch.utils;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.facebook.login.widget.ProfilePictureView.TAG;


public class AlarmReceiver extends BroadcastReceiver {

    //Receive Alarm and execute request to reset choice
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        //request sharedPreferences to reset restaurant
        Prefs prefs = new Prefs(context);
        User user = prefs.getPrefsUser();
        if(user != null){
            user.setChoice(null);
        }
        prefs.storeChoicePrefs(null);
        prefs.storeUserPrefs(user);
        executeRequestToFireStoreToResetChoice();
    }

    // Get users in database to delete choice in restaurant table
    private void executeRequestToFireStoreToResetChoice() {
        getUsersAndRestaurantsFromDataBase();
    }

    @SuppressWarnings("unchecked")
    private void getUsersAndRestaurantsFromDataBase() {
        RestaurantHelper.getRestaurantsCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        List<String> users = (List<String>) Objects.requireNonNull(doc.getData()).get("users");
                        String uid = Objects.requireNonNull(doc.getData()).get("restaurantName").toString();
                        for (String s : users){
                            Log.e("users restaurant list", s);
                            RestaurantHelper.deleteUserChoice(s, uid);
                        }
                    }
                }

            }
        });
        // Reset choice in users table
        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        String uid = Objects.requireNonNull(doc.getData()).get("uid").toString();
                        UserHelper.updateChoice(null, uid);
                    }
                }
            }
        });
    }
}



