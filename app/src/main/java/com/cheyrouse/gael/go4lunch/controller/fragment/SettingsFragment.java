package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.cheyrouse.gael.go4lunch.utils.UserHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    @BindView(R.id.switch_notifications)
    Switch aSwitch;

    private Prefs prefs;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        // Create new fragment
        return new SettingsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        prefs = Prefs.get(getActivity());
        configureSwitchNotifications();
        return view;
    }

    private void configureSwitchNotifications() {
        User user = prefs.getPrefsUser();
        boolean check = user.isNotification();
        if(check){
            aSwitch.setChecked(true);
        }
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                user.setNotification(true);
                prefs.storeUserPrefs(user);
                updateUserNotification(true);
            }else {
                user.setNotification(true);
                prefs.storeUserPrefs(user);
                updateUserNotification(false);
            }
        });
    }

    private void updateUserNotification(boolean notification) {
        UserHelper.updateNotification(notification, prefs.getPrefsUser().getUid());
    }


}
