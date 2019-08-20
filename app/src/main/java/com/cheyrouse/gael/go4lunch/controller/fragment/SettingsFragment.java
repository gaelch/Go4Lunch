package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.cheyrouse.gael.go4lunch.utils.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    @BindView(R.id.switch_notifications)
    Switch aSwitch;

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
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        prefs = Prefs.get(getActivity());
        configureSwitchNotifications();
        return view;
    }

    // Configure switch to enable notifications
    private void configureSwitchNotifications() {
        User user = prefs.getPrefsUser();
        getUserInDatabase(user);

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                updateUserNotification(true);
            }else {
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
                if(check){
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
