package com.cheyrouse.gael.go4lunch.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.controller.activity.MainActivity;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.cheyrouse.gael.go4lunch.utils.Constants.NOTIFICATION_ID;
import static com.cheyrouse.gael.go4lunch.utils.Constants.NOTIFICATION_TAG;
import static com.facebook.login.widget.ProfilePictureView.TAG;

public class NotificationsService extends FirebaseMessagingService {

    private ResultDetail restaurant;
    private List<User> users;
    private String message;
    private String coWorkers;
    private String text;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            message = remoteMessage.getNotification().getBody();
            // 8 - Show notification after received message
            this.getData();
        }
    }

    private void getData() {
        Prefs prefs = Prefs.get(getApplicationContext());
        restaurant = prefs.getChoice();
        if(restaurant==null){
            text = getResources().getString(R.string.no_restaurant);
            sendVisualNotification();
        }else{
            getUsersInDatabase();
        }
    }

    private void getUsersInDatabase() {
        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    users = new ArrayList<>();
                    String choice = null;
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String uid = document.getData().get("uid").toString();
                        String username = document.getData().get("username").toString();
                        String urlPicture = document.getData().get("urlPicture").toString();
                        if(document.getData().get("choice")!=null){
                            choice = document.getData().get("choice").toString();
                        }
                        User userToAdd = new User(uid, username, urlPicture);
                        userToAdd.setChoice(choice);
                        users.add(userToAdd);
                        coWorkers = getCoWorkers();
                        Log.e("test notification", "in getUserInDataBase");
                        sendVisualNotification();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private String getCoWorkers() {
        List<String> coWorks = new ArrayList<>();
        for(User user : users){
            if(user.getChoice()!=null){
                if(user.getChoice().equals(restaurant.name)){
                    coWorks.add(user.getUsername());
                }
            }
        }
        String match = String.valueOf(coWorks).replace("[", "").replace("]", "") + "will be there with you";
        if(Objects.requireNonNull(coWorks).size() != 0){
            return match;
        }else{
            return getResources().getString(R.string.no_coworker);
        }
    }

    private void sendVisualNotification() {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        if(restaurant != null){
            text = "This lunch, you eat at" + restaurant.name + "located at" + restaurant.getFormattedAddress() + ", " + coWorkers;
        }
        // 2 - Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.notification_title));
        inboxStyle.addLine(text);

        // 3 - Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);

        // 4 - Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_go4lunch)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message provenant de Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            Objects.requireNonNull(notificationManager).createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        Objects.requireNonNull(notificationManager).notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }
}


