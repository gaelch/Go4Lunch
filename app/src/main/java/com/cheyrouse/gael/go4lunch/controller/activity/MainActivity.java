package com.cheyrouse.gael.go4lunch.controller.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cheyrouse.gael.go4lunch.utils.Constants.RC_SIGN_IN;
import static com.cheyrouse.gael.go4lunch.utils.Constants.USER;
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
    @BindView(R.id.button_twitter)
    Button twitterLoginButton;
    @BindView(R.id.spinner) Spinner spinner;

    private static int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST = 112;

    private Context mContext;
    private User user;
    private FirebaseAuth firebaseAuth;
    private String email;
    private String password;
    private String userName;
    private String TAG = "tag";
    private Prefs prefs;
    boolean var = false;
    private View dialogView;
    private String currentLanguage = "en", currentLang;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        prefs = Prefs.get(this);
        checkIfLanguageIsOk();
        CheckSelfPermissions();
        checkNotificationIsEnable();
        checkIfGpsIsEnable();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        (new AlarmHelper()).configureAlarmToResetChoice(this);
    }

    // Check language prefs and purpose choice if null
    private void checkIfLanguageIsOk() {
        String locale = prefs.getLanguage();
        if(locale != null && !locale.isEmpty()){
            setLocale(locale);
            spinner.setVisibility(View.GONE);
        }else{
            configureSpinner();
        }
    }

    // Spinner configuration to select language
    private void configureSpinner() {
        spinner.setVisibility(View.VISIBLE);
        hideButtons();
        currentLanguage = getIntent().getStringExtra(currentLang);
        List<String> list = new ArrayList<String>();

        list.add(getResources().getString(R.string.select));
        list.add(getResources().getString(R.string.en));
        list.add(getResources().getString(R.string.fr));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        setLocale("en");
                        prefs.storeLanguageChoice("en");
                        break;
                    case 2:
                        setLocale("fr");
                        prefs.storeLanguageChoice("fr");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    // Hide connexion buttons
    private void hideButtons() {
        buttonEmail.setVisibility(View.GONE);
        buttonFaceBook.setVisibility(View.GONE);
        buttonGoogle.setVisibility(View.GONE);
        twitterLoginButton.setVisibility(View.GONE);
    }


    // Set new language to apply
    public void setLocale(String localeName) {
        currentLanguage = getIntent().getStringExtra(currentLang);
        if (!localeName.equals(currentLanguage)) {
            Locale myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, MainActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        }
        showButtons();
    }

    // Show connexion buttons
    private void showButtons() {
        buttonEmail.setVisibility(View.VISIBLE);
        buttonFaceBook.setVisibility(View.VISIBLE);
        buttonGoogle.setVisibility(View.VISIBLE);
        twitterLoginButton.setVisibility(View.VISIBLE);
    }

    // Verify if Notifications are enable
    private void checkNotificationIsEnable() {
        if (!isNotificationEnabled(this)) {
            //Enable to notification access service.
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getResources().getString(R.string.notification_enable));
            alertDialog.setMessage(getResources().getString(R.string.notification_turn_on));
            alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
                    //for Android 5-7
                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);
                    // for Android 8 and above
                    intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }
    }

    public static boolean isNotificationEnabled(Context context) {
        return NotificationManagerCompat.from(context.getApplicationContext())
                .areNotificationsEnabled();
    }

    // Twitter Auth configuration
    private void configTwitterAuth() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        firebaseAuth
                .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                .addOnSuccessListener(
                        authResult -> {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            getUserInfo(user);
                        })
                .addOnFailureListener(
                        e -> {
                        });
    }

    // Check permissions to activate them
    @RequiresApi(api = Build.VERSION_CODES.M)
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
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SET_ALARM) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.SET_ALARM);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SET_TIME) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.SET_TIME);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY);
        } //if
        if (!permissions.isEmpty()) {
            String[] ListedemandeDroit = {};
            ListedemandeDroit = permissions.toArray(ListedemandeDroit);
            ActivityCompat.requestPermissions(this, permissions.toArray(ListedemandeDroit), 1);
        } //if
    }

    // result for image
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

    // Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message) throws InterruptedException {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    // --------------------
    // UTILS
    // --------------------

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) throws InterruptedException {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));
                configureAndShowHomeActivity();
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

    // Verify if GPS is enable
    private void checkIfGpsIsEnable() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showSettingsAlert();
        }
    }

    // Show dialog if GPS is disable
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.GPS_is_settings));
        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.settings_menu));
        // On pressing Settings button
        alertDialog.setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    // Launch HomeActivity
    private void configureAndShowHomeActivity() {
        Intent homeActivityIntent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(homeActivityIntent);
    }

    // If permission is granted launch HomeActivity
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                configureAndShowHomeActivity();
            }
        }
    }

    // Buttons for connexion modes
    @OnClick({R.id.button_facebook, R.id.button_google, R.id.button_twitter, R.id.button_email})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_facebook:
                startConnexionWhitFacebook();
                break;
            case R.id.button_google:
                startConnexionWhitGoogle();
                break;
            case R.id.button_twitter:
                configTwitterAuth();
                break;
            case R.id.button_email:
                startConnexionByEmail();
                break;
        }
    }

    // To start connexion by email
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

    // Check if permission is granted to email connexion
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

    // Custom dialog to connexion by email
    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);
        //then we will inflate the custom alert dialog xml that we created
        dialogView = LayoutInflater.from(this).inflate(R.layout.email_dialog, viewGroup, false);
        EditText editTextMail = dialogView.findViewById(R.id.edit_mail);
        EditText editTextPassword = dialogView.findViewById(R.id.edit_password);
        EditText editTextUserName = dialogView.findViewById(R.id.edit_user_name);

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
        dialogView.findViewById(R.id.yesButton).setOnClickListener(v -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        });
        dialogView.findViewById(R.id.button_ok).setOnClickListener(v -> {
            if (!RegexUtil.isValidEmail(email)) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_valid), Toast.LENGTH_LONG).show();
            }
            if (RegexUtil.isValidEmail(email) && password != null && password.length() > 1) {
                launchConnection(email, password);
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_pass), Toast.LENGTH_LONG).show();
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

    // Launch connexion by email
    private void launchConnection(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
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
                });
    }

    // SignIn by email
    private void signIn() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        getUserInfo(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.auth_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // SignIn by Google
    private void startConnexionWhitGoogle() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())) // SUPPORT GOOGLE
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_google_48)
                        .build(),
                RC_SIGN_IN);
    }

    // SignIn by Facebook
    private void startConnexionWhitFacebook() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build())) // FACEBOOK
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_facebook_48)
                        .build(),
                RC_SIGN_IN);
    }

    // To get list of users in database
    private void getUsersFromDataBase() {
        UserHelper.getUsersCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    if (user.getUid().equals(document.getData().get("uid").toString())) {
                        var = true;
                    }
                }
                if (!var) {
                    storeUserInDataBase(user.getUid(), user.getUid(), user.getUsername(), user.getUrlPicture());
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    // Get info to store current user
    private void getUserInfo(FirebaseUser currentUser) {
        user = new User();
        if (currentUser != null) { //---- Email connection ---- //
            for (UserInfo profile : currentUser.getProviderData()) {
                // UID specific to the provider
                user.setUid(profile.getUid());
                // Name, email address, and profile photo Url
                if (userName != null) {
                    user.setUsername(userName);
                } else {
                    user.setUsername(profile.getDisplayName());
                }
                if (profile.getEmail() != null) {
                    user.seteMail(profile.getEmail());
                } else {
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

    // Store user in preferences to retrieve him after
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

    // Store user in Firebase database
    private void storeUserInDataBase(String uId, String id, String userName, String url) {
        UserHelper.createUser(uId, id, userName, url);
    }
}
