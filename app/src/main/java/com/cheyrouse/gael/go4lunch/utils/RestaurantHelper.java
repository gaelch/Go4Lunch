package com.cheyrouse.gael.go4lunch.utils;

import android.util.Log;

import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.cheyrouse.gael.go4lunch.utils.Constants.COLLECTION_RESTAURANT_NAME;

public class RestaurantHelper {

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_RESTAURANT_NAME);
    }

    // --- CREATE ---

    public static void createRestaurant(String uid, String restaurantUid, String restaurantName, double lat, double lng) {
        Restaurant restaurantToCreate = new Restaurant(restaurantUid, restaurantName, lat, lng);
        RestaurantHelper.getRestaurantsCollection().document(uid).set(restaurantToCreate);
    }

    // --- GET --

    public static Task<DocumentSnapshot> getRestaurant(String uid){
        return RestaurantHelper.getRestaurantsCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static void updateRestaurantChoice(String userName, String uid) {
        DocumentReference restaurant = RestaurantHelper.getRestaurantsCollection().document(uid);
        restaurant.update("users", FieldValue.arrayUnion(userName));
    }

    public static void updateRestaurantRate(String userId, String uid) {
        DocumentReference restaurant = RestaurantHelper.getRestaurantsCollection().document(uid);
        restaurant.update("rate", FieldValue.arrayUnion(userId));
    }

    // --- DELETE ---

    public static void deleteUserChoice(String userName, String uid) {
        DocumentReference restaurant = RestaurantHelper.getRestaurantsCollection().document(uid);
        Log.e("arrayRemove username", userName);
        restaurant.update("users", FieldValue.arrayRemove(userName));
    }

    public static void deleteRestaurantRate(String userId, String uid) {
        DocumentReference restaurant = RestaurantHelper.getRestaurantsCollection().document(uid);
        restaurant.update("rate", FieldValue.arrayRemove(userId));
    }
}
