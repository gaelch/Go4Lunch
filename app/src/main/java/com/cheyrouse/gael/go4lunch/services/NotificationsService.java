package com.cheyrouse.gael.go4lunch.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.controller.activity.MainActivity;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.cheyrouse.gael.go4lunch.utils.RestaurantHelper;
import com.cheyrouse.gael.go4lunch.utils.StringHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.cheyrouse.gael.go4lunch.utils.Constants.NOTIFICATION_ID;
import static com.cheyrouse.gael.go4lunch.utils.Constants.NOTIFICATION_TAG;

public class NotificationsService extends FirebaseMessagingService {

    private ResultDetail restaurant;
    private String message;
    private String coWorkers;
    private String text;
    private User user;
    private Prefs prefs;

    // On receive
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            message = remoteMessage.getNotification().getBody();
            // 8 - Show notification after received message
            prefs = Prefs.get(getApplicationContext());
            this.checkIfLanguageIsOk();
        }
    }


    // Check language prefs and purpose choice if null
    private void checkIfLanguageIsOk() {
        String locale = prefs.getLanguage();
        if (locale != null && !locale.isEmpty()) {
            setLocale(locale);
        } else {
            getData();
        }
    }

    public void setLocale(String localeName) {
        Locale myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        getData();
    }

    // Get data to send notification
    private void getData() {
        user = prefs.getPrefsUser();
        restaurant = prefs.getChoice();
        if (restaurant == null) {
            text = getResources().getString(R.string.no_restaurant);
            sendVisualNotification();
        } else {
            getRestaurantFromDataBase();
        }
    }

    // get restaurant from database to get users in restaurant table
    @SuppressWarnings("unchecked")
    private void getRestaurantFromDataBase() {
        RestaurantHelper.getRestaurant(restaurant.getName()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> userList = ((List<String>) Objects.requireNonNull(task.getResult()).get("users"));
                coWorkers = StringHelper.getCoWorkers(Objects.requireNonNull(userList), user, getApplicationContext());
                sendVisualNotification();
            }
        }).addOnFailureListener(e -> Log.e("fail", e.getMessage()));
    }

    // Send Notification
    private void sendVisualNotification() {
        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        if (restaurant != null) {
            text = getResources().getString(R.string.you_eat) + restaurant.getName() + getResources().getString(R.string.located) + restaurant.getFormattedAddress() + ", " + coWorkers;
        }
        // 2 - Create a Style for the Notification
        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
        inboxStyle.setBigContentTitle(getString(R.string.notification_title));
        inboxStyle.bigText(text);

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
            CharSequence channelName = "Firebase Message";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            Objects.requireNonNull(notificationManager).createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        Objects.requireNonNull(notificationManager).notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }
}


