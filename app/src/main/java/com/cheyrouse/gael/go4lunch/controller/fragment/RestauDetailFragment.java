package com.cheyrouse.gael.go4lunch.controller.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
import com.cheyrouse.gael.go4lunch.utils.ListUtils;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.cheyrouse.gael.go4lunch.utils.RestaurantHelper;
import com.cheyrouse.gael.go4lunch.utils.UserHelper;
import com.cheyrouse.gael.go4lunch.utils.StarsUtils;
import com.cheyrouse.gael.go4lunch.controller.activity.RestaurantWebSiteActivity;
import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.views.WorkMatesAdapter;
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
import static com.cheyrouse.gael.go4lunch.utils.Constants.API_KEY;
import static com.cheyrouse.gael.go4lunch.utils.Constants.BASE_URL;
import static com.cheyrouse.gael.go4lunch.utils.Constants.MAX_HEIGHT;
import static com.cheyrouse.gael.go4lunch.utils.Constants.MAX_WIDTH;
import static com.cheyrouse.gael.go4lunch.utils.Constants.RESTAURANTS;
import static com.cheyrouse.gael.go4lunch.utils.Constants.RESULT;
import static com.cheyrouse.gael.go4lunch.utils.Constants.USER;
import static com.cheyrouse.gael.go4lunch.utils.Constants.USERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestauDetailFragment extends Fragment implements FloatingActionButton.OnClickListener {


    public static final String WEB_SITE_EXTRA = "web_site";
    @BindView(R.id.image_restaurant)
    ImageView imageViewRestaurant;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.tv_restaurant_name)
    TextView tvRestaurantName;
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

    private WorkMatesAdapter adapter;
    private List<User> users;
    private List<User> usersAreJoining;
    private RestauDetailFragmentListener mListener;
    private boolean var = false;
    private User user;
    //private User userDatabase;
    private String choice;
    private Restaurant restaurant;
    private ResultDetail resultDetail;
    private List<Restaurant> restaurantList;
    private boolean found = false;
    private boolean isFound = false;


    public static RestauDetailFragment newInstance(ResultDetail result, List<User> users, User user, List<Restaurant> restaurants) {
        // Create new fragment
        RestauDetailFragment frag = new RestauDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESULT, (Serializable) result);
        bundle.putSerializable(USERS, (Serializable) users);
        bundle.putSerializable(USER, (Serializable) user);
        bundle.putSerializable(RESTAURANTS, (Serializable) restaurants);
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
        getUsersInDatabase();
        getRestaurantFromFirestore();
        setTransparentStatusBar();
        setImages();
        configureTextView();
        configureRecyclerView();
        configureBottomView();
        configureTextView();
        configureSwipeRefreshLayout();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getRestaurantFromFirestore() {
        RestaurantHelper.getRestaurant(resultDetail.getName()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                restaurant = new Restaurant();
                restaurant.setRestaurantName((String) Objects.requireNonNull(task.getResult()).get("restaurantName"));
                restaurant.setRate((List<String>) task.getResult().get("rate"));
                restaurant.setUsers((List<String>) task.getResult().get("users"));
                setStars();
            }
        }).addOnFailureListener(e -> Log.e("fail", e.getMessage()));
    }

    private void configureFab() {
        floatingActionButton.setOnClickListener(this);
        if (resultDetail.getName().equals(choice)) {
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
        restaurantList = (List<Restaurant>) getArguments().getSerializable(RESTAURANTS);
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
            if (user.getUid().equals(this.user.getUid())) {
                choice = user.getChoice();
            }
        }
    }

    private void configureTextView() {
        tvRestaurantName.setText(resultDetail.getName());
        tvRestaurantAddress.setText(resultDetail.getFormattedAddress());
    }

    //configure recyclerView and Tabs
    private void configureRecyclerView() {
        // Create adapter passing in the sample user data
        this.adapter = new WorkMatesAdapter(getActivity(), usersAreJoining, Glide.with(this), null, 1);
        // Attach the adapter to the recyclerView to populate items
        this.recyclerView.setAdapter(this.adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    //Configure SwipeRefreshLayout
    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getUsersInDatabase();
            configureRecyclerView();
        });
    }

    // 2 - Configure BottomNavigationView Listener
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void configureBottomView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> getBottomChoice(item.getItemId()));
        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Boolean getBottomChoice(Integer integer) {
        switch (integer) {
            case R.id.action_phone:
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    //Creating intents for making a call
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + resultDetail.getFormattedPhoneNumber()));
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "You don't assign permission.", Toast.LENGTH_SHORT).show();
                    showSettingsAlert();
                }
                break;
            case R.id.action_like:
                String userId = user.getUid();
                updateTableRestaurants(userId, user.getUsername(), resultDetail.getName(), 1);
                break;
            case R.id.action_website:
                if (resultDetail.getWebsite() != null) {
                    Intent detailActivityIntent = new Intent(getActivity(), RestaurantWebSiteActivity.class);
                    detailActivityIntent.putExtra(WEB_SITE_EXTRA, resultDetail.getWebsite());
                    startActivity(detailActivityIntent);
                } else {
                    showBoxInfo();
                }
                break;
        }
        return true;
    }

    private void showBoxInfo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("No url found!");
        alertDialog.setMessage("This restaurant does not seem to have an website");
        alertDialog.setPositiveButton("ok", (dialog, which) -> {
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void setStars() {
        StarsUtils.setStars(getActivity(), imageViewRate1, imageViewRate2, imageViewRate3, users, restaurant);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", "com.cheyrouse.gael.go4lunch", null));
            startActivity(intent);
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        // Showing Alert Message
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        Prefs prefs = Prefs.get(getActivity());
        if (v.getId() == R.id.fab) {
            if (!var) {
                var = true;
                floatingActionButton.setColorFilter(getResources().getColor(R.color.green));
                if (choice != null && choice.length() != 0) {
                    deleteChoice(user.getUsername(), choice);
                }
                //set user table restaurant et set restaurant table users
                user.setChoice(resultDetail.getName());
                prefs.storeChoicePrefs(resultDetail);
                prefs.storeUserPrefs(user);
                updateTableUsers();
                updateTableRestaurants("", user.getUsername(), resultDetail.getName(), 0);
                getUsersInDatabase();
            } else {
                var = false;
                if (choice != null) {
                    deleteChoice(user.getUsername(), choice);
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
        UserHelper.getUsersCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                users = new ArrayList<>();
                String urlPicture = null;
                String choice = null;
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    String uid = document.getData().get("uid").toString();
                    String username = document.getData().get("username").toString();
                    if (document.getData().get("urlPicture") != null) {
                        urlPicture = document.getData().get("urlPicture").toString();
                    } else {
                        urlPicture = null;
                    }
                    if (document.getData().get("choice") != null) {
                        choice = document.getData().get("choice").toString();
                    } else {
                        choice = null;
                    }
                    User userToAdd = new User(uid, username, urlPicture);
                    userToAdd.setChoice(choice);
                    users.add(userToAdd);
                    getRestaurantJoiningUsers();
                    configureRecyclerView();
                    configureFab();
                }
            } else {
                Log.d(ProfilePictureView.TAG, "Error getting documents: ", task.getException());
            }
            Log.e("listMates", String.valueOf(users.size()));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateTableRestaurants(String userId, String userName, String choice, int i) {
        for (Restaurant r : restaurantList) {
            if (r.getRestaurantName().equals(restaurant.getRestaurantName())) {
                isFound = true;
                List<String> rateList = restaurant.getRate();
                if (rateList.size() != 0) {
                    found = ListUtils.findUserInRateList(userId, rateList);
                    if (found) {
                        deleteLike(userId, r.getRestaurantUid());
                    } else {
                        updateChoiceOrLike(i, userName, choice, userId);
                    }
                } else {
                    updateChoiceOrLike(i, userName, choice, userId);
                }
            }
        }
        if (!isFound) {
            createRestaurantAndUpdateIt(i, userName, userId);
        }
        getRestaurantFromFirestore();
        refreshRestaurantList();
    }

    private void refreshRestaurantList() {
        restaurantList = new ArrayList<>();
        RestaurantHelper.getRestaurantsCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                    Restaurant r = doc.toObject(Restaurant.class);
                    Objects.requireNonNull(r).setRestaurantUid(doc.getId());
                    r.setRestaurantName(doc.getString("restaurantName"));
                    r.setRate((List<String>) doc.get("rate"));
                    r.setUsers((List<String>) doc.get("users"));
                    if (doc.getDouble("lat") != null) {
                        r.setLat(doc.getDouble("lat"));
                    } else {
                        r.setLat(0);
                    }
                    if (doc.getDouble("lng") != null) {
                        r.setLng(doc.getDouble("lng"));
                    } else {
                        r.setLng(0);
                    }
                    restaurantList.add(r);
                }
            } else {
                Log.e("error", "Error getting documents: ", task.getException());
            }
        });
    }

    private void createRestaurantAndUpdateIt(int i, String userName, String userId) {
        if (i == 0) {
            RestaurantHelper.createRestaurant(resultDetail.getName(), resultDetail.id, resultDetail.name,
                    resultDetail.getGeometry().getLocation().getLat(), resultDetail.getGeometry().getLocation().getLng());
            RestaurantHelper.updateRestaurantChoice(userName, choice);
        } else {
            RestaurantHelper.createRestaurant(resultDetail.getName(), resultDetail.id, resultDetail.name,
                    resultDetail.getGeometry().getLocation().getLat(), resultDetail.getGeometry().getLocation().getLng());
            RestaurantHelper.updateRestaurantRate(userId, resultDetail.getName());
            Toast.makeText(getActivity(), "liked !", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateChoiceOrLike(int i, String userName, String choice, String userId) {
        if (i == 0) {
            RestaurantHelper.updateRestaurantChoice(userName, choice);
        } else {
            RestaurantHelper.updateRestaurantRate(userId, resultDetail.getName());
            Toast.makeText(getActivity(), "liked !", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteLike(String userId, String restaurantUid) {
        RestaurantHelper.deleteRestaurantRate(userId, restaurantUid);
        Toast.makeText(getActivity(), "Unliked !", Toast.LENGTH_SHORT).show();
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
