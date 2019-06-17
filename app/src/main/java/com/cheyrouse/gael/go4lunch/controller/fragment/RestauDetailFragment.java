package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cheyrouse.gael.go4lunch.R;
import com.cheyrouse.gael.go4lunch.models.Result;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.views.DetailAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;
import static com.cheyrouse.gael.go4lunch.Utils.Constants.BASE_URL;
import static com.cheyrouse.gael.go4lunch.Utils.Constants.MAX_HEIGHT;
import static com.cheyrouse.gael.go4lunch.Utils.Constants.MAX_WIDTH;
import static com.cheyrouse.gael.go4lunch.Utils.Constants.RESULT;
import static com.cheyrouse.gael.go4lunch.Utils.Constants.USERS;
import static com.cheyrouse.gael.go4lunch.Utils.Go4LunchService.API_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestauDetailFragment extends Fragment implements DetailAdapter.onUserAdapterListener, FloatingActionButton.OnClickListener {


    @BindView(R.id.image_restaurant) ImageView imageViewRestaurant;
    @BindView(R.id.fab) FloatingActionButton floatingActionButton;
    @BindView(R.id.tv_restaurant_name) TextView tvRestauurantName;
    @BindView(R.id.tv_restaurant_address) TextView tvRestaurantAddress;
    @BindView(R.id.rate_stars) ImageView imageViewRate;
    @BindView(R.id.bottomNavigationDetailView) BottomNavigationView bottomNavigationView;
    @BindView(R.id.recycler_view_detail) RecyclerView recyclerView;

    private Result result;
    private DetailAdapter adapter;
    private List<User> users;
    private List<User> usersAreJoining;
    private RestauDetailFragmentListener mListener;

    public static RestauDetailFragment newInstance(Result result, List<User> users) {
        // Create new fragment
        RestauDetailFragment frag = new RestauDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESULT, (Serializable) result);
        bundle.putSerializable(USERS, (Serializable) users);
        frag.setArguments(bundle);
        return frag;
    }

    public RestauDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RestauDetailFragmentListener) {
            //Listener to pass userLogin to th activityMain
            mListener = (RestauDetailFragmentListener) context;
        } else {
            Log.d(TAG, "onAttach: parent Activity must implement MainFragmentListener");
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restau_detail, container, false);
        ButterKnife.bind(this, view);
        getTheBundleData();
        setTransparentStatusBar();
        setImages();
        configureTextView();
        configureRecyclerView();
        configureBottomView();
        return view;
    }

    private void setImages() {
        if (!(result.getPhotos() == null)){
            if (!(result.getPhotos().isEmpty())){
                // Photo restaurant
                Glide.with(this)
                        .load(BASE_URL+"?maxwidth="+MAX_WIDTH+"&maxheight="+MAX_HEIGHT+"&photoreference="+result
                                .getPhotos().get(0).getPhotoReference()+"&key="+ API_KEY).into(imageViewRestaurant);
            }
        }else{
            Glide.with(this).load(result.getIcon()).apply(RequestOptions.centerCropTransform()).into(imageViewRestaurant);
        }


        // Rate stars
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setTransparentStatusBar() {
        Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private void getTheBundleData() {
        assert getArguments() != null;
        result = (Result) getArguments().getSerializable(RESULT);
        users = (List<User>) getArguments().getSerializable(USERS);
        getRestaurantJoiningUsers();
    }

    private void getRestaurantJoiningUsers() {
        usersAreJoining = new ArrayList<>();
        for(User user : users){
            if (user.getChoice() != null){
                if(user.getChoice().equals(result.getName())){
                    usersAreJoining.add(user);
                }
            }
        }
    }

    private void configureTextView() {
        tvRestauurantName.setText(result.getName());
        tvRestaurantAddress.setText(result.getVicinity());
    }

    //configure recyclerView and Tabs
    private void configureRecyclerView() {
        // Create adapter passing in the sample user data
        this.adapter = new DetailAdapter(usersAreJoining, Glide.with(this), this);
        // Attach the adapter to the recyclerView to populate items
        this.recyclerView.setAdapter(this.adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // 2 - Configure BottomNavigationView Listener
    private void configureBottomView(){
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> getBottomChoice(item.getItemId()));
    }
    private Boolean getBottomChoice(Integer integer){
        switch (integer) {
            case R.id.action_phone:
                Toast.makeText(getActivity(), "téléphone", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_like:
                Toast.makeText(getActivity(), "like !", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_website:
                Toast.makeText(getActivity(), "website", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onArticleClicked(User user) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            floatingActionButton.setColorFilter(getResources().getColor(R.color.green));
        }
    }

    public interface RestauDetailFragmentListener{
        void callbackRestauDetail();
    }

}
