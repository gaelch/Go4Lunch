package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.controller.activity.HomeActivity;
import com.cheyrouse.gael.go4lunch.controller.activity.MainActivity;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.cheyrouse.gael.go4lunch.utils.UserHelper;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cheyrouse.gael.go4lunch.utils.Constants.SIGN_OUT_TASK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    @BindView(R.id.switch_notifications)
    Switch aSwitch;
    @BindView(R.id.spinner)
    Spinner spinner;

    private Prefs prefs;
    private boolean check = false;
    private static final String currentLang = "current_lang";

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        // Create new fragment
        return new SettingsFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        prefs = Prefs.get(getActivity());
        configureSpinner();
        configureSwitchNotifications();
        return view;
    }

    // Spinner to language choice
    private void configureSpinner() {
        List<String> list = new ArrayList<String>();

        list.add(getResources().getString(R.string.select));
        list.add(getResources().getString(R.string.en));
        list.add(getResources().getString(R.string.fr));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, list);
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

    // Apply choice
    public void setLocale(String localeName) {
        Locale myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        showDialogToRestart(localeName);
    }

    // Show dialog box to restart app and apply modifications
    private void showDialogToRestart(String localeName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.restarting));
        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.restarting_now));
        // On pressing Settings button
        alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               signOutUserFromFirebase(localeName);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton(getResources().getString(R.string.later), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    // Configure switch to enable notifications
    private void configureSwitchNotifications() {
        User user = prefs.getPrefsUser();
        getUserInDatabase(user);

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                updateUserNotification(true);
            } else {
                updateUserNotification(false);
            }
        });
    }

    // Get user to get setting of notification
    private void getUserInDatabase(User user) {
        UserHelper.getUser(user.getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                check = (boolean) Objects.requireNonNull(task.getResult()).get("notification");
                Log.e("notification", String.valueOf(check));
                if (check) {
                    aSwitch.setChecked(true);
                }
            }
        }).addOnFailureListener(e -> Log.e("fail", e.getMessage()));
    }

    // Update notifications choice
    private void updateUserNotification(boolean notification) {
        UserHelper.updateNotification(notification, prefs.getPrefsUser().getUid());
    }

    // Sign out
    private void signOutUserFromFirebase(String localeName) {
        AuthUI.getInstance()
                .signOut(Objects.requireNonNull(getActivity()))
                .addOnSuccessListener(getActivity(), this.updateUIAfterRESTRequestsCompleted(localeName));
    }

    // SignOut from Firebase
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(String localeName) {
        return aVoid -> {
            switch (SIGN_OUT_TASK) {
                case SIGN_OUT_TASK:
                    Intent refresh = new Intent(getActivity(), MainActivity.class);
                    refresh.putExtra(currentLang, localeName);
                    startActivity(refresh);
                    break;
                default:
                    break;
            }
        };
    }

}
