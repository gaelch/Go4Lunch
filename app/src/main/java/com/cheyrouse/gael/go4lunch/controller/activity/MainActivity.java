package com.cheyrouse.gael.go4lunch.controller.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cheyrouse.gael.go4lunch.Utils.Prefs;
import com.cheyrouse.gael.go4lunch.Utils.RestaurantHelper;
import com.cheyrouse.gael.go4lunch.Utils.UserHelper;
import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.User;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.cheyrouse.gael.go4lunch.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.DocumentCollections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cheyrouse.gael.go4lunch.Utils.Constants.RC_SIGN_IN;
import static com.cheyrouse.gael.go4lunch.Utils.Constants.UID_DOC_USERS;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button_facebook) Button buttonFaceBook;
    @BindView(R.id.button_google) Button buttonGoogle;
    @BindView(R.id.activity_main_coordinator_layout) CoordinatorLayout coordinatorLayout;

    private User user;
    private Restaurant restaurant;
    private FirebaseAuth firebaseAuth;
    private List<User> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 4 - Handle SignIn Activity response on activity result
        try {
            this.handleResponseAfterSignIn(requestCode, resultCode, data);
            getUserInfo();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // --------------------
    // UI
    // --------------------

    // 2 - Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message) throws InterruptedException {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    // --------------------
    // UTILS
    // --------------------

    // 3 - Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) throws InterruptedException {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));
                checkPermission();
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_no_internet));
                } else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showAlertDialog();
        } else {
            configureAndShowHomeActivity();
        }
    }

    private void configureAndShowHomeActivity() {
        Intent homeActivityIntent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(homeActivityIntent);
    }

    private void showAlertDialog() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configureAndShowHomeActivity();
                } else {
                    showSettingsAlert();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    @OnClick({R.id.button_facebook, R.id.button_google})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_facebook:
                startConnexionWhitFacebook();
                break;
            case R.id.button_google:
                startConnexionWhitGoogle();
                break;
        }
    }

    private void startConnexionWhitGoogle() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())) // SUPPORT GOOGLE
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_google_48)
                        .build(),
                RC_SIGN_IN);
    }

    private void startConnexionWhitFacebook() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build())) // FACEBOOK
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_facebook_48)
                        .build(),
                RC_SIGN_IN);
    }

    private void getUsersFromDataBase(){
        CollectionReference usersCollection = UserHelper.getUsersCollection();
        DocumentReference doc = usersCollection.document(user.getUid());
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Log.d("testDocumentSnapshot", "DocumentSnapshot data: " + Objects.requireNonNull(documentSnapshot.getData()).get("userName"));
                if(documentSnapshot != null) {
                    if (documentSnapshot.getData() != null) {
                        String uid = (String) documentSnapshot.getData().get("uid");
                        if(!uid.equals(user.getUid())) {
                            storeUserInDataBase(user.getUid(), user.getUid(), user.getUsername(), user.getUrlPicture());
                        }
                    }
                }
            }
        });
    }



    private void getUserInfo() {
        user = new User();
        user.setUrlPicture(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhotoUrl()).toString());
        user.setUsername(firebaseAuth.getCurrentUser().getDisplayName());
        user.setUid(firebaseAuth.getUid());
        user.seteMail(firebaseAuth.getCurrentUser().getEmail());
        Prefs prefs = Prefs.get(this);
        prefs.storeUserPrefs(user);
        getUsersFromDataBase();
    }

    private void storeUserInDataBase(String uId, String id, String userName, String url) {
        UserHelper.createUser(uId, id,userName, url);
    }
}
