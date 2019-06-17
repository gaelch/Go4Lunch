package com.cheyrouse.gael.go4lunch.Utils;

import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.cheyrouse.gael.go4lunch.Utils.Constants.COLLECTION_RESTAURANT_NAME;

public class RestaurantHelper {

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_RESTAURANT_NAME);
    }

    // --- CREATE ---

    public static com.google.android.gms.tasks.Task<Void> createRestaurant(String uid, String restaurantUid, String restaurantName) {
        Restaurant restaurantToCreate = new Restaurant(restaurantUid, restaurantName);
        return RestaurantHelper.getRestaurantsCollection().document(uid).set(restaurantToCreate);
    }

    // --- GET ---

    public static com.google.android.gms.tasks.Task<DocumentSnapshot> getRestaurant(String uid){
        return RestaurantHelper.getRestaurantsCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static com.google.android.gms.tasks.Task<Void> updateRestaurant(String restaurantName, String uid) {
        return RestaurantHelper.getRestaurantsCollection().document(uid).update("restaurantName", restaurantName);
    }

}
