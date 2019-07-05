package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment implements WorkMatesAdapter.onUserAdapterListener {

    @BindView(R.id.fragment_workMates_recycler_view) RecyclerView recyclerView;

    private List<User> users;
    private WorkMatesAdapter adapter;
    private FirebaseAuth firebaseAuth;

    public WorkmatesFragment() {
        // Required empty public constructor
    }

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
        firebaseAuth = FirebaseAuth.getInstance();
        getUsersInBundle();
        configureRecyclerView();
        return v;
    }

    private void getUsersInBundle() {
        assert getArguments() != null;
        users = (List<User>) getArguments().getSerializable(USERS);
       // updateUI(users);
    }


    //configure recyclerView and Tabs
    private void configureRecyclerView() {
        // Create adapter passing in the sample user data
        this.adapter = new WorkMatesAdapter(users, Glide.with(this), this);
        // Attach the adapter to the recyclerView to populate items
        this.recyclerView.setAdapter(this.adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /*@Override
    public void onRestaurantClicked(Result result) {
        mListener.callbackList(result);
        Log.e("test ressult click", "result returned !");
    }*/


   /* private void updateUI(List<User> users){
        if(users != null && users.size() != 0) {
            adapter.notifyDataSetChanged();
        }
    }*/

    @Override
    public void onArticleClicked(User user) {

    }
}
