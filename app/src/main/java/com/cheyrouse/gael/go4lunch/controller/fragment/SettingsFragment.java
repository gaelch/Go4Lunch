package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
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
import android.widget.Toast;

import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.controller.activity.MainActivity;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.cheyrouse.gael.go4lunch.utils.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private void configureSpinner() {
        List<String> list = new ArrayList<String>();

        list.add("Select language");
        list.add("English");
        list.add("Français");

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

    public void setLocale(String localeName) {
        Locale myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Toast.makeText(getActivity(), getResources().getString(R.string.Language_selected), Toast.LENGTH_SHORT).show();
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


}
