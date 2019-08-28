package com.cheyrouse.gael.go4lunch.utils;

import com.cheyrouse.gael.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.cheyrouse.gael.go4lunch.utils.Constants.COLLECTION_NAME;

public class UserHelper {


    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static void createUser(String uid, String userUid, String username, String urlPicture, String email) {
        User userToCreate = new User(userUid, username, urlPicture, email);
        UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static void updateChoice(String choice, String uid) {
        UserHelper.getUsersCollection().document(uid).update("choice", choice);
    }

    public static void updateNotification(boolean notification, String uid) {
        UserHelper.getUsersCollection().document(uid).update("notification", notification);
    }
}
