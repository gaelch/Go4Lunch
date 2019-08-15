package com.cheyrouse.gael.go4lunch.utils;

import android.util.Log;

import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.cheyrouse.gael.go4lunch.utils.Constants.COLLECTION_RESTAURANT_NAME;

public class RestaurantHelper {

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_RESTAURANT_NAME);
    }

    // --- CREATE ---

    public static com.google.android.gms.tasks.Task<Void> createRestaurant(String uid, String restaurantUid, String restaurantName, double lat, double lng) {
        Restaurant restaurantToCreate = new Restaurant(restaurantUid, restaurantName, lat, lng);
        return RestaurantHelper.getRestaurantsCollection().document(uid).set(restaurantToCreate);
    }

    // --- GET --

    public static Task<DocumentSnapshot> getRestaurant(String uid){
        return RestaurantHelper.getRestaurantsCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static com.google.android.gms.tasks.Task<Void> updateRestaurantChoice(String userName, String uid) {
        DocumentReference restaurant = RestaurantHelper.getRestaurantsCollection().document(uid);
        return restaurant.update("users", FieldValue.arrayUnion(userName));
    }

    public static com.google.android.gms.tasks.Task<Void> updateRestaurantRate(String userId, String uid) {
        DocumentReference restaurant = RestaurantHelper.getRestaurantsCollection().document(uid);
        return restaurant.update("rate", FieldValue.arrayUnion(userId));
    }

    public static com.google.android.gms.tasks.Task<Void> deleteRestaurantRate(String userId, String uid) {
        DocumentReference restaurant = RestaurantHelper.getRestaurantsCollection().document(uid);
        return restaurant.update("rate", FieldValue.arrayRemove(userId));
    }

    // --- DELETE ---

    public static Task<Void> deleteUserChoice(String userName, String uid) {
        DocumentReference restaurant = RestaurantHelper.getRestaurantsCollection().document(uid);
        Log.e("arrayRemove username", userName);
        return restaurant.update("users", FieldValue.arrayRemove(userName));
    }
}
