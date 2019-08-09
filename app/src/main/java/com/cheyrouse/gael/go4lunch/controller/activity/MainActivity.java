package com.cheyrouse.gael.go4lunch.controller.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cheyrouse.gael.go4lunch.utils.AlarmHelper;
import com.cheyrouse.gael.go4lunch.utils.Constants;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.cheyrouse.gael.go4lunch.utils.RegexUtil;
import com.cheyrouse.gael.go4lunch.utils.UserHelper;
import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.User;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.cheyrouse.gael.go4lunch.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cheyrouse.gael.go4lunch.utils.Constants.RC_SIGN_IN;
import static com.facebook.login.widget.ProfilePictureView.TAG;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button_facebook)
    Button buttonFaceBook;
    @BindView(R.id.button_google)
    Button buttonGoogle;
    @BindView(R.id.button_email)
    Button buttonEmail;
    @BindView(R.id.activity_main_coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.button_twitter) Button twitterLoginButton;

    private static int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST = 112;
    private static int RESULT_TWITTER = 2;

    private User user;
    private Restaurant restaurant;
    private FirebaseAuth firebaseAuth;
    private List<User> userList;
    private String email;
    private String password;
    private String userName;
    private String TAG = "tag";
    private Prefs prefs;
    boolean var = false;
    private View dialogView;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        CheckSelfPermissions();
        checkIfGpsIsEnable();
        prefs = Prefs.get(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        (new AlarmHelper()).configureAlarmToResetChoice(this);
    }

    private void configTwitterAuth() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        firebaseAuth
                .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                getUserInfo(user);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure.
                            }
                        });
    }

    // Check permissions to activate them
    private void CheckSelfPermissions() {
        List<String> permissions = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WAKE_LOCK);
        } //if
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);
        } //if
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } //if
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        } //if
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.VIBRATE);
        } //if
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        } //if
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        } //if
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        } //if
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CALL_PHONE);
        } //if
        if (!permissions.isEmpty()) {
            String[] ListedemandeDroit = {};
            ListedemandeDroit = permissions.toArray(ListedemandeDroit);
            ActivityCompat.requestPermissions(this, permissions.toArray(ListedemandeDroit), 1);
        } //if
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            //Load image from picked Uri
            assert selectedImage != null;
            Glide.with(this).load(selectedImage.toString()).into((ImageView) dialogView.findViewById(R.id.imageAvatar));
            prefs.storePicture(selectedImage, userName);
        } else {
            // 4 - Handle SignIn Activity response on activity result
            try {
                this.handleResponseAfterSignIn(requestCode, resultCode, data);
                getUserInfo(null);
            } catch (InterruptedException e) {
                e.printStackTrace();
                e.getMessage();
            }
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
        } else {
            configureAndShowHomeActivity();
        }
    }

    private void checkIfGpsIsEnable() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        assert manager != null;
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            showSettingsAlert();
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
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

    private void configureAndShowHomeActivity() {
        Intent homeActivityIntent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(homeActivityIntent);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configureAndShowHomeActivity();
                } else {
                   // showSettingsAlert();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            break;
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //showAlertDialog();
                }
            }

        }
        return;
    }

    @OnClick({R.id.button_facebook, R.id.button_google,  R.id.button_twitter, R.id.button_email})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_facebook:
                startConnexionWhitFacebook();
                break;
            case R.id.button_google:
                startConnexionWhitGoogle();
                break;
            case R.id.button_twitter:
                //startConnexionWhitTwitter();
                configTwitterAuth();
                break;
            case R.id.button_email:
                startConnexionByEmail();
                break;
        }
    }

    private void startConnexionByEmail() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST);
            } else {
                showCustomDialog();
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);
        //then we will inflate the custom alert dialog xml that we created
        dialogView = LayoutInflater.from(this).inflate(R.layout.email_dialog, viewGroup, false);
        EditText editTextMail = (EditText) dialogView.findViewById(R.id.edit_mail);
        EditText editTextPassword = (EditText) dialogView.findViewById(R.id.edit_password);
        EditText editTextUserName = (EditText) dialogView.findViewById(R.id.edit_user_name);

        user = prefs.getPrefsUser();
        if (user != null) {
            editTextMail.setText(user.geteMail());
            email = user.geteMail();
            editTextUserName.setText(user.getUsername());
            userName = user.getUsername();
            Uri selectedImage = prefs.getPicture(user.getUsername());
            if (selectedImage != null) {
                Picasso.get().load(selectedImage).into((ImageView) dialogView.findViewById(R.id.imageAvatar));
            }
        }
        editTextMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                email = editTextMail.getText().toString();
            }
        });
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                password = editTextPassword.getText().toString();
            }
        });
        editTextUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                userName = editTextUserName.getText().toString();
            }
        });
        dialogView.findViewById(R.id.yesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        dialogView.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!RegexUtil.isValidEmail(email)) {
                    Toast.makeText(getApplicationContext(), "Sorry but is not a valid email", Toast.LENGTH_LONG).show();
                }
                if (RegexUtil.isValidEmail(email) && password != null && password.length() > 1) {
                    launchConnection(email, password);
                }else{
                    Toast.makeText(getApplicationContext(), "enter your password please", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void launchConnection(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            getUserInfo(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            signIn();
                        }
                    }
                });
    }

    private void signIn() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            getUserInfo(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    private void getUsersFromDataBase() {
        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if (user.getUid().equals(document.getData().get("uid").toString())) { ////////// voir pour éviter la création de plusieur compte avec les même id
                            var = true;
                        }
                    }
                    if (!var) {
                        storeUserInDataBase(user.getUid(), user.getUid(), user.getUsername(), user.getUrlPicture());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void getUserInfo(FirebaseUser currentUser) {
        user = new User();
        if (currentUser != null) { //---- Email connection ---- //
            for (UserInfo profile : currentUser.getProviderData()) {
                // UID specific to the provider
                user.setUid(profile.getUid());
                // Name, email address, and profile photo Url
                if(userName!=null){
                    user.setUsername(userName);
                }else{
                    user.setUsername(profile.getDisplayName());
                }
                if(profile.getEmail()!=null){
                    user.seteMail(profile.getEmail());
                }else{
                    user.seteMail(profile.getUid());
                }
                if (profile.getPhotoUrl() != null) {
                    user.setUrlPicture(Objects.requireNonNull(profile.getPhotoUrl()).toString());
                }
            }
            if (currentUser.getPhotoUrl() != null) {
                user.setUrlPicture(Objects.requireNonNull(currentUser.getPhotoUrl()).toString());
            }
            storeUserInPrefs();
            configureAndShowHomeActivity();
        } else { //---- Google, Facebook, Twitter connection ---- //
            if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null) {
                user.setUrlPicture(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhotoUrl()).toString());
                user.setUsername(firebaseAuth.getCurrentUser().getDisplayName());
                user.setUid(firebaseAuth.getUid());
                user.seteMail(firebaseAuth.getCurrentUser().getEmail());
                storeUserInPrefs();
            }
        }
    }

    private void storeUserInPrefs() {
        prefs = Prefs.get(this);
        prefs.storeListResults(null);
        User userPref = prefs.getPrefsUser();
        if (userPref != null && userPref.getUid().equals(user.getUid())) {
            String choice = userPref.getChoice();
            boolean notification = userPref.isNotification();
            user.setChoice(choice);
            user.setNotification(notification);
        }
        prefs.storeUserPrefs(user);
        getUsersFromDataBase();
    }

    private void storeUserInDataBase(String uId, String id, String userName, String url) {
        UserHelper.createUser(uId, id, userName, url);
    }
}
