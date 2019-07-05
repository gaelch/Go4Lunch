package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.cheyrouse.gael.go4lunch.utils.RestaurantHelper;
import com.cheyrouse.gael.go4lunch.utils.UserHelper;
import com.cheyrouse.gael.go4lunch.utils.starsUtils;
import com.cheyrouse.gael.go4lunch.controller.activity.RestaurantWebSiteActivity;
import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.views.DetailAdapter;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;
import static com.cheyrouse.gael.go4lunch.utils.Constants.BASE_URL;
import static com.cheyrouse.gael.go4lunch.utils.Constants.MAX_HEIGHT;
import static com.cheyrouse.gael.go4lunch.utils.Constants.MAX_WIDTH;
import static com.cheyrouse.gael.go4lunch.utils.Constants.RESULT;
import static com.cheyrouse.gael.go4lunch.utils.Constants.USER;
import static com.cheyrouse.gael.go4lunch.utils.Constants.USERS;
import static com.cheyrouse.gael.go4lunch.utils.Go4LunchService.API_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestauDetailFragment extends Fragment implements DetailAdapter.onUserAdapterListener, FloatingActionButton.OnClickListener {


    public static final String WEB_SITE_EXTRA = "web_site";
    @BindView(R.id.image_restaurant)
    ImageView imageViewRestaurant;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.tv_restaurant_name)
    TextView tvRestauurantName;
    @BindView(R.id.tv_restaurant_address)
    TextView tvRestaurantAddress;
    @BindView(R.id.rate_star1)
    ImageView imageViewRate1;
    @BindView(R.id.rate_star2)
    ImageView imageViewRate2;
    @BindView(R.id.rate_star3)
    ImageView imageViewRate3;
    @BindView(R.id.bottomNavigationDetailView)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.recycler_view_detail)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_restau_swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private DetailAdapter adapter;
    private List<User> users;
    private List<User> usersAreJoining;
    private RestauDetailFragmentListener mListener;
    private boolean var = false;
    private User user;
    private Restaurant restaurant;
    private ResultDetail resultDetail;


    public static RestauDetailFragment newInstance(ResultDetail result, List<User> users, User user) {
        // Create new fragment
        RestauDetailFragment frag = new RestauDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESULT, (Serializable) result);
        bundle.putSerializable(USERS, (Serializable) users);
        bundle.putSerializable(USER, (Serializable) user);
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


    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restau_detail, container, false);
        ButterKnife.bind(this, view);
        getTheBundleData();
        getRestaurentFromFirestore();
        setTransparentStatusBar();
        setImages();
        configureTextView();
        configureRecyclerView();
        configureBottomView();
        configureFab();
        configureTextView();
        return view;
    }

    private void updateUi() {
        setImages();

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getRestaurentFromFirestore() {
        RestaurantHelper.getRestaurant(resultDetail.getName()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    restaurant = new Restaurant();
                    restaurant.setRestaurantName((String) Objects.requireNonNull(task.getResult()).get("restaurantName"));
                    restaurant.setRate((List<String>) task.getResult().get("rate"));
                    restaurant.setUsers((List<String>) task.getResult().get("users"));
                    Log.e("restaurantName", restaurant.getRestaurantName());
                    setStars();
                    //executeRequestDetail();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fail", e.getMessage());
            }
        });
    }

    private void configureFab() {
        floatingActionButton.setOnClickListener(this);
        if (resultDetail.getName().equals(user.getChoice())) {
            floatingActionButton.setColorFilter(getResources().getColor(R.color.green));
        } else {
            floatingActionButton.setColorFilter(getResources().getColor(R.color.grey));
        }
    }

    private void setImages() {
        if (!(resultDetail.getPhotos() == null)) {
            if (!(resultDetail.getPhotos().isEmpty())) {
                // Photo restaurant
                Glide.with(this)
                        .load(BASE_URL + "?maxwidth=" + MAX_WIDTH + "&maxheight=" + MAX_HEIGHT + "&photoreference=" + resultDetail
                                .getPhotos().get(0).getPhotoReference() + "&key=" + API_KEY).into(imageViewRestaurant);
            }
        } else {
            Glide.with(this).load(resultDetail.getIcon()).apply(RequestOptions.centerCropTransform()).into(imageViewRestaurant);
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
        resultDetail = (ResultDetail) getArguments().getSerializable(RESULT);
        users = (List<User>) getArguments().getSerializable(USERS);
        user = (User) getArguments().getSerializable(USER);
        getRestaurantJoiningUsers();
    }

    private void getRestaurantJoiningUsers() {
        usersAreJoining = new ArrayList<>();
        for (User user : users) {
            if (user.getChoice() != null) {
                if (user.getChoice().equals(resultDetail.getName())) {
                    usersAreJoining.add(user);
                }
            }
        }
    }

    private void configureTextView() {
        tvRestauurantName.setText(resultDetail.getName());
        tvRestaurantAddress.setText(resultDetail.getAddressComponents().get(0).getLongName());
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

    //Configure SwipeRefreshLayout
    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    // 2 - Configure BottomNavigationView Listener
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void configureBottomView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> getBottomChoice(item.getItemId()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Boolean getBottomChoice(Integer integer) {
        switch (integer) {
            case R.id.action_phone:
                Toast.makeText(getActivity(), "téléphone", Toast.LENGTH_SHORT).show();
                Intent dialIntent = new Intent();
                dialIntent.setAction(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse(resultDetail.getFormattedPhoneNumber()));
                break;
            case R.id.action_like:
                String userId = user.getUid();
                RestaurantHelper.updateRestaurantRate(userId, resultDetail.getName());
                getRestaurentFromFirestore();
                Toast.makeText(getActivity(), "like !", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_website:
                Toast.makeText(getActivity(), "website", Toast.LENGTH_SHORT).show();
                Intent detailActivityIntent = new Intent(getActivity(), RestaurantWebSiteActivity.class);
                detailActivityIntent.putExtra(WEB_SITE_EXTRA, resultDetail.getWebsite());
                startActivity(detailActivityIntent);
                break;
        }
        return true;
    }

    private void setStars() {
        if (starsUtils.getRate(restaurant.getRate().size(), users) == 1) {
            imageViewRate1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
        }
        if (starsUtils.getRate(restaurant.getRate().size(), users) == 2) {
            imageViewRate1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
            imageViewRate2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
        }
        if (starsUtils.getRate(restaurant.getRate().size(), users) == 3) {
            imageViewRate1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
            imageViewRate2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
            imageViewRate3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_rate_white_18dp));
        }

    }

    @Override
    public void onArticleClicked(User user) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        Prefs prefs = Prefs.get(getActivity());
        if (v.getId() == R.id.fab) {
            if (!var) {
                var = true;
                floatingActionButton.setColorFilter(getResources().getColor(R.color.green));
                if(user.getChoice() != null && user.getChoice().length() != 0){
                    deleteChoice(user.getUsername(), user.getChoice());
                }
                //set user table restaurant et set restaurant table users
                user.setChoice(resultDetail.getName());
                prefs.storeChoicePrefs(resultDetail);
                prefs.storeUserPrefs(user);
                updateTableUsers();
                updateTableRestaurants(user.getUsername(), resultDetail.getName());
                getUsersInDatabase();
            } else {
                var = false;
                if(user.getChoice() != null){
                    deleteChoice(user.getUsername(), user.getChoice());
                }
                floatingActionButton.setColorFilter(getResources().getColor(R.color.grey));
                user.setChoice("");
                prefs.storeChoicePrefs(null);
                prefs.storeUserPrefs(user);
                updateTableUsers();
                getUsersInDatabase();
            }
        }
    }

    private void getUsersInDatabase() {
        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String uid = document.getData().get("uid").toString();
                        String username = document.getData().get("username").toString();
                        String urlPicture = document.getData().get("urlPicture").toString();
                        String choice = document.getData().get("choice").toString();
                        User userToAdd = new User(uid, username, urlPicture);
                        userToAdd.setChoice(choice);
                        users.add(userToAdd);
                        getRestaurantJoiningUsers();
                        configureRecyclerView();
                    }
                } else {
                    Log.d(ProfilePictureView.TAG, "Error getting documents: ", task.getException());
                }
                Log.e("listMates", String.valueOf(users.size()));
            }
        });
    }

    private void updateTableRestaurants(String userName, String choice) {
        RestaurantHelper.updateRestaurantChoice(userName, choice);
    }

    private void deleteChoice(String userName, String choice) {
        RestaurantHelper.deleteUserChoice(userName, choice);
    }

    private void updateTableUsers() {
        UserHelper.updateChoice(user.getChoice(), user.getUid());
    }

    public interface RestauDetailFragmentListener {
        void callbackRestauDetail();
    }

}
