package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.views.WorkMatesAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cheyrouse.gael.go4lunch.utils.Constants.USERS;
import static com.firebase.ui.auth.ui.email.CheckEmailFragment.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment implements WorkMatesAdapter.onUserAdapterListener {

    @BindView(R.id.fragment_workMates_recycler_view) RecyclerView recyclerView;

    private List<User> users;
    private WorkMateFragmentListener mListener;

    public WorkmatesFragment() {
        // Required empty public constructor
    }

    //Attach the listener to activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WorkMateFragmentListener) {
            //Listener to pass userLogin to th activityMain
            mListener = (WorkMateFragmentListener) context;
        } else {
            Log.d(TAG, "onAttach: parent Activity must implement MainFragmentListener");
        }
    }


    // New instance
    public static WorkmatesFragment newInstance(List<User> users) {
        // Create new fragment
        WorkmatesFragment frag = new WorkmatesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USERS, (Serializable) users);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, v);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        getUsersInBundle();
        configureRecyclerView();
        return v;
    }

    // Get bundle data
    @SuppressWarnings("unchecked")
    private void getUsersInBundle() {
        assert getArguments() != null;
        users = (List<User>) getArguments().getSerializable(USERS);
       // updateUI(users);
    }


    //configure recyclerView and Tabs
    private void configureRecyclerView() {
        // Create adapter passing in the sample user data
        WorkMatesAdapter adapter = new WorkMatesAdapter(getActivity(), users, Glide.with(this), this, 0);
        // Attach the adapter to the recyclerView to populate items
        this.recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Listener
    @Override
    public void onArticleClicked(User user) {
        mListener.callbackMates(user);
    }

    // Interface
    public interface WorkMateFragmentListener{
        void callbackMates(User user);
    }
}
