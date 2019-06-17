package com.cheyrouse.gael.go4lunch.Utils;

import com.cheyrouse.gael.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.cheyrouse.gael.go4lunch.Utils.Constants.COLLECTION_NAME;

public class UserHelper {


    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static com.google.android.gms.tasks.Task<Void> createUser(String uid, String userUid, String username, String urlPicture, String choice) {
        User userToCreate = new User(userUid, username, urlPicture, choice);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static com.google.android.gms.tasks.Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static com.google.android.gms.tasks.Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}
